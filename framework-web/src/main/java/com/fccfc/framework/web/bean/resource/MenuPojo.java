package com.fccfc.framework.web.bean.resource;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> MENU的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "MENU")
public class MenuPojo extends BaseEntity {

    /** 叶子 */
    public static final String LEAF = "Y";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** MENU_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    private Integer menuId;

    /** MODULE_CODE */
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** MENU_NAME */
    @Column(name = "MENU_NAME")
    private String menuName;

    /** PARENT_ID */
    @Column(name = "PARENT_ID")
    private Integer parentId;

    /** IS_LEAF */
    @Column(name = "IS_LEAF")
    private String isLeaf;

    /** RESOURCE_ID */
    @Column(name = "RESOURCE_ID")
    private Integer resourceId;

    /** RESOURCE_ITEM_ID */
    @Column(name = "RESOURCE_ITEM_ID")
    private Integer resourceItemId;

    /** ICON_URL */
    @Column(name = "ICON_URL")
    private String iconUrl;

    /** ICON_URL */
    @Column(name = "SEQ")
    private Integer seq;

    @Transient
    private String url;

    /** children */
    @Transient
    private List<MenuPojo> childrenMenu;

    public Integer getMenuId() {
        return this.menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getIsLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getResourceItemId() {
        return this.resourceItemId;
    }

    public void setResourceItemId(Integer resourceItemId) {
        this.resourceItemId = resourceItemId;
    }

    public String getIconUrl() {
        return this.iconUrl;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public boolean contain(List<MenuPojo> menuList) {
        if (menuList != null && menuList.size() > 0) {
            for (MenuPojo menu : menuList) {
                if (menu.getMenuId().equals(this.getMenuId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
