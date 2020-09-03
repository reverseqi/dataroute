package com.mingyi.dataroute.executor;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExecutorFactory {

    public static final String ALGC = "algc";
    public static final String BSQL = "bsql";
    public static final String EXPORT = "export";
    public static final String EXTRACT = "extract";
    public static final String VIMPORT = "vimport";

    public static Executor createExecutor(String executorType){

        switch (executorType){
            case EXTRACT :
                return new ExtractExecutor();
            case BSQL :
                return new BSqlExecutor();
            default :
                return null;
        }

    }

}
