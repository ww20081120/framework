package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> CONFIG_ITEM_PARAM的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
public class ConfigItemParamPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** CONFIG_ITEM_ID */
    @Column(name = "CONFIG_ITEM_ID")
    private Integer configItemId;

    /** PARAM_CODE */
    @Column(name = "PARAM_CODE")
    private String paramCode;

    /** PARAM_NAME */
    @Column(name = "PARAM_NAME")
    private String paramName;

    /** PARAM_VALUE */
    @Column(name = "PARAM_VALUE")
    private String paramValue;

    /** DEFAULT_PARAM_VALUE */
    @Column(name = "DEFAULT_PARAM_VALUE")
    private String defaultParamValue;

    /** DATA_TYPE */
    @Column(name = "DATA_TYPE")
    private String dataType;

    /** INPUT_TYPE */
    @Column(name = "INPUT_TYPE")
    private String inputType;

    /** VALUE_SCRIPT */
    @Column(name = "VALUE_SCRIPT")
    private String valueScript;

    /** UPDATE_TIME */
    @Column(name = "UPDATE_TIME")
    private java.util.Date updateTime;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    public Integer getConfigItemId() {
        return this.configItemId;
    }

    public void setConfigItemId(Integer configItemId) {
        this.configItemId = configItemId;
    }

    public String getParamCode() {
        return this.paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getDefaultParamValue() {
        return this.defaultParamValue;
    }

    public void setDefaultParamValue(String defaultParamValue) {
        this.defaultParamValue = defaultParamValue;
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

    public java.util.Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
