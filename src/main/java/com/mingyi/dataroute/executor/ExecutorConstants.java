package com.mingyi.dataroute.executor;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExecutorConstants {


    // 文件类型
    public static final String FILE_TYPE_JSON = "JSON";
    public static final String FILE_TYPE_CSV  = "CSV";

    // 文件解析参数
    public static final String FILE_COMM_PARAM_FILE_ENCODING     = "file_encoding";
    public static final String FILE_COMM_PARAM_IS_LINE_TO_HUMP   = "is_line_to_hump";
    public static final String CSV_PARAM_FIRST_ROW_IS_HEADER     = "first_row_is_header";
    public static final String CSV_PARAM_VALUE_SEPARATOR         = "value_separator";
    public static final String CSV_PARAM_QUOTATION               = "quotation";
    public static final String JSON_PARAM_ONE_LINE_CONTAIN_MULTI = "one_line_contain_multi";
    public static final String JSON_PARAM_TARGET_PATH            = "target_path";

    // param
    public static final String PARAM_BUFFER_FETCH_SIZE  = "buffer_fetch_size";
    public static final String PARAM_BUFFER_INSERT_SIZE = "buffer_insert_size";
    public static final String PARAM_DEQUE_MAX_SIZE     = "deque_max_size";


    public static final String CONTEXT_PARAM_PLACEHOLDER = "${}";

    // 输出类型
    public static final String SINK_DB_TYPE_TRUNCATE = "TRUNCATE";                    // 清空
    public static final String SINK_DB_TYPE_APPEND   = "APPEND";                      // 追加

}
