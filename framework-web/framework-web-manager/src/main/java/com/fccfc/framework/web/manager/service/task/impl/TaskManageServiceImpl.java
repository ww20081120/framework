package com.fccfc.framework.web.manager.service.task.impl;

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

import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.ConfigHelper;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.config.core.dao.ModuleDao;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.task.core.bean.TaskPojo;
import com.fccfc.framework.task.core.dao.JobDao;
import com.fccfc.framework.web.core.utils.excel.ExcelImportCallback;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.ManagerConstant;
import com.fccfc.framework.web.manager.dao.common.AttachmentsDao;
import com.fccfc.framework.web.manager.dao.system.task.TaskDao;
import com.fccfc.framework.web.manager.service.task.TaskManageService;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月19日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.service.task.impl <br>
 */
@Service
public class TaskManageServiceImpl implements TaskManageService {

    /**
     * jobDao
     */
    @Resource
    private JobDao jobDao;

    /**
     * taskDao
     */
    @Resource
    private TaskDao taskDao;

    /**
     * moduleDao
     */
    @Resource
    private ModuleDao moduleDao;

    /**
     * attachmentsDao
     */
    @Resource
    private AttachmentsDao attachmentsDao;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskManageServiceImpl.class);

    /**
     * Description: 根据已有信息查询任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @param pageIndex <br>
     * @param pageSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<TaskPojo> queryAllTask(TaskPojo taskPojo, int pageIndex, int pageSize) throws ServiceException {
        try {
            return jobDao.selectTaskList(taskPojo, pageIndex, pageSize);
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
     * @param taskPojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<TaskPojo> queryTaskById(TaskPojo taskPojo) throws ServiceException {
        try {
            return taskDao.selectTaskList(taskPojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 新增 修改 导入校验<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<TaskPojo> checkTask(TaskPojo taskPojo) throws ServiceException {
        try {
            return taskDao.checkTask(taskPojo);
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
    public List<ModulePojo> queryModule(ModulePojo pojo) throws ServiceException {
        try {
            return taskDao.selectAllModule(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 添加任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void addTask(TaskPojo pojo) throws ServiceException {
        try {
            taskDao.insertTask(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 修改任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param pojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void modifyTask(TaskPojo pojo) throws ServiceException {
        try {
            taskDao.updateTask(pojo);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 删除任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskId <br>
     * @throws ServiceException <br>
     */
    @Override
    public void deleteTask(Integer taskId) throws ServiceException {
        try {
            jobDao.deleteById(TaskPojo.class, taskId);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 导入<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param mediaId <br>
     * @param mediaName <br>
     * @throws ServiceException <br>
     */
    @Override
    public void importTaskData(String mediaId, String mediaName) throws ServiceException {
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

            final List<TaskPojo> taskPojoList = new ArrayList<TaskPojo>();
            ExcelUtil.importExcel(attachment.getAttachmentsType(), file, new ExcelImportCallback() {
                private TaskPojo taskPojoCheck;

                @Override
                public void invokeDataHandler(Row row) {

                    try {
                        boolean flag = this.checkRow(row); // 判断是否执行此行数据
                        if (flag) {

                            TaskPojo pojo = new TaskPojo();
                            pojo.setModuleCode(getCellValue(row.getCell(0)));
                            pojo.setTaskName(getCellValue(row.getCell(1)));
                            pojo.setClassName(getCellValue(row.getCell(2)));
                            pojo.setMethod(getCellValue(row.getCell(3)));
                            pojo.setPriority(Integer.valueOf(getCellValue(row.getCell(4))));
                            pojo.setIsConcurrent(getCellValue(row.getCell(5)));
                            pojo.setTaskState(TaskPojo.TASK_STATE_INITIAL);
                            pojo.setOperatorId(Integer.valueOf(10086));

                            taskPojoList.add(pojo);
                        }
                    }
                    catch (Exception e) {
                        logger.error("It's appear exception while handler row data.", e);
                    }
                }

                private boolean checkRow(Row row) throws ServiceException {
                    List<TaskPojo> taskListCheck = new ArrayList<TaskPojo>();
                    taskPojoCheck = new TaskPojo();
                    // 校验任务内容
                    taskPojoCheck.setModuleCode(getCellValue(row.getCell(0)));
                    taskPojoCheck.setClassName(getCellValue(row.getCell(2)));
                    taskPojoCheck.setMethod(getCellValue(row.getCell(3)));
                    taskListCheck = queryTaskById(taskPojoCheck);

                    if (CommonUtil.isNotEmpty(taskListCheck)) { // 存在已有方法名 模块名 类名对应的任务
                        return false;
                    }
                    // 校验任务名称
                    taskPojoCheck = new TaskPojo();
                    taskListCheck = null;
                    taskPojoCheck.setTaskName(getCellValue(row.getCell(1)));
                    taskListCheck = queryTaskById(taskPojoCheck);

                    if (CommonUtil.isNotEmpty(taskListCheck)) { // 存在已有任务名
                        return false;
                    }
                    // 校验是否并发
                    if (!"Y".equals(getCellValue(row.getCell(5))) && !"N".equals(getCellValue(row.getCell(5)))) {
                        return false;
                    }

                    // 模块编码是否存在
                    ModulePojo modulePojo = new ModulePojo();
                    modulePojo.setModuleCode(getCellValue(row.getCell(0)));
                    List<ModulePojo> moduleList = queryModule(modulePojo);
                    if (CommonUtil.isEmpty(moduleList)) {
                        return false;
                    }
                    return true;
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
                    if (CollectionUtils.isNotEmpty(taskPojoList)) {
                        try {
                            for (TaskPojo pojo : taskPojoList) {
                                int lines = taskDao.insertTask(pojo);
                                logger.info("Batch insert task list [lines = {}].", lines);
                            }
                            taskPojoList.clear();
                        }
                        catch (DaoException e) {
                            logger.error("It's appear exception while batch insert task list.", e);
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
     * Description:运行暂停任务<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskIds <br>
     * @param state <br>
     * @throws ServiceException <br>
     */
    @Override
    public void modifyTaskState(String[] taskIds, String state) throws ServiceException {
        try {
            // "P"表示暂停状态
            taskDao.modifyTaskState(taskIds, state);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Description: 插入历史表<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param taskPojo <br>
     * @throws ServiceException <br>
     */
    @Override
    public void insertTaskHis(TaskPojo taskPojo) throws ServiceException {

        try {
            jobDao.insertTaskHistory(taskPojo.getTaskId(), taskPojo.getOperatorId());
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
     * @param taskIds <br>
     * @param taskStates <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<TaskPojo> queryTaskByState(String[] taskIds, String[] taskStates) throws ServiceException {
        try {
            return taskDao.queryTaskByState(taskIds, taskStates);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }
}
