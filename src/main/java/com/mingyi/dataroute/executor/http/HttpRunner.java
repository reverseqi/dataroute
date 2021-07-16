package com.mingyi.dataroute.executor.http;

import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.common.util.third.http.HttpHelp;
import com.vbrug.fw4j.common.util.third.http.PostRequest;
import com.vbrug.workflow.core.context.TaskContext;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * 服务请求
 * @author vbrug
 * @since 1.0.0
 */
public class HttpRunner {

    private final static Logger logger = LoggerFactory.getLogger(HttpRunner.class);

    private final HttpConfigure configure;
    private final TaskContext   taskContext;

    HttpRunner(TaskContext taskContext, HttpConfigure configure) {
        this.taskContext = taskContext;
        this.configure = configure;
    }

    /**
     * 执行请求
     * @throws IOException 异常
     */
    public void execute() throws IOException {
        PostRequest postRequest = HttpHelp.createPostRequest(configure.getPo().getUrl());
        this.setHeader(postRequest);
        this.setParam(postRequest);
        this.setConfig(postRequest);
        try {
            String result = postRequest.execute();
            logger.info("【{}--{}】，服务结果秒回，请注意是否异常!!!!", taskContext.getTaskId(), taskContext.getTaskName());
        } catch (SocketTimeoutException e) {
            logger.info("【{}--{}】，强制断开请求", taskContext.getTaskId(), taskContext.getTaskName());
        }
    }

    /**
     * 设置请求头
     * @param postRequest 请求实体
     */
    private void setHeader(PostRequest postRequest) {
        if (!StringUtils.hasText(configure.getPo().getHeader())) return;
        Map<String, String> map = JacksonUtils.json2Map(configure.getPo().getHeader(), String.class, String.class);
        map.keySet().iterator().forEachRemaining(x -> {
            postRequest.addHeader(String.valueOf(x), String.valueOf(map.get(x)));
        });
    }

    /**
     * 设置请求参数
     * @param postRequest 请求实体
     */
    private void setParam(PostRequest postRequest) {
        if (!StringUtils.hasText(configure.getPo().getParams())) return;
        Map<String, String> map = JacksonUtils.json2Map(configure.getPo().getParams(), String.class, String.class);
        map.keySet().iterator().forEachRemaining(x -> {
            postRequest.putParam(String.valueOf(x), String.valueOf(map.get(x)));
        });
        logger.info("【{}--{}】，请求参数: {}", taskContext.getTaskId(), taskContext.getTaskName(), map);
    }

    /**
     * 设置配置信息
     * @param postRequest 请求实体
     */
    private void setConfig(PostRequest postRequest) {
        if (!StringUtils.hasText(configure.getPo().getConfigure())) return;
        Map<String, String>   map           = JacksonUtils.json2Map(configure.getPo().getConfigure(), String.class, String.class);
        RequestConfig.Builder configBuilder = postRequest.getConfigBuilder();
        if (map.containsKey("socketTimeout"))
            configBuilder.setSocketTimeout(Integer.parseInt(String.valueOf(map.get("socketTimeout"))));

        if (map.containsKey("connectTimeout"))
            configBuilder.setConnectTimeout(Integer.parseInt(String.valueOf(map.get("connectTimeout"))));

        if (map.containsKey("connectionRequestTimeout"))
            configBuilder.setConnectionRequestTimeout(Integer.parseInt(String.valueOf(map.get("connectionRequestTimeout"))));
        logger.info("【{}--{}】，请求配置: {}", taskContext.getTaskId(), taskContext.getTaskName(), map);
    }
}
