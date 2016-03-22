package com.hbasesoft.framework.web.permission.service;

import java.util.Set;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.web.permission.bean.LoginResult;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.hbasesoft.framework.web.manager.service.login <br>
 * @since V1.0<br>
 */
public interface LoginService {

    LoginResult login(String username, String password);

    void logout();

    Set<String> queryPermissionByDutyId(Long dutyId) throws ServiceException;
}
