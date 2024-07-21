/**
 * 
 */
package com.hbasesoft.framework.db.hibernate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.jpa.spi.NativeQueryMapTransformer;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.TransactionManagerHolder;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.criteria.DeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaDeleteWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaQueryWrapper;
import com.hbasesoft.framework.db.core.criteria.LambdaUpdateWrapper;
import com.hbasesoft.framework.db.core.criteria.QueryWrapper;
import com.hbasesoft.framework.db.core.criteria.UpdateWrapper;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
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
 * @param <T> bean类型
 * @author ww200<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.hibernate <br>
 */
public class BaseHibernateDao<T extends BaseEntity> implements BaseJpaDao<T>, ISqlExcutor {

    /** 匿名 */
    private static final String ALIAS = "QUERY_DATA__";

    /** 最大分页数 */
    private static final int MAX_SIZE = 1000;

    /** Number */
    private static final int NUM_100 = 100;

    /** entity class */
    private Class<T> entityClazz;

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
            NativeQuery<?> query;
            for (int i = 0; i < sqls.length; i++) {
                query = session.createNativeQuery(sqls[i], Object.class);
                setParamMap(param.getParamMap(), query);
                result[i] = query.executeUpdate();
            }
            return result;
        }
        catch (Exception e) {
            LoggerUtil.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR, e);
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
     * @return <br>
     */
    @Override
    public CriteriaBuilder criteriaBuilder() {
        return getSession().getCriteriaBuilder();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void delete(final BaseEntity entity) {
        getSession().remove(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void delete(final DeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new DeleteWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entities <br>
     */
    @Override
    public void deleteBatch(final Collection<T> entities) {
        if (entities.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        Session session = getSession();
        int i = 0;
        for (Object obj : entities) {
            session.remove(obj);
            if (i++ % NUM_100 == 0) {
                // 1000个对象后才清理缓存，写入数据库
                session.flush();
                session.clear();
            }
        }
        // 最后清理一下----防止大于1000小于2000的不保存
        session.flush();
        session.clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    @Override
    public void deleteByCriteria(final CriteriaDelete<T> criteria) {
        MutationQuery query = getSession().createMutationQuery(criteria);
        query.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    @Override
    public void deleteById(final Serializable id) {
        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        Field primaryKeyField = findPrimaryKeyField(entityType);
        if (primaryKeyField == null) {
            throw new IllegalStateException(
                "Could not find a primary key field annotated with @Id in the given entity type.");
        }

        // 构建HQL或Criteria删除语句
        String hql = new StringBuilder().append("DELETE FROM ").append(entityType.getName()).append(" WHERE ")
            .append(primaryKeyField.getName()).append(" = :id").toString();
        Query query = getSession().createQuery(hql, null);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param ids <br>
     */
    @Override
    public void deleteByIds(final Collection<? extends Serializable> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            if (ids.size() == 1) {
                deleteById((Serializable) ids.iterator().next());
            }
            else {
                // 确保实体类上有@Entity注解
                Class<?> entityType = getEntityClazz();
                if (!entityType.isAnnotationPresent(Entity.class)) {
                    throw new IllegalArgumentException("The provided class is not an Entity.");
                }

                Field primaryKeyField = findPrimaryKeyField(entityType);
                if (primaryKeyField == null) {
                    throw new IllegalStateException(
                        "Could not find a primary key field annotated with @Id in the given entity type.");
                }

                // 构建HQL或Criteria删除语句
                String hql = new StringBuilder().append("DELETE FROM ").append(entityType.getName()).append(" WHERE ")
                    .append(primaryKeyField.getName()).append(" IN (:ids)").toString();
                Query query = getSession().createQuery(hql, null);
                query.setParameter("ids", ids);
                query.executeUpdate();
            }
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void deleteByLambda(final LambdaDeleteSpecification<T> specification) {
        deleteBySpecification(specification.toSpecification(new LambdaDeleteWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void deleteBySpecification(final CriterialDeleteSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaDelete<T> query = cb.createCriteriaDelete(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "删除条件");
        specification.toPredicate(root, query, cb);
        deleteByCriteria(query);

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

            NativeQuery<Object> query = session.createNativeQuery(sql, Object.class);
            setParamMap(param.getParamMap(), query);
            return query.executeUpdate();
        }
        catch (Exception e) {
            LoggerUtil.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.EXECUTE_ERROR, e);
        }
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
    @Override
    public void executeBatch(final String sql, final Collection<Object[]> objcts, final int commitNumber) {
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
     * @param procedureSql
     * @param params
     * @return <br>
     */
    @Override
    public List<T> executeProcedure(final String procedureSql, final Object... params) {
        Session session = getSession();
        session.flush();
        NativeQuery<T> sqlQuery = session.createNativeQuery(procedureSql, getEntityClazz());

        for (int i = 0; i < params.length; i++) {
            sqlQuery.setParameter(i, params[i]);
        }
        return sqlQuery.list();
    }

    private Field findPrimaryKeyField(final Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true); // 解除私有访问限制
                return field;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            return findPrimaryKeyField(superClass);
        }
        return null;
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
     * @param specification
     * @return <br>
     */
    @Override
    public T get(final QuerySpecification<T> specification) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M get(final QuerySpecification<T> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    public T get(final Serializable id) {
        return getSession().get(getEntityClazz(), id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria
     * @return <br>
     */
    @Override
    public <M> M getByCriteria(final CriteriaQuery<M> criteria) {
        org.hibernate.query.Query<M> query = getSession().createQuery(criteria);
        return query.getSingleResultOrNull();
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
    public T getByHql(final String hql) throws DaoException {
        // 确保实体类上有@Entity注解
        Class<T> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        org.hibernate.query.Query<T> query = getSession().createQuery(hql, entityType);
        return query.getSingleResultOrNull();
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> M getByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T getByLambda(final LambdaQuerySpecification<T, T> specification) {
        return getBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public T getBySpecification(final CriterialQuerySpecification<T> specification) {
        return getBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public <M> M getBySpecification(final CriterialQuerySpecification<T> specification, final Class<M> clazz) {
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root root = query.from(getEntityClazz());
        specification.toPredicate(root, query, cb);
        org.hibernate.query.Query q = getSession().createQuery(query);

        if (Map.class.isAssignableFrom(clazz)) {
            q.setTupleTransformer(NativeQueryMapTransformer.INSTANCE);
        }
        else {
            q.setTupleTransformer(new AutoResultTransformer(clazz));
        }
        return (M) q.getSingleResultOrNull();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    protected Class<T> getEntityClazz() {
        Assert.notNull(entityClazz, ErrorCodeDef.PROXY_TARGET_NOT_FOUND);
        return entityClazz;
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
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> query(final QuerySpecification<T> specification) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> query(final QuerySpecification<T> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new QueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @param param
     * @return
     */
    @SuppressWarnings({
        "unchecked", "rawtypes"
    })
    @Override
    public Object query(final String sql, final DataParam param) {
        try {
            SQlCheckUtil.checkSql(sql);

            Session session = getSession();
            session.flush();

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (param.getReturnType() == null) {
                param.setReturnType(void.class);
            }

            NativeQuery<Object> query = session.createNativeQuery(sql, Object.class);

            // step1:设置参数
            setParamMap(param.getParamMap(), query);

            // step2:设置返回值类型
            final Object callBack = param.getCallback();
            if (callBack != null && callBack instanceof TupleTransformer ttf) {
                query.setTupleTransformer(ttf);
            }
            else if (callBack != null && callBack instanceof ResultListTransformer ttf) {
                query.setResultListTransformer(ttf);
            }
            else if (Map.class.isAssignableFrom(param.getBeanType())) {
                query.setTupleTransformer(NativeQueryMapTransformer.INSTANCE);
            }
            else {
                Class<?> beanType = param.getBeanType();
                if (entityClazz != null && List.class.isAssignableFrom(param.getReturnType())) {
                    beanType = entityClazz;
                    AutoResultTransformer atf = new AutoResultTransformer(beanType);
                    query.setTupleTransformer(atf);
                    query.setResultListTransformer(atf);
                }
                else {
                    if (Serializable.class.isAssignableFrom(beanType)) {
                        beanType = param.getReturnType();
                    }
                    query.setTupleTransformer(new AutoResultTransformer(beanType));
                }
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

                NativeQuery countQuery = session.createNativeQuery(new StringBuilder().append("SELECT COUNT(1) FROM (")
                    .append(sql).append(") ").append(ALIAS).toString(), Long.class);
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
                query.setMaxResults(MAX_SIZE);
                return query.list();
            }
            else {
                return query.uniqueResult();
            }
        }
        catch (Exception e) {
            LoggerUtil.error(e.getMessage(), e);
            throw new DaoException(ErrorCodeDef.QUERY_ERROR, e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List<T> queryAll() {
        // 确保实体类上有@Entity注解
        Class<T> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 构建HQL或Criteria删除语句
        String hql = new StringBuilder().append("FROM ").append(entityType.getName()).toString();

        org.hibernate.query.Query<T> query = getSession().createQuery(hql, entityType);
        query.setMaxResults(MAX_SIZE);
        return query.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria
     * @return <br>
     */
    @Override
    public <M> List<M> queryByCriteria(final CriteriaQuery<M> criteria) {
        org.hibernate.query.Query<M> query = getSession().createQuery(criteria);
        query.setMaxResults(MAX_SIZE);
        return query.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return <br>
     */
    @Override
    public List<T> queryByHql(final String hql) {
        // 确保实体类上有@Entity注解
        Class<T> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        org.hibernate.query.Query<T> query = getSession().createQuery(hql, entityType);
        return query.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @param param
     * @return <br>
     */
    @Override
    public List<T> queryByHqlParam(final String hql, final Object... param) {
        // 确保实体类上有@Entity注解
        Class<T> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        org.hibernate.query.Query<T> q = getSession().createQuery(hql, entityType);
        if (param != null && param.length > 0) {
            for (int i = 0; i < param.length; i++) {
                q.setParameter(i, param[i]);
            }
        }
        return q.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> List<M> queryByLambda(final LambdaQuerySpecification<T, M> specification, final Class<M> clazz) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> queryByLambda(final LambdaQuerySpecification<T, T> specification) {
        return queryBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @return <br>
     */
    @Override
    public List<T> queryBySpecification(final CriterialQuerySpecification<T> specification) {
        return queryBySpecification(specification, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param clazz
     * @return <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public <M> List<M> queryBySpecification(final CriterialQuerySpecification<T> specification, final Class<M> clazz) {
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root root = query.from(getEntityClazz());
        specification.toPredicate(root, query, cb);
        org.hibernate.query.Query q = getSession().createQuery(query);
        q.setMaxResults(MAX_SIZE);
        if (Map.class.isAssignableFrom(clazz)) {
            q.setTupleTransformer(NativeQueryMapTransformer.INSTANCE);
        }
        else {
            AutoResultTransformer<M> art = new AutoResultTransformer<M>(clazz);
            q.setTupleTransformer(art);
            q.setResultListTransformer(art);
        }
        return q.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param sql
     * @return <br>
     */
    @Override
    public List<T> queryBySql(final String sql) {
        return queryBySql(sql, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param sql
     * @return <br>
     */
    @Override
    public <M> List<M> queryBySql(final String sql, final Class<M> clazz) {
        Session session = getSession();
        session.flush();
        NativeQuery<M> query = session.createNativeQuery(sql, clazz);

        if (Map.class.isAssignableFrom(clazz)) {
            query.setTupleTransformer(NativeQueryMapTransformer.INSTANCE);
        }
        else {
            AutoResultTransformer<M> art = new AutoResultTransformer<M>(clazz);
            query.setTupleTransformer(art);
            query.setResultListTransformer(art);
        }
        return query.list();
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
    @Override
    public PagerList<T> queryPager(final QuerySpecification<T> specification, final int pageIndex, final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPager(final QuerySpecification<T> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
        return queryPagerBySpecification(specification.toSpecification(new QueryWrapper<>()), pageIndex, pageSize,
            clazz);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria
     * @param pi
     * @param pageSize
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByCriteria(final CriteriaQuery<M> criteria, final int pi, final int pageSize) {
        CriteriaBuilder builder = criteriaBuilder();
        Set<Root<?>> roots = criteria.getRoots();

        // 查询总页数据
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);

        Root<?> root = countCriteria.from(roots.iterator().next().getJavaType());
        countCriteria.select(builder.count(root));
        // 复制原criteria中的所有where条件（如果有）
        Predicate predicate = criteria.getRestriction();
        if (predicate != null) {
            countCriteria.where(predicate);
        }
        // 总页数
        Long totalCount = getSession().createQuery(countCriteria).getSingleResultOrNull();
        if (totalCount == null) {
            totalCount = 0L;
        }

        // 设置分页数据
        int pageIndex = pi;
        if (pi <= 0) {
            pageIndex = 1;
        }

        PagerList<M> resultList = new PagerList<M>();
        resultList.setPageIndex(pageIndex);
        resultList.setPageSize(pageSize);
        resultList.setTotalCount(totalCount);

        // 如果还有数据，则分页查询
        if (totalCount > (pageIndex - 1) * pageSize) {
            org.hibernate.query.Query<M> query = getSession().createQuery(criteria);
            query.setFirstResult((pageIndex - 1) * pageSize);
            query.setMaxResults(pageSize);
            resultList.addAll(query.getResultList());
        }
        return resultList;
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pageIndex
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @Override
    public <M> PagerList<M> queryPagerByLambda(final LambdaQuerySpecification<T, M> specification, final int pageIndex,
        final int pageSize, final Class<M> clazz) {
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
    @Override
    public PagerList<T> queryPagerByLambda(final LambdaQuerySpecification<T, T> specification, final int pageIndex,
        final int pageSize) {
        return queryPagerBySpecification(specification.toSpecification(new LambdaQueryWrapper<>()), pageIndex,
            pageSize);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification
     * @param pi
     * @param pageSize
     * @return <br>
     */
    @Override
    public PagerList<T> queryPagerBySpecification(final CriterialQuerySpecification<T> specification, final int pi,
        final int pageSize) {
        return queryPagerBySpecification(specification, pi, pageSize, getEntityClazz());
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param <M>
     * @param specification
     * @param pi
     * @param pageSize
     * @param clazz
     * @return <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public <M> PagerList<M> queryPagerBySpecification(final CriterialQuerySpecification<T> specification, final int pi,
        final int pageSize, final Class<M> clazz) {
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "查询条件");
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
        Root entityRoot = criteria.from(getEntityClazz());
        specification.toPredicate(entityRoot, criteria, cb);

        Set<Root<?>> roots = criteria.getRoots();
        roots.forEach(r -> {
            r.alias(ALIAS);
        });

        // 查询总页数据
        CriteriaBuilder builder = criteriaBuilder();
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);

        Root<?> root = countCriteria.from(roots.iterator().next().getJavaType());
        root.alias(ALIAS);
        countCriteria.select(builder.count(root));
        // 复制原criteria中的所有where条件（如果有）
        Predicate predicate = criteria.getRestriction();
        if (predicate != null) {
            countCriteria.where(predicate);
        }
        // 总页数
        Long totalCount = getSession().createQuery(countCriteria).getSingleResultOrNull();
        if (totalCount == null) {
            totalCount = 0L;
        }

        // 设置分页数据
        int pageIndex = pi;
        if (pi <= 0) {
            pageIndex = 1;
        }

        PagerList<M> resultList = new PagerList<>();
        resultList.setPageIndex(pageIndex);
        resultList.setPageSize(pageSize);
        resultList.setTotalCount(totalCount);

        // 如果还有数据，则分页查询
        if (totalCount > (pageIndex - 1) * pageSize) {
            org.hibernate.query.Query query = getSession().createQuery(criteria);
            if (Map.class.isAssignableFrom(clazz)) {
                query.setTupleTransformer(NativeQueryMapTransformer.INSTANCE);
            }
            else {
                AutoResultTransformer<M> art = new AutoResultTransformer<M>(clazz);
                query.setTupleTransformer(art);
                query.setResultListTransformer(art);
            }
            query.setFirstResult((pageIndex - 1) * pageSize);
            query.setMaxResults(pageSize);
            resultList.addAll(query.getResultList());
        }
        return resultList;

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entity <br>
     */
    @Override
    public void save(final BaseEntity entity) {
        getSession().persist(entity);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void saveBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        Session session = getSession();
        for (int i = 0; i < entitys.size(); i++) {
            session.persist(entitys.get(i));
            if (i % NUM_100 == 0) {
                // 1000个对象后才清理缓存，写入数据库
                session.flush();
                session.clear();
            }
        }
        // 最后清理一下----防止大于1000小于2000的不保存
        session.flush();
        session.clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entityClazz <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = (Class<T>) entityClazz;
    }

    private void setParamMap(final Map<String, Object> paramMap, final NativeQuery<?> query) throws DaoException {
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
     * @param pojo <br>
     */
    @Override
    public void update(final BaseEntity pojo) {
        getSession().merge(pojo);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void update(final UpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new UpdateWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param entitys <br>
     */
    @Override
    public void updateBatch(final List<T> entitys) {
        if (entitys.size() > GlobalConstants.DEFAULT_LINES) {
            throw new UtilException(ErrorCodeDef.TOO_MANY_OBJECTS);
        }
        Session session = getSession();
        for (int i = 0; i < entitys.size(); i++) {
            session.merge(entitys.get(i));
            if (i % NUM_100 == 0) {
                // 1000个对象后才清理缓存，写入数据库
                session.flush();
                session.clear();
            }
        }
        // 最后清理一下----防止大于1000小于2000的不保存
        session.flush();
        session.clear();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param criteria <br>
     */
    @Override
    public void updateByCriteria(final CriteriaUpdate<T> criteria) {
        MutationQuery query = getSession().createMutationQuery(criteria);
        query.executeUpdate();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param specification <br>
     */
    @Override
    public void updateByLambda(final LambdaUpdateSpecification<T> specification) {
        updateBySpecification(specification.toSpecification(new LambdaUpdateWrapper<>()));
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param specification <br>
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    @Override
    public void updateBySpecification(final CriterialUpdateSpecification<T> specification) {
        CriteriaBuilder cb = criteriaBuilder();
        CriteriaUpdate<T> query = cb.createCriteriaUpdate(getEntityClazz());
        Root root = query.from(getEntityClazz());
        Assert.notNull(specification, ErrorCodeDef.PARAM_NOT_NULL, "修改条件");
        specification.toPredicate(root, query, cb);
        updateByCriteria(query);
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
    public int updateBySql(final String sql) throws DaoException {
        Session session = getSession();
        session.flush();

        NativeQuery<Object> querys = session.createNativeQuery(sql, Object.class);
        return querys.executeUpdate();
    }

}
