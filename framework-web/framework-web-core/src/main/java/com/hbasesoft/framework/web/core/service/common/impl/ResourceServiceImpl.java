/**
 *
 */
package com.hbasesoft.framework.web.core.service.common.impl;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.io.ImageUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;
import com.hbasesoft.framework.web.core.dao.AttachmentsDao;
import com.hbasesoft.framework.web.core.service.common.ResourceService;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年11月30日 <br>
 * @see com.hbasesoft.framework.web.manager.service.common.impl <br>
 * @since V1.0<br>
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    /**
     * logger
     */
    private static Logger logger = new Logger(ResourceServiceImpl.class);

    /**
     * attachmentsDao
     */
    @Resource
    private AttachmentsDao attachmentsDao;

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.web.service.ResourceService#downloadResource(int)
     */
    @Override
    public AttachmentsPojo downloadResource(int resourceId, boolean isThumb) throws ServiceException {
        try {
            String thumbPath = null;
            AttachmentsPojo pojo = attachmentsDao.selectAttachments(resourceId);
            if (pojo == null) {
                throw new ServiceException(ErrorCodeDef.RESOURCE_ID_ERROR_20003, "未找到resourceId为{0}的资源", resourceId);
            }

            String resourcePath = ConfigHelper.getString("RESOURCE.PATH");

            File file = new File(resourcePath + pojo.getFilePath());
            if (!file.exists()) {
                throw new ServiceException(ErrorCodeDef.RESOURCE_ID_ERROR_20003, "未找到resourceId为{0}的资源", resourceId);
            }

            if (isThumb && "Y".equals(pojo.getIsPicture())) {
                if (!"Y".equals(pojo.getIsThumb())) {
                    thumbPath = new StringBuilder(pojo.getFilePath())
                        .insert(pojo.getFilePath().lastIndexOf("."), "_thumb").toString();

                    try {
                        ImageUtil.pictureZoom(resourcePath + pojo.getFilePath(), resourcePath + thumbPath);
                        if (thumbPath.toLowerCase().indexOf("jpg") == -1
                            || thumbPath.toLowerCase().indexOf("jpeg") == -1) {
                            thumbPath = thumbPath.substring(0, thumbPath.lastIndexOf(".")) + ".jpg";
                        }
                    }
                    catch (Exception e) {
                        logger.warn("生成缩略图失败", e);
                        thumbPath = pojo.getFilePath();
                    }
                    pojo.setThumbPath(thumbPath);
                }
            }
            attachmentsDao.updateAttachments(resourceId, thumbPath);

            return pojo;
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.web.service.ResourceService#saveAttachment(com.hbasesoft
     * .framework.core.bean.resource.AttachmentsPojo )
     */
    @Override
    public void saveAttachment(AttachmentsPojo attachments) throws ServiceException {
        try {
            attachmentsDao.save(attachments);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * queryResourceByfilePath Description: <br>
     *
     * @param filePath filePath
     * @return AttachmentsPojo
     * @throws ServiceException <br>
     * @author XXX<br>
     * @taskId <br>
     */
    public AttachmentsPojo queryResourceByfilePath(String filePath) throws ServiceException {

        try {
            return attachmentsDao.queryAttachmentsByFilePath(filePath);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }
}
