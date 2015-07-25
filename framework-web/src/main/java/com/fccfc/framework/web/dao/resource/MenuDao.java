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

    /**
     * 
     * Description: 根据权限查询菜单<br> 
     *  
     * @author 王伟<br>
     * @taskId <br>
     * @param operateId
     * @param moduleCode
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = MenuPojo.class)
    List<MenuPojo> selectMenuByPermision(@Param("operateId") Integer operateId,
        @Param("moduleCode") List<String> moduleCode) throws DaoException;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @param clazz <br>
     * @param method <br>
     * @return <br>
     * @throws DaoException <br>
     */
    Integer getMenuByClassAndMethod(@Param("moduleCode") List<String> moduleCode, @Param("class") String clazz,
        @Param("method") String method) throws DaoException;
}
