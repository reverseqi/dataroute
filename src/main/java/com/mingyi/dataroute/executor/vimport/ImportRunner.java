package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.exceptions.DataRouteException;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.vbrug.fw4j.core.design.producecs.PCPool;
import com.vbrug.fw4j.core.design.producecs.ProducerTask;
import com.vbrug.workflow.core.context.TaskContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导入执行器
 * @author vbrug
 * @since 1.0.0
 */
public class ImportRunner {

    private final ImportConfigure configure;
    private final TaskContext     taskContext;

    ImportRunner(ImportConfigure configure, TaskContext taskContext) {
        this.configure = configure;
        this.taskContext = taskContext;
    }

    /**
     * 多线程导入数据
     * @throws Exception
     */
    public void parseFileAndSinkDB() throws Exception {
        // 01-判断抽取数据是否需清空目标表
        if (configure.getPo().getSinkDbType().equalsIgnoreCase(ExecutorConstants.SINK_DB_TYPE_TRUNCATE)) {
            this.truncateMdTable();
        }

        // 02-开始导入
        // 消费者
        ImportConsumerTask consumer = new ImportConsumerTask(this.configure, this.taskContext, new AtomicLong(0L));

        // 生产者
        ProducerTask<List<Map<String, String>>> producer = null;
        switch (configure.getPo().getFileType()) {
            case ExecutorConstants.FILE_TYPE_CSV:
                producer = new CSVImportProducerTask(configure, taskContext, new AtomicLong(0L), new AtomicInteger(0));
                break;
            case ExecutorConstants.FILE_TYPE_JSON:
                producer = new JSONImportProducerTask(configure, taskContext, new AtomicLong(0L), new AtomicInteger(0));
                break;
            default:
                throw new DataRouteException("不支持的导入文件格式：{}", configure.getPo().getFileType());
        }

        // 启动生产消费者线程进行抽取
        //noinspection unchecked
        new PCPool<List<Map<String, String>>>(String.valueOf(taskContext.getJobContext().getJobId())).setDequeMaxSize(configure.getDequeMaxSize())
                .push(consumer.split(configure.getPo().getConsumerNumber()))
                .push(producer).run();
    }

    /**
     * 清空中间表
     */
    private void truncateMdTable() throws SQLException {
        configure.getDataSource().getSqlRunner().run("truncate table " + configure.getPo().getImportTableName());
    }

}
