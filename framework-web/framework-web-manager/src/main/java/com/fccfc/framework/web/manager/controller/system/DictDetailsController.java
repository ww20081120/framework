package com.fccfc.framework.web.manager.controller.system;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.bean.system.DictionaryDataPojo;
import com.fccfc.framework.web.manager.bean.system.DictionaryPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.system.DictDetailsService;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月27日 <br>
 * @since V7.3<br>
 * @see com.fccfc.framework.web.manager.controller.dict <br>
 */
@Controller
@RequestMapping("/dict")
public class DictDetailsController extends AbstractController {

    private static final Logger logger = new Logger(DictDetailsController.class);

    private static final String[] DICT_FIELDS = new String[] {
        "dictCode", "dictName", "remark"
    };

    private static final Map<String, String> DICT_FIELDS_HEADER = new HashMap<String, String>();

    static {
        DICT_FIELDS_HEADER.put("dictCode", "字典代码");
        DICT_FIELDS_HEADER.put("dictName", "字典名称");
        DICT_FIELDS_HEADER.put("remark", "备注");
    }

    private static final String[] DICT_DATA_FIELDS = new String[] {
        "dictDataId", "dictCode", "dictDataName", "dictDataValue", "isFixed", "isCancel"
    };

    private static final Map<String, String> DICT_DATA_FIELDS_HEADER = new HashMap<String, String>();

    static {
        DICT_DATA_FIELDS_HEADER.put("dictDataId", "字典数据标识");
        DICT_DATA_FIELDS_HEADER.put("dictCode", "字典代码");
        DICT_DATA_FIELDS_HEADER.put("dictDataName", "字典数据名称");
        DICT_DATA_FIELDS_HEADER.put("dictDataValue", "字典数据值");
        DICT_DATA_FIELDS_HEADER.put("isFixed", "是否固定");
        DICT_DATA_FIELDS_HEADER.put("isCancel", "是否可以删除");
    }

    /** dictService */
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
    public String toDict() {
        return "dictionary/dictionary";
    }

