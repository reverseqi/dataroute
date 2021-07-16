package com.mingyi.dataroute.executor.export;

import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.exceptions.DataRouteException;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.node.export.entity.ExportPO;
import com.vbrug.fw4j.common.ValueMap;
import com.vbrug.fw4j.common.util.third.file.*;
import com.vbrug.fw4j.common.util.*;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * 导出配置类
 * @author vbrug
 * @since 1.0.0
 */
public class ExportConfigure {

    protected static final String RESULT_PARAM_NAME   = "export_file_path";
    protected static final String FIELD_EXPORT_AMOUNT = "EXPORT_AMOUNT";

    private final ExportPO      po;
    private final JobDataSource dataSource;
    private       List<Field>   exportFieldList;
    private       List<Field>   exportBatchFieldList;
    private       int           bufferFetchSize = 500;
    private       int           dequeMaxSize    = 5;
    private       long          exportAmount;


    ExportConfigure(ExportPO po) {
        this.po = po;
        this.dataSource = DataSourcePool.getInstance().getDataSource(po.getExportDatasourceId());
        // 解析字段
        this.parseParams();
    }


    /**
     * 解析参数
     */
    private void parseParams() {
        // 01-解析抽取字段、抽取条件字段
        Assert.notNull(po.getExportFields(), "导出字段列名称不可为空");
        this.exportFieldList = JacksonUtils.jsonToList(po.getExportFields(), Field.class);
        // 02-批次标识字段解析，获取常量值，或者从作业环境信息中获取
        if (StringUtils.hasText(po.getExportBatchFields())) {
            this.exportBatchFieldList = JacksonUtils.jsonToList(po.getExportBatchFields(), Field.class);
        }
        // 03-配置参数解析
        if (StringUtils.hasText(po.getParams())) {
            Map<String, String> paramMap = JacksonUtils.json2Map(po.getParams(), String.class, String.class);
            if (!CollectionUtils.isEmpty(paramMap)) {
                if (paramMap.containsKey(ExecutorConstants.PARAM_BUFFER_FETCH_SIZE)) {
                    this.bufferFetchSize = Integer.parseInt(paramMap.get(ExecutorConstants.PARAM_BUFFER_FETCH_SIZE));
                }
                if (paramMap.containsKey(ExecutorConstants.PARAM_DEQUE_MAX_SIZE)) {
                    this.dequeMaxSize = Integer.parseInt(paramMap.get(ExecutorConstants.PARAM_DEQUE_MAX_SIZE));
                }
            }
        }
    }

    /**
     * 构建文件写入
     * @return 结果
     */
    public FileWriter buildFileWriter() throws Exception {
        switch (this.getPo().getFileType()) {
            case ExecutorConstants.FILE_TYPE_CSV:
                CSVFileConfigure csvFileConfigure = new CSVFileConfigure();
                if (StringUtils.hasText(this.getPo().getFileParserParams())) {
                    ValueMap<String, Object> paramMap = JacksonUtils.json2ValueMap(this.getPo().getFileParserParams(), String.class, Object.class);
                    BeanUtils.copyProperties(CollectionUtils.keyLineToHump(paramMap), csvFileConfigure);
                    if (!csvFileConfigure.getFirstRowIsHeader()) {
                        csvFileConfigure.setHeaders(this.getExportFieldList().stream().map(Field::getFieldName).toArray(String[]::new));
                    }

                } else {
                    csvFileConfigure.setValueSeparator(CSVFileConfigure.SeparatorType.TAB);
                    csvFileConfigure.setFirstRowIsHeader(true);
                }
                return new CSVFileWriter(new File(this.getPo().getFilePath()), csvFileConfigure);
            case ExecutorConstants.FILE_TYPE_JSON:
                JSONFileConfigure jsonFileConfigure = new JSONFileConfigure();
                if (StringUtils.hasText(this.getPo().getFileParserParams())) {
                    ValueMap<String, Object> paramMap = JacksonUtils.json2ValueMap(this.getPo().getFileParserParams(), String.class, Object.class);
                    BeanUtils.copyProperties(CollectionUtils.keyLineToHump(paramMap), jsonFileConfigure);
                    if (jsonFileConfigure.getOneLineContainMulti()) {
                        if (StringUtils.isEmpty(jsonFileConfigure.getTargetPath())) {
                            throw new DataRouteException("JSON文件一行包含多条记录，此时target_path参数不可为空");
                        }
                    }
                }
                return new JSONFileWriter(new File(this.getPo().getFilePath()), jsonFileConfigure);
            default:
                throw new DataRouteException("不支持的导入文件格式：{}", this.getPo().getFileType());
        }
    }

    /**
     * 查询源表导出取数据总数
     * @return 计算SQL
     */
    public String buildExportRangeSQL() {
        Dialect dialect = dataSource.getDialect();
        return dialect.buildQuerySQL("(" + this.getPo().getExportSql() + ") t", new String[]{dialect.funcCount1(FIELD_EXPORT_AMOUNT)});
    }


    public ExportPO getPo() {
        return po;
    }

    public JobDataSource getDataSource() {
        return dataSource;
    }

    public List<Field> getExportFieldList() {
        return exportFieldList;
    }

    public List<Field> getExportBatchFieldList() {
        return exportBatchFieldList;
    }

    public int getBufferFetchSize() {
        return bufferFetchSize;
    }

    public int getDequeMaxSize() {
        return dequeMaxSize;
    }

    public long getExportAmount() {
        return exportAmount;
    }

    public void setExportAmount(long exportAmount) {
        this.exportAmount = exportAmount;
    }
}
