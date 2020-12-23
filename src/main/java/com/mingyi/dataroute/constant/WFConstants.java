package com.mingyi.dataroute.constant;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class WFConstants {
    /*
     * 共用方法返回码
     */
    public final static Integer STATUS_CODE_0 = 0;                   // 成功
    public final static Integer STATUS_CODE_5 = 5;                   // 执行完成
    public final static Integer STATUS_CODE_9 = 9;                   // 异常


    /*
     * 作业状态（0-执行中，1-成功，9-失败）
     */
    public final static Integer JOB_STATE_0 = 0;
    public final static Integer JOB_STATE_1 = 1;
    public final static Integer JOB_STATE_9 = 9;

    /*
     * 任务状态（0-执行中，1-成功，9-失败）
     */
    public final static Integer TASK_STATE_0 = 0;
    public final static Integer TASK_STATE_1 = 1;
    public final static Integer TASK_STATE_9 = 9;

    /*
     * 判断
     */
    public final static String BOOLEAN_0 = "0";
    public final static String BOOLEAN_1 = "1";

    /**
     * TRUE、FALSE
     */
    public final static String TRUE = "TRUE";
    public final static String FALSE = "FALSE";

    /**
     * 任务类型
     */
    public final static String TASK_START = "STAT";
    public final static String TASK_EXTRACT = "EXTRACT";
    public final static String TASK_BSQL = "BSQL";
    public final static String TASK_EXPORT = "EXPORT";
    public final static String TASK_FILE_UD = "FILE_UD";
    public final static String TASK_HTTP = "HTTP";
    public final static String TASK_IMPORT = "IMPORT";

    /**
     * 共用变量名
     */
    public final static String PARAM_JOB_ID = "jobId";
    public final static String PARAM_PROCESS_ID = "processId";
    public final static String PARAM_NODE_ID = "nodeId";
    public final static String PARAM_NODE_TYPE = "nodeType";
    public final static String PARAM_NODE_NAME = "nodeName";
    public final static String PARAM_NODE_GROUP = "nodeGroup";
    public final static String PARAM_NODE_LIST = "nodeList";
}
