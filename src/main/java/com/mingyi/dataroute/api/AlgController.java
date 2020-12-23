package com.mingyi.dataroute.api;

import com.mingyi.dataroute.executor.http.HttpExecutor;
import com.vbrug.fw4j.common.third.http.HttpHelp;
import com.vbrug.fw4j.common.third.http.PostRequest;
import com.vbrug.fw4j.core.thread.SignalLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 算法接口服务
 *
 * @author vbrug
 * @since 1.0.0
 */
@RequestMapping("alg")
@RestController
public class AlgController {

    private static final Logger logger = LoggerFactory.getLogger(AlgController.class);

    /**
     * 算法服务回调
     */
    @RequestMapping("callback")
    public String callback(@RequestBody Map<String, Object> params) throws InterruptedException {
        logger.info("此次回调参数：{}", params);
        String jobId = (String) params.get("jobId");
        String taskId = (String) params.get("taskId");
        String status = (String) params.get("status");
        if ("0".equals(status)) {
            TimeUnit.SECONDS.sleep(5);
            SignalLock lock = HttpExecutor.lockMap.get(jobId + taskId);
            lock.lock();
            try {
                lock.signal();
            } finally {
                lock.unlock();
            }
            logger.info("任务--> {}, 唤醒锁", jobId + taskId);
        }
        return "callback success";
    }

    /**
     * 算法临时请求服务
     */
    @RequestMapping("call")
    public String call(String type) throws IOException {
        String url, jobId = "J001", taskId, uploadDockerFilePath, callBackUrl = "http://127.0.0.1:9081/dataroute/alg/callback";
        switch (type) {
            case "aitype":
                url = "http://127.0.0.1:4441/bigdata/110/classifyFileTg";
                taskId = "T001";
                uploadDockerFilePath = "/root/test/data.tsv";
                break;
            case "feature":
                url = "http://127.0.0.1:4445/bigdata/110/digitalFile";
                taskId = "T002";
                uploadDockerFilePath = "/root/test/digital.tsv";
                break;
            case "crash":
                url = "http://127.0.0.1:4443/bigdata/110/crashFile";
                taskId = "T003";
                uploadDockerFilePath = "/root/test/crash.tsv";
                break;
            default:
                url = "http://127.0.0.1:4442/bigdata/110/clusterFile";
                taskId = "T004";
                uploadDockerFilePath = "/root/test/cluster.tsv";
                break;
        }

        PostRequest postRequest = HttpHelp.createPostRequest(url);
        postRequest.putParam("jobId", jobId);
        postRequest.putParam("taskId", taskId);
        postRequest.putParam("callBackUrl", callBackUrl);
        postRequest.putParam("uploadDockerFilePath", uploadDockerFilePath);
        String execute = postRequest.execute();
        System.out.println(execute);
        return execute;
    }

}
