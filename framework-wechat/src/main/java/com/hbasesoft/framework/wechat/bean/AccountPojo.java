package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_ACCOUNT的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_ACCOUNT")
public class AccountPojo extends BaseEntity {

	public static final	String ID = "id";
	
    public static final String ACCOUNT_ACCESS_TOKEN = "accountaccesstoken";

    public static final String ACCOUNT_APPID = "accountappid";

    public static final String ACCOUNT_SECRET = "accountappsecret";

    public static final String STATE = "state";

    public static final String STATE_AVALIBLE = "A";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "ID")
    private String id;

    /** accountname */
    @Column(name = "accountname")
    private String accountname;

    /** accounttoken */
    @Column(name = "accounttoken")
    private String accounttoken;

    /** accounttype */
    @Column(name = "accounttype")
    private String accounttype;

    /** accountemail */
    @Column(name = "accountemail")
    private String accountemail;

    /** accountdesc */
    @Column(name = "accountdesc")
    private String accountdesc;

    /** accountaccesstoken */
    @Column(name = "accountaccesstoken")
    private String accountaccesstoken;

    /** accountappid */
    @Column(name = "accountappid")
    private String accountappid;

    /** accountappsecret */
    @Column(name = "accountappsecret")
    private String accountappsecret;

    /** addtokentime */
    @Column(name = "addtoekntime")
    private java.util.Date addtokentime;

    /** weixin_accountid */
    @Column(name = "weixin_accountid")
    private String weixinAccountid;

    /** jsapiticket */
    @Column(name = "jsapiticket")
    private String jsapiticket;

    /** jsapitickettime */
    @Column(name = "jsapitickettime")
    private java.util.Date jsapitickettime;

    /** org_code */
    @Column(name = "org_code")
    private String orgCode;

    /** state */
    @Column(name = "state")
    private String state;

    @Column(name = "host")
    private String host;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountname() {
        return this.accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAccounttoken() {
        return this.accounttoken;
    }

    public void setAccounttoken(String accounttoken) {
        this.accounttoken = accounttoken;
    }

    public String getAccounttype() {
        return this.accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getAccountemail() {
        return this.accountemail;
    }

    public void setAccountemail(String accountemail) {
        this.accountemail = accountemail;
    }

    public String getAccountdesc() {
        return this.accountdesc;
    }

    public void setAccountdesc(String accountdesc) {
        this.accountdesc = accountdesc;
    }

    public String getAccountaccesstoken() {
        return this.accountaccesstoken;
    }

    public void setAccountaccesstoken(String accountaccesstoken) {
        this.accountaccesstoken = accountaccesstoken;
    }

    public String getAccountappid() {
        return this.accountappid;
    }

    public void setAccountappid(String accountappid) {
        this.accountappid = accountappid;
    }

    public String getAccountappsecret() {
        return this.accountappsecret;
    }

    public void setAccountappsecret(String accountappsecret) {
        this.accountappsecret = accountappsecret;
    }

    public java.util.Date getAddtokentime() {
        return this.addtokentime;
    }

    public void setAddtokentime(java.util.Date addtokentime) {
        this.addtokentime = addtokentime;
    }

    public String getWeixinAccountid() {
        return this.weixinAccountid;
    }

    public void setWeixinAccountid(String weixinAccountid) {
        this.weixinAccountid = weixinAccountid;
    }

    public String getJsapiticket() {
        return this.jsapiticket;
    }

    public void setJsapiticket(String jsapiticket) {
        this.jsapiticket = jsapiticket;
    }

    public java.util.Date getJsapitickettime() {
        return this.jsapitickettime;
    }

    public void setJsapitickettime(java.util.Date jsapitickettime) {
        this.jsapitickettime = jsapitickettime;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
