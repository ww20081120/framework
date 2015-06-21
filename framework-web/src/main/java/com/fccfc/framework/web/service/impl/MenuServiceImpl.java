/**
 * 
 */
package com.fccfc.framework.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.api.FrameworkException;
import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.web.MenuPojo;
import com.fccfc.framework.core.ErrorCodeDef;
import com.fccfc.framework.core.cache.CacheConstant;
import com.fccfc.framework.core.cache.CacheException;
import com.fccfc.framework.core.cache.CacheHelper;
import com.fccfc.framework.core.config.Configuration;
import com.fccfc.framework.core.db.DaoException;
import com.fccfc.framework.core.utils.CommonUtil;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.dao.MenuDao;
import com.fccfc.framework.web.service.MenuService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuDao menuDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.web.service.MenuService#cacheAllMenu()
     */
    @Override
    public void cacheAllMenu() throws ServiceException {
        try {
            final Map<Integer, MenuPojo> menuMap = new HashMap<Integer, MenuPojo>();

            List<MenuPojo> menuList = menuDao.selectAllMenu(Configuration.getModuleCode());
            CacheHelper.set(CacheConstant.SYSTEM_MENU_DIR, WebConstant.MENU_VARIABLE, menuList);
            if (CommonUtil.isNotEmpty(menuList)) {
                List<MenuPojo> topMenuList = new ArrayList<MenuPojo>();
                for (MenuPojo menu : menuList) {
                    if (menu.getParentId() == null) {
                        topMenuList.add(menu);
                    }

                    if (MenuPojo.LEAF.equals(menu.getIsLeaf()) && menu.getParentId() != null) {
                        MenuPojo parentMenu = menuMap.get(menu.getParentId());
                        if (parentMenu == null) {
                            throw new ServiceException(ErrorCodeDef.MENU_DATA_ERROR_10025, "Menu 数据有问题，请检查。");
                        }
                        parentMenu.getChildrenMenu().add(menu);
                    }
                    else {
                        menuMap.put(menu.getMenuId(), menu);
                        List<MenuPojo> childrenMenu = menu.getChildrenMenu();
                        if (childrenMenu == null) {
                            menu.setChildrenMenu(new ArrayList<MenuPojo>());
                        }
                    }
                }
                CacheHelper.set(CacheConstant.SYSTEM_MENU_DIR, WebConstant.MENU_VARIABLE_TREE, topMenuList);
            }
        }
        catch (DaoException e) {
            throw new ServiceException("缓存菜单失败", e);
        }
        catch (CacheException e) {
            throw new ServiceException("缓存菜单失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.web.service.MenuService#selectBreadLine(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<MenuPojo> selectBreadLine(String className, String method) throws ServiceException {

        List<MenuPojo> list = new ArrayList<MenuPojo>();

        try {
            Integer menuId = menuDao.getMenuByClassAndMethod(Configuration.getModuleCode(), className, method);
            if (menuId != null) {
                List<MenuPojo> menuList = (List<MenuPojo>) CacheHelper.get(CacheConstant.SYSTEM_MENU_DIR,
                    WebConstant.MENU_VARIABLE);
                if (CommonUtil.isNotEmpty(menuList)) {
                    selectBreadLine(menuId, list, menuList);
                    Collections.reverse(list);
                }
            }
        }
        catch (FrameworkException e) {
            throw new ServiceException(e);
        }
        return list;
    }

    private void selectBreadLine(int menuId, List<MenuPojo> breadLineList, List<MenuPojo> allMenuList) {
        for (MenuPojo menu : allMenuList) {
            if (menu.getMenuId().equals(menuId)) {
                breadLineList.add(menu);
                if (menu.getParentId() != null) {
                    selectBreadLine(menu.getParentId(), breadLineList, allMenuList);
                }
                break;
            }
        }
    }

}
