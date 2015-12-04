package com.fccfc.framework.web.manager.controller.permission;

import javax.annotation.Resource;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.web.manager.bean.permission.OrgPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.OrgService;

/**
 * <Description> <br>
 *
 * @author wk<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/15 <br>
 * @see com.fccfc.framework.web.manager.controller <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/permission/org")
public class OrgController extends AbstractController {

    private static final String PAGE_INDEX = "permission/org/index";

    private static final String PAGE_ADD = "permission/org/add";

    private static final String PAGE_MODIFY = "permission/org/modify";

    @Resource
    private OrgService orgService;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return PAGE_INDEX;
    }

    @ResponseBody
    @RequestMapping("/list")
    public String list() throws FrameworkException {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("orgId", "parentOrgId", "orgName", "permission");
        return JSON.toJSONString(orgService.listOrg(), filter, SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(ModelAndView modelAndView) {
        modelAndView.addObject("parentOrgId", getParameter("parentOrgId"));
        modelAndView.addObject("parentOrgName", getParameter("parentOrgName"));
        modelAndView.setViewName(PAGE_ADD);
        return modelAndView;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(OrgPojo orgPojo) throws FrameworkException {
        orgService.addOrg(orgPojo);
        return success("新增组织信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> remove() throws FrameworkException {
        String id = getParameter("id", "删除的组织标识不能为空");
        orgService.remove(NumberUtils.toLong(id));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @RequestMapping(value = "/toModify")
    public String toModify(ModelMap modelMap) throws FrameworkException {
        String orgId = getParameter("orgId");
        modelMap.addAttribute("parentOrgName", getParameter("parentOrgName"));
        modelMap.addAttribute("orgPojo", orgService.queryOrg(NumberUtils.toLong(orgId)));
        return PAGE_MODIFY;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(OrgPojo orgPojo) throws FrameworkException {
        orgService.modifyOrg(orgPojo);
        return success("修改组织信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/checkCode")
    public boolean checkCode() {
        return orgService.checkCode(getParameter("orgId"), getParameter("orgCode"));
    }

    @ResponseBody
    @RequestMapping(value = "/checkName")
    public boolean checkName() {
        return orgService.checkName(getParameter("orgId"), getParameter("orgName"));
    }
}
