/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.job.manager.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hbasesoft.framework.job.manager.bean.RegistryCenterConfiguration;
import com.hbasesoft.framework.job.manager.service.RegistryCenterConfigurationService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.plat.job.manager.controller <br>
 */
@RestController
@RequestMapping("/api/jobManager/registoryCenters")
public class RegistryCenterController {

    @Resource
    private RegistryCenterConfigurationService registryCenterConfigurationService;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @GetMapping
    public List<RegistryCenterConfiguration> queryAll() {
        return registryCenterConfigurationService.queryAll();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param registryCenterConfiguration
     * @return <br>
     */
    @PostMapping
    public RegistryCenterConfiguration add(@RequestBody RegistryCenterConfiguration registryCenterConfiguration) {
        return registryCenterConfigurationService.add(registryCenterConfiguration);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @GetMapping("/{id}")
    public RegistryCenterConfiguration get(@PathVariable("id") String id) {
        return registryCenterConfigurationService.get(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        registryCenterConfigurationService.delete(id);
    }

}
