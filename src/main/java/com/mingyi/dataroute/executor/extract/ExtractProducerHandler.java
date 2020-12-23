package com.mingyi.dataroute.executor.extract;

import com.vbrug.fw4j.core.design.pc.ProducerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractProducerHandler extends ProducerHandler<List<Map<String, String>>> {

    private static final Logger logger = LoggerFactory.getLogger(ExtractProducerHandler.class);

    private final ExtractProducerDO producerDO;
    private String lastSyncTime;
    private ResultSet rs;
    private ResultSetMetaData metaData;

    public ExtractProducerHandler(ExtractProducerDO producerDO, String lastSyncTime) throws SQLException {
        this.producerDO = producerDO;
        this.lastSyncTime = lastSyncTime;
        Connection connection = producerDO.getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement(producerDO.getExtractSql()
                .replace("?", "'" + lastSyncTime + "'"), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(producerDO.getExtractPO().getBufferFetchSize());
        this.rs = statement.executeQuery();
        this.metaData = rs.getMetaData();
    }

    /**
     * 抽取原始数据
     */
    @Override
    public List<Map<String, String>> produce() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                map.put(metaData.getColumnName(i).toUpperCase(), rs.getObject(i));
            }
            list.add(map);
            if (list.size() >= this.producerDO.getExtractPO().getBufferInsertSize()) {
                logger.info("本次生产数据量：{}, 总计生产数据量：{}", list.size(), producerDO.getCount().addAndGet(list.size()));
                return producerDO.getDataSource().getDialect().result2String(list);
            }
        }
        if (list.size() > 0) {
            logger.info("本次生产数据量：{}, 总计生产数据量：{}", list.size(), producerDO.getCount().addAndGet(list.size()));
            return producerDO.getDataSource().getDialect().result2String(list);
        } else
            return null;
    }

    public void close() throws SQLException {
        if (!this.rs.isClosed())
            this.rs.close();
    }


}
