package com.fccfc.framework.web.manager.controller.system;

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
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.web.core.controller.BaseController;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.bean.system.OperateLogPojo;
import com.fccfc.framework.web.manager.service.system.OperateLogService;

/**
 * <Description> <br>
 * 
 * @author xu.jun<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月13日 <br>
 * @since V1.0<br>
 * @see com.fccfc.bps.web.control.operator <br>
 */
@Controller
@RequestMapping("/operatelog")
public class OperateLogController extends BaseController {

    /** OperateLogService */
    @Resource
    private OperateLogService operateLogService;

    /***
     * Description: <br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     * @return ModelAndView <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView serachOperateLog(HttpServletRequest request) throws NumberFormatException, ServiceException {
        // 测试用的
        int operatorId = 1;

        String searchcode = getParameter("eventid");
        if (searchcode == null) {
            searchcode = "0";
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("operateloglist", operateLogService.queryOperateLog(operatorId, getParameter("starttime"),
            getParameter("endtime"), Integer.parseInt(searchcode), getPageIndex(), getPageSize()));

        return new ModelAndView("operate/operatelog", map);
    }

    /***
     * Description: 导出excel<br>
     * 
     * @author xu.jun<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletRequest request, HttpServletResponse response)
        throws NumberFormatException, ServiceException, Exception {
        // 测试用的
        int operatorId = 1;

        String searchcode = getParameter("eventid");
        if (searchcode == null || "".equals(searchcode) || "null".equals(searchcode)) {
            searchcode = "0";
        }

        String starttime = ("null".equals(getParameter("starttime"))) ? null : getParameter("starttime");
        String endtime = ("null".equals(getParameter("endtime"))) ? null : getParameter("endtime");

        List<OperateLogPojo> list = operateLogService.queryOperateLog(operatorId, starttime, endtime,
            Integer.parseInt(searchcode));
        String[] fields = {
            "operateLogId", "userName", "createTime", "eventName", "ip"
        };
        Map<String, String> fieldsHeader = new HashMap<String, String>();
        fieldsHeader.put("operateLogId", "操作日志标识");
        fieldsHeader.put("userName", "操作员名称");
        fieldsHeader.put("createTime", "创建时间");
        fieldsHeader.put("eventName", "事件名称");
        fieldsHeader.put("ip", "IP地址");
        ExcelExportDto<OperateLogPojo> dto = new ExcelExportDto<OperateLogPojo>(fields, fieldsHeader, list);
        response.setHeader("Content-Disposition",
            "attachment;filename=\"" + new String("操作日志.xls".getBytes(), "ISO8859-1") + "\"");
        // 导出
        ExcelUtil.exportExcel(response.getOutputStream(), dto);
    }

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
