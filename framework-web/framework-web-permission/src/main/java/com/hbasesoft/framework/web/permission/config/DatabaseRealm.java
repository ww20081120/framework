/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.permission.config;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.LoginResult;
import com.hbasesoft.framework.web.permission.service.LoginService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年3月22日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.web.permission.config <br>
 */
@Component
public class DatabaseRealm extends AuthorizingRealm {

    /**
     * LOG
     */
    private static final Logger LOG = new Logger(DatabaseRealm.class);

    @Resource
    private LoginService loginService;

    @Resource
    private SessionFactory sessionFactory;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param principals
     * @return <br>
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        OperatorPojo operator = WebUtil.getCurrentOperator();
        if (operator != null) {
            try {
                Set<String> permissions = loginService.queryPermissionByDutyId(operator.getDutyId());
                if ("admin".equals(operator.getUserName())) {
                    permissions.add("*");
                }

                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
                info.setStringPermissions(permissions);
                info.setRoles((Set<String>) WebUtil.getAttribute(PermissionConstant.SESSION_ROLE_DATA));
                return info;
            }
            catch (ServiceException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param token
     * @return
     * @throws AuthenticationException <br>
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        if (authenticationToken instanceof UsernamePasswordToken) {
            UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
            LoginResult loginResult = loginService.login(token.getUsername(), new String(token.getPassword()));
            if (loginResult == LoginResult.LOGIN_SUCCESS) {
                return new SimpleAuthenticationInfo(token.getUsername(), token.getPassword(), getName());
            }
            else {
                throw new AuthenticationException("登录失败，错误码：" + loginResult);
            }
        }
        return null;
    }
}
