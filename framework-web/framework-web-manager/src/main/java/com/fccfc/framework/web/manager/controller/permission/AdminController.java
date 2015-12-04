package com.fccfc.framework.web.manager.controller.permission;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.bean.permission.AdminBean;
import com.fccfc.framework.web.manager.bean.permission.AdminPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.AdminService;
import com.fccfc.framework.web.manager.utils.WebUtil;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin")
public class AdminController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static final String PAGE_ADD = "org/admin/add";

    private static final String PAGE_MODIFY = "org/admin/mod";

    private static final String PAGE_MODIFY_PWD = "org/admin/modpwd";
    @Resource
    private AdminService adminService;

    @ResponseBody
    @RequestMapping(value = "/query")
    public Map<String, Object> query() throws FrameworkException {
        String dutyId = getParameter("dutyId");
        String serchStr = getParameter("Str");

        PagerList<AdminPojo> adminList = (PagerList<AdminPojo>) adminService.queryAdmin(NumberUtils.toLong(dutyId), serchStr, getPageIndex(), getPageSize());

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
    @RequestMapping(value = "/toAdd")
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
    @RequestMapping(value = "/saveAdmin")
    public ModelAndView addAdmin(HttpServletRequest request, AdminBean adminBean) throws FrameworkException {
        if (null != adminBean) {
            adminBean.setLoginIp(WebUtil.getRemoteIP(request));
        }
        adminService.addAdmin(adminBean);

        return success("添加管理员成功");
    }

    /**
     * Description: 修改管理员----跳转页面<br>
     *
     * @param map
     * @return
     * @throws FrameworkException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/toModAdmin")
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
    @RequestMapping(value = "/modadmin")
    public ModelAndView changeAdminInfo(AdminPojo adminPojo) throws FrameworkException {
        adminService.modifyAdmin(adminPojo);
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
    @RequestMapping(value = "/toModPassword")
    public ModelAndView getModifyPasswordPage() throws FrameworkException {
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
    @RequestMapping(value = "/modpassword")
    public ModelAndView modityPassword() throws FrameworkException {
        adminService.resetPassword(getParameter("new_password"), Integer.parseInt(getParameter("adminId")));
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
    @RequestMapping(value = "/deladmin", method = RequestMethod.POST)
    public ResponseEntity<?> delAdmin() throws FrameworkException {
        String ids = getParameter("ids", "删除的管理员不能为空");
        adminService.delAdmin(CommonUtil.splitId(ids));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
    
    
    /**
     * 用户名校验
     */
    @ResponseBody
    @RequestMapping(value = "/checkOperatorName")
    public boolean checkOperatorName() {
        return  adminService.checkOperatorName(getParameter("username"));
    }
    
    /**
     * 管理员姓名校验
     */
    @ResponseBody
    @RequestMapping(value = "/checkAdminName")
    public boolean checkAdminName() {
        return adminService.checkAdminName(getParameter("adminId"),getParameter("name"));
    }
    
}
