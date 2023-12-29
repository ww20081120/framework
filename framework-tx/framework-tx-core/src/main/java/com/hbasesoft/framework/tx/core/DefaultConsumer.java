/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import com.hbasesoft.framework.common.annotation.NoTracerLog;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 21, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
@NoTracerLog
public class DefaultConsumer implements TxConsumer {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public boolean retry(final ClientInfo clientInfo) {
        LoggerUtil.debug("before execute retry {0}|{1}", clientInfo.getId(), clientInfo.getMark());
        TxProducer sender = TxInvokerProxy.getSender();
        try {
            if (sender.containClient(clientInfo.getId())) {
                TxManager.execute(clientInfo.getId(), clientInfo.getMark(), null,
                    ArgsSerializationUtil.unserialArgs(clientInfo.getArgs()));
                sender.removeClient(clientInfo.getId());
                LoggerUtil.debug("success execute retry {0}|{1}", clientInfo.getId(), clientInfo.getMark());
                return true;
            }
        }
        catch (Exception e) {
            LoggerUtil.error(e, "error execute retry {0}|{1}", clientInfo.getId(), clientInfo.getMark());
        }
        return false;
    }

}
