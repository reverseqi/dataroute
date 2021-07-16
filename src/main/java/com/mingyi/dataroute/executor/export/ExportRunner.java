package com.mingyi.dataroute.executor.export;

import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.core.design.producecs.PCPool;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 导出执行器
 * @author vbrug
 * @since 1.0.0
 */
public class ExportRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExportRunner.class);

    private final ExportConfigure configure;
    private final TaskContext     taskContext;


    ExportRunner(ExportConfigure configure, TaskContext taskContext) {
        this.configure = configure;
        this.taskContext = taskContext;
    }


    /**
     * 多线程将数据写入文件
     * @throws Exception 异常
     */
    public void sinkToFile() throws Exception {
        // 02-开始导入
        // 消费者
        ExportConsumerTask consumer = new ExportConsumerTask(this.configure, this.taskContext, new AtomicLong(0L));
        // 生产者
        ExportProducerTask producer = new ExportProducerTask(this.configure, this.taskContext, new AtomicLong(0L));
        // 启动生产消费者线程进行导出
        new PCPool<List<Map<String, String>>>(String.valueOf(taskContext.getJobContext().getJobId())).setDequeMaxSize(configure.getDequeMaxSize())
                .push(consumer.split(configure.getPo().getConsumerNumber()))
                .push(producer).run();
    }

    /**
     * 判断是否有导出数据
     * @return 结果
     * @throws SQLException 异常
     */
    public boolean hasExportData() throws SQLException {
        String              exportRangeSQL = configure.buildExportRangeSQL();
        Map<String, Object> result         = configure.getDataSource().getSqlRunner().selectOne(exportRangeSQL);
        if (CollectionUtils.isEmpty(result))
            return false;
        // 获取结果
        Map<String, String> resultMap = configure.getDataSource().getDialect().jdbcType2String(result);
        configure.setExportAmount(Long.parseLong(resultMap.get(ExportConfigure.FIELD_EXPORT_AMOUNT)));
        // 判断结果
        if (configure.getExportAmount() == 0)
            return false;
        logger.info("【{}--{}】，此次导出总数据量：{} ", taskContext.getTaskId(), taskContext.getTaskName(),
                configure.getExportAmount());
        return true;
    }

    public ExportConfigure getConfigure() {
        return configure;
    }
}
