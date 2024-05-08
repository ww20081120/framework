/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.wrapper.DeleteWrapper;
import com.hbasesoft.framework.db.core.wrapper.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.wrapper.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.wrapper.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.wrapper.QueryWrapper;
import com.hbasesoft.framework.db.core.wrapper.UpdateWrapper;

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
public interface BaseDao<T extends BaseEntity> {

    /**
     * Description: JPA工厂<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    CriteriaBuilder getCriteriaBuilder();

    /**
     * Description: 保存数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return <br>
     */
    Serializable save(T entity);

    /**
     * Description: 批量保存<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    void saveBatch(List<T> entitys);

    /**
     * Description: 更新数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pojo <br>
     */
    void update(T pojo);

    /**
     * Description: 批量更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    void updateBatch(List<T> entitys);

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
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void update(UpdateSpecification<T> specification);

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void updateByLambda(LambdaUpdateSpecification<T> specification);

    /**
     * Description: 删除数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    void delete(T entity);

    /**
     * Description: 根据id来删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void deleteById(Serializable id);

    /**
     * Description: 批量删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     */
    void deleteBatch(Collection<T> entities);

    /**
     * Description: 根据id批量删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    void deleteByIds(Collection<? extends Serializable> ids);

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
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void delete(DeleteSpecification<T> specification);

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void deleteByLambda(LambdaDeleteSpecification<T> specification);

    /**
     * Description: 根据id来获取数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    T get(Serializable id);

    /**
     * Description: 根据唯一数据来查询数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    T getByProperty(String propertyName, Object value);

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
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    T getBySpecification(CriterialQuerySpecification<T> specification);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    T get(QuerySpecification<T> specification);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    T getByLambda(LambdaQuerySpecification<T> specification);

    /**
     * Description: 查询所有数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    List<T> queryAll();

    /**
     * Description: 根据属性查询数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    List<T> queryByProperty(String propertyName, Object value);

    /**
     * Description: 根据条件查询<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    PagerList<T> queryPagerByCriteria(CriteriaQuery<T> criteria, int pageIndex, int pageSize);

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
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    PagerList<T> queryPager(QuerySpecification<T> specification, int pageIndex, int pageSize);

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
    PagerList<T> queryPagerByLambda(LambdaQuerySpecification<T> specification, int pageIndex, int pageSize);

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
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    List<T> queryBySpecification(CriterialQuerySpecification<T> specification);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    List<T> query(QuerySpecification<T> specification);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    List<T> queryByLambda(LambdaQuerySpecification<T> specification);

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
        Predicate toPredicate(Root<T> root, CriteriaDelete<T> delete, CriteriaBuilder criteriaBuilder);
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
        Predicate toPredicate(Root<T> root, CriteriaQuery<T> query, CriteriaBuilder criteriaBuilder);
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
        Predicate toPredicate(Root<T> root, CriteriaUpdate<T> update, CriteriaBuilder criteriaBuilder);
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
    interface QuerySpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialQuerySpecification<T> toSpecification(QueryWrapper<T> wrapper);
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
    interface UpdateSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialUpdateSpecification<T> toSpecification(UpdateWrapper<T> wrapper);
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
    interface DeleteSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialDeleteSpecification<T> toSpecification(DeleteWrapper<T> wrapper);
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
    interface LambdaQuerySpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialQuerySpecification<T> toSpecification(LambdaQueryWrapper<T> wrapper);
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
    interface LambdaUpdateSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialUpdateSpecification<T> toSpecification(LambdaUpdateWrapper<T> wrapper);
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
    interface LambdaDeleteSpecification<T> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialDeleteSpecification<T> toSpecification(LambdaDeleteWrapper<T> wrapper);
    }
}
