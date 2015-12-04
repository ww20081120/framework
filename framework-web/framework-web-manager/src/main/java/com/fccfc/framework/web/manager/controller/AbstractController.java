/****************************************************************************************
 * Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.fccfc.framework.web.core.controller.BaseController;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年8月23日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.controller <br>
 */
public abstract class AbstractController extends BaseController {

    /** 成功跳转页面 */
    private static final String SUCCESS_PAGE = "common/updatePage";

    /** 失败跳转页面 */
    private static final String ERROR_PAGE = "common/page500";

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param message message
     * @return ModelAndView<br>
     */
    protected ModelAndView success(String message) {
        return success(message, null);
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param message message
     * @param url url
     * @return ModelAndView<br>
     */
    protected ModelAndView success(String message, String url) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("url", url);
        map.put("supportEvent", getParameter("e"));
        return new ModelAndView(SUCCESS_PAGE, map);
    }

    /**
     * Description: <br>
     *
     * @author 王伟 <br>
     * @taskId <br>
     * @param message <br>
     * @return <br>
     */
    protected ModelAndView fail(String message) {
        return fail(message, null);
    }

    /**
     * Description: <br>
     *
     * @author 王伟 <br>
     * @taskId <br>
     * @param message <br>
     * @param url <br>
     * @return <br>
     */
    protected ModelAndView fail(String message, String url) {
        ModelMap map = new ModelMap();
        map.put("message", message);
        map.put("url", url);
        return new ModelAndView(ERROR_PAGE, map);
    }

}