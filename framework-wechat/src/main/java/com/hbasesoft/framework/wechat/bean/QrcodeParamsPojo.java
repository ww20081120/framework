package com.hbasesoft.framework.wechat.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> T_VCC_QRCODE_PARAMS的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年11月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_VCC_QRCODE_PARAMS")
public class QrcodeParamsPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    public static final String EMPLOYEE_CODE = "employeeCode";
    
    public static final String APP_ID = "appId";
    
    public static final String SUBS_CODE = "subsCode";
    
    public static final String GARDEN_CODE = "gardenCode";

    /** id */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /** employee_code */
    @Column(name = "employee_code")
    private String employeeCode;

    /** appId */
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
    
    /** garden_code */
    @Column(name = "garden_code")
    private String gardenCode;
    
    /** type */
    @Column(name = "type")
    private String type;
    
    @Column(name = "create_time")
    private Date createTime;
    
    @Column(name = "qrcode_url")
    private String qrcodeUrl;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return this.employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getAppId() {
		return appId;
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

	public String getGardenCode() {
		return gardenCode;
	}

	public void setGardenCode(String gardenCode) {
		this.gardenCode = gardenCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getQrcodeUrl() {
		return qrcodeUrl;
	}

	public void setQrcodeUrl(String qrcodeUrl) {
		this.qrcodeUrl = qrcodeUrl;
	}
    
}
