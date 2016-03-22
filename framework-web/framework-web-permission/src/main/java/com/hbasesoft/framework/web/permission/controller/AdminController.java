package com.hbasesoft.framework.web.permission.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.bean.AdminPojo;
import com.hbasesoft.framework.web.permission.service.AdminService;

@Controller
@RequestMapping(value = "/permission/admin")
public class AdminController extends BaseController {

    private static final String PAGE_ADD = "permission/admin/add";

    private static final String PAGE_MODIFY = "permission/admin/modify";

    private static final String PAGE_MODIFY_PWD = "permission/admin/modifyPwd";

    private static final String PAGE_MOD = "permission/admin/mod";

    private static final String PAGE_INFO = "permission/admin/info";

    @Resource
    private AdminService adminService;

    @RequiresPermissions("admin:query")
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> query() throws FrameworkException {
        Long dutyId = getLongParameter("dutyId");
        Long orgId = getLongParameter("orgId");
        String serchStr = getParameter("queryStr");

        PagerList<AdminPojo> adminList = (PagerList<AdminPojo>) adminService.queryAdmin(orgId, dutyId, serchStr,
            getPageIndex(), getPageSize());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", adminList);
        result.put("pageIndex", adminList.getPageIndex());
        result.put("pageSize", adminList.getPageSize());
        result.put("totalCount", adminList.getTotalCount());
        result.put("totalPage", adminList.getTotalPage());
        return result;
    }

    /**
     * Description: 添加----跳转页面<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:add")
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public String getAddAdminPage(HttpServletRequest request) throws FrameworkException {
        request.setAttribute("dutyId", getParameter("dutyId", "没有获取岗位标识"));
        return PAGE_ADD;
    }

    /**
     * Description: 添加<br>
     *
     * @param request
     * @return
     * @throws FrameworkException <br>
     * @author 于梦雅<br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:add")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(HttpServletRequest request, AdminPojo adminPojo) throws FrameworkException {
        if (null != adminPojo) {
            adminPojo.getOperatorPojo().setRegistIp(WebUtil.getRemoteIP());
        }
        adminService.addAdmin(adminPojo);

        return success("添加管理员成功");
    }

    /**
     * Description: 修改管理员----跳转页面<br>
     *
     * @param map
     * @return
     * @throws FrameworkException <br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:modify")
    @RequestMapping(value = "/toModify", method = RequestMethod.GET)
    public ModelAndView toModify(ModelMap map) throws FrameworkException {
        String adminIdStr = getParameter("adminId");
        AdminPojo admin = adminService.selectAdmin(NumberUtils.toInt(adminIdStr));
        map.put("admin", admin);
        return new ModelAndView(PAGE_MODIFY, map);
    }

    /**
     * Description: 修改管理员操作<br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 于梦雅<br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:modify")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(AdminPojo adminPojo) throws FrameworkException {
        adminService.modify(adminPojo);
        return success("修改管理员成功");
    }

    /**
     * Description: 重置密码---跳转页面<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author ymy<br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:modifyPwd")
    @RequestMapping(value = "/toModifyPwd", method = RequestMethod.GET)
    public ModelAndView toModifyPwd() throws FrameworkException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("adminId", getParameter("adminId"));
        return new ModelAndView(PAGE_MODIFY_PWD, map);
    }

    /**
     * Description: 重置密码<br>
     *
     * @return
     * @throws FrameworkException
     * @author ymy<br>
     * @taskId <br>
     */
    @RequiresPermissions("admin:modifyPwd")
    @RequestMapping(value = "/modify/pwd", method = RequestMethod.POST)
    public ModelAndView modifyPwd() throws FrameworkException {
        adminService.modifyPwd(getParameter("new_password"), Integer.parseInt(getParameter("adminId")));
        return success("重置密码成功");
    }

    /**
     * Description: 删除<br>
     *
     * @throws FrameworkException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequiresPermissions("admin:remove")
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> remove() throws FrameworkException {
        String ids = getParameter("ids", "删除的管理员不能为空");
        adminService.remove(CommonUtil.splitId(ids));
        return new ResponseEntity<Object>(GlobalConstants.BLANK, HttpStatus.OK);
    }

    /**
     * 用户名校验
     */
    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/checkOperatorName", method = RequestMethod.GET)
    public boolean checkOperatorName() {
        return adminService.checkOperatorName(getParameter("operatorPojo.userName"));
    }

    /**
     * 管理员姓名校验
     */
    @ResponseBody
    @RequiresAuthentication
    @RequestMapping(value = "/checkAdminName", method = RequestMethod.GET)
    public boolean checkAdminName() {
        return adminService.checkAdminName(getParameter("adminId"), getParameter("adminName"));
    }

    @ResponseBody
    @RequiresPermissions("admin:query")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ModelAndView info(HttpServletRequest request) throws FrameworkException {
        /** session里获得当前登录的操作员ID */
        Integer operatorId = WebUtil.getCurrentOperatorId();
        AdminPojo admin = adminService.getAdminByOperatorId(operatorId);
        // 账号类型: P-平台新增账号；M-手机号；E-邮箱
        switch (admin.getAccountType()) {
            case "P":
                admin.setAccountType("平台新增账号");
                break;
            case "M":
                admin.setAccountType("手机号");
                break;
            case "E":
                admin.setAccountType("邮箱");
                break;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("admin", admin);
        return new ModelAndView(PAGE_INFO, map);
    }

    @RequiresPermissions("admin:modify")
    @RequestMapping(value = "/toMod", method = RequestMethod.GET)
    public ModelAndView toMod() throws FrameworkException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("operatorId", getParameter("operatorId"));
        return new ModelAndView(PAGE_MOD, map);
    }

    @RequiresPermissions("admin:modify")
    @RequestMapping(value = "/modifyInfo", method = RequestMethod.POST)
    public ModelAndView modifyInfo(AdminPojo adminPojo) throws FrameworkException {
        adminService.modify(adminPojo);
        return new ModelAndView("redirect:/permission/admin/info");
    }

}
