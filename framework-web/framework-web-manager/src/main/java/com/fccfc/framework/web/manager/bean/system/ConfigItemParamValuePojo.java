package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> CONFIG_ITEM_PARAM_VALUE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "CONFIG_ITEM_PARAM_VALUE")
public class ConfigItemParamValuePojo extends BaseEntity {

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

    /** PARAM_VALUE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARAM_VALUE_ID")
    private Integer paramValueId;

    /** VALUE_MARK */
    @Column(name = "VALUE_MARK")
    private String valueMark;

    /** VALUE */
    @Column(name = "VALUE")
    private String value;

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

    public Integer getParamValueId() {
        return this.paramValueId;
    }

    public void setParamValueId(Integer paramValueId) {
        this.paramValueId = paramValueId;
    }

    public String getValueMark() {
        return this.valueMark;
    }

    public void setValueMark(String valueMark) {
        this.valueMark = valueMark;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
