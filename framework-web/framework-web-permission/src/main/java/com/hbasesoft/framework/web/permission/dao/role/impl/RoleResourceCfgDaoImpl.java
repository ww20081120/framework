package com.hbasesoft.framework.web.permission.dao.role.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.web.permission.dao.role.RoleResourceCfgDao;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 * 
 * @author chaizhi<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月6日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.dao.permission.role.impl <br>
 */
@Component("roleResourceCfgDao")
public class RoleResourceCfgDaoImpl implements RoleResourceCfgDao {

    /**
     * sessionFactory
     */
    private SessionFactory sessionFactory;

    /**
     * Description: <br>
     * 
     * @author chaizhi<br>
     * @taskId <br>
     * @param sql sql
     * @return List
     * @throws DaoException <br>
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> query(final String sql) throws DaoException {
        Session session = sessionFactory.getCurrentSession();
        SQLQuery query = session.createSQLQuery(sql);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (List<Map<String, Object>>) query.list();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
