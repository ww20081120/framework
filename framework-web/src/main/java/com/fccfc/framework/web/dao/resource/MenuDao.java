/**
 * 
 */
package com.fccfc.framework.web.dao.resource;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.web.bean.resource.MenuPojo;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.dao <br>
 */
@Dao
public interface MenuDao {

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(bean = MenuPojo.class)
    List<MenuPojo> selectAllMenu(@Param("moduleCode") List<String> moduleCode) throws DaoException;
}
