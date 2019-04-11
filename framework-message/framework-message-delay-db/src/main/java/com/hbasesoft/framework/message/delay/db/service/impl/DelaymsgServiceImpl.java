/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.message.delay.db.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.delay.db.dao.MsgDelaymsgDao;
import com.hbasesoft.framework.message.delay.db.entity.MsgDelaymsgEntity;
import com.hbasesoft.framework.message.delay.db.service.DelaymsgService;

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
    public void save(MsgDelaymsgEntity entity) {
        msgDelaymsgDao.save(entity);
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
    public MsgDelaymsgEntity get(String id) {
        return msgDelaymsgDao.get(id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void delete(String id) {
        msgDelaymsgDao.deleteById(id);
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
    public PagerList<MsgDelaymsgEntity> queryByTime(Date expireTime, int pageIndex, int pageSize) {
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
    public PagerList<MsgDelaymsgEntity> queryByTimeAndShard(Date expireTime, String shardInfo, int pageIndex,
        int pageSize) {
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
    public int updateMemeryFlag(String id) {
        return msgDelaymsgDao.updateMemeryFlag(id);
    }

}
