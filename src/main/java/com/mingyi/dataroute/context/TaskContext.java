package com.mingyi.dataroute.context;

import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.ObjectUtils;

import java.util.*;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class TaskContext {

    private       Long                id;                                                              // id
    private       Integer             nodeId;                                                           // 任务ID
    private       String              nodeName;                                                         // 任务名称
    private       String              type;                                                                // 类型
    private       JobContext          jobContext;                                                    // 作业环境
    private final List<TaskContext>   lastTaskContextList = new ArrayList<>();          // 前置任务数据Map
    private final Map<String, Object> dataMap             = new HashMap<>();                      // 数据Map

    TaskContext() {
    }

    public Long getId() {
        return id;
    }

    public JobContext getJobContext() {
        return jobContext;
    }

    public List<TaskContext> getLastTaskContextList() {
        return lastTaskContextList;
    }

    public String getContextDataString(String key) {
        return ObjectUtils.castString(getContextData(key));
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public Integer getContextDataInteger(String key) {
        return ObjectUtils.castInteger(getContextData(key));
    }


    private Object getContextData(String key) {
        if (Objects.nonNull(this.getObjectData(key)))
            return this.getObjectData(key);
        if (!CollectionUtils.isEmpty(lastTaskContextList)) {
            for (TaskContext taskContext : lastTaskContextList) {
                if (taskContext.getDataMap().containsKey(key))
                    return taskContext.getObjectData(key);
            }
        }
        if (jobContext.getDataMap().containsKey(key))
            return jobContext.getObjectData(key);
        return null;
    }

    /**
     * 放入初始化环境变量信息
     * @param key   键
     * @param value 值
     */
    protected void putData(String key, Object value) {
        dataMap.put(key, value);
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
     * @param key
     * @return
     */
    public Object getObjectData(String key) {
        return Optional.ofNullable(dataMap.get(key)).orElse(null);
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getType() {
        return type;
    }

    public static class Builder {

        private TaskContext taskContext = new TaskContext();

        public Builder(Long id, Integer nodeId, String nodeName, JobContext jobContext, String type) {
            taskContext.nodeId = nodeId;
            taskContext.jobContext = jobContext;
            taskContext.nodeName = nodeName;
            taskContext.type = type;
            taskContext.id = id;
        }

        public Builder putJsonData(String jsonData) {
            Map<String, Object> json2Map = JacksonUtils.json2Map(jsonData, String.class, Object.class);
            CollectionUtils.copy(json2Map, taskContext.dataMap);
            return this;
        }

        /**
         * 构建任务环境变量
         * @return TaskContext
         */
        public TaskContext build() {
            taskContext.putData("taskId", taskContext.getNodeId());
            return taskContext;
        }
    }
}
