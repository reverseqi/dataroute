package com.mingyi.dataroute.context;

import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;

import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class JobContextBuilder {

    private JobContext jobContext;

    private JobContextBuilder(Long jobId, String jobName, Integer processId){
        jobContext = new JobContext(jobId, jobName, processId);
    }

    public static JobContextBuilder newJobContext(Long jobId, String jobName, Integer processId){
        return new JobContextBuilder(jobId, jobName, processId);
    }

    public JobContextBuilder putJsonData(String jsonData){
        Map<String, Object> json2Map = JacksonUtils.json2Map(jsonData, String.class, Object.class);
        CollectionUtils.copy(json2Map, jobContext.getDataMap());
        return this;
    }

    public JobContextBuilder putData(String key, Object value){
        jobContext.getDataMap().put(key, value);
        return this;
    }

    /**
     * 构建作业环境变量
     *
     * @return JobContext
     */
    public JobContext build(){
        jobContext.putData("jobId", jobContext.getJobId());
        jobContext.putData("processId", jobContext.getProcessId());
        return jobContext;
    }
}
