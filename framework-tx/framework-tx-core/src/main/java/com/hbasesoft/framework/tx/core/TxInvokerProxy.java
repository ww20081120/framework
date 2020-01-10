/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import com.hbasesoft.framework.tx.core.client.TxProducer;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 10, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.core <br>
 */
public final class TxInvokerProxy {

    private TxProducer sender;

    @SuppressWarnings("unchecked")
    public <T> T invoke(String mark, TxInvoker invoker) {
        String id = null;
        CheckInfo checkInfo = sender.check(id, mark);
        if (checkInfo.getFlag() != 0) {
            T msg = invoker.invoke();
            checkInfo.setResult(msg);
            sender.saveResult(checkInfo);
            return msg;
        }
        return (T) checkInfo.getResult();
    }
}
