package com.fccfc.framework.config.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> CONFIG_ITEM的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月08日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "CONFIG_ITEM")
public class ConfigItemPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** CONFIG_ITEM_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONFIG_ITEM_ID")
    private Integer configItemId;

    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** DIRECTORY_CODE */
    @Column(name = "DIRECTORY_CODE")
    private String directoryCode;

    /** CONFIG_ITEM_CODE */
    @Column(name = "CONFIG_ITEM_CODE")
    private String configItemCode;

    /** CONFIG_ITEM_NAME */
    @Column(name = "CONFIG_ITEM_NAME")
    private String configItemName;

    /** IS_VISIABLE */
    @Column(name = "IS_VISIABLE")
    private String isVisiable;

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

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getDirectoryCode() {
        return this.directoryCode;
    }

    public void setDirectoryCode(String directoryCode) {
        this.directoryCode = directoryCode;
    }

    public String getConfigItemCode() {
        return this.configItemCode;
    }

    public void setConfigItemCode(String configItemCode) {
        this.configItemCode = configItemCode;
    }

    public String getConfigItemName() {
        return this.configItemName;
    }

    public void setConfigItemName(String configItemName) {
        this.configItemName = configItemName;
    }

    public String getIsVisiable() {
        return this.isVisiable;
    }

    public void setIsVisiable(String isVisiable) {
        this.isVisiable = isVisiable;
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
