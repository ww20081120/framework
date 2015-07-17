package com.fccfc.framework.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.DirectoryPojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.dao.DirectoryDao;
import com.fccfc.framework.web.service.DirectoryService;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class DirectoryServiceImpl implements DirectoryService {

    /**
     * directoryDao
     */
    @Resource
    private DirectoryDao directoryDao;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public List<DirectoryPojo> selectDirectoryByCode(String directoryCode) throws ServiceException {
        try {
            return directoryDao.queryDirectory(directoryCode);
        }
        catch (FrameworkException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parentDirectoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public List<DirectoryPojo> queryDirectoryByParentCode(String parentDirectoryCode) throws ServiceException {
        try {
            return directoryDao.queryDirectoryByParentCode(parentDirectoryCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param directoryCode <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public int deleteDirectory(String directoryCode) throws ServiceException {
        try {
            return directoryDao.deleteDirectory(directoryCode);
        }
        catch (DaoException e) {
            throw new ServiceException("删除目录失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public int modifyDirectory(DirectoryPojo pojo) throws ServiceException {
        try {
            return directoryDao.updateDirectory(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException("修改目录失败", e);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public int addDirectory(DirectoryPojo pojo) throws ServiceException {
        try {
            return directoryDao.insertDirectory(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException("添加目录失败", e);
        }
    }
}
