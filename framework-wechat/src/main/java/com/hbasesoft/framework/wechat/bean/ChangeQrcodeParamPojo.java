package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> T_VCC_CHANGE_QRCODE_PARAM的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2019年05月29日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_VCC_CHANGE_QRCODE_PARAM")
public class ChangeQrcodeParamPojo extends BaseEntity {

	public static final String NOT_USED = "0";
	
	public static final String USED = "1";
	
	public static final String QRCODE_PARAMS_ID = "qrcodeParamsId";
	
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** id */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /** qrcode_params_id */
    @Column(name = "qrcode_params_id")
    private String qrcodeParamsId;

    /** create_time */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /** employee_code */
    @Column(name = "employee_code")
    private String employeeCode;

    /** app_id */
    @Column(name = "app_id")
    private String appId;

    /** subs_code */
    @Column(name = "subs_code")
    private String subsCode;

    /** datas */
    @Column(name = "datas")
    private String datas;

    /** org_code */
    @Column(name = "org_code")
    private String orgCode;

    /** type */
    @Column(name = "type")
    private String type;

    /** garden_code */
    @Column(name = "garden_code")
    private String gardenCode;

    /** qrcode_url */
    @Column(name = "qrcode_url")
    private String qrcodeUrl;
    
    /** is_used */
    @Column(name = "is_used")
    private String isUsed;
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQrcodeParamsId() {
        return this.qrcodeParamsId;
    }

    public void setQrcodeParamsId(String qrcodeParamsId) {
        this.qrcodeParamsId = qrcodeParamsId;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getEmployeeCode() {
        return this.employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSubsCode() {
        return this.subsCode;
    }

    public void setSubsCode(String subsCode) {
        this.subsCode = subsCode;
    }

    public String getDatas() {
        return this.datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGardenCode() {
        return this.gardenCode;
    }

    public void setGardenCode(String gardenCode) {
        this.gardenCode = gardenCode;
    }

    public String getQrcodeUrl() {
        return this.qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

	public String getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(String isUsed) {
		this.isUsed = isUsed;
	}

}
