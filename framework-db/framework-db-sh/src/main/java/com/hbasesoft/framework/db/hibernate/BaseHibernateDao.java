/**
 * 
 */
package com.hbasesoft.framework.db.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.TransactionManagerHolder;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.utils.DataSourceUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-10-26 <br>
 * @see com.hbasesoft.framework.dao.support.hibernate <br>
 */
@SuppressWarnings({
    "deprecation", "rawtypes"
})
public class BaseHibernateDao implements IGenericBaseDao, ISqlExcutor {

    /**
     * logger
     */
    private static Logger logger = new Logger(BaseHibernateDao.class);

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.dao.support.SqlExcutor#query(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object query(final String sql, final DataParam param) throws DaoException {
        try {

            SQlCheckUtil.checkSql(sql);

            Session session = getSession();

            SQLQuery query = session.createSQLQuery(sql);

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (param.getReturnType() == null) {
                param.setReturnType(void.class);
            }

            // step1:设置参数
            setParamMap(param.getParamMap(), query);

            // step2:设置返回值类型
            final Object callBack = param.getCallback();
            if (callBack != null && callBack instanceof ResultTransformer) {
                query.setResultTransformer((ResultTransformer) callBack);
            }
            else if (param.getBeanType().equals(Map.class)) {
                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
            else {
                Class<?> beanType = param.getBeanType();
                if (Serializable.class.equals(beanType)) {
                    beanType = param.getReturnType();
                }
                query.setResultTransformer(new AutoResultTransformer(beanType));
            }

            boolean isPager = false;
            PagerList resultList = null;

            // step3:设置分页
            if (param.getPageIndex() != -1 && param.getPageSize() != -1
                && List.class.isAssignableFrom(param.getReturnType())) {
                query.setFirstResult((param.getPageIndex() - 1) * param.getPageSize());
                query.setMaxResults(param.getPageSize());

                SQLQuery countQuery = session.createSQLQuery("SELECT COUNT(*) FROM (" + sql + ") QUERY_DATA__");
                setParamMap(param.getParamMap(), countQuery);
                resultList = new PagerList();
                resultList.setPageIndex(param.getPageIndex());
                resultList.setPageSize(param.getPageSize());
                resultList.setTotalCount(Long.valueOf(countQuery.uniqueResult().toString()));
                isPager = true;
            }

            if (isPager) {
                resultList.addAll(query.list());
                return resultList;
            }
            else if (List.class.isAssignableFrom(param.getReturnType())) {
                return query.list();
            }
            else {
                return query.uniqueResult();
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.QUERY_ERROR_10010, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramMap <br>
     * @param query <br>
     * @throws DaoException <br>
     */
    private void setParamMap(Map<String, Object> paramMap, Query query) throws DaoException {
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, Object> entry : paramMap.entrySet()) {
                Object obj = entry.getValue();

                // 这里考虑传入的参数是什么类型，不同类型使用的方法不同
                if (obj instanceof Collection<?>) {
                    query.setParameterList(entry.getKey(), (Collection<?>) obj);
                }
                else if (obj != null && obj.getClass().isArray()) {
                    if (!(obj instanceof Object[])) {
                        throw new DaoException(ErrorCodeDef.LIST_PARAM_ERROR_10040);
                    }
                    query.setParameterList(entry.getKey(), (Object[]) obj);
                }
                else {
                    query.setParameter(entry.getKey(), obj);
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.dao.support.SqlExcutor#excuteSql(java.lang. String, java.util.Map)
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);
            Session session = getSession();
            SQLQuery query = session.createSQLQuery(sql);
            setParamMap(param.getParamMap(), query);
            return query.executeUpdate();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.EXECUTE_ERROR_10011, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.dao.support.SqlExcutor#batchExcuteSql(java.lang. String[])
     */
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) throws DaoException {
        try {
            Session session = getSession();
            int[] result = new int[sqls.length];
            SQLQuery query;
            for (int i = 0; i < sqls.length; i++) {
                query = session.createSQLQuery(sqls[i]);
                setParamMap(param.getParamMap(), query);
                result[i] = query.executeUpdate();
            }
            return result;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR_10012, e);
        }
    }

    /**
     * 创建Criteria对象带属性比较
     *
     * @param <T>
     * @param entityClass
     * @param criterions
     * @return
     */
    private <T> Criteria createCriteria(Class<T> entityClass, Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * 创建Criteria对象，有排序功能。
     *
     * @param <T>
     * @param entityClass
     * @param orderBy
     * @param isAsc
     * @param criterions
     * @return
     */
    private <T> Criteria createCriteria(Class<T> entityClass, boolean isAsc, Criterion... criterions) {
        Criteria criteria = createCriteria(entityClass, criterions);
        if (isAsc) {
            criteria.addOrder(Order.asc("asc"));
        }
        else {
            criteria.addOrder(Order.desc("desc"));
        }
        return criteria;
    }

    protected Session getSession() {
        // 事务必须是开启的(Required)，否则获取不到
        return TransactionManagerHolder.getSessionFactory().getCurrentSession();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @return
     * @throws DaoException <br>
     */
    @Override
    public <T> Serializable save(T entity) throws DaoException {
        try {
            Serializable id = getSession().save(entity);
            getSession().flush();
            logger.info("保存实体成功," + entity.getClass().getName());
            return id;
        }
        catch (Exception e) {
            logger.error("保存实体异常", e);
            throw new DaoException(ErrorCodeDef.SAVE_ERROR_10013, e);
        }
    }

    /**
     * 根据传入的实体添加或更新对象
     *
     * @param <T>
     * @param entity
     */
    @Override
    public <T> void saveOrUpdate(T entity) throws DaoException {
        try {
            getSession().saveOrUpdate(entity);
            getSession().flush();
            logger.info("添加或更新成功," + entity.getClass().getName());
        }
        catch (RuntimeException e) {
            logger.error("添加或更新异常", e);
            throw new DaoException(ErrorCodeDef.SAVE_ERROR_10013, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity
     * @throws DaoException <br>
     */
    @Override
    public <T> void delete(T entity) throws DaoException {
        try {
            getSession().delete(entity);
            getSession().flush();
            logger.info("删除成功," + entity.getClass().getName());
        }
        catch (RuntimeException e) {
            logger.error("删除异常", e);
            throw e;
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys
     * @throws DaoException <br>
     */
    @Override
    public <T> void batchSave(List<T> entitys) throws DaoException {
        for (int i = 0; i < entitys.size(); i++) {
            getSession().save(entitys.get(i));
            if (i % 20 == 0) {
                // 20个对象后才清理缓存，写入数据库
                getSession().flush();
                getSession().clear();
            }
        }
        // 最后清理一下----防止大于20小于40的不保存
        getSession().flush();
        getSession().clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param class1
     * @param id
     * @return
     * @throws DaoException <br>
     */
    @Override
    public <T> T get(Class<T> entityClass, Serializable id) throws DaoException {
        Assert.notNull(id, ErrorCodeDef.ID_IS_NULL);
        return (T) getSession().get(entityClass, id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityName
     * @param id
     * @return
     * @throws DaoException <br>
     */
    @Override
    public <T> T getEntity(Class<T> entityName, Serializable id) throws DaoException {
        Assert.notNull(id, ErrorCodeDef.ID_IS_NULL);
        T t = (T) getSession().get(entityName, id);
        if (t != null) {
            getSession().flush();
        }
        return t;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @param propertyName
     * @param value
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T findUniqueByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException {
        Assert.notEmpty(propertyName, ErrorCodeDef.DAO_PROPERTY_IS_EMPTY);
        return (T) createCriteria(entityClass, Restrictions.eq(propertyName, value)).uniqueResult();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @param propertyName
     * @param value
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByProperty(Class<T> entityClass, String propertyName, Object value) throws DaoException {
        Assert.notEmpty(propertyName, ErrorCodeDef.DAO_PROPERTY_IS_EMPTY);
        return (List<T>) createCriteria(entityClass, Restrictions.eq(propertyName, value)).list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> loadAll(Class<T> entityClass) throws DaoException {
        return createCriteria(entityClass).list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityName
     * @param id
     * @throws DaoException <br>
     */
    @Override
    public <T> void deleteEntityById(Class<T> entityName, Serializable id) throws DaoException {
        Assert.notNull(id, ErrorCodeDef.ID_IS_NULL);
        delete(get(entityName, id));
        getSession().flush();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities
     * @throws DaoException <br>
     */
    @Override
    public <T> void deleteAllEntitie(Collection<T> entities) throws DaoException {
        for (Object entity : entities) {
            getSession().delete(entity);
        }
        getSession().flush();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids
     * @throws DaoException <br>
     */
    @Override
    public <T> void deleteAllEntitiesByIds(Class<T> entityName, Collection<String> ids) throws DaoException {
        Assert.notEmpty(ids, ErrorCodeDef.ID_IS_NULL);
        for (String id : ids) {
            getSession().delete(get(entityName, id));
        }
        getSession().flush();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pojo
     * @throws DaoException <br>
     */
    @Override
    public <T> void updateEntity(T pojo) throws DaoException {
        getSession().update(pojo);
        getSession().flush();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByQueryString(String hql) throws DaoException {
        Query queryObject = getSession().createQuery(hql);
        List<T> list = queryObject.list();
        if (list.size() > 0) {
            getSession().flush();
        }
        return list;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @return
     * @throws DaoException <br>
     */
    @Override
    public int updateBySqlString(String sql) throws DaoException {
        Query querys = getSession().createSQLQuery(sql);
        return querys.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param query
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findListbySql(String sql) throws DaoException {
        Query querys = getSession().createSQLQuery(sql);
        return querys.list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @param propertyName
     * @param value
     * @param isAsc
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findByPropertyisOrder(Class<T> entityClass, String propertyName, Object value, boolean isAsc)
        throws DaoException {
        return createCriteria(entityClass, isAsc, Restrictions.eq(propertyName, value)).list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T singleResult(String hql) throws DaoException {
        return (T) getSession().createQuery(hql).uniqueResult();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param clazz
     * @param cq
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> PagerList<T> getPageList(DetachedCriteria detachedCriteria, int pageIndex, int pageSize)
        throws DaoException {

        Criteria criteria = detachedCriteria.getExecutableCriteria(getSession());

        // 查询分页总数
        CriteriaImpl impl = (CriteriaImpl) criteria;
        Projection projection = impl.getProjection();
        Long allCounts = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();

        criteria.setProjection(projection);
        criteria.setFirstResult((pageIndex - 1) * pageSize);
        criteria.setMaxResults(pageSize);

        PagerList<T> resultList = new PagerList<T>();
        resultList.setPageIndex(pageIndex);
        resultList.setPageSize(pageSize);
        resultList.setTotalCount(allCounts);

        if (allCounts > 0) {
            resultList.addAll(criteria.list());
        }
        return resultList;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param cq
     * @param ispage
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> getListByCriteriaQuery(DetachedCriteria detachedCriteria) throws DaoException {
        return detachedCriteria.getExecutableCriteria(getSession()).list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> findHql(String hql, Object... param) throws DaoException {
        Query q = getSession().createQuery(hql);
        if (param != null && param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                q.setParameter(i, param[i]);
            }
        }
        return q.list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param procedureSql
     * @param params
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> executeProcedure(String procedureSql, Object... params) throws DaoException {
        SQLQuery sqlQuery = getSession().createSQLQuery(procedureSql);

        for (int i = 0; i < params.length; i++) {
            sqlQuery.setParameter(i, params[i]);
        }
        return sqlQuery.list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCriteriaQuery(DetachedCriteria detachedCriteria) throws DaoException {
        return (T) detachedCriteria.getExecutableCriteria(getSession()).uniqueResult();
    }

    @Override
    public <T> void batchExecute(String sql, Collection<Object[]> objcts, int commitNumber) throws DaoException {
        commitNumber = commitNumber == 0 ? 1000 : commitNumber;
        Connection conn = null;
        try {
            conn = DataSourceUtil.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            int i = 0;
            for (Object[] object : objcts) {
                i++;
                for (int j = 0; j < object.length; j++) {
                    stmt.setObject(j + 1, object[j]);
                }
                stmt.addBatch();
                if (i % commitNumber == 0) {
                    stmt.executeBatch();
                    conn.commit();
                }
            }
            stmt.executeBatch();
            conn.commit();
        }
        catch (SQLException e) {
            throw new DaoException(e.getErrorCode(), e);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    throw new DaoException(e.getErrorCode(), e);
                }
            }
        }

    }
}
