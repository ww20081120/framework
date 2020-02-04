/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server;

import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Jan 21, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server <br>
 */
public interface TxStorage {

    boolean containsClientInfo(String id);

    CheckInfo getCheckInfo(String id, String mark);

    void saveClientInfo(ClientInfo clientInfo);

    void saveCheckInfo(CheckInfo checkInfo);

    PagerList<ClientInfo> queryTimeoutClientInfo(int retryTimes, int pageIndex, int pageSize);

    void updateClientRetryTimes(String id);

    void delete(String id);

}
