/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> 可过期的bean <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年9月26日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.cache.core <br>
 */
public class CacheObject {

    /** expreTime */
    private long expreTime;

    /** target */
    private Object target;

    /** 
     *  
     */
    public CacheObject() {
        super();
    }

    /**
     * @param t
     */
    public CacheObject(final Object t) {
        super();
        this.target = t;
    }

    /**
     * @param seconds
     * @param t target
     */
    public CacheObject(final int seconds, final Object t) {
        super();
        if (seconds > 0) {
            this.expreTime = System.currentTimeMillis() + (seconds * GlobalConstants.SECONDS);
        }
        this.target = t;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public long getExpreTime() {
        return expreTime;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param seconds <br>
     */
    public void setExpreTime(final long seconds) {
        this.expreTime = System.currentTimeMillis() + (seconds * GlobalConstants.SECONDS);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T> T
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    public <T> T getTarget() {
        if (expreTime > 0 && System.currentTimeMillis() - expreTime > 0) {
            return null;
        }
        return (T) target;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param t <br>
     */
    public void setTarget(final Object t) {
        this.target = t;
    }

}
