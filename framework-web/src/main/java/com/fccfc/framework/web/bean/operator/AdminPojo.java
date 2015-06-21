package com.fccfc.framework.web.bean.operator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.api.bean.BaseEntity;

/**
 * <Description> ADMIN的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年06月03日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "ADMIN")
public class AdminPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ADMIN_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ADMIN_ID")
    private Integer adminId;

    /** ADMIN_NAME */
    @Column(name = "ADMIN_NAME")
    private String adminName;

    /** CREATED_TIME */
    @Column(name = "CREATED_TIME")
    private java.util.Date createdTime;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** STATE_DATE */
    @Column(name = "STATE_DATE")
    private java.util.Date stateDate;

    /** EMAIL */
    @Column(name = "EMAIL")
    private String email;

    /** PHONE */
    @Column(name = "PHONE")
    private String phone;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** ADDRESS */
    @Column(name = "ADDRESS")
    private String address;

    public Integer getAdminId() {
        return this.adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return this.adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public java.util.Date getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(java.util.Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public java.util.Date getStateDate() {
        return this.stateDate;
    }

    public void setStateDate(java.util.Date stateDate) {
        this.stateDate = stateDate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
