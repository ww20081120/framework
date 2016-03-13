package com.hbasesoft.framework.web.system.bean;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> CONFIG_ITEM_HISTORY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年11月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */

public class ConfigItemHistoryPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** CONFIG_ITEM_ID */
    @Column(name = "CONFIG_ITEM_ID")
    private Integer configItemId;

    /** SEQ */
    @Column(name = "SEQ")
    private Integer seq;

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

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** CHANNEL_ID */
    @Column(name = "CHANNEL_ID")
    private Integer channelId;

    public Integer getConfigItemId() {
        return this.configItemId;
    }

    public void setConfigItemId(Integer configItemId) {
        this.configItemId = configItemId;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
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

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getChannelId() {
        return this.channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

}
