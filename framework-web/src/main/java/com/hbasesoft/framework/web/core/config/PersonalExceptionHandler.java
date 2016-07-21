/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.AssertException;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月24日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.config <br>
 */
@ControllerAdvice
public class PersonalExceptionHandler {
    /** 失败跳转页面 */
    private static final String ERROR_PAGE = "common/page500";

    private static Logger logger = new Logger(PersonalExceptionHandler.class);

    @ExceptionHandler(AssertException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ModelAndView handleException(AssertException exception) {
        logger.info(exception.getMessage());

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("message", "参数不合法");
        param.put("code", exception.getCode());
        return new ModelAndView(ERROR_PAGE, param);
    }

    @ExceptionHandler(FrameworkException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ModelAndView handleException(FrameworkException exception) {
        logger.error(exception.getMessage(), exception);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("message", exception.getMessage());
        param.put("code", exception.getCode());
        return new ModelAndView(ERROR_PAGE, param);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ModelAndView handleException(Exception exception) {
        logger.error(exception.getMessage(), exception);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("message","");
        param.put("code","系统异常");
        return new ModelAndView(ERROR_PAGE, param);
    }

}
