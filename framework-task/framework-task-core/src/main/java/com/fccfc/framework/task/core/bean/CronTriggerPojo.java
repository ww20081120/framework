package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

import com.fccfc.framework.common.utils.CommonUtil;
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
     * 默认构造函数
     */
    public CronTriggerPojo() {
    }

    /**
     * 默认构造函数
     * @param trigger <br>
     */
    public CronTriggerPojo(CronTrigger trigger) {
        this.setTriggerId(trigger.getTriggerId());
        this.setTriggerName(trigger.getTriggerName());
        this.setCreateTime(CommonUtil.getDate(trigger.getCreateTime()));
        this.setOperatorId(trigger.getOperatorId());
        this.setTriggerType(trigger.getTriggerType());
        this.cronExpression = trigger.getCronExpression();
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
