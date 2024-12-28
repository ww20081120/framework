/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tracing.skywalking;

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
public class SkyWalkingTraceContext implements TraceContext {

    /**
     * Description: <br> 
     *  
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String traceId() {
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
    public String parentId() {
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
    public String spanId() {
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
    public Boolean sampled() {
        // TODO Auto-generated method stub
        return null;
    }

}
