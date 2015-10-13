/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.controller;

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

    /** 分页最大数 */
    private static final int MAX_PAGE_SIZE = 100;

    /** 分页最小数 */
    private static final int MIN_PAGE_SIZE = 5;

    /** 默认页大小 */
    private static final int DEFAULT_PAGE_SIZE = 10;

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
     * @throws AssertException <br>
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
     */
    protected String getParameter(String param) {
        String value = getRequest().getParameter(param);
        return StringUtils.trim(value);
    }

    /**
     * Description: getLongParameter<br>
     * 
     * @author wang.wei297<br>
     * @taskId <br>
     * @param param param <br>
     * @param errMsg errMsg <br>
     * @return <br>
     * @throws AssertException <br>
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
     */
    protected Long getLongParameter(String param) {
        String value = getParameter(param);
        return CommonUtil.isEmpty(value) ? null : Long.valueOf(value);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param param <br>
     * @param errMsg <br>
     * @return <br>
     * @throws AssertException <br>
     */
    protected Integer getIntegerParameter(String param, String errMsg) throws AssertException {
        String value = getParameter(param, errMsg);
        return CommonUtil.isEmpty(value) ? null : Integer.valueOf(value);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param param <br>
     * @return <br>
     */
    protected Integer getIntegerParameter(String param) {
        String value = getParameter(param);
        return CommonUtil.isEmpty(value) ? null : Integer.valueOf(value);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    protected int getPageIndex() {
        Integer pageIndex = getIntegerParameter("index");
        return (pageIndex == null || pageIndex < 1) ? 1 : pageIndex;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    protected int getPageSize() {
        Integer pageSize = getIntegerParameter("size");
        return (pageSize == null || pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) ? DEFAULT_PAGE_SIZE
            : pageSize;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param message <br>
     * @param redirectUrl <br>
     * @param param <br>
     * @return <br>
     */
    protected ModelAndView success(String message, String redirectUrl, Map<String, String> param) {
        ModelMap map = new ModelMap();
        map.put("success", true);
        map.put("message", message);
        map.put("url", redirectUrl);
        map.put("paramMap", param);
        return new ModelAndView(successPage, map);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param errMessage <br>
     * @param redirectUrl <br>
     * @param param <br>
     * @return <br>
     */
    protected ModelAndView fail(String errMessage, String redirectUrl, Map<String, String> param) {
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
