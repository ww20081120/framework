/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.utils;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.date.DateUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.config.core.DictionaryHelper;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月18日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.utils <br>
 */
public class VelocityTool {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     */
    public String getPagerUrl(HttpServletRequest request, int pageIndex, int pageSize) {
        StringBuilder sb = new StringBuilder(request.getRequestURL());
        sb.append("?");

        String queryStr = request.getQueryString();
        if (CommonUtil.isNotEmpty(queryStr)) {
            sb.append(queryStr);
        }

        int from = sb.indexOf("index=");
        if (from != -1) {
            sb.replace(from + 6, sb.indexOf("&", from + 6), String.valueOf(pageIndex));
        }
        else {
            sb.append("&index=").append(pageIndex);
        }

        from = sb.indexOf("size=");

        if (from != -1) {
            int to = sb.indexOf("&", from + 6);
            to = to == -1 ? sb.length() : to;
            sb.replace(from + 5, to, String.valueOf(pageSize));
        }
        else {
            sb.append("&size=").append(pageSize);
        }
        from = sb.indexOf("toLast=");
        if (from != -1) {
            sb.replace(from - 1, sb.indexOf("&", from), "");
        }

        from = sb.indexOf("?&");
        if (from != -1) {
            sb.deleteCharAt(from + 1);
        }
        return sb.toString();
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param date <br>
     * @param formatStr <br>
     * @return <br>
     */
    public String format(Date date, String formatStr) {
        return DateUtil.date2String(date, formatStr);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode <br>
     * @param data <br>
     * @return <br>
     */
    public String getDictionary(String dictCode, String data) {
        return DictionaryHelper.getString(dictCode, data);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param code <br>
     * @return <br>
     */
    public String getConfig(String code) {
        return Configuration.getString(code);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param resourceCode <br>
     * @return <br>
     */
    public boolean hasPermission(Object resourceCode) {
        return WebUtil.hasPermission(resourceCode);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param key <br>
     * @param request <br>
     * @return <br>
     */
    public Object getAttribute(String key, HttpServletRequest request) {
        Object value = request.getAttribute(key);
        if (value == null) {
            value = request.getSession().getAttribute(key);
            if (value == null) {
                value = request.getSession().getServletContext().getAttribute(key);
            }
        }
        return value;
    }
}
