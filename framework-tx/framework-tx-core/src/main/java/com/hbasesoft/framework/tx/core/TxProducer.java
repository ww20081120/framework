/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.core;

import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;

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

    /** 检测消息是否执行完毕 */
    boolean containClient(String id);

    /**
     * Description: 注册客户端<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo 客户端信息<br>
     */
    boolean registClient(ClientInfo clientInfo);

    /**
     * Description: 删除客户端<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void removeClient(String id);

    /**
     * Description: 注册消息，并获取结果<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    CheckInfo registMsg(String id, String mark);

    /**
     * Description: 反馈执行结果<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    void saveResult(CheckInfo checkInfo);

}
