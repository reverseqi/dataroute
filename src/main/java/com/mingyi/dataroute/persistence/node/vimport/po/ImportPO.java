package com.mingyi.dataroute.persistence.node.vimport.po;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ImportPO {

    private Integer id;                                // 节点ID
    private String  filePath;                           // 文件地址【支持作业环境变量解析】
    private String  fileType;                           // 文件类型（JSON、CSV）
    private String  fileParserParams;                        // 文件解析参数，格式为JSON串。所有文件共用参数（file_encoding-文件编码，包含UTF-8、GBK、GB2312等；is_line_to_hump-标题是否下划线转驼峰false、true）；CSV特有参数（first_row_is_header-首行是否为标题，false、true；value_separator-值分隔符号，quotation-引用符号）；JSON特有参数（one_line_contain_multi-一行是否包含多条数据，false、true；target_path-目标结果路径）
    private Integer importDatasourceId;                // 数据源ID
    private String  importTableName;                    // 导入表名【支持作业环境变量解析】
    private String  importFields;                        // 列名称（以逗号分割）
    private String  importBatchFields;                  // 批次字段
    private String  sinkDbType;                         // 入库类型（TRUNCATE, APPEND）
    private String  params;                             //JSON格式，可支持参数（buffer_insert_size--每次插入数量，deque_max_size--队列最大值）
    private Integer consumerNumber;                    // 入库消费者数量
    private String  remark;                             // 备注

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

    public Integer getImportDatasourceId() {
        return importDatasourceId;
    }

    public void setImportDatasourceId(Integer importDatasourceId) {
        this.importDatasourceId = importDatasourceId;
    }

    public String getImportTableName() {
        return importTableName;
    }

    public void setImportTableName(String importTableName) {
        this.importTableName = importTableName;
    }

    public String getImportFields() {
        return importFields;
    }

    public void setImportFields(String importFields) {
        this.importFields = importFields;
    }

    public String getImportBatchFields() {
        return importBatchFields;
    }

    public void setImportBatchFields(String importBatchFields) {
        this.importBatchFields = importBatchFields;
    }

    public String getSinkDbType() {
        return sinkDbType;
    }

    public void setSinkDbType(String sinkDbType) {
        this.sinkDbType = sinkDbType;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
