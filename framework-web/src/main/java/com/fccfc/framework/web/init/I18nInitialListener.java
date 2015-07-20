/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.init;

import java.util.Locale;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.config.core.Configuration;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月20日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.init <br>
 */
public class I18nInitialListener implements HttpSessionListener {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param se <br>
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        String[] defLangs = StringUtils.split(
            Configuration.getString("DEFAULT_LANGUAGE", GlobalConstants.DEFAULT_LANGUAGE), GlobalConstants.UNDERLINE);
        event.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
            new Locale(defLangs[0], defLangs[1]));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param se <br>
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

    }

}
