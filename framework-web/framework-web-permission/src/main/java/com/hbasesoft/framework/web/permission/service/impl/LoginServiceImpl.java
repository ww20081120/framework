package com.hbasesoft.framework.web.permission.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.AccountPojo;
import com.hbasesoft.framework.web.permission.bean.LoginResult;
import com.hbasesoft.framework.web.permission.bean.RoleResourcePojo;
import com.hbasesoft.framework.web.permission.dao.admin.AccountDao;
import com.hbasesoft.framework.web.permission.dao.admin.AdminDao;
import com.hbasesoft.framework.web.permission.dao.admin.OperatorDao;
import com.hbasesoft.framework.web.permission.dao.duty.DutyDao;
import com.hbasesoft.framework.web.permission.dao.role.RoleResourceDao;
import com.hbasesoft.framework.web.permission.service.LoginService;

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

    @Resource
    private DutyDao dutyDao;

    @Override
    public LoginResult login(String username, String password) {
        LoginResult result = LoginResult.LOGIN_SUCCESS;
        try {
            OperatorPojo operatorPojo = operatorDao.getOperatorByAccount(username, AccountPojo.ACCOUNT_TYPE_PLATFORM);
            if (null == operatorPojo) {
                logger.info("User operator isn't exist. [Username = {0}].", username);
                return LoginResult.USER_INCORRECT;
            }

            // 密码正确性
            if (DataUtil.matchPassword(operatorPojo.getPassword(), DataUtil.encryptPassowrd(password))) {
                logger.info("User account password incorrect. [Username = {0}].", username);
                modifyLoginFailedNum(username, operatorPojo);
                return LoginResult.USER_INCORRECT;
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
            loginSuccessHandler(operatorPojo);

        }
        catch (Exception e) {
            logger.error("Login exception.", e);
            result = LoginResult.SYSTEM_ERROR;
        }
        return result;
    }

    @Override
    public void logout() {
        SecurityUtils.getSubject().logout();
    }

    private void loginSuccessHandler(OperatorPojo operatorPojo) throws DaoException {
        operatorPojo.setLoginFail(0);
        operatorPojo.setIsLocked(PermissionConstant.NO);
        operatorPojo.setLastIp(WebUtil.getRemoteIP());
        operatorPojo.setLastLoginDate(new Date());
        operatorDao.update(operatorPojo);

        WebUtil.setAttribute(PermissionConstant.SESSION_OPERATOR, operatorPojo);
        WebUtil.setAttribute(PermissionConstant.SESSION_ADMIN,
            adminDao.getAdminByOperatorId(operatorPojo.getOperatorId()));

        Set<String> roleCodeSet = new HashSet<String>();
        List<Long> roleIds = dutyDao.selectListDutyRole(operatorPojo.getDutyId());
        if (CommonUtil.isNotEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                roleCodeSet.add(roleId.toString());
            }
        }
        WebUtil.setAttribute(PermissionConstant.SESSION_ROLE_DATA, roleCodeSet);
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

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param dutyId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    @Transactional(readOnly = true)
    public Set<String> queryPermissionByDutyId(Long dutyId) throws ServiceException {
        Set<String> permissions = new HashSet<String>();

        List<RoleResourcePojo> permissionList;
        try {
            permissionList = roleResourceDao.selectListRoleResourceByDutyId(dutyId, ConfigHelper.getModuleCode());
            if (CommonUtil.isNotEmpty(permissionList)) {
                for (RoleResourcePojo resourcePojo : permissionList) {
                    permissions.add(resourcePojo.getResourceCode());
                }
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        return permissions;
    }
}
