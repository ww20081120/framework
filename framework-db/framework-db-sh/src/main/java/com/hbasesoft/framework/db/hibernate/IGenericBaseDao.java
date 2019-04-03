/**
 * 
 */
package com.hbasesoft.framework.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.utils.PagerList;

/**
 * <Description> 
 * 请使用com.hbasesoft.framework.db.hibernate.IBaseDAO<T> 代替现在这个类 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate.IBaseDAO<T> <br>
 */
@Deprecated
public interface IGenericBaseDao {

    <T> Serializable save(T entity) throws DaoException;

    <T> void delete(T entity) throws DaoException;

    <T> void batchSave(List<T> entitys) throws DaoException;

    <T> void batchExecute(String sql, Collection<Object[]> objcts, int commitNumber) throws DaoException;

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    <T> T get(Class<T> class1, Serializable id) throws DaoException;

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    @Deprecated
    <T> T getEntity(Class<T> entityName, Serializable id) throws DaoException;

    /**
     * 根据实体名称和字段名称和字段值获取唯一记录
     * 
     * @param <T>
     * @param entityClass
     * @param propertyName
     * @param value
     * @return
     */
    <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException;

    /**
     * 按属性查找对象列表.
     */
    <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException;

    /**
     * 加载全部实体
     * 
     * @param <T>
     * @param entityClass
     * @return
     */
    <T> List<T> loadAll(final Class<T> entityClass) throws DaoException;

    /**
     * 删除实体主键删除
     * 
     * @param <T>
     * @param entities
     */
    <T> void deleteEntityById(Class<T> entityName, Serializable id) throws DaoException;

    /**
     * 删除实体集合
     * 
     * @param <T>
     * @param entities
     */
    <T> void deleteAllEntitie(Collection<T> entities) throws DaoException;

    /**
     * Description: 根据Id集合删除实体<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    <T> void deleteAllEntitiesByIds(Class<T> entityName, Collection<? extends Serializable> ids) throws DaoException;

    /**
     * 更新指定的实体
     * 
     * @param <T>
     * @param pojo
     */
    <T> void updateEntity(T pojo) throws DaoException;

    /**
     * 通过hql 查询语句查找对象
     * 
     * @param <T>
     * @param query
     * @return
     */
    <T> List<T> findByQueryString(String hql) throws DaoException;

    /**
     * 根据sql更新
     * 
     * @param query
     * @return
     */
    int updateBySqlString(String sql) throws DaoException;

    /**
     * 根据sql查找List
     * 
     * @param <T>
     * @param query
     * @return
     */
    <T> List<T> findListbySql(String sql) throws DaoException;

    <T> T singleResult(String hql) throws DaoException;

    /**
     * cq方式分页
     * 
     * @param cq
     * @param isOffset
     * @return
     */
    <T> PagerList<T> getPageList(DetachedCriteria detachedCriteria, int pageIndex, int pageSize) throws DaoException;

    /**
     * 通过cq获取全部实体
     * 
     * @param <T>
     * @param cq
     * @return
     */
    <T> List<T> getListByCriteriaQuery(DetachedCriteria detachedCriteria) throws DaoException;

    <T> T getCriteriaQuery(DetachedCriteria detachedCriteria) throws DaoException;

    /**
     * 通过hql 查询语句查找对象
     * 
     * @param <T>
     * @param query
     * @return
     */
    <T> List<T> findHql(String hql, Object... param) throws DaoException;

    // update-begin--Author:luobaoli Date:20150708 for：增加执行存储过程方法
    /**
     * 执行存储过程
     * 
     * @param executeSql
     * @param params
     * @return
     */
    <T> List<T> executeProcedure(String procedureSql, Object... params) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @throws DaoException <br>
     */
    <T> void saveOrUpdate(T entity) throws DaoException;

    void clear();

    void flush();
}
