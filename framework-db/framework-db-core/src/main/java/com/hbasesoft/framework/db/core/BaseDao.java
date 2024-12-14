/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
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
public interface BaseDao<T extends BaseEntity> extends ISqlExcutor {

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

    /**
     * <Description> <br>
     * 
     * @param <T> 参数类型
     * @param <M> 返参类型
     * @author ww200<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2024年5月6日 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.db.hibernate <br>
     */
    @FunctionalInterface
    interface LambdaQuerySpecification<T, M> {

        /**
         * Description: <br>
         * 
         * @author 王伟<br>
         * @taskId <br>
         * @param wrapper
         * @return <br>
         */
        CriterialQuerySpecification<T> toSpecification(LambdaQueryWrapper<T, M> wrapper);
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
     * @param specification <br>
     */
    default void delete(DeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new DeleteWrapper<>()));
    }

    /**
     * Description: 删除数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    void delete(T entity);

    /**
     * Description: 批量删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     */
    void deleteBatch(Collection<T> entities);

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    void deleteByCriteria(CriteriaDelete<T> criteria);

    /**
     * Description: 根据id来删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void deleteById(Serializable id);

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
     * @param specification <br>
     */
    default void deleteByLambda(LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new LambdaDeleteWrapper<>()));
    }

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void deleteBySpecification(CriterialDeleteSpecification<T> specification);

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    default T get(QuerySpecification<T> specification) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

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
    default <M> M get(QuerySpecification<T> specification, Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

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
     * @param clazz
     * @param <M>
     * @return <br>
     */
    default <M> M getByLambda(LambdaQuerySpecification<T, M> specification, Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    default T getByLambda(LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

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
     * @param specification
     * @return <br>
     */
    default List<T> query(QuerySpecification<T> specification) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

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
    default <M> List<M> query(QuerySpecification<T> specification, Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: 查询所有数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    List<T> queryAll();

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
     * @param clazz
     * @param <M>
     * @return <br>
     */
    default <M> List<M> queryByLambda(LambdaQuerySpecification<T, M> specification, Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: 根据条件查询 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    default List<T> queryByLambda(LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

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
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    default PagerList<T> queryPager(QuerySpecification<T> specification, int pageIndex, int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param <M> M
     * @param clazz
     * @return <br>
     */
    default <M> PagerList<M> queryPager(QuerySpecification<T> specification, int pageIndex, int pageSize,
        Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize,
            clazz);
    }

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
     * @param <M> M
     * @param clazz
     * @return <br>
     */
    default <M> PagerList<M> queryPagerByLambda(LambdaQuerySpecification<T, M> specification, int pageIndex,
        int pageSize, Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex, pageSize,
            clazz);
    }

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
    default PagerList<T> queryPagerByLambda(LambdaQuerySpecification<T, T> specification, int pageIndex, int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
            pageSize);
    }

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
     * Description: 保存数据<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     */
    void save(T entity);

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
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    default void update(UpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new UpdateWrapper<>()));
    }

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
    default void updateByLambda(LambdaUpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new LambdaUpdateWrapper<>()));
    }

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    void updateBySpecification(CriterialUpdateSpecification<T> specification);

}
