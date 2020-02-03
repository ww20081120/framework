/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

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
@Service
public class TxProducerImpl implements TxProducer {

    @Resource
    private TxStorage txStorage;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public void registClient(ClientInfo clientInfo) {
        txStorage.saveClientInfo(clientInfo);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void removeClient(String id) {
        txStorage.delete(id);
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
    public CheckInfo registMsg(String id, String mark) {
        CheckInfo checkInfo = txStorage.getCheckInfo(id, mark);
        if (checkInfo == null) {
            checkInfo = new CheckInfo(id, mark, 1, null);
            txStorage.saveCheckInfo(checkInfo);
        }
        return checkInfo;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @Override
    public void saveResult(CheckInfo checkInfo) {
        txStorage.updateCheckInfo(checkInfo);
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
    public boolean containClient(String id) {
        return txStorage.containsClientInfo(id);
    }
}
