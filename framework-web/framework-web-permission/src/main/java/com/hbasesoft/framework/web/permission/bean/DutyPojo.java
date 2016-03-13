package com.hbasesoft.framework.web.permission.bean;

import javax.persistence.*;

import com.hbasesoft.framework.db.core.BaseEntity;

import java.util.Date;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.hbasesoft.framework.web.manager.bean.duty <br>
 * @since V1.0<br>
 */
@Entity(name = "DUTY")
public class DutyPojo extends BaseEntity {

    private static final long serialVersionUID = -4758506714440377983L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DUTY_ID")
    private Long dutyId;

    @Column(name = "DUTY_NAME")
    private String dutyName;

    @Column(name = "ORG_ID")
    private Long orgId;

    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "STATE")
    private String state;

    @Column(name = "STATE_DATE")
    private Date stateDate;

    @Column(name = "EXT")
    private String ext;

    @Transient
    private String orgName;

    @Transient
    private String roleList;

    public Long getDutyId() {
        return dutyId;
    }

    public void setDutyId(Long dutyId) {
        this.dutyId = dutyId;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getRoleList() {
        return roleList;
    }

    public void setRoleList(String roleList) {
        this.roleList = roleList;
    }
}
