/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud;

import org.apache.commons.lang3.StringUtils;

import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.tx.core.TransIdGeneratorFactory;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.client.producer.springcloud <br>
 */
public class WebTransIdGeneratorFactory implements TransIdGeneratorFactory {

    /** trans id holder */
    private static ThreadLocal<String> transIdHolder = new InheritableThreadLocal<String>();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getTraceId() {
        String traceId = transIdHolder.get();
        if (StringUtils.isEmpty(traceId)) {
            traceId = CommonUtil.getTransactionID();
            setTraceId(traceId);
        }
        return traceId;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param traceId <br>
     */
    public void setTraceId(final String traceId) {
        transIdHolder.set(traceId);
    }

}
