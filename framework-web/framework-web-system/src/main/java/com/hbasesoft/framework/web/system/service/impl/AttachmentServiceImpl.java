package com.hbasesoft.framework.web.system.service.impl;

import java.io.File;

import javax.annotation.Resource;

import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.system.service.AttachmentService;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.io.ImageUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

/**
 * Created by wk on 2015/10/20.
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {
    private static Logger logger = new Logger(AttachmentServiceImpl.class);

    @Resource
    private AttachmentDao attachmentDao;

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.web.service.ResourceService#downloadResource(int)
     */
    @Override
    public AttachmentsPojo downloadResource(int resourceId, boolean isThumb) throws ServiceException {
        try {
            String thumbPath = null;
            AttachmentsPojo pojo = attachmentDao.selectAttachments(resourceId);
            if (pojo == null) {
                throw new ServiceException(ErrorCodeDef.RESOURCE_ID_ERROR, "未找到resourceId为{0}的资源", resourceId);
            }

            String resourcePath = ConfigHelper.getString("RESOURCE.PATH");

            File file = new File(resourcePath + pojo.getFilePath());
            if (!file.exists()) {
                throw new ServiceException(ErrorCodeDef.RESOURCE_ID_ERROR, "未找到resourceId为{0}的资源", resourceId);
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
                    } catch (Exception e) {
                        logger.warn("生成缩略图失败", e);
                        thumbPath = pojo.getFilePath();
                    }
                    pojo.setThumbPath(thumbPath);
                }
            }
            attachmentDao.updateAttachments(resourceId, thumbPath);

            return pojo;
        } catch (DaoException e) {
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
            attachmentDao.save(attachments);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
