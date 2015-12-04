package com.fccfc.framework.web.manager.controller.system;

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
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.bean.system.AnnouncementPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.system.AnnouncementService;

/**
 * <Description> 公告管理<br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月26日 <br>
 * @since bps<br>
 * @see com.fccfc.framework.web.manager.controller.announcement <br>
 */
@Controller
@RequestMapping("/announcement")
public class AnnouncementController extends AbstractController {

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);

    /**
     * announcementService
     */
    @Resource
    private AnnouncementService announcementService;

    /**
     * Description:跳转公告主页面 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toAnnouncement() {
        return "announcement/index";
    }

    /**
     * Description: 展示数据<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    public Map<String, Object> queryAllAnnouncement() throws ServiceException {
        PagerList<AnnouncementPojo> list = (PagerList<AnnouncementPojo>) announcementService.listAnnouncement(
            getPageIndex(), getPageSize());

        for (AnnouncementPojo pojo : list) {
            if (AnnouncementPojo.ANNOUNCEMENT_STATE_INITIAL.equals(pojo.getState())) {
                pojo.setState("待审核");
            }
            else if (AnnouncementPojo.ANNOUNCEMENT_STATE_SUCCESS.equals(pojo.getState())) {
                pojo.setState("审核通过");
            }
            else if (AnnouncementPojo.ANNOUNCEMENT_STATE_FAILURE.equals(pojo.getState())) {
                pojo.setState("审核未通过");
            }
        }
        logger.info("查询成功");
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", list);
        result.put("pageIndex", list.getPageIndex());
        result.put("pageSize", list.getPageSize());
        result.put("totalCount", list.getTotalCount());
        result.put("totalPage", list.getTotalPage());
        return result;
    }

    /**
     * Description:新增修改页面 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @RequestMapping(value = "/toAnnouncementManager", method = RequestMethod.GET)
    public ModelAndView getAnnouncementManager() throws ServiceException {

        Map<String, Object> map = new HashMap<String, Object>();
        String announcementId = getParameter("announcementId");
        if (CommonUtil.isNotEmpty(announcementId)) { // announcement管理页面为修改功能
            AnnouncementPojo pojo = new AnnouncementPojo();
            pojo.setAnnouncementId(Integer.valueOf(announcementId));
            List<AnnouncementPojo> announcementist = announcementService.qryAnnouncement(pojo);
            map.put("pojo", announcementist.get(0));

        }

        return new ModelAndView("announcement/announcementManager", map);
    }

    /**
     * Description: 新增修改操作<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/announcementManager", method = RequestMethod.POST)
    public ModelAndView announcementManager() throws Exception {

        String returnSuccess = "";
        try {
            // 1.获取页面参数
            String title = getParameter("title");
            String content = getParameter("content");
            String announcementId = getParameter("announcementId");

            // 2.必要的校验

            // 3.announcementId有为修改 无为新增
            if (CommonUtil.isEmpty(announcementId)) { // 新增
                AnnouncementPojo announcementPojoA = new AnnouncementPojo();
                announcementPojoA.setState(AnnouncementPojo.ANNOUNCEMENT_STATE_INITIAL);
                announcementPojoA.setOperatorId(Integer.valueOf(10086));
                announcementPojoA.setTitle(title);
                announcementPojoA.setContent(content);
                announcementService.add(announcementPojoA);

                returnSuccess = "新增成功";
            }
            else { // 修改
                AnnouncementPojo pojo = new AnnouncementPojo();
                pojo.setAnnouncementId(Integer.valueOf(announcementId));
                List<AnnouncementPojo> announcementist = announcementService.qryAnnouncement(pojo);
                if (AnnouncementPojo.ANNOUNCEMENT_STATE_SUCCESS.equals(announcementist.get(0).getState())) {
                    // 已经审核好的公告需要需要重新审核 状态置为待审核
                    announcementist.get(0).setState(AnnouncementPojo.ANNOUNCEMENT_STATE_INITIAL);
                }
                announcementist.get(0).setOperatorId(Integer.valueOf(10086));
                announcementist.get(0).setTitle(title);
                announcementist.get(0).setContent(content);

                announcementService.modify(announcementist.get(0));
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
     * Description:删除功能 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/deleteAnnouncement", method = RequestMethod.POST)
    public String deleteAnnouncement() throws Exception {

        try {
            String announcementIdArr = getParameter("ids");
            String[] ids = announcementIdArr.split(",");
            announcementService.delete(ids);
        }
        catch (Exception e) {
            logger.info("errorMessage:" + e.getMessage());
            return "failure";
        }
        return "success";
    }

    /**
     * Description:导出 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @throws Exception <br>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/exportAnnouncement", method = RequestMethod.GET)
    public void exportTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            List<AnnouncementPojo> list = announcementService.qryAnnouncement(null);

            String[] fields = {
                "announcementId", "title", "content", "createTime", "state", "stateDate", "operatorId", "comments"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("announcementId", "公告标识");
            fieldsHeader.put("title", "公告标题");
            fieldsHeader.put("content", "公告内容");
            fieldsHeader.put("createTime", "创建时间");
            fieldsHeader.put("state", "状态");
            fieldsHeader.put("stateDate", "状态时间");
            fieldsHeader.put("operatorId", "操作人 ");
            fieldsHeader.put("comments", "备注");
            ExcelExportDto<AnnouncementPojo> dto = new ExcelExportDto<AnnouncementPojo>(fields, fieldsHeader, list);
            response.setHeader("Content-Disposition", "attachment;filename=\""
                + new String("公告列表.xls".getBytes(), "ISO8859-1") + "\"");
            // 导出
            ExcelUtil.exportExcel(response.getOutputStream(), dto);
        }
        catch (Exception e) {
            logger.info("导出公告失败", e);
        }
    }

    /**
     * Description: 导入引导页面<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param modelMap <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toImportAnnouncement", method = RequestMethod.GET)
    public String toImportAnnouncement(ModelMap modelMap) throws FrameworkException {
        return "announcement/importAnnouncement";
    }

    /**
     * Description: 导入Excel数据到系统中<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/importAnnouncement", method = RequestMethod.POST)
    public ModelAndView importAnnouncement() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        announcementService.importAnnouncementData(mediaId, mediaName);
        return success("导入公告成功!");
    }

    /**
     * Description: 审核管理页面 支持批量审核<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param modelMap <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toAnnouncementAudit", method = RequestMethod.GET)
    public ModelAndView toAnnouncementAudit(ModelMap modelMap) throws FrameworkException {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ids", getParameter("ids"));
        return new ModelAndView("announcement/announcementAudit", map);
    }

    /**
     * Description:审核操作 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @RequestMapping(value = "/auditAnnouncement", method = RequestMethod.POST)
    public ModelAndView auditAnnouncement() throws ServiceException {

        String isAudit = getParameter("isAudit");
        String announcementIdArr = getParameter("ids");
        try {
            String[] ids = announcementIdArr.split(",");
            // 校验
            List<AnnouncementPojo> list = announcementService.auditCheck(ids,
                AnnouncementPojo.ANNOUNCEMENT_STATE_SUCCESS);

            if (CommonUtil.isNotEmpty(list)) {
                return fail("请选择待审核以及审核未通过的记录！");
            }
            AnnouncementPojo pojo = new AnnouncementPojo();

            if ("Y".equals(isAudit)) {
                pojo.setState(AnnouncementPojo.ANNOUNCEMENT_STATE_SUCCESS);
            }
            else if ("N".equals(isAudit)) {
                pojo.setState(AnnouncementPojo.ANNOUNCEMENT_STATE_FAILURE);
            }

            String comments = getParameter("comments");

            if (CommonUtil.isNotEmpty(comments)) {
                pojo.setComments(comments);
            }
            announcementService.audit(ids, pojo);
        }
        catch (NumberFormatException e) {

            return fail("审核失败");
        }
        return success("已审核，请查看");
    }
}
