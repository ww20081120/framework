/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.utils;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.web.permission.bean.MenuPojo;
import com.hbasesoft.framework.web.permission.service.MenuService;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月18日 <br>
 * @since V1.0<br>
 */
public class VelocityTool {

    /**
     * Description: <br>
     *
     * @param request <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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
     * Description: <br>
     *
     * @param date <br>
     * @param formatStr <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public String format(Date date, String formatStr) {
        return DateUtil.date2String(date, formatStr);
    }

    /**
     * Description: <br>
     *
     * @param dictCode <br>
     * @param data <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public String getDictionary(String dictCode, String data) {
        return ConfigHelper.getDictName(dictCode, data);
    }

    /**
     * Description: <br>
     *
     * @param code <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    public String getConfig(String code) {
        return ConfigHelper.getString(code);
    }

    public List<MenuPojo> getMenus() throws ServiceException {
        MenuService menuService = ContextHolder.getContext().getBean(MenuService.class);
        return menuService.queryMenu(ConfigHelper.getModuleCode());
    }

    /**
     * Description: <br>
     *
     * @param key <br>
     * @param request <br>
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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

    /**
     * Description: 资源路径<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param request
     * @return <br>
     */
    public String resourcePath(HttpServletRequest request) {
        return ConfigHelper.getString("SYSTEM.RESOURCE_BASE_URL",
            request.getContextPath() + "/common/resource/download?mediaId=");
    }

    /**
     * Description: 將對象轉換成json<br>
     * 
     * @author chaizhi<br>
     * @taskId <br>
     * @param obj <br>
     * @return <br>
     */
    public String toJson(Object obj) {
        return JSON.toJSONString(obj, new SimplePropertyPreFilter(), SerializerFeature.WriteMapNullValue);
    }
}
