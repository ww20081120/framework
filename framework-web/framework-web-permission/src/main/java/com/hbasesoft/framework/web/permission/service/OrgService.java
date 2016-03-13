package com.hbasesoft.framework.web.permission.service;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.OrgPojo;
import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.hbasesoft.framework.web.manager.service.org <br>
 * @since V1.0<br>
 */
public interface OrgService {
    List<OrgPojo> listOrg() throws ServiceException;

    void addOrg(OrgPojo pojo) throws ServiceException;

    void remove(Long id) throws ServiceException;

    OrgPojo queryOrg(Long id) throws ServiceException;

    void modifyOrg(OrgPojo pojo) throws ServiceException;

    boolean checkCode(String orgCode, Long orgId);
}
