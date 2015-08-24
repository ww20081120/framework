package com.fccfc.framework.web.bean.resource;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> MENU的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年07月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "MENU")
public class MenuPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** RESOURCE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_RESOURCE_ID")
    @SequenceGenerator(name = "SEQ_RESOURCE_ID", sequenceName = "SEQ_RESOURCE_ID")
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESOURCE_ID")
    private Long resourceId;

    /** PARENT_RESOURCE_ID */
    @Column(name = "PARENT_RESOURCE_ID")
    private Long parentResourceId;

    /** SEQ */
    @Column(name = "SEQ")
    private Long seq;

    /** MENU_NAME */
    @Column(name = "MENU_NAME")
    private String menuName;

    /** URL */
    @Column(name = "URL")
    private String url;

    /** IS_LEAF */
    @Column(name = "IS_LEAF")
    private String isLeaf;

    /** ICON_URL */
    @Column(name = "ICON_URL")
    private String iconUrl;

    /**
     * childrenMenu
     */
    @Transient
    private List<MenuPojo> childrenMenu;

    /**
     * moduleCode
     */
    @Transient
    private String moduleCode;

    public Long getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getParentResourceId() {
        return this.parentResourceId;
    }

    public void setParentResourceId(Long parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public Long getSeq() {
        return this.seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIsLeaf() {
        return this.isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
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

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

}
