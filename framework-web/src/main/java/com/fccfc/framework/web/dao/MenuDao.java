/**
 * 
 */
package com.fccfc.framework.web.dao;

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
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param moduleCode <br>
     * @return <br>
     * @throws DaoException <br>
     */
    @Sql(
        value = "SELECT M.*,U.URL FROM MENU M LEFT JOIN URL_RESOURCE U ON "
            + "M.RESOURCE_ID = U.RESOURCE_ID WHERE M.MODULE_CODE IN (:moduleCode) ORDER BY M.PARENT_ID ASC, M.SEQ ASC",
        bean = MenuPojo.class)
    List<MenuPojo> selectAllMenu(@Param("moduleCode") List<String> moduleCode) throws DaoException;

    /**
     * 
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
    @Sql("SELECT M.MENU_ID FROM MENU M, URL_RESOURCE U WHERE M.RESOURCE_ID = U.RESOURCE_ID "
        + "AND U.MODULE_CODE IN (:moduleCode) AND M.IS_LEAF = 'Y' AND U.EXECUTE_CLASS = :class AND U.EXECUTE_METHOD = :method")
    Integer getMenuByClassAndMethod(@Param("moduleCode") List<String> moduleCode, @Param("class") String clazz,
        @Param("method") String method) throws DaoException;
}
