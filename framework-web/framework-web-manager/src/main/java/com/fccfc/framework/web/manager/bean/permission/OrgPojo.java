package com.fccfc.framework.web.manager.bean.permission;

import com.fccfc.framework.db.core.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.fccfc.framework.web.manager.bean.permission <br>
 * @since V1.0<br>
 */
@Entity(name = "ORG")
public class OrgPojo extends BaseEntity {

    private static final long serialVersionUID = -6789259933021333230L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "ORG_NAME")
    private String orgName;

    @Column(name = "ORG_CODE")
    private String orgCode;

    @Column(name = "PARENT_ORG_ID")
    private Long parentOrgId;

    @Column(name = "OWNER_AREA")
    private String ownerArea;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    @Column(name = "STATE")
    private String state;

    @Column(name = "STATE_DATE")
    private Date stateDate;

    @Column(name = "EXT")
    private String ext;

    @Transient
    private boolean permission;

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Long getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(Long parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getOwnerArea() {
        return ownerArea;
    }

    public void setOwnerArea(String ownerArea) {
        this.ownerArea = ownerArea;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStateDate() {
        return stateDate;
    }

    public void setStateDate(Date stateDate) {
        this.stateDate = stateDate;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public boolean isPermission() {
        return permission;
    }

    public void setPermission(boolean permission) {
        this.permission = permission;
    }
}
