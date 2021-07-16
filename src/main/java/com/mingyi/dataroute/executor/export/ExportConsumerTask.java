package com.mingyi.dataroute.executor.export;

import com.vbrug.fw4j.common.util.third.file.FileWriter;
import com.vbrug.fw4j.common.util.NumberUtils;
import com.vbrug.fw4j.core.design.producecs.ConsumerTask;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导出消费者
 * @author vbrug
 * @since 1.0.0
 */
public class ExportConsumerTask implements ConsumerTask<List<Map<String, String>>> {

    private static final Logger          logger = LoggerFactory.getLogger(ExportConsumerTask.class);
    private final        ExportConfigure configure;
    private final        TaskContext     taskContext;
    private final        AtomicLong      counter;
    private final        FileWriter      fileWriter;

    public ExportConsumerTask(ExportConfigure configure, TaskContext taskContext, AtomicLong counter) throws Exception {
        this.fileWriter = configure.buildFileWriter();
        this.configure = configure;
        this.taskContext = taskContext;
        this.counter = counter;
    }

    @Override
    public void consume(List<Map<String, String>> list) throws Exception {
        for (Map<String, String> map : list) {
            fileWriter.writeLine(map);
        }
        logger.info("【{}--{}】, 已消费：{}，当前导出进度：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter.addAndGet(list.size()), configure.getExportAmount()));
    }

    @Override
    public void finallyHandle() throws Exception {
        if (Objects.nonNull(fileWriter)) {
            fileWriter.close();
        }
        logger.info("【{}--{}】, FileParser资源关闭, 生产者{} 读取文件结束",
                taskContext.getTaskId(), taskContext.getTaskName(), Thread.currentThread().getName());
    }

}
