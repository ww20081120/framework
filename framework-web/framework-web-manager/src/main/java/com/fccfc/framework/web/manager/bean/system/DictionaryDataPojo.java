package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fccfc.framework.db.core.BaseEntity;

@Entity
@Table(name = "DICTIONARY_DATA")
public class DictionaryDataPojo extends BaseEntity {
    /** 版本号 */
    private static final long serialVersionUID = 1L;
    
    /** 字典数据表标识 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DICT_DATA_ID")
    private Integer dictDataId;
    
    /** 字典代码 */
    @Column(name = "DICT_CODE")
    private String dictCode;
    
    /** 字典数据名 */
    @Column(name = "DICT_DATA_NAME")
    private String dictDataName;
    
    /** 字典数据值 */
    @Column(name = "DICT_DATA_VALUE")
    private String dictDataValue;
    
    /** 是否固定 */
    @Column(name = "IS_FIXED")
    private String isFixed;
    
    /** 是否可以删除 */
    @Column(name = "IS_CANCEL")
    private String isCancel;
    
    /**
     * 获取字典数据表标识
     * 
     * @return 字典数据表标识
     */
    public Integer getDictDataId() {
        return this.dictDataId;
    }
     
    /**
     * 设置字典数据表标识
     * 
     * @param dictDataId
     *          字典数据表标识
     */
    public void setDictDataId(Integer dictDataId) {
        this.dictDataId = dictDataId;
    }
    
    /**
     * 获取字典代码
     * 
     * @return 字典代码
     */
    public String getDictCode() {
        return this.dictCode;
    }
     
    /**
     * 设置字典代码
     * 
     * @param dictCode
     *          字典代码
     */
    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }
    
    /**
     * 获取字典数据名
     * 
     * @return 字典数据名
     */
    public String getDictDataName() {
        return this.dictDataName;
    }
     
    /**
     * 设置字典数据名
     * 
     * @param dictDataName
     *          字典数据名
     */
    public void setDictDataName(String dictDataName) {
        this.dictDataName = dictDataName;
    }
    
    /**
     * 获取字典数据值
     * 
     * @return 字典数据值
     */
    public String getDictDataValue() {
        return this.dictDataValue;
    }
     
    /**
     * 设置字典数据值
     * 
     * @param dictDataValue
     *          字典数据值
     */
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
}