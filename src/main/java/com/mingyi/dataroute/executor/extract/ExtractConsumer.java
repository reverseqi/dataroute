package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.dialect.Dialect;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.core.design.producecs.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 抽取
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConsumer implements Consumer<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConsumer.class);

    private final ExtractConfigure configure;
    private final Dialect          dialect;
    private final AtomicLong       counter;
    private final Long             extractAmount;

    public ExtractConsumer(ExtractConfigure configure, AtomicLong counter, Long extractAmount) {
        this.configure = configure;
        this.dialect = configure.getTargetDataSource().getDialect();
        this.counter = counter;
        this.extractAmount = extractAmount;
    }

    @Override
    public Consumer<List<Map<String, String>>>[] split(int number) throws Exception {
        Assert.isTrue(number > 0, "number 必须大于0");
        if (number == 1) {
            return new ExtractConsumer[]{this};
        }
        List<ExtractConsumer> splitConsumers = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            splitConsumers.add(new ExtractConsumer(this.configure, this.counter, this.extractAmount));
        }
        return splitConsumers.toArray(new ExtractConsumer[0]);
    }

    @Override
    public void consume(List<Map<String, String>> dataList) throws SQLException {
        List<Object>       argList   = new ArrayList<>();
        List<ExtractField> fieldList = new ArrayList<>();
        fieldList.addAll(configure.getExtractFieldList());
        fieldList.addAll(configure.getBatchFieldList());
        String insertSQL = dialect.buildInsertSQL(configure.getPo().getTargetTable(), fieldList, dataList, argList);
        try {
            if (CollectionUtils.isEmpty(argList))
                configure.getTargetDataSource().getSqlRunner().insert(insertSQL);
            else
                configure.getTargetDataSource().getSqlRunner().insert(insertSQL, argList);
        } catch (SQLException e) {
            logger.error("【{}--{}】, 入库异常, 异常信息: {}, 异常SQL-> {} ",
                    configure.getTaskContext().getTaskId(), configure.getTaskContext().getTaskName(),
                    e.getMessage(),
                    insertSQL);
            throw e;
        }
    }
}
