/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.BaggageInScope;
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
public class SkyWalkingBaggage implements Baggage, BaggageInScope {

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String name() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String get() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param traceContext
     * @return <br>
     */
    @Override
    public String get(final TraceContext traceContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param value
     * @return
     * @deprecated <br>
     */
    @Override
    public Baggage set(final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param traceContext
     * @param value
     * @return
     * @deprecated <br>
     */
    @Override
    public Baggage set(final TraceContext traceContext, final String value) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public BaggageInScope makeCurrent() {
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
    public void close() {
        // TODO Auto-generated method stub

    }

}
