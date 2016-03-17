/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.config.core.bean;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.config.core.bean <br>
 */
public class Config extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 3243226114413304338L;

    private String configItemCode;

    private String moduleCode;

    private String paramCode;

    private String paramValue;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return configItemCode <br>
     */
    public String getConfigItemCode() {
        return configItemCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param configItemCode <br>
     */
    public void setConfigItemCode(String configItemCode) {
        this.configItemCode = configItemCode;
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
     * @return paramCode <br>
     */
    public String getParamCode() {
        return paramCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramCode <br>
     */
    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return paramValue <br>
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param paramValue <br>
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

}
