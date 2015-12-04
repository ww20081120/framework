package com.fccfc.framework.web.manager.controller.permission;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.config.core.ConfigHelper;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.LoginResult;
import com.fccfc.framework.web.manager.service.permission.LoginService;
import com.fccfc.framework.web.manager.utils.WebUtil;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.fccfc.framework.web.manager.controller.login <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping
public class LoginController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_PAGE = "permission/login/index";

    private static final String HOST_PAGE = "redirect:/";

    @Resource
    private LoginService loginService;

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
    public String login(HttpServletRequest request, ModelMap map) {
        String forward = LOGIN_PAGE;
        String username = null;
        try {
            username = getParameter("account", "用户名不能为空");
            String password = getParameter("password", "密码不能为空");
            checkVerifyCode(getParameter("verifyCode", "验证码不能为空"));

            LoginResult result = loginService.login(request, username, password);

            if (result.isResult()) {
                return HOST_PAGE;
            }

            setModelMap(map, username, result.getCode(), result.getMsg());
            if (result.getCode() == LoginResult.USER_PWD_EXPIRED.getCode()) {
                // 预留
                forward = LOGIN_PAGE;
            }
        }
        catch (Exception e) {
            logger.warn(e.getMessage(), e);
            setModelMap(map, username, 20025, e.getMessage());
        }
        return forward;
    }

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
