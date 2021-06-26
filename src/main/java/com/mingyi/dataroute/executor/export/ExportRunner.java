package com.mingyi.dataroute.executor.export;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.task.export.po.ExportPO;
import com.vbrug.fw4j.common.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExportRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExportRunner.class);

    private final BufferedOutputStream bos;
    private final ExportPO po;
    private final TaskContext taskContext;
    private List<String> fieldList;


    ExportRunner(ExportPO po, TaskContext taskContext) throws FileNotFoundException {
        FileUtil.mkdir(new File(po.getFilePath()));
        bos = new BufferedOutputStream(new FileOutputStream(po.getFilePath()));
        this.po = po;
        this.taskContext = taskContext;
    }

    public void run() throws SQLException, IOException {

        Long count = Long.valueOf(0);

        // 初始化
        JobDataSource dataSource = taskContext.getJobContext().getDsPool().getDataSource(po.getDatasourceId());
        PreparedStatement statement = dataSource.getConnection()
                .prepareStatement(po.getSql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(po.getBufferFetchSize());
        ResultSet rs = statement.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        this.initFields(metaData);
        try {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    map.put(metaData.getColumnName(i).toUpperCase(), rs.getObject(i));
                }
                this.write(dataSource.getDialect().jdbcType2String(map));
                count++;
                if (count % 10000 == 0) {
                    bos.flush();
                    logger.info("【{}--{}】已导出数据量：{}", taskContext.getId(), taskContext.getNodeName(), count);
                }
            }
        } finally {
            rs.close();
            bos.close();
        }
        taskContext.getDataMap().put(ExecutorConstants.EXPORT_FILE_PATH, po.getFilePath());
        logger.info("【{}--{}】，导出完成，总计导出：{}", taskContext.getId(), taskContext.getNodeName(), count);
    }

    public void write(Map<String, String> map) throws IOException {
        for (int i = 0; i < fieldList.size(); i++) {
            String field = fieldList.get(i);
            bos.write(map.get(field).getBytes(StandardCharsets.UTF_8));
            if (i != fieldList.size() - 1)
                bos.write("\t".getBytes(StandardCharsets.UTF_8));
        }
        bos.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
    }

    private void initFields(ResultSetMetaData metaData) throws SQLException {
        fieldList = new ArrayList<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            fieldList.add(metaData.getColumnName(i).toUpperCase());
        }
    }
}
