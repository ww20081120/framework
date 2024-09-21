/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

import io.micrometer.tracing.ScopedSpan;
import io.micrometer.tracing.TraceContext;

/**
 * <Description> <br>
 * 
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年8月25日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tracing.skywalking <br>
 */
public class SkyWalkingScopedSpan implements ScopedSpan {

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public boolean isNoop() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public TraceContext context() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param name
     * @return <br>
     */
    @Override
    public ScopedSpan name(final String name) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param key
     * @param value
     * @return <br>
     */
    @Override
    public ScopedSpan tag(final String key, final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param value
     * @return <br>
     */
    @Override
    public ScopedSpan event(final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param throwable
     * @return <br>
     */
    @Override
    public ScopedSpan error(final Throwable throwable) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void end() {
        // TODO Auto-generated method stub

    }

}
