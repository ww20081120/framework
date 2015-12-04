package com.fccfc.framework.web.manager.bean.common;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> AREA_RANGE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年01月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */

public class AreaRangePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** AREA_ID */
    @Column(name = "AREA_ID")
    private Integer areaId;

    /** SEQ */
    @Column(name = "SEQ")
    private Integer seq;

    /** LONGITUDE */
    @Column(name = "LONGITUDE")
    private String longitude;

    /** LATITUDE */
    @Column(name = "LATITUDE")
    private String latitude;

    public Integer getAreaId() {
        return this.areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
