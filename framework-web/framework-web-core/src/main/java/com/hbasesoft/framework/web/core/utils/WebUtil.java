/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.bean.OperatorPojo;

/**
 * <Description> <br>
 *
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月5日 <br>
 * @see com.hbasesoft.framework.web <br>
 */
public final class WebUtil {

    /**
     * 默认构造函数
     */
    private WebUtil() {
    }

    /**
     * Description: <br>
     *
     * @param attrCode <br>
     * @param attrValue <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public static void setAttribute(String attrCode, Object attrValue) {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().setAttribute(attrCode, attrValue);
    }

    /**
     * Description: <br>
     *
     * @param attrCode <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public static Object getAttribute(String attrCode) {
        Subject subject = SecurityUtils.getSubject();
        return subject.getSession().getAttribute(attrCode);
    }

    /**
     * 删除属性值 Description: <br>
     *
     * @param code <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    public static void removeAttribute(String code) {
        Subject subject = SecurityUtils.getSubject();
        subject.getSession().removeAttribute(code);
    }

    /***
     * Description: <br>
     *
     * @param request <br>
     * @return String <br>
     * @author bai.wenlong<br>
     * @taskId <br>
     */
    public static String getRemoteIP() {
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            return request.getRemoteAddr();
        }
        return GlobalConstants.BLANK;
    }

    /**
     * Description: <br>
     *
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public static Integer getCurrentOperatorId() {
        OperatorPojo pojo = getCurrentOperator();
        return pojo == null ? null : pojo.getOperatorId();
    }

    /**
     * Description: <br>
     *
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public static OperatorPojo getCurrentOperator() {
        OperatorPojo operator = (OperatorPojo) getAttribute(WebConstant.SESSION_OPERATOR);
        if (operator != null) {
            operator.setLastIp(getRemoteIP());
        }
        return operator;
    }
}
