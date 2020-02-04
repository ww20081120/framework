/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud.interceptor;

import com.hbasesoft.framework.tx.core.TxManager;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.sgp.uum.user.config <br>
 */
public class FeginTraceIdInterceptor implements RequestInterceptor {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param template <br>
     */
    @Override
    public void apply(RequestTemplate template) {
        String traceId = TxManager.getTraceId();
        System.out.println(traceId);
        template.header(TraceIdFilter.TRACE_ID, traceId);
    }

}
