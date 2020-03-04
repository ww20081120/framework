/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.utils.PagerList;

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
public interface IBaseDao<T extends BaseEntity> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return <br>
     */
    Serializable save(T entity);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    void saveBatch(List<T> entitys);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pojo <br>
     */
    void update(T pojo);

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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    void updateBatch(List<T> entitys);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    void delete(T entity);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    void deleteById(Serializable id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     */
    void delete(Collection<T> entities);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    void deleteByIds(Collection<? extends Serializable> ids);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    T get(Serializable id);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    T getByProperty(String propertyName, Object value);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @return <br>
     */
    T getByCriteria(DetachedCriteria detachedCriteria);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return T
     * @throws DaoException <br>
     */
    T getByHql(String hql) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    List<T> queryAll();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    List<T> queryByProperty(String propertyName, Object value);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @param pageIndex
     * @param pageSize
     * @return <br>
     */
    PagerList<T> queryPagerByCriteria(DetachedCriteria detachedCriteria, int pageIndex, int pageSize);

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @return <br>
     */
    List<T> queryByCriteria(DetachedCriteria detachedCriteria);

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
     * @param sql
     * @return <br>
     */
    List<T> queryBySql(String sql);

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
     *         <br>
     */
    void clear();

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    void flush();

}
