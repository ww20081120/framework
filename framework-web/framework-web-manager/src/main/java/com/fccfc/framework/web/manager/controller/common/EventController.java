package com.fccfc.framework.web.manager.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.config.core.ConfigHelper;
import com.fccfc.framework.config.core.service.DictionaryDataService;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.bean.common.EventPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.common.EventService;

@Controller
@RequestMapping(value = "/common/event")
public class EventController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    /** 事件新增页 */
    private static final String PAGE_ADD = "event/add";

    /** 事件修改页 */
    private static final String PAGE_MODIFY = "event/modify";

    /** 事件导入页 */
    private static final String PAGE_TOIMPORT = "event/import";

    /** 事件导出页 */
    private static final String PAGE_TOEXPORT = "event/export";

    @Resource
    private EventService eventService;

    @Resource
    private DictionaryDataService dictionaryDataService;

    /** 进入事件主页 */
    @RequestMapping()
    public ModelAndView index() throws FrameworkException {
        return new ModelAndView("event/event");
    }

    /** 事件列表查询 */
    @ResponseBody
    @RequestMapping(value = "/list")
    public Map<String, Object> query() throws FrameworkException {
        // String serchStr = getParameter("Str");

        PagerList<EventPojo> eventList = (PagerList<EventPojo>) eventService.selectList(getPageIndex(), getPageSize());
        logger.info("查询成功");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", eventList);
        result.put("pageIndex", eventList.getPageIndex());
        result.put("pageSize", eventList.getPageSize());
        result.put("totalCount", eventList.getTotalCount());
        result.put("totalPage", eventList.getTotalPage());
        return result;
    }

    /** 进入事件新增页 */
    @RequestMapping(value = "/toAdd")
    public String getAddEventPage(ModelMap map) throws FrameworkException {
        map.put("eventTypeGroup", JSON.toJSONString(ConfigHelper.getDict("EVENT_TYPE")));
        return PAGE_ADD;
    }

    /** 事件新增 */
    @RequestMapping(value = "/add")
    public ModelAndView addAdmin(HttpServletRequest request, EventPojo event) throws FrameworkException {
        eventService.saveEvent(event);
        return success("添加资源信息成功");
    }

    /** 进入事件修改页 */
    @RequestMapping(value = "/toModify")
    public ModelAndView toModify(ModelMap map) throws FrameworkException {
        String eventId = getParameter("eventId");
        EventPojo event = eventService.getEvent(Integer.parseInt(eventId));
        map.put("event", event);
        map.put("eventTypeGroup", JSON.toJSONString(ConfigHelper.getDict("EVENT_TYPE")));
        return new ModelAndView(PAGE_MODIFY, map);
    }

    /** 事件修改 */
    @RequestMapping(value = "/modify")
    public ModelAndView changeAdminInfo(EventPojo event) throws FrameworkException {
        eventService.modify(event);
        return success("修改管理员成功");
    }

    /** 事件名称校验 */
    @ResponseBody
    @RequestMapping(value = "checkUserName")
    public boolean checkUserName() throws FrameworkException {
        return eventService.checkUserName(getParameter("eventId"), getParameter("eventName"));
    }

    /** 事件删除 */
    @RequestMapping(value = "delete")
    public void delete() throws FrameworkException {
        String ids = getParameter("ids", "删除的管理员不能为空");
        eventService.delete(CommonUtil.splitId(ids));
    }

    /** 进入事件导入页 */
    @RequestMapping(value = "/toImport")
    public ModelAndView toImport() throws FrameworkException {
        return new ModelAndView(PAGE_TOIMPORT);
    }

    /** 事件导入 */
    @RequestMapping(value = "/import")
    public ModelAndView importEvent(HttpServletRequest request) throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        eventService.importEvent(mediaId, mediaName);
        return success("事件导入");
    }

    /** 进入事件导出页 */
    @RequestMapping(value = "/toExport")
    public String toExport() throws FrameworkException {
        // adminService.modifyAdmin(adminPojo);
        return PAGE_TOEXPORT;
    }

    /** 事件导出 */
    @RequestMapping(value = "/export")
    public ModelAndView export(HttpServletRequest request, HttpServletResponse response) throws FrameworkException {
        eventService.exportEvent(response, getPageIndex(), getPageSize());
        return success("事件导出");
    }

}
