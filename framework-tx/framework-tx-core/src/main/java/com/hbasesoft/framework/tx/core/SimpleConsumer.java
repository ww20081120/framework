/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.core.util.ArgsSerializationUtil;

import lombok.RequiredArgsConstructor;

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
@RequiredArgsConstructor
public class SimpleConsumer implements TxConsumer {

    private final TxProducer txProducer;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public void retry(ClientInfo clientInfo) {
        try {
            TxManager.execute(clientInfo.getMark(), clientInfo.getContext(),
                ArgsSerializationUtil.unserialArgs(clientInfo.getArgs()));
            txProducer.removeClient(clientInfo.getId());
        }
        catch (Exception e) {
            LoggerUtil.error(e);
        }
    }

}
