package com.fccfc.framework.web.manager.bean.permission;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;


@Entity(name = "OPERATOR")
public class OperatorPojo extends BaseEntity {

    public static final String OPERATOR_TYPE_ADMIN = "A";

    public static final String OPERATOR_TYPE_MEMBER = "M";

    
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** OPERATOR_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** OPERATOR_TYPE */
    @Column(name = "OPERATOR_TYPE")
    private String operatorType;

    /** OPERATOR_CODE */
    @Column(name = "OPERATOR_CODE")
    private Integer operatorCode;

    /** 岗位 */
    @Column(name = "DUTY_ID")
    private Long dutyId;

    /** USER_NAME */
    @Column(name = "USER_NAME")
    private String userName;

    /** PASSWORD */
    @Column(name = "PASSWORD")
    private String password;

    /** CREATE_DATE */
    @Column(name = "CREATE_DATE")
    private Date createDate;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** STATE_DATE */
    @Column(name = "STATE_DATE")
    private Date stateDate;

    /** IS_LOCKED */
    @Column(name = "IS_LOCKED")
    private String isLocked;

    /** PWD_EXP_DATE */
    @Column(name = "PWD_EXP_DATE")
    private Date pwdExpDate;

    /** LOGIN_FAIL */
    @Column(name = "LOGIN_FAIL")
    private Integer loginFail;

    /** REGIST_IP */
    @Column(name = "REGIST_IP")
    private String registIp;

    /** LAST_IP */
    @Column(name = "LAST_IP")
    private String lastIp;

    /** LAST_LOGIN_DATE */
    @Column(name = "LAST_LOGIN_DATE")
    private Date lastLoginDate;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(Integer operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public Date getPwdExpDate() {
        return pwdExpDate;
    }

    public void setPwdExpDate(Date pwdExpDate) {
        this.pwdExpDate = pwdExpDate;
    }

    public Integer getLoginFail() {
        return loginFail;
    }

    public void setLoginFail(Integer loginFail) {
        this.loginFail = loginFail;
    }

    public String getRegistIp() {
        return registIp;
    }

    public void setRegistIp(String registIp) {
        this.registIp = registIp;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Long getDutyId() {
        return dutyId;
    }

    public void setDutyId(Long dutyId) {
        this.dutyId = dutyId;
    }

    public OperatorPojo(Integer operatorId, String operatorType, Integer operatorCode, Long dutyId,
        String userName, String password, Date createDate, String state, Date stateDate, String isLocked,
        Date pwdExpDate, Integer loginFail, String registIp, String lastIp, Date lastLoginDate) {
        super();
        this.operatorId = operatorId;
        this.operatorType = operatorType;
        this.operatorCode = operatorCode;
        this.dutyId = dutyId;
        this.userName = userName;
        this.password = password;
        this.createDate = createDate;
        this.state = state;
        this.stateDate = stateDate;
        this.isLocked = isLocked;
        this.pwdExpDate = pwdExpDate;
        this.loginFail = loginFail;
        this.registIp = registIp;
        this.lastIp = lastIp;
        this.lastLoginDate = lastLoginDate;
    }

    public OperatorPojo(String operatorType,  String password, String registIp) {
        super();
        this.operatorType = operatorType;
        this.password = password;
        this.registIp = registIp;
    }

    public OperatorPojo() {
        super();
    }



}
