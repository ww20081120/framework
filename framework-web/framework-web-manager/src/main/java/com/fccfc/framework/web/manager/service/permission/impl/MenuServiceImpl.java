/**
 *
 */
package com.fccfc.framework.web.manager.service.permission.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.annotation.Cache;
import com.fccfc.framework.cache.core.annotation.CacheKey;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.config.core.dao.ModuleDao;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;
import com.fccfc.framework.web.manager.bean.permission.MenuPojo;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;
import com.fccfc.framework.web.manager.dao.permission.menu.FunctionDao;
import com.fccfc.framework.web.manager.dao.permission.menu.MenuDao;
import com.fccfc.framework.web.manager.dao.permission.menu.UrlResourceDao;
import com.fccfc.framework.web.manager.dao.permission.role.RoleResourceDao;
import com.fccfc.framework.web.manager.service.permission.MenuService;

/**
 * 菜单业务模块 <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @see com.fccfc.framework.web.manager.service.menu.impl <br>
 * @since V1.0<br>
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    /**
     * menuDao
     */
    @Resource
    private MenuDao menuDao;

    /**
     * moduleDao
     */
    @Resource
    private ModuleDao moduleDao;

    /**
     * functionDao
     */
    @Resource
    private FunctionDao functionDao;

    /**
     * urlResourceDao
     */
    @Resource
    private UrlResourceDao urlResourceDao;

    @Resource
    private RoleResourceDao roleResourceDao;

    /**
     * 菜单顺序比较器
     */
    private final Comparator<MenuPojo> menuComparator = new Comparator<MenuPojo>() {
        @Override
        public int compare(MenuPojo o1, MenuPojo o2) {
            return o1.getSeq().compareTo(o2.getSeq());
        }
    };

    /**
     * Description:查询所有的菜单 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    @Cache(node = CacheConstant.SYSTEM_MENU_DIR)
    public List<MenuPojo> queryMenu(@CacheKey String moduleCode, String type) throws ServiceException {
        try {
            List<MenuPojo> menuList = menuDao.selectMenu(moduleCode, type);

            Map<Long, MenuPojo> menuMap = new HashMap<Long, MenuPojo>();
            for (MenuPojo menu : menuList) {
                menuMap.put(menu.getResourceId(), menu);
            }
            List<MenuPojo> result = new ArrayList<MenuPojo>();
            for (Entry<Long, MenuPojo> entry : menuMap.entrySet()) {
                MenuPojo menu = entry.getValue();
                if (menu.getParentResourceId() != null) {
                    MenuPojo pMenu = menuMap.get(menu.getParentResourceId());
                    Assert.notNull(pMenu, "子菜单[{0}]未找到[{1}]的父菜单", menu.getResourceId(), menu.getParentResourceId());
                    if (pMenu.getChildrenMenu() == null) {
                        pMenu.setChildrenMenu(new ArrayList<MenuPojo>());
                    }
                    pMenu.getChildrenMenu().add(menu);
                }
                else {
                    result.add(menu);
                }
            }

            sortMenu(result);
            return result;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (AssertException e) {
            throw new ServiceException(e.getCode(), e.getMessage());
        }
    }

    private void sortMenu(List<MenuPojo> menuList) {
        Collections.sort(menuList, menuComparator);
        for (MenuPojo pojo : menuList) {
            if (CommonUtil.isNotEmpty(pojo.getChildrenMenu())) {
                sortMenu(pojo.getChildrenMenu());
            }
        }
    }

    /**
     * Description:查询所有的业务 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<ModulePojo> queryModule() throws ServiceException {
        try {
            return moduleDao.selectAllModule();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description:添加菜单 <br>
     *
     * @param menuPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void addMenu(MenuPojo menuPojo) throws ServiceException {
        try {
            menuPojo.setIsLeaf(ManagerConstant.YES);
            Long seq = menuDao.getSeq(menuPojo.getParentResourceId());
            menuPojo.setSeq((null == seq ? 0 : seq) + 1);
            menuDao.save(menuPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * 更新/修改菜单 Description: <br>
     *
     * @param menuPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void modifyMenu(MenuPojo menuPojo) throws ServiceException {
        try {
            menuDao.updateAttr(menuPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * Description: 查询所有的Url资源信息 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<UrlResourcePojo> queryUrlResource() throws ServiceException {
        try {
            return urlResourceDao.selectUrl();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 根据moduleCode查询modulePojo对象<br>
     *
     * @param moduleCode
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public ModulePojo queryModule(String moduleCode) throws ServiceException {
        try {
            return moduleDao.getById(ModulePojo.class, moduleCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 根据functionId查询functionPojo对象<br>
     *
     * @param functionId
     * @return <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public FunctionPojo queryFunction(Long functionId) throws ServiceException {
        try {
            return functionDao.getById(FunctionPojo.class, functionId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 根据resourceId删除对象<br>
     *
     * @param resourceIds
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void remove(String resourceIds) throws ServiceException {
        try {
            Long[] ids = CommonUtil.splitIdsByLong(resourceIds, ",");
            List<Long> remain = new ArrayList<Long>();
            for (int i = 0; i < ids.length; i++) {
                Long id = ids[i];
                if (CollectionUtils.isEmpty(roleResourceDao.selectListRoleResourceByResourceId(id))) {
                    remain.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(remain)) {
                Long[] remainIds = remain.toArray(new Long[] {});
                int lines = menuDao.deleteByIds(remainIds);
                logger.info("Delete menus or menu buttons successful. effect lines: [{}]", lines);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkName(String resourceId, String menuName) {
        boolean result = false;
        try {
            MenuPojo paramPojo = new MenuPojo();
            paramPojo.setMenuName(menuName);
            MenuPojo pojo = menuDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(resourceId)) {
                result = NumberUtils.toLong(resourceId) == pojo.getResourceId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    @Override
    public List<MenuPojo> listMenuButton(String moduleCode, String menuId, int pageIndex, int pageSize)
        throws ServiceException {

        try {
            List<String> modules = new ArrayList<String>();
            if (StringUtils.isNotEmpty(moduleCode)) {
                modules.add(moduleCode);
            }
            return menuDao.selectMenuButton(StringUtils.isNotEmpty(moduleCode) ? modules : null,
                NumberUtils.toLong(menuId), MenuPojo.TYPE_BUTTON, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 根据resourceId查询菜单<br>
     *
     * @param resourceId
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public MenuPojo queryById(Long resourceId) throws ServiceException {
        try {
            return menuDao.getById(resourceId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param menuPojos
     * @throws ServiceException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public void modifyMenuSeq(List<MenuPojo> menuPojos) throws ServiceException {
        try {
            for (MenuPojo menu : menuPojos) {
                menuDao.updateSeq(menu);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
