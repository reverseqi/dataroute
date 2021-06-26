package com.mingyi.dataroute.db;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class Field {

    private static final String PROPERTY_CONSTANT = "CONSTANT";       // 常量值
    private static final String PROPERTY_FUNC     = "FUNC";           // 函数

    private String  fieldName;
    private String  dataType;
    private String  value;
    private Boolean nullable;
    private String  property;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DataType getDataType() {
        return DataType.getByValue(dataType);
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
