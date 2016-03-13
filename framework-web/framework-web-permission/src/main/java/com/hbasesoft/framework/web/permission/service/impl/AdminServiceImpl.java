package com.hbasesoft.framework.web.permission.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.bean.OperatorPojo;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.permission.PermissionConstant;
import com.hbasesoft.framework.web.permission.bean.AccountPojo;
import com.hbasesoft.framework.web.permission.bean.AdminPojo;
import com.hbasesoft.framework.web.permission.dao.admin.AccountDao;
import com.hbasesoft.framework.web.permission.dao.admin.AdminDao;
import com.hbasesoft.framework.web.permission.dao.admin.OperatorDao;
import com.hbasesoft.framework.web.permission.service.AdminService;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.DaoException;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = new Logger(AdminServiceImpl.class);

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
    public void addAdmin(AdminPojo adminPojo) throws ServiceException {
        try {
            if (null != operatorDao.getOperatorByAccount(adminPojo.getOperatorPojo().getUserName())) {
                throw new ServiceException(ErrorCodeDef.ACCOUNT_EXSIST_20004, "账号已经存在");
            }

            setOperator(adminPojo.getOperatorPojo());
            operatorDao.save(adminPojo.getOperatorPojo());
            logger.info("操作员添加");
            AccountPojo account = new AccountPojo();
            accountDao.save(setAccount(account, adminPojo));
            logger.info("账户添加");
            setAdmin(adminPojo, adminPojo.getOperatorPojo().getOperatorId());
            adminDao.save(adminPojo);
            logger.info("管理员添加");
            Integer id = adminPojo.getAdminId();
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

    private void setOperator(OperatorPojo operator) throws UtilException {
        Date currentDate = new Date();
        operator.setOperatorType(OperatorPojo.OPERATOR_TYPE_ADMIN);
        operator.setPassword(CommonUtil.md5(operator.getPassword()));
        operator.setIsLocked(PermissionConstant.NO);
        operator.setState(PermissionConstant.STATE_AVAILABLE);
        operator.setLoginFail(0);
        operator.setCreateDate(currentDate);
        operator.setStateDate(currentDate);
        operator.setLastLoginDate(currentDate);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 3);
        operator.setPwdExpDate(cal.getTime());
    }

    private void setAdmin(AdminPojo adminPojo, Integer operatorId) {
        adminPojo.setOperatorId(operatorId);
        adminPojo.setState(PermissionConstant.STATE_AVAILABLE);
        Date currentDate = new Date();
        adminPojo.setCreateTime(currentDate);
        adminPojo.setStateDate(currentDate);
    }

    private AccountPojo setAccount(AccountPojo accountPojo, AdminPojo adminPojo) {
        Date currentDate = new Date();
        accountPojo.setAccountValue(adminPojo.getOperatorPojo().getUserName());
        accountPojo.setAccountType(AccountPojo.ACCOUNT_TYPE_PLATFORM);
        accountPojo.setOperatorId(adminPojo.getOperatorPojo().getOperatorId());
        accountPojo.setCreateTime(currentDate);
        accountPojo.setState(PermissionConstant.STATE_AVAILABLE);
        accountPojo.setStateDate(currentDate);
        return accountPojo;
    }

    // 查询
    @Override
    public List<AdminPojo> queryAdmin(Long orgId, Long dutyId, String queryStr, int pageIndex, int pageSize)
        throws ServiceException {
        try {
            String name = null;
            if (CommonUtil.isNotEmpty(queryStr)) {
                name = "%" + queryStr + "%";
            }
            return adminDao.selectList(orgId, dutyId, name, pageIndex, pageSize);
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
    public void modify(AdminPojo admin) throws ServiceException {
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
    public void remove(Integer[] ids) throws ServiceException {
        Integer[] oids = new Integer[ids.length];
        try {
            for (int i = 0; i < ids.length; i++) {
                oids[i] = adminDao.getById(AdminPojo.class, ids[i]).getOperatorId();
            }
            accountDao.deleteAccountById(oids, PermissionConstant.STATE_UNAVAILABLE);
            logger.info("删除account");
            adminDao.deleteAdminById(ids, PermissionConstant.STATE_UNAVAILABLE);
            logger.info("删除admin");
            operatorDao.deleteOperatorById(oids, PermissionConstant.STATE_UNAVAILABLE);
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
    public void modifyPwd(String new_password, Integer adminId) {
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
            param.setState(PermissionConstant.STATE_AVAILABLE);
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
    public boolean checkAdminName(String adminId, String adminName) {
        boolean result = false;
        try {

            AdminPojo admin = new AdminPojo();
            admin.setAdminName(adminName);
            admin.setState(PermissionConstant.STATE_AVAILABLE);
            AdminPojo pojo = adminDao.getByEntity(admin);
            result = null == pojo;
            if (!result && StringUtils.isNotBlank(adminId)) {
                result = NumberUtils.toLong(adminId) == pojo.getAdminId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    @Override
    public AdminPojo getOne(Integer operatorId) throws ServiceException {
        AdminPojo admin = new AdminPojo();
        try {
            admin = adminDao.getOne(operatorId);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
        return admin;
    }

    @Override
    public boolean checkPwd(Integer operatorId, String passwprd) throws ServiceException {
        boolean result = false;
        try {
            result = CommonUtil.md5(passwprd).equals(adminDao.checkPwd(operatorId));
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
        catch (UtilException e) {
            e.printStackTrace();
        }
        return result;
    }

}
