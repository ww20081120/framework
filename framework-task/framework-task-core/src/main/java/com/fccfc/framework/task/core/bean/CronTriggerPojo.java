package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

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
