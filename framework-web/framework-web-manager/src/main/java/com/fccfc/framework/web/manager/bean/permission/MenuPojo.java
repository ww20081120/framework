package com.fccfc.framework.web.manager.bean.permission;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.bean.menu <br>
 */
@Entity(name = "MENU")
public class MenuPojo extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 836720737775406950L;

    /** 菜单类型 */
    public static final String TYPE_MENU = "M";

    public static final String TYPE_BUTTON = "B";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /** 菜单标识 */
    /** RESOURCE_ID */
    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    /** 父菜单标识 */
    /** PARENT_RESOURCE_ID */
    @Column(name = "PARENT_RESOURCE_ID")
    private Long parentResourceId;

    /** 业务模块代码 */
    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** 功能点标识 */
    /** FUNCTION_ID */
    @Column(name = "FUNCTION_ID")
    private Long functionId;

    /** 序列 */
    /** SEQ */
    @Column(name = "SEQ")
    private Long seq;

    /** 菜单名称 */
    /** MENU_NAME */
    @Column(name = "MENU_NAME")
    private String menuName;

    /** 访问地址 */
    /** URL */
    @Column(name = "URL")
    private String url;

    /** 是否为叶节点 */
    /** IS_LEAF */
    @Column(name = "IS_LEAF")
    private String isLeaf;

    /** 图标URL */
    /** ICON_URL */
    @Column(name = "ICON_URL")
    private String iconUrl;

    /**
     * 菜单类型：1-是菜单; 其它-非菜单
     */
    @Column(name = "TYPE")
    private String type;

    /** 子菜单 */
    /** childrenMenu */
    @Transient
    private List<MenuPojo> childrenMenu;

    /** 关联功能 */
    /** function */
    @Transient
    private List<FunctionPojo> function;

    /** 关联业务 */
    /** module */
    @Transient
    private List<ModulePojo> module;

    @Transient
    private String moduleName;

    @Transient
    private String parentMenuName;

    @Transient
    private String functionName;

    public MenuPojo() {

    }

    public MenuPojo(Long parentResourceId, String moduleCode, Long functionId, Long seq, String menuName, String url,
        String isLeaf, String iconUrl, String type) {
        super();
        this.parentResourceId = parentResourceId;
        this.moduleCode = moduleCode;
        this.functionId = functionId;
        this.seq = seq;
        this.menuName = menuName;
        this.url = url;
        this.isLeaf = isLeaf;
        this.iconUrl = iconUrl;
        this.type = type;
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

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
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

    public List<FunctionPojo> getFunction() {
        return function;
    }

    public void setFunction(List<FunctionPojo> function) {
        this.function = function;
    }

    public List<ModulePojo> getModule() {
        return module;
    }

    public void setModule(List<ModulePojo> module) {
        this.module = module;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getParentMenuName() {
        return parentMenuName;
    }

    public void setParentMenuName(String parentMenuName) {
        this.parentMenuName = parentMenuName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
