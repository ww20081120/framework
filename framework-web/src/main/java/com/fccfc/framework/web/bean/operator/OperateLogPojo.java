package com.fccfc.framework.web.bean.operator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> OPERATE_LOG的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年07月11日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "OPERATE_LOG")
public class OperateLogPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** OPERATE_LOG_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPERATE_LOG")
    @SequenceGenerator(name = "SEQ_OPERATE_LOG", sequenceName = "SEQ_OPERATE_LOG", allocationSize = 1, initialValue = 1)
    @Column(name = "OPERATE_LOG_ID")
    private Integer operateLogId;

    /** EVENT_ID */
    @Column(name = "EVENT_ID")
    private Integer eventId;

    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** IP */
    @Column(name = "IP")
    private String ip;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** PARAMS_VALUE */
    @Column(name = "PARAMS_VALUE")
    private String paramsValue;

    public Integer getOperateLogId() {
        return operateLogId;
    }

    public void setOperateLogId(Integer operateLogId) {
        this.operateLogId = operateLogId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getParamsValue() {
        return this.paramsValue;
    }

    public void setParamsValue(String paramsValue) {
        this.paramsValue = paramsValue;
    }

}
