package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_OPENAPI_CHANNEL的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_OPENAPI_CHANNEL")
public class OpenapiChannelPojo extends BaseEntity {

	public static final String ACCESS_TOKEN = "accessToken";
	
	public static final String APP_CODE = "appCode";
	
	public static final String APP_SECRET = "appSecret";
	
	public static final String STATE = "state";
	
	// 可用
	public static final String AVALIABLE = "A";
	
	//测试
	public static final String TEST = "T";
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

    /** name */
    @Column(name = "name")
    private String name;

    /** account_id */
    @Column(name = "account_id")
    private String accountId;

    /** access_token */
    @Column(name = "access_token")
    private String accessToken;

    /** host */
    @Column(name = "host")
    private String host;

    /** app_code */
    @Column(name = "app_code")
    private String appCode;

    /** app_secret */
    @Column(name = "app_secret")
    private String appSecret;

    /** state */
    @Column(name = "state")
    private String state;

    /** push_url  */
    @Column(name = "push_url")
    private String pushUrl;
    
    /** token */
    @Column(name = "token")
    private String token;
    
    /**  openapi_state */
    @Column(name = "openapi_state")
    private String openapiState;
   
    /** remark */
    @Column(name = "remark")
    private String remark;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppCode() {
        return this.appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public String getPushUrl() {
		return pushUrl;
	}

	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOpenapiState() {
		return openapiState;
	}

	public void setOpenapiState(String openapiState) {
		this.openapiState = openapiState;
	}

}
