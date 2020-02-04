/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.dao;

import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxCheckinfoEntity;

/**
 * <Description> T_TX_CHECKINFO的Dao<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate.IBaseDao <br>
 */
@Dao
public interface TxCheckinfoDao {

    @Sql("SELECT ID, MARK, FLAG, RESULT FROM T_TX_CHECKINFO WHERE ID = :id AND MARK = :mark")
    TxCheckinfoEntity getCheckInfoById(@Param("id") String id, @Param("mark") String mark);

    @Sql("INSERT INTO T_TX_CHECKINFO (ID, MARK, FLAG, RESULT) VALUES (:entity.id, :entity.mark, :entity.flag, :entity.result)")
    int saveCheckInfo(@Param("entity") TxCheckinfoEntity entity);

    @Sql("UPDATE T_TX_CHECKINFO SET FLAG = :entity.flag, RESULT = :entity.result WHERE ID = :entity.id AND MARK = :entity.mark")
    int updateCheckInfo(@Param("entity") TxCheckinfoEntity entity);

    @Sql("DELETE FROM T_TX_CHECKINFO WHERE ID = :id")
    int deleteCheckInfo(@Param("id") String id);
}
