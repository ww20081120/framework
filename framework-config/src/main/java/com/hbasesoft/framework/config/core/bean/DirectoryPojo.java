package com.hbasesoft.framework.config.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> MEMBER的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.bean.BaseEntity <br>
 */
@Entity(name = "T_SYS_DIRECTORY")
public class DirectoryPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** DIRECTORY_CODE */
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DIRECTORY_CODE")
    private String directoryCode;

    /** DIRECTORY_NAME */
    @Column(name = "DIRECTORY_NAME")
    private String directoryName;

    /** PARENT_DIRECTORY_CODE */
    @Column(name = "PARENT_DIRECTORY_CODE")
    private String parentDirectoryCode;

    /** REMARK */
    @Column(name = "REMARK")
    private String remark;

    public String getDirectoryCode() {
        return directoryCode;
    }

    public void setDirectoryCode(String directoryCode) {
        this.directoryCode = directoryCode;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getParentDirectoryCode() {
        return parentDirectoryCode;
    }

    public void setParentDirectoryCode(String parentDirectoryCode) {
        this.parentDirectoryCode = parentDirectoryCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
