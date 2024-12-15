/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.dao;

import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.db.BaseJpaDao;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxClientinfoEntity;

/**
 * <Description> T_TX_CLIENTINFO的Dao<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.BaseJpaDao <br>
 */
@Dao
public interface TxClientinfoDao extends BaseJpaDao<TxClientinfoEntity> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Sql("SELECT ID FROM T_TX_CLIENTINFO WHERE ID = :id")
    String containsClientInfo(@Key("id") String id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Sql("SELECT ID, MAX_RETRY_TIMES, CURRENT_RETRY_TIMES, RETRY_CONFIGS FROM T_TX_CLIENTINFO WHERE ID = :id")
    TxClientinfoEntity getRetryConfigs(@Key("id") String id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return <br>
     */
    @Sql("UPDATE T_TX_CLIENTINFO SET CURRENT_RETRY_TIMES = :entity.currentRetryTimes,"
        + " NEXT_RETRY_TIME = :entity.nextRetryTime WHERE ID = :entity.id")
    int updateRetryTimes(@Key("entity") TxClientinfoEntity entity);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo
     * @param retryTimes
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Sql("SELECT ID, MARK, CONTEXT, ARGS, CLIENT_INFO FROM T_TX_CLIENTINFO WHERE CLIENT_INFO = :clientInfo AND "
        + "NEXT_RETRY_TIME <= NOW() AND MAX_RETRY_TIMES > CURRENT_RETRY_TIMES AND CURRENT_RETRY_TIMES = :retryTimes ")
    PagerList<TxClientinfoEntity> queryTimeoutClientInfos(@Key("clientInfo") String clientInfo,
        @Key("retryTimes") int retryTimes, @Key(DaoConstants.PAGE_INDEX) int pageIndex,
        @Key(DaoConstants.PAGE_SIZE) int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Sql("DELETE FROM T_TX_CLIENTINFO WHERE ID = :id")
    int deleteClientinfo(@Key("id") String id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Sql("SELECT COUNT(1) FROM T_TX_CLIENTINFO")
    int checkTable();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void createTable();
}
