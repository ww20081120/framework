/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.controller.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.ResourcesConfigPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.impl.ResourcesConfigServiceImpl;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月18日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.controller.common <br>
 */
@Controller
@RequestMapping("/resourcesconfig")
public class ResourcesConfigController extends AbstractController {

    /**
     * configurationService
     */
    @Resource
    private ResourcesConfigServiceImpl resourcesConfigService;

    /**
     * Description: 查询模块数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(value = "/querySqlByid", method = RequestMethod.GET)
    public List<Map<String, Object>> queryByIdData() throws ServiceException {
        String resourceId = getParameter("resourceId");
        List<Map<String, Object>> rcQuery = new ArrayList<Map<String, Object>>();
        if(null == resourceId ||"".equals(resourceId)){
            return rcQuery;
        }
        List<ResourcesConfigPojo> rcList = resourcesConfigService.selectResourcesConfig(null, resourceId);
        for (ResourcesConfigPojo resourcesConfigPojo : rcList) {
            String sql = resourcesConfigPojo.getQuerySql();
            rcQuery = resourcesConfigService.query(sql);
        }
        return rcQuery;

    }

    /**
     * Description: 查询模块数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public List<ResourcesConfigPojo> queryModuleData() throws ServiceException {
        String moduleCode = getParameter("moduleCode");
        return resourcesConfigService.selectResourcesConfig(moduleCode, null);
    }

}
