/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db.service;

import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Transactional(rollbackFor = Exception.class)
    void save(MsgDelaymsgEntity entity);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Transactional(rollbackFor = Exception.class)
    MsgDelaymsgEntity delete(String id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(readOnly = true)
    PagerList<MsgDelaymsgEntity> queryByTime(Date expireTime, int pageIndex, int pageSize);

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
    @Transactional(readOnly = true)
    PagerList<MsgDelaymsgEntity> queryByTimeAndShard(Date expireTime, String shardInfo, int pageIndex, int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Transactional(rollbackFor = Exception.class)
    int updateMemeryFlag(String id);
}
