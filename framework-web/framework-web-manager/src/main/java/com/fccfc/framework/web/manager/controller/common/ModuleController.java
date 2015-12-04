/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.controller.common;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.common.ModuleService;

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
@RequestMapping("/common/module")
public class ModuleController extends AbstractController {

    /**
     * configurationService
     */
    @Resource
    private ModuleService moduleService;

    /**
     * Description: 查询模块数据<br>
     * 
     * @author wangwei<br>
     * @taskId <br>
     * @return List<ModulePojo>
     * @throws ServiceException <br>
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Collection<ModulePojo> queryModuleData() throws ServiceException {
        Map<String, ModulePojo> moduleMap = moduleService.selectAllModule();
        return moduleMap.values();
    }

}
