/**
 * 
 */
package com.fccfc.framework.db.core.executor;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.config.DataParam;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-10-26 <br>
 * @see com.fccfc.framework.dao.support <br>
 */
public interface ISqlExcutor {

    /**
     * 查询
     * 
     * @param sql <br>
     * @param param <br>
     * @return <br>
     * @throws DaoException <br>
     */
    Object query(String sql, DataParam param) throws DaoException;

    /**
     * 执行sql语句
     * 
     * @param sql sql <br>
     * @param param params <br>
     * @return Object <br>
     * @throws DaoException DaoException <br>
     */
    int excuteSql(String sql, DataParam param) throws DaoException;

    /**
     * 批处理sql
     * 
     * @param sql <br>
     * @param param <br>
     * @return <br>
     * @throws DaoException <br>
     */
    int[] batchExcuteSql(String[] sql, DataParam param) throws DaoException;
}
