package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.vbrug.fw4j.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConfigure {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConfigure.class);

    // 抽取处理类型
    protected static final String HANDLE_TYPE_TRUNCATE           = "TRUNCATE";                    // 清空
    protected static final String HANDLE_TYPE_APPEND             = "APPEND";                      // 追加
    // 抽取字段类型
    protected static final String FIELD_TYPE_STRING              = "STRING";
    protected static final String FIELD_TYPE_DATETIME            = "DATETIME";
    protected static final String FIELD_TYPE_NUMBER              = "NUMBER";
    protected static final String FIELD_TYPE_CONSTANT            = "CONSTANT";
    // 抽取字段属性
    protected static final String FIELD_PROPERTY_RANGE           = "RANGE";                        // 范围抽取
    protected static final String FIELD_PROPERTY_MINIMUM_INCLUDE = "MINIMUM_INCLUDE";              // 包含最小值
    protected static final String FIELD_PROPERTY_MINIMUM_EXCLUDE = "MINIMUM_EXCLUDE";              // 排除最小值
    // 抽取字段
    protected static final String FIELD_MAX_VALUE                = "MAX_VALUE";
    protected static final String FIELD_EXTRACT_AMOUNT           = "EXTRACT_AMOUNT";

    private final ExtractPO          po;
    private final TaskContext        taskContext;
    private final JobDataSource      originDataSource;
    private final JobDataSource      targetDataSource;
    private       ExtractField       extractCondExtractField;
    private       List<ExtractField> extractExtractFieldList;
    private       List<ExtractField> extractBatchExtractFieldList;
    private       int                bufferFetchSize  = 500;
    private       int                bufferInsertSize = 500;
    private       long               extractAmount;

    ExtractConfigure(ExtractPO po, TaskContext taskContext) throws SQLException, IOException {
        this.po = po;
        this.taskContext = taskContext;
        this.originDataSource = taskContext.getJobContext().getDsPool().getDataSource(po.getOriginDatasource());
        this.targetDataSource = taskContext.getJobContext().getDsPool().getDataSource(po.getTargetDatasource());
        // 解析字段
        this.parseParams();
    }


    /**
     * 解析参数
     */
    private void parseParams() {
        // 01-解析抽取字段、抽取条件字段
        Assert.notNull(po.getExtractField(), "抽取字段列名称为空");
        this.extractExtractFieldList = JacksonUtils.jsonToList(po.getExtractField(), ExtractField.class);
        Assert.notNull(po.getExtractCondField(), "抽取条件字段为空");
        this.extractCondExtractField = JacksonUtils.json2Bean(po.getExtractCondField(), ExtractField.class);

        // 02-批次标识字段解析，获取常量值，或者从作业环境信息中获取
        if (StringUtils.hasText(po.getExtractBatchField())) {
            extractBatchExtractFieldList = JacksonUtils.jsonToList(po.getExtractBatchField(), ExtractField.class);
        }
        if (!CollectionUtils.isEmpty(extractBatchExtractFieldList)) {
            for (ExtractField extractField : extractBatchExtractFieldList) {
                if (extractField.getType().toUpperCase().equals(FIELD_TYPE_CONSTANT)
                        && (extractField.getValue().contains("#") || extractField.getValue().contains("$"))) {
                    extractField.setValue(ParamParser.parseParam(extractField.getValue(), new ParamTokenHandler(taskContext)));
                }
            }
        }
        // 03-配置参数解析
        Map<String, String> paramMap = JacksonUtils.json2Map(po.getParams(), String.class, String.class);
        if (!CollectionUtils.isEmpty(paramMap)) {
            if (paramMap.containsKey("buffer_fetch_size")) {
                this.bufferFetchSize = Integer.parseInt(paramMap.get("buffer_fetch_size"));
            }
            if (paramMap.containsKey("buffer_insert_size")) {
                this.bufferInsertSize = Integer.parseInt(paramMap.get("buffer_insert_size"));
            }
        }
    }

    /**
     * 查询源表抽取字段最大值，待抽取数据总数
     * @return 计算SQL
     */
    public String buildExtractRangeSQL() {
        List<String> columnList    = new ArrayList<>();
        List<String> condList      = new ArrayList<>();
        Dialect      originDialect = originDataSource.getDialect();
        // 01-字段处理
        String maxValueField, minValueExp = null, maxValueExp = null;
        switch (extractCondExtractField.getType().toUpperCase()) {
            case FIELD_TYPE_DATETIME:
                minValueExp = originDialect.funcStringToDate(extractCondExtractField.getMinValue());
                if (extractCondExtractField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = originDialect.funcStringToDate(extractCondExtractField.getMaxValue());
                } else {
                    // 防止出现脏数据，日期超过当前时间，限制查询数据范围小于当前时间
                    maxValueExp = originDialect.funcStringToDate(DateUtils.formatDate(new Date(), DateUtils.YMDHMS));
                }
                break;
            case FIELD_TYPE_NUMBER:
                minValueExp = extractCondExtractField.getMinValue();
                if (extractCondExtractField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = extractCondExtractField.getMaxValue();
                }
                break;
            case FIELD_TYPE_STRING:
                minValueExp = "'" + extractCondExtractField.getMinValue() + "'";
                if (extractCondExtractField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = "'" + extractCondExtractField.getMaxValue() + "'";
                }
                break;
            default:
        }
        // 02-最小值处理
        if (StringUtils.hasText(minValueExp)) {
            switch (extractCondExtractField.getProperty().toUpperCase()) {
                case FIELD_PROPERTY_MINIMUM_EXCLUDE:
                    condList.add(extractCondExtractField.getOriginFieldName() + " > " + minValueExp);
                    break;
                case FIELD_PROPERTY_RANGE:
                case FIELD_PROPERTY_MINIMUM_INCLUDE:
                default:
                    condList.add(extractCondExtractField.getOriginFieldName() + " >= " + minValueExp);
            }
        }
        // 03-最大值处理
        if (StringUtils.hasText(maxValueExp))
            condList.add(extractCondExtractField.getOriginFieldName() + " <= " + maxValueExp);
        // 04-查询字段处理
        columnList.add(originDialect.funcMax(extractCondExtractField.getOriginFieldName(), FIELD_MAX_VALUE));
        columnList.add(originDialect.funcCount1(FIELD_EXTRACT_AMOUNT));

        // 02-默认查询条件
        if (StringUtils.hasText(po.getDefaultCond()))
            condList.add(po.getDefaultCond());

        return originDataSource.getDialect().buildQuerySQL(po.getOriginTable(), columnList.toArray(new String[0]), condList.toArray(new String[0]));
    }


    /**
     * 构建抽取SQL
     */
    public String buildExtractSQL() {
        String       rangeCond;
        List<String> condList      = new ArrayList<>();
        Dialect      originDialect = originDataSource.getDialect();

        // 01-范围查询条件处理
        // 数据范围处理
        if (extractCondExtractField.getType().equalsIgnoreCase(FIELD_TYPE_DATETIME)) {
            rangeCond = originDialect.funcStringToDate("${}");
        } else {
            rangeCond = "${}";
        }
        condList.add(extractCondExtractField.getOriginFieldName() + " > " + rangeCond);
        condList.add(extractCondExtractField.getOriginFieldName() + " <= " + rangeCond);

        // 02-默认查询条件
        if (StringUtils.hasText(po.getDefaultCond()))
            condList.add(po.getDefaultCond());

        // sql构建
        return originDialect.buildQuerySQL(this.getPo().getOriginTable(),
                extractExtractFieldList.stream().map(ExtractField::getOriginFieldName).toArray(String[]::new),
                condList.toArray(new String[0]));
    }


    public String buildTruncateTargetSQL() {
        return this.getOriginDataSource().getDialect().buildTruncateSQL(po.getTargetTable());
    }

    /**
     * 构建插入SQL
     */
    public String buildInsertTargetSQL() {
        // 数据批量插入SQL
        String targetFields = this.getExtractFieldList().stream()
                .map(ExtractField::getTargetFieldName)
                .collect(Collectors.joining(","));
        if (!CollectionUtils.isEmpty(extractBatchExtractFieldList)) {
            for (ExtractField extractField : extractBatchExtractFieldList) {
                targetFields += ", " + extractField.getTargetFieldName();
            }
        }
        return this.getTargetDataSource().getDialect().buildInsertSQL(this.getPo().getTargetTable(), targetFields);
    }


    public ExtractPO getPo() {
        return po;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public JobDataSource getOriginDataSource() {
        return originDataSource;
    }

    public JobDataSource getTargetDataSource() {
        return targetDataSource;
    }

    public ExtractField getExtractCondField() {
        return extractCondExtractField;
    }

    public List<ExtractField> getExtractFieldList() {
        return extractExtractFieldList;
    }

    public List<ExtractField> getExtractBatchFieldList() {
        return extractBatchExtractFieldList;
    }

    public int getBufferFetchSize() {
        return bufferFetchSize;
    }

    public int getBufferInsertSize() {
        return bufferInsertSize;
    }

    public long getExtractAmount() {
        return extractAmount;
    }

    public void setExtractAmount(long extractAmount) {
        this.extractAmount = extractAmount;
    }
}

