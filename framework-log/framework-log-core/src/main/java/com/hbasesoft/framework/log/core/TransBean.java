/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.core;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.core <br>
 */
public class TransBean implements Serializable {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -7472460952684365657L;

    /** stackId */
    private String stackId;

    /** parentStackId */
    private String parentStackId;

    /** params */
    private String params;

    /** method */
    private String method;

    /** begin Time */
    private Long beginTime;

    /** endTime */
    private Long endTime;

    /** consumeTime */
    private Long consumeTime;

    /** return Value */
    private String returnValue;

    /** reusult */
    private String result;

    /** exception */
    private String exception;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getStackId() {
        return stackId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param stackId <br>
     */
    public void setStackId(final String stackId) {
        this.stackId = stackId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getParentStackId() {
        return parentStackId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param parentStackId <br>
     */
    public void setParentStackId(final String parentStackId) {
        this.parentStackId = parentStackId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Long getBeginTime() {
        return beginTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime <br>
     */
    public void setBeginTime(final Long beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param endTime <br>
     */
    public void setEndTime(final Long endTime) {
        this.endTime = endTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Long getConsumeTime() {
        return consumeTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param consumeTime <br>
     */
    public void setConsumeTime(final Long consumeTime) {
        this.consumeTime = consumeTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getReturnValue() {
        return returnValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param returnValue <br>
     */
    public void setReturnValue(final String returnValue) {
        this.returnValue = returnValue;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getResult() {
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param result <br>
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getException() {
        return exception;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param exception <br>
     */
    public void setException(final String exception) {
        this.exception = exception;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getMethod() {
        return method;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param method <br>
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getParams() {
        return params;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param params <br>
     */
    public void setParams(final String params) {
        this.params = params;
    }

    /**
     * toString
     * 
     * @see java.lang.Object#toString()
     * @return <br>
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
