package com.fccfc.framework.web.service;

import java.util.List;

import com.fccfc.framework.api.ServiceException;
import com.fccfc.framework.api.bean.config.DirectoryPojo;

public interface DirectoryService {

	List<DirectoryPojo> selectDirectoryByCode(String directoryCode) throws ServiceException;

	List<DirectoryPojo> queryDirectoryByParentCode(String parentDirectoryCode) throws ServiceException;

	int deleteDirectory(String directoryCode) throws ServiceException;
	
	int modifyDirectory(DirectoryPojo pojo) throws ServiceException;
	
	int addDirectory(DirectoryPojo pojo) throws ServiceException;
}
