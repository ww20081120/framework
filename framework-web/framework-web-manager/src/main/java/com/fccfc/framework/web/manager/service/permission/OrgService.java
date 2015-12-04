package com.fccfc.framework.web.manager.service.permission;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.OrgPojo;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.fccfc.framework.web.manager.service.org <br>
 * @since V1.0<br>
 */
public interface OrgService {
    List<OrgPojo> listOrg() throws ServiceException;

    void addOrg(OrgPojo pojo) throws ServiceException;

    void remove(Long id) throws ServiceException;

    OrgPojo queryOrg(Long id) throws ServiceException;

    void modifyOrg(OrgPojo pojo) throws ServiceException;

    boolean checkCode(String orgId, String orgCode);

    boolean checkName(String orgId, String orgName);
}
