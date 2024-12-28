/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db;

import java.util.Collection;
import java.util.List;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.utils.PagerList;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @param <T> T
 * @CreateDate 2019年4月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate <br>
 */
public interface BaseJpaDao<T extends BaseEntity> extends BaseDao<T> {

    /**
     * <Description> <br>
     * 
     * @param <T> 参数类型
     * @author ww200<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月6日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.hibernate <br>
     */
    @FunctionalInterface
    interface CriterialDeleteSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param root
         * @param delete
         * @param criteriaBuilder
         * @return <br>
         */
        Predicate toPredicate(Root<? extends Tuple> root, CriteriaDelete<T> delete, CriteriaBuilder criteriaBuilder);
    }

    /**
     * <Description> <br>
     * 
     * @param <T> 参数类型
     * @author ww200<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月6日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.hibernate <br>
     */
    @FunctionalInterface
    interface CriterialQuerySpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param root
         * @param query
         * @param criteriaBuilder
         * @return <br>
         */
        Predicate toPredicate(Root<? extends Tuple> root, CriteriaQuery<Tuple> query, CriteriaBuilder criteriaBuilder);
    }

    /**
     * <Description> <br>
     * 
     * @param <T> 参数类型
     * @author ww200<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月6日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.hibernate <br>
     */
    @FunctionalInterface
    interface CriterialUpdateSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param root
         * @param update
         * @param criteriaBuilder
         * @return <br>
         */
        Predicate toPredicate(Root<? extends Tuple> root, CriteriaUpdate<T> update, CriteriaBuilder criteriaBuilder);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void clear();

    /**
     * Description: JPA工厂<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    CriteriaBuilder criteriaBuilder();

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    void deleteByCriteria(CriteriaDelete<T> criteria);

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void deleteBySpecification(CriterialDeleteSpecification<T> specification);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param objcts
     * @param commitNumber <br>
     */
    void executeBatch(String sql, Collection<Object[]> objcts, int commitNumber);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param procedureSql
     * @param params
     * @return <br>
     */
    List<T> executeProcedure(String procedureSql, Object... params);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void flush();

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M> M
     * @param criteria
     * @return <br>
     */
    <M> M getByCriteria(CriteriaQuery<M> criteria);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return T
     */
    T getByHql(String hql);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    T getBySpecification(CriterialQuerySpecification<T> specification);

    /**
     * Description: 根据条件查询<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    <M> M getBySpecification(CriterialQuerySpecification<T> specification, Class<M> clazz);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param criteria
     * @return <br>
     */
    <M> List<M> queryByCriteria(CriteriaQuery<M> criteria);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return <br>
     */
    List<T> queryByHql(String hql);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param param
     * @return <br>
     */
    List<T> queryByHqlParam(String hql, Object... param);

    /**
     * Description: 根据条件查询<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    List<T> queryBySpecification(CriterialQuerySpecification<T> specification);

    /**
     * Description:根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    <M> List<M> queryBySpecification(CriterialQuerySpecification<T> specification, Class<M> clazz);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @return <br>
     */
    List<T> queryBySql(String sql);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param sql
     * @param clazz
     * @return <br>
     */
    <M> List<M> queryBySql(String sql, Class<M> clazz);

    /**
     * Description: 根据条件查询<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M> M
     * @param criteria
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    <M> PagerList<M> queryPagerByCriteria(CriteriaQuery<M> criteria, int pageIndex, int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    PagerList<T> queryPagerBySpecification(CriterialQuerySpecification<T> specification, int pageIndex, int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    <M> PagerList<M> queryPagerBySpecification(CriterialQuerySpecification<T> specification, int pageIndex,
        int pageSize, Class<M> clazz);

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    void updateByCriteria(CriteriaUpdate<T> criteria);

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void updateBySpecification(CriterialUpdateSpecification<T> specification);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @return numbers
     * @throws DaoException <br>
     */
    int updateBySql(String sql) throws DaoException;

}
