/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud;

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

    private static ThreadLocal<String> transIdHolder = new ThreadLocal<String>() {
        protected String initialValue() {
            return CommonUtil.getTransactionID();
        };
    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getTraceId() {
        return transIdHolder.get();
    }

    public void setTraceId(String traceId) {
        transIdHolder.set(traceId);
    }

}
