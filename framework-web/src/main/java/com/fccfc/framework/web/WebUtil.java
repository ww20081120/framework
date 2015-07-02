/**************************************************************************************** 
 Copyright © 2003-2012 ZTEsoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.web.bean.operator.OperatorPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟 <br>
 * @version 1.0 <br>
 * @CreateDate 2014年12月5日 <br>
 * @see com.fccfc.framework.web <br>
 */
public final class WebUtil {

    /**
     * 测试账号MAP
     */
    public static Map<String, String> TEST_OPER_MAP = null;

    /**
     * 默认构造函数
     */
    private WebUtil() {
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public static Integer getCurrentOperatorId() {
        OperatorPojo pojo = getCurrentOperator();
        return pojo == null ? null : pojo.getOperatorId();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    public static OperatorPojo getCurrentOperator() {
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            String ip = request.getRemoteAddr();
            HttpSession session = request.getSession();
            OperatorPojo operator = (OperatorPojo) session.getAttribute(WebConstant.SESSION_OPERATOR);
            if (operator != null) {
                operator.setLastIp(ip);
            }
            return operator;
        }
        return null;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrCode <br>
     * @param attrValue <br>
     */
    public static void setAttribute(String attrCode, Object attrValue) {
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            request.getSession().setAttribute(attrCode, attrValue);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrCode <br>
     * @return <br>
     */
    public static Object getAttribute(String attrCode) {
        Object obj = null;
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            obj = request.getAttribute(attrCode);
            if (obj == null) {
                obj = request.getSession().getAttribute(attrCode);
            }
        }
        return obj;
    }

    /**
     * 获取测试账号
     * 
     * @return <br>
     */
    public static Map<String, String> getTestOperMap() {
        if (null != TEST_OPER_MAP) {
            return TEST_OPER_MAP;
        }

        TEST_OPER_MAP = new HashMap<String, String>();
        String operators = Configuration.getString("OPERATOR.TEST_OPERATOR");
        if (CommonUtil.isNotEmpty(operators)) {
            String[] operatorArr = operators.split(GlobalConstants.SPLITOR);
            for (String value : operatorArr) {
                TEST_OPER_MAP.put(value.split(GlobalConstants.EQUAL_SPLITER)[0],
                    value.split(GlobalConstants.EQUAL_SPLITER)[1]);
            }
        }
        return TEST_OPER_MAP;
    }
}
