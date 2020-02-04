/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr != null && requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();

            String traceId = request.getHeader(TraceIdFilter.TRACE_ID);
            if (StringUtils.isEmpty(traceId)) {
                traceId = TxManager.getTraceId();
            }
            template.header(TraceIdFilter.TRACE_ID, traceId);
        }
    }

}
