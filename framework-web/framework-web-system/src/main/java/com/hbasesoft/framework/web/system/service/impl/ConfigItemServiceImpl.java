/****************************************************************************************
 * Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 * transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 * or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.web.system.service.impl;

import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.utils.WebUtil;
import com.hbasesoft.framework.web.core.utils.excel.ExcelImportCallback;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.ConfigItemHistoryPojo;
import com.hbasesoft.framework.web.system.bean.ConfigItemParamHistoryPojo;
import com.hbasesoft.framework.web.system.bean.ConfigItemParamPojo;
import com.hbasesoft.framework.web.system.bean.ConfigItemParamValuePojo;
import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import com.hbasesoft.framework.web.system.dao.configitem.ConfigItemParamDao;
import com.hbasesoft.framework.web.system.service.ConfigItemService;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.config.core.bean.ConfigItemPojo;
import com.hbasesoft.framework.config.core.bean.DictionaryDataPojo;
import com.hbasesoft.framework.config.core.bean.DirectoryPojo;
import com.hbasesoft.framework.config.core.bean.ModulePojo;
import com.hbasesoft.framework.config.core.service.DictionaryDataService;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * <Description> <br>
 *
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @see com.hbasesoft.framework.web.system.service.impl <br>
 * @since V1.0<br>
 */
