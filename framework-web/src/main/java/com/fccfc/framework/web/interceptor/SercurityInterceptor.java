/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.web.bean.operator.OperatorPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;
import com.fccfc.framework.web.utils.WebUtil;

/**
 * SercurityInterceptor
 * 
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

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
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @param handler <br>
     * @return <br>
     * @throws Exception <br>
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
            if (permission != null && WebUtil.hasPermission(permission.getResourceId())) {
                return true;
            }
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