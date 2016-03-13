/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.dao.role;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.RolePojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;
import com.hbasesoft.framework.db.hibernate.IGenericBaseDao;

/**
 * <Description> Role ORM<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.dao.permission <br>
 */
@Dao
public interface RoleDao extends IGenericBaseDao {

    /**
     * Description: 查询角色<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleName
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws DaoException <br>
     */
    @Sql(bean = RolePojo.class)
    List<RolePojo> selectRole(@Param("roleName") String roleName, @Param(Param.PAGE_INDEX) Integer pageIndex,
        @Param(Param.PAGE_SIZE) Integer pageSize) throws DaoException;

    @Sql(bean = RolePojo.class)
    List<RolePojo> selectRole(@Param("moduleCode") String moduleCode) throws DaoException;

    /**
     * Description: 批量删除角色 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleIds
     * @return
     * @throws DaoException <br>
     */
    int deleteByRoleIds(@Param("roleIds") Integer[] roleIds) throws DaoException;
}
