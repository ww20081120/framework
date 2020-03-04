/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.storage.db;

import java.util.Calendar;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.server.PagerList;
import com.hbasesoft.framework.tx.server.TxStorage;
import com.hbasesoft.framework.tx.server.storage.db.dao.TxCheckinfoDao;
import com.hbasesoft.framework.tx.server.storage.db.dao.TxClientinfoDao;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxCheckinfoEntity;
import com.hbasesoft.framework.tx.server.storage.db.entity.TxClientinfoEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Feb 1, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server <br>
 */
@Service
public class TxStorageImpl implements TxStorage {

    /** Number */
    private static final int NUM_5 = 5;

    /** txClientinfoDao */
    @Resource
    private TxClientinfoDao txClientinfoDao;

    /** txCheckinfoDao */
    @Resource
    private TxCheckinfoDao txCheckinfoDao;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    @Transactional(readOnly = true)
    public boolean containsClientInfo(String id) {
        return StringUtils.isNotEmpty(txClientinfoDao.containsClientInfo(id));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark
     * @return <br>
     */
    @Override
    @Transactional(readOnly = true)
    public CheckInfo getCheckInfo(String id, String mark) {
        TxCheckinfoEntity bean = txCheckinfoDao.getCheckInfoById(id, mark);
        if (bean != null) {
            return new CheckInfo(bean.getId(), bean.getMark(), bean.getResult());
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveClientInfo(ClientInfo clientInfo) {
        TxClientinfoEntity bean = new TxClientinfoEntity();
        bean.setArgs(clientInfo.getArgs());
        bean.setContext(clientInfo.getContext());
        bean.setCurrentRetryTimes(0);
        bean.setId(clientInfo.getId());
        bean.setMark(clientInfo.getMark());
        bean.setMaxRetryTimes(clientInfo.getMaxRetryTimes());
        bean.setClientInfo(clientInfo.getClientInfo());

        String[] retryConfigs = StringUtils.split(clientInfo.getRetryConfigs(), GlobalConstants.SPLITOR);
        int min = CommonUtil.isEmpty(retryConfigs) ? NUM_5 : Integer.parseInt(retryConfigs[0]);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, min);
        bean.setNextRetryTime(calendar.getTime());
        bean.setRetryConfigs(clientInfo.getRetryConfigs());
        txClientinfoDao.save(bean);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void saveCheckInfo(CheckInfo checkInfo) {
        TxCheckinfoEntity bean = new TxCheckinfoEntity();
        bean.setId(checkInfo.getId());
        bean.setMark(checkInfo.getMark());
        bean.setResult(checkInfo.getResult());
        txCheckinfoDao.saveCheckInfo(bean);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param retryTimes
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    @Transactional(readOnly = true)
    public PagerList<ClientInfo> queryTimeoutClientInfo(int retryTimes, int pageIndex, int pageSize) {
        com.hbasesoft.framework.db.core.utils.PagerList<TxClientinfoEntity> beans = txClientinfoDao
            .queryTimeoutClientInfos(retryTimes, pageIndex, pageSize);
        if (beans != null) {
            PagerList<ClientInfo> pagerList = new PagerList<>();
            pagerList.setPageIndex(beans.getPageIndex());
            pagerList.setPageSize(beans.getPageSize());
            pagerList.setTotalCount(beans.getTotalCount());
            pagerList.addAll(beans.stream().map(
                b -> new ClientInfo(b.getId(), b.getMark(), b.getContext(), b.getArgs(), 0, null, b.getClientInfo()))
                .collect(Collectors.toList()));
            return pagerList;
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateClientRetryTimes(String id) {
        TxClientinfoEntity bean = txClientinfoDao.getRetryConfigs(id);
        if (bean != null) {
            bean.setCurrentRetryTimes(bean.getCurrentRetryTimes() + 1);
            if (bean.getMaxRetryTimes() > bean.getCurrentRetryTimes()) {
                String[] retryConfigs = StringUtils.split(bean.getRetryConfigs(), GlobalConstants.SPLITOR);
                int min = Integer.parseInt(
                    bean.getCurrentRetryTimes() >= retryConfigs.length - 1 ? retryConfigs[retryConfigs.length - 1]
                        : retryConfigs[bean.getCurrentRetryTimes()]);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, min);
                bean.setNextRetryTime(calendar.getTime());
            }
            txClientinfoDao.updateRetryTimes(bean);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void delete(String id) {
        txClientinfoDao.deleteClientinfo(id);
        txCheckinfoDao.deleteCheckInfo(id);
    }
}
