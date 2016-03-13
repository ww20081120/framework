package com.hbasesoft.framework.web.system.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> ATTR的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月05日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "ATTR")
public class AttrPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ATTR_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTR_ID")
    private Integer attrId;

    /** ATTR_NAME */
    @Column(name = "ATTR_NAME")
    private String attrName;

    /** ATTR_TYPE */
    @Column(name = "ATTR_TYPE")
    private String attrType;

    /** PARENT_ATTR_ID */
    @Column(name = "PARENT_ATTR_ID")
    private Integer parentAttrId;

    /** ATTR_CODE */
    @Column(name = "ATTR_CODE")
    private String attrCode;

    /** VISIBLE */
    @Column(name = "VISIBLE")
    private String visible;

    /** INSTANTIATABLE */
    @Column(name = "INSTANTIATABLE")
    private String instantiatable;

    /** DEFAULT_VALUE */
    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    /** DATA_TYPE */
    @Column(name = "DATA_TYPE")
    private String dataType;

    /** INPUT_TYPE */
    @Column(name = "INPUT_TYPE")
    private String inputType;

    /** VALUE_SCRIPT */
    @Column(name = "VALUE_SCRIPT")
    private String valueScript;

    /**
     * attrValue
     */
    @Transient
    private String attrValue;

    public Integer getAttrId() {
        return this.attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return this.attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrType() {
        return this.attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public Integer getParentAttrId() {
        return this.parentAttrId;
    }

    public void setParentAttrId(Integer parentAttrId) {
        this.parentAttrId = parentAttrId;
    }

    public String getAttrCode() {
        return this.attrCode;
    }

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public String getVisible() {
        return this.visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getInstantiatable() {
        return this.instantiatable;
    }

    public void setInstantiatable(String instantiatable) {
        this.instantiatable = instantiatable;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getInputType() {
        return this.inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getValueScript() {
        return this.valueScript;
    }

    public void setValueScript(String valueScript) {
        this.valueScript = valueScript;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
