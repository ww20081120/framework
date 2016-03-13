package com.hbasesoft.framework.config.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> MODULE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "T_SYS_MODULE")
public class ModulePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** MODULE_CODE */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODULE_CODE")
    private String moduleCode;

    /** PARENT_MODULE_CODE */
    @Column(name = "PARENT_MODULE_CODE")
    private String parentModuleCode;

    /** MODULE_NAME */
    @Column(name = "MODULE_NAME")
    private String moduleName;

    public String getModuleCode() {
        return this.moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getParentModuleCode() {
        return this.parentModuleCode;
    }

    public void setParentModuleCode(String parentModuleCode) {
        this.parentModuleCode = parentModuleCode;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

}
