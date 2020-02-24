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

    Serializable save(T entity);

    void saveBatch(List<T> entitys);

    void update(T pojo);

    int updateBySql(String sql) throws DaoException;
    
    void updateBatch(List<T> entitys);

    void delete(T entity);

    void deleteById(Serializable id);

    void delete(Collection<T> entities);

    void deleteByIds(Collection<? extends Serializable> ids);

    T get(Serializable id);

    T getByProperty(String propertyName, Object value);

    T getByCriteria(DetachedCriteria detachedCriteria);

    T getByHql(String hql) throws DaoException;

    List<T> queryAll();

    List<T> queryByProperty(String propertyName, Object value);

    PagerList<T> queryPagerByCriteria(DetachedCriteria detachedCriteria, int pageIndex, int pageSize);

    List<T> queryByCriteria(DetachedCriteria detachedCriteria);

    List<T> executeProcedure(String procedureSql, Object... params);

    List<T> queryBySql(String sql);

    List<T> queryByHqlParam(String hql, Object... param);

    List<T> queryByHql(String hql);

    void executeBatch(String sql, Collection<Object[]> objcts, int commitNumber);

    void clear();

    void flush();

}
