package com.fccfc.framework.web.manager.bean.permission;

import javax.persistence.Column;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> ROLE_RESOURCE的Pojo<br>
 *
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
public class RoleResourcePojo extends BaseEntity {

    public static final String RESOURCE_TYPE_MENU = "01";

    public static final String RESOURCE_TYPE_ORG = "02";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ROLE_ID */
    @Column(name = "ROLE_ID")
    private Integer roleId;

    /** RESOURCE_ID */
    @Column(name = "RESOURCE_ID")
    private Integer resourceId;

    /** RESOURCE_TYPE */
    @Column(name = "RESOURCE_TYPE")
    private String resourceType;

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

}
