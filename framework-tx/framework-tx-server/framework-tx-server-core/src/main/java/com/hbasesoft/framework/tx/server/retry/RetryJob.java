/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.retry;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.apache.commons.collections.CollectionUtils;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.job.core.annotation.Job;
import com.hbasesoft.framework.tx.core.TxClientInfoFactory;
import com.hbasesoft.framework.tx.core.TxConsumer;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.server.PagerList;
import com.hbasesoft.framework.tx.server.TxStorage;

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
@Job(cron = "3 1/5 * * * ?", shardingParam = "0,1,2,3,4,5,6,7,8,9,10")
public class RetryJob implements SimpleJob {

    /**
     * txConsumer
     */
    private static TxConsumer txConsumer;

    /**
     * storage
     */
    private static TxStorage storage;

    /** clientInfoFactory */
    private static TxClientInfoFactory clientInfoFactory;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param shardingContext <br>
     */
    @Override
    public void execute(final ShardingContext shardingContext) {
        // 按每小时内

        TxConsumer txConsumer = getConsumer();
        TxStorage storage = getTxStorage();

        if (txConsumer != null && storage != null) {

            int pageIndex = 1;
            int pageSize = GlobalConstants.DEFAULT_LINES;

            PagerList<ClientInfo> timeoutClientInfos;
            do {
                timeoutClientInfos = storage.queryTimeoutClientInfo(getClientInfoFactory().getClientInfo(),
                    shardingContext.getShardingItem(), pageIndex++, pageSize);
                if (CollectionUtils.isNotEmpty(timeoutClientInfos)) {
                    for (ClientInfo clientInfo : timeoutClientInfos) {
                        if (txConsumer.retry(clientInfo)) {
                            storage.updateClientRetryTimes(clientInfo.getId());
                        }
                    }
                }
            }
            while (timeoutClientInfos != null && timeoutClientInfos.hasNextPage());
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static TxConsumer getConsumer() {
        if (txConsumer == null) {
            ServiceLoader<TxConsumer> serviceLoader = ServiceLoader.load(TxConsumer.class);
            Iterator<TxConsumer> iterator = serviceLoader.iterator();
            if (iterator.hasNext()) {
                txConsumer = iterator.next();
            }
        }
        return txConsumer;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static TxStorage getTxStorage() {
        if (storage == null) {
            storage = ContextHolder.getContext().getBean(TxStorage.class);
        }
        return storage;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private static TxClientInfoFactory getClientInfoFactory() {
        if (clientInfoFactory == null) {
            ServiceLoader<TxClientInfoFactory> loader = ServiceLoader.load(TxClientInfoFactory.class);
            Iterator<TxClientInfoFactory> it = loader.iterator();
            Assert.isTrue(it.hasNext(), ErrorCodeDef.TRANS_CLIENT_INFO_FACTORY_NOT_FOUND);
            clientInfoFactory = it.next();
        }
        return clientInfoFactory;
    }

}
