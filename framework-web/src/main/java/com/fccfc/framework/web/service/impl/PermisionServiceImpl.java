/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.bean.resource.MenuPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;
import com.fccfc.framework.web.dao.resource.MenuDao;
import com.fccfc.framework.web.dao.resource.UrlResourceDao;
import com.fccfc.framework.web.service.PermisionService;

/** 
 * <Description> <br> 
 *  
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.migu.kf.manger.service.impl <br>
 */
@Service
public class PermisionServiceImpl implements PermisionService {
    
    @Resource
    private MenuDao menuDao;
    
    @Resource
    private UrlResourceDao urlResourceDao;

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @return
     * @throws ServiceException <br>
     */ 
    @Override
    public List<MenuPojo> queryMenu(Integer operateId) throws ServiceException {
        try {
         List<MenuPojo> menuList = menuDao.selectMenuByPermision(operateId, Configuration.getModuleCode());
         return menuList;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @return
     * @throws ServiceException <br>
     */ 
    @Override
    public List<UrlResourcePojo> queryUrlResource(Integer operateId) throws ServiceException {
        try {
            return urlResourceDao.selectUrlResourceByPermision(operateId,  Configuration.getModuleCode());
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }


}
