/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.dao.role;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.db.core.annotation.Dao;
import com.hbasesoft.framework.db.core.annotation.Param;
import com.hbasesoft.framework.db.core.annotation.Sql;

/**
 * <Description> RoleResource ORM<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @see com.hbasesoft.framework.web.manager.dao.permission <br>
 * @since V1.0<br>
 */
@Dao
public interface RoleResourceDao {

    /**
     * Description: listRoleResource<br>
     *
     * @param roleId
     * @param resourceType
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Sql(bean = RoleResourcePojo.class)
    List<RoleResourcePojo> selectRoleResource(@Param("roleId") int roleId, @Param("resourceType") String resourceType)
        throws DaoException;

    /**
     * Description: saveRoleResource<br>
     *
     * @param pojo
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    int insertRoleResource(@Param("pojo") RoleResourcePojo pojo) throws DaoException;

    /**
     * Description: deleteRoleResourceByRoleId<br>
     *
     * @param roleId
     * @param resourceType
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    int deleteRoleResourceByRoleId(@Param("roleId") int roleId, @Param("resourceType") String resourceType)
        throws DaoException;

    /**
     * Description: 批量删除角色资源<br>
     *
     * @param roleIds
     * @return
     * @throws DaoException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    int deleteRoleResourceByRoleId(@Param("roleIds") Integer[] roleIds) throws DaoException;

    @Sql(bean = RoleResourcePojo.class)
    List<RoleResourcePojo> selectListRoleResourceByDutyId(@Param("dutyId") Long dutyId,
        @Param("moduleCode") String moduleCode) throws DaoException;

    @Sql(bean = RoleResourcePojo.class)
    List<RoleResourcePojo> selectListRoleResourceByResourceId(@Param("resourceId") Long resourceId) throws DaoException;
}
