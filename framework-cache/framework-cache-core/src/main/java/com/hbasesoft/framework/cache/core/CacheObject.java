/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.cache.core;

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

    private long expreTime;

    private Object target;

    /** 
     *  
     */
    public CacheObject() {
        super();
    }

    /** 
     *  
     */
    public CacheObject(Object target) {
        super();
        this.target = target;
    }

    /**
     * @param expreTime
     * @param target
     */
    public CacheObject(int seconds, Object target) {
        super();
        if (seconds > 0) {
            this.expreTime = System.currentTimeMillis() + (seconds * 1000L);
        }
        this.target = target;
    }

    public long getExpreTime() {
        return expreTime;
    }

    public void setExpreTime(long seconds) {
        this.expreTime = System.currentTimeMillis() + (seconds * 1000L);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTarget() {
        if (expreTime > 0 && System.currentTimeMillis() - expreTime > 0) {
            return null;
        }
        return (T) target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

}
