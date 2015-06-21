package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

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
