/**
 * 
 */
package com.hbasesoft.framework.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.dao.support <br>
 */
public interface IGenericBaseDao {

    public <T> Serializable save(T entity) throws DaoException;

    public <T> void delete(T entity) throws DaoException;

    public <T> void batchSave(List<T> entitys) throws DaoException;

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T get(Class<T> class1, Serializable id) throws DaoException;

    /**
     * 根据实体名称和主键获取实体
     * 
     * @param <T>
     * @param entityName
     * @param id
     * @return
     */
    public <T> T getEntity(Class<T> entityName, Serializable id) throws DaoException;

    /**
     * 根据实体名称和字段名称和字段值获取唯一记录
     * 
     * @param <T>
     * @param entityClass
     * @param propertyName
     * @param value
     * @return
     */
    public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException;

    /**
     * 按属性查找对象列表.
     */
    public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException;

    /**
     * 加载全部实体
     * 
     * @param <T>
     * @param entityClass
     * @return
     */
    public <T> List<T> loadAll(final Class<T> entityClass) throws DaoException;

    /**
     * 删除实体主键删除
     * 
     * @param <T>
     * @param entities
     */
    public <T> void deleteEntityById(Class<T> entityName, Serializable id) throws DaoException;

    /**
     * 删除实体集合
     * 
     * @param <T>
     * @param entities
     */
    public <T> void deleteAllEntitie(Collection<T> entities) throws DaoException;

    /**
     * Description: 根据Id集合删除实体<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    public <T> void deleteAllEntitiesByIds(Class<T> entityName, Collection<String> ids) throws DaoException;

    /**
     * 更新指定的实体
     * 
     * @param <T>
     * @param pojo
     */
    public <T> void updateEntity(T pojo) throws DaoException;

    /**
     * 通过hql 查询语句查找对象
     * 
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findByQueryString(String hql) throws DaoException;

    /**
     * 根据sql更新
     * 
     * @param query
     * @return
     */
    public int updateBySqlString(String sql) throws DaoException;

    /**
     * 根据sql查找List
     * 
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findListbySql(String sql) throws DaoException;

    /**
     * 通过属性称获取实体带排序
     * 
     * @param <T>
     * @param clas
     * @return
     */
    public <T> List<T> findByPropertyisOrder(Class<T> entityClass, String propertyName, Object value, boolean isAsc)
        throws DaoException;

    public <T> T singleResult(String hql) throws DaoException;

    /**
     * cq方式分页
     * 
     * @param cq
     * @param isOffset
     * @return
     */
    public <T> List<T> getPageList(DetachedCriteria detachedCriteria, int pageIndex, int pageSize) throws DaoException;

    /**
     * 通过cq获取全部实体
     * 
     * @param <T>
     * @param cq
     * @return
     */
    public <T> List<T> getListByCriteriaQuery(DetachedCriteria detachedCriteria) throws DaoException;

    /**
     * 通过hql 查询语句查找对象
     * 
     * @param <T>
     * @param query
     * @return
     */
    public <T> List<T> findHql(String hql, Object... param) throws DaoException;

    // update-begin--Author:luobaoli Date:20150708 for：增加执行存储过程方法
    /**
     * 执行存储过程
     * 
     * @param executeSql
     * @param params
     * @return
     */
    public <T> List<T> executeProcedure(String procedureSql, Object... params) throws DaoException;
}
