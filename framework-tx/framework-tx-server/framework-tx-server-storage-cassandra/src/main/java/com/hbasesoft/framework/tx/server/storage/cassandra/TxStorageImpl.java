/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.tx.server.storage.cassandra;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;
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

    private ThreadLocal<String> holder = new ThreadLocal<>();

    /** Number */
    private static final int NUM_5 = 5;

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
    public boolean containsClientInfo(String id) {
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
    public CheckInfo getCheckInfo(String id, String mark) {

        Where select = QueryBuilder.select().from("t_tx_check_info").where(QueryBuilder.eq("id", id))
            .and(QueryBuilder.eq("mark", mark));

        TxCheckinfoEntity entity = cassandraOperations.selectOne(select, TxCheckinfoEntity.class);

        if (entity != null) {
            return new CheckInfo(entity.getId(), entity.getMark(),
                entity.getResult() == null ? null : entity.getResult().array());
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
    public void saveClientInfo(ClientInfo clientInfo) {

        TxClientinfoEntity bean = new TxClientinfoEntity();
        bean.setArgs(array2Buffer(clientInfo.getArgs()));
        bean.setContext(clientInfo.getContext());
        bean.setCurrentRetryTimes(0);
        bean.setId(clientInfo.getId());
        bean.setMark(clientInfo.getMark());
        bean.setMaxRetryTimes(clientInfo.getMaxRetryTimes());
        bean.setClientInfo(clientInfo.getClientInfo());
        bean.setCreateTime(DateUtil.getCurrentDate());

        String[] retryConfigs = StringUtils.split(clientInfo.getRetryConfigs(), GlobalConstants.SPLITOR);
        int min = CommonUtil.isEmpty(retryConfigs) ? NUM_5 : Integer.parseInt(retryConfigs[0]);
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
    public void saveCheckInfo(CheckInfo checkInfo) {

        TxCheckinfoEntity bean = new TxCheckinfoEntity();
        bean.setId(checkInfo.getId());
        bean.setMark(checkInfo.getMark());
        bean.setResult(array2Buffer(checkInfo.getResult()));
        cassandraOperations.insert(bean);
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
    public PagerList<ClientInfo> queryTimeoutClientInfo(int retryTimes, int pageIndex, int pageSize) {

        Query q = Query.query(Criteria.where("currentRetryTimes").is(retryTimes),
            Criteria.where("nextRetryTime").lte(DateUtil.getCurrentDate())).withAllowFiltering().limit(pageSize);

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
            pagerList.setTotalCount(100000);
            pagerList.addAll(entities.stream()
                .map(b -> new ClientInfo(b.getId(), b.getMark(), b.getContext(),
                    b.getArgs() == null ? null : b.getArgs().array(), 0, null, b.getClientInfo()))
                .collect(Collectors.toList()));
            return pagerList;
        }
        else {
            holder.remove();
        }
        return null;
    }

    private ByteBuffer array2Buffer(byte[] array) {
        if (array != null) {
            ByteBuffer args = ByteBuffer.allocate(array.length);
            args.put(array);
            return args;
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
    public void updateClientRetryTimes(String id) {
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
    public void delete(String id) {
        cassandraOperations.deleteById(id, TxClientinfoEntity.class);
        cassandraOperations.delete(Query.query(Criteria.where("id").is(id)), TxCheckinfoEntity.class);
    }
}
