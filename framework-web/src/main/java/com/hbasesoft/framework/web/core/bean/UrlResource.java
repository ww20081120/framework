/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.core.bean;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年1月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.core.bean <br>
 */
public class UrlResource extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -8318532945128724711L;

    private String functionCode;

    private String url;

    private RequestMethod[] methods;

    private String[] events;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return url <br>
     */
    public String getUrl() {
        return url;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url <br>
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return methods <br>
     */
    public RequestMethod[] getMethods() {
        return methods;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param methods <br>
     */
    public void setMethods(RequestMethod[] methods) {
        this.methods = methods;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return events <br>
     */
    public String[] getEvents() {
        return events;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param events <br>
     */
    public void setEvents(String[] events) {
        this.events = events;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return functionCode <br>
     */
    public String getFunctionCode() {
        return functionCode;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param functionCode <br>
     */
    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public boolean match(String method) {
        return StringUtils.join(methods, GlobalConstants.SPLITOR).indexOf(method.toUpperCase()) != -1;
    }
}
