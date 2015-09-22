package com.fccfc.framework.web.bean.operator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> OPERATOR的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "OPERATOR")
public class OperatorPojo extends BaseEntity {

    /** 会员类型 */
    public static final String OPERATOR_TYPE_MEMBER = "M";

    /** 操作员类型 */
    public static final String OPERATOR_TYPE_ADMIN = "A";

    /** 操作员状态 */
    public static final String STATE_AVALIABLE = "A";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** OPERATOR_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPERATOR")
    @SequenceGenerator(name = "SEQ_OPERATOR", sequenceName = "SEQ_OPERATOR", allocationSize = 1)
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** OPERATOR_TYPE */
    @Column(name = "OPERATOR_TYPE")
    private String operatorType;

    /** OPERATOR_CODE */
    @Column(name = "OPERATOR_CODE")
    private Integer operatorCode;

    /** 默认角色 */
    @Column(name = "ROLE_ID")
    private Integer roleId;

    /** USER_NAME */
    @Column(name = "USER_NAME")
    private String userName;

    /** PASSWORD */
    @Column(name = "PASSWORD")
    private String password;

    /** CREATE_DATE */
    @Column(name = "CREATE_DATE")
    private java.util.Date createDate;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** STATE_DATE */
    @Column(name = "STATE_DATE")
    private java.util.Date stateDate;

    /** IS_LOCKED */
    @Column(name = "IS_LOCKED")
    private String isLocked;

    /** PWD_EXP_DATE */
    @Column(name = "PWD_EXP_DATE")
    private java.util.Date pwdExpDate;

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
    private java.util.Date lastLoginDate;

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

    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public java.util.Date getStateDate() {
        return stateDate;
    }

    public void setStateDate(java.util.Date stateDate) {
        this.stateDate = stateDate;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(String isLocked) {
        this.isLocked = isLocked;
    }

    public java.util.Date getPwdExpDate() {
        return pwdExpDate;
    }

    public void setPwdExpDate(java.util.Date pwdExpDate) {
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

    public java.util.Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(java.util.Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

}
