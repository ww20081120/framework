package com.fccfc.framework.web.bean.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> URL_RESOURCE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年07月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "URL_RESOURCE")
public class UrlResourcePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** RESOURCE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESOURCE_ID")
    @SequenceGenerator(name = "SEQ_RESOURCE_ID", sequenceName = "SEQ_RESOURCE_ID", allocationSize = 1)
    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    /** DIRECTORY_CODE */
    @Column(name = "DIRECTORY_CODE")
    private String directoryCode;

    /** RESOURCE_NAME */
    @Column(name = "RESOURCE_NAME")
    private String resourceName;

    /** URL */
    @Column(name = "URL")
    private String url;

    /** METHOD */
    @Column(name = "METHOD")
    private String method;

    /** EVENT_ID */
    @Column(name = "EVENT_ID")
    private String eventId;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    /** IS_SELECT */
    @Column(name = "IS_SELECT")
    private String isSelect;

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceId) {
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

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIsSelect() {
        return isSelect;
    }

    public void setIsSelect(String isSelect) {
        this.isSelect = isSelect;
    }

}
