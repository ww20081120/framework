/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server;

import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.tx.core.TxProducer;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 20, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server <br>
 */
public class TxProducerImpl implements TxProducer {

    /** txStorage */
    private static TxStorage txStorage;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public boolean registClient(final ClientInfo clientInfo) {
        TxStorage txstorage = getTxStorage();
        if (!txstorage.containsClientInfo(clientInfo.getId())) {
            txstorage.saveClientInfo(clientInfo);
            return true;
        }
        return false;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void removeClient(final String id) {
        getTxStorage().delete(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    @Override
    public CheckInfo check(final String id, final String mark) {
        return getTxStorage().getCheckInfo(id, mark);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @Override
    public void saveResult(final CheckInfo checkInfo) {
        getTxStorage().saveCheckInfo(checkInfo);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public boolean containClient(final String id) {
        return getTxStorage().containsClientInfo(id);
    }

    private static TxStorage getTxStorage() {
        if (txStorage == null) {
            txStorage = ContextHolder.getContext().getBean(TxStorage.class);
        }
        return txStorage;
    }
}
