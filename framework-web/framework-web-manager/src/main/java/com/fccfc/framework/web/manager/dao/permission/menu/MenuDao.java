/**
 *
 */
package com.fccfc.framework.web.manager.dao.permission.menu;

import java.util.List;

import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.db.core.annotation.Dao;
import com.fccfc.framework.db.core.annotation.Param;
import com.fccfc.framework.db.core.annotation.Sql;
import com.fccfc.framework.db.hibernate.IGenericBaseDao;
import com.fccfc.framework.web.manager.bean.permission.MenuPojo;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月23日 <br>
 * @see com.fccfc.framework.web.manager.dao.permission.menu <br>
 * @since V1.0<br>
 */
@Dao
public interface MenuDao extends IGenericBaseDao {

    /**
     * Description:查询所有的菜单 <br>
     *
     * @return
     * @throws DaoException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Sql(bean = MenuPojo.class)
    List<MenuPojo> selectMenu(@Param("moduleCode") String moduleCode, @Param("type") String type) throws DaoException;

    /**
     * Description: 根据resourceId删除<br>
     *
     * @param resourceId
     * @return
     * @throws DaoException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Sql(bean = MenuPojo.class)
    int deleteById(@Param("resourceId") Long resourceId) throws DaoException;

    /**
     * Description: <br>
     *
     * @param menu <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    void updateSeq(@Param("menu") MenuPojo menu) throws DaoException;

    MenuPojo getById(@Param("id") Long id) throws DaoException;

    Long getSeq(@Param("parentResourceId") Long parentResourceId) throws DaoException;

    int updateAttr(@Param("pojo") MenuPojo pojo) throws DaoException;

    @Sql(bean = MenuPojo.class)
    List<MenuPojo> selectMenuButton(@Param("moduleCodes") List<String> moduleCodes,
        @Param("menuResourceId") Long menuResourceId, @Param("type") String type,
        @Param(Param.PAGE_INDEX) Integer pageIndex, @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    @Sql(value = "DELETE FROM MENU WHERE RESOURCE_ID IN :ids")
    int deleteByIds(@Param("ids") Long[] ids) throws DaoException;
}
