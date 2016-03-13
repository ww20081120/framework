package com.hbasesoft.framework.web.system.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> EVENT的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年07月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.system.bean <br>
 */
@Entity(name = "EVENT")
public class EventPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** EVENT_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Integer eventId;

    /** EVENT_CODE */
    @Column(name = "EVENT_CODE")
    private String eventCode;

    /** EVENT_TYPE */
    @Column(name = "EVENT_TYPE")
    private String eventType;

    /** PARAMS_NAME */
    @Column(name = "PARAMS_NAME")
    private String paramsName;

    /** EVENT_NAME */
    @Column(name = "EVENT_NAME")
    private String eventName;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    public Integer getEventId() {
        return this.eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventTypeName() {
        return ConfigHelper.getDictName("EVENT_TYPE", this.eventType);
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getParamsName() {
        return this.paramsName;
    }

    public void setParamsName(String paramsName) {
        this.paramsName = paramsName;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
