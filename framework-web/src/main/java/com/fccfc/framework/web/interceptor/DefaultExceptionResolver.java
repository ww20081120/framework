/**
 * 
 */
package com.fccfc.framework.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fccfc.framework.api.FrameworkException;
import com.fccfc.framework.core.ErrorCodeDef;
import com.fccfc.framework.core.utils.CommonUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月3日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.interceptor <br>
 */
public class DefaultExceptionResolver implements HandlerExceptionResolver {

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex) {
        if (ex != null) {
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                ResponseBody responseBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
                if (responseBody != null || request.getRequestURI().indexOf("/api/") != -1) {
                    MappingJackson2JsonView view = new MappingJackson2JsonView();
                    view.addStaticAttribute("errcode",
                        ex instanceof FrameworkException ? ((FrameworkException) ex).getCode()
                            : ErrorCodeDef.SYSTEM_ERROR_10001);
                    if (CommonUtil.isNotEmpty(ex.getMessage())) {
                        view.addStaticAttribute("errmsg", ex.getMessage());
                    }
                    return new ModelAndView(view);
                }
            }
        }

        ModelAndView result = new ModelAndView("error/page500");
        result.addObject("exception", ex);
        return result;
    }

}
