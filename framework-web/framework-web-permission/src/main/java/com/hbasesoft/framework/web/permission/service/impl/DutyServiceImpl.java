package com.hbasesoft.framework.web.permission.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.DutyPojo;
import com.hbasesoft.framework.web.permission.dao.admin.OperatorDao;
import com.hbasesoft.framework.web.permission.dao.duty.DutyDao;
import com.hbasesoft.framework.web.permission.service.DutyService;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.DaoException;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/10/29 <br>
 * @see com.hbasesoft.framework.web.manager.service.org.impl <br>
 * @since V1.0<br>
 */
@Service
public class DutyServiceImpl implements DutyService {

    private static final Logger logger = new Logger(DutyServiceImpl.class);

    @Resource
    private DutyDao dutyDao;

    @Resource
    private OperatorDao operatorDao;

    @Override
    public List<DutyPojo> listDuty(Long orgId, Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return dutyDao.selectDuty(orgId, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void addDuty(DutyPojo dutyPojo) throws ServiceException {
        try {
            dutyPojo.setCreateTime(new Date());
            dutyPojo.setOperatorId(WebUtil.getCurrentOperatorId());
            dutyPojo.setState(PermissionConstant.STATE_AVAILABLE);
            dutyPojo.setStateDate(dutyPojo.getCreateTime());
            dutyDao.save(dutyPojo);

            String roles = dutyPojo.getRoleList();
            if (CommonUtil.isNotEmpty(roles)) {
                int lines = dutyDao.insertDutyRole(dutyPojo.getDutyId(),
                    CommonUtil.splitIdsByLong(roles, GlobalConstants.SPLITOR));
                logger.info("Insert duty-role successful. effect lines [lines = {}].", lines);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void remove(Long[] ids) throws ServiceException {
        try {
            List<Long> remain = new ArrayList<Long>();
            for (int i = 0; i < ids.length; i++) {
                Long id = ids[i];
                if (CollectionUtils.isEmpty(operatorDao.selectList(id, 1, 1))) {
                    remain.add(id);
                }
            }
            if (CollectionUtils.isNotEmpty(remain)) {
                Long[] remainIds = remain.toArray(new Long[] {});
                int lines = dutyDao.deleteByIds(remainIds);
                logger.info("Delete duty successful. effect lines [lines = {}]", lines);

                lines = dutyDao.deleteDutyRole(remainIds);
                logger.info("Delete duty-role successful. effect lines [lines = {}]", lines);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public DutyPojo queryDuty(Long dutyId) throws ServiceException {
        try {
            DutyPojo pojo = dutyDao.selectDuty(dutyId);
            if (pojo != null) {
                List<Long> dutyRoles = dutyDao.selectListDutyRole(dutyId);
                StringBuilder roles = new StringBuilder();
                for (int i = 0; i < dutyRoles.size(); i++) {
                    if (i != 0) {
                        roles.append(",");
                    }
                    roles.append(dutyRoles.get(i));
                }
                pojo.setRoleList(roles.toString());
            }
            return pojo;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void modifyDuty(DutyPojo dutyPojo) throws ServiceException {
        try {
            DutyPojo newDuty = dutyDao.getById(DutyPojo.class, dutyPojo.getDutyId());
            newDuty.setDutyName(dutyPojo.getDutyName());
            int lines = dutyDao.update(newDuty);
            logger.info("Modify duty successful. effect lines [lines = {}].", lines);

            dutyDao.deleteDutyRole(new Long[] {
                newDuty.getDutyId()
            });

            if (CommonUtil.isNotEmpty(dutyPojo.getRoleList())) {
                lines = dutyDao.insertDutyRole(newDuty.getDutyId(),
                    CommonUtil.splitIdsByLong(dutyPojo.getRoleList(), GlobalConstants.SPLITOR));
                logger.info("Modify duty-role successful. effect lines [lines = {}].", lines);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
