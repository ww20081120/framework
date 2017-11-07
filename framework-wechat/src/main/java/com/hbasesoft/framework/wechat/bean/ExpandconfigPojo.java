package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_EXPANDCONFIG的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_EXPANDCONFIG")
public class ExpandconfigPojo extends BaseEntity {

    public static final String ACCOUNT_ID = "accountid";

    public static final String CLASSNAME = "classname";
    
    public static final String ORG_CODE = "orgCode";
    
    public static final String ORDER = "order";

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

    /** classname */
    @Column(name = "classname")
    private String classname;

    /** content */
    @Column(name = "content")
    private String content;

    /** name */
    @Column(name = "name")
    private String name;

    /** org_code */
    @Column(name = "org_code")
    private String orgCode;

    @Column(name = "order")
    private String order;
    
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassname() {
        return this.classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
