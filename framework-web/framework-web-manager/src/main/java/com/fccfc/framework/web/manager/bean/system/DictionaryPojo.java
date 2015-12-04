package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fccfc.framework.db.core.BaseEntity;

@Entity
@Table(name = "DICTIONARY")
public class DictionaryPojo extends BaseEntity {
    
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1L;

    /** 字典代码 */
    @Id
    @Column(name = "DICT_CODE")
    private String dictCode;
    
    /** 字典名称 */
    @Column(name = "DICT_NAME")
    private String dictName;
    
    /** 备注 */
    @Column(name = "REMARK")
    private String remark;
    
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
     * 获取字典名称
     * 
     * @return 字典名称
     */
    public String getDictName() {
        return this.dictName;
    }
     
    /**
     * 设置字典名称
     * 
     * @param dictName
     *          字典名称
     */
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }
    
    /**
     * 获取备注
     * 
     * @return 备注
     */
    public String getRemark() {
        return this.remark;
    }
     
    /**
     * 设置备注
     * 
     * @param remark
     *          备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}