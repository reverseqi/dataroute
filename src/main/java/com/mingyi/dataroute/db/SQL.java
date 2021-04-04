package com.mingyi.dataroute.db;

import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.ibatis.jdbc.AbstractSQL;
import org.assertj.core.util.Arrays;

/**
 * SQL 组合器
 *
 * @author vbrug
 * @since 1.0.0
 */
public class SQL extends AbstractSQL<SQL> {

    @Override
    public SQL getSelf() {
        return this;
    }

    @Override
    public SQL WHERE(String... conditions) {
        if (Arrays.isNullOrEmpty(conditions))
            return getSelf();
        return super.WHERE(conditions);
    }

    @Override
    public SQL WHERE(String condition) {
        if (StringUtils.isEmpty(condition))
            return getSelf();
        return super.WHERE(condition);
    }

    @Override
    public SQL ORDER_BY(String columns) {
        if (StringUtils.isEmpty(columns))
            return getSelf();
        return super.ORDER_BY(columns);
    }
}
