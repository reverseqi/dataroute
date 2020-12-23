package com.mingyi.dataroute.executor.http;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.task.http.po.HttpPO;
import com.vbrug.fw4j.common.third.http.HttpHelp;
import com.vbrug.fw4j.common.third.http.PostRequest;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class HttpRunner {

    private final static Logger logger = LoggerFactory.getLogger(HttpRunner.class);

    private final HttpPO httpPO;
    private final TaskContext taskContext;

    HttpRunner(TaskContext taskContext, HttpPO httpPO) {
        this.taskContext = taskContext;
        this.httpPO = httpPO;
    }

    public void run() throws IOException {
        PostRequest postRequest = HttpHelp.createPostRequest(httpPO.getUrl());
        this.setHeader(postRequest);
        this.setParam(postRequest);
        this.setConfig(postRequest);
        try {
            String result = postRequest.execute();
            logger.info("任务--> {}--{}，服务结果秒回，请注意是否异常!!!!", taskContext.getId(), taskContext.getNodeName());
        } catch (SocketTimeoutException e) {
            logger.info("任务--> {}--{}，强制断开请求", taskContext.getId(), taskContext.getNodeName());
        }
    }

    private void setHeader(PostRequest postRequest) {
        if (!StringUtils.hasText(httpPO.getHeader())) return;
        Map map = JacksonUtils.json2Map(httpPO.getHeader());
        map.keySet().iterator().forEachRemaining(x -> {
            postRequest.addHeader(String.valueOf(x), String.valueOf(map.get(x)));
        });
    }

    private void setParam(PostRequest postRequest) {
        if (!StringUtils.hasText(httpPO.getParams())) return;
        Map map = JacksonUtils.json2Map(ParamParser.parseParam(httpPO.getParams(), new ParamTokenHandler(taskContext)));
        map.keySet().iterator().forEachRemaining(x -> {
            postRequest.putParam(String.valueOf(x), String.valueOf(map.get(x)));
        });
        logger.info("任务--> {}--{}，请求参数: {}", taskContext.getId(), taskContext.getNodeName(), map);
    }

    private void setConfig(PostRequest postRequest) {
        if (!StringUtils.hasText(httpPO.getConfigure())) return;
        Map map = JacksonUtils.json2Map(httpPO.getConfigure());
        RequestConfig.Builder configBuilder = postRequest.getConfigBuilder();
        if (map.containsKey("socketTimeout"))
            configBuilder.setSocketTimeout(Integer.parseInt(String.valueOf(map.get("socketTimeout"))));

        if (map.containsKey("connectTimeout"))
            configBuilder.setConnectTimeout(Integer.parseInt(String.valueOf(map.get("connectTimeout"))));

        if (map.containsKey("connectionRequestTimeout"))
            configBuilder.setConnectionRequestTimeout(Integer.parseInt(String.valueOf(map.get("connectionRequestTimeout"))));
        logger.info("任务--> {}--{}，请求配置: {}", taskContext.getId(), taskContext.getNodeName(), map);
    }
}
