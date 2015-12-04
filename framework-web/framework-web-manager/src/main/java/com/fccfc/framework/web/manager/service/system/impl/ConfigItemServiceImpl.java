/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.service.system.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.ConfigHelper;
import com.fccfc.framework.config.core.bean.ConfigItemPojo;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.system.ConfigItemHistoryPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamHistoryPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamValuePojo;
import com.fccfc.framework.web.manager.bean.system.DirectoryPojo;
import com.fccfc.framework.web.manager.dao.common.AttachmentsDao;
import com.fccfc.framework.web.manager.dao.system.configitem.ConfigItemParamDao;
import com.fccfc.framework.web.manager.service.system.ConfigItemService;
import com.fccfc.framework.web.manager.utils.WebUtil;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.service.configitem.impl <br>
 */
@Service
public class ConfigItemServiceImpl implements ConfigItemService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigItemServiceImpl.class);

    @Resource
    private ConfigItemParamDao configDao;

    @Resource
    private AttachmentsDao attachmentsDao;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<ConfigItemPojo> queryConfigItemPager(Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItem(pageIndex, pageSize);
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
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<ConfigItemParamPojo> queryConfigItemParamPager(Integer configItemId, Integer pageIndex,
        Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItemParam(configItemId, pageIndex, pageSize);
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
     * @param configItemId
     * @param paramCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<ConfigItemParamValuePojo> queryConfigItemParamValuePager(Integer configItemId, String paramCode,
        Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItemParamValue(configItemId, paramCode, pageIndex, pageSize);
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
    public void addConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            configDao.save(pojo);
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
     * @param configItemId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public ConfigItemPojo queryConfigItem(Integer configItemId) throws ServiceException {
        try {
            return configDao.getById(ConfigItemPojo.class, configItemId);
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
    public void modifyConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            configDao.update(pojo);
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
     * @param configItemIds
     * @throws ServiceException <br>
     */
    @Override
    public void deleteConfigItems(Integer[] configItemIds) throws ServiceException {
        try {
            configDao.deleteConfigItems(configItemIds);
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
    public void addConfigItemParam(ConfigItemParamPojo pojo) throws ServiceException {
        try {
            configDao.insertConfigItemParam(pojo);
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
     * @param configItemId
     * @param paramCode
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public ConfigItemParamPojo queryConfigItemParam(Integer configItemId, String paramCode) throws ServiceException {
        try {
            return configDao.queryConfigItemParam(configItemId, paramCode);
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
     * @param oldParamCode
     * @throws ServiceException <br>
     */
    @Override
    public void modifyConfigItemParam(ConfigItemParamPojo pojo, String oldParamCode) throws ServiceException {
        try {
            configDao.updateConfigItemParam(pojo, oldParamCode);
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
     * @param configItemId
     * @param paramCode
     * @throws ServiceException <br>
     */
    @Override
    public void deleteConfigItemParams(Integer configItemId, String paramCode) throws ServiceException {
        try {
            configDao.deleteConfigItemParams(configItemId, paramCode);
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
    public void addConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException {
        try {
            configDao.save(pojo);
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
     * @param paramValueId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public ConfigItemParamValuePojo queryConfigItemParamValue(Integer paramValueId) throws ServiceException {
        try {
            return configDao.getById(ConfigItemParamValuePojo.class, paramValueId);
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
    public void modifyConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException {
        try {
            configDao.update(pojo);
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
     * @param paramValueIds
     * @throws ServiceException <br>
     */
    @Override
    public void deleteConfigItemParamValues(Integer[] paramValueIds) throws ServiceException {
        try {
            configDao.deleteConfigItemParamValues(paramValueIds);
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
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public ConfigItemPojo queryConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            return configDao.getByEntity(pojo);
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
    public List<DirectoryPojo> queryDirectoryCode() throws ServiceException {
        try {
            return configDao.queryDirectoryCode();
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
    public List<ModulePojo> queryModuleCode() throws ServiceException {
        try {
            return configDao.queryModuleCode();
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
    public void importConfigItem(String mediaId, String mediaName) throws ServiceException {
        try {
            AttachmentsPojo attachment = attachmentsDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
            if (null == attachment) {
                logger.info("Attachment is not find. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }
            String path = ConfigHelper.getString(ManagerConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getFilePath();
            File file = new File(path);
            if (!file.exists()) {
                logger.info("File isn't exist. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }

            final List<Integer> existItemList = assembleExistItem(queryConfigItemPager(-1, -1));
            final List<ConfigItemPojo> itemPojoList = new ArrayList<ConfigItemPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer configItemId = NumberUtils.toInt(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        if (!existItemList.contains(configItemId)) {
                            ConfigItemPojo pojo = new ConfigItemPojo();
                            pojo.setConfigItemId(configItemId);
                            pojo.setDirectoryCode(
                                null == row.getCell(1) ? null : StringUtils.trim(row.getCell(1).getStringCellValue()));
                            pojo.setModuleCode(
                                null == row.getCell(2) ? null : StringUtils.trim(row.getCell(2).getStringCellValue()));
                            pojo.setConfigItemCode((StringUtils.trim(row.getCell(3).getStringCellValue())));
                            pojo.setConfigItemName((StringUtils.trim(row.getCell(4).getStringCellValue())));
                            pojo.setIsVisiable((StringUtils.trim(row.getCell(5).getStringCellValue())));
                            pojo.setUpdateTime(new Date());
                            pojo.setRemark(
                                null == row.getCell(6) ? null : StringUtils.trim(row.getCell(6).getStringCellValue()));

                            itemPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(itemPojoList)) {
                        try {
                            int lines = configDao.batchInsertItem(itemPojoList);
                            logger.info("Batch insert config item list [lines = {}].", lines);
                            itemPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item list.", e);
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
    private List<Integer> assembleExistItem(List<ConfigItemPojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (ConfigItemPojo pojo : existList) {
            exist.add(pojo.getConfigItemId());
        }
        return exist;
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
    public void importConfigItemParam(String mediaId, String mediaName) throws ServiceException {
        try {
            AttachmentsPojo attachment = attachmentsDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
            if (null == attachment) {
                logger.info("Attachment is not find. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }
            String path = ConfigHelper.getString(ManagerConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getFilePath();
            File file = new File(path);
            if (!file.exists()) {
                logger.info("File isn't exist. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }

            final List<Integer> existItemList = assembleExistItem(queryConfigItemPager(-1, -1));
            final List<String> existParamList = assembleExistParamCode(queryConfigItemParamPager(null, -1, -1));
            final List<ConfigItemParamPojo> paramPojoList = new ArrayList<ConfigItemParamPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer configItemId = NumberUtils.toInt(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        String paramCode = StringUtils.trim(row.getCell(1).getStringCellValue());
                        if (existItemList.contains(configItemId) && !existParamList.contains(paramCode)) {
                            ConfigItemParamPojo pojo = new ConfigItemParamPojo();
                            pojo.setConfigItemId(configItemId);
                            pojo.setParamCode(paramCode);
                            pojo.setParamName(StringUtils.trim(row.getCell(2).getStringCellValue()));
                            pojo.setParamValue(
                                null == row.getCell(3) ? null : StringUtils.trim(row.getCell(3).getStringCellValue()));
                            pojo.setDefaultParamValue(
                                null == row.getCell(4) ? null : StringUtils.trim(row.getCell(4).getStringCellValue()));
                            pojo.setDataType((StringUtils.trim(row.getCell(5).getStringCellValue())));
                            pojo.setInputType((StringUtils.trim(row.getCell(6).getStringCellValue())));
                            pojo.setValueScript(
                                null == row.getCell(7) ? null : StringUtils.trim(row.getCell(7).getStringCellValue()));
                            pojo.setUpdateTime(new Date());
                            pojo.setRemark(
                                null == row.getCell(8) ? null : StringUtils.trim(row.getCell(8).getStringCellValue()));

                            paramPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(paramPojoList)) {
                        try {
                            int lines = configDao.batchInsertParam(paramPojoList);
                            logger.info("Batch insert config item param list [lines = {}].", lines);
                            paramPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item param list.", e);
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
    private List<String> assembleExistParamCode(List<ConfigItemParamPojo> existList) {
        List<String> exist = new ArrayList<String>();
        for (ConfigItemParamPojo pojo : existList) {
            exist.add(pojo.getParamCode());
        }
        return exist;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param existList
     * @return <br>
     */
    private List<Integer> assembleExistParam(List<ConfigItemParamPojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (ConfigItemParamPojo pojo : existList) {
            exist.add(pojo.getConfigItemId());
        }
        return exist;
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
    public void importConfigItemParamValue(String mediaId, String mediaName) throws ServiceException {
        try {
            AttachmentsPojo attachment = attachmentsDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
            if (null == attachment) {
                logger.info("Attachment is not find. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }
            String path = ConfigHelper.getString(ManagerConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getFilePath();
            File file = new File(path);
            if (!file.exists()) {
                logger.info("File isn't exist. [Attachment_ID: {}, Attachment_Name: {}]", mediaId, mediaName);
                return;
            }

            final List<Integer> existItemList = assembleExistParam(queryConfigItemParamPager(null, -1, -1));
            final List<String> existParamList = assembleExistParamCode(queryConfigItemParamPager(null, -1, -1));
            final List<Integer> existParamValueList = assembleExistParamValue(
                queryConfigItemParamValuePager(null, null, -1, -1));
            final List<ConfigItemParamValuePojo> paramValuePojoList = new ArrayList<ConfigItemParamValuePojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer configItemId = NumberUtils.toInt(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        String paramCode = StringUtils.trim(row.getCell(1).getStringCellValue());
                        Integer paramValueId = NumberUtils.toInt(StringUtils.trim(row.getCell(2).getStringCellValue()));
                        if (existItemList.contains(configItemId) && existParamList.contains(paramCode)
                            && !existParamValueList.contains(paramValueId)) {
                            ConfigItemParamValuePojo pojo = new ConfigItemParamValuePojo();
                            pojo.setConfigItemId(configItemId);
                            pojo.setParamCode(paramCode);
                            pojo.setParamValueId(paramValueId);
                            pojo.setValueMark(StringUtils.trim(row.getCell(3).getStringCellValue()));
                            pojo.setValue(
                                null == row.getCell(4) ? null : StringUtils.trim(row.getCell(4).getStringCellValue()));
                            pojo.setRemark(
                                null == row.getCell(5) ? null : StringUtils.trim(row.getCell(5).getStringCellValue()));

                            paramValuePojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(paramValuePojoList)) {
                        try {
                            int lines = configDao.batchInsertParamValue(paramValuePojoList);
                            logger.info("Batch insert config item param value list [lines = {}].", lines);
                            paramValuePojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item param value list.", e);
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
    private List<Integer> assembleExistParamValue(List<ConfigItemParamValuePojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (ConfigItemParamValuePojo pojo : existList) {
            exist.add(pojo.getParamValueId());
        }
        return exist;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param pojoList
     * @throws ServiceException <br>
     */
    @Override
    public void addConfigItemHistory(List<ConfigItemPojo> pojoList) throws ServiceException {
        try {
            List<ConfigItemHistoryPojo> hisList = new ArrayList<ConfigItemHistoryPojo>();
            ConfigItemHistoryPojo hisPojo;
            for (ConfigItemPojo pojo : pojoList) {
                hisPojo = new ConfigItemHistoryPojo();
                hisPojo.setConfigItemId(pojo.getConfigItemId());
                hisPojo.setSeq(configDao.getMaxSeqConfigItemHis(pojo.getConfigItemId()));
                hisPojo.setModuleCode(pojo.getModuleCode());
                hisPojo.setDirectoryCode(pojo.getDirectoryCode());
                hisPojo.setConfigItemCode(pojo.getConfigItemCode());
                hisPojo.setConfigItemName(pojo.getConfigItemName());
                hisPojo.setIsVisiable(pojo.getIsVisiable());
                hisPojo.setRemark(pojo.getRemark());
                hisPojo.setOperatorId(WebUtil.getCurrentOperatorId());
                hisPojo.setChannelId(ConfigHelper.getInt("RESOURCE.CONTACT_CHANNEL_ID"));

                hisList.add(hisPojo);
            }
            configDao.insertConfigItemHistory(hisList);
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
     * @param pojoList
     * @throws ServiceException <br>
     */
    @Override
    public void addConfigItemParamHistory(List<ConfigItemParamPojo> pojoList) throws ServiceException {
        try {
            List<ConfigItemParamHistoryPojo> hisList = new ArrayList<ConfigItemParamHistoryPojo>();
            ConfigItemParamHistoryPojo hisPojo;
            for (ConfigItemParamPojo pojo : pojoList) {
                hisPojo = new ConfigItemParamHistoryPojo();
                hisPojo.setConfigItemId(pojo.getConfigItemId());
                hisPojo.setParamCode(pojo.getParamCode());
                hisPojo.setSeq(configDao.getMaxSeqConfigItemParamHis(pojo.getConfigItemId(), pojo.getParamCode()));
                hisPojo.setParamName(pojo.getParamName());
                hisPojo.setParamValue(pojo.getParamValue());
                hisPojo.setDefaultParamValue(pojo.getDefaultParamValue());
                hisPojo.setDataType(pojo.getDataType());
                hisPojo.setInputType(pojo.getInputType());
                hisPojo.setValueScript(pojo.getValueScript());
                hisPojo.setRemark(pojo.getRemark());
                hisPojo.setOperatorId(WebUtil.getCurrentOperatorId());
                hisPojo.setChannelId(ConfigHelper.getInt("RESOURCE.CONTACT_CHANNEL_ID"));

                hisList.add(hisPojo);
            }
            configDao.insertConfigItemParamHistory(hisList);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

}
