package com.fccfc.framework.web.manager.service.permission.impl;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.OrgPojo;
import com.fccfc.framework.web.manager.dao.permission.duty.DutyDao;
import com.fccfc.framework.web.manager.dao.permission.org.OrgDao;
import com.fccfc.framework.web.manager.service.permission.OrgService;
import com.fccfc.framework.web.manager.utils.WebUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/25 <br>
 * @see com.fccfc.framework.web.manager.service.org.impl <br>
 * @since V1.0<br>
 */
@Service
public class OrgServiceImpl implements OrgService {

    private static final Logger logger = LoggerFactory.getLogger(OrgServiceImpl.class);

    @Resource
    private OrgDao orgDao;

    @Resource
    private DutyDao dutyDao;

    @Override
    public List<OrgPojo> listOrg() throws ServiceException {
        try {
            Set<Long> dataPermissions = (Set<Long>) WebUtil.getAttribute(ManagerConstant.SESSION_PERMISSIONS_DATA);

            List<OrgPojo> orgPojoList = orgDao.selectList(null, -1, -1);
            for (OrgPojo org : orgPojoList) {
                org.setPermission(WebUtil.isAdminAccount() || dataPermissions.contains(org.getOrgId()));
            }
            return orgPojoList;
        } catch (DaoException e) {
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
            pojo.setState(ManagerConstant.STATE_AVAILABLE);
            pojo.setStateDate(new Date());
            orgDao.save(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void remove(Long id) throws ServiceException {
        try {
            Assert.isEmpty(orgDao.selectList(id, -1, -1), "该组织有子组织，不能删除");
            Assert.isEmpty(dutyDao.selectList(id, -1, -1), "该组织有岗位引用，不能删除");

            int lines = orgDao.deleteById(id);
            logger.info("Delete organizations effect numbers [lines={}]. ", lines);
        } catch (DaoException e) {
            throw new ServiceException(e);
        } catch (AssertException e) {
            throw new ServiceException(e.getCode(), e.getMessage());
        }
    }

    @Override
    public OrgPojo queryOrg(Long id) throws ServiceException {
        try {
            return orgDao.getById(OrgPojo.class, id);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void modifyOrg(OrgPojo pojo) throws ServiceException {
        try {
            int lines = orgDao.update(pojo);
            logger.info("Modify org successful. effect lines [lines = {}].", lines);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkCode(String orgId, String orgCode) {
        boolean result = false;
        try {
            OrgPojo paramPojo = new OrgPojo();
            paramPojo.setOrgCode(orgCode);
            paramPojo.setState(ManagerConstant.STATE_AVAILABLE);
            OrgPojo pojo = orgDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(orgId)) {
                result = NumberUtils.toLong(orgId) == pojo.getOrgId();
            }
        } catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    @Override
    public boolean checkName(String orgId, String orgName) {
        boolean result = false;
        try {
            OrgPojo paramPojo = new OrgPojo();
            paramPojo.setOrgName(orgName);
            paramPojo.setState(ManagerConstant.STATE_AVAILABLE);
            OrgPojo pojo = orgDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(orgId)) {
                result = NumberUtils.toLong(orgId) == pojo.getOrgId();
            }
        } catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }
}
