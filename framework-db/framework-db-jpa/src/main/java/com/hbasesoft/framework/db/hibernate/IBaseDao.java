/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.hibernate;

import java.util.Collection;
import java.util.List;

import com.hbasesoft.framework.db.core.BaseDao;
import com.hbasesoft.framework.db.core.BaseDto;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.utils.PagerList;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;

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
public interface IBaseDao<T extends BaseDto> extends BaseDao<T> {

    /**
     * Description: JPA工厂<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    CriteriaBuilder getCriteriaBuilder();

    /**
     * Description: 根据条件来做更新<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    void updateByCriteria(CriteriaUpdate<T> criteria);

    /**
     * Description: 根据条件删除<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    void deleteByCriteria(CriteriaDelete<T> criteria);
    

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
