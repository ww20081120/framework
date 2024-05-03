/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.storage.cassandra;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.tx.core.bean.CheckInfo;
import com.hbasesoft.framework.tx.core.bean.ClientInfo;
import com.hbasesoft.framework.tx.server.PagerList;
import com.hbasesoft.framework.tx.server.TxStorage;
import com.hbasesoft.framework.tx.server.storage.cassandra.entity.TxCheckinfoEntity;
import com.hbasesoft.framework.tx.server.storage.cassandra.entity.TxClientinfoEntity;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Apr 2, 2020 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.tx.server.storage.cassandra <br>
 */
@Service
public class TxStorageImpl implements TxStorage {

    /** */
    private ThreadLocal<String> holder = new ThreadLocal<>();

    /** Number */
    private static final int NUM_5 = 5;

    /** */
    private static final int NUM100000 = 100000;

    /** */
    @Autowired
    private CassandraOperations cassandraOperations;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public boolean containsClientInfo(final String id) {
        return cassandraOperations.exists(id, TxClientinfoEntity.class);
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
    public CheckInfo getCheckInfo(final String id, final String mark) {

        Query q = Query.query(Criteria.where("id").is(id), Criteria.where("mark").is(mark));
        TxCheckinfoEntity entity = cassandraOperations.selectOne(q, TxCheckinfoEntity.class);

        if (entity != null) {
            return new CheckInfo(entity.getId(), entity.getMark(),
                entity.getResult() == null ? null : DataUtil.hexStr2Byte(entity.getResult()));
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
    public void saveClientInfo(final ClientInfo clientInfo) {

        TxClientinfoEntity bean = new TxClientinfoEntity();
        bean.setArgs(DataUtil.byte2HexStr(clientInfo.getArgs()));
        bean.setContext(clientInfo.getContext());
        bean.setCurrentRetryTimes(0);
        bean.setId(clientInfo.getId());
        bean.setMark(clientInfo.getMark());
        bean.setMaxRetryTimes(clientInfo.getMaxRetryTimes());
        bean.setClientInfo(clientInfo.getClientInfo());
        bean.setCreateTime(DateUtil.getCurrentDate());

        String[] retryConfigs = StringUtils.split(clientInfo.getRetryConfigs(), GlobalConstants.SPLITOR);
        int min = ArrayUtils.isEmpty(retryConfigs) ? NUM_5 : Integer.parseInt(retryConfigs[0]);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, min);
        bean.setNextRetryTime(calendar.getTime());
        bean.setRetryConfigs(clientInfo.getRetryConfigs());
        cassandraOperations.insert(bean);

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @Override
    public void saveCheckInfo(final CheckInfo checkInfo) {

        TxCheckinfoEntity bean = new TxCheckinfoEntity();
        bean.setId(checkInfo.getId());
        bean.setMark(checkInfo.getMark());
        if (checkInfo.getResult() != null) {
            bean.setResult(DataUtil.byte2HexStr(checkInfo.getResult()));
        }
        bean.setCreateTime(DateUtil.getCurrentDate());
        cassandraOperations.insert(bean);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo
     * @param retryTimes
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<ClientInfo> queryTimeoutClientInfo(final String clientInfo, final int retryTimes,
        final int pageIndex, final int pageSize) {

        Query q = Query.query(Criteria.where("currentRetryTimes").is(retryTimes),
            Criteria.where("clientInfo").is(clientInfo), Criteria.where("nextRetryTime").lte(DateUtil.getCurrentDate()))
            .withAllowFiltering().limit(pageSize);

        if (pageIndex > 1) {
            String id = holder.get();
            if (id != null) {
                q.and(Criteria.where("token(id)").gt("token('" + id + "')"));
            }
        }
        List<TxClientinfoEntity> entities = cassandraOperations.select(q, TxClientinfoEntity.class);
        if (CollectionUtils.isNotEmpty(entities)) {
            holder.set(entities.get(entities.size() - 1).getId());
            PagerList<ClientInfo> pagerList = new PagerList<>();
            pagerList.setPageIndex(pageIndex);
            pagerList.setPageSize(pageSize);
            pagerList.setTotalCount(NUM100000);
            pagerList.addAll(entities.stream()
                .map(b -> new ClientInfo(b.getId(), b.getMark(), b.getContext(),
                    b.getArgs() == null ? null : DataUtil.hexStr2Byte(b.getArgs()), 0, null, b.getClientInfo()))
                .collect(Collectors.toList()));
            return pagerList;
        }
        else {
            holder.remove();
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
    public void updateClientRetryTimes(final String id) {
        TxClientinfoEntity bean = cassandraOperations.selectOneById(id, TxClientinfoEntity.class);
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
            cassandraOperations.update(bean);
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
    public void delete(final String id) {
        cassandraOperations.deleteById(id, TxClientinfoEntity.class);
        cassandraOperations.delete(Query.query(Criteria.where("id").is(id)), TxCheckinfoEntity.class);
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
    public ClientInfo getClientInfo(final String id) {
        TxClientinfoEntity bean = cassandraOperations.selectOneById(id, TxClientinfoEntity.class);
        return bean == null ? null
            : new ClientInfo(id, bean.getMark(), bean.getContext(),
                bean.getArgs() == null ? null : DataUtil.hexStr2Byte(bean.getArgs()), bean.getMaxRetryTimes(),
                bean.getRetryConfigs(), bean.getContext());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clientInfo <br>
     */
    @Override
    public void updateClientinfo(final ClientInfo clientInfo) {
        TxClientinfoEntity bean = cassandraOperations.selectOneById(clientInfo.getId(), TxClientinfoEntity.class);
        if (bean != null) {
            bean.setArgs(clientInfo.getArgs() == null ? null : DataUtil.byte2HexStr(clientInfo.getArgs()));
            cassandraOperations.update(bean);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param checkInfo <br>
     */
    @Override
    public void updateCheckInfo(final CheckInfo checkInfo) {

        Query q = Query.query(Criteria.where("id").is(checkInfo.getId()),
            Criteria.where("mark").is(checkInfo.getMark()));
        TxCheckinfoEntity entity = cassandraOperations.selectOne(q, TxCheckinfoEntity.class);

        if (entity != null) {
            entity.setResult(checkInfo.getResult() == null ? null : DataUtil.byte2HexStr(checkInfo.getResult()));
            cassandraOperations.update(entity);
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param mark <br>
     */
    @Override
    public void deleteCheckInfo(final String id, final String mark) {

        Query q = Query.query(Criteria.where("id").is(id), Criteria.where("mark").is(mark));
        TxCheckinfoEntity entity = cassandraOperations.selectOne(q, TxCheckinfoEntity.class);
        if (entity != null) {
            cassandraOperations.delete(entity);
        }
    }
}
