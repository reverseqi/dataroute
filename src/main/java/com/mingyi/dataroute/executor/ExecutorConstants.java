package com.mingyi.dataroute.executor;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExecutorConstants {

    public static final String EXPORT_FILE_PATH   = "EXPORT_FILE_PATH";
    public static final String UPLOAD_FILE_PATH   = "UPLOAD_FILE_PATH";
    public static final String DOWNLOAD_FILE_PATH = "DOWNLOAD_FILE_PATH";
    public static final String SYSTEM_TIME        = "SYSTEM_TIME";


    public static final String CONTEXT_PARAM_PLACEHOLDER = "${}";

    // 输出类型
    public static final String SINK_DB_TYPE_TRUNCATE = "TRUNCATE";                    // 清空
    public static final String SINK_DB_TYPE_APPEND   = "APPEND";                      // 追加

}
