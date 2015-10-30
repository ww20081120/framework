/**
 * 
 */
package com.fccfc.framework.config.core.dao;

import java.util.List;

import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;

/**
 * 业务模块 <Description> <br>
 * 
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.config.core.dao <br>
 */
@Dao
public interface ModuleDao extends IGenericBaseDao {

    /**
     * 查询所有的业务模块 Description: <br>
     * 
     * @author 胡攀<br>
     * @taskId <br>
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = ModulePojo.class)
    List<ModulePojo> selectAllModule() throws DaoException;
}
