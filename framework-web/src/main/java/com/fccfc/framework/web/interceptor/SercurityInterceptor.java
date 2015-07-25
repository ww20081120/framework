/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.bean.operator.OperatorPojo;
import com.fccfc.framework.web.bean.resource.UrlResourcePojo;
import com.fccfc.framework.web.utils.WebUtil;

/**
 * SercurityInterceptor
 * 
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

    private String loginUrl = "/login";

    private String noPermissionUrl = "/error/nopermission";

    private boolean validatePermission;

    private AntPathMatcher matcher = new AntPathMatcher();

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
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        OperatorPojo operator = WebUtil.getCurrentOperator();
        if (null == operator) {
            response.sendRedirect(request.getContextPath() + loginUrl);
            return false;
        }

        if (validatePermission) {
            List<UrlResourcePojo> permissionList = (List<UrlResourcePojo>) WebUtil
                .getAttribute(WebConstant.SESSION_PERMISSIONS);
            String url = request.getRequestURI();
            if (CommonUtil.isNotEmpty(permissionList)) {
                for (UrlResourcePojo resource : permissionList) {
                    if (matcher.match(resource.getUrl(), url)) {
                        return true;
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + noPermissionUrl);
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