/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or     <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.message.delay.db.dao;

import java.util.Date;

import com.hbasesoft.framework.common.annotation.Key;
import com.hbasesoft.framework.db.BaseJpaDao;
import com.hbasesoft.framework.db.Dao;
import com.hbasesoft.framework.db.core.DaoConstants;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.delay.db.entity.MsgDelaymsgEntity;

/**
 * <Description> T_MSG_DELAYMSG的Dao<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.BaseJpaDao <br>
 */
@Dao
public interface MsgDelaymsgDao extends BaseJpaDao<MsgDelaymsgEntity> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void createTable();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param expireTime
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Sql("SELECT ID, EXPIRE_TIME FROM T_MSG_DELAYMSG WHERE EXPIRE_TIME <= :expireTime AND MEMERY_FLAG = 'N'")
    PagerList<MsgDelaymsgEntity> queryByTime(@Key("expireTime") Date expireTime,
        @Key(DaoConstants.PAGE_INDEX) int pageIndex, @Key(DaoConstants.PAGE_SIZE) int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param expireTime
     * @param shardInfo
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Sql("SELECT ID, EXPIRE_TIME FROM T_MSG_DELAYMSG WHERE SHARD_INFO = :shardInfo "
        + "AND EXPIRE_TIME <= :expireTime AND MEMERY_FLAG = 'Y'")
    PagerList<MsgDelaymsgEntity> queryByTimeAndShard(@Key("expireTime") Date expireTime,
        @Key("shardInfo") String shardInfo, @Key(DaoConstants.PAGE_INDEX) int pageIndex,
        @Key(DaoConstants.PAGE_SIZE) int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Sql("UPDATE T_MSG_DELAYMSG SET MEMERY_FLAG = 'Y' WHERE ID = :id")
    int updateMemeryFlag(@Key("id") String id);
}
