package com.mingyi.dataroute.context;

import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.exceptions.DataRouteException;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class JobContext {

    private static final Logger logger = LoggerFactory.getLogger(JobContext.class);

    private final List<TaskContext> taskContextList = new CopyOnWriteArrayList<>();   // 任务环境集合
    private final Integer jobId;                                           // 作业ID
    private final String jobName;                                          // 作业名称
    private final Integer processId;                                       // 流程ID
    private Map<String, Object> dataMap = new HashMap<>();                 // 数据Map
    private final DataSourcePool dsPool = new DataSourcePool();            // 数据Map

    private boolean isStop;                                                // 停止状态

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    protected JobContext(Integer jobId, String jobName, Integer processId) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.processId = processId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public boolean putTaskContext(TaskContext taskContext) {
        return taskContextList.add(taskContext);
    }

    public TaskContext findTaskContext(Integer nodeId) {
        Iterator<TaskContext> iterator = taskContextList.iterator();
        while (iterator.hasNext()) {
            TaskContext taskContext = iterator.next();
            if (taskContext.getNodeId().intValue() == nodeId.intValue())
                return taskContext;
        }
        return null;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    /**
     * 放入初始化环境变量信息
     *
     * @param key   键
     * @param value 值
     */
    protected void putData(String key, Object value) {
        dataMap.put(key, value);
    }

    public DataSourcePool getDsPool() {
        return dsPool;
    }


    /**
     * 获取Sql执行器
     *
     * @param dataSourceId 数据源ID
     * @return SqlRunner
     */
    public SqlRunner getSqlRunner(Integer dataSourceId) {
        try {
            return new SqlRunner(this.dsPool.getDataSource(dataSourceId).getConnection());
        } catch (SQLException e) {
            logger.error("数据库：{}, 建立连接失败", dataSourceId);
            throw new DataRouteException(e);
        }
    }

    /**
     * 获取字符串
     */
    public String getStringData(String key) {
        return Optional.ofNullable(dataMap.get(key)).map(String::valueOf).orElse(null);
    }

    /**
     * 获取整型数字
     */
    public Integer getIntegerData(String key) {
        return Optional.ofNullable(dataMap.get(key)).map(x -> Integer.parseInt(String.valueOf(x))).orElse(null);
    }

    /**
     * 获取对象
     *
     * @param key
     * @return
     */
    public Object getObjectData(String key) {
        return Optional.ofNullable(dataMap.get(key)).orElse(null);
    }

    public String getJobName() {
        return jobName;
    }
}
