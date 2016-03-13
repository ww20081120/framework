package com.hbasesoft.framework.web.system.bean;


/**
 * <Description> <br> 
 *  
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月27日 <br>
 * @since V7.3<br>
 * @see com.hbasesoft.framework.web.manager.bean.dict <br>
 */
public class DictDetails {
    
    private Integer dictDataId;
    
    /** 字典代码 */
    private String dictCode;
    
    /** 字典数据名 */
    private String dictDataName;
    
    /** 字典数据值 */
    private String dictDataValue;
    
    /** 是否固定 */
    private String isFixed;
    
    /** 是否可以删除 */
    private String isCancel;
    
    /** 字典名称 */
    private String dictName;
    
    /** 备注 */
    private String remark;

    public Integer getDictDataId() {
        return dictDataId;
    }

    public void setDictDataId(Integer dictDataId) {
        this.dictDataId = dictDataId;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictDataName() {
        return dictDataName;
    }

    public void setDictDataName(String dictDataName) {
        this.dictDataName = dictDataName;
    }

    public String getDictDataValue() {
        return dictDataValue;
    }

    public void setDictDataValue(String dictDataValue) {
        this.dictDataValue = dictDataValue;
    }

    public String getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(String isFixed) {
        this.isFixed = isFixed;
    }

    public String getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}