package com.fccfc.framework.web.manager.service.permission;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.manager.bean.permission.AdminBean;
import com.fccfc.framework.web.manager.bean.permission.AdminPojo;

import java.util.List;

public interface AdminService {

    /** 查询管理员信息*/
    List<AdminPojo> queryAdmin(Long dutyId, String queryStr, int pageIndex, int pageSize) throws ServiceException;

    /** 修改管理员信息*/
    void modifyAdmin(AdminPojo admin) throws ServiceException;

    /**
     * Description: <br>
     *
     * @param bean
     * @throws ServiceException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    void addAdmin(AdminBean bean) throws ServiceException;

    /**
     * Description: <br>
     *
     * @throws ServiceException
     * @author XXX<br>
     * @taskId <br> <br>
     */
    void delAdmin(Integer[] ids) throws ServiceException;

    /** 根据id查找管理员*/
    AdminPojo selectAdmin(Integer adminId) throws ServiceException;

    /**
     * Description: 重置密码<br>
     *
     * @param new_password
     * @param adminId      <br>
     * @author ymy<br>
     * @taskId <br>
     */
    void resetPassword(String new_password, Integer adminId);

    
    boolean checkOperatorName(String operatorName);
    
    boolean checkAdminName(String adminId,String adminNam);
}