@Service
public class ConfigItemServiceImpl implements ConfigItemService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigItemServiceImpl.class);

    @Resource
    private ConfigItemParamDao configDao;

    @Resource
    private AttachmentDao attachmentDao;

    @Resource
    private DictionaryDataService dictionaryDataService;

    /**
     * Description: <br>
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public List<ConfigItemPojo> queryConfigItemPager(Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItem(pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public List<ConfigItemParamPojo> queryConfigItemParamPager(Integer configItemId, Integer pageIndex,
                                                               Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItemParam(configItemId, pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configItemId
     * @param paramCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public List<ConfigItemParamValuePojo> queryConfigItemParamValuePager(Integer configItemId, String paramCode,
                                                                         Integer pageIndex, Integer pageSize) throws ServiceException {
        try {
            return configDao.queryConfigItemParamValue(configItemId, paramCode, pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void addConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            configDao.save(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configItemId
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public ConfigItemPojo queryConfigItem(Integer configItemId) throws ServiceException {
        try {
            return configDao.getById(ConfigItemPojo.class, configItemId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void modifyConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            pojo.setUpdateTime(new Date());
            // 执行修改
            configDao.update(pojo);
            // 调用添加配置项历史的方法
            addConfigItemHistory(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configItemId
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void deleteConfigItem(Integer configItemId) throws ServiceException {
        try {
            // 调用添加配置项历史的方法
            addConfigItemHistory(configDao.getById(ConfigItemPojo.class, configItemId));
            // 执行删除
            configDao.deleteConfigItem(configItemId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void addConfigItemParam(ConfigItemParamPojo pojo) throws ServiceException {
        try {
            configDao.insertConfigItemParam(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configItemId
     * @param paramCode
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public ConfigItemParamPojo queryConfigItemParam(Integer configItemId, String paramCode) throws ServiceException {
        try {
            return configDao.queryConfigItemParam(configItemId, paramCode);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @param paramCode
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void modifyConfigItemParam(ConfigItemParamPojo pojo, String paramCode) throws ServiceException {
        try {
            pojo.setUpdateTime(new Date());
            configDao.updateConfigItemParam(pojo, paramCode);
            // 调用添加配置参数历史的方法
            addConfigItemParamHistory(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configItemId
     * @param paramCode
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void deleteConfigItemParams(Integer configItemId, String paramCode) throws ServiceException {
        try {
            addConfigItemParamHistory(configDao.queryConfigItemParam(configItemId, paramCode));
            configDao.deleteConfigItemParams(configItemId, paramCode);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void addConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException {
        try {
            configDao.save(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param paramValueId
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public ConfigItemParamValuePojo queryConfigItemParamValue(Integer paramValueId) throws ServiceException {
        try {
            return configDao.getById(ConfigItemParamValuePojo.class, paramValueId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void modifyConfigItemParamValue(ConfigItemParamValuePojo pojo) throws ServiceException {
        try {
            configDao.update(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param paramValueId
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void deleteConfigItemParamValue(Integer paramValueId) throws ServiceException {
        try {
            configDao.deleteConfigItemParamValues(paramValueId);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param pojo
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public ConfigItemPojo queryConfigItem(ConfigItemPojo pojo) throws ServiceException {
        try {
            return configDao.getByEntity(pojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public List<DirectoryPojo> queryDirectoryCode() throws ServiceException {
        try {
            return configDao.queryDirectoryCode();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public List<ModulePojo> queryModuleCode() throws ServiceException {
        try {
            return configDao.queryModuleCode();
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void importConfigItem(String mediaId, String mediaName) throws ServiceException {
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
                    } catch (Exception e) {
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
                        } catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item list.", e);
                        }
                    }
                }
            });

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param existList
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void importConfigItemParam(String mediaId, String mediaName) throws ServiceException {
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
                    } catch (Exception e) {
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
                        } catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item param list.", e);
                        }
                    }
                }
            });

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param existList
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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
     * @param existList
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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
     * @param mediaId
     * @param mediaName
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void importConfigItemParamValue(String mediaId, String mediaName) throws ServiceException {
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
                    } catch (Exception e) {
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
                        } catch (DaoException e) {
                            logger.error("It's appear exception while batch insert config item param value list.", e);
                        }
                    }
                }
            });

        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param existList
     * @return <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
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
     * @param configItemPojo
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void addConfigItemHistory(ConfigItemPojo configItemPojo) throws ServiceException {
        try {
            ConfigItemHistoryPojo hisPojo = new ConfigItemHistoryPojo();
            hisPojo.setConfigItemId(configItemPojo.getConfigItemId());
            hisPojo.setSeq(configDao.getMaxSeqConfigItemHis(configItemPojo.getConfigItemId()));
            hisPojo.setModuleCode(configItemPojo.getModuleCode());
            hisPojo.setDirectoryCode(configItemPojo.getDirectoryCode());
            hisPojo.setConfigItemCode(configItemPojo.getConfigItemCode());
            hisPojo.setConfigItemName(configItemPojo.getConfigItemName());
            hisPojo.setIsVisiable(configItemPojo.getIsVisiable());
            hisPojo.setUpdateTime(new Date());
            hisPojo.setRemark(configItemPojo.getRemark());
            hisPojo.setOperatorId(WebUtil.getCurrentOperatorId());
            hisPojo.setChannelId(PropertyHolder.getIntProperty("contact.channel.id"));
            configDao.insertConfigItemHistory(hisPojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: <br>
     *
     * @param configParam
     * @throws ServiceException <br>
     * @author yang.zhipeng <br>
     * @taskId <br>
     */
    @Override
    public void addConfigItemParamHistory(ConfigItemParamPojo configParam) throws ServiceException {
        try {
            ConfigItemParamHistoryPojo hisPojo = new ConfigItemParamHistoryPojo();
            hisPojo.setConfigItemId(configParam.getConfigItemId());
            hisPojo.setParamCode(configParam.getParamCode());
            hisPojo.setSeq(configDao.getMaxSeqConfigItemParamHis(configParam.getConfigItemId(), configParam.getParamCode()));
            hisPojo.setParamName(configParam.getParamName());
            hisPojo.setParamValue(configParam.getParamValue());
            hisPojo.setDefaultParamValue(configParam.getDefaultParamValue());
            hisPojo.setDataType(configParam.getDataType());
            hisPojo.setInputType(configParam.getInputType());
            hisPojo.setUpdateTime(new Date());
            hisPojo.setValueScript(configParam.getValueScript());
            hisPojo.setRemark(configParam.getRemark());
            hisPojo.setOperatorId(WebUtil.getCurrentOperatorId());
            hisPojo.setChannelId(PropertyHolder.getIntProperty("contact.channel.id"));
            configDao.insertConfigItemParamHistory(hisPojo);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 配置map
     */
    @Override
    public List<Map<String, String>> getMap(String dictCode) throws ServiceException {

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        List<DictionaryDataPojo> dictDatas = dictionaryDataService.qryAlldictData(dictCode);
        if (CommonUtil.isNotEmpty(dictDatas)) {
            for (int i = 0; i < dictDatas.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("value", dictDatas.get(i).getDictDataName());
                map.put("key", dictDatas.get(i).getDictDataValue());
                list.add(map);
            }
            /*
             * dictDatas.forEach((dict) -> { map.put("value", dict.getDictDataName()); map.put("key",
             * dict.getDictDataValue()); list.add(map); });
             */
        }
        return list;
    }

}
