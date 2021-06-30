package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.DataType;
import com.mingyi.dataroute.db.Field;
import com.mingyi.dataroute.db.datasource.DataSourcePool;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.node.extract.po.ExtractPO;
import com.vbrug.fw4j.common.util.*;
import com.vbrug.workflow.core.context.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractConfigure {

    private static final Logger logger = LoggerFactory.getLogger(ExtractConfigure.class);

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
    private       ExtractField       condField;
    private       List<ExtractField> extractFieldList;
    private       List<ExtractField> batchFieldList;
    private       int                bufferFetchSize  = 500;
    private       int                bufferInsertSize = 500;
    private       int                dequeMaxSize     = 5;
    private       long               extractAmount;

    ExtractConfigure(ExtractPO po, TaskContext taskContext) throws SQLException, IOException {
        this.po = po;
        this.taskContext = taskContext;
        this.originDataSource = DataSourcePool.getInstance().getDataSource(po.getOriginDatasource());
        this.targetDataSource = DataSourcePool.getInstance().getDataSource(po.getTargetDatasource());
        // 解析字段
        this.parseParams();
    }


    /**
     * 解析参数
     */
    private void parseParams() {
        // 01-解析抽取字段、抽取条件字段
        Assert.notNull(po.getExtractField(), "抽取字段列名称为空");
        this.extractFieldList = JacksonUtils.jsonToList(po.getExtractField(), ExtractField.class);
        Assert.notNull(po.getExtractCondField(), "抽取条件字段为空");
        this.condField = JacksonUtils.json2Bean(po.getExtractCondField(), ExtractField.class);

        // 02-批次标识字段解析，获取常量值，或者从作业环境信息中获取
        if (StringUtils.hasText(po.getExtractBatchField())) {
            batchFieldList = JacksonUtils.jsonToList(po.getExtractBatchField(), ExtractField.class);
            for (ExtractField batchField : batchFieldList) {
                if (batchField.getProperty().toUpperCase().equals(Field.PROPERTY_CONSTANT)
                        && (batchField.getValue().contains("$"))) {
                    batchField.setValue(ParamParser.parseParam(batchField.getValue(), new ParamTokenHandler(taskContext)));
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
            if (paramMap.containsKey("deque_max_size")) {
                this.dequeMaxSize = Integer.parseInt(paramMap.get("deque_max_size"));
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
        switch (condField.getDataType()) {
            case DATETIME:
                minValueExp = originDialect.funcStringToDate(condField.getMinValue());
                if (condField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = originDialect.funcStringToDate(condField.getMaxValue());
                } else {
                    // 防止出现脏数据，日期超过当前时间，限制查询数据范围小于当前时间
                    maxValueExp = originDialect.funcStringToDate(DateUtils.formatDate(new Date(), DateUtils.YMDHMS));
                }
                break;
            case NUMBER:
                minValueExp = condField.getMinValue();
                if (condField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = condField.getMaxValue();
                }
                break;
            case STRING:
                minValueExp = "'" + condField.getMinValue() + "'";
                if (condField.getProperty().equalsIgnoreCase(FIELD_PROPERTY_RANGE)) {
                    maxValueExp = "'" + condField.getMaxValue() + "'";
                }
                break;
            default:
        }
        // 02-最小值处理
        if (StringUtils.hasText(minValueExp)) {
            switch (condField.getProperty().toUpperCase()) {
                case FIELD_PROPERTY_MINIMUM_EXCLUDE:
                    condList.add(condField.getOriginFieldName() + " > " + minValueExp);
                    break;
                case FIELD_PROPERTY_RANGE:
                case FIELD_PROPERTY_MINIMUM_INCLUDE:
                default:
                    condList.add(condField.getOriginFieldName() + " >= " + minValueExp);
            }
        }
        // 03-最大值处理
        if (StringUtils.hasText(maxValueExp))
            condList.add(condField.getOriginFieldName() + " <= " + maxValueExp);
        // 04-查询字段处理
        columnList.add(originDialect.funcMax(condField.getOriginFieldName(), FIELD_MAX_VALUE));
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
        if (condField.getDataType().equals(DataType.DATETIME)) {
            rangeCond = originDialect.funcStringToDate(ExecutorConstants.CONTEXT_PARAM_PLACEHOLDER);
        } else {
            rangeCond = ExecutorConstants.CONTEXT_PARAM_PLACEHOLDER;
        }

        switch (condField.getProperty().toUpperCase()) {
            case FIELD_PROPERTY_MINIMUM_EXCLUDE:
                condList.add(condField.getOriginFieldName() + " > " + rangeCond);
                break;
            case FIELD_PROPERTY_RANGE:
            case FIELD_PROPERTY_MINIMUM_INCLUDE:
            default:
                condList.add(condField.getOriginFieldName() + " >= " + rangeCond);
        }
        condList.add(condField.getOriginFieldName() + " <= " + rangeCond);

        // 02-默认查询条件
        if (StringUtils.hasText(po.getDefaultCond()))
            condList.add(po.getDefaultCond());

        // sql构建
        return originDialect.buildQuerySQL(this.getPo().getOriginTable(),
                extractFieldList.stream().map(ExtractField::getOriginFieldName).toArray(String[]::new),
                condList.toArray(new String[0]));
    }


    public String buildTruncateTargetSQL() {
        return this.getOriginDataSource().getDialect().buildTruncateSQL(po.getTargetTable());
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

    public ExtractField getCondField() {
        return condField;
    }

    public List<ExtractField> getExtractFieldList() {
        return extractFieldList;
    }

    public List<ExtractField> getBatchFieldList() {
        return batchFieldList;
    }

    public int getDequeMaxSize() {
        return dequeMaxSize;
    }
}

