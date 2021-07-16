package com.mingyi.dataroute.persistence.node.export.entity;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExportPO {


    private Integer id;                                 // 节点ID
    private String  filePath;                           // 文件地址【支持作业环境变量解析】
    private String  fileType;                           // 文件类型（JSON、CSV）
    private String  fileParserParams;                   // 文件解析参数，格式为JSON串。所有文件共用参数（file_encoding-文件编码，包含UTF-8、GBK、GB2312等；is_line_to_hump-标题是否下划线转驼峰false、true）；CSV特有参数（first_row_is_header-首行是否为标题，false、true；value_separator-值分隔符号，quotation-引用符号）；JSON特有参数（one_line_contain_multi-一行是否包含多条数据，false、true；target_path-目标结果路径）
    private Integer exportDatasourceId;                 // 数据源ID
    private String  exportSql;                          // 导出表名【支持作业环境变量解析】
    private String  exportFields;                       // 导出字段，JSON格式【支持作业环境变量解析】
    private String  exportBatchFields;                  // 批次字段，JSON
    private String  params;                             // 参数，JSON格式（buffer_fetch_size--每次获取数量，deque_max_size--队列最大值）
    private Integer consumerNumber;                     // 导出消费者数量
    private String  resultParamName;                    // 结果参数名称（如果为空，默认导出文件路径参数名-export_file_path）
    private String  remark;                             // 备注

    public String getExportBatchFields() {
        return exportBatchFields;
    }

    public void setExportBatchFields(String exportBatchFields) {
        this.exportBatchFields = exportBatchFields;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileParserParams() {
        return fileParserParams;
    }

    public void setFileParserParams(String fileParserParams) {
        this.fileParserParams = fileParserParams;
    }

    public Integer getExportDatasourceId() {
        return exportDatasourceId;
    }

    public void setExportDatasourceId(Integer exportDatasourceId) {
        this.exportDatasourceId = exportDatasourceId;
    }

    public String getExportSql() {
        return exportSql;
    }

    public void setExportSql(String exportSql) {
        this.exportSql = exportSql;
    }

    public String getExportFields() {
        return exportFields;
    }

    public void setExportFields(String exportFields) {
        this.exportFields = exportFields;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(Integer consumerNumber) {
        this.consumerNumber = consumerNumber;
    }

    public String getResultParamName() {
        return resultParamName;
    }

    public void setResultParamName(String resultParamName) {
        this.resultParamName = resultParamName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
