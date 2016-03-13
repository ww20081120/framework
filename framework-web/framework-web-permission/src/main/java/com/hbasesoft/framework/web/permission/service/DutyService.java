package com.hbasesoft.framework.web.permission.service;

import java.util.List;

import com.hbasesoft.framework.web.permission.bean.DutyPojo;
import com.hbasesoft.framework.common.ServiceException;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.hbasesoft.framework.web.manager.service.org <br>
 * @since V1.0<br>
 */
public interface DutyService {

    List<DutyPojo> listDuty(Long orgId, Integer pageIndex, Integer pageSize) throws ServiceException;

    void addDuty(DutyPojo dutyPojo) throws ServiceException;

    void remove(Long[] ids) throws ServiceException;

    DutyPojo queryDuty(Long dutyId) throws ServiceException;

    void modifyDuty(DutyPojo dutyPojo) throws ServiceException;
}
