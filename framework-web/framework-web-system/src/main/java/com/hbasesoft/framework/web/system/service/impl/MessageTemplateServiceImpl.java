package com.hbasesoft.framework.web.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.utils.excel.ExcelImportCallback;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.dao.template.MessageTemplateDao;
import com.hbasesoft.framework.web.system.service.MessageTemplateService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;
import com.hbasesoft.framework.message.core.bean.MessageTemplatePojo;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月27日 <br>
 * @since V7.3<br>
 * @see com.hbasesoft.framework.web.manager.service.template.impl <br>
 */
@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {

    /** messageTemplateDao */
    @Autowired
    private MessageTemplateDao messageTemplateDao;

    /**
     * attachmentDao
     */
    @Resource
    private AttachmentDao attachmentDao;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MessageTemplateServiceImpl.class);

    /**
     * Description:查询所有的消息消息模板<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<MessageTemplatePojo> queryAllMessageTemplate(int pageIndex, int pageSize) throws ServiceException {
        try {
            return messageTemplateDao.getAllMessageTemplate(pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加消息模板<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void addMessageTemplateService(MessageTemplatePojo pojo) throws ServiceException {
        try {
            messageTemplateDao.insertMessageTemplate(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改消息模板<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void modifyMessageTemplate(MessageTemplatePojo pojo) throws ServiceException {
        try {
            messageTemplateDao.modifyMessageTemplate(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 删除消息模板<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param messageTemplateId <br>
     * @throws ServiceException <br>
     */
    @Override
    public void deleteMessageTemplate(Integer messageTemplateId) throws ServiceException {
        try {
            messageTemplateDao.deleteMessageTemplate(messageTemplateId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 导入消息模板<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param mediaId <br>
     * @param mediaName <br>
     * @throws ServiceException <br>
     */
    @Override
    public void importMessageTemplateData(String mediaId, String mediaName) throws ServiceException {
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

            final List<String> existList = assembleExistMt(queryAllMt());
            final List<MessageTemplatePojo> mtPojoList = new ArrayList<MessageTemplatePojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        String messageTemplatCode = getCellValue(row.getCell(0));
                        if (!existList.contains(messageTemplatCode)) {
                            MessageTemplatePojo pojo = new MessageTemplatePojo();
                            pojo.setMessageTemplateCode(getCellValue(row.getCell(0)));
                            pojo.setName(getCellValue(row.getCell(1)));
                            pojo.setTemplate(getCellValue(row.getCell(2)));

                            // 必传参数
                            pojo.setContactChannelIds("1");
                            pojo.setDelay(Integer.valueOf(0));
                            pojo.setResendTimes(Integer.valueOf(0));
                            pojo.setSaveDay(Integer.valueOf(365));
                            pojo.setSaveHistory("Y");
                            pojo.setState("A");

                            mtPojoList.add(pojo);
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
                    if (CollectionUtils.isNotEmpty(mtPojoList)) {
                        try {
                            for (MessageTemplatePojo pojo : mtPojoList) {
                                int lines = messageTemplateDao.insertMessageTemplate(pojo);
                                logger.info("Batch insert messageTemplate list [lines = {}].", lines);
                            }

                            mtPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert messageTemplate list.", e);
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
     * @param existList <br>
     * @return <br>
     */
    private List<String> assembleExistMt(List<MessageTemplatePojo> existList) {
        List<String> exist = new ArrayList<String>();
        for (MessageTemplatePojo pojo : existList) {
            exist.add(pojo.getMessageTemplateCode());
        }
        return exist;
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<MessageTemplatePojo> queryAllMt() throws ServiceException {
        try {
            return messageTemplateDao.queryAllMt();
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
     * @param messageTemplateId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    public MessageTemplatePojo queryMessageTemplateById(Integer messageTemplateId) throws ServiceException {
        MessageTemplatePojo messageTemplate = null;
        try {
            messageTemplate = messageTemplateDao.queryMessageTemplateById(messageTemplateId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        return messageTemplate;
    }
}
