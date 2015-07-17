package com.fccfc.framework.web.bean.operator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> ACCOUNT的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月04日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "ACCOUNT")
public class AccountPojo extends BaseEntity {

    /** 会员类型 */
    public static final String TYPE_MEMBER = "M";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ACCOUNT_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Integer accountId;

    /** ACCOUNT_TYPE */
    @Column(name = "ACCOUNT_TYPE")
    private String accountType;

    /** ACCOUNT_VALUE */
    @Column(name = "ACCOUNT_VALUE")
    private String accountValue;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** STATE_TIME */
    @Column(name = "STATE_TIME")
    private java.util.Date stateTime;

    /** EXT1 */
    @Column(name = "EXT1")
    private String ext1;

    /** EXT2 */
    @Column(name = "EXT2")
    private String ext2;

    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountValue() {
        return this.accountValue;
    }

    public void setAccountValue(String accountValue) {
        this.accountValue = accountValue;
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

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public java.util.Date getStateTime() {
        return this.stateTime;
    }

    public void setStateTime(java.util.Date stateTime) {
        this.stateTime = stateTime;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return this.ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

}
