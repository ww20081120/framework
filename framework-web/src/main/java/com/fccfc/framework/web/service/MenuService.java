/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.web.MenuPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service <br>
 */
public interface MenuService {

    /**
     * 缓存所有菜单
     * 
     * @throws ServiceException
     */
    void cacheAllMenu() throws ServiceException;

    List<MenuPojo> selectBreadLine(String className, String method) throws ServiceException;
}
