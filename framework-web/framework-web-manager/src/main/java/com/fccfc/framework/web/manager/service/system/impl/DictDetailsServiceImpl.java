package com.fccfc.framework.web.manager.service.system.impl;

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

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.ConfigHelper;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.system.DictionaryDataPojo;
import com.fccfc.framework.web.manager.bean.system.DictionaryPojo;
import com.fccfc.framework.web.manager.dao.common.AttachmentsDao;
import com.fccfc.framework.web.manager.dao.system.dict.DictDao;
import com.fccfc.framework.web.manager.service.system.DictDetailsService;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月28日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.web.manager.service.dict.impl <br>
 */
@Service
public class DictDetailsServiceImpl implements DictDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(DictDetailsServiceImpl.class);

    @Resource
    private DictDao dictDao;

    @Resource
    private AttachmentsDao attachmentsDao;

    /**
     * Description: 查询所有的数据字典<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<DictionaryPojo> queryDictPager(int pageIndex, int pageSize) throws ServiceException {
        try {
            return dictDao.queryDict(pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加数据字典信息<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void addDict(DictionaryPojo pojo) throws ServiceException {
        try {
            dictDao.save(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改数据字典信息<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void modifyDict(DictionaryPojo pojo, String oldDictCode) throws ServiceException {
        try {
            dictDao.updateDict(pojo, oldDictCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 删除数据字典信息<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param dictCode
     * @throws ServiceException <br>
     */
    @Override
    public void deleteDict(String dictCode) throws ServiceException {
        try {
            dictDao.deleteById(DictionaryPojo.class, dictCode);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * <Description> 实现ExcelImportCallback的内部类<br>
     * 
     * @author shao.dinghui<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2015年10月28日 <br>
     * @since V7.3<br>
     * @see com.fccfc.bps.web.service.impl <br>
     */
    class DictExcelImportCallback implements ExcelImportCallback {

        List<DictionaryPojo> list = new ArrayList<DictionaryPojo>();

        @Override
        public void invokeDataHandler(Row row) {
            String dictCode = row.getCell(0).getStringCellValue();
            String dictName = row.getCell(1).getStringCellValue();
            String remark = row.getCell(2).getStringCellValue();

            if (CommonUtil.isNull(dictCode)) {
                return;
            }

            DictionaryPojo pojo = new DictionaryPojo();
            pojo.setDictCode(dictCode);
            pojo.setDictName(dictName);
            pojo.setRemark(remark);

            list.add(pojo);
        }

        @Override
        public void invokeInsertHandler() {
            for (DictionaryPojo pojo : list) {
                try {
                    dictDao.save(pojo);
                }
                catch (DaoException e) {
                }
            }
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<DictionaryDataPojo> queryDictDataPager(String dictCode, int pageIndex, int pageSize)
        throws ServiceException {
        try {
            return dictDao.queryDictData(dictCode, pageIndex, pageSize);
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
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public boolean checkDictCode(String dictCode) throws ServiceException {
        boolean result = false;
        try {
            DictionaryPojo paramPojo = new DictionaryPojo();
            paramPojo.setDictCode(dictCode);
            DictionaryPojo pojo = (DictionaryPojo) dictDao.getById(DictionaryPojo.class, dictCode);
            if (null == pojo) {
                result = true;
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public DictionaryPojo queryDict(String dictCode) throws ServiceException {
        try {
            return dictDao.getById(DictionaryPojo.class, dictCode);
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
     * @param dictCodes
     * @throws ServiceException <br>
     */
    @Override
    public void deleteDicts(String[] dictCodes) throws ServiceException {
        try {
            dictDao.deleteDicts(dictCodes);
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
    public void addDictData(DictionaryDataPojo pojo) throws ServiceException {
        try {
            dictDao.save(pojo);
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
     * @param dictDataId
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public DictionaryDataPojo queryDictData(Integer dictDataId) throws ServiceException {
        try {
            return dictDao.getById(DictionaryDataPojo.class, dictDataId);
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
    public void modifyDictData(DictionaryDataPojo pojo) throws ServiceException {
        try {
            dictDao.update(pojo);
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
     * @param dictDataIds
     * @throws ServiceException <br>
     */
    @Override
    public void deleteDictData(Integer[] dictDataIds) throws ServiceException {
        try {
            dictDao.deleteDictDatas(dictDataIds);
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
    public List<DictionaryPojo> queryDict() throws ServiceException {
        try {
            return dictDao.queryDict();
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
    public List<DictionaryDataPojo> queryDictData() throws ServiceException {
        try {
            return dictDao.queryDictData();
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
     * @param dictCode
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<DictionaryDataPojo> queryDictData(String dictCode) throws ServiceException {
        try {
            return dictDao.queryDictData(dictCode);
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
    public void importDict(String mediaId, String mediaName) throws ServiceException {
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

            final List<String> existList = assembleExistDict(queryDict());
            final List<DictionaryPojo> dictPojoList = new ArrayList<DictionaryPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        String dictCode = StringUtils.trim(row.getCell(0).getStringCellValue());
                        if (!existList.contains(dictCode)) {
                            DictionaryPojo pojo = new DictionaryPojo();
                            pojo.setDictCode(dictCode);
                            pojo.setDictName((StringUtils.trim(row.getCell(1).getStringCellValue())));
                            pojo.setRemark(
                                (StringUtils.trim(null == row.getCell(2) ? "" : row.getCell(2).getStringCellValue())));

                            dictPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(dictPojoList)) {
                        try {
                            int lines = dictDao.batchInsertDict(dictPojoList);
                            logger.info("Batch insert dictionary list [lines = {}].", lines);
                            dictPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert dictionary list.", e);
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
    private List<String> assembleExistDict(List<DictionaryPojo> existList) {
        List<String> exist = new ArrayList<String>();
        for (DictionaryPojo pojo : existList) {
            exist.add(pojo.getDictCode());
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
    public void importDictData(String mediaId, String mediaName) throws ServiceException {
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

            final List<Integer> existList = assembleExistDictData(queryDictData());
            final List<DictionaryDataPojo> dictDataPojoList = new ArrayList<DictionaryDataPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Integer dictDataId = (int) row.getCell(0).getNumericCellValue();
                        String dictCode = StringUtils.trim(row.getCell(1).getStringCellValue());
                        if (!existList.contains(dictDataId)) {
                            if (CommonUtil.isNotEmpty(dictCode)) {
                                DictionaryPojo dictPojo = queryDict(dictCode);
                                if (null == dictPojo) {
                                    return;
                                }
                            }
                            DictionaryDataPojo pojo = new DictionaryDataPojo();
                            pojo.setDictDataId(dictDataId);
                            pojo.setDictCode(dictCode);
                            pojo.setDictDataName((StringUtils.trim(row.getCell(2).getStringCellValue())));
                            pojo.setDictDataValue((StringUtils.trim(row.getCell(3).getStringCellValue())));
                            pojo.setIsFixed((StringUtils.trim(row.getCell(4).getStringCellValue())));
                            pojo.setIsCancel(
                                (StringUtils.trim(null == row.getCell(5) ? "" : row.getCell(5).getStringCellValue())));

                            dictDataPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(dictDataPojoList)) {
                        try {
                            int lines = dictDao.batchInsertDictData(dictDataPojoList);
                            logger.info("Batch insert dictionary data list [lines = {}].", lines);
                            dictDataPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert dictionary data list.", e);
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
    private List<Integer> assembleExistDictData(List<DictionaryDataPojo> existList) {
        List<Integer> exist = new ArrayList<Integer>();
        for (DictionaryDataPojo pojo : existList) {
            exist.add(pojo.getDictDataId());
        }
        return exist;
    }
}
