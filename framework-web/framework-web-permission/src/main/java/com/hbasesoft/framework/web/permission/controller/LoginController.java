package com.hbasesoft.framework.web.permission.controller;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.AssertException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.service.LoginService;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.hbasesoft.framework.web.manager.controller.login <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping
public class LoginController extends BaseController {

    private static final Logger logger = new Logger(LoginController.class);

    private static final String LOGIN_PAGE = "permission/login/index";

    private static final String HOST_PAGE = "redirect:/";

    @Resource
    private LoginService loginService;

    @RequiresAuthentication
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        String homeUrl = ConfigHelper.getString("SYSTEM.HOME_PAGE_URL");
        Assert.notEmpty(homeUrl, "首页url地址未配置,配置项为：SYSTEM.HOME_PAGE_URL");
        return "forward:" + homeUrl;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String toLogin() {
        return LOGIN_PAGE;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(ModelMap map) {
        String username = null;
        try {
            username = getParameter("account", "用户名不能为空");
            String password = getParameter("password", "密码不能为空");
            checkVerifyCode(getParameter("verifyCode", "验证码不能为空"));

            Subject subject = SecurityUtils.getSubject();
            subject.login(new UsernamePasswordToken(username, password));
            return HOST_PAGE;
        }
        catch (Exception e) {
            logger.warn(e.getMessage(), e);
            setModelMap(map, username, 20025, e.getMessage());
        }
        return LOGIN_PAGE;
    }

    @RequiresAuthentication
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        loginService.logout();
        return "redirect:/login";
    }

    private void setModelMap(ModelMap map, String username, int code, String message) {
        map.put("account", username);
        map.put("code", code);
        map.put("message", message);
    }

    private void checkVerifyCode(String verifyCode) {
        if (!StringUtils.equalsIgnoreCase((String) WebUtil.getAttribute(WebConstant.SESSION_VERIFY_CODE), verifyCode)) {
            throw new AssertException(20025, "验证码不正确");
        }
        else {
            WebUtil.removeAttribute(WebConstant.SESSION_VERIFY_CODE);
        }
    }
}
