package com.hbasesoft.framework.web.system.service.impl;

import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.utils.excel.ExcelExportDto;
import com.hbasesoft.framework.web.core.utils.excel.ExcelImportCallback;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.dao.attachments.AttachmentDao;
import com.hbasesoft.framework.web.system.dao.event.EventDao;
import com.hbasesoft.framework.web.system.service.EventService;
import com.hbasesoft.framework.cache.core.CacheConstant;
import com.hbasesoft.framework.cache.core.annotation.Cache;
import com.hbasesoft.framework.cache.core.annotation.CacheKey;
import com.hbasesoft.framework.cache.core.annotation.RmCache;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.db.core.DaoException;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * <Description> <br>
 *
 * @author bai.wenlong<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月13日 <br>
 * @see com.hbasesoft.framework.web.service.impl <br>
 * @since V6.11<br>
 */
@Service
public class EventServiceImpl implements EventService {
    /**
     * operateLogDao
     */
    @Resource
    private EventDao eventDao;

    @Resource
    private AttachmentDao attachmentDao;

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    /**
     * 查询所有
     */
    @Override
    public List<EventPojo> selectList(int pageIndex, int pageSize) throws ServiceException {
        try {
            return eventDao.selectList(pageIndex, pageSize);
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * 新增
     */
    @Override
    @RmCache(node = CacheConstant.EVENT)
    public void saveEvent(EventPojo event) throws ServiceException {
        try {
            eventDao.save(event);
            logger.info("保存事件成功");
        } catch (DaoException e) {
            throw new ServiceException(e);
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
        } catch (DaoException e) {
            throw new ServiceException(e);
        }
        return result;
    }

    /**
     * 跳修改页面
     */
    @Override
    @Cache(node = CacheConstant.EVENT)
    public EventPojo getEventByCode(@CacheKey String eventCode) throws ServiceException {
        try {
            return eventDao.queryByCode(eventCode);
        } catch (DaoException e) {
            throw new ServiceException("获取修改页面ID失败", e);
        }
    }

    /**
     * 修改
     */
    @Override
    @RmCache(node = CacheConstant.EVENT)
    public void modify(EventPojo event) throws ServiceException {
        try {
            eventDao.update(event);
        } catch (DaoException e) {
            throw new ServiceException("修改页面失败", e);
        }

    }

    /**
     * 删除
     */
    @Override
    @RmCache(node = CacheConstant.EVENT)
    public void delete(Integer[] ids) throws ServiceException {
        EventPojo event = new EventPojo();
        try {
            for (int i = 0; i < ids.length; i++) {
                event.setEventId(ids[i]);
                eventDao.delete(event);
            }
        } catch (DaoException e) {
            throw new ServiceException(e);
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }

    @Override
    @RmCache(node = CacheConstant.EVENT)
    public void importEvent(String mediaId, String mediaName) throws ServiceException {

        AttachmentsPojo attachment;
        try {
            attachment = attachmentDao.getById(AttachmentsPojo.class, NumberUtils.toLong(mediaId));
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
            ExcelImportCallback callback = new EventExcelImportCallback();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, callback);
        } catch (DaoException e) {
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
                } catch (DaoException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * Description: <br>
     *
     * @param eventId
     * @return
     * @throws ServiceException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public EventPojo getEventById(Integer eventId) throws ServiceException {
        try {
            return eventDao.getById(EventPojo.class, eventId);
        } catch (DaoException e) {
            throw new ServiceException("获取修改页面ID失败", e);
        }
    }

}
