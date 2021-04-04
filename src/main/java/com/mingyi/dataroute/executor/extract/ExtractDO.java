package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.JobSqlRunner;
import com.mingyi.dataroute.db.datasource.JobDataSource;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.executor.ParamParser;
import com.mingyi.dataroute.executor.ParamTokenHandler;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.vbrug.fw4j.common.util.JacksonUtils;
import com.vbrug.fw4j.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractDO {

    private static final Logger logger = LoggerFactory.getLogger(ExtractDO.class);

    private final AtomicLong produceCount = new AtomicLong(0L);

    private final AtomicLong consumeCount = new AtomicLong(0L);

    private final JobSqlRunner originSqlRunner;

    private final JobSqlRunner targetSqlRunner;

    private final JobDataSource originDataSource;

    private final JobDataSource targetDataSource;

    private final ExtractPO po;

    private final TaskContext taskContext;

    private final List<Field> fieldList;

    private Field jobField;

    private String extractValueCond;

    private String extractKeyCond;

    private Field extractValueField;

    private Field extractKeyField;

    private final String batchInsertSql;

    ExtractDO(ExtractPO po, TaskContext taskContext) throws SQLException, IOException {
        this.po = po;
        this.taskContext = taskContext;
        this.originDataSource = taskContext.getJobContext().getDsPool().getDataSource(po.getOriginDatasource());
        this.targetDataSource = taskContext.getJobContext().getDsPool().getDataSource(po.getTargetDatasource());
        this.originSqlRunner = this.originDataSource.getSqlRunner();
        this.targetSqlRunner = this.targetDataSource.getSqlRunner();

        // 解析字段
        this.fieldList = JacksonUtils.jsonToList(po.getFields(), Field.class);
        for (Field field : fieldList) {
            if (field.getType().toUpperCase().equals(ExecutorConstants.FIELD_CONSTANT)
                    && (field.getValue().contains("#") || field.getValue().contains("$"))) {
                if (field.getValue().contains("jobId")) {
                    jobField = field;
                }
                field.setValue(ParamParser.parseParam(field.getValue(), new ParamTokenHandler(taskContext)));
            }
        }

        // 解析抽取查询条件
        Dialect originDialect = this.getOriginDataSource().getDialect();
        List<ExtractDO.Field> fieldList = JacksonUtils.jsonToList(this.getPo().getTriggerField(), ExtractDO.Field.class);
        String joinValue = "";
        for (ExtractDO.Field field : fieldList) {
            if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
                this.extractValueField = field;
                this.extractValueCond = field.getName() + " >= " + originDialect.vfString2Date(field.getValue());
            } else if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_NUMBER)) {
                this.extractValueField = field;
                this.extractValueCond = field.getName() + " >= " + field.getValue();
            } else {
                this.extractKeyField = field;
                if (StringUtils.hasText(field.getValue())) {
                    if (field.getType().equalsIgnoreCase(ExecutorConstants.FIELD_NUMBER)) {
                        joinValue = field.getValue();
                    } else {
                        joinValue = Arrays.stream(field.getValue().split(",")).map(x -> "'" + x + "'").collect(Collectors.joining(","));
                    }
                }
            }
        }
        if (StringUtils.hasText(joinValue)) {
            String childSQL = originDialect.buildQuerySQL(po.getOriginTable(),
                    new String[]{this.extractKeyField.getName()},
                    this.extractValueCond.replace(">", ""),
                    this.extractKeyField.getName() + " IN (" + joinValue + ")");
            this.extractKeyCond = this.extractKeyField.getName() + " NOT IN (" + childSQL + ")";
        }

        // 数据批量插入SQL
        String fields = this.getFieldList().stream()
                .map(ExtractDO.Field::getName)
                .collect(Collectors.joining(","));
        this.batchInsertSql = this.getTargetDataSource().getDialect().buildInsertSQL(this.getPo().getTargetTable(), fields);
    }

    public String getBatchInsertSql() {
        return batchInsertSql;
    }

    public String getExtractValueCond() {
        return extractValueCond;
    }

    public String getExtractKeyCond() {
        return extractKeyCond;
    }

    public AtomicLong getProduceCount() {
        return produceCount;
    }

    public AtomicLong getConsumeCount() {
        return consumeCount;
    }

    public JobSqlRunner getOriginSqlRunner() {
        return originSqlRunner;
    }

    public JobSqlRunner getTargetSqlRunner() {
        return targetSqlRunner;
    }

    public JobDataSource getOriginDataSource() {
        return originDataSource;
    }

    public JobDataSource getTargetDataSource() {
        return targetDataSource;
    }

    public ExtractPO getPo() {
        return po;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public Field getJobField() {
        return jobField;
    }

    public Field getExtractValueField() {
        return extractValueField;
    }

    public Field getExtractKeyField() {
        return extractKeyField;
    }

    public static class Field {

        public Field() {
        }

        private String name;
        private String type;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

