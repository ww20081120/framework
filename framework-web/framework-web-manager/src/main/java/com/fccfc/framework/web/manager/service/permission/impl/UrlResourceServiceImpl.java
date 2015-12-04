package com.fccfc.framework.web.manager.service.permission.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;
import com.fccfc.framework.web.manager.dao.permission.menu.UrlResourceDao;
import com.fccfc.framework.web.manager.service.permission.UrlResourceService;

/**
 * <Description> Url资源配置<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月29日 <br>
 * @see com.fccfc.framework.web.manager.service.menu.impl <br>
 * @since V1.0<br>
 */
@Service
public class UrlResourceServiceImpl implements UrlResourceService {
    private static final Logger logger = LoggerFactory.getLogger(UrlResourceServiceImpl.class);

    @Resource
    private UrlResourceDao urlResourceDao;

    /**
     * Description: 查询所有的Url配置资源<br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<UrlResourcePojo> queryUrlResource() throws ServiceException {
        try {
            return urlResourceDao.selectUrl();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }


    /**
     * Description: 分页查询所有的Url配置资源<br>
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<UrlResourcePojo> queryUrlResource(Long functionId,
                                                  Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return urlResourceDao.selectUrl(functionId, pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加Url资源信息<br>
     *
     * @param urlResourcePojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void addUrlResource(UrlResourcePojo urlResourcePojo)
            throws ServiceException {
        try {
            urlResourceDao.save(urlResourcePojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 批量删除Url资源信息<br>
     *
     * @param resourceIds
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void deleteUrls(Long[] resourceIds) throws ServiceException {
        try {
            if (resourceIds.length == 1) {
                urlResourceDao
                        .deleteById(UrlResourcePojo.class, resourceIds[0]);
            } else {
                urlResourceDao.deleteByResourceId(resourceIds);
            }
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description:根据resourceId查询UrlResourcePojo对象 <br>
     *
     * @param resourceId
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public UrlResourcePojo queryUrl(Long resourceId) throws ServiceException {
        try {
            return urlResourceDao.getById(UrlResourcePojo.class, resourceId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改URL<br>
     *
     * @param urlResourcePojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void modifyUrl(UrlResourcePojo urlResourcePojo)
            throws ServiceException {
        try {
            urlResourceDao.update(urlResourcePojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkName(String resourceId, String resourceName) {
        boolean result = false;
        try {
            UrlResourcePojo paramPojo = new UrlResourcePojo();
            paramPojo.setResourceName(resourceName);
            UrlResourcePojo pojo = urlResourceDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(resourceId)) {
                result = NumberUtils.toLong(resourceId) == pojo.getResourceId();
            }
        } catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

}
