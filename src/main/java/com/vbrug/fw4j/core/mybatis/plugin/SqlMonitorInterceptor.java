package com.vbrug.fw4j.core.mybatis.plugin;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;

import java.sql.Statement;
import java.util.Properties;

/**
 * SQL监控
 *
 * @author vbrug
 * @since 1.0.0
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlMonitorInterceptor implements Interceptor {

    private long warnMillis;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long beginTimeMillis = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        try {
            return invocation.proceed();
        } finally {
            long endTimeMillis = System.currentTimeMillis();
            System.out.println(statementHandler.getBoundSql().getSql() + "执行耗时："+(endTimeMillis - beginTimeMillis) + "ms");

        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        String warnMillis = properties.getProperty("warnMillis");
        this.warnMillis = warnMillis == null ? 10000L : Long.parseLong(warnMillis);
    }
}
