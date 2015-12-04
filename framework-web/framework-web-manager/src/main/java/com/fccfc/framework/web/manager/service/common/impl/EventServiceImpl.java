package com.fccfc.framework.web.manager.service.common.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
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
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.dao.common.AttachmentsDao;
import com.fccfc.framework.web.manager.dao.common.event.EventDao;
import com.fccfc.framework.web.manager.service.common.EventService;

/***
 * <Description> <br>
 * 
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @since V6.11<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class EventServiceImpl implements EventService {
    /**
     * operateLogDao
     */
    @Resource
    private EventDao eventDao;

    @Resource
    private AttachmentsDao attachmentsDao;

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    /** 查询所有 */
    @Override
    public List<EventPojo> selectList(int pageIndex, int pageSize) throws ServiceException {
        try {
            return eventDao.selectList(pageIndex, pageSize);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 新增
     */
    @Override
    public void saveEvent(EventPojo event) throws ServiceException {
        try {
            eventDao.save(event);
            logger.info("保存事件成功");
        }
        catch (DaoException e) {
            e.printStackTrace();
        }

    }

    /**
     * 新增事件名称校验
     */
    @Override
    public boolean checkUserName(String eventId, String eventName) throws ServiceException {
        boolean result = false;
        logger.info(eventId);
        try {
            EventPojo event = new EventPojo();
            event.setEventName(eventName);
            EventPojo pojo = eventDao.getByEntity(event);
            result = null == pojo;
            logger.info("result1" + result);
            if (!result && StringUtils.isNotBlank(eventId)) {
                result = NumberUtils.toLong(eventId) == pojo.getEventId();
            }

            logger.info("result2" + result);
        }
        catch (DaoException e) {
            logger.error("事件名称校验*", e);
        }
        return result;
    }

    /**
     * 跳修改页面
     */
    @Override
    public EventPojo getEvent(Integer eventId) throws ServiceException {
        try {
            return eventDao.getById(EventPojo.class, eventId);
        }
        catch (DaoException e) {
            throw new ServiceException("获取修改页面ID失败", e);
        }
    }

    /**
     * 修改
     */
    @Override
    public void modify(EventPojo event) throws ServiceException {
        try {
            eventDao.update(event);
        }
        catch (DaoException e) {
            throw new ServiceException("修改页面失败", e);
        }

    }

    /**
     * 删除
     */
    @Override
    public void delete(Integer[] ids) throws ServiceException {
        EventPojo event = new EventPojo();
        try {
            for (int i = 0; i < ids.length; i++) {
                event.setEventId(ids[i]);
                eventDao.delete(event);
            }
        }
        catch (DaoException e) {
            e.printStackTrace();
        }

    }

    /**
     * 导出
     */
    @Override
    public void exportEvent(HttpServletResponse response, int pageIndex, int pageSize) throws ServiceException {
        try {
            List<EventPojo> list = eventDao.selectList(pageIndex, pageSize);
            String[] fields = {
                "eventId", "eventType", "paramsName", "eventName", "remark"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("eventId", "事件标识");
            fieldsHeader.put("eventType", "事件类型");
            fieldsHeader.put("paramsName", "参数名");
            fieldsHeader.put("eventName", "事件名称");
            fieldsHeader.put("remark", "备注");
            ExcelExportDto<EventPojo> exportDto = new ExcelExportDto<EventPojo>(fields, fieldsHeader, list);
            OutputStream os = null;
            try {
                response.setHeader("Content-Disposition",
                    "attachment;filename=\"" + new String("事件.xls".getBytes(), "ISO8859-1") + "\"");
                os = response.getOutputStream();
                ExcelUtil.exportExcel(os, exportDto);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                }
                catch (IOException e) {
                }
            }
        }
        catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void importEvent(String mediaId, String mediaName) throws ServiceException {

        AttachmentsPojo attachment;
        try {
            attachment = attachmentsDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
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
            ExcelImportCallback callback = new EventExcelImportCallback();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, callback);
        }
        catch (DaoException e) {
            e.printStackTrace();
        }
    }

    class EventExcelImportCallback implements ExcelImportCallback {
        List<EventPojo> event = new ArrayList<EventPojo>();

        @Override
        public void invokeDataHandler(Row row) {
            String eventId = row.getCell(0).getStringCellValue();
            String eventType = row.getCell(1).getStringCellValue();
            String paramsName = row.getCell(2).getStringCellValue();
            String eventName = row.getCell(3).getStringCellValue();
            String remark = row.getCell(4).getStringCellValue();
            if (CommonUtil.isNull(eventId)) {
                return;
            }
            EventPojo pojo = new EventPojo();
            pojo.setEventId(Integer.parseInt(eventId));
            pojo.setEventType(eventType);
            pojo.setEventName(eventName);
            pojo.setParamsName(paramsName);
            pojo.setRemark(remark);
            event.add(pojo);
        }

        @Override
        public void invokeInsertHandler() {
            for (EventPojo e : event) {
                try {
                    EventPojo pojo = new EventPojo();
                    pojo = eventDao.getById(EventPojo.class, e.getEventId());
                    if (pojo == null) {
                        eventDao.save(e);
                    }
                }
                catch (DaoException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
