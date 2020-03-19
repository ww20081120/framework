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

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    boolean containsClientInfo(String id);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    CheckInfo getCheckInfo(String id, String mark);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    void saveClientInfo(ClientInfo clientInfo);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    void saveCheckInfo(CheckInfo checkInfo);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param retryTimes
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    PagerList<ClientInfo> queryTimeoutClientInfo(int retryTimes, int pageIndex, int pageSize);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void updateClientRetryTimes(String id);

    /** 
     * 
     * Description: <br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void delete(String id);

}
