package com.hbasesoft.framework.web.system.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.utils.excel.ExcelExportDto;
import com.hbasesoft.framework.web.core.utils.excel.ExcelUtil;
import com.hbasesoft.framework.web.system.bean.AttrPojo;
import com.hbasesoft.framework.web.system.bean.AttrValuePojo;
import com.hbasesoft.framework.web.system.service.AttrService;
import com.hbasesoft.framework.web.system.service.DictDetailsService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.db.core.utils.PagerList;

@Controller
@RequestMapping("/common/attr")
public class AttrController extends BaseController {

    private static final String PAGE_INDEX = "common/attr/index";

    private static final String PAGE_ADD = "common/attr/add";

    private static final String PAGE_MODIFY = "common/attr/modify";

    private static final String PAGE_IMPORT = "common/attr/import";
    
    private static final String PAGE_VALUE_ADD = "common/attr/value/add";
    
    private static final String PAGE_VALUE_MODIFY = "common/attr/value/modify";
    
    private static final String PAGE_VALUE_IMPORT = "common/attr/value/import";

    private static final String[] ATTR_FIELDS = new String[] {
        "attrId", "attrName", "attrType", "parentAttrId", "attrCode", "visible", "instantiatable", "defaultValue",
        "dataType", "inputType", "valueScript"
    };

    private static Logger logger = new Logger(AttrController.class);

    private static final Map<String, String> ATTR_FIELDS_HEADER = new HashMap<String, String>();

    private static final String[] ATTR_VALUE_FIELDS = new String[] {
        "attrId", "attrValueId", "valueMark", "value", "linkAttrId"
    };

    private static final Map<String, String> ATTR_VALUE_FIELDS_HEADER = new HashMap<String, String>();

    static {
        ATTR_VALUE_FIELDS_HEADER.put("attrId", "属性标识");
        ATTR_VALUE_FIELDS_HEADER.put("attrValueId", "属性值标识");
        ATTR_VALUE_FIELDS_HEADER.put("valueMark", "取值说明");
        ATTR_VALUE_FIELDS_HEADER.put("value", "取值");
        ATTR_VALUE_FIELDS_HEADER.put("linkAttrId", "联动属性标识");

        ATTR_FIELDS_HEADER.put("attrId", "属性标识");
        ATTR_FIELDS_HEADER.put("attrName", "属性名称");
        ATTR_FIELDS_HEADER.put("attrType", "属性类型");
        ATTR_FIELDS_HEADER.put("parentAttrId", "父属性标识");
        ATTR_FIELDS_HEADER.put("attrCode", "属性代码");
        ATTR_FIELDS_HEADER.put("visible", "是否可见");
        ATTR_FIELDS_HEADER.put("instantiatable", "是否可实例化");
        ATTR_FIELDS_HEADER.put("defaultValue", "缺省值");
        ATTR_FIELDS_HEADER.put("dataType", "输入方式");
        ATTR_FIELDS_HEADER.put("inputType", "数据类型");
        ATTR_FIELDS_HEADER.put("valueScript", "取值校验规则");
    }

    @Resource
    private AttrService attrService;

    @Resource
    private DictDetailsService dictService;

