package com.hbasesoft.framework.web.system.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.system.bean.AreaPojo;
import com.hbasesoft.framework.web.system.service.AreaService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;

/**
 * <Description> <br>
 * 
 * @author shao.dinghui<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月19日 <br>
 * @since V7.3<br>
 * @see com.hbasesoft.framework.web.manager.controller.area <br>
 */
@Controller
@RequestMapping("/common/area")
public class AreaController extends BaseController {

    private static final String PAGE_INDEX = "common/area/index";

    private static final String PAGE_ADD = "common/area/add";

    private static final String PAGE_MODIFY = "common/area/modify";

    private static final String PAGE_IMPORT = "common/area/import";

    /** areaDetailsService */
    @Resource
    private AreaService areaService;

    /**
     * Description: 跳转页面<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toAreaDeitails() {
        return PAGE_INDEX;
    }

    /**
     * Description: 查询所有区域<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return 返回JSON数据
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list() throws Exception {

        // 查询所有area
        List<AreaPojo> allAreaList = areaService.listArea();

        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("areaId", "parentAreaId", "areaName");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(allAreaList, filter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Description: <br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return 返回JSON数据
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/getAreaById", method = RequestMethod.GET)
    public String getAreaById() throws Exception {

        Long id = NumberUtils.toLong(getParameter("areaId"));
        AreaPojo area = areaService.qryAreaDetailById(id.intValue());

        List<AreaPojo> areaList = new ArrayList<AreaPojo>();

        areaList.add(area);

        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("areaId", "parentAreaId", "areaName", "areaType",
            "areaCode", "remark");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(areaList, filter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param modelAndView
     * @return <br>
     */
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView toAdd(ModelAndView modelAndView) {
        modelAndView.addObject("parentAreaId", getParameter("parentAreaId"));
        modelAndView.addObject("parentAreaName", getParameter("parentAreaName"));

        modelAndView.setViewName(PAGE_ADD);
        return modelAndView;
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(AreaPojo areaPojo) throws FrameworkException {
        areaService.insertArea(areaPojo);
        return success("新增区域信息成功。");
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @return <br>
     */
    @ResponseBody
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public boolean checkCode() {
        return areaService.checkCode(getParameter("areaId"), getParameter("areaCode"));
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @return <br>
     */
    @ResponseBody
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public boolean checkName() {
        return areaService.checkName(getParameter("areaId"), getParameter("areaName"));
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toModify", method = RequestMethod.GET)
    public String toModify(ModelMap modelMap) throws FrameworkException {
        int areaId = getIntegerParameter("areaId");
        modelMap.addAttribute("parentAreaName", getParameter("parentAreaName"));
        modelMap.addAttribute("areaPojo", areaService.qryAreaDetailById(areaId));
        return PAGE_MODIFY;
    }

    /**
     * Description: <br>
     * 
     * @author XXX<br>
     * @taskId <br>
     * @param areaPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(AreaPojo areaPojo) throws FrameworkException {
        areaService.modifyArea(areaPojo);
        return success("修改区域信息成功!");
    }

    /**
     * Description: 删除区域<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @return 返回JSON数据
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> removeArea() throws Exception {

        int id = getIntegerParameter("id", "删除的区域标识不能为空");

        areaService.remove(id);

        return new ResponseEntity<Object>(GlobalConstants.BLANK, HttpStatus.OK);
    }

    /**
     * Description: 导出区域<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportArea(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition",
            "attachment;filename=\"" + new String("区域.xls".getBytes(), "ISO8859-1") + "\"");

        // 进行数据导出操作
        areaService.exportAreaData(response);
    }

    /** 导入页 */
    @RequestMapping(value = "/toImport")
    public String toImport() throws FrameworkException {
        return PAGE_IMPORT;
    }

    /**
     * Description: 导入区域<br>
     * 
     * @author shao.dinghui<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ModelAndView importArea() throws Exception {

        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        areaService.importAreaData(mediaId, mediaName);

        // 进行数据导出操作

        return new ModelAndView("导入成功");
    }
}
