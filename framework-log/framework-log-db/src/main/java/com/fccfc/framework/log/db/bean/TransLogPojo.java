package com.fccfc.framework.log.db.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> TRANS_LOG的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月06日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "T_SYS_TRANS_LOG")
public class TransLogPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** TRANS_ID */
    @Id
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    @Column(name = "TRANS_ID")
    private String transId;

    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** BEGIN_TIME */
    @Column(name = "BEGIN_TIME")
    private java.util.Date beginTime;

    /** END_TIME */
    @Column(name = "END_TIME")
    private java.util.Date endTime;

    /** CONSUME_TIME */
    @Column(name = "CONSUME_TIME")
    private Integer consumeTime;

    /** INPUT_PARAM */
    @Column(name = "INPUT_PARAM")
    private String inputParam;

    /** OUTPUT_PARAM */
    @Column(name = "OUTPUT_PARAM")
    private String outputParam;

    /** SQL_LOG */
    @Column(name = "SQL_LOG")
    private String sqlLog;

    /** EXCEPTION_LOG */
    @Column(name = "EXCEPTION_LOG")
    private String exceptionLog;

    /** CONTACT_CHANNEL_ID */
    @Column(name = "CONTACT_CHANNEL_ID")
    private Integer contactChannelId;

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

}
