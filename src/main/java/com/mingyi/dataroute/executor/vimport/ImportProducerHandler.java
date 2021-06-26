package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.persistence.task.vimport.po.ImportPO;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.IOUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.pc.ProducerHandler;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportProducerHandler extends ProducerHandler<List<Map<String, Object>>> {

    private static final Logger logger = LoggerFactory.getLogger(ImportProducerHandler.class);

    private final BufferedReader bufferedReader;

    private final ImportPO importPO;

    private final AtomicInteger counter = new AtomicInteger(0);

    ImportProducerHandler(String filePath, ImportPO importPO) {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.importPO = importPO;
    }

    @Override
    public List<Map<String, Object>> produce() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        String                    line = null;
        if (importPO.getParseType().equals("JSON")) {
            while ((line = bufferedReader.readLine()) != null) {
                line = line.replaceAll("'", "\"");
                List<Map<String, String>> tileList = JacksonUtils.json2TileList(line, "/featureList");
                List<Map<String, Object>> maps = tileList.stream().map(x -> {
                    Iterator<String>    keyIterator = x.keySet().iterator();
                    Map<String, Object> newMap      = new HashMap<>();
                    while (keyIterator.hasNext()) {
                        String next = keyIterator.next();
                        if (!"caseNo".equals(next)) {
                            newMap.put("featureCode", next);
                            newMap.put("featureValue", x.get(next));
                        } else {
                            newMap.put(next, x.get(next));
                        }
                    }
                    return newMap;
                }).filter( x -> x.containsKey("featureCode")).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(maps)) {
                    list.addAll(maps);
                    if (list.size() >= importPO.getBufferSize()) {
                        logger.info("此次生产数据：{}, 共计生产：{}", list.size(), counter.addAndGet(list.size()));
                        return list;
                    }
                }
            }
        } else {
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split("\\s+");
                if (!Arrays.isNullOrEmpty(split)) {
                    Map<String, Object> loopMap     = new HashMap<>();
                    String[]            splitFields = importPO.getFields().split(",");
                    for (int i = 0; i < splitFields.length && i < split.length; i++) {
                        loopMap.put(StringUtils.lineToHump(splitFields[i].trim()), split[i]);
                    }
                    list.add(loopMap);
                    if (list.size() >= importPO.getBufferSize()) {
                        logger.info("此次生产数据：{}, 共计生产：{}", list.size(), counter.addAndGet(list.size()));
                        return list;
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(list))
            return null;
        else {
            logger.info("此次生产数据：{}, 共计生产：{}", list.size(), counter.addAndGet(list.size()));
            return list;
        }
    }

    public void close() {
        IOUtils.close(this.bufferedReader);
    }
}
