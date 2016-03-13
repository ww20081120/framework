package com.hbasesoft.framework.web.system.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.utils.excel.ExcelImportCallback;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.AttrPojo;
import com.hbasesoft.framework.web.system.bean.AttrValuePojo;
import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import com.hbasesoft.framework.web.system.dao.attr.AttrDao;
import com.hbasesoft.framework.web.system.service.AttrService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

@Service
public class AttrServiceImpl implements AttrService {

    private static final Logger logger = LoggerFactory.getLogger(AttrServiceImpl.class);

    /** attrDao */
    @Resource
    private AttrDao attrDao;

    @Resource
    private AttachmentDao attachmentDao;

    /**
     * Description: 分页查询属性数据<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrPojo> queryAttrPager(Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return attrDao.queryAttr(pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrPojo> queryAttr() throws ServiceException {
        try {
            return attrDao.queryAttr();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrPojo> queryChildAttr(Integer attrId) throws ServiceException {
        try {
            return attrDao.queryChildAttr(attrId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public AttrPojo queryAttr(Integer attrId) throws ServiceException {
        try {
            return attrDao.getById(AttrPojo.class, attrId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrValuePojo> queryAttrValuePager(Integer attrId, Integer pageIndex, Integer pageSize)
        throws ServiceException {
        try {
            return attrDao.queryAttrValue(attrId, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrValuePojo> queryAttrValue(Integer attrId) throws ServiceException {
        try {
            return attrDao.queryAttrValue(attrId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加属性信息<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void addAttr(AttrPojo pojo) throws ServiceException {
        try {
            attrDao.save(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改属性信息<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void modifyAttr(AttrPojo pojo) throws ServiceException {
        try {
            attrDao.update(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 删除属性信息<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param attrIds
     * @throws ServiceException <br>
     */
    @Override
    public void deleteAttr(Integer[] attrIds) throws ServiceException {
        try {
            attrDao.deleteAttr(attrIds);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    @Override
    public void importAttr(String mediaId, String mediaName) throws ServiceException {
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

            final List<Integer> existList = assembleExistAttr(queryAttr());
            final List<AttrPojo> attrPojoList = new ArrayList<AttrPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer attrId = NumberUtils.toInt(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        if (!existList.contains(attrId)) {
                            AttrPojo pojo = new AttrPojo();
                            pojo.setAttrId(attrId);
                            pojo.setAttrName((StringUtils.trim(row.getCell(1).getStringCellValue())));
                            pojo.setAttrType((StringUtils.trim(row.getCell(2).getStringCellValue())));
                            pojo.setParentAttrId((NumberUtils.toInt(
                                null == row.getCell(3) ? "" : StringUtils.trim(row.getCell(3).getStringCellValue()))));
                            pojo.setAttrCode((StringUtils.trim(row.getCell(4).getStringCellValue())));
                            pojo.setVisible((StringUtils.trim(row.getCell(5).getStringCellValue())));
                            pojo.setInstantiatable((StringUtils.trim(row.getCell(6).getStringCellValue())));
                            pojo.setDefaultValue(
                                (null == row.getCell(7) ? "" : StringUtils.trim(row.getCell(7).getStringCellValue())));
                            pojo.setInputType((StringUtils.trim(row.getCell(8).getStringCellValue())));
                            pojo.setDataType((StringUtils.trim(row.getCell(9).getStringCellValue())));
                            pojo.setValueScript((null == row.getCell(10) ? ""
                                : StringUtils.trim(row.getCell(10).getStringCellValue())));

                            attrPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(attrPojoList)) {
                        try {
                            int lines = attrDao.batchInsertAttr(attrPojoList);
                            logger.info("Batch insert attr list [lines = {}].", lines);
                            attrPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert attr list.", e);
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
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     */
    @Override
    public void importAttrValue(String mediaId, String mediaName) throws ServiceException {
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

            final List<Integer> existAttrList = assembleExistAttr(queryAttr());
            final List<Integer> existList = assembleExistAttrValue(queryAttrValue());
            final List<AttrValuePojo> attrValuePojoList = new ArrayList<AttrValuePojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer attrId = NumberUtils.toInt(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        Integer attrValueId = NumberUtils.toInt(StringUtils.trim(row.getCell(1).getStringCellValue()));
                        if (!existList.contains(attrValueId) && existAttrList.contains(attrId)) {
                            AttrValuePojo pojo = new AttrValuePojo();
                            pojo.setAttrId(attrId);
                            pojo.setAttrValueId(attrValueId);
                            pojo.setValueMark((StringUtils.trim(row.getCell(2).getStringCellValue())));
                            pojo.setValue(
                                null == row.getCell(3) ? "" : StringUtils.trim(row.getCell(3).getStringCellValue()));
                            pojo.setLinkAttrId(NumberUtils.toInt(
                                null == row.getCell(4) ? "" : StringUtils.trim(row.getCell(4).getStringCellValue())));

                            attrValuePojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(attrValuePojoList)) {
                        try {
                            int lines = attrDao.batchInsertAttrValue(attrValuePojoList);
                            logger.info("Batch insert attr value list [lines = {}].", lines);
                            attrValuePojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert attr value list.", e);
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
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param existList
     * @return <br>
     */
    private List<Integer> assembleExistAttr(List<AttrPojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (AttrPojo pojo : existList) {
            exist.add(pojo.getAttrId());
        }
        return exist;
    }

    private List<Integer> assembleExistAttrValue(List<AttrValuePojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (AttrValuePojo pojo : existList) {
            exist.add(pojo.getAttrValueId());
        }
        return exist;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AttrValuePojo> queryAttrValue() throws ServiceException {
        try {
            return attrDao.queryAttrValue();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void addAttrValue(AttrValuePojo pojo) throws ServiceException {
        try {
            attrDao.save(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValueId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public AttrValuePojo getAttrValue(Integer attrValueId) throws ServiceException {
        try {
            return attrDao.getById(AttrValuePojo.class, attrValueId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void modifyAttrValue(AttrValuePojo pojo) throws ServiceException {
        try {
            attrDao.update(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValueIds
     * @throws ServiceException <br>
     */
    @Override
    public void deleteAttrValue(Integer[] attrValueIds) throws ServiceException {
        try {
            attrDao.deleteAttrValue(attrValueIds);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
