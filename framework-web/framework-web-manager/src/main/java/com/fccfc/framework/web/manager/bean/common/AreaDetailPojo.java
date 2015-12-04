package com.fccfc.framework.web.manager.bean.common;


public class AreaDetailPojo {
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

    private Integer areaId;

    private Integer parentAreaId;

    private String areaType;

    private String areaName;

    private String areaCode;

    private String remark;

    private String longitude;

    private String latitude;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(Integer parentAreaId) {
        this.parentAreaId = parentAreaId;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
