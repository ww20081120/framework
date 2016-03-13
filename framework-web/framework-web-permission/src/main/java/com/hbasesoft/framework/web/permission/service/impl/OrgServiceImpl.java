package com.hbasesoft.framework.web.permission.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.OrgPojo;
import com.hbasesoft.framework.web.permission.dao.duty.DutyDao;
import com.hbasesoft.framework.web.permission.dao.org.OrgDao;
import com.hbasesoft.framework.web.permission.service.OrgService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.AssertException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.hbasesoft.framework.web.manager.service.org.impl <br>
 * @since V1.0<br>
 */
@Service
public class OrgServiceImpl implements OrgService {

    private static final Logger logger = new Logger(OrgServiceImpl.class);

    @Resource
    private OrgDao orgDao;

    @Resource
    private DutyDao dutyDao;

    @Override
    public List<OrgPojo> listOrg() throws ServiceException {
        try {
            return orgDao.selectList(null, -1, -1);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addOrg(OrgPojo pojo) throws ServiceException {
        try {
            if (null == pojo) {
                pojo = new OrgPojo();
            }
            pojo.setOperatorId(WebUtil.getCurrentOperatorId());
            pojo.setCreateTime(new Date());
            pojo.setState(PermissionConstant.STATE_AVAILABLE);
            pojo.setStateDate(new Date());
            orgDao.save(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void remove(Long id) throws ServiceException {
        try {
            OrgPojo org = orgDao.getById(OrgPojo.class, id);
            Assert.notNull(org, "组织信息不存在");

            Assert.isEmpty(orgDao.selectList(id, 1, 1), "该组织有子组织，不能删除");
            Assert.isEmpty(dutyDao.selectDuty(id, 1, 1), "该组织有岗位引用，不能删除");

            org.setState(PermissionConstant.STATE_UNAVAILABLE);
            org.setStateDate(new Date());

            orgDao.update(org);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (AssertException e) {
            throw new ServiceException(e.getCode(), e.getMessage());
        }
    }

    @Override
    public OrgPojo queryOrg(Long id) throws ServiceException {
        try {
            return orgDao.getById(OrgPojo.class, id);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void modifyOrg(OrgPojo pojo) throws ServiceException {
        try {
            OrgPojo org = orgDao.getById(OrgPojo.class, pojo.getOrgId());
            Assert.notNull(org, "组织信息不存在");
            org.setOrgName(pojo.getOrgName());
            orgDao.update(org);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkCode(String orgCode, Long orgId) {
        try {
            OrgPojo paramPojo = new OrgPojo();
            paramPojo.setOrgCode(orgCode);
            paramPojo.setState(PermissionConstant.STATE_AVAILABLE);
            OrgPojo pojo = orgDao.getByEntity(paramPojo);
            return pojo == null || pojo.getOrgId().equals(orgId);
        }
        catch (DaoException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
}
