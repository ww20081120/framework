package com.fccfc.framework.web.manager.service.permission.impl;

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
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;
import com.fccfc.framework.web.manager.dao.common.AttachmentsDao;
import com.fccfc.framework.web.manager.dao.permission.menu.FunctionDao;
import com.fccfc.framework.web.manager.service.permission.FunctionService;

/**
 * <Description> 功能点配置<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月31日 <br>
 * @see com.fccfc.framework.web.manager.service.menu.impl <br>
 * @since V1.0<br>
 */
@Service
public class FunctionServiceImpl implements FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionServiceImpl.class);

    @Resource
    private FunctionDao functionDao;

    @Resource
    private AttachmentsDao attachmentsDao;

    /**
     * Description: 分页查询所有功能点<br>
     *
     * @param functionName
     * @param pageIndex
     * @param pageSize
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<FunctionPojo> queryFunction(String directoryCode, String functionName, Integer pageIndex,
        Integer pageSize) throws ServiceException {
        try {
            return functionDao.selectList(directoryCode, functionName, pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public int listTotal(String directoryCode, String functionName) throws ServiceException {
        try {
            return functionDao.count(directoryCode, functionName);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加功能点信息<br>
     *
     * @param functionPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void addFunction(FunctionPojo functionPojo) throws ServiceException {
        try {
            functionPojo.setCreateTime(new Date());
            functionDao.save(functionPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改功能点信息<br>
     *
     * @param functionPojo
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void modifyFunction(FunctionPojo functionPojo) throws ServiceException {
        try {
            functionDao.update(functionPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 根据functionId查询FunctionPojo对象<br>
     *
     * @param functionId
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public FunctionPojo queryFunction(Long functionId) throws ServiceException {
        try {
            return functionDao.getById(FunctionPojo.class, functionId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 批量删除<br>
     *
     * @param functionIds
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public void deleteFunctions(Long[] functionIds) throws ServiceException {
        try {
            if (functionIds.length == 1) {
                functionDao.deleteById(FunctionPojo.class, functionIds[0]);
            }
            else {
                functionDao.deleteByFunctionId(functionIds);
            }
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description:查询所有的功能模块 <br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @Override
    public List<FunctionPojo> queryFunction() throws ServiceException {
        try {
            return functionDao.selectList(null, null, -1, -1);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean checkName(String functionId, String functionName) {
        boolean result = false;
        try {
            FunctionPojo paramPojo = new FunctionPojo();
            paramPojo.setFunctionName(functionName);
            FunctionPojo pojo = functionDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(functionId)) {
                result = NumberUtils.toLong(functionId) == pojo.getFunctionId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    @Override
    public void importFunction(String directoryCode, String mediaId, String mediaName) throws ServiceException {
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

            final List<Long> existList = assembleExist(functionDao.selectList(FunctionPojo.class));
            final List<FunctionPojo> functionPojoList = new ArrayList<FunctionPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                @Override
                public void invokeDataHandler(Row row) {
                    try {
                        Long id = NumberUtils.toLong(StringUtils.trim(row.getCell(0).getStringCellValue()));
                        if (!existList.contains(id)) {
                            FunctionPojo functionPojo = new FunctionPojo();
                            functionPojo.setFunctionId(id);
                            functionPojo.setDirectoryCode(StringUtils.trim(row.getCell(1).getStringCellValue()));
                            functionPojo.setFunctionName(StringUtils.trim(row.getCell(2).getStringCellValue()));
                            functionPojo.setRemark(StringUtils.trim(row.getCell(3).getStringCellValue()));
                            functionPojoList.add(functionPojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                @Override
                public void invokeInsertHandler() {
                    if (CollectionUtils.isNotEmpty(functionPojoList)) {
                        try {
                            int lines = functionDao.batchInsert(functionPojoList);
                            logger.info("Batch insert function list [lines = {}].", lines);
                            functionPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert function list.", e);
                        }
                    }
                }
            });

        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    private List<Long> assembleExist(List<FunctionPojo> existList) {
        List<Long> exist = new ArrayList<Long>();
        for (FunctionPojo functionPojo : existList) {
            exist.add(functionPojo.getFunctionId());
        }
        return exist;
    }
}
