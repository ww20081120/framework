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

import javax.management.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;

import org.apache.commons.collections.MapUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.persister.collection.mutation.RowMutationOperations.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.TransactionManagerHolder;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
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
    "rawtypes", "deprecation", "unchecked"
})
public class BaseHibernateDao implements IBaseDao, ISqlExcutor {

    /** Number */
    private static final int NUM_100 = 100;

    /**
     * logger
     */
    private static Logger logger = new Logger(BaseHibernateDao.class);

    /** entity class */
    private Class<?> entityClazz;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public CriteriaBuilder getCriteriaBuilder() {
        return getSession().getCriteriaBuilder();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @Override
    public Object query(final String sql, final DataParam param) throws DaoException {
        try {

            SQlCheckUtil.checkSql(sql);

            Session session = getSession();
            session.flush();

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
                ResultTransformer rt = (ResultTransformer) callBack;

                query.setResultTransformer(rt);
            }
            else if (param.getBeanType().equals(Map.class)) {
                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
            else {
                Class<?> beanType = param.getBeanType();
                if (Serializable.class.equals(beanType)) {
                    beanType = param.getReturnType();
                    if (entityClazz != null && List.class.isAssignableFrom(param.getReturnType())) {
                        beanType = entityClazz;
                    }
                }
                query.setResultTransformer(new AutoResultTransformer(beanType));
            }

            boolean isPager = false;
            PagerList resultList = null;

            if (param.getPageIndex() == 0) {
                param.setPageIndex(1);
            }

            // step3:设置分页
            if (param.getPageIndex() != -1 && param.getPageSize() != -1
                && List.class.isAssignableFrom(param.getReturnType())) {
                query.setFirstResult((param.getPageIndex() - 1) * param.getPageSize());
                query.setMaxResults(param.getPageSize());

                SQLQuery countQuery = session.createSQLQuery("SELECT COUNT(1) FROM (" + sql + ") QUERY_DATA__");
                setParamMap(param.getParamMap(), countQuery);
                resultList = new PagerList();
                resultList.setPageIndex(param.getPageIndex());
                resultList.setPageSize(param.getPageSize());
                resultList.setTotalCount(Long.valueOf(countQuery.uniqueResult().toString()));
                isPager = true;
            }

            if (isPager) {
                if (resultList.getTotalCount() > 0
                    && (resultList.getPageIndex() - 1) * resultList.getPageSize() < resultList.getTotalCount()) {
                    resultList.addAll(query.list());
                }
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
            throw new DaoException(ErrorCodeDef.QUERY_ERROR, e);
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
    private void setParamMap(final Map<String, Object> paramMap, final Query query) throws DaoException {
        if (MapUtils.isNotEmpty(paramMap)) {
            for (Entry<String, Object> entry : paramMap.entrySet()) {
                Object obj = entry.getValue();

                // 这里考虑传入的参数是什么类型，不同类型使用的方法不同
                if (obj instanceof Collection<?>) {
                    query.setParameterList(entry.getKey(), (Collection<?>) obj);
                }
                else if (obj != null && obj.getClass().isArray() && obj instanceof Object[]) {
                    Object[] os = (Object[]) obj;

                    query.setParameterList(entry.getKey(), os);
                }
                else {
                    query.setParameter(entry.getKey(), obj);
                }
            }
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);
            Session session = getSession();
            session.flush();

            SQLQuery query = session.createSQLQuery(sql);
            setParamMap(param.getParamMap(), query);
            return query.executeUpdate();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.EXECUTE_ERROR, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sqls
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) throws DaoException {
        try {
            Session session = getSession();
            session.flush();

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
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR, e);
        }
    }

    /**
     * 创建Criteria对象带属性比较
     *
     * @param <T>
     * @param entityClass
     * @param criterions
     * @return Criteria
     */
    private <T> Criteria createCriteria(final Class<T> entityClass, final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
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
    public <T> Serializable save(final T entity) throws DaoException {
        try {
            Serializable id = getSession().save(entity);
            logger.debug("保存实体成功," + entity.getClass().getName());
            return id;
        }
        catch (Exception e) {
            logger.error("保存实体异常", e);
            throw new DaoException(ErrorCodeDef.SAVE_ERROR, e);
        }
    }

    /**
     * 根据传入的实体添加或更新对象
     *
     * @param <T>
     * @param entity
     */
    @Override
    public <T> void saveOrUpdate(final T entity) throws DaoException {
        try {
            getSession().saveOrUpdate(entity);
            logger.debug("添加或更新成功," + entity.getClass().getName());
        }
        catch (RuntimeException e) {
            logger.error("添加或更新异常", e);
            throw new DaoException(ErrorCodeDef.SAVE_ERROR, e);
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
    public <T> void delete(final T entity) throws DaoException {
        try {
            getSession().delete(entity);
            logger.debug("删除成功," + entity.getClass().getName());
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
    public <T> void batchSave(final List<T> entitys) throws DaoException {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        for (int i = 0; i < entitys.size(); i++) {
            getSession().save(entitys.get(i));
            if (i % NUM_100 == 0) {
                // 1000个对象后才清理缓存，写入数据库
                getSession().flush();
                getSession().clear();
            }
        }
        // 最后清理一下----防止大于1000小于2000的不保存
        getSession().flush();
        getSession().clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClass
     * @param id
     * @param <T> T
     * @return T
     * @throws DaoException <br>
     */
    @Override
    public <T> T get(final Class<T> entityClass, final Serializable id) throws DaoException {
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
    public <T> T getEntity(final Class<T> entityName, final Serializable id) throws DaoException {
        return get(entityName, id);
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

    @Override
    public <T> T findUniqueByProperty(final Class<T> entityClass, final String propertyName, final Object value)
        throws DaoException {
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

    @Override
    public <T> List<T> findByProperty(final Class<T> entityClass, final String propertyName, final Object value)
        throws DaoException {
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

    @Override
    public <T> List<T> loadAll(final Class<T> entityClass) throws DaoException {
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
    public <T> void deleteEntityById(final Class<T> entityName, final Serializable id) throws DaoException {
        Assert.notNull(id, ErrorCodeDef.ID_IS_NULL);
        T entity = get(entityName, id);
        if (entity != null) {
            delete(entity);
        }
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
    public <T> void deleteAllEntitie(final Collection<T> entities) throws DaoException {
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
    public <T> void deleteAllEntitiesByIds(final Class<T> entityName, final Collection<? extends Serializable> ids)
        throws DaoException {
        Assert.notEmpty(ids, ErrorCodeDef.ID_IS_NULL);
        for (Serializable id : ids) {
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
    public <T> void updateEntity(final T pojo) throws DaoException {
        getSession().update(pojo);
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

    @Override
    public <T> List<T> findByQueryString(final String hql) throws DaoException {
        Query queryObject = getSession().createQuery(hql);
        return queryObject.list();
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
    public int updateBySqlString(final String sql) throws DaoException {
        Session session = getSession();
        session.flush();

        Query querys = session.createSQLQuery(sql);
        return querys.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param <T> T
     * @return T
     * @throws DaoException <br>
     */

    @Override
    public <T> List<T> findListbySql(final String sql) throws DaoException {
        Session session = getSession();
        session.flush();
        Query querys = session.createSQLQuery(sql);
        return querys.list();
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

    @Override
    public <T> T singleResult(final String hql) throws DaoException {
        return (T) getSession().createQuery(hql).uniqueResult();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @param pi
     * @param pageSize
     * @param <T> T
     * @return T
     * @throws DaoException <br>
     */
    @Override
    public <T> PagerList<T> getPageList(final DetachedCriteria detachedCriteria, final int pi, final int pageSize)
        throws DaoException {
        int pageIndex = pi;
        if (pi == 0) {
            pageIndex = 1;
        }

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
        if (allCounts == null) {
            allCounts = 0L;
        }
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
     * @param detachedCriteria
     * @param <T> T
     * @return T
     * @throws DaoException <br>
     */
    @Override
    public <T> List<T> getListByCriteriaQuery(final DetachedCriteria detachedCriteria) throws DaoException {
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

    @Override
    public <T> List<T> findHql(final String hql, final Object... param) throws DaoException {
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

    @Override
    public <T> List<T> executeProcedure(final String procedureSql, final Object... params) throws DaoException {
        Session session = getSession();
        session.flush();
        SQLQuery sqlQuery = session.createSQLQuery(procedureSql);

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

    @Override
    public <T> T getCriteriaQuery(final DetachedCriteria detachedCriteria) throws DaoException {
        return (T) detachedCriteria.getExecutableCriteria(getSession()).uniqueResult();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param objcts
     * @param commitNumber
     * @throws DaoException <br>
     */
    @Override
    public <T> void batchExecute(final String sql, final Collection<Object[]> objcts, final int commitNumber)
        throws DaoException {
        Session session = getSession();
        session.flush();

        session.doWork(new Work() {

            @Override
            public void execute(final Connection connection) throws SQLException {
                PreparedStatement stmt = null;
                try {
                    stmt = connection.prepareStatement(sql);
                    connection.setAutoCommit(false);
                    int i = 0;
                    for (Object[] object : objcts) {
                        i++;
                        for (int j = 0; j < object.length; j++) {
                            stmt.setObject(j + 1, object[j]);
                        }
                        stmt.addBatch();
                        if (i % commitNumber == 0) {
                            stmt.executeBatch();
                            connection.commit();
                        }
                    }
                    stmt.executeBatch();
                    connection.commit();
                }
                finally {
                    if (stmt != null) {
                        stmt.close();
                    }
                }
            }
        });
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void clear() {
        try {
            getSession().clear();
        }
        catch (Exception e) {
            throw new DaoException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Override
    public void flush() {
        try {
            getSession().flush();
        }
        catch (Exception e) {
            throw new DaoException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     * @param <T> T
     */
    public <T> void saveBatch(final List<T> entitys) {
        this.batchSave(entitys);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     * @param <T> T
     */
    public <T> void updateBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        for (int i = 0; i < entitys.size(); i++) {
            getSession().update(entitys.get(i));
            if (i % NUM_100 == 0) {
                // 1000个对象后才清理缓存，写入数据库
                getSession().flush();
                getSession().clear();
            }
        }
        // 最后清理一下----防止大于1000小于2000的不保存
        getSession().flush();
        getSession().clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param pojo <br>
     * @param <T> T
     */
    public <T> void update(final T pojo) {
        this.updateEntity(pojo);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @return <br>
     */
    public int updateBySql(final String sql) {
        return this.updateBySqlString(sql);
    };

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param criteria <br>
     */
    public <T> void updateByCriteria(final CriteriaUpdate<T> criteria) {
        org.hibernate.query.Query query = getSession().createQuery(criteria);
        query.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    public void deleteById(final Serializable id) {
        this.deleteEntityById(getEntityClazz(), id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     * @param <T> T
     */
    public <T> void delete(final Collection<T> entities) {
        this.deleteAllEntitie(entities);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        this.deleteAllEntitiesByIds(getEntityClazz(), ids);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T>
     * @param criteria <br>
     */
    public <T> void deleteByCriteria(final CriteriaDelete<T> criteria) {
        org.hibernate.query.Query query = getSession().createQuery(criteria);
        query.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @param <T> T
     * @return <br>
     */
    public <T> T get(final Serializable id) {
        return (T) this.get(getEntityClazz(), id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @param <T> T
     * @return <br>
     */
    public <T> T getByProperty(final String propertyName, final Object value) {
        return (T) this.findUniqueByProperty(getEntityClazz(), propertyName, value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @param <T> T
     * @return <br>
     */
    public <T> T getByCriteria(final DetachedCriteria detachedCriteria) {
        return this.getCriteriaQuery(detachedCriteria);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param <T> T
     * @return <br>
     */
    public <T> T getByHql(final String hql) {
        return this.singleResult(hql);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryAll() {
        return (List<T>) this.loadAll(getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryByProperty(final String propertyName, final Object value) {
        return (List<T>) this.findByProperty(getEntityClazz(), propertyName, value);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @param pageIndex
     * @param pageSize
     * @param <T> T
     * @return <br>
     */
    public <T> PagerList<T> queryPagerByCriteria(final DetachedCriteria detachedCriteria, final int pageIndex,
        final int pageSize) {
        return this.getPageList(detachedCriteria, pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param detachedCriteria
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryByCriteria(final DetachedCriteria detachedCriteria) {
        return this.getListByCriteriaQuery(detachedCriteria);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryBySql(final String sql) {

        Session session = getSession();
        session.flush();
        Query query = session.createSQLQuery(sql);

        if (getEntityClazz().equals(Map.class)) {
            query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        }
        else {
            query.setResultTransformer(new AutoResultTransformer(getEntityClazz()));
        }
        return query.list();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param param
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryByHqlParam(final String hql, final Object... param) {
        return findHql(hql, param);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param <T> T
     * @return <br>
     */
    public <T> List<T> queryByHql(final String hql) {
        return this.findByQueryString(hql);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param objcts
     * @param commitNumber <br>
     */
    public void executeBatch(final String sql, final Collection<Object[]> objcts, final int commitNumber) {
        this.batchExecute(sql, objcts, commitNumber);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    private Class<?> getEntityClazz() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        return entityClazz;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
     */
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = entityClazz;
    }

}
