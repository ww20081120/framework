package com.fccfc.framework.task.core.bean;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> TASK_HISTORY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月05日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */

public class TaskHistoryPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** TASK_ID */
    @Column(name = "TASK_ID")
    private Integer taskId;

    /** SEQ */
    @Column(name = "SEQ")
    private Integer seq;

    /** TASK_NAME */
    @Column(name = "TASK_NAME")
    private String taskName;

    /** CLASS_NAME */
    @Column(name = "CLASS_NAME")
    private String className;

    /** METHOD */
    @Column(name = "METHOD")
    private String method;

    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** PRIORITY */
    @Column(name = "PRIORITY")
    private Integer priority;

    /** IS_CONCURRENT */
    @Column(name = "IS_CONCURRENT")
    private String isConcurrent;

    /** TASK_STATE */
    @Column(name = "TASK_STATE")
    private String taskState;

    /** LAST_EXECUTE_TIME */
    @Column(name = "LAST_EXECUTE_TIME")
    private java.util.Date lastExecuteTime;

    /** NEXT_EXCUTE_DATE */
    @Column(name = "NEXT_EXCUTE_DATE")
    private java.util.Date nextExcuteDate;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** UPDATE_TIME */
    @Column(name = "UPDATE_TIME")
    private java.util.Date updateTime;

    /** UPDATE_OPERATOR_ID */
    @Column(name = "UPDATE_OPERATOR_ID")
    private Integer updateOperatorId;

    public Integer getTaskId() {
        return this.taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getIsConcurrent() {
        return this.isConcurrent;
    }

    public void setIsConcurrent(String isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    public String getTaskState() {
        return this.taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public java.util.Date getLastExecuteTime() {
        return this.lastExecuteTime;
    }

    public void setLastExecuteTime(java.util.Date lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    public java.util.Date getNextExcuteDate() {
        return this.nextExcuteDate;
    }

    public void setNextExcuteDate(java.util.Date nextExcuteDate) {
        this.nextExcuteDate = nextExcuteDate;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
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
