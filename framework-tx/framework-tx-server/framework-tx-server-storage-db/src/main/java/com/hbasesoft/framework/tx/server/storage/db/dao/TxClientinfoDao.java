/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.dao;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.hibernate.IBaseDao;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxClientinfoEntity;

/**
 * <Description> T_TX_CLIENTINFO的Dao<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate.IBaseDao <br>
 */
@Dao
public interface TxClientinfoDao extends IBaseDao<TxClientinfoEntity> {

    @Sql("SELECT ID FROM T_TX_CLIENTINFO WHERE ID = :id")
    String containsClientInfo(@Param("id") String id);

    @Sql("SELECT ID, MAX_RETRY_TIMES, CURRENT_RETRY_TIMES, RETRY_CONFIGS FROM T_TX_CLIENTINFO WHERE ID = :id")
    TxClientinfoEntity getRetryConfigs(@Param("id") String id);

    @Sql("UPDATE T_TX_CLIENTINFO SET CURRENT_RETRY_TIMES = :entity.currentRetryTimes, NEXT_RETRY_TIME = :entity.nextRetryTime WHERE ID = :entity.id")
    int updateRetryTimes(@Param("entity") TxClientinfoEntity entity);

    @Sql("SELECT ID, MARK, CONTEXT, ARGS, CLIENT_INFO FROM T_TX_CLIENTINFO WHERE NEXT_RETRY_TIME <= NOW() AND MAX_RETRY_TIMES > CURRENT_RETRY_TIMES AND CURRENT_RETRY_TIMES = :retryTimes ")
    PagerList<TxClientinfoEntity> queryTimeoutClientInfos(@Param("retryTimes") int retryTimes,
        @Param(Param.PAGE_INDEX) int pageIndex, @Param(Param.PAGE_SIZE) int pageSize);

    @Sql("DELETE FROM T_TX_CLIENTINFO WHERE ID = :id")
    int deleteClientinfo(@Param("id") String id);
}
