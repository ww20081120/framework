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

@Service
public class DirectoryServiceImpl implements DirectoryService {

    @Resource
    private DirectoryDao directoryDao;

    public List<DirectoryPojo> selectDirectoryByCode(String directoryCode) throws ServiceException {
        try {
            return directoryDao.queryDirectory(directoryCode);
        }
        catch (FrameworkException e) {
            throw new ServiceException(e);
        }
    }

    public List<DirectoryPojo> queryDirectoryByParentCode(String parentDirectoryCode) throws ServiceException {
        try {
            return directoryDao.queryDirectoryByParentCode(parentDirectoryCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public int deleteDirectory(String directoryCode) throws ServiceException {
        try {
            return directoryDao.deleteDirectory(directoryCode);
        }
        catch (DaoException e) {
            throw new ServiceException("删除目录失败", e);
        }
    }

    public int modifyDirectory(DirectoryPojo pojo) throws ServiceException {
        try {
            return directoryDao.updateDirectory(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException("修改目录失败", e);
        }
    }

    public int addDirectory(DirectoryPojo pojo) throws ServiceException {
        try {
            return directoryDao.insertDirectory(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException("添加目录失败", e);
        }
    }
}
