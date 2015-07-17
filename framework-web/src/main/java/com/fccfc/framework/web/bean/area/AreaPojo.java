package com.fccfc.framework.web.bean.area;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> AREA的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年01月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "AREA")
public class AreaPojo extends BaseEntity {

    /** 社区 */
    public static final String TYPE_COMMUNITY = "O";

    /** 小区 */
    public static final String TYPE_GARDEN = "G";

    /** 市 */
    public static final String TYPE_CITY = "C";

    /** 区,县 */
    public static final String TYPE_DISTRICT = "D";

    /** 省、直辖市 */
    public static final String TYPE_PROVINCE = "P";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** AREA_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AREA_ID")
    private Integer areaId;

    /** PARENT_AREA_ID */
    @Column(name = "PARENT_AREA_ID")
    private Integer parentAreaId;

    /** AREA_TYPE */
    @Column(name = "AREA_TYPE")
    private String areaType;

    /** AREA_NAME */
    @Column(name = "AREA_NAME")
    private String areaName;

    /** AREA_CODE */
    @Column(name = "AREA_CODE")
    private String areaCode;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    /**
     * parent
     */
    @Transient
    private AreaPojo parent;

    /**
     * children
     */
    @Transient
    private List<AreaPojo> children;

    public Integer getAreaId() {
        return this.areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getParentAreaId() {
        return this.parentAreaId;
    }

    public void setParentAreaId(Integer parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    public String getAreaType() {
        return this.areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getAreaName() {
        return this.areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return this.areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<AreaPojo> getChildren() {
        return children;
    }

    public void setChildren(List<AreaPojo> children) {
        this.children = children;
    }

    public AreaPojo getParent() {
        return parent;
    }

    public void setParent(AreaPojo parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new StringBuilder().append('{').append('"').append("areaId").append('"').append(':').append('"')
            .append(areaId).append('"').append(',').append('"').append("parentAreaId").append('"').append(':')
            .append('"').append(parentAreaId).append('"').append(',').append('"').append("areaType").append('"')
            .append(':').append('"').append(areaType).append('"').append(',').append('"').append("areaName")
            .append('"').append(':').append('"').append(areaName).append('"').append(',').append('"')
            .append("areaCode").append('"').append(':').append('"').append(areaCode).append('"').append(',')
            .append('"').append("remark").append('"').append(':').append('"').append(remark).append('"').append('}')
            .toString();
    }
}
