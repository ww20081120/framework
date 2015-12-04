package com.fccfc.framework.web.manager.controller.permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.bean.permission.DutyPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.DutyService;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.fccfc.framework.web.manager.controller.org <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/duty")
public class DutyController extends AbstractController {

    private static final String PAGE_ADD = "org/duty/add";

    private static final String PAGE_MODIFY = "org/duty/modify";

    @Resource
    private DutyService dutyService;

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST)
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

    @RequestMapping(value = "/toAdd")
    public ModelAndView toAdd(ModelAndView modelAndView) {
        try {
            modelAndView.addObject("orgId", getParameter("orgId", "没有选择组织信息"));
            modelAndView.addObject("orgName", getParameter("orgName"));

            modelAndView.setViewName(PAGE_ADD);
        } catch (AssertException e) {
            modelAndView = fail(e.getMessage());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(DutyPojo dutyPojo) throws FrameworkException {
        dutyService.addDuty(dutyPojo);
        return success("新增岗位信息成功。");
    }

    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> remove() throws FrameworkException {
        String ids = getParameter("ids", "删除的岗位标识不能为空");
        dutyService.remove(CommonUtil.splitIdsByLong(ids, ","));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @RequestMapping(value = "/toModify")
    public String toModify(ModelMap modelMap) throws FrameworkException {
        String dutyId = getParameter("dutyId");
        modelMap.addAttribute("orgName", getParameter("orgName"));
        modelMap.addAttribute("duty", dutyService.queryDuty(NumberUtils.toLong(dutyId)));
        return PAGE_MODIFY;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(DutyPojo dutyPojo) throws FrameworkException {
        dutyService.modifyDuty(dutyPojo);
        return success("修改岗位信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/checkName")
    public boolean checkName() {
        return dutyService.checkName(getParameter("dutyId"), getParameter("dutyName"));
    }
}
