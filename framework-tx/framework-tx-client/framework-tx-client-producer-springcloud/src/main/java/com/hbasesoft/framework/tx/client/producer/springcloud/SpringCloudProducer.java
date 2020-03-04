/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.client.producer.springcloud;

import com.hbasesoft.framework.common.annotation.NoTransLog;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.client.producer.springcloud.client.FeginProducer;
import com.hbasesoft.framework.tx.core.TxProducer;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

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
@NoTransLog
public class SpringCloudProducer implements TxProducer {

    /** fegin producer */
    private static FeginProducer feginProducer;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public boolean registClient(ClientInfo clientInfo) {
        LoggerUtil.debug("registClient {0}|{1}", clientInfo.getId(), clientInfo.getMark());
        return getFeginProducer().registClient(clientInfo);
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
        LoggerUtil.debug("removeClient {0}", id);
        getFeginProducer().removeClient(id);
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
    public CheckInfo check(String id, String mark) {
        LoggerUtil.debug("check {0}|{1}", id, mark);
        return getFeginProducer().check(id, mark);
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
        LoggerUtil.debug("saveResult {0}|{1}", checkInfo.getId(), checkInfo.getMark());
        getFeginProducer().saveResult(checkInfo);
    }

    private FeginProducer getFeginProducer() {
        if (feginProducer == null) {
            feginProducer = ContextHolder.getContext().getBean(FeginProducer.class);
        }
        return feginProducer;
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
        LoggerUtil.debug("containClient {0}", id);
        return getFeginProducer().containClient(id);
    }

}
