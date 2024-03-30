/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.delay.db.dao.MsgDelaymsgDao;
import com.hbasesoft.framework.message.delay.db.entity.MsgDelaymsgEntity;
import com.hbasesoft.framework.message.delay.db.service.DelaymsgService;

import jakarta.annotation.Resource;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年4月11日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.delay.db.service.impl <br>
 */
@Service
public class DelaymsgServiceImpl implements DelaymsgService {

    /**
     * msg delay msg dao
     */
    @Resource
    private MsgDelaymsgDao msgDelaymsgDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void save(final MsgDelaymsgEntity entity) {
        msgDelaymsgDao.save(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public MsgDelaymsgEntity delete(final String id) {
        MsgDelaymsgEntity entity = msgDelaymsgDao.get(id);
        if (entity != null) {
            msgDelaymsgDao.deleteById(id);
        }
        return entity;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void createTable() {
        msgDelaymsgDao.createTable();
    }

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
    @Override
    public PagerList<MsgDelaymsgEntity> queryByTime(final Date expireTime, final int pageIndex, final int pageSize) {
        return msgDelaymsgDao.queryByTime(expireTime, pageIndex, pageSize);
    }

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
    @Override
    public PagerList<MsgDelaymsgEntity> queryByTimeAndShard(final Date expireTime, final String shardInfo,
        final int pageIndex, final int pageSize) {
        return msgDelaymsgDao.queryByTimeAndShard(expireTime, shardInfo, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public int updateMemeryFlag(final String id) {
        return msgDelaymsgDao.updateMemeryFlag(id);
    }

}