    /**
     * Description: 查询字典的所有信息 <br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @return 返回JSON数据
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/queryDictPager", method = RequestMethod.GET)
    public Map<String, Object> queryDictPager() throws FrameworkException {
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<DictionaryPojo> dictList = (PagerList<DictionaryPojo>) dictService.queryDictPager(getPageIndex(),
            getPageSize());
        result.put("data", dictList);
        result.put("pageIndex", dictList.getPageIndex());
        result.put("pageSize", dictList.getPageSize());
        result.put("totalCount", dictList.getTotalCount());
        result.put("totalPage", dictList.getTotalPage());
        return result;
    }

    /**
     * Description: <br>
     * 查询字典数据
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/queryDictDataPager", method = RequestMethod.GET)
    public Map<String, Object> queryDictDataPager() throws FrameworkException {
        Map<String, Object> result = new HashMap<String, Object>();
        String dictCode = getParameter("dictCode");
        PagerList<DictionaryDataPojo> dictList = (PagerList<DictionaryDataPojo>) dictService
            .queryDictDataPager(dictCode, getPageIndex(), getPageSize());
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
     * @throws FrameworkException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/checkDictCode")
    public boolean checkDictCode() throws FrameworkException {
        boolean result = false;
        String oldDictCode = getParameter("oldDictCode");
        String dictCode = getParameter("dictCode");
        if (dictCode.equals(oldDictCode)) {
            result = true;
            return result;
        }
        return dictService.checkDictCode(dictCode);
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
    @RequestMapping(value = "/checkHasChild", method = {
        RequestMethod.POST, RequestMethod.GET
    })
    public boolean checkHasChild() throws FrameworkException {
        boolean hasChild = false;
        String dictCodes = getParameter("dictCodes", "字典标识不能为空");
        String[] strs = StringUtils.split(dictCodes, ",");
        for (String dictCode : strs) {
            List<DictionaryDataPojo> list = dictService.queryDictDataPager(dictCode, getPageIndex(), getPageSize());
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
     * @param modelAndView
     * @return
     * @throws ServiceException <br>
     */
    @RequestMapping("/toAddDict")
    public ModelAndView toAddDict(ModelAndView modelAndView) throws FrameworkException {
        modelAndView.setViewName("dictionary/addDict");
        return modelAndView;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param functionPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/addDict", method = RequestMethod.POST)
    public ModelAndView addDict(@ModelAttribute("dictionaryPojo") DictionaryPojo dictionaryPojo)
        throws FrameworkException {
        dictService.addDict(dictionaryPojo);
        return success("新增字典成功!");
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
    @RequestMapping(value = "/toModifyDict")
    public String toModifyDict(ModelMap modelMap) throws FrameworkException {
        String dictCode = getParameter("dictCode");
        modelMap.addAttribute("dictionaryPojo", dictService.queryDict(dictCode));
        return "dictionary/modifyDict";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictionaryPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modifyDict", method = RequestMethod.POST)
    public ModelAndView modifyDict(@ModelAttribute("dictionaryPojo") DictionaryPojo dictionaryPojo)
        throws FrameworkException {
        String oldDictCode = getParameter("oldDictCode");
        dictService.modifyDict(dictionaryPojo, oldDictCode);
        return success("修改字典成功!");
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
    @RequestMapping(value = "/deleteDict", method = RequestMethod.POST)
    public ResponseEntity<?> deleteDict() throws FrameworkException {
        String dictCodes = getParameter("dictCodes", "删除的字典标识不能为空");
        String[] strs = StringUtils.split(dictCodes, ",");
        // 执行批量删除
        dictService.deleteDicts(strs);
        return new ResponseEntity<Object>(HttpStatus.OK);
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
    @RequestMapping("/toAddDictData")
    public ModelAndView toAddDictData(ModelAndView modelAndView) throws FrameworkException {
        modelAndView.addObject("dictCode", getParameter("dictCode"));
        modelAndView.setViewName("dictionary/addDictData");
        return modelAndView;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictionaryPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/addDictData", method = RequestMethod.POST)
    public ModelAndView addDictData(@ModelAttribute("dictionaryDataPojo") DictionaryDataPojo dictionaryDataPojo)
        throws FrameworkException {
        dictService.addDictData(dictionaryDataPojo);
        return success("新增字典数据成功!");
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
    @RequestMapping(value = "/toModifyDictData")
    public String toModifyDictData(ModelMap modelMap) throws FrameworkException {
        Integer dictDataId = getIntegerParameter("dictDataId");
        modelMap.addAttribute("dictionaryDataPojo", dictService.queryDictData(dictDataId));
        return "dictionary/modifyDictData";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param dictionaryDataPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modifyDictData", method = RequestMethod.POST)
    public ModelAndView modifyDictData(@ModelAttribute("dictionaryDataPojo") DictionaryDataPojo dictionaryDataPojo)
        throws FrameworkException {
        dictService.modifyDictData(dictionaryDataPojo);
        return success("修改字典数据成功!");
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
    @RequestMapping(value = "/deleteDictData", method = RequestMethod.POST)
    public ResponseEntity<?> deleteDictData() throws FrameworkException {
        String dictDataIds = getParameter("dictDataIds", "删除的字典数据标识不能为空");
        Integer[] strs = CommonUtil.splitId(dictDataIds, ",");
        // 执行批量删除
        dictService.deleteDictData(strs);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * Description: 从系统中将字典导出为Excel文件<br>
     * 
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @throws Exception <br>
     */
    @RequestMapping(value = "/exportDict", method = RequestMethod.GET)
    public void exportDict(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("字典.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<DictionaryPojo> list = dictService.queryDict();
            ExcelExportDto<DictionaryPojo> dto = new ExcelExportDto<DictionaryPojo>(DICT_FIELDS, DICT_FIELDS_HEADER,
                list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export dict.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export dict.", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "/exportDictData", method = RequestMethod.GET)
    public void exportDictData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("字典数据.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<DictionaryDataPojo> list = dictService.queryDictData();
            ExcelExportDto<DictionaryDataPojo> dto = new ExcelExportDto<DictionaryDataPojo>(DICT_DATA_FIELDS,
                DICT_DATA_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export dict.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export dict.", e);
        }
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
    @RequestMapping(value = "/toImportDict", method = RequestMethod.GET)
    public String toImportDict(ModelMap modelMap) throws FrameworkException {
        return "dictionary/importDict";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/importDict", method = RequestMethod.POST)
    public ModelAndView importDict() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        dictService.importDict(mediaId, mediaName);
        return success("导入字典成功!");
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
    @RequestMapping(value = "/toImportDictData", method = RequestMethod.GET)
    public String toImportDictData(ModelMap modelMap) throws FrameworkException {
        return "dictionary/importDictData";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/importDictData", method = RequestMethod.POST)
    public ModelAndView importDictData() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        dictService.importDictData(mediaId, mediaName);
        return success("导入字典成功!");
    }
}
