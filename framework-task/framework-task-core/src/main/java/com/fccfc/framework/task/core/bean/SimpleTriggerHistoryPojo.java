package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> SIMPLE_TRIGGER_HISTORY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */

public class SimpleTriggerHistoryPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** TRIGGER_ID */
    @Column(name = "TRIGGER_ID")
    private Integer triggerId;

    /** SEQ */
    @Column(name = "SEQ")
    private Integer seq;

    /** TRIGGER_NAME */
    @Column(name = "TRIGGER_NAME")
    private String triggerName;

    /** BEGIN_TIME */
    @Column(name = "BEGIN_TIME")
    private java.util.Date beginTime;

    /** END_TIME */
    @Column(name = "END_TIME")
    private java.util.Date endTime;

    /** TIMES */
    @Column(name = "TIMES")
    private Integer times;

    /** EXECUTE_INTERVAL */
    @Column(name = "EXECUTE_INTERVAL")
    private Integer executeInterval;

    /** INTERVAL_UNIT */
    @Column(name = "INTERVAL_UNIT")
    private String intervalUnit;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** UPDATE_TIME */
    @Column(name = "UPDATE_TIME")
    private java.util.Date updateTime;

    /** UPDATE_OPERATOR_ID */
    @Column(name = "UPDATE_OPERATOR_ID")
    private Integer updateOperatorId;

    public Integer getTriggerId() {
        return this.triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getTriggerName() {
        return this.triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
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

    public Integer getTimes() {
        return this.times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getExecuteInterval() {
        return this.executeInterval;
    }

    public void setExecuteInterval(Integer executeInterval) {
        this.executeInterval = executeInterval;
    }

    public String getIntervalUnit() {
        return this.intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public java.util.Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateOperatorId() {
        return this.updateOperatorId;
    }

    public void setUpdateOperatorId(Integer updateOperatorId) {
        this.updateOperatorId = updateOperatorId;
    }

}
