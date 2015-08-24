package com.fccfc.framework.config.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;
/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月11日 <br>
 * @since migu<br>
 * @see com.fccfc.framework.config.core.bean <br>
 */
@Entity(name = "DICTIONARY_DATA")
public class DictionaryDataPojo extends BaseEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     * DICT_DATA_ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DICT_DATA_ID")
    private Long dictDataId;
    /**
     * DICT_CODE
     */
    @Column(name = "DICT_CODE")
    private String dictCode;
    /**
     * DICT_DATA_NAME
     */
    @Column(name = "DICT_DATA_NAME")
    private String dictDataName;
    /**
     * DICT_DATA_VALUE
     */
    @Column(name = "DICT_DATA_VALUE")
    private String dictDataValue;
    /**
     * IS_FIXED
     */
    @Column(name = "IS_FIXED")
    private String isFixed;
    /**
     * IS_CANCEL
     */
    @Column(name = "IS_CANCEL")
    private String isCancel;
    public Long getDictDataId() {
        return dictDataId;
    }
    public void setDictDataId(Long dictDataId) {
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
}