package com.hbasesoft.framework.web.system.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> ATTR_VALUE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年11月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "ATTR_VALUE")
public class AttrValuePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ATTR_ID */
    @Column(name = "ATTR_ID")
    private Integer attrId;

    /** ATTR_VALUE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTR_VALUE_ID")
    private Integer attrValueId;

    /** VALUE_MARK */
    @Column(name = "VALUE_MARK")
    private String valueMark;

    /** VALUE */
    @Column(name = "VALUE")
    private String value;

    /** LINK_ATTR_ID */
    @Column(name = "LINK_ATTR_ID")
    private Integer linkAttrId;

    public Integer getAttrId() {
        return this.attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public Integer getAttrValueId() {
        return this.attrValueId;
    }

    public void setAttrValueId(Integer attrValueId) {
        this.attrValueId = attrValueId;
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

    public Integer getLinkAttrId() {
        return this.linkAttrId;
    }

    public void setLinkAttrId(Integer linkAttrId) {
        this.linkAttrId = linkAttrId;
    }

}
