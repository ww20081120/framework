package com.hbasesoft.framework.web.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.utils.excel.ExcelExportDto;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.service.MessageTemplateService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.db.core.utils.PagerList;
import com.hbasesoft.framework.message.core.bean.MessageTemplatePojo;

/**
 * <Description> <br>
 * 
 * @author liu.baiyang<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月12日 <br>
 * @since bps<br>
 * @see com.hbasesoft.framework.web.manager.controller.template <br>
 */
@Controller
@RequestMapping("/system/messageTemplate")
public class MessageTemplateController extends BaseController {

    private static final String PAGE_INDEX = "system/template/index";

    private static final String PAGE_ADD = "system/template/addMessageTemplate";

    private static final String PAGE_MOD = "system/template/modifyMessageTemplate";

    private static final String PAGE_IMPORT = "system/template/importMessageTemplate";

    /**
     * MessageTemplateService
     */
    @Resource
    private MessageTemplateService messageTemplateService;

    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(MessageTemplateController.class);

    /**
     * Description: 初始化页面<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toTemplate() {
        return PAGE_INDEX;
    }

    /**
     * Description:查询表单数据 <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    public Map<String, Object> toMessageTemplate() throws ServiceException {
        PagerList<MessageTemplatePojo> list = (PagerList<MessageTemplatePojo>) messageTemplateService
            .queryAllMessageTemplate(getPageIndex(), getPageSize());
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
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView getAddAdminPage() throws ServiceException {

        return new ModelAndView(PAGE_ADD, null);
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @RequestMapping(value = "/toMod", method = RequestMethod.GET)
    public ModelAndView getModAdminPage(HttpServletRequest request, HttpServletResponse response)
        throws ServiceException {

        Map<String, Object> map = new HashMap<String, Object>();
        String messageTemplateId = getParameter("messageTemplateId");
        MessageTemplatePojo messageTemplatePojo = messageTemplateService
            .queryMessageTemplateById(Integer.valueOf(messageTemplateId));
        map.put("Pojo", messageTemplatePojo);
        return new ModelAndView(PAGE_MOD, map);
    }

    /**
     * Description: 添加消息模板以及修改模版<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/add")
    public ModelAndView addMessageTemplate() throws Exception {

        // 页面获取
        String name = getParameter("name");
        String template = getParameter("template");
        String messageTemplateId = getParameter("messageTemplateId");
        String messageTemplateCode = getParameter("messageTemplateCode");

        String returnSuccess = "";

        try {
            List<MessageTemplatePojo> allMessageTemplateList = messageTemplateService.queryAllMt();
            final List<String> existList = assembleExistMt(allMessageTemplateList);

            if (existList.contains(messageTemplateCode)) {
                return fail("信息编码已经存在");
            }
            if (StringUtils.isEmpty(messageTemplateId)) { // 页面采集不到messageTemplateId表示是新增模版

                // 1.获取待添加节点的参数
                String state = "A";
                String contactChannelIds = "1";
                Integer delay = Integer.valueOf(0);
                Integer resendTimes = Integer.valueOf(0);
                String saveHistory = "Y";
                Integer saveDay = Integer.valueOf(1);

                // 2.封装参数
                MessageTemplatePojo pojo = new MessageTemplatePojo();
                pojo.setMessageTemplateCode(messageTemplateCode);
                pojo.setName(name);
                pojo.setTemplate(template);
                pojo.setState(state);
                pojo.setContactChannelIds(contactChannelIds);
                pojo.setDelay(delay);
                pojo.setResendTimes(resendTimes);
                pojo.setSaveHistory(saveHistory);
                pojo.setSaveDay(saveDay);

                // 3.进行添加操作
                messageTemplateService.addMessageTemplateService(pojo);
                returnSuccess = "添加信息模版成功";
            }
            else { // 获取的到页面上的messageTemplateId表示修改操作
                MessageTemplatePojo messageTemplatePojo = messageTemplateService
                    .queryMessageTemplateById(Integer.valueOf(messageTemplateId));
                messageTemplatePojo.setName(name);
                messageTemplatePojo.setTemplate(template);

                messageTemplateService.modifyMessageTemplate(messageTemplatePojo);
                returnSuccess = "修改信息模版成功";
            }
        }
        catch (Exception e) {
            return fail("执行失败:" + e.getMessage());
        }

        return success(returnSuccess);
    }

    /**
     * Description: 修改消息模板<br>
     * @author smile<br>
     * @return
     */
    @RequestMapping(value = "/modify")
    public ModelAndView modify() throws Exception {
        String messageTemplateCode = getParameter("messageTemplateCode");
        String messageTemplateId   = getParameter("messageTemplateId");
        String name                = getParameter("name");
        String template            = getParameter("template");

        Integer messageId          = Integer.parseInt(messageTemplateId);
        MessageTemplatePojo pojo   = messageTemplateService.queryMessageTemplateById(messageId);

        if(pojo == null){
            return fail("要修改的消息模板不存在。");
        }

        pojo.setTemplate(template);
        pojo.setMessageTemplateCode(messageTemplateCode);
        pojo.setName(name);

        messageTemplateService.modifyMessageTemplate(pojo);
        return success("修改消息模板成功。");
    }

