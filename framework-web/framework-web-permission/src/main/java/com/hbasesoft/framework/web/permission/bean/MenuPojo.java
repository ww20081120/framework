package com.hbasesoft.framework.web.permission.bean;

import javax.persistence.*;

import com.hbasesoft.framework.db.core.BaseEntity;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @see com.hbasesoft.framework.web.manager.bean.menu <br>
 * @since V1.0<br>
 */
@Entity(name = "MENU")
public class MenuPojo extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 836720737775406950L;

    /**
     * 菜单类型
     */
    public static final String TYPE_MENU = "M";

    public static final String TYPE_BUTTON = "B";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 菜单标识 */
    /** RESOURCE_ID */
    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    /** 父菜单标识 */
    /**
     * PARENT_RESOURCE_ID
     */
    @Column(name = "PARENT_RESOURCE_ID")
    private Long parentResourceId;

    /** 业务模块代码 */
    /**
     * MODULE_CODE
     */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** 功能点标识 */
    /**
     * FUNCTION_ID
     */
    @Column(name = "FUNCTION_CODE")
    private String functionCode;

    /** 序列 */
    /**
     * SEQ
     */
    @Column(name = "SEQ")
    private Long seq;

    /** 菜单名称 */
    /**
     * MENU_NAME
     */
    @Column(name = "MENU_NAME")
    private String menuName;

    /** 访问地址 */
    /**
     * URL
     */
    @Column(name = "URL")
    private String url;

    /** 是否为叶节点 */
    /**
     * IS_LEAF
     */
    @Column(name = "IS_LEAF")
    private String isLeaf;

    /** 图标URL */
    /**
     * ICON_URL
     */
    @Column(name = "ICON_URL")
    private String iconUrl;

    /** 子菜单 */
    /**
     * childrenMenu
     */
    @Transient
    private List<MenuPojo> childrenMenu;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static String getTypeMenu() {
        return TYPE_MENU;
    }

    public static String getTypeButton() {
        return TYPE_BUTTON;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getParentResourceId() {
        return parentResourceId;
    }

    public void setParentResourceId(Long parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public List<MenuPojo> getChildrenMenu() {
        return childrenMenu;
    }

    public void setChildrenMenu(List<MenuPojo> childrenMenu) {
        this.childrenMenu = childrenMenu;
    }
}
