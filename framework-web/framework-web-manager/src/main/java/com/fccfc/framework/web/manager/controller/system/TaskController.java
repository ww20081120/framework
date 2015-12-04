package com.fccfc.framework.web.manager.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.task.core.bean.TaskPojo;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.task.TaskManageService;

/**
 * <Description> 任务管理<br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月18日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.controller.task <br>
 */
@Controller
@RequestMapping("/task")
public class TaskController extends AbstractController {

    /** taskManageService */
    @Resource
    private TaskManageService taskManageService;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    /**
     * Description: 跳转页面<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toTask() {
        return "task/index";
    }

    /**
     * Description: 查询所有任务信息 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    public Map<String, Object> queryAllTask() throws ServiceException {
        PagerList<TaskPojo> list = (PagerList<TaskPojo>) taskManageService.queryAllTask(null, getPageIndex(),
            getPageSize());

        logger.info("查询成功");
        for (TaskPojo task : list) {

            if (TaskPojo.TASK_STATE_ACQUIRED.equals(task.getTaskState())) {
                task.setTaskState("执行中");
            }
            else if (TaskPojo.TASK_STATE_BLOCKED.equals(task.getTaskState())) {
                task.setTaskState("阻塞");
            }
            else if (TaskPojo.TASK_STATE_ERROR.equals(task.getTaskState())) {
                task.setTaskState("错误");
            }
            else if (TaskPojo.TASK_STATE_INITIAL.equals(task.getTaskState())) {
                task.setTaskState("初始");
            }
            else if (TaskPojo.TASK_STATE_PAUSED.equals(task.getTaskState())) {
                task.setTaskState("暂停");
            }
            else if (TaskPojo.TASK_STATE_WAITING.equals(task.getTaskState())) {
                task.setTaskState("等待");
            }
            else if (TaskPojo.TASK_STATE_COMPLETE.equals(task.getTaskState())) {
                task.setTaskState("完成");
            }

            if ("Y".equals(task.getIsConcurrent())) {
                task.setIsConcurrent("是");
            }
            else if ("N".equals(task.getIsConcurrent())) {
                task.setIsConcurrent("否");
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", list);
        result.put("pageIndex", list.getPageIndex());
        result.put("pageSize", list.getPageSize());
        result.put("totalCount", list.getTotalCount());
        result.put("totalPage", list.getTotalPage());
        return result;
    }

    /**
     * Description: 跳转新增修改页面<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @RequestMapping(value = "/toTaskManager", method = RequestMethod.GET)
    public ModelAndView getTaskManager() throws ServiceException {

        Map<String, Object> map = new HashMap<String, Object>();
        String taskId = getParameter("taskId");
        if (CommonUtil.isNotEmpty(taskId)) { // task管理页面为修改功能
            TaskPojo taskPojo = new TaskPojo();
            taskPojo.setTaskId(Integer.valueOf(taskId));
            List<TaskPojo> taskList = taskManageService.queryTaskById(taskPojo);
            map.put("pojo", taskList.get(0));
        }

        List<ModulePojo> moduleList = taskManageService.queryModule(null);
        map.put("moduleList", moduleList);

        return new ModelAndView("task/taskManager", map);
    }

    /**
     * Description: 新增修改任务信息<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/taskManager", method = RequestMethod.POST)
    public ModelAndView taskManager() throws Exception {

        String returnSuccess = "";
        try {
            // 1.获取页面参数
            String taskId = getParameter("taskId");
            String taskName = getParameter("taskName");
            String className = getParameter("className");
            String method = getParameter("method");
            Integer priority = getIntegerParameter("priority");
            String isConcurrent = getParameter("isConcurrent");
            String moduleCode = getParameter("moduleCode");

            // 2.必要的校验
            List<TaskPojo> taskListCheck = new ArrayList<TaskPojo>();
            TaskPojo taskPojoCheck = new TaskPojo();
            if (CommonUtil.isNotEmpty(taskId)) {
                taskPojoCheck.setTaskId(Integer.valueOf(taskId));
            }
            taskPojoCheck.setModuleCode(moduleCode);
            taskPojoCheck.setTaskName(taskName);
            taskPojoCheck.setMethod(method);
            taskListCheck = taskManageService.checkTask(taskPojoCheck);

            if (CommonUtil.isEmpty(taskId) && CommonUtil.isNotEmpty(taskListCheck)) { // 新增判断
                return fail("请检查上是否存在已有方法名 模块名 类名对应的任务");
            }
            else if (CommonUtil.isNotEmpty(taskId) && taskListCheck.size() > 1) { // 修改判断
                return fail("请检查上是否存在已有方法名 模块名 类名对应的任务");
            }

            taskPojoCheck = new TaskPojo();
            taskPojoCheck.setTaskName(taskName);

            taskListCheck = taskManageService.checkTask(taskPojoCheck);

            if (CommonUtil.isEmpty(taskId) && CommonUtil.isNotEmpty(taskListCheck)) { // 新增判断
                return fail("已存在任务名称");
            }
            else if (CommonUtil.isNotEmpty(taskId) && taskListCheck.size() > 1) { // 修改判断
                return fail("已存在任务名称");
            }

            // 3.taskId有为修改 无为新增
            if (CommonUtil.isEmpty(taskId)) { // 新增
                TaskPojo taskPojoA = new TaskPojo();
                String taskState = TaskPojo.TASK_STATE_INITIAL; // 初始状态
                Integer operatorId = Integer.valueOf(10086);
                taskPojoA.setTaskState(taskState);
                taskPojoA.setOperatorId(operatorId);
                taskPojoA.setClassName(className);
                taskPojoA.setIsConcurrent(isConcurrent);
                taskPojoA.setMethod(method);
                taskPojoA.setModuleCode(moduleCode);

                taskPojoA.setPriority(priority);
                taskPojoA.setTaskName(taskName);

                taskManageService.addTask(taskPojoA);

                returnSuccess = "新增成功";
            }
            else { // 修改
                TaskPojo taskPojo = new TaskPojo();
                taskPojo.setTaskId(Integer.valueOf(taskId));
                List<TaskPojo> taskList = taskManageService.queryTaskById(taskPojo);
                if (!taskList.isEmpty()) {
                    // 插入历史表
                    taskManageService.insertTaskHis(taskList.get(0));

                    taskList.get(0).setClassName(className);
                    taskList.get(0).setIsConcurrent(isConcurrent);
                    taskList.get(0).setMethod(method);

                    taskList.get(0).setPriority(priority);
                    taskList.get(0).setTaskName(taskName);
                    taskList.get(0).setModuleCode(moduleCode);

                    // 修改信息
                    taskManageService.modifyTask(taskList.get(0));

                }
                returnSuccess = "修改成功";
            }
        }
        catch (Exception e) {
            logger.info("errorMessage:" + e.getMessage());
            return fail("操作失败");
        }

        return success(returnSuccess);
    }

    /**
     * Description: 删除任务<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/deleteTask", method = RequestMethod.POST)
    public String deleteTask() throws Exception {

        String taskIdArr = getParameter("ids");
        String[] taskId = taskIdArr.split(",");
        try {
            if (taskId.length > 0) {
                for (int i = 0; i < taskId.length; i++) {
                    taskManageService.deleteTask(Integer.valueOf(taskId[i]));
                }
            }
        }
        catch (Exception e) {
            logger.info("errorMessage:" + e.getMessage());
            return "failure";
        }
        return "success";
    }

    /**
     * Description:从系统中将任务数据导出为Excel文件<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @throws Exception <br>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/exportTask", method = RequestMethod.GET)
    public void exportTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            List<TaskPojo> list = taskManageService.queryTaskById(null);
            String[] fields = {
                "taskId", "moduleCode", "taskName", "className", "method", "priority", "isConcurrent", "taskState",
                "lastExecuteTime", "nextExcuteDate", "operatorId", "createTime"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("taskId", "任务标识");
            fieldsHeader.put("moduleCode", "业务模块代码");
            fieldsHeader.put("taskName", "任务名称");
            fieldsHeader.put("className", "执行类名");
            fieldsHeader.put("method", "方法名");
            fieldsHeader.put("priority", "优先级");
            fieldsHeader.put("isConcurrent", "是否并发 ");
            fieldsHeader.put("taskState", "任务状态");
            fieldsHeader.put("lastExecuteTime", "上次执行时间");
            fieldsHeader.put("nextExcuteDate", "下次执行时间");
            fieldsHeader.put("operatorId", "创建人标识");
            fieldsHeader.put("createTime", "创建时间 ");
            ExcelExportDto<TaskPojo> dto = new ExcelExportDto<TaskPojo>(fields, fieldsHeader, list);
            response.setHeader("Content-Disposition",
                "attachment;filename=\"" + new String("任务列表.xls".getBytes(), "ISO8859-1") + "\"");
            // 导出
            ExcelUtil.exportExcel(response.getOutputStream(), dto);
            // response.reset();
        }
        catch (Exception e) {
            logger.info("导出任务失败", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param modelMap <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toImportTask", method = RequestMethod.GET)
    public String toImportTask(ModelMap modelMap) throws FrameworkException {
        return "task/importTask";
    }

    /**
     * Description: 导入Excel数据到系统中<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/importTask", method = RequestMethod.POST)
    public ModelAndView importMessageTemplate() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        taskManageService.importTaskData(mediaId, mediaName);
        return success("导入任务信息成功!");
    }

    /**
     * Description: 暂停<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/pauseTask", method = RequestMethod.GET)
    public ModelAndView pauseTask() throws Exception {
        try {
            String taskIdArr = getParameter("ids");
            String[] taskIds = taskIdArr.split(",");
            String[] taskStates = new String[2];
            taskStates[0] = TaskPojo.TASK_STATE_ACQUIRED;
            taskStates[1] = TaskPojo.TASK_STATE_WAITING;
            // 1. 校验是否全为可暂停状态的任务
            List<TaskPojo> playTaskList = taskManageService.queryTaskByState(taskIds, taskStates);

            if (taskIds.length != playTaskList.size()) {
                return fail("现阶段只允许等待状态W与执行状态A运行，请重新确认");
            }

            // 2.运行
            String state = TaskPojo.TASK_STATE_PAUSED;
            taskManageService.modifyTaskState(taskIds, state);
        }
        catch (Exception e) {
            logger.info("errorMessage:" + e.getMessage());
            return fail("暂停失败" + e.getMessage());
        }
        return success("暂停成功");
    }

    /**
     * Description: 运行<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return 暂停
     * @throws Exception <br>
     */
    @RequestMapping(value = "/playTask", method = RequestMethod.GET)
    public ModelAndView playTask() throws Exception {
        try {
            String taskIdArr = getParameter("ids");
            String[] taskIds = taskIdArr.split(",");
            String[] taskStates = new String[2];
            taskStates[0] = TaskPojo.TASK_STATE_INITIAL;
            taskStates[1] = TaskPojo.TASK_STATE_PAUSED;
            // 1. 校验是否全为可运行状态的任务
            List<TaskPojo> playTaskList = taskManageService.queryTaskByState(taskIds, taskStates);

            if (taskIds.length != playTaskList.size()) {
                return fail("现阶段只允许初始状态I与暂停状态P状态运行，请重新确认");
            }

            // 2.运行
            String state = TaskPojo.TASK_STATE_ACQUIRED;
            taskManageService.modifyTaskState(taskIds, state);
        }
        catch (Exception e) {
            logger.info("errorMessage:" + e.getMessage());
            return fail("运行失败" + e.getMessage());
        }
        return success("运行中.....");
    }
}
