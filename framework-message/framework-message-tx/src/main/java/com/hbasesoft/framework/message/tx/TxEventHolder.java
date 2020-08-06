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

    private static final Map<String, Tx> txHolder = new HashMap<String, Tx>();

    public static synchronized void put(String event, Tx tx) {
        txHolder.put(event, tx);
    }

    public static Tx get(String event) {
        return txHolder.get(event);
    }
}
