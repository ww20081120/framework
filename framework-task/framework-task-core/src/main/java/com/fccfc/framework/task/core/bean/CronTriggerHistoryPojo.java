package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> CRON_TRIGGER_HISTORY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */

public class CronTriggerHistoryPojo extends BaseEntity {

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

    /** CRON_EXPRESSION */
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;

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

    public String getCronExpression() {
        return this.cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
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
