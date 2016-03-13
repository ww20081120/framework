/**
 *
 */
package com.hbasesoft.framework.web.permission.service;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.MenuPojo;
import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @since V1.0<br>
 */
public interface MenuService {

    /**
     * Description:查询菜单 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    List<MenuPojo> queryMenu(String moduleCode) throws ServiceException;

    /**
     * Description: 按照菜单标识查询菜单<br>
     *
     * @param resourceId
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    MenuPojo queryById(Long resourceId) throws ServiceException;

    /**
     * Description:添加菜单 <br>
     *
     * @param menuPojo
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    void addMenu(MenuPojo menuPojo) throws ServiceException;

    /**
     * Description:更新/修改菜单 <br>
     *
     * @param menuPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    void modifyMenu(MenuPojo menuPojo) throws ServiceException;

    /**
     * Description: 更新菜单顺序<br>
     *
     * @param menuPojos
     * @throws ServiceException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    void modifyMenuSeq(List<MenuPojo> menuPojos) throws ServiceException;

    /**
     * Description: 根据resourceId删除<br>
     *
     * @param resourceIds
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    void remove(String resourceIds) throws ServiceException;

    boolean checkName(String resourceId, String menuName);
}
