package com.mingyi.dataroute.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
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
public class Test {

    public static void main(String[] args) throws Exception {
    	Long count = Long.valueOf(0);
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		loggerContext.getLogger("org.apache.http").setLevel(Level.INFO);
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        Connection connection = DriverManager.getConnection("jdbc:clickhouse://39.98.169.101:8123/va_dev?socket_timeout=300000");
        PreparedStatement statement = connection.prepareStatement("select * from sh_yqfx.bs_aitype_case_h", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(10000);
		ResultSet rs = statement.executeQuery();
		ResultSetMetaData metaData = rs.getMetaData();
		List<Map<String, Object>> list = new ArrayList<>();
		try {
				while (rs.next()) {
					Map<String, Object> map = new HashMap<>();
					try {
						for (int i = 1; i <= metaData.getColumnCount(); i++) {
							map.put(metaData.getColumnName(i), rs.getObject(i));
						}
						System.out.println(++count);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println(++count);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println(list.size());
    }
}
