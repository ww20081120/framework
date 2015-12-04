/****************************************************************************************
 * Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.controller.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.RolePojo;
import com.fccfc.framework.web.manager.bean.permission.RoleResourcePojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.RoleManagerService;
import com.fccfc.framework.web.manager.utils.WebUtil;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.controller.permission <br>
 */
@Controller
@RequestMapping("/role")
public class RoleManagerController extends AbstractController {

    @Resource
    private RoleManagerService roleManagerService;

    /**
     * Description: 页面跳转<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toRole() {
        return "permission/role/index";
    }

    /**
     * Description: 查询列表数据<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Map<String, Object> queryRoleData() throws ServiceException {
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<RolePojo> roleList = (PagerList<RolePojo>) roleManagerService.queryRoleList(getParameter("roleName"),
                getPageIndex(), getPageSize());
        result.put("data", roleList);
        result.put("pageIndex", roleList.getPageIndex());
        result.put("pageSize", roleList.getPageSize());
        result.put("totalCount", roleList.getTotalCount());
        result.put("totalPage", roleList.getTotalPage());
        return result;
    }

    /**
     * Description: 查询列表数据<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<RolePojo> list() throws FrameworkException {
        return roleManagerService.queryRoleList(getParameter("moduleCode"));
    }

    /**
     * Description: 查询角色详情<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param roleId
     * @param map
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/query/{roleId}", method = RequestMethod.POST)
    public ModelAndView detail(@PathVariable("roleId") Integer roleId, ModelMap map) throws FrameworkException {
        Assert.notNull(roleId, "角色标识不能为空");
        RolePojo role = roleManagerService.queryRole(roleId);
        Assert.notNull(role, "角色不存在");
        map.put("role", role);
        map.put("resources", roleManagerService.queryRoleResourceList(roleId));
        return new ModelAndView("permission/role/detail", map);
    }

    /**
     * Description: g<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String toAddRole() {
        return "permission/role/add";
    }

    /**
     * Description: 新增role<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addRole() throws FrameworkException {
        String roleName = getParameter("roleName", "角色名称不能为空");
        String moduleCode = getParameter("moduleCode", "模块代码不能为空");

        RolePojo role = new RolePojo();
        role.setRoleName(roleName);
        role.setModuleCode(moduleCode);
        role.setOperatorId(WebUtil.getCurrentOperatorId());
        role.setExt(getParameter("ext"));
        role.setState(ManagerConstant.STATE_AVAILABLE);

        String resources = getParameter("resources");
        Set<RoleResourcePojo> roleResourceSet = null;
        if (CommonUtil.isNotEmpty(resources)) {
            JSONArray jsonArr = JSONArray.parseArray(resources);
            roleResourceSet = new HashSet<RoleResourcePojo>();
            for (Object obj : jsonArr) {
                JSONObject json = (JSONObject) obj;
                RoleResourcePojo pojo = new RoleResourcePojo();
                pojo.setResourceId(json.getInteger("id"));
                pojo.setResourceType(json.getString("type"));
                roleResourceSet.add(pojo);
            }
        }

        roleManagerService.addRoleAndPermission(role, roleResourceSet);
        return success("新增角色信息成功。");
    }

    /**
     * Description:toModify <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param roleId
     * @param map
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modify/{roleId}", method = RequestMethod.GET)
    public ModelAndView toModify(@PathVariable("roleId") Integer roleId, ModelMap map) throws FrameworkException {
        Assert.notNull(roleId, "角色标识不能为空");
        RolePojo role = roleManagerService.queryRole(roleId);
        Assert.notNull(role, "角色不存在");
        map.put("role", role);
        map.put("resources", roleManagerService.queryRoleResourceList(roleId));
        return new ModelAndView("permission/role/edit", map);
    }

    /**
     * Description: 修改role<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modifyRole() throws FrameworkException {
        Integer roleId = Integer.parseInt(getParameter("roleId", "角色标识不能为空"));
        String roleName = getParameter("roleName", "角色名称不能为空");
        String moduleCode = getParameter("moduleCode", "模块代码不能为空");
        
        RolePojo role = new RolePojo();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setExt(getParameter("ext"));
        role.setModuleCode(moduleCode);

        String resources = getParameter("resources");
        Set<RoleResourcePojo> roleResourceSet = null;
        if (CommonUtil.isNotEmpty(resources)) {
            JSONArray jsonArr = JSONArray.parseArray(resources);
            roleResourceSet = new HashSet<RoleResourcePojo>();
            for (Object obj : jsonArr) {
                JSONObject json = (JSONObject) obj;
                RoleResourcePojo pojo = new RoleResourcePojo();
                pojo.setResourceId(json.getInteger("id"));
                pojo.setResourceType(json.getString("type"));
                roleResourceSet.add(pojo);
            }
        }

        roleManagerService.updateRoleAndPermission(role, roleResourceSet);
        return success("修改角色信息成功。");

    }

    /**
     * Description: 删除角色<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteRole() throws FrameworkException {
        String ids = getParameter("ids", "删除的角色标识不能为空");
        roleManagerService.deleteRoles(CommonUtil.splitId(ids));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}
