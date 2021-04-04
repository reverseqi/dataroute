package com.mingyi.dataroute.api.security;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class SubscribeDO {

    private String ruleNo;    //订阅规则编号
    private String xqdwCodes;    //辖区单位（，拼接）
    private String classifyCodes;    //智能分类（，拼接）
    private String callSummaryCodes;    //接警类别（，拼接）
    private String dealSummaryCodes;    //反馈类别（，拼接）
    private String callTimeStart;    //接警开始时间
    private String callTimeEnd;    //接警结束时间

    public String getRuleNo() {
        return ruleNo;
    }

    public void setRuleNo(String ruleNo) {
        this.ruleNo = ruleNo;
    }

    public String getXqdwCodes() {
        return xqdwCodes;
    }

    public void setXqdwCodes(String xqdwCodes) {
        this.xqdwCodes = xqdwCodes;
    }

    public String getClassifyCodes() {
        return classifyCodes;
    }

    public void setClassifyCodes(String classifyCodes) {
        this.classifyCodes = classifyCodes;
    }

    public String getCallSummaryCodes() {
        return callSummaryCodes;
    }

    public void setCallSummaryCodes(String callSummaryCodes) {
        this.callSummaryCodes = callSummaryCodes;
    }

    public String getDealSummaryCodes() {
        return dealSummaryCodes;
    }

    public void setDealSummaryCodes(String dealSummaryCodes) {
        this.dealSummaryCodes = dealSummaryCodes;
    }

    public String getCallTimeStart() {
        return callTimeStart;
    }

    public void setCallTimeStart(String callTimeStart) {
        this.callTimeStart = callTimeStart;
    }

    public String getCallTimeEnd() {
        return callTimeEnd;
    }

    public void setCallTimeEnd(String callTimeEnd) {
        this.callTimeEnd = callTimeEnd;
    }
}
