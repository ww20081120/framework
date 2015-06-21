/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.api.bean.web.MenuPojo;
import com.fccfc.framework.core.cache.CacheConstant;
import com.fccfc.framework.core.cache.CacheHelper;
import com.fccfc.framework.core.utils.CommonUtil;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.service.MappingService;
import com.fccfc.framework.web.service.MenuService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.interceptor <br>
 */
public class MenuInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private MenuService menuService;

    @Resource
    private MappingService mappingService;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            ModelMap map = modelAndView.getModelMap();
            map.put(WebConstant.MENU_VARIABLE_TREE,
                CacheHelper.get(CacheConstant.SYSTEM_MENU_DIR, WebConstant.MENU_VARIABLE_TREE));

            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                List<String> resourceCache = mappingService.selectAllUrlResource();
                Class<?> handlerClazz = handlerMethod.getBean().getClass();
                String clazzName = handlerClazz.getName();
                if (!resourceCache.contains(clazzName)) {
                    Class<?> superClazz = handlerClazz.getSuperclass();
                    if (superClazz != null) {
                        clazzName = superClazz.getName();
                    }
                }

                if (CommonUtil.isNotEmpty(clazzName)) {
                    List<MenuPojo> breadLineList = menuService.selectBreadLine(clazzName, handlerMethod.getMethod()
                        .getName());
                    map.put(WebConstant.BREAD_LINE_LIST, breadLineList);
                }
            }
        }
    }
}
