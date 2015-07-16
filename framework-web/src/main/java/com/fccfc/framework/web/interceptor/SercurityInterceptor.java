/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.web.WebUtil;
import com.fccfc.framework.web.bean.operator.OperatorPojo;

/**
 * SercurityInterceptor
 * 
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

    private String loginUrl = "/login";

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
        return true;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

}