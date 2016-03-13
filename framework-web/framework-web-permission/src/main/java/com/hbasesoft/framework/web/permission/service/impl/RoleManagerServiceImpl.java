/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.permission.bean.RolePojo;
import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.web.permission.dao.role.RoleDao;
import com.hbasesoft.framework.web.permission.dao.role.RoleResourceDao;
import com.hbasesoft.framework.web.permission.service.RoleManagerService;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> 角色管理服务实现类<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.service.permission.impl <br>
 */
@Service
public class RoleManagerServiceImpl implements RoleManagerService {

    /**
     * roleDao
     */
    @Resource
    private RoleDao roleDao;

    /**
     * roleResourceDao
     */
    @Resource
    private RoleResourceDao roleResourceDao;

    @Override
    public List<RolePojo> queryRoleList(String roleName, Integer pageIndex, Integer pageSize) throws ServiceException {
        if (CommonUtil.isNotEmpty(roleName)) {
            roleName = new StringBuilder().append(GlobalConstants.PERCENT).append(roleName)
                .append(GlobalConstants.PERCENT).toString();
        }else{
            roleName = null;
        }
        try {
            return roleDao.selectRole(roleName, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<RolePojo> queryRoleList(String moduleCode) throws ServiceException {
        try {
            return roleDao.selectRole(moduleCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public List<RoleResourcePojo> queryRoleResourceList(Integer roleId) throws ServiceException {
        try {
            return roleResourceDao.selectRoleResource(roleId, null);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addRoleAndPermission(RolePojo role, Set<RoleResourcePojo> roleResourceSet) throws ServiceException {
        Date currentDate = new Date();
        role.setCreateTime(currentDate);
        role.setStateDate(currentDate);
        try {
            roleDao.save(role);
            if (CommonUtil.isNotEmpty(roleResourceSet)) {
                for (RoleResourcePojo pojo : roleResourceSet) {
                    pojo.setRoleId(role.getRoleId());
                    roleResourceDao.insertRoleResource(pojo);
                }
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    @Override
    public void updateRoleAndPermission(RolePojo role, Set<RoleResourcePojo> roleResourceSet) throws ServiceException {
        try {
            RolePojo newRole = roleDao.getById(RolePojo.class, role.getRoleId());
            newRole.setExt(role.getExt());
            newRole.setRoleName(role.getRoleName());
            roleDao.save(newRole);

            roleResourceDao.deleteRoleResourceByRoleId(newRole.getRoleId(), null);
            if (CommonUtil.isNotEmpty(roleResourceSet)) {
                for (RoleResourcePojo pojo : roleResourceSet) {
                    pojo.setRoleId(newRole.getRoleId());
                    roleResourceDao.insertRoleResource(pojo);
                }
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void deleteRoles(Integer[] roleIds) throws ServiceException {
        try {
            if (roleIds.length == 1) {
                roleResourceDao.deleteRoleResourceByRoleId(roleIds[0], null);
                roleDao.deleteById(RolePojo.class, roleIds[0]);
            }
            else {
                roleResourceDao.deleteRoleResourceByRoleId(roleIds);
                roleDao.deleteByRoleIds(roleIds);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    @Override
    public RolePojo queryRole(int roleId) throws ServiceException {
        try {
            return roleDao.getById(RolePojo.class, roleId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
