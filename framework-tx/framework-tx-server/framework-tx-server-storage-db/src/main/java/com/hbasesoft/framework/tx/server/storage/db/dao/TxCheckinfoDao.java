/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.tx.server.storage.db.dao;

import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxCheckinfoEntity;

/**
 * <Description> T_TX_CHECKINFO的Dao<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate.BaseJpaDao <br>
 */
@Dao
public interface TxCheckinfoDao {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    @Sql("SELECT ID, MARK, RESULT FROM T_TX_CHECKINFO WHERE ID = :id AND MARK = :mark")
    TxCheckinfoEntity getCheckInfoById(@Key("id") String id, @Key("mark") String mark);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return <br>
     */
    @Sql("INSERT INTO T_TX_CHECKINFO (ID, MARK, RESULT, CREATE_TIME) VALUES "
        + "(:entity.id, :entity.mark, :entity.result, now())")
    int saveCheckInfo(@Key("entity") TxCheckinfoEntity entity);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Sql("DELETE FROM T_TX_CHECKINFO WHERE ID = :id")
    int deleteCheckInfo(@Key("id") String id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     * @return int
     */
    @Sql("UPDATE FROM T_TX_CHECKINFO SET RESULT = :bean.result WHERE ID = :bean.id AND MARK = :bean.mark")
    int updateCheckInfo(@Key("bean") CheckInfo checkInfo);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark <br>
     * @return int
     */
    @Sql("DELETE FROM T_TX_CHECKINFO WHERE ID = :id AND MARK = :mark")
    int delCheckInfo(@Key("id") String id, @Key("mark") String mark);
}
