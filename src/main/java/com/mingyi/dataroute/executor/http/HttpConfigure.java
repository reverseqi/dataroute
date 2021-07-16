package com.mingyi.dataroute.executor.http;

import com.mingyi.dataroute.persistence.node.http.po.HttpPO;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * HTTP服务配置
 * @author vbrug
 * @since 1.0.0
 */
public class HttpConfigure {

    // 数据源类型（CONTEXT-作业环境变量，DB-数据库）
    public static final String SOURCE_TYPE_CONTEXT = "CONTEXT";
    public static final String SOURCE_TYPE_DB      = "DB";

    // 处理类型（SYNC--同步，ASYNC--异步）
    public static final String HANDLE_TYPE_SYNC  = "SYNC";
    public static final String HANDLE_TYPE_ASYNC = "ASYNC";

    // 结果输出类型（CONTEXT-作业环境变量，MAP-集合，KAFKA-消息队列）
    public static final String SINK_TYPE_CONTEXT = "CONTEXT";
    public static final String SINK_TYPE_MAP     = "MAP";
    public static final String SINK_TYPE_KAFKA   = "KAFKA";

    private ReentrantLock asyncLock;
    private Condition     asyncCondition;

    private final HttpPO po;

    HttpConfigure(HttpPO po) {
        this.po = po;
    }

    public HttpPO getPo() {
        return po;
    }

    public synchronized ReentrantLock getAsyncLock() {
        if (asyncLock == null) {
            asyncLock = new ReentrantLock(true);
        }
        return asyncLock;
    }

    public synchronized Condition getAsyncCondition() {
        if (asyncCondition == null) {
            if (asyncLock == null) {
                asyncLock = new ReentrantLock(true);
            }
            asyncCondition = asyncLock.newCondition();
        }
        return asyncCondition;
    }
}
