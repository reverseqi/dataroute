package com.mingyi.dataroute.executor.extract;

import com.mingyi.dataroute.db.Field;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractField extends Field {

    public ExtractField() {}

    private String originFieldName;
    private String targetFieldName;
    private String minValue;
    private String maxValue;

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getOriginFieldName() {
        return originFieldName;
    }

    public void setOriginFieldName(String originFieldName) {
        this.originFieldName = originFieldName;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {
        this.targetFieldName = targetFieldName;
    }
}
