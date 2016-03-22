package com.hbasesoft.framework.web.permission.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.permission.bean.DutyPojo;
import com.hbasesoft.framework.web.permission.bean.OrgPojo;
import com.hbasesoft.framework.web.permission.service.DutyService;
import com.hbasesoft.framework.web.permission.service.OrgService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.db.core.utils.PagerList;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.hbasesoft.framework.web.manager.controller.org <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/permission/duty")
public class DutyController extends BaseController {

    private static final String PAGE_ADD = "permission/duty/add";

    private static final String PAGE_MODIFY = "permission/duty/modify";

    @Resource
    private DutyService dutyService;

    @Resource
    private OrgService orgService;

    @RequiresPermissions("duty:query")
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> list() throws FrameworkException {
        Map<String, Object> result = new HashMap<String, Object>();
        Long id = NumberUtils.toLong(getParameter("orgId"));
        PagerList<DutyPojo> list = (PagerList<DutyPojo>) dutyService.listDuty(id, getPageIndex(), getPageSize());
        result.put("data", list);
        result.put("pageIndex", list.getPageIndex());
        result.put("pageSize", list.getPageSize());
        result.put("totalCount", list.getTotalCount());
        result.put("totalPage", list.getTotalPage());
        return result;
    }

    @RequiresPermissions("duty:add")
    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(ModelAndView modelAndView) throws FrameworkException {
        Long orgId = getLongParameter("orgId", "没有选择组织信息");
        OrgPojo org = orgService.queryOrg(orgId);
        Assert.notNull(org, "组织信息不存在");
        modelAndView.addObject("orgInfo", org);
        modelAndView.setViewName(PAGE_ADD);
        return modelAndView;
    }

    @RequiresPermissions("duty:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(DutyPojo dutyPojo) throws FrameworkException {
        Assert.notNull(dutyPojo, "岗位信息不存在");
        Assert.notEmpty(dutyPojo.getDutyName(), "岗位名称不存在");
        Assert.notNull(dutyPojo.getOrgId(), "组织信息不存在");
        dutyService.addDuty(dutyPojo);
        return success("新增岗位信息成功。");
    }

    @RequiresPermissions("duty:remove")
    @ResponseBody
    @RequestMapping(value = "/remove/{dutyId}", method = RequestMethod.POST)
    public Map<String, Object> remove(@PathVariable("dutyId") Long dutyId) throws FrameworkException {
        dutyService.remove(new Long[] {
            dutyId
        });
        return checkResult(true);
    }

    @RequiresPermissions("duty:modify")
    @RequestMapping(value = "/toModify/{dutyId}")
    public String toModify(@PathVariable("dutyId") Long dutyId, ModelMap modelMap) throws FrameworkException {
        DutyPojo duty = dutyService.queryDuty(dutyId);
        Assert.notNull(duty, "岗位信息不存在");
        modelMap.addAttribute("duty", duty);
        return PAGE_MODIFY;
    }

    @RequiresPermissions("duty:modify")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(DutyPojo dutyPojo) throws FrameworkException {
        Assert.notNull(dutyPojo, "岗位信息不存在");
        Assert.notNull(dutyPojo.getDutyId(), "岗位信息不存在");
        Assert.notEmpty(dutyPojo.getDutyName(), "岗位名称不存在");
        dutyService.modifyDuty(dutyPojo);
        return success("修改岗位信息成功!");
    }
}
