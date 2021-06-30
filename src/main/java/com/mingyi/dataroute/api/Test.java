package com.mingyi.dataroute.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.FileUtil;
import com.vbrug.fw4j.common.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
@RestController
@RequestMapping("test")
public class Test {

    public static void main2(String[] args) throws Exception {
        Long count = Long.valueOf(0);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
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



    @RequestMapping("testWF")
    public String testWF() {
        return "success";
    }

    public static void main(String[] args) {
            Integer a = Integer.valueOf(1);
        Integer b = Integer.valueOf(1);
        System.out.println(a.equals(b));
    }

    public static void mai(String[] args) throws Exception {
        List<String> lineList = FileUtil.parseFileByLine(new File("/home/vbrug/Downloads/hd_jq/short.txt"));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("/home/vbrug/Downloads/hd_jq/short_clear.txt")));
        String clusterNo = "-1";
        for (String line : lineList) {
            if (line.startsWith("簇")) {
                List<String> extract = StringUtils.extractAll(line, "簇.*?:");
                if (!CollectionUtils.isEmpty(extract)) {
                    clusterNo = extract.get(0).replaceAll("( |簇|:)", "");
                }
            } else if (line.startsWith("[")) {
                bos.write(clusterNo.getBytes(StandardCharsets.UTF_8));
                bos.write("\t".getBytes(StandardCharsets.UTF_8));
                for (String s : line.split(", ")) {
                    s = StringUtils.removeTNR(s);
                    s = StringUtils.trimStr(s, "[");
                    s = StringUtils.trimStr(s, "]");
                    s = StringUtils.trimStr(s, "'");
                    s = StringUtils.trimStr(s, "\"");
                    bos.write(s.getBytes(StandardCharsets.UTF_8));
                    bos.write("\t".getBytes(StandardCharsets.UTF_8));
                }
                bos.write("bt".getBytes(StandardCharsets.UTF_8));
                bos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            }
        }
        bos.flush();
        bos.close();
    }
}
