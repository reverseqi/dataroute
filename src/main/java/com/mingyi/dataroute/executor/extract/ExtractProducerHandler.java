package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.vbrug.fw4j.common.util.StringUtils;
import com.vbrug.fw4j.core.design.pc.ProducerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractProducerHandler extends ProducerHandler<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractProducerHandler.class);

    private final ExtractDO ado;
    private final String startTime;
    private final String endTime;
    private boolean isStart = false;
    private boolean isEnd = false;
    private boolean hasInit = false;
    private Connection connection;
    private ResultSet rs;
    private ResultSetMetaData metaData;

    public ExtractProducerHandler(ExtractDO ado, String startTime, String endTime, boolean isStart, boolean isEnd) {
        this.ado = ado;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isStart = isStart;
        this.isEnd = isEnd;
    }

    /**
     * 初始化ResultSet
     */
    private void init() throws SQLException, IOException {
        connection = ado.getOriginDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(this.getExtractSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        if (ado.getPo().getBufferFetchSize() == 0) {
            statement.setFetchSize(Integer.MIN_VALUE);
        } else {
            statement.setFetchSize(ado.getPo().getBufferFetchSize());
        }
        this.rs = statement.executeQuery();
        this.metaData = rs.getMetaData();
        this.hasInit = true;
    }

    /**
     * 拼接抽取SQL
     */
    private String getExtractSql() throws IOException {
        Dialect dialect = ado.getOriginDataSource().getDialect();
        // 列处理
        List<String> columnList = ado.getFieldList().stream()
                .filter(x -> !x.getType().equalsIgnoreCase(ExecutorConstants.FIELD_CONSTANT))
                .map(ExtractDO.Field::getName)
                .collect(Collectors.toList());

        // 条件处理
        List<String> condList = new ArrayList<>();
        String startContent = startTime, endContent = endTime;
        if (ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
            startContent = dialect.vfString2Date(startTime);
            endContent = dialect.vfString2Date(endTime);
        }
        String startTimeCond = ado.getExtractValueField().getName() + " >= " + startContent;
        String endTimeCond = ado.getExtractValueField().getName() + " < " + endContent;
        condList.add(startTimeCond);

        if (StringUtils.hasText(ado.getPo().getDefaultCond())) {
            condList.add(ado.getPo().getDefaultCond());
        }
        // 第一个生产者增加Key判断条件
        if (this.isStart && this.isEnd) {
            if (StringUtils.hasText(ado.getExtractKeyCond()))
                condList.add(ado.getExtractKeyCond());
            endTimeCond = ado.getExtractValueField().getName() + " <= " + endContent;
            condList.add(endTimeCond);
        } else if (this.isStart) {
            if (StringUtils.hasText(ado.getExtractKeyCond()))
                condList.add(ado.getExtractKeyCond());
            condList.add(endTimeCond);
        } else if (this.isEnd) {
            endTimeCond = ado.getExtractValueField().getName() + " <= " + endContent;
            condList.add(endTimeCond);
        } else {
            condList.add(endTimeCond);
        }

        // sql构建
        String extractSql = ado.getOriginDataSource().getDialect()
                .buildQuerySQL(ado.getPo().getOriginTable(),
                        columnList.toArray(new String[columnList.size()]),
                        condList.toArray(new String[condList.size()]));
        logger.info("【{}--{}】，抽取SQL-->{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), StringUtils.removeTNR(extractSql));
        return extractSql;
    }

    /**
     * 抽取原始数据
     */
    @Override
    public List<Map<String, String>> produce() throws SQLException, IOException {
        // 初始化数据库加载
        if (!hasInit) this.init();

        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i).toUpperCase(), rs.getObject(i));
            }
            list.add(map);
            if (list.size() >= ado.getPo().getBufferInsertSize()) {
                logger.info("【{}--{}】, 已生产：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(),
                        ado.getProduceCount().addAndGet(list.size()));
                return ado.getOriginDataSource().getDialect().jdbcType2String(list);
            }
        }
        if (list.size() > 0) {
            logger.info("【{}--{}】, 已生产：{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(),
                    ado.getProduceCount().addAndGet(list.size()));
            return ado.getOriginDataSource().getDialect().jdbcType2String(list);
        } else
            return null;
    }

    @Override
    public void close() {
        try {
            if (Objects.nonNull(rs) && !this.rs.isClosed()) {
                this.rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info("【{}--{}】, ResultSet资源关闭", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName());
    }


}
