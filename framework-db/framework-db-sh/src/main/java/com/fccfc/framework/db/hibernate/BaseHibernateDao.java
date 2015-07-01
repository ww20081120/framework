/**
 * 
 */
package com.fccfc.framework.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.BaseEntity;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.config.DataParam;
import com.fccfc.framework.db.core.executor.ISqlExcutor;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-10-26 <br>
 * @see com.fccfc.framework.dao.support.hibernate <br>
 */
public class BaseHibernateDao implements IGenericBaseDao, ISqlExcutor {

    /** sessionFactory */
    private SessionFactory sessionFactory;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.SqlExcutor#query(java.lang.String, java.util.Map)
     */
    @Override
    public Object query(final String sql, final DataParam param) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();

            SQLQuery query = session.createSQLQuery(sql);

            // Redis缓存序列化时不能有void返回类型，特殊处理一下
            if (CommonUtil.isNull(param.getReturnType())) {
                param.setReturnType(void.class);
            }

            // step1:设置参数
            setParamMap(param.getParamMap(), query);

            // step2:设置分页
            if (param.getPageIndex() != -1 && param.getPageSize() != -1) {
                query.setFirstResult((param.getPageIndex() - 1) * param.getPageSize());
                query.setMaxResults(param.getPageSize());
            }

            // step3:设置返回值类型
            final Object callBack = param.getCallback();
            if (callBack != null && callBack instanceof ResultTransformer) {
                query.setResultTransformer((ResultTransformer) callBack);
            }
            else if (param.getBeanType().equals(Map.class)) {
                query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            }
            else if (Serializable.class.isAssignableFrom(param.getBeanType())) {
                Class<?> beanType = param.getBeanType();
                if (Serializable.class.equals(beanType)) {
                    beanType = param.getReturnType();
                }
                query.setResultTransformer(new AutoResultTransformer(beanType));
            }

            return List.class.isAssignableFrom(param.getReturnType()) || Object.class.equals(param.getReturnType()) ? query
                .list() : query.uniqueResult();
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.QUERY_ERROR_10010, "执行查询语句失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param paramMap <br>
     * @param query <br>
     * @throws DaoException <br>
     */
    private void setParamMap(Map<String, Object> paramMap, Query query) throws DaoException {
        if (CommonUtil.isNotEmpty(paramMap)) {
            for (Entry<String, Object> entry : paramMap.entrySet()) {
                Object obj = entry.getValue();

                // 这里考虑传入的参数是什么类型，不同类型使用的方法不同
                if (obj instanceof Collection<?>) {
                    query.setParameterList(entry.getKey(), (Collection<?>) obj);
                }
                else if (obj != null && obj.getClass().isArray()) {
                    if (!(obj instanceof Object[])) {
                        throw new DaoException(ErrorCodeDef.LIST_PARAM_ERROR_10040, "请使用包装类型的数组");
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
     * @see com.fccfc.framework.dao.support.SqlExcutor#excuteSql(java.lang.String, java.util.Map)
     */
    @Override
    public int excuteSql(final String sql, final DataParam param) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            SQLQuery query = session.createSQLQuery(sql);
            setParamMap(param.getParamMap(), query);
            return query.executeUpdate();
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR_10012, "执行SQL语句失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.SqlExcutor#batchExcuteSql(java.lang.String[])
     */
    @Override
    public int[] batchExcuteSql(final String[] sqls, final DataParam param) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
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
            throw new DaoException(ErrorCodeDef.BATCH_EXECUTE_ERROR_10012, "执行批量SQL语句失败", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#save(java.lang.Object)
     */
    @Override
    public void save(BaseEntity t) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(t);
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.SAVE_ERROR_10013, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#getById(java.lang.Class, java.io.Serializable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseEntity> T getById(Class<T> entityClass, Serializable id) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            return (T) session.get(entityClass, id);
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.GET_BY_ID_ERROR_10014, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#getByEntity(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseEntity> T getByEntity(T entity) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria executableCriteria = session.createCriteria(entity.getClass());
            executableCriteria.add(Example.create(entity));
            return (T) executableCriteria.uniqueResult();
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.GET_BY_ENTITY_ERROR_10015, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#update(java.lang.Object)
     */
    @Override
    public void update(BaseEntity entity) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(entity);
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.UPDATE_ERROR_10016, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#delete(java.lang.Object)
     */
    @Override
    public void delete(BaseEntity entity) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.delete(entity);
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.DELETE_ERROR_10017, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#deleteById(java.lang.Class, java.io.Serializable)
     */
    @Override
    public <T extends BaseEntity> void deleteById(Class<T> entityClass, Serializable id) throws DaoException {
        try {
            delete(getById(entityClass, id));
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.DELETE_BY_ID_ERROR_10018, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.dao.support.IGenericBaseDao#selectList(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseEntity> List<T> selectList(Class<T> entityClass) throws DaoException {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria executableCriteria = session.createCriteria(entityClass);
            return executableCriteria.list();
        }
        catch (Exception e) {
            throw new DaoException(ErrorCodeDef.SELECT_LIST_ERROR_10019, e);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
