/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core.client;

import com.hbasesoft.framework.tx.core.CheckInfo;
import com.hbasesoft.framework.tx.core.ClientInfo;

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
public interface TxProducer {

    /**
     * Description: 注册客户端信息<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo
     * @return <br>
     */
    ClientInfo regist(ClientInfo clientInfo);

    /**
     * Description: 检查并获取结果<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    CheckInfo check(String id, String mark);

    /**
     * Description: 反馈执行结果<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    void saveResult(CheckInfo checkInfo);
}
