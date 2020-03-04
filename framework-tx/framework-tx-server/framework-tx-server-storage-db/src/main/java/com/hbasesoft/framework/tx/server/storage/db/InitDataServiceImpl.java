/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.storage.db;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.tx.server.storage.db.dao.TxClientinfoDao;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 4, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.storage.db <br>
 */
@Service
public class InitDataServiceImpl implements InitDataService {

    /** txClientinfoDao */
    @Resource
    private TxClientinfoDao txClientinfoDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    @Transactional
    public void createTableIfNotExist() {
        try {
            txClientinfoDao.checkTable();
            return;
        }
        catch (Exception e) {
        }
        LoggerUtil.info("创建分布式事务相关的表");
        txClientinfoDao.createTable();
    }

}
