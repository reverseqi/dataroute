package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.db.Field;
import com.vbrug.fw4j.common.ValueMap;
import com.vbrug.fw4j.common.util.third.file.CSVFileConfigure;
import com.vbrug.fw4j.common.util.third.file.CSVFileReader;
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
public class CSVImportProducerTask implements ProducerTask<List<Map<String, String>>> {

    private static final Logger          logger = LoggerFactory.getLogger(CSVImportProducerTask.class);
    private final        ImportConfigure configure;
    private final        TaskContext     taskContext;
    private final        AtomicLong      counter;
    private final        AtomicInteger   lineCounter;
    private              CSVFileReader   fileReader;

    public CSVImportProducerTask(ImportConfigure configure, TaskContext taskContext, AtomicLong counter, AtomicInteger lineCounter) {
        this.configure = configure;
        this.taskContext = taskContext;
        this.counter = counter;
        this.lineCounter = lineCounter;
    }

    @Override
    public void beforeProduce() throws Exception {
        CSVFileConfigure csvFileConfigure = new CSVFileConfigure();
        if (StringUtils.hasText(configure.getPo().getFileParserParams())) {
            ValueMap<String, Object> paramMap = JacksonUtils.json2ValueMap(configure.getPo().getFileParserParams(), String.class, Object.class);
            BeanUtils.copyProperties(CollectionUtils.keyLineToHump(paramMap), csvFileConfigure);
            if (!csvFileConfigure.getFirstRowIsHeader()) {
                csvFileConfigure.setHeaders(configure.getImportFieldList().stream().map(Field::getFieldName).toArray(String[]::new));
            }

        } else {
            csvFileConfigure.setLineToHump(true);
            csvFileConfigure.setValueSeparator(CSVFileConfigure.SeparatorType.TAB);
            csvFileConfigure.setFirstRowIsHeader(true);
        }
        fileReader = new CSVFileReader(new File(configure.getPo().getFilePath()), csvFileConfigure);
    }

    @Override
    public List<Map<String, String>> produce() throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String>       map;
        while ((map = fileReader.readLineToMap()) != null) {
            list.add(map);
            lineCounter.addAndGet(1);
            if (list.size() >= configure.getBufferInsertSize()) {
                logger.info("【{}--{}】, 已抽取：{}，当前抽取进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                        counter.addAndGet(list.size()), NumberUtils.divisionPercent(lineCounter.get(), fileReader.getLineAmount()));
                return list;
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已解析：{}，当前解析进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(lineCounter.get(), fileReader.getLineAmount()));
            return list;
        } else
            return null;
    }


    @Override
    public void finallyHandle() throws Exception {
        if (Objects.nonNull(fileReader)) {
            fileReader.close();
        }
        logger.info("【{}--{}】, FileParser资源关闭, 生产者{} 读取文件结束",
                taskContext.getTaskId(), taskContext.getTaskName(), Thread.currentThread().getName());
    }

}
