package com.fccfc.framework.web.manager.service.permission.impl;

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.AccountPojo;
import com.fccfc.framework.web.manager.bean.permission.AdminBean;
import com.fccfc.framework.web.manager.bean.permission.AdminPojo;
import com.fccfc.framework.web.manager.bean.permission.OperatorPojo;
import com.fccfc.framework.web.manager.dao.permission.admin.AccountDao;
import com.fccfc.framework.web.manager.dao.permission.admin.AdminDao;
import com.fccfc.framework.web.manager.dao.permission.admin.OperatorDao;
import com.fccfc.framework.web.manager.service.permission.AdminService;
import com.fccfc.framework.web.manager.utils.WebUtil;

import oracle.sql.INTERVALYM;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Resource
    private OperatorDao operatorDao;

    @Resource
    private AccountDao accountDao;

    @Resource
    private AdminDao adminDao;

    /**
     * Description: 添加<br>
     *
     * @param bean
     * @throws ServiceException <br>
     * @author ymy<br>
     * @taskId <br>
     */
    @Override
    public void addAdmin(AdminBean bean) throws ServiceException {
        try {
            OperatorPojo operator = operatorDao.getOperatorByAccount(bean.getUsername());
            if (operator != null) {
                throw new ServiceException(ErrorCodeDef.ACCOUNT_EXSIST_20004, "账号已经存在");
            }

            operator = new OperatorPojo();
            setOperator(bean, operator);
            operatorDao.save(operator);
            logger.info("操作员添加");
            accountDao.save(setAccount(bean, operator.getOperatorId()));
            logger.info("账户添加");
            AdminPojo admin = setAdmin(bean, operator.getOperatorId());
            adminDao.save(admin);
            logger.info("管理员添加");
            Integer id = admin.getAdminId();
            /** session里获得当前登录的操作员ID */
            Integer operatorId = WebUtil.getCurrentOperatorId();
            Date updateTime = new Date();
            logger.info("****id:" + id + "***operatorId:" + operatorId + "**updateTime" + updateTime);
            adminDao.saveHistory(id, operatorId, updateTime);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        catch (UtilException e) {
            throw new ServiceException(e);
        }
    }

    private void setOperator(AdminBean bean, OperatorPojo operator) throws UtilException {
        Date currentDate = new Date();
        operator.setUserName(bean.getUsername());
        operator.setOperatorType(OperatorPojo.OPERATOR_TYPE_ADMIN);
        operator.setDutyId(bean.getDutyId());
        operator.setPassword(CommonUtil.md5(bean.getPwd()));
        operator.setIsLocked(ManagerConstant.NO);
        operator.setState(ManagerConstant.STATE_AVAILABLE);
        operator.setRegistIp(bean.getLoginIp());
        operator.setLoginFail(0);
        operator.setCreateDate(currentDate);
        operator.setStateDate(currentDate);
        operator.setLastLoginDate(currentDate);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3);
        operator.setPwdExpDate(cal.getTime());
    }

    private AdminPojo setAdmin(AdminBean bean, Integer operatorId) {
        AdminPojo admin = new AdminPojo();
        admin.setAdminName(bean.getName());
        admin.setOperatorId(operatorId);
        admin.setEmail(bean.getEmail());
        admin.setPhone(bean.getPhone());
        admin.setAddress(bean.getAddress());
        admin.setGener(bean.getGener());
        admin.setHeadImg(NumberUtils.toInt(bean.getImgId()));
        admin.setState(ManagerConstant.STATE_AVAILABLE);

        Date currentDate = new Date();
        admin.setCreateTime(currentDate);
        admin.setStateDate(currentDate);
        return admin;
    }

    private AccountPojo setAccount(AdminBean bean, Integer operatorId) {
        Date currentDate = new Date();

        AccountPojo account = new AccountPojo();
        account.setAccountType(AccountPojo.ACCOUNT_TYPE_PLATFORM);
        account.setAccountValue(bean.getUsername());
        account.setOperatorId(operatorId);
        account.setCreateTime(currentDate);
        account.setState(ManagerConstant.STATE_AVAILABLE);
        account.setStateDate(currentDate);
        return account;
    }

    // 查询
    @Override
    public List<AdminPojo> queryAdmin(Long dutyId, String queryStr, int pageIndex, int pageSize)
        throws ServiceException {
        try {
            String name = null;
            if (CommonUtil.isNotEmpty(queryStr)) {
                name = "%" + queryStr + "%";
            }
            return adminDao.selectList(dutyId, name, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description:修改管理员信息 <br>
     *
     * @param admin
     * @throws ServiceException <br>
     * @author ymy<br>
     * @taskId <br>
     */
    @Override
    public void modifyAdmin(AdminPojo admin) throws ServiceException {
        try {
            int lines = adminDao.update4Attr(admin);
            Integer id = admin.getAdminId();
            /** session里获得当前登录的操作员ID */
            Integer operatorId = WebUtil.getCurrentOperatorId();
            Date updateTime = new Date();
            logger.info("****id:" + id + "***operatorId:" + operatorId + "**updateTime" + updateTime);
            adminDao.saveHistory(id, operatorId, updateTime);
            logger.info("修改管理员成功，影响条数：[{}]", lines);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }

    }

    /**
     * Description: 删除<br>
     *
     * @param ids <br>
     * @author ymy<br>
     * @taskId <br>
     */
    @Override
    public void delAdmin(Integer[] ids) throws ServiceException {
        Integer[] oids = new Integer[ids.length];
        try {
            for (int i = 0; i < ids.length; i++) {
                oids[i] = adminDao.getById(AdminPojo.class, ids[i]).getOperatorId();
            }
            accountDao.deleteAccountById(oids, ManagerConstant.STATE_UNAVAILABLE);
            logger.info("删除account");
            adminDao.deleteAdminById(ids, ManagerConstant.STATE_UNAVAILABLE);
            logger.info("删除admin");
            operatorDao.deleteOperatorById(oids, ManagerConstant.STATE_UNAVAILABLE);
            logger.info("删除operator");
            for (int i = 0; i < ids.length; i++) {
                Integer id = ids[i];
                /** session里获得当前登录的操作员ID */
                Integer operatorId = WebUtil.getCurrentOperatorId();
                Date updateTime = new Date();
                logger.info("****id:" + id + "***operatorId:" + operatorId + "**updateTime" + updateTime);
                adminDao.saveHistory(id, operatorId, updateTime);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public AdminPojo selectAdmin(Integer adminId) throws ServiceException {
        AdminPojo admin = null;
        try {
            admin = adminDao.getById(AdminPojo.class, adminId);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
        return admin;
    }

    /** 重置密码 */
    public void resetPassword(String new_password, Integer adminId) {
        try {
            AdminPojo admin = adminDao.getById(AdminPojo.class, adminId);
            logger.info("admin.getOperatorId()*****" + admin.getOperatorId());
            operatorDao.updateOperatorPassword(admin.getOperatorId(), CommonUtil.md5(new_password));
            logger.info("重置密码成功");
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
        catch (UtilException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户名校验
     */
    public boolean checkOperatorName(String operatorName) {
        boolean result = false;
        try {
            AccountPojo param = new AccountPojo();
            param.setAccountValue(operatorName);
            param.setAccountType(AccountPojo.ACCOUNT_TYPE_PLATFORM);
            param.setState(ManagerConstant.STATE_AVAILABLE);
            result = null == accountDao.getByEntity(param);
        }
        catch (DaoException e) {
            logger.error("用户名校验*", e);
        }
        return result;
    }

    /**
     * 管理员姓名校验
     */
    public boolean checkAdminName(String adminId,String adminName) {
        boolean result = false;
        try {
            
            AdminPojo admin = new AdminPojo();
            admin.setAdminName(adminName);
            admin.setState(ManagerConstant.STATE_AVAILABLE);
            AdminPojo pojo= adminDao.getByEntity(admin);
            result = null == pojo;
            if(!result&&StringUtils.isNotBlank(adminId)){
                result=NumberUtils.toLong(adminId) == pojo.getAdminId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

}
