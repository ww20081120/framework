package com.fccfc.framework.web.manager.service.permission;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.AssertException;

import javax.servlet.http.HttpServletRequest;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.fccfc.framework.web.manager.service.login <br>
 * @since V1.0<br>
 */
public interface LoginService {

    LoginResult login(HttpServletRequest request, String username, String password);

    void logout();
}
