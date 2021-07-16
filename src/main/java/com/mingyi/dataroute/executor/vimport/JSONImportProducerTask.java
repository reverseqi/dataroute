package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.vbrug.fw4j.common.ValueMap;
import com.vbrug.fw4j.common.util.third.file.JSONFileConfigure;
import com.vbrug.fw4j.common.util.third.file.JSONFileReader;
import com.vbrug.fw4j.common.util.*;
import com.vbrug.fw4j.core.design.producecs.ProducerTask;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生产任务
 * @author vbrug
 * @since 1.0.0
 */
public class JSONImportProducerTask implements ProducerTask<List<Map<String, String>>> {

    private static final Logger          logger = LoggerFactory.getLogger(CSVImportProducerTask.class);
    private final        ImportConfigure configure;
    private final        TaskContext     taskContext;
    private final        AtomicLong      counter;
    private final        AtomicInteger   lineCounter;
    private              JSONFileReader  fr;

    public JSONImportProducerTask(ImportConfigure configure, TaskContext taskContext, AtomicLong counter, AtomicInteger lineCounter) {
        this.configure = configure;
        this.taskContext = taskContext;
        this.counter = counter;
        this.lineCounter = lineCounter;
    }

    @Override
    public void beforeProduce() throws Exception {
        JSONFileConfigure jsonFileConfigure = new JSONFileConfigure();
        if (StringUtils.hasText(configure.getPo().getFileParserParams())) {
            ValueMap<String, Object> paramMap = JacksonUtils.json2ValueMap(configure.getPo().getFileParserParams(), String.class, Object.class);
            BeanUtils.copyProperties(CollectionUtils.keyLineToHump(paramMap), jsonFileConfigure);
            if (jsonFileConfigure.getOneLineContainMulti()) {
                if (StringUtils.isEmpty(jsonFileConfigure.getTargetPath())) {
                    throw new DataRouteException("JSON文件一行包含多条记录，此时target_path参数不可为空");
                }
            }
        } else {
            jsonFileConfigure.setIsLineToHump(true);
        }
        fr = new JSONFileReader(new File(configure.getPo().getFilePath()), jsonFileConfigure);
    }

    @Override
    public List<Map<String, String>> produce() throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        // 判断JSON文件一行内容是否包含多条记录
        if (fr.isOneLineContainMulti()) {
            List<Map<String, String>> mapList;
            while (CollectionUtils.isNotEmpty(mapList = fr.readLineToMapList())) {
                list.addAll(mapList);
                if (this.progress(list))
                    return list;
            }
        } else {
            Map<String, String> map;
            while ((map = fr.readLineToMap()) != null) {
                list.add(map);
                if (this.progress(list))
                    return list;
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已解析：{}，当前解析进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(lineCounter.get(), fr.getLineAmount()));
            return list;
        } else
            return null;
    }

    private Boolean progress(List<Map<String, String>> list) {
        lineCounter.addAndGet(1);
        if (list.size() >= configure.getBufferInsertSize()) {
            logger.info("【{}--{}】, 已抽取：{}，当前抽取进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(lineCounter.get(), fr.getLineAmount()));
            return true;
        }
        return false;
    }


    @Override
    public void finallyHandle() throws Exception {
        if (Objects.nonNull(fr)) {
            fr.close();
        }
        logger.info("【{}--{}】, FileReader资源关闭, 生产者{} 读取文件结束",
                taskContext.getTaskId(), taskContext.getTaskName(), Thread.currentThread().getName());
    }

}
