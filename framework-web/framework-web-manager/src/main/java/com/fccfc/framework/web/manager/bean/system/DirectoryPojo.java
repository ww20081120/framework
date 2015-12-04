package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Transient;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/11 <br>
 * @see com.fccfc.framework.web.manager.bean.directory <br>
 * @since V1.0<br>
 */
public class DirectoryPojo extends com.fccfc.framework.config.core.bean.DirectoryPojo {

    private static final long serialVersionUID = -6427609392510505991L;

    @Transient
    private Integer childNum;

    public Integer getChildNum() {
        return childNum;
    }

    public void setChildNum(Integer childNum) {
        this.childNum = childNum;
    }
}
