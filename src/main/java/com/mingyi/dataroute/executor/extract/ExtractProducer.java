package com.mingyi.dataroute.executor.extract;

import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.DateUtils;
import com.vbrug.fw4j.common.util.NumberUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.producecs.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractProducer implements Producer<List<Map<String, String>>> {
    private static final Logger            logger = LoggerFactory.getLogger(ExtractProducer.class);
    private final        ExtractConfigure  configure;
    private final        String            extractSql;
    private final        String            startTime;
    private final        String            endTime;
    private              ResultSet         rs;
    private              ResultSetMetaData metaData;
    private final        AtomicLong        counter;

    public ExtractProducer(String extractSql, String startTime, String endTime, ExtractConfigure configure, AtomicLong counter) {
        this.extractSql = extractSql;
        this.configure = configure;
        this.startTime = startTime;
        this.endTime = endTime;
        this.counter = counter;
    }

    @Override
    public void beforeProduce() throws Exception {
        Connection        connection = configure.getOriginDataSource().getConnection();
        String            extractSQL = this.extractSql.replaceFirst("\\$\\{\\}", startTime).replaceFirst("\\$\\{\\}", endTime);
        PreparedStatement statement  = connection.prepareStatement(extractSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(configure.getBufferFetchSize());
        this.rs = statement.executeQuery();
        this.metaData = rs.getMetaData();
        logger.info("【{}--{}】，数据抽取SQL--》{}", configure.getTaskContext().getTaskId(), configure.getTaskContext().getTaskName(), StringUtils.removeTNR(extractSQL));
    }

    @Override
    public List<Map<String, String>> produce() throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i).toUpperCase(), rs.getObject(i));
            }
            list.add(map);
            if (list.size() >= configure.getBufferInsertSize()) {
                logger.info("【{}--{}】, 已抽取：{}，当前抽取进度：{} ", configure.getTaskContext().getTaskId(), configure.getTaskContext().getTaskName(),
                        counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, configure.getExtractAmount()));
                return configure.getOriginDataSource().getDialect().jdbcType2String(list);
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已抽取：{}， 当前抽取进度：{}", configure.getTaskContext().getTaskId(), configure.getTaskContext().getTaskName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, configure.getExtractAmount()));
            return configure.getOriginDataSource().getDialect().jdbcType2String(list);
        } else
            return null;
    }

    @Override
    public Producer<List<Map<String, String>>>[] split(int number) throws Exception {

        List<ExtractProducer> splitProducers = new ArrayList<>();
        Assert.isTrue(number > 0, "number 必须大于0");
        if (number == 1) return new ExtractProducer[]{this};

        long startLong = DateUtils.parseDate(startTime, DateUtils.YMDHMS).getTime();
        long endLong   = DateUtils.parseDate(endTime, DateUtils.YMDHMS).getTime();

        long dif = (endLong - startLong) / 1000L;
        if (dif == 0 || dif == 1) return new ExtractProducer[]{this};
        if (dif < number) {
            System.out.println("数据范围间距小于任务数量, 重置任务数量为间距");
            number = (int) dif;
        }
        List<Long> splitNumbers = NumberUtils.avgSplitNumber(startLong, endLong, number);
        for (int i = 1; i < splitNumbers.size(); i++) {
            String loopExtractSql = this.extractSql;
            if (i != 1)
                loopExtractSql = loopExtractSql.replaceFirst(">=", ">");
            splitProducers.add(new ExtractProducer(loopExtractSql,
                    DateUtils.formatTime(splitNumbers.get(i - 1), DateUtils.YMDHMS),
                    DateUtils.formatTime(splitNumbers.get(i), DateUtils.YMDHMS),
                    this.configure, this.counter));
        }
        return splitProducers.toArray(new ExtractProducer[0]);
    }


    @Override
    public void finallyHandle() throws Exception {
        try {
            if (Objects.nonNull(rs) && !this.rs.isClosed()) {
                this.rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info("【{}--{}】, ResultSet资源关闭, 生产者{} 抽取结束",
                configure.getTaskContext().getTaskId(), configure.getTaskContext().getTaskName(), Thread.currentThread().getName());
    }
}
