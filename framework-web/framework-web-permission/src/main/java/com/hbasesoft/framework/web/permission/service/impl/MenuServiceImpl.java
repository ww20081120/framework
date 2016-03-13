/**
 *
 */
package com.hbasesoft.framework.web.permission.service.impl;

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
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.MenuPojo;
import com.hbasesoft.framework.web.permission.dao.menu.MenuDao;
import com.hbasesoft.framework.web.permission.dao.role.RoleResourceDao;
import com.hbasesoft.framework.web.permission.service.MenuService;
import com.hbasesoft.framework.cache.core.annotation.Cache;
import com.hbasesoft.framework.cache.core.annotation.CacheKey;
import com.hbasesoft.framework.cache.core.annotation.CacheType;
import com.hbasesoft.framework.cache.core.annotation.RmCache;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.AssertException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.dao.ModuleDao;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @since V1.0<br>
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger logger = new Logger(MenuServiceImpl.class);

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
    @Cache(node = PermissionConstant.CACHE_MENU)
    public List<MenuPojo> queryMenu(@CacheKey String moduleCode) throws ServiceException {
        try {
            List<MenuPojo> menuList = menuDao.selectMenu(moduleCode);

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
     * Description:添加菜单 <br>
     *
     * @param menuPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    @RmCache(type = CacheType.NODE, node = PermissionConstant.CACHE_MENU)
    public void addMenu(MenuPojo menuPojo) throws ServiceException {
        try {
            menuPojo.setIsLeaf(PermissionConstant.YES);
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
    @RmCache(type = CacheType.NODE, node = PermissionConstant.CACHE_MENU)
    public void modifyMenu(MenuPojo menuPojo) throws ServiceException {
        try {
            menuDao.updateAttr(menuPojo);
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
    @RmCache(type = CacheType.NODE, node = PermissionConstant.CACHE_MENU)
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
    @RmCache(type = CacheType.NODE, node = PermissionConstant.CACHE_MENU)
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
