/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db.service;

import java.util.Date;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.delay.db.entity.MsgDelaymsgEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.db.service <br>
 */
public interface DelaymsgService {

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    void save(MsgDelaymsgEntity entity);

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    MsgDelaymsgEntity get(String id);

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    void delete(String id);

    @Transactional(rollbackFor = Exception.class)
    void createTable();

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    PagerList<MsgDelaymsgEntity> queryByTime(Date expireTime, int pageIndex, int pageSize);

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    PagerList<MsgDelaymsgEntity> queryByTimeAndShard(Date expireTime, String shardInfo, int pageIndex, int pageSize);

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    int updateMemeryFlag(@Param("id") String id);
}
