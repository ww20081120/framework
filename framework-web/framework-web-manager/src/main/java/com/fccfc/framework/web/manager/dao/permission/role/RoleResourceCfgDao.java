package com.fccfc.framework.web.manager.dao.permission.role;

import java.util.List;
import java.util.Map;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;

/**
 * 
 * <Description> <br> 
 *  
 * @author chaizhi<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月6日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.dao.permission <br>
 */ 
public interface RoleResourceCfgDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author chaizhi<br>
     * @taskId <br>
     * @param sql sql
     * @return
     * @throws DaoException <br>
     */
    public List<Map<String, Object>> query(final String sql) throws DaoException;
}
