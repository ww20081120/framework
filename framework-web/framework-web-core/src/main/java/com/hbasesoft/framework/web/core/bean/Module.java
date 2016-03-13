/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.bean;

import java.util.List;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年1月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.bean <br>
 */
public class Module extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -8608202052739653701L;

    private String moduleName;

    private String moduleCode;

    private List<String> controllerPath;

    private List<String> resources;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return moduleName <br>
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param moduleName <br>
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return moduleCode <br>
     */
    public String getModuleCode() {
        return moduleCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param moduleCode <br>
     */
    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return controllerPath <br>
     */
    public List<String> getControllerPath() {
        return controllerPath;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param controllerPath <br>
     */
    public void setControllerPath(List<String> controllerPath) {
        this.controllerPath = controllerPath;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return resources <br>
     */
    public List<String> getResources() {
        return resources;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param resources <br>
     */
    public void setResources(List<String> resources) {
        this.resources = resources;
    }

}
