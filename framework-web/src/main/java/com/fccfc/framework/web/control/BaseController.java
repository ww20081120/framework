/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.control;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.web.WebConstant;

/**
 * <Description> <br>
 * 
 * @author wang.wei297<br>
 * @version 1.0<br>
 * @taskId 660560<br>
 * @CreateDate 2015年4月23日 <br>
 * @since V7.3<br>
 * @see com.ztesoft.zsmart.bss.seecom.webcare.control <br>
 */
public abstract class BaseController {

    /** 成功跳转页面 */
    private static String successPage = "error/page500";

    /** 失败跳转页面 */
    private static String errorPage = "error/page500";

    /**
     * Description: 获取参数<br>
     * 
     * @author wang.wei297<br>
     * @taskId 660560<br>
     * @param param 参数名
     * @param errMsg 当该值不为空时，校验参数值是否为空，如果为空的话抛出异常
     * @return 参数值<br>
     * @throws AssertException
     */
    protected String getParameter(String param, String errMsg) throws AssertException {
        String value = getRequest().getParameter(param);
        if (errMsg != null) {
            Assert.notEmpty(value, errMsg);
        }
        return StringUtils.trim(value);
    }

    /**
     * Description: 获取参数<br>
     * 
     * @author wang.wei297<br>
     * @taskId 660560 <br>
     * @param param 参数名
     * @return <br>
     * @throws AssertException
     */
    protected String getParameter(String param) throws AssertException {
        return getParameter(param, null);
    }

    /**
     * Description: getLongParameter<br>
     * 
     * @author wang.wei297<br>
     * @taskId <br>
     * @param param param
     * @param errMsg errMsg
     * @return <br>
     * @throws AssertException
     */
    protected Long getLongParameter(String param, String errMsg) throws AssertException {
        String value = getParameter(param, errMsg);
        return CommonUtil.isEmpty(value) ? null : Long.valueOf(value);
    }

    /**
     * Description: getLongParameter<br>
     * 
     * @author wang.wei297<br>
     * @taskId <br>
     * @param param param
     * @return <br>
     * @throws AssertException
     */
    protected Long getLongParameter(String param) throws AssertException {
        return getLongParameter(param, null);
    }

    protected ModelAndView success(String message, String redirectUrl, Map<String, String> param) {
        ModelMap map = new ModelMap();
        map.put("success", true);
        map.put("message", message);
        map.put("url", redirectUrl);
        map.put("paramMap", param);
        return new ModelAndView(successPage, map);
    }

    protected ModelAndView fail(String errMessage, List<String> errReminds, String redirectUrl,
        Map<String, String> param) {
        ModelMap map = new ModelMap();
        map.put("errMsg", errMessage);
        map.put("url", redirectUrl);

        if (param != null) {
            map.put(WebConstant.PAGE_NOT_REDIRECT, param.remove(WebConstant.PAGE_NOT_REDIRECT));
        }
        map.put("paramMap", param);
        return new ModelAndView(errorPage, map);
    }

    /**
     * Description: 获取request<br>
     * 
     * @author wang.wei297<br>
     * @taskId 660560<br>
     * @return <br>
     */
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttr).getRequest();
    }
}
