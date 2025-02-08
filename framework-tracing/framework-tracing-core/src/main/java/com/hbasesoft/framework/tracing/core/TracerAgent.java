/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.core;

import java.io.Closeable;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年2月8日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.core <br>
 */
public interface TracerAgent {

    /**
     * Description: 获取TraceId <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getTraceId();

    /**
     * Description: 前置拦截 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param args
     * @return <br>
     */
    Closeable before(long beginTime, String methodName, Object[] args);

    /**
     * Description: 正常返回 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param returnValue <br>
     */
    void afterReturning(long beginTime, String methodName, Object returnValue);

    /**
     * Description: 异常返回<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param beginTime
     * @param methodName
     * @param e <br>
     */
    void afterThrowing(long beginTime, String methodName, Throwable e);
}
