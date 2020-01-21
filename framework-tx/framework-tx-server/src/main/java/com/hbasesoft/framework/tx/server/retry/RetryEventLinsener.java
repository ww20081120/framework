/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.retry;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventLinsener;
import com.hbasesoft.framework.tx.server.TxStorage;
import com.hbasesoft.framework.tx.server.bean.TxClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 21, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.retry <br>
 */
@Component
public class RetryEventLinsener implements EventLinsener {

    public static final String TX_RETRY_SEND_MESSAGE = "TX_RETRY_SEND_MESSAGE";

    private Map<String, RetryService> retryServiceMap;

    @Resource
    private TxStorage txStorage;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String[] events() {
        return new String[] {
            TX_RETRY_SEND_MESSAGE
        };
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param event
     * @param data <br>
     */
    @Override
    public void onEmmit(String event, EventData data) {}

    private synchronized RetryService getRetryService(String type) {
        if (retryServiceMap == null) {
            retryServiceMap = new HashMap<String, RetryService>();
            ServiceLoader<RetryService> serviceLoader = ServiceLoader.load(RetryService.class);
            for (RetryService retryService : serviceLoader) {
                retryServiceMap.put(retryService.getType(), retryService);
            }
        }
        return retryServiceMap.get(type);
    }
}
