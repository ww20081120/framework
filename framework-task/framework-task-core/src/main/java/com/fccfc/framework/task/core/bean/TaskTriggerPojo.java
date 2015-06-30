package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> TASK_TRIGGER的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */

public class TaskTriggerPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** TASK_ID */
    @Column(name = "TASK_ID")
    private Integer taskId;

    /** TRIGGER_TYPE */
    @Column(name = "TRIGGER_TYPE")
    private Integer triggerType;

    /** TRIGGER_ID */
    @Column(name = "TRIGGER_ID")
    private Integer triggerId;

    public Integer getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTriggerType() {
        return this.triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }

    public Integer getTriggerId() {
        return this.triggerId;
    }

    public void setTriggerId(Integer triggerId) {
        this.triggerId = triggerId;
    }

}
