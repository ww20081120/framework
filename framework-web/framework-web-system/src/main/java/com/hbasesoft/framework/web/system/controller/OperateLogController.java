package com.hbasesoft.framework.web.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.system.bean.EventPojo;
import com.hbasesoft.framework.web.system.bean.OperateLogPojo;
import com.hbasesoft.framework.web.system.service.OperateLogService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.db.core.utils.PagerList;

/**
 * <Description> <br>
 * 
 * @author xu.jun<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月13日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.bps.web.control.operator <br>
 */
@Controller
@RequestMapping("/system/operatelog")
public class OperateLogController extends BaseController {

    private static final String PAGE_INDEX = "system/log/index";

    private static final String PAGE_ADD = "permission/role/add";

    private static final String PAGE_MODIFY = "permission/role/modify";

    /** OperateLogService */
    @Resource
    private OperateLogService operateLogService;

    @RequestMapping(method = RequestMethod.GET)
    public String toRole() {
        return PAGE_INDEX;
    }

    /***
     * Description: 查询所有<br>
     * 
     * @author <br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Map<String, Object> query() throws ServiceException {
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<OperateLogPojo> logList = (PagerList<OperateLogPojo>) operateLogService.getAll(getPageIndex(),
            getPageSize());
        result.put("data", logList);
        result.put("pageIndex", logList.getPageIndex());
        result.put("pageSize", logList.getPageSize());
        result.put("totalCount", logList.getTotalCount());
        result.put("totalPage", logList.getTotalPage());
        return result;
    }

    /** 导出 */
    @RequestMapping(value = "/export")
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws FrameworkException {
        operateLogService.export(response, getPageIndex(), getPageSize());
        return success("导出成功");
    }

    /*    *//***
             * Description: <br>
             * 
             * @author xu.jun<br>
             * @taskId <br>
             * @return ModelAndView <br>
             */
    /*
     * @RequestMapping(method = RequestMethod.GET) public ModelAndView serachOperateLog(HttpServletRequest request)
     * throws NumberFormatException, ServiceException { // 测试用的 int operatorId = 1; String searchcode =
     * getParameter("eventid"); if (searchcode == null) { searchcode = "0"; } Map<String, Object> map = new
     * HashMap<String, Object>(); map.put("operateloglist", operateLogService.queryOperateLog(operatorId,
     * getParameter("starttime"), getParameter("endtime"), Integer.parseInt(searchcode), getPageIndex(),
     * getPageSize())); return new ModelAndView("system/log/index", map); }
     */

    /*    *//***
             * Description: 导出excel<br>
             * 
             * @author xu.jun<br>
             * @taskId <br>
             */
    /*
     * @ResponseBody
     * @RequestMapping(value = "/export", method = RequestMethod.GET) public void export(HttpServletRequest request,
     * HttpServletResponse response) throws NumberFormatException, ServiceException, Exception { // 测试用的 int operatorId
     * = 1; String searchcode = getParameter("eventid"); if (searchcode == null || "".equals(searchcode) ||
     * "null".equals(searchcode)) { searchcode = "0"; } String starttime = ("null".equals(getParameter("starttime"))) ?
     * null : getParameter("starttime"); String endtime = ("null".equals(getParameter("endtime"))) ? null :
     * getParameter("endtime"); List<OperateLogPojo> list = operateLogService.queryOperateLog(operatorId, starttime,
     * endtime, Integer.parseInt(searchcode)); String[] fields = { "operateLogId", "userName", "createTime",
     * "eventName", "ip" }; Map<String, String> fieldsHeader = new HashMap<String, String>();
     * fieldsHeader.put("operateLogId", "操作日志标识"); fieldsHeader.put("userName", "操作员名称"); fieldsHeader.put("createTime",
     * "创建时间"); fieldsHeader.put("eventName", "事件名称"); fieldsHeader.put("ip", "IP地址"); ExcelExportDto<OperateLogPojo>
     * dto = new ExcelExportDto<OperateLogPojo>(fields, fieldsHeader, list); response.setHeader("Content-Disposition",
     * "attachment;filename=\"" + new String("操作日志.xls".getBytes(), "ISO8859-1") + "\""); // 导出
     * ExcelUtil.exportExcel(response.getOutputStream(), dto); }
     */

    /***
     * Description: 查询所有事件集合<br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/qryEventList", method = RequestMethod.POST)
    public JSONObject qryEventList(HttpServletRequest request) throws ServiceException, Exception {
        JSONObject obj = new JSONObject();
        List<EventPojo> eventList = operateLogService.qryEventList();
        obj.put("eventList", eventList);
        return obj;
    }

}
