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
    private final        ExtractConfigure  ado;
    private final        String            startTime;
    private final        String            endTime;
    private              Connection        connection;
    private              ResultSet         rs;
    private              ResultSetMetaData metaData;
    private final        AtomicLong        counter;
    private final        Long              extractAmount;

    public ExtractProducer(String startTime, String endTime, ExtractConfigure ado, Connection connection, AtomicLong counter, Long extractAmount) {
        this.ado = ado;
        this.startTime = startTime;
        this.endTime = endTime;
        this.connection = connection;
        this.counter = counter;
        this.extractAmount = extractAmount;
    }

    @Override
    public void beforeProduce() throws Exception {
        connection = ado.getOriginDataSource().getConnection();
        String            extractSQL = ado.buildExtractSQL().replaceFirst("\\$\\{\\}", startTime).replaceFirst("\\$\\{\\}", endTime);
        PreparedStatement statement        = connection.prepareStatement(extractSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(ado.getBufferFetchSize());
        this.rs = statement.executeQuery();
        this.metaData = rs.getMetaData();
        logger.info("【{}--{}】，数据抽取SQL--》{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), StringUtils.removeTNR(extractSQL));
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
            if (list.size() >= ado.getBufferInsertSize()) {
                logger.info("【{}--{}】, 已抽取：{}，当前抽取进度：{} ", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(),
                        counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, extractAmount));
                return ado.getOriginDataSource().getDialect().jdbcType2String(list);
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已抽取：{}， 当前抽取进度：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(),
                    counter.addAndGet(list.size()), NumberUtils.divisionPercent(counter, extractAmount));
            return ado.getOriginDataSource().getDialect().jdbcType2String(list);
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
            if (i == splitNumbers.size() - 1)
                splitProducers.add(new ExtractProducer(DateUtils.formatTime(splitNumbers.get(i - 1), DateUtils.YMDHMS),
                        DateUtils.formatTime(splitNumbers.get(i), DateUtils.YMDHMS),
                        this.ado, this.connection, this.counter, this.extractAmount));
            else
                splitProducers.add(new ExtractProducer(DateUtils.formatTime(splitNumbers.get(i - 1), DateUtils.YMDHMS),
                        DateUtils.formatTime(splitNumbers.get(i) - 1000L, DateUtils.YMDHMS),
                        this.ado, this.connection, this.counter, this.extractAmount));

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
                ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), Thread.currentThread().getName());
    }
}
