/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.web.WebUtil;
import com.fccfc.framework.web.bean.operator.OperatorPojo;

/**
 * SercurityInterceptor
 * 
 * @author Administrator
 */
public class SercurityInterceptor extends HandlerInterceptorAdapter {

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
            String url = Configuration.getString("WEB_LOGIN_URL");
            if (CommonUtil.isEmpty(url)) {
                url = "/login";
            }
            response.sendRedirect(request.getContextPath() + url);
            return false;
        }
        return true;
    }

}