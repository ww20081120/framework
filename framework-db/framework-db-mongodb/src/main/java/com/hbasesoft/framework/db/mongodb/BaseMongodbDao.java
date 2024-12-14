/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.mongodb;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.hbasesoft.framework.db.core.AbstractBaseDao;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.utils.PagerList;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;

/**
 * <Description> <br>
 * 
 * @param <T> T
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年12月14日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.mongodb <br>
 */
public class BaseMongodbDao<T extends BaseEntity> extends AbstractBaseDao<T> {

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public CriteriaBuilder criteriaBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void delete(final T entity) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param entities <br>
     */
    @Override
    public void deleteBatch(final Collection<T> entities) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param criteria <br>
     */
    @Override
    public void deleteByCriteria(final CriteriaDelete<T> criteria) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void deleteById(final Serializable id) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param ids <br>
     */
    @Override
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public T get(final Serializable id) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param criteria
     * @return <br>
     */
    @Override
    public <M> M getByCriteria(final CriteriaQuery<M> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M getBySpecification(final CriterialQuerySpecification<T> specification, final Class<M> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<T> queryAll() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param criteria
     * @return <br>
     */
    @Override
    public <M> List<M> queryByCriteria(final CriteriaQuery<M> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> queryBySpecification(final CriterialQuerySpecification<T> specification, final Class<M> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param criteria
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByCriteria(final CriteriaQuery<M> criteria, final int pageIndex,
        final int pageSize) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerBySpecification(final CriterialQuerySpecification<T> specification,
        final int pageIndex, final int pageSize, final Class<M> clazz) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void save(final T entity) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void saveBatch(final List<T> entitys) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param pojo <br>
     */
    @Override
    public void update(final T pojo) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void updateBatch(final List<T> entitys) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param criteria <br>
     */
    @Override
    public void updateByCriteria(final CriteriaUpdate<T> criteria) {
        // TODO Auto-generated method stub

    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public Object query(final String sql, final DataParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return <br>
     */
    @Override
    public int[] batchExcuteSql(final String[] sql, final DataParam param) {
        // TODO Auto-generated method stub
        return null;
    }
}
