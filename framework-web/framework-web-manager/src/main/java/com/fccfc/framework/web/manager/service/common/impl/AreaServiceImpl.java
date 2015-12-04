/**
 * 
 */
package com.fccfc.framework.web.manager.service.common.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.bean.common.AreaPojo;
import com.fccfc.framework.web.manager.dao.common.area.AreaDao;
import com.fccfc.framework.web.manager.service.common.AreaService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年1月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class AreaServiceImpl implements AreaService {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    /**
     * areaDao
     */
    @Resource
    private AreaDao areaDao;

    /**
     * Description: 获取所有的Area数据<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return
     * @throws ServiceException <br>
     */
    @Override
    public List<AreaPojo> listArea() throws ServiceException {
        try {
            return areaDao.selectList();
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    public AreaPojo qryAreaDetailById(int areaId) throws ServiceException {
        try {
            return areaDao.getById(AreaPojo.class, areaId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 写入Area数据<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param pojo 将要被写入数据库的数据
     * @throws ServiceException <br>
     */
    @Override
    public void insertArea(AreaPojo areaPojo) throws ServiceException {
        try {
            areaDao.save(areaPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 校验code是否重复<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaId
     * @param areaCode
     * @return <br>
     */
    @Override
    public boolean checkCode(String areaId, String areaCode) {
        boolean result = false;
        try {
            AreaPojo paramPojo = new AreaPojo();
            paramPojo.setAreaCode(areaCode);
            AreaPojo pojo = areaDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(areaId)) {
                result = NumberUtils.toLong(areaId) == pojo.getAreaId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    /**
     * Description: 校验名称是否重复<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaId
     * @param areaName
     * @return <br>
     */
    @Override
    public boolean checkName(String areaId, String areaName) {
        boolean result = false;
        try {
            AreaPojo paramPojo = new AreaPojo();
            paramPojo.setAreaName(areaName);
            AreaPojo pojo = areaDao.getByEntity(paramPojo);

            result = null == pojo;
            if (!result && StringUtils.isNotBlank(areaId)) {
                result = NumberUtils.toLong(areaId) == pojo.getAreaId();
            }
        }
        catch (DaoException e) {
            logger.error("", e);
        }
        return result;
    }

    /**
     * Description: 删除area<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param id
     * @throws ServiceException <br>
     */
    @Override
    public void remove(int id) throws ServiceException {
        // TODO Auto-generated method stub
        try {
            areaDao.deleteById(AreaPojo.class, id);
        }
        catch (DaoException e) {
            logger.error("", e);
        }
    }

    /**
     * Description: 修改area<br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param pojo
     * @throws ServiceException <br>
     */
    @Override
    public void modifyArea(AreaPojo pojo) throws ServiceException {
        // TODO Auto-generated method stub
        try {
            areaDao.update(pojo);
        }
        catch (DaoException e) {
            logger.error("", e);
        }
    }

    /**
     * Description: 导出消息模板<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param filePath
     * @throws ServiceException <br>
     */
    @Override
    public void exportAreaData(HttpServletResponse response) throws Exception {
        try {
            // 1、获取数据
            List<AreaPojo> list = areaDao.selectList();

            // 2、构建数据
            // 2.1、构建Excel导出数据
            String[] fields = {
                "areaId", "parentAreaId", "areaType", "areaName", "areaCode", "remark"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("areaId", "区域标识");
            fieldsHeader.put("parentAreaId", "父级区域标识");
            fieldsHeader.put("areaType", "区域类型");
            fieldsHeader.put("areaName", "区域名称");
            fieldsHeader.put("areaCode", "区域编码");
            fieldsHeader.put("remark", "备注");

            ExcelExportDto<AreaPojo> exportDto = new ExcelExportDto<AreaPojo>(fields, fieldsHeader, list);

            ExcelUtil.exportExcel(response.getOutputStream(), exportDto);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 导入消息模板<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @param filePath
     * @throws ServiceException <br>
     */
    @Override
    public void importAreaData(String filePath) throws ServiceException {
        ExcelImportCallback callback = new AreaExcelImportCallback();
        ExcelUtil.importExcel(ExcelUtil.TYPE_XLS, new File(filePath), callback);
    }

    /**
     * <Description> 实现ExcelImportCallback的内部类<br>
     * 
     * @author shao.dinghui<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2015年10月27日 <br>
     * @since V7.3<br>
     * @see com.fccfc.framework.web.manager.service.template.impl <br>
     */
    class AreaExcelImportCallback implements ExcelImportCallback {

        List<AreaPojo> areaPojos = new ArrayList<AreaPojo>();

        @Override
        public void invokeDataHandler(Row row) {
            String parentAreaCode = getCellValue(row.getCell(0));
            String areaType = getCellValue(row.getCell(1));
            String areaName = getCellValue(row.getCell(2));
            String areaCode = getCellValue(row.getCell(3));
            String remark = getCellValue(row.getCell(4));
            Integer parentAreaId = null;

            if (CommonUtil.isEmpty(areaCode)) {
                logger.debug("area code is empty");
            }
            else if (!checkCode(null, areaCode)) {
                logger.debug("area code is exist:", areaCode);
            }
            else {
                AreaPojo pojo = new AreaPojo();
                try {
                    pojo.setParentAreaCode(parentAreaCode);
                    pojo.setParentAreaId(parentAreaId);
                    pojo.setAreaType(areaType);
                    pojo.setAreaName(areaName);
                    pojo.setAreaCode(areaCode);
                    pojo.setRemark(remark);

                    // 根据parantAreaCode查询父区域
                    if (CommonUtil.isNotEmpty(parentAreaCode)) {
                        AreaPojo paramPojo = new AreaPojo();
                        paramPojo.setAreaCode(parentAreaCode);
                        AreaPojo tempPojo = areaDao.getByEntity(paramPojo);
                        if (tempPojo != null) {
                            pojo.setParentAreaId(tempPojo.getAreaId());
                        }
                    }

                }
                catch (Exception e) {
                    logger.debug("read row error", e);
                }
                areaPojos.add(pojo);
            }
        }

        @Override
        public void invokeInsertHandler() {
            for (AreaPojo pojo : areaPojos) {
                try {

                    // 根据parantAreaCode查询父区域
                    if (CommonUtil.isNotEmpty(pojo.getParentAreaCode())) {
                        try {
                            AreaPojo paramPojo = new AreaPojo();
                            paramPojo.setAreaCode(pojo.getParentAreaCode());
                            AreaPojo tempPojo = areaDao.getByEntity(paramPojo);
                            if (pojo != null) {
                                pojo.setParentAreaId(tempPojo.getAreaId());
                            }
                        }
                        catch (DaoException e) {
                            logger.error("", e);
                        }
                    }

                    areaDao.save(pojo);

                }
                catch (DaoException e) {
                    logger.debug("save row error", e);
                }
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
    }
}
