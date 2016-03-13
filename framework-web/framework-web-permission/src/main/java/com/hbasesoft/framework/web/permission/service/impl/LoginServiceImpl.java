package com.hbasesoft.framework.web.permission.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.AccountPojo;
import com.hbasesoft.framework.web.permission.bean.AdminPojo;
import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.web.permission.dao.admin.AccountDao;
import com.hbasesoft.framework.web.permission.dao.admin.AdminDao;
import com.hbasesoft.framework.web.permission.dao.admin.OperatorDao;
import com.hbasesoft.framework.web.permission.dao.role.RoleResourceDao;
import com.hbasesoft.framework.web.permission.service.LoginResult;
import com.hbasesoft.framework.web.permission.service.LoginService;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.hbasesoft.framework.web.manager.service.login.impl <br>
 * @since V1.0<br>
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = new Logger(LoginServiceImpl.class);

    @Resource
    private AccountDao accountDao;

    @Resource
    private OperatorDao operatorDao;

    @Resource
    private AdminDao adminDao;

    @Resource
    private RoleResourceDao roleResourceDao;

    @Override
    public LoginResult login(HttpServletRequest request, String username, String password) {
        LoginResult result = LoginResult.LOGIN_SUCCESS;
        try {
            AccountPojo accountPojo = queryAccountPojo(username);
            if (null == accountPojo) {
                logger.info("User account isn't exist. [Username = {0}].", username);
                return LoginResult.USER_INCORRECT;
            }

            OperatorPojo operatorPojo = queryOperatorPojo(accountPojo.getOperatorId());
            if (null == operatorPojo) {
                logger.info("User operator isn't exist. [Username = {0}, Operator ID = {1}].", username,
                    accountPojo.getOperatorId());
                return LoginResult.USER_INCORRECT;
            }

            // 密码正确性
            if (!StringUtils.equals(CommonUtil.md5(password), operatorPojo.getPassword())) {
                logger.info("User account password incorrect. [Username = {0}].", username);
                modifyLoginFailedNum(username, operatorPojo);
                return LoginResult.USER_INCORRECT;
            }

            // 帐号状态
            if (!isAdmin(username)
                && !StringUtils.equals(PermissionConstant.STATE_AVAILABLE, operatorPojo.getState())) {
                logger.info("User account status incorrect. [Username = {0}].", username);
                modifyLoginFailedNum(username, operatorPojo);
                return LoginResult.USER_STATUS_INCORRECT;
            }

            // 是否锁定
            if (!isAdmin(username) && StringUtils.equals(PermissionConstant.YES, operatorPojo.getIsLocked())) {
                logger.info("User account status [isLocked]  incorrect. [Username = {0}].", username);
                modifyLoginFailedNum(username, operatorPojo);
                return LoginResult.USER_STATUS_INCORRECT;
            }

            // 密码是否过期
            if (!isAdmin(username) && isPwdExpired(operatorPojo.getPwdExpDate())) {
                logger.info("User account password overdue. [Username = {0}].", username);
                return LoginResult.USER_PWD_EXPIRED;
            }

            // 登录OK
            loginSuccessHandler(request, operatorPojo, accountPojo);

        }
        catch (Exception e) {
            logger.error("Login exception.", e);
            result = LoginResult.SYSTEM_ERROR;
        }
        return result;
    }

    @Override
    public void logout() {
        RequestAttributes requestAttr = RequestContextHolder.getRequestAttributes();
        if (requestAttr instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttr).getRequest();
            HttpSession session = request.getSession();
            session.removeAttribute(PermissionConstant.SESSION_OPERATOR);
            session.removeAttribute(PermissionConstant.SESSION_ADMIN);
            session.removeAttribute(PermissionConstant.SESSION_PERMISSIONS);
            session.removeAttribute(PermissionConstant.SESSION_PERMISSIONS_DATA);
            session.invalidate();
        }
    }

    private void loginSuccessHandler(HttpServletRequest request, OperatorPojo operatorPojo, AccountPojo accountPojo)
        throws DaoException {
        //
        operatorPojo.setLoginFail(0);
        operatorPojo.setIsLocked(PermissionConstant.NO);
        operatorPojo.setLastIp(WebUtil.getRemoteIP(request));
        operatorPojo.setLastLoginDate(new Date());
        operatorDao.update(operatorPojo);

        WebUtil.setAttribute(PermissionConstant.SESSION_OPERATOR, operatorPojo);
        WebUtil.setAttribute(PermissionConstant.SESSION_ACCOUNT, accountPojo);
        WebUtil.setAttribute(PermissionConstant.SESSION_ADMIN, queryAdminPojo(operatorPojo.getOperatorId()));

        setPermission(operatorPojo.getDutyId());
    }

    private void setPermission(Long dutyId) throws DaoException {
        Set<String> permissions = new HashSet<String>();
        Set<Long> dataPermissions = new HashSet<Long>();
        List<RoleResourcePojo> permissionList = roleResourceDao.selectListRoleResourceByDutyId(dutyId,
            ConfigHelper.getModuleCode());
        if (CommonUtil.isNotEmpty(permissionList)) {
            for (RoleResourcePojo resourcePojo : permissionList) {
                String resourceId = String.valueOf(resourcePojo.getResourceId());
                if (StringUtils.equals(RoleResourcePojo.RESOURCE_TYPE_MENU, resourcePojo.getResourceType())) {
                    permissions.add(resourceId);
                }
                else if (StringUtils.equals(RoleResourcePojo.RESOURCE_TYPE_ORG, resourcePojo.getResourceType())) {
                    dataPermissions.add(NumberUtils.toLong(resourceId));
                }
            }
        }
        WebUtil.setAttribute(PermissionConstant.SESSION_PERMISSIONS, permissions);
        WebUtil.setAttribute(PermissionConstant.SESSION_PERMISSIONS_DATA, dataPermissions);
    }

    private void modifyLoginFailedNum(String username, OperatorPojo operatorPojo) throws DaoException {
        if (!isAdmin(username)) {
            operatorPojo.setLoginFail((null != operatorPojo.getLoginFail() ? operatorPojo.getLoginFail() : 0) + 1);
            if (operatorPojo.getLoginFail() > 5) {
                operatorPojo.setIsLocked(PermissionConstant.YES);
                operatorPojo.setLoginFail(0);
            }
            operatorDao.update(operatorPojo);
        }
        logger.info("Users account for [username = {0}] of user login failed.", username);
    }

    private boolean isAdmin(String account) {
        return StringUtils.equals("admin", account);
    }

    private boolean isPwdExpired(Date pwdExpireDate) {
        if (null == pwdExpireDate) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date now = cal.getTime();

        cal.setTime(pwdExpireDate);
        Date expire = cal.getTime();
        return now.compareTo(expire) > 0;
    }

    private OperatorPojo queryOperatorPojo(Integer operatorId) throws DaoException {
        return operatorDao.getById(OperatorPojo.class, operatorId);
    }

    private AccountPojo queryAccountPojo(String username) throws DaoException {
        AccountPojo paramPojo = new AccountPojo();
        paramPojo.setAccountValue(username);
        return accountDao.getByEntity(paramPojo);
    }

    private AdminPojo queryAdminPojo(Integer operatorId) throws DaoException {
        AdminPojo paramPojo = new AdminPojo();
        paramPojo.setOperatorId(operatorId);
        return adminDao.getByEntity(paramPojo);
    }
}
