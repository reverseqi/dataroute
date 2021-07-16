package com.mingyi.dataroute.executor.vimport;

import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.node.vimport.po.ImportPO;
import com.vbrug.fw4j.common.util.Assert;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 导入配置类
 * @author vbrug
 * @since 1.0.0
 */
public class ImportConfigure {

    private final ImportPO      po;
    private final JobDataSource dataSource;
    private       List<Field>   importFieldList;
    private       List<Field>   importBatchFieldList;
    private       int           bufferInsertSize = 500;
    private       int           dequeMaxSize     = 5;


    ImportConfigure(ImportPO po) {
        this.po = po;
        this.dataSource = DataSourcePool.getInstance().getDataSource(po.getImportDatasourceId());
        // 解析字段
        this.parseParams();
    }


    /**
     * 解析参数
     */
    private void parseParams() {
        // 01-解析抽取字段、抽取条件字段
        Assert.notNull(po.getImportFields(), "入库字段列名称不可为空");
        this.importFieldList = JacksonUtils.jsonToList(po.getImportFields(), Field.class);
        // 02-批次标识字段解析，获取常量值，或者从作业环境信息中获取
        if (StringUtils.hasText(po.getImportBatchFields())) {
            this.importBatchFieldList = JacksonUtils.jsonToList(po.getImportBatchFields(), Field.class);
        }
        // 03-配置参数解析
        if (StringUtils.hasText(po.getParams())) {
            Map<String, String> paramMap = JacksonUtils.json2Map(po.getParams(), String.class, String.class);
            if (!CollectionUtils.isEmpty(paramMap)) {
                if (paramMap.containsKey(ExecutorConstants.PARAM_BUFFER_INSERT_SIZE)) {
                    this.bufferInsertSize = Integer.parseInt(paramMap.get(ExecutorConstants.PARAM_BUFFER_INSERT_SIZE));
                }
                if (paramMap.containsKey(ExecutorConstants.PARAM_DEQUE_MAX_SIZE)) {
                    this.dequeMaxSize = Integer.parseInt(paramMap.get(ExecutorConstants.PARAM_DEQUE_MAX_SIZE));
                }
            }
        }
    }

    public ImportPO getPo() {
        return po;
    }

    public JobDataSource getDataSource() {
        return dataSource;
    }

    public List<Field> getImportFieldList() {
        return importFieldList;
    }

    public List<Field> getImportBatchFieldList() {
        return importBatchFieldList;
    }

    public int getBufferInsertSize() {
        return bufferInsertSize;
    }

    public int getDequeMaxSize() {
        return dequeMaxSize;
    }
}
