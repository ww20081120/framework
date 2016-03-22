package com.hbasesoft.framework.web.permission.service;

import java.util.List;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.web.permission.bean.AdminPojo;

public interface AdminService {

    /** 查询管理员信息 */
    List<AdminPojo> queryAdmin(Long orgId, Long dutyId, String queryStr, int pageIndex, int pageSize)
        throws ServiceException;

    /** 修改管理员信息 */
    void modify(AdminPojo admin) throws ServiceException;

    /**
     * Description: <br>
     *
     * @param bean
     * @throws ServiceException <br>
     * @taskId <br>
     */
    void addAdmin(AdminPojo adminPojo) throws ServiceException;

    /**
     * Description: <br>
     *
     * @throws ServiceException
     * @taskId <br>
     *         <br>
     */
    void remove(Integer[] ids) throws ServiceException;

    /** 根据id查找管理员 */
    AdminPojo selectAdmin(Integer adminId) throws ServiceException;

    /**
     * Description: 重置密码<br>
     *
     * @param new_password
     * @param adminId <br>
     * @author ymy<br>
     * @taskId <br>
     */
    void modifyPwd(String newPassword, Integer adminId) throws ServiceException;

    boolean checkOperatorName(String operatorName);

    boolean checkAdminName(String adminId, String adminNam);

    AdminPojo getAdminByOperatorId(Integer operatorId) throws ServiceException;

    boolean checkPwd(Integer operatorId, String password) throws ServiceException;

}
