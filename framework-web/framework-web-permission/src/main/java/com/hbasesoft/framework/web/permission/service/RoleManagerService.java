/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.service;

import java.util.List;
import java.util.Set;

import com.hbasesoft.framework.web.permission.bean.RolePojo;
import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> 角色管理服务<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.service.permission <br>
 */
public interface RoleManagerService {

    /**
     * Description: 根据角色名称分页查询角色列表<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleName
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    List<RolePojo> queryRoleList(String roleName, Integer pageIndex, Integer pageSize) throws ServiceException;

    List<RolePojo> queryRoleList(String moduleCode) throws ServiceException;

    /**
     * Description:查询角色信息 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleId
     * @return
     * @throws ServiceException <br>
     */
    RolePojo queryRole(int roleId) throws ServiceException;

    /**
     * Description: 查询角色的资源<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleId
     * @return
     * @throws ServiceException <br>
     */
    List<RoleResourcePojo> queryRoleResourceList(Integer roleId) throws ServiceException;

    /**
     * Description: 添加角色与权限<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param role
     * @param roleResourceSet
     * @throws ServiceException <br>
     */
    void addRoleAndPermission(RolePojo role, Set<RoleResourcePojo> roleResourceSet) throws ServiceException;

    /**
     * Description: 更新角色与权限<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param role
     * @param roleResourceSet
     * @throws ServiceException <br>
     */
    void updateRoleAndPermission(RolePojo role, Set<RoleResourcePojo> roleResourceSet) throws ServiceException;

    /**
     * Description: 批量删除角色<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param roleIds
     * @throws ServiceException <br>
     */
    void deleteRoles(Integer[] roleIds) throws ServiceException;
}
