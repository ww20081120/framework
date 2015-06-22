package com.fccfc.framework.task.core.bean;

import java.util.Date;

import javax.persistence.Column;

import com.fccfc.framework.task.api.SimpleTrigger;

/**
 * <Description> SIMPLE_TRIGGER的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
public class SimpleTriggerPojo extends TriggerPojo {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -838328754689978626L;

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

    public SimpleTriggerPojo() {
    }

    /**
     * @param beginTime
     * @param endTime
     * @param times
     * @param executeInterval
     * @param intervalUnit
     */
    public SimpleTriggerPojo(SimpleTrigger trigger) {
        setTriggerId(trigger.getTriggerId());
        setTriggerName(trigger.getTriggerName());
        if (trigger.getCreateTime() > 0) {
            setCreateTime(new Date(trigger.getCreateTime()));
        }
        setOperatorId(trigger.getOperatorId());
        setTriggerType(Integer.valueOf(trigger.getTriggerType()));
        if (trigger.getBeginTime() > 0) {
            setBeginTime(new Date(trigger.getBeginTime()));
        }
        if (trigger.getEndTime() > 0) {
            setEndTime(new Date(trigger.getEndTime()));
        }

        setTimes(trigger.getTimes());
        setExecuteInterval(trigger.getExecuteInterval());
        setIntervalUnit(trigger.getIntervalUnit());
    }

    public java.util.Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(java.util.Date beginTime) {
        this.beginTime = beginTime;
    }

    public java.util.Date getEndTime() {
        return endTime;
    }

    public void setEndTime(java.util.Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getExecuteInterval() {
        return executeInterval;
    }

    public void setExecuteInterval(Integer executeInterval) {
        this.executeInterval = executeInterval;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }
}
