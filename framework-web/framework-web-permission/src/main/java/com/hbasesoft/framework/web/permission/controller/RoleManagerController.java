/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.RolePojo;
import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.web.permission.service.RoleManagerService;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.manager.controller.permission <br>
 */
@Controller
@RequestMapping("/permission/role")
public class RoleManagerController extends BaseController {

    private static final String PAGE_INDEX = "permission/role/index";

    private static final String PAGE_ADD = "permission/role/add";

    private static final String PAGE_MODIFY = "permission/role/modify";

    @Resource
    private RoleManagerService roleManagerService;

    /**
     * Description: 页面跳转<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequiresPermissions("role:query")
    @RequestMapping(method = RequestMethod.GET)
    public String toRole() {
        return PAGE_INDEX;
    }

    /**
     * Description: 查询列表数据<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException
     */
    @RequiresPermissions("role:query")
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Map<String, Object> query() throws ServiceException {
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
    @RequiresPermissions("role:query")
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
    @RequiresPermissions("role:query")
    @RequestMapping(value = "/query/{roleId}", method = RequestMethod.POST)
    public ModelAndView query(@PathVariable("roleId") Integer roleId, ModelMap map) throws FrameworkException {
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
    @RequiresPermissions("role:add")
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public String toAdd() {
        return PAGE_ADD;
    }

    /**
     * Description: 新增role<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @RequiresPermissions("role:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add() throws FrameworkException {
        String roleName = getParameter("roleName", "角色名称不能为空");
        String moduleCode = getParameter("moduleCode", "模块代码不能为空");

        RolePojo role = new RolePojo();
        role.setRoleName(roleName);
        role.setModuleCode(moduleCode);
        role.setOperatorId(WebUtil.getCurrentOperatorId());
        role.setExt(getParameter("ext"));
        role.setState(PermissionConstant.STATE_AVAILABLE);

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
    @RequiresPermissions("role:modify")
    @RequestMapping(value = "/toModify/{roleId}", method = RequestMethod.GET)
    public ModelAndView toModify(@PathVariable("roleId") Integer roleId, ModelMap map) throws FrameworkException {
        Assert.notNull(roleId, "角色标识不能为空");
        RolePojo role = roleManagerService.queryRole(roleId);
        Assert.notNull(role, "角色不存在");
        map.put("role", role);
        map.put("resources", roleManagerService.queryRoleResourceList(roleId));
        return new ModelAndView(PAGE_MODIFY, map);
    }

    /**
     * Description: 修改role<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException
     */
    @RequiresPermissions("role:modify")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify() throws FrameworkException {
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
    @RequiresPermissions("role:remove")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Map<String, Object> remove() throws FrameworkException {
        String ids = getParameter("ids", "删除的角色标识不能为空");
        roleManagerService.deleteRoles(CommonUtil.splitId(ids));
        return this.checkResult(true);
    }
}