    /**
     * Description: 跳转页面<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toAttr() {
        return PAGE_INDEX;
    }

    /**
     * Description: 查询属性信息 <br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return 返回JSON数据
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> queryAttrPager() throws FrameworkException {
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<AttrPojo> dictList = (PagerList<AttrPojo>) attrService.queryAttrPager(getPageIndex(), getPageSize());
        result.put("data", dictList);
        result.put("pageIndex", dictList.getPageIndex());
        result.put("pageSize", dictList.getPageSize());
        result.put("totalCount", dictList.getTotalCount());
        result.put("totalPage", dictList.getTotalPage());
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/queryAttrValuePager", method = RequestMethod.GET)
    public Map<String, Object> queryAttrValuePager() throws FrameworkException {
    	String attrId=getParameter("attrId");
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<AttrValuePojo> dictList = (PagerList<AttrValuePojo>) attrService.queryAttrValuePager(Integer.valueOf(attrId),
            getPageIndex(), getPageSize());
        result.put("data", dictList);
        result.put("pageIndex", dictList.getPageIndex());
        result.put("pageSize", dictList.getPageSize());
        result.put("totalCount", dictList.getTotalCount());
        result.put("totalPage", dictList.getTotalPage());
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelAndView
     * @return
     * @throws ServiceException <br>
     */
    @RequestMapping("/toAdd")
    public String toAddAttr() throws FrameworkException {
        return PAGE_ADD;
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
     @ResponseBody
     @RequestMapping(value = "/queryAllAttr", method = RequestMethod.GET)
     public String queryAllAttr() throws FrameworkException {
     // 指定返回的json字段
     SimplePropertyPreFilter filter = new SimplePropertyPreFilter("attrId", "attrName");
    
     // 将数据集合转换成json并返回给调用者
     return JSON.toJSONString(attrService.queryAttr(), filter, SerializerFeature.WriteMapNullValue);
     }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/queryInputType", method = RequestMethod.GET)
    public String queryInputType() throws FrameworkException {
        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("dictDataName", "dictDataValue");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(dictService.queryDictData("INPUT_TYPE"), filter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/queryDataType", method = RequestMethod.GET)
    public String queryDataType() throws FrameworkException {
        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("dictDataName", "dictDataValue");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(dictService.queryDictData("DATA_TYPE"), filter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Description: 添加属性信息<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addAttr(@ModelAttribute("attrPojo") AttrPojo attrPojo) throws FrameworkException {
        attrService.addAttr(attrPojo);
        return success("新增属性成功!");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toModify")
    public String toModifyAttr(ModelMap modelMap) throws FrameworkException {
        Integer attrId = getIntegerParameter("attrId", "要修改的属性标识不能为空");
        modelMap.addAttribute("attrPojo", attrService.queryAttr(attrId));
        return PAGE_MODIFY;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/checkHasChild", method = RequestMethod.POST)
    public boolean checkHasChild() throws FrameworkException {
        boolean hasChild = false;
        String attrIds = getParameter("attrIds", "属性标识不能为空");
        Integer[] strs = CommonUtil.splitId(attrIds, ",");
        for (Integer attrId : strs) {
            List<AttrPojo> list = attrService.queryChildAttr(attrId);
            if (CommonUtil.isNotEmpty(list)) {
                hasChild = true;
                return hasChild;
            }
        }
        return hasChild;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/checkHasValue", method = RequestMethod.POST)
    public boolean checkHasValue() throws FrameworkException {
        boolean hasChild = false;
        String attrIds = getParameter("attrIds", "属性标识不能为空");
        Integer[] strs = CommonUtil.splitId(attrIds, ",");
        for (Integer attrId : strs) {
            List<AttrValuePojo> list = attrService.queryAttrValue(attrId);
            if (CommonUtil.isNotEmpty(list)) {
                hasChild = true;
                return hasChild;
            }
        }
        return hasChild;
    }

    /**
     * Description: 修改属性数据<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modifyAttr(@ModelAttribute("attrPojo") AttrPojo attrPojo) throws FrameworkException {
        attrService.modifyAttr(attrPojo);
        return success("修改属性成功");
    }

    /**
     * Description: 删除属性<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAttr() throws FrameworkException {
        String attrIds = getParameter("attrIds", "删除的属性标识不能为空");
        Integer[] strs = CommonUtil.splitId(attrIds, ",");
        // 执行批量删除
        attrService.deleteAttr(strs);
        return new ResponseEntity<Object>(GlobalConstants.BLANK,HttpStatus.OK);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/toImport", method = RequestMethod.GET)
    public String toImportAttr(ModelMap modelMap) throws FrameworkException {
        return PAGE_IMPORT ;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ModelAndView importAttr() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        attrService.importAttr(mediaId, mediaName);
        return success("导入属性成功!");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void exportAttr(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("属性.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<AttrPojo> list = attrService.queryAttr();
            ExcelExportDto<AttrPojo> dto = new ExcelExportDto<AttrPojo>(ATTR_FIELDS, ATTR_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export attr.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export attr.", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelAndView
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping("value/toAdd")
    public ModelAndView toAddAttrValue(ModelAndView modelAndView) throws FrameworkException {
        modelAndView.addObject("attrId", getParameter("attrId"));
        modelAndView.setViewName(PAGE_VALUE_ADD );
        return modelAndView;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValuePojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "value/add", method = RequestMethod.POST)
    public ModelAndView addAttrValue(@ModelAttribute("attrValuePojo") AttrValuePojo attrValuePojo)
        throws FrameworkException {
        attrService.addAttrValue(attrValuePojo);
        return success("新增属性值成功!");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "value/toImport", method = RequestMethod.GET)
    public String toImportAttrValue(ModelMap modelMap) throws FrameworkException {
        return PAGE_VALUE_IMPORT ;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "value/toModify")
    public String toModifyAttrValue(ModelMap modelMap) throws FrameworkException {
        Integer attrValueId = getIntegerParameter("attrValueId", "要修改的属性值标识不能为空");
        modelMap.addAttribute("attrValuePojo", attrService.getAttrValue(attrValueId));
        return PAGE_VALUE_MODIFY ;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param attrValuePojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "value/modify", method = RequestMethod.POST)
    public ModelAndView modifyAttr(@ModelAttribute("attrValuePojo") AttrValuePojo attrValuePojo)
        throws FrameworkException {
        attrService.modifyAttrValue(attrValuePojo);
        return success("修改属性值成功");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @ResponseBody
    @RequestMapping(value = "value/remove", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAttrValue() throws FrameworkException {
        String attrValueIds = getParameter("attrValueIds", "删除的属性值标识不能为空");
        Integer[] strs = CommonUtil.splitId(attrValueIds, ",");
        // 执行批量删除
        attrService.deleteAttrValue(strs);
        return new ResponseEntity<Object>(GlobalConstants.BLANK,HttpStatus.OK);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "value/import", method = RequestMethod.POST)
    public ModelAndView importAttrValue() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        attrService.importAttrValue(mediaId, mediaName);
        return success("导入属性值成功!");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "value/export", method = RequestMethod.GET)
    public void exportAttrValue(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("属性值.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<AttrValuePojo> list = attrService.queryAttrValue();
            ExcelExportDto<AttrValuePojo> dto = new ExcelExportDto<AttrValuePojo>(ATTR_VALUE_FIELDS,
                ATTR_VALUE_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export attr value.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export attr value.", e);
        }
    }
}
