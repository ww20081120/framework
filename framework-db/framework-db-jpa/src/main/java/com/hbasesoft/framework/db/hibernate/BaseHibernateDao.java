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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.db.TransactionManagerHolder;
import com.hbasesoft.framework.db.core.BaseEntity;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.config.DataParam;
import com.hbasesoft.framework.db.core.executor.ISqlExcutor;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.db.core.utils.SQlCheckUtil;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-10-26 <br>
 * @see com.hbasesoft.framework.dao.support.hibernate <br>
 */
@SuppressWarnings({
    "unchecked", "rawtypes"
})
public class BaseHibernateDao implements IBaseDao, ISqlExcutor {

    /** Number */
    private static final int NUM_100 = 100;

    /** 最大分页数 */
    private static final int MAX_SIZE = 1000;

    /** entity class */
    private Class<?> entityClazz;

    /** jpaQueryFactory */
    private JPAQueryFactory jpaQueryFactory;

    /** jpaEntity */
    private EntityPath<?> jpaEntity;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
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
    public void saveBatch(final List entitys) {
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
     * @param entitys <br>
     */
    @Override
    public void updateBatch(final List entitys) {
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
    public void updateByCriteria(final CriteriaUpdate criteria) {
        MutationQuery query = getSession().createMutationQuery(criteria);
        query.executeUpdate();
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
     * @param entities <br>
     */
    @Override
    public void deleteBatch(final Collection entities) {
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
     * @param ids <br>
     */
    @Override
    public void deleteByIds(final Collection ids) {
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
     * @param criteria <br>
     */
    @Override
    public void deleteByCriteria(final CriteriaDelete criteria) {
        MutationQuery query = getSession().createMutationQuery(criteria);
        query.executeUpdate();
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
    public BaseEntity get(final Serializable id) {
        return (BaseEntity) getSession().get(getEntityClazz(), id);
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    @Override
    public BaseEntity getByProperty(final String propertyName, final Object value) {
        Assert.notEmpty(propertyName, ErrorCodeDef.DAO_PROPERTY_IS_EMPTY);

        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 构建HQL或Criteria删除语句
        String hql = new StringBuilder().append("FROM ").append(entityType.getName()).append(" WHERE ")
            .append(propertyName).append(" = :value").toString();

        Query query = getSession().createQuery(hql, entityType);
        query.setParameter("value", value);

        // 获取唯一结果，如果不存在则返回null
        return (BaseEntity) query.getSingleResult();
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
    public Object getByCriteria(final CriteriaQuery criteria) {
        org.hibernate.query.Query query = getSession().createQuery(criteria);
        return query.getSingleResult();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public List queryAll() {
        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }
        // 构建HQL或Criteria删除语句
        String hql = new StringBuilder().append("FROM ").append(entityType.getName()).toString();

        Query query = getSession().createQuery(hql, entityType);
        query.setMaxResults(MAX_SIZE);
        return query.getResultList();
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param propertyName
     * @param value
     * @return <br>
     */
    @Override
    public List queryByProperty(final String propertyName, final Object value) {
        Assert.notEmpty(propertyName, ErrorCodeDef.DAO_PROPERTY_IS_EMPTY);

        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        // 构建HQL或Criteria删除语句
        String hql = new StringBuilder().append("FROM ").append(entityType.getName()).append(" WHERE ")
            .append(propertyName).append(" = :value").toString();

        Query query = getSession().createQuery(hql, entityType);
        query.setParameter("value", value);
        query.setMaxResults(MAX_SIZE);
        return query.getResultList();
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
    public PagerList queryPagerByCriteria(final CriteriaQuery criteria, final int pi, final int pageSize) {
        // 查询总页数据
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        countCriteria.select(builder.count((Root) criteria.getRoots().iterator().next()));

        // 复制原criteria中的所有where条件（如果有）
        Predicate predicate = criteria.getRestriction();
        if (predicate != null) {
            countCriteria.where(predicate);
        }
        // 总页数
        Long totalCount = getSession().createQuery(countCriteria).getSingleResult();
        if (totalCount == null) {
            totalCount = 0L;
        }

        // 设置分页数据
        int pageIndex = pi;
        if (pi <= 0) {
            pageIndex = 1;
        }

        PagerList resultList = new PagerList();
        resultList.setPageIndex(pageIndex);
        resultList.setPageSize(pageSize);
        resultList.setTotalCount(totalCount);

        // 如果还有数据，则分页查询
        if (totalCount >= pageIndex * pageSize) {
            org.hibernate.query.Query query = getSession().createQuery(criteria);
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
     * @param criteria
     * @return <br>
     */
    @Override
    public List queryByCriteria(final CriteriaQuery criteria) {
        org.hibernate.query.Query query = getSession().createQuery(criteria);
        query.setMaxResults(MAX_SIZE);
        return query.getResultList();
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
    @SuppressWarnings("deprecation")
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

            NativeQuery query = session.createNativeQuery(sql);

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

                NativeQuery countQuery = session.createNativeQuery("SELECT COUNT(1) FROM (" + sql + ") QUERY_DATA__",
                    Long.class);
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
     * @param sql
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("deprecation")
    @Override
    public int excuteSql(final String sql, final DataParam param) throws DaoException {
        try {
            SQlCheckUtil.checkSql(sql);
            Session session = getSession();
            session.flush();

            NativeQuery query = session.createNativeQuery(sql);
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
     * @param sqls
     * @param param
     * @return
     * @throws DaoException <br>
     */
    @SuppressWarnings("deprecation")
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) throws DaoException {
        try {
            Session session = getSession();
            session.flush();

            int[] result = new int[sqls.length];
            NativeQuery query;
            for (int i = 0; i < sqls.length; i++) {
                query = session.createNativeQuery(sqls[i]);
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
     * @param entityClazz <br>
     */
    @Override
    public void setEntityClazz(final Class<?> entityClazz) {
        this.entityClazz = entityClazz;
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
    @SuppressWarnings("deprecation")
    @Override
    public int updateBySql(final String sql) throws DaoException {
        Session session = getSession();
        session.flush();

        NativeQuery querys = session.createNativeQuery(sql);
        return querys.executeUpdate();
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
    public BaseEntity getByHql(final String hql) throws DaoException {
        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        Query query = getSession().createQuery(hql, entityType);
        return (BaseEntity) query.getSingleResult();
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
    public List executeProcedure(final String procedureSql, final Object... params) {
        Class<?> entityType = getEntityClazz();

        Session session = getSession();
        session.flush();
        NativeQuery sqlQuery = session.createNativeQuery(procedureSql, entityType);

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
     * @param sql
     * @return <br>
     */
    @SuppressWarnings("deprecation")
    @Override
    public List queryBySql(final String sql) {
        Session session = getSession();
        session.flush();
        NativeQuery query = session.createNativeQuery(sql);

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
     * @return <br>
     */
    @Override
    public List queryByHqlParam(final String hql, final Object... param) {
        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        Query q = getSession().createQuery(hql, entityType);
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
     * @author 王伟<br>
     * @taskId <br>
     * @param hql
     * @return <br>
     */
    @Override
    public List queryByHql(final String hql) {
        // 确保实体类上有@Entity注解
        Class<?> entityType = getEntityClazz();
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("The provided class is not an Entity.");
        }

        Query query = getSession().createQuery(hql, entityType);
        return query.getResultList();
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
    public void executeBatch(final String sql, final Collection objcts, final int commitNumber) {
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
                    for (Object[] object : (Collection<Object[]>) objcts) {
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
     * @return <br>
     */
    protected Class<?> getEntityClazz() {
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
    protected JPQLQueryFactory getJpaQueryFactory() {
        if (jpaQueryFactory == null) {
            jpaQueryFactory = new JPAQueryFactory(getSession());
            Class<?> clazz = getEntityClazz();
            if (BaseEntity.class.isAssignableFrom(clazz)) {
                String clazzName = StringUtils.uncapitalize(clazz.getSimpleName());
                String qName = new StringBuilder(clazz.getName())
                    .insert(clazz.getName().length() - clazzName.length(), 'Q').toString();
                try {
                    Class<EntityPath<?>> qClazz = (Class<EntityPath<?>>) Class.forName(qName);
                    Field field = qClazz.getField(clazzName);
                    if (field != null) {
                        jpaEntity = (EntityPath<?>) field.get(null);
                    }
                }
                catch (Exception e) {
                    LoggerUtil.error(e);
                }
            }
        }
        return jpaQueryFactory;
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

    private void setParamMap(final Map<String, Object> paramMap, final NativeQuery query) throws DaoException {
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
     * @author ww200<br>
     * @taskId <br>
     * @param exprs
     * @return <br>
     */
    @Override
    public JPQLQuery<Tuple> select(final Expression... exprs) {
        return getJpaQueryFactory().select(exprs).from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public DeleteClause delete() {
        return getJpaQueryFactory().delete(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param expr
     * @return <br>
     */
    @Override
    public JPQLQuery select(final Expression expr) {
        return getJpaQueryFactory().select(expr).from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param expr
     * @return <br>
     */
    @Override
    public JPQLQuery selectDistinct(final Expression expr) {
        return getJpaQueryFactory().selectDistinct(expr).from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @param exprs
     * @return <br>
     */
    @Override
    public JPQLQuery selectDistinct(final Expression... exprs) {
        return getJpaQueryFactory().selectDistinct(exprs).from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public JPQLQuery selectOne() {
        return getJpaQueryFactory().selectOne().from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public JPQLQuery selectZero() {
        return getJpaQueryFactory().selectZero().from(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public JPQLQuery select() {
        return getJpaQueryFactory().selectFrom(jpaEntity);
    }

    /**
     * Description: <br>
     * 
     * @author ww200<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public UpdateClause update() {
        return getJpaQueryFactory().update(jpaEntity);
    }
}
