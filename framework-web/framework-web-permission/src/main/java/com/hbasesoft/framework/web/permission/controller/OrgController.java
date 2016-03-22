package com.hbasesoft.framework.web.permission.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.permission.bean.OrgPojo;
import com.hbasesoft.framework.web.permission.service.OrgService;

/**
 * <Description> <br>
 *
 * @author wk<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/15 <br>
 * @see com.hbasesoft.framework.web.manager.controller <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/permission/org")
public class OrgController extends BaseController {

    private static final String PAGE_INDEX = "permission/org/index";

    private static final String PAGE_ADD = "permission/org/add";

    private static final String PAGE_MODIFY = "permission/org/modify";

    @Resource
    private OrgService orgService;

    @RequiresPermissions("org:query")
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return PAGE_INDEX;
    }

    @RequiresPermissions("org:list")
    @ResponseBody
    @RequestMapping("/list")
    public String list() throws FrameworkException {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("orgId", "parentOrgId", "orgCode", "orgName");
        return JSON.toJSONString(orgService.listOrg(), filter, SerializerFeature.WriteMapNullValue);
    }

    @RequiresPermissions("org:add")
    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(ModelAndView modelAndView) throws FrameworkException {
        Long parentOrgId = getLongParameter("parentOrgId");
        if (parentOrgId != null) {
            OrgPojo pojo = orgService.queryOrg(parentOrgId);
            Assert.notNull(pojo, "组织信息不存在");
            modelAndView.addObject("parentOrgId", parentOrgId);
            modelAndView.addObject("parentOrgName", pojo.getOrgName());
        }
        modelAndView.setViewName(PAGE_ADD);
        return modelAndView;
    }

    @RequiresPermissions("org:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(OrgPojo orgPojo) throws FrameworkException {
        Assert.notNull(orgPojo, "组织信息不存在");
        Assert.notEmpty(orgPojo.getOrgCode(), "组织机构代码为必填字段");
        Assert.notEmpty(orgPojo.getOrgName(), "组织机构代码为必填字段");
        orgService.addOrg(orgPojo);
        return success("新增组织信息成功!");
    }

    @RequiresPermissions("org:remove")
    @ResponseBody
    @RequestMapping(value = "/remove/{orgId}", method = RequestMethod.POST)
    public Map<String, Object> remove(@PathVariable("orgId") Long orgId) throws FrameworkException {
        orgService.remove(orgId);
        return checkResult(true);
    }

    @RequiresPermissions("org:modify")
    @RequestMapping(value = "/toModify/{orgId}")
    public String toModify(@PathVariable("orgId") Long orgId, ModelMap modelMap) throws FrameworkException {
        OrgPojo pojo = orgService.queryOrg(orgId);
        Assert.notNull(pojo, "id为{0}组织不存在", orgId);
        modelMap.addAttribute("orgPojo", pojo);

        if (pojo.getParentOrgId() != null) {
            modelMap.addAttribute("parentOrgName", orgService.queryOrg(pojo.getParentOrgId()).getOrgName());
        }
        return PAGE_MODIFY;
    }

    @RequiresPermissions("org:modify")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(OrgPojo orgPojo) throws FrameworkException {
        Assert.notNull(orgPojo, "组织信息不存在");
        Assert.notNull(orgPojo.getOrgId(), "组织机构标示不存在");
        Assert.notEmpty(orgPojo.getOrgName(), "组织机构代码为必填字段");
        orgService.modifyOrg(orgPojo);
        return success("修改组织信息成功!");
    }

    @RequiresAuthentication
    @ResponseBody
    @RequestMapping(value = "/checkCode/{orgCode}")
    public Map<String, Object> checkCode(@PathVariable("orgCode") String orgCode) {
        return checkResult(orgService.checkCode(orgCode, getLongParameter("orgId")));
    }
}
