/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.tx;

import java.util.HashMap;
import java.util.Map;

import com.hbasesoft.framework.tx.core.annotation.Tx;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jul 9, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.tx <br>
 */
public final class TxEventHolder {

    /** */
    private static final Map<String, Tx> TX_HOLDER = new HashMap<String, Tx>();

    /**
     * @Method put
     * @param event
     * @param tx
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 15:16
    */
    public static synchronized void put(final String event, final Tx tx) {
        TX_HOLDER.put(event, tx);
    }

    /**
     * @Method get
     * @param event
     * @return com.hbasesoft.framework.tx.core.annotation.Tx
     * @Author 李煜龙
     * @Description TODD
     * @Date 2023/1/29 15:17
    */
    public static Tx get(final String event) {
        return TX_HOLDER.get(event);
    }
}
