/**
 *
 */
package com.hbasesoft.framework.web.permission.interceptor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.web.core.bean.UrlResource;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * SercurityInterceptor
 *
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = new Logger(SercurityInterceptor.class);

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
     * @param request <br>
     * @param response <br>
     * @param handler <br>
     * @return <br>
     * @throws Exception <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

        OperatorPojo operator = WebUtil.getCurrentOperator();
        if (null == operator) {
            response.sendRedirect(request.getContextPath() + loginUrl);
            return false;
        }

        if (validatePermission) {
            UrlResource permission = WebUtil.urlMatch(request);
            Set<String> permissionSet = (Set<String>) WebUtil.getAttribute(PermissionConstant.SESSION_PERMISSIONS);
            if (permission != null && permissionSet.contains(permission.getFunctionCode())) {
                return true;
            }

            String url = request.getRequestURI().substring(request.getContextPath().length());
            logger.info("************ Url is no permission. [ url= '{}', method= '{}' ]. ************", url,
                request.getMethod());
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