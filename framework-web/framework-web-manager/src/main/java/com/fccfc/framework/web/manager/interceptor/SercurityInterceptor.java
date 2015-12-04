/**
 *
 */
package com.fccfc.framework.web.manager.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fccfc.framework.web.manager.bean.permission.MenuPojo;
import com.fccfc.framework.web.manager.bean.permission.OperatorPojo;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;
import com.fccfc.framework.web.manager.utils.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * SercurityInterceptor
 *
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SercurityInterceptor.class);

    /**
     * loginUrl
     */
    private String loginUrl = "/login";

    /**
     * noPermissionUrl
     */
    private String noPermissionUrl = "/error/nopermission";

    /**
     * validatePermission
     */
    private boolean validatePermission;

    /**
     * Description: <br>
     *
     * @param request  <br>
     * @param response <br>
     * @param handler  <br>
     * @return <br>
     * @throws Exception <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        OperatorPojo operator = WebUtil.getCurrentOperator();
        if (null == operator) {
            response.sendRedirect(request.getContextPath() + loginUrl);
            return false;
        }

        if (validatePermission) {
            UrlResourcePojo permission = WebUtil.urlMatch(request);
            MenuPojo menuPojo = null;
            if (null != permission && null != (menuPojo = WebUtil.getMenu(permission.getFunctionId()))
                    && WebUtil.hasPermission(menuPojo.getResourceId())) {
                return true;
            }
            String url = request.getRequestURI().substring(request.getContextPath().length());
            logger.info("************ Url is no permission. [ url= '{}', method= '{}' ]. ************", url, request.getMethod());
            request.getRequestDispatcher(noPermissionUrl).forward(request, response);
            return false;
        }
        return true;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public void setValidatePermission(boolean validatePermission) {
        this.validatePermission = validatePermission;
    }

    public void setNoPermissionUrl(String noPermissionUrl) {
        this.noPermissionUrl = noPermissionUrl;
    }

}