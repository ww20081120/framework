package com.hbasesoft.framework.web.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.utils.excel.ExcelImportCallback;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.AnnouncementPojo;
import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import com.hbasesoft.framework.web.system.dao.announcement.AnnouncementDao;
import com.hbasesoft.framework.web.system.service.AnnouncementService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月26日 <br>
 * @since bps<br>
 * @see com.hbasesoft.framework.web.manager.service.system.impl <br>
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    /**
     * announcementDao
     */
    @Resource
    private AnnouncementDao announcementDao;

    /**
     * attachmentDao
     */
    @Resource
    private AttachmentDao attachmentDao;

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<AnnouncementPojo> listAnnouncement(int pageIndex, int pageSize) throws ServiceException {
        try {
            return announcementDao.listAnnouncement(pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<AnnouncementPojo> qryAnnouncement(AnnouncementPojo pojo) throws ServiceException {
        try {
            return announcementDao.qryAnnouncement(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void add(AnnouncementPojo pojo) throws ServiceException {
        try {
            announcementDao.insertAnnouncement(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void modify(AnnouncementPojo pojo) throws ServiceException {
        try {
            announcementDao.modifyAnnouncement(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void delete(String[] ids) throws ServiceException {
        try {
            announcementDao.deleteAnnouncement(ids);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    @Override
    public void importAnnouncementData(String mediaId, String mediaName) throws ServiceException {
        try {
            AttachmentsPojo attachment = attachmentDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
            if (null == attachment) {
                logger.info("Attachment is not find. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }
            String path = ConfigHelper.getString(WebConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getFilePath();
            File file = new File(path);
            if (!file.exists()) {
                logger.info("File isn't exist. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }

            final List<AnnouncementPojo> announcementList = new ArrayList<AnnouncementPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {

                @Override
                public void invokeDataHandler(Row row) {

                    try {
                        boolean flag = true; // 判断是否执行此行数据
                        if (flag) {

                            AnnouncementPojo pojo = new AnnouncementPojo();
                            pojo.setTitle(getCellValue(row.getCell(0)));
                            pojo.setContent(getCellValue(row.getCell(1)));
                            pojo.setState(AnnouncementPojo.ANNOUNCEMENT_STATE_INITIAL);

                            announcementList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                private String getCellValue(Cell cell) {
                    String ret;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            ret = "";
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            ret = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case Cell.CELL_TYPE_ERROR:
                            ret = null;
                            break;
                        case Cell.CELL_TYPE_STRING:
                            ret = cell.getRichStringCellValue().getString();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            ret = String.valueOf(cell.getNumericCellValue());
                            break;
                        default:
                            ret = null;
                    }

                    return ret; // 有必要自行trim
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(announcementList)) {
                        try {
                            for (AnnouncementPojo pojo : announcementList) {
                                int lines = announcementDao.insertAnnouncement(pojo);
                                logger.info("Batch insert announcement list [lines = {}].", lines);
                            }
                            announcementList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert announcement list.", e);
                        }
                    }
                }
            });
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void audit(String[] ids, AnnouncementPojo pojo) throws ServiceException {
        try {
            announcementDao.auditAnnouncement(ids, pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param ids <br>
     * @param state <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<AnnouncementPojo> auditCheck(String[] ids, String state) throws ServiceException {
        // TODO Auto-generated method stub
        try {
            return announcementDao.checkAnnouncement(ids, state);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
