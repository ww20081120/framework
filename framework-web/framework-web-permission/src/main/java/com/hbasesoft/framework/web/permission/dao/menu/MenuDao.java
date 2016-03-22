/**
 *
 */
package com.hbasesoft.framework.web.permission.dao.menu;

import com.hbasesoft.framework.web.permission.bean.MenuPojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月23日 <br>
 * @see com.hbasesoft.framework.web.permission.dao.menu <br>
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
    List<MenuPojo> selectMenu(@Param("moduleCode") String moduleCode) throws DaoException;

    /**
     * Description: <br>
     *
     * @param menu <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    void updateSeq(@Param("menu") MenuPojo menu) throws DaoException;

    Long getSeq(@Param("parentResourceId") Long parentResourceId) throws DaoException;

    int updateAttr(@Param("pojo") MenuPojo pojo) throws DaoException;

    @Sql(value = "DELETE FROM T_MANAGER_MENU WHERE RESOURCE_ID IN :ids")
    int deleteByIds(@Param("ids") Long[] ids) throws DaoException;
}
