package com.fccfc.framework.web.manager.service.permission;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.DutyPojo;

import java.util.List;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.fccfc.framework.web.manager.service.org <br>
 * @since V1.0<br>
 */
public interface DutyService {

    List<DutyPojo> listDuty(Long orgId, Integer pageIndex, Integer pageSize) throws ServiceException;

    void addDuty(DutyPojo dutyPojo) throws ServiceException;

    void remove(Long[] ids) throws ServiceException;

    DutyPojo queryDuty(Long dutyId) throws ServiceException;

    void modifyDuty(DutyPojo dutyPojo) throws ServiceException;

    boolean checkName(String dutyId, String dutyName);
}
