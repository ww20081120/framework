package com.fccfc.framework.web.manager.service.common.impl;

import java.util.List;

import javax.annotation.Resource;

import com.fccfc.framework.config.core.bean.DirectoryPojo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.AssertException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.dao.common.DirectoryDao;
import com.fccfc.framework.web.manager.dao.permission.menu.FunctionDao;
import com.fccfc.framework.web.manager.service.common.DirectoryService;

/**
 *
 * <Description> 目录<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月4日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.common.impl <br>
 */
@Service
public class DirectoryServiceImpl implements DirectoryService {

	/**
	 * directoryDao
	 */
	@Resource
	private DirectoryDao directoryDao;

	@Resource
	private FunctionDao functionDao;

	private static final Logger logger = LoggerFactory
			.getLogger(DirectoryServiceImpl.class);

	/**
	 *
	 * Description: 修改目录<br>
	 *
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param pojo
	 * @throws ServiceException <br>
	 */
	public void modifyDirectory(DirectoryPojo pojo) throws ServiceException {
		try {
			directoryDao.update(pojo);
		} catch (DaoException e) {
			throw new ServiceException("修改目录失败", e);
		}
	}

	/**
	 *
	 * Description: 添加目录<br>
	 *
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param directoryPojo
	 * @throws ServiceException <br>
	 */
	public void addDirectory(DirectoryPojo directoryPojo) throws ServiceException {
		try {
			if (StringUtils.isEmpty(directoryPojo.getParentDirectoryCode())) {
				directoryPojo.setParentDirectoryCode(ManagerConstant.DIRECTORY_URL);
			}
			directoryDao.save(directoryPojo);
		} catch (DaoException e) {
			throw new ServiceException("添加目录失败", e);
		}
	}

	/**
	 *
	 * Description: 查询所有目录结构<br>
	 *
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	@Override
	public List<com.fccfc.framework.web.manager.bean.system.DirectoryPojo> selectDirectory(String parentDirectoryCode) throws ServiceException {
		try {
			return directoryDao.selectList(parentDirectoryCode, -1, -1);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 *
	 * Description: 根据目录代码查询目录<br>
	 *
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param directoryCode
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	@Override
	public DirectoryPojo queryDirectoryByCode(String directoryCode)
			throws ServiceException {
		try {
			return directoryDao.getById(DirectoryPojo.class, directoryCode);
		} catch (DaoException e) {
			throw new ServiceException(e);
		}
	}

	/**
	 *
	 * Description: 删除目录<br>
	 *
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param directoryCode
	 * @throws ServiceException
	 * <br>
	 */
	@Override
	public void removeDirectory(String directoryCode) throws ServiceException {
		try {
			Assert.isEmpty(directoryDao.selectList(directoryCode, -1, -1), "该目录有子目录，不能删除");
			Assert.isEmpty(functionDao.selectList(directoryCode, null, -1, -1), "该目录有功能模块引用，不能删除");

			int lines = directoryDao.deleteById(directoryCode);
			logger.info("Delete organizations effect numbers [lines={}]. ", lines);
		} catch (DaoException e) {
			throw new ServiceException(e);
		} catch (AssertException e) {
			throw new ServiceException(e.getCode(), e.getMessage());
		}
	}

	@Override
	public boolean checkCode(String code) {
		boolean result = false;
		try {
			DirectoryPojo paramPojo = new DirectoryPojo();
			paramPojo.setDirectoryCode(code);
			DirectoryPojo pojo = directoryDao.getByAttr(paramPojo);

			result = null == pojo;
		} catch (DaoException e) {
			logger.error("", e);
		}
		return result;
	}

	@Override
	public boolean checkName(String code, String name) {
		boolean result = false;
		try {
			DirectoryPojo paramPojo = new DirectoryPojo();
			paramPojo.setDirectoryName(name);
			DirectoryPojo pojo = directoryDao.getByAttr(paramPojo);

			result = null == pojo;
			if (!result && StringUtils.isNotBlank(code)) {
				result = StringUtils.equals(code, pojo.getDirectoryCode());
			}
		} catch (DaoException e) {
			logger.error("", e);
		}
		return result;
	}
}
