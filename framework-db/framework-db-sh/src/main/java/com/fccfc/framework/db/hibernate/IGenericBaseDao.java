/**
 * 
 */
package com.fccfc.framework.db.hibernate;

import java.io.Serializable;
import java.util.List;

import com.fccfc.framework.db.core.BaseEntity;
import com.fccfc.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年10月23日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.dao.support <br>
 */
public interface IGenericBaseDao {

    /**
     * 保存
     * 
     * @param t <br>
     * @throws DaoException <br>
     */
    void save(BaseEntity t) throws DaoException;

    /**
     * 根据Id查询
     * 
     * @param entityClass <br>
     * @param id <br>
     * @param <T> <br>
     * @return <br>
     * @throws DaoException <br>
     */
    <T extends BaseEntity> T getById(Class<T> entityClass, final Serializable id) throws DaoException;

    /**
     * 根据实体查询
     * 
     * @param entity <br>
     * @return <br>
     * @param <T> <br>
     * @throws DaoException <br>
     */
    <T extends BaseEntity> T getByEntity(T entity) throws DaoException;

    /**
     * 更新实体
     * 
     * @param entity <br>
     * @throws DaoException <br>
     */
    void update(BaseEntity entity) throws DaoException;

    /**
     * 删除实体
     * 
     * @param entity <br>
     * @throws DaoException <br>
     */
    void delete(BaseEntity entity) throws DaoException;

    /**
     * 根据Id删除实体
     * 
     * @param entityClass <br>
     * @param id <br>
     * @param <T> <br>
     * @throws DaoException <br>
     */
    <T extends BaseEntity> void deleteById(Class<T> entityClass, final Serializable id) throws DaoException;

    /**
     * 根据实体查询
     * 
     * @param <T> <br>
     * @param entityClass <br>
     * @return <br>
     * @throws DaoException <br>
     */
    <T extends BaseEntity> List<T> selectList(Class<T> entityClass) throws DaoException;
}
