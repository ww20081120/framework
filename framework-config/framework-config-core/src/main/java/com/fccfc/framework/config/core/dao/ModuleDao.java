/**
 * 
 */
package com.fccfc.framework.config.core.dao;

import java.util.List;

import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Sql;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月26日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.core.config.dao <br>
 */
@Dao
public interface ModuleDao {

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = ModulePojo.class)
    List<ModulePojo> selectAllModule() throws DaoException;
}