    /**
     * Description: <br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param existList <br>
     * @return <br>
     */
    private List<String> assembleExistMt(List<MessageTemplatePojo> existList) {
        List<String> exist = new ArrayList<String>();
        for (MessageTemplatePojo pojo : existList) {
            exist.add(pojo.getMessageTemplateCode());
        }
        return exist;
    }

    /**
     * Description: 删除消息模板<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public Map<String,Object> deleteMessageTemplate() throws Exception {
        // 1、获取要要删除节点的ID
        String messageTemplateIdArr = getParameter("ids");
        String[] messageTemplateId = messageTemplateIdArr.split(",");
        try {
            if (messageTemplateId.length > 0) {
                for (int i = 0; i < messageTemplateId.length; i++) {
                    messageTemplateService.deleteMessageTemplate(Integer.valueOf(messageTemplateId[i]));
                }
            }
        }
        catch (Exception e) {
            logger.info("删除信息模版失败", e);
            return checkResult(false);
        }
        return checkResult(true);
    }

    /**
     * Description:导出消息模板数据<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     * @throws Exception <br>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportMessageTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {

        try {
            List<MessageTemplatePojo> list = messageTemplateService.queryAllMt();
            String[] fields = {
                "messageTemplateId", "messageTemplateCode", "name", "template", "state", "contactChannelIds",
                "stateTime", "delay", "resendTimes", "saveHistory", "saveDay", "createTime"
            };
            Map<String, String> fieldsHeader = new HashedMap();
            fieldsHeader.put("messageTemplateId", "消息模板标识");
            fieldsHeader.put("messageTemplateCode", "消息模板代码");
            fieldsHeader.put("name", "名称");
            fieldsHeader.put("template", "模板");
            fieldsHeader.put("state", "状态");
            fieldsHeader.put("contactChannelIds", "接触渠到");
            fieldsHeader.put("stateTime", "状态时间 ");
            fieldsHeader.put("delay", "延迟时间(秒)");
            fieldsHeader.put("resendTimes", "失败重发次数");
            fieldsHeader.put("saveHistory", "是否保留历史记录");
            fieldsHeader.put("saveDay", "保留天数");
            fieldsHeader.put("createTime", "创建时间 ");
            ExcelExportDto<MessageTemplatePojo> dto = new ExcelExportDto<MessageTemplatePojo>(fields, fieldsHeader,
                list);
            response.setHeader("Content-Disposition",
                "attachment;filename=\"" + new String("消息模版.xls".getBytes(), "ISO8859-1") + "\"");
            // 导出
            ExcelUtil.exportExcel(response.getOutputStream(), dto);
            // response.reset();
        }
        catch (Exception e) {
            logger.info("导出信息模版失败", e);
            // return fail("导出信息模版失败" + e);
        }
        // return success("导出信息模版成功");
    }

    /**
     * Description: 弹出导入页面<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @param modelMap <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toImport", method = RequestMethod.GET)
    public String toImportMessageTemplate(ModelMap modelMap) throws FrameworkException {
        return PAGE_IMPORT;
    }

    /**
     * Description: 导入操作<br>
     * 
     * @author liu.baiyang<br>
     * @taskId <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ModelAndView importMessageTemplate() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        messageTemplateService.importMessageTemplateData(mediaId, mediaName);
        return success("导入信息模版成功!");
    }
}
