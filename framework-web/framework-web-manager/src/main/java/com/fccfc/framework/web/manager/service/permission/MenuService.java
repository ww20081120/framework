/**
 *
 */
package com.fccfc.framework.web.manager.service.permission;

import java.util.List;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;
import com.fccfc.framework.web.manager.bean.permission.MenuPojo;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @see com.fccfc.framework.web.manager.service.menu <br>
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
    List<MenuPojo> queryMenu(String moduleCode, String type) throws ServiceException;

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
     * Description: 查询业务模块<br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    List<ModulePojo> queryModule() throws ServiceException;

    ;

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
     * Description:查询所有的Url资源信息 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    List<UrlResourcePojo> queryUrlResource() throws ServiceException;

    /**
     * Description:根据moduleCode查询modulePojo对象 <br>
     *
     * @param moduleCode
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    ModulePojo queryModule(String moduleCode) throws ServiceException;

    /**
     * Description: 根据functionId查询functionPojo对象<br>
     *
     * @param functionId
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    FunctionPojo queryFunction(Long functionId) throws ServiceException;

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

    List<MenuPojo> listMenuButton(String moduleCode, String menuId, int pageIndex, int pageSize)
        throws ServiceException;
}
