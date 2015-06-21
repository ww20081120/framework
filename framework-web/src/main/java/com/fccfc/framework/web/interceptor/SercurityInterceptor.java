/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.api.bean.operator.OperatorPojo;
import com.fccfc.framework.web.WebUtil;

/**
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        OperatorPojo operator = WebUtil.getCurrentOperator();
        if (null == operator) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

}