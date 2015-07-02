/**
 * 
 */
package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.bean.resource.MenuPojo;

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
     * @throws ServiceException <br>
     */
    void cacheAllMenu() throws ServiceException;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param className <br>
     * @param method <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    List<MenuPojo> selectBreadLine(String className, String method) throws ServiceException;
}
