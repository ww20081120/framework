package com.fccfc.framework.task.core.bean;

import java.util.Date;

import javax.persistence.Column;

import com.fccfc.framework.task.api.CronTrigger;

/**
 * <Description> CRON_TRIGGER的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
public class CronTriggerPojo extends TriggerPojo {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8638840359147174356L;
    
    /**
     * 
     */
    public CronTriggerPojo(CronTrigger trigger) {
        setTriggerId(trigger.getTriggerId());
        setTriggerName(trigger.getTriggerName());
        if(trigger.getCreateTime() > 0){
            setCreateTime(new Date(trigger.getCreateTime()));
        }
        setOperatorId(trigger.getOperatorId());
        setTriggerType(Integer.valueOf(trigger.getTriggerType()));
        setCronExpression(trigger.getCronExpression());

    }

    public CronTriggerPojo() {
    }

    /** CRON_EXPRESSION */
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
