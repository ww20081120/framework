/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.init;

import java.util.List;

import javax.annotation.Resource;

import com.fccfc.framework.common.Initialization;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.bean.resource.MenuPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;
import com.fccfc.framework.web.service.ResourceService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月26日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.init <br>
 */
public class ResourceLoader implements Initialization {

    /** resourceService */
    @Resource
    private ResourceService resourceService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> modules = Configuration.getModuleCode();
        List<UrlResourcePojo> urlResourceList = resourceService.queryUrlResource(modules);
        if (CommonUtil.isNotEmpty(urlResourceList)) {
            StartupServlet.getContext().setAttribute(WebConstant.APPLICATION_URL, urlResourceList);
        }

        List<MenuPojo> menuResourceList = resourceService.queryMenu(modules);
        if (CommonUtil.isNotEmpty(menuResourceList)) {
            StartupServlet.getContext().setAttribute(WebConstant.APPLICATION_MENU, menuResourceList);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @Override
    public void destroy() throws Exception {
        StartupServlet.getContext().removeAttribute(WebConstant.APPLICATION_URL);
        StartupServlet.getContext().removeAttribute(WebConstant.APPLICATION_MENU);
    }

}
