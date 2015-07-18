package com.fccfc.framework.web.bean.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> URL_RESOURCE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月26日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "URL_RESOURCE")
public class UrlResourcePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** RESOURCE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID")
    private Integer resourceId;

    /** DIRECTORY_CODE */
    @Column(name = "DIRECTORY_CODE")
    private String directoryCode;

    /** RESOURCE_NAME */
    @Column(name = "RESOURCE_NAME")
    private String resourceName;

    /** URL */
    @Column(name = "URL")
    private String url;

    /** EVENT_ID */
    @Column(name = "EVENT_ID")
    private String eventId;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    /** METHOD */
    @Column(name = "METHOD")
    private String method;

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getDirectoryCode() {
        return this.directoryCode;
    }

    public void setDirectoryCode(String directoryCode) {
        this.directoryCode = directoryCode;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
