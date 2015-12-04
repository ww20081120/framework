package com.fccfc.framework.web.manager.service.permission.impl;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.DutyPojo;
import com.fccfc.framework.web.manager.dao.permission.admin.OperatorDao;
import com.fccfc.framework.web.manager.dao.permission.duty.DutyDao;
import com.fccfc.framework.web.manager.service.permission.DutyService;
import com.fccfc.framework.web.manager.utils.WebUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.fccfc.framework.web.manager.service.org.impl <br>
 * @since V1.0<br>
 */
@Service
public class DutyServiceImpl implements DutyService {

    private static final Logger logger = LoggerFactory.getLogger(DutyServiceImpl.class);

    @Resource
    private DutyDao dutyDao;

    @Resource
    private OperatorDao operatorDao;

    @Override
    public List<DutyPojo> listDuty(Long orgId, Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return dutyDao.selectList(orgId, pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addDuty(DutyPojo dutyPojo) throws ServiceException {
        try {
            dutyPojo.setCreateTime(new Date());
            dutyPojo.setOperatorId(WebUtil.getCurrentOperatorId());
            dutyPojo.setState(ManagerConstant.STATE_AVAILABLE);
            dutyPojo.setStateDate(new Date());
            dutyDao.save(dutyPojo);

            String roles = dutyPojo.getRoleList();
            int lines = dutyDao.insertDutyRole(dutyPojo.getDutyId(), CommonUtil.splitIdsByLong(roles, ","));
            logger.info("Insert duty-role successful. effect lines [lines = {}].", lines);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void remove(Long[] ids) throws ServiceException {
        try {
            List<Long> remain = new ArrayList<Long>();
            for (int i = 0; i < ids.length; i++) {
                Long id = ids[i];
                if (CollectionUtils.isEmpty(operatorDao.selectList(id, -1, -1))) {
                    remain.add(id);
                }
            }
            if(CollectionUtils.isNotEmpty(remain)){
                Long[] remainIds = remain.toArray(new Long[]{});
                int lines = dutyDao.deleteByIds(remainIds);
                logger.info("Delete duty successful. effect lines [lines = {}]", lines);

                lines = dutyDao.deleteDutyRole(remainIds);
                logger.info("Delete duty-role successful. effect lines [lines = {}]", lines);
            }
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public DutyPojo queryDuty(Long dutyId) throws ServiceException {
        try {
            DutyPojo pojo = dutyDao.getById(DutyPojo.class, dutyId);
            List<Long> dutyRoles = dutyDao.selectListDutyRole(dutyId);
            StringBuilder roles = new StringBuilder();
            for (int i = 0; i < dutyRoles.size(); i++) {
                if (i != 0) {
                    roles.append(",");
                }
                roles.append(dutyRoles.get(i));
            }
            pojo.setRoleList(roles.toString());
            return pojo;
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void modifyDuty(DutyPojo dutyPojo) throws ServiceException {
        try {
            int lines = dutyDao.update(dutyPojo);
            logger.info("Modify duty successful. effect lines [lines = {}].", lines);

            dutyDao.deleteDutyRole(new Long[]{dutyPojo.getDutyId()});
            lines = dutyDao.insertDutyRole(dutyPojo.getDutyId(), CommonUtil.splitIdsByLong(dutyPojo.getRoleList(), ","));
            logger.info("Modify duty-role successful. effect lines [lines = {}].", lines);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkName(String dutyId, String dutyName) {
        boolean result = false;
        try {
            DutyPojo paramPojo = new DutyPojo();
            paramPojo.setDutyName(dutyName);
            paramPojo.setState(ManagerConstant.STATE_AVAILABLE);
            DutyPojo pojo = dutyDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(dutyId)) {
                result = NumberUtils.toLong(dutyId) == pojo.getDutyId();
            }
        } catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }
}
