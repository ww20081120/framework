package com.fccfc.framework.log.mongodb.model;

import java.util.List;

/***
 * 
 * <Description> <br> 
 *  
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月4日 <br>
 * @see com.fccfc.framework.log.mongodb.model <br>
 */
public class TransLog {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** TRANS_ID */
    private String transId;

    /** MODULE_CODE */
    private String moduleCode;

    /** BEGIN_TIME */
    private java.util.Date beginTime;

    /** END_TIME */
    private java.util.Date endTime;

    /** CONSUME_TIME */
    private Integer consumeTime;

    /** INPUT_PARAM */
    private String inputParam;

    /** OUTPUT_PARAM */
    private String outputParam;

    /** SQL_LOG */
    private String sqlLog;

    /** EXCEPTION_LOG */
    private String exceptionLog;

    /** CONTACT_CHANNEL_ID */
    private Integer contactChannelId;

    /** stackJson */
    private List<String> stackJson;

    public String getTransId() {
        return this.transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public java.util.Date getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(java.util.Date beginTime) {
        this.beginTime = beginTime;
    }

    public java.util.Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(java.util.Date endTime) {
        this.endTime = endTime;
    }

    public Integer getConsumeTime() {
        return this.consumeTime;
    }

    public void setConsumeTime(Integer consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getInputParam() {
        return this.inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
    }

    public String getOutputParam() {
        return this.outputParam;
    }

    public void setOutputParam(String outputParam) {
        this.outputParam = outputParam;
    }

    public String getSqlLog() {
        return this.sqlLog;
    }

    public void setSqlLog(String sqlLog) {
        this.sqlLog = sqlLog;
    }

    public String getExceptionLog() {
        return this.exceptionLog;
    }

    public void setExceptionLog(String exceptionLog) {
        this.exceptionLog = exceptionLog;
    }

    public Integer getContactChannelId() {
        return this.contactChannelId;
    }

    public void setContactChannelId(Integer contactChannelId) {
        this.contactChannelId = contactChannelId;
    }

    public List<String> getStackJson() {
        return stackJson;
    }

    public void setStackJson(List<String> stackJson) {
        this.stackJson = stackJson;
    }
}
