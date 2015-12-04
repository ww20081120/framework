package com.fccfc.framework.web.manager.controller.permission;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
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
import com.fccfc.framework.web.core.utils.excel.GetDataCallback;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.FunctionService;

/**
 * <Description> 功能模块配置<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月29日 <br>
 * @see com.fccfc.framework.web.manager.controller.menu <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/function")
public class FunctionController extends AbstractController {

    private static final Logger logger = new Logger(FunctionController.class);

    private static final String[] FIELDS = new String[] {
        "functionId", "directoryCode", "functionName", "remark", "directoryName"
    };

    private static final Map<String, String> FIELDS_HEADER = new HashMap<String, String>();

    static {
        FIELDS_HEADER.put("functionId", "功能Id");
        FIELDS_HEADER.put("directoryCode", "归属目录");
        FIELDS_HEADER.put("directoryName", "归属目录名称");
        FIELDS_HEADER.put("functionName", "功能名称");
        FIELDS_HEADER.put("remark", "备注");
    }

    @Resource
    private FunctionService functionService;

    /**
     * Description: 跳转到功能配置页<br>
     *
     * @return <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toFunction() {
        return "function/function";
    }

    /**
     * Description: 查询所有的功能点<br>
     *
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Map<String, Object> queryFunctionData() throws ServiceException {
        Map<String, Object> result = new HashMap<String, Object>();
        String directoryCode = getParameter("directoryCode");
        String functionName = getParameter("params");
        // 分页查询所有功能点信息
        PagerList<FunctionPojo> functionList = (PagerList<FunctionPojo>) functionService.queryFunction(directoryCode,
            functionName, getPageIndex(), getPageSize());
        result.put("data", functionList);
        result.put("pageIndex", functionList.getPageIndex());
        result.put("pageSize", functionList.getPageSize());
        result.put("totalCount", functionList.getTotalCount());
        result.put("totalPage", functionList.getTotalPage());
        return result;
    }

    /**
     * Description: 跳转到添加页面<br>
     *
     * @param modelAndView
     * @return
     * @throws ServiceException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView toAddFunction(ModelAndView modelAndView) throws ServiceException {
        modelAndView.addObject("directoryCode", getParameter("directoryCode"));
        modelAndView.addObject("directoryName", getParameter("directoryName"));
        modelAndView.setViewName("function/addFunction");
        return modelAndView;
    }

    /**
     * Description: 添加功能信息<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/addFunction", method = RequestMethod.POST)
    public ModelAndView addFunction(@ModelAttribute("functionPojo") FunctionPojo functionPojo)
        throws FrameworkException {
        functionService.addFunction(functionPojo);
        return success("新增功能信息成功!");
    }

    /**
     * Description: 批量删除功能模块信息<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<?> deleteFunction() throws FrameworkException {
        // 获取要删除的id
        String ids = getParameter("ids", "删除的功能标识不能为空");
        // 执行批量删除
        functionService.deleteFunctions(CommonUtil.splitIdsByLong(ids, ","));
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * Description:跳转到功能模块修改页面 <br>
     *
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/toModify", method = RequestMethod.GET)
    public String toModify(ModelMap modelMap) throws FrameworkException {
        // 获取 functionId
        String functionId = getParameter("functionId");
        modelMap.addAttribute("directoryName", getParameter("directoryName"));
        modelMap.addAttribute("functionPojo", functionService.queryFunction(NumberUtils.toLong(functionId)));
        return "function/modifyFunction";
    }

    /**
     * Description: 修改功能点<br>
     *
     * @param functionPojo
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/modifyFunction", method = RequestMethod.POST)
    public ModelAndView modifyFunction(FunctionPojo functionPojo) throws FrameworkException {
        functionService.modifyFunction(functionPojo);
        return success("修改功能信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public boolean checkName() {
        return functionService.checkName(getParameter("functionId"), getParameter("functionName"));
    }

    @RequestMapping(value = "/toImport", method = RequestMethod.GET)
    public String toImport(ModelMap modelMap) throws FrameworkException {
        modelMap.addAttribute("directoryCode", getParameter("directoryCode"));
        modelMap.addAttribute("directoryName", getParameter("directoryName"));
        return "function/importFunction";
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ModelAndView importFunction() throws FrameworkException {
        String directoryCode = getParameter("directoryCode");
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        functionService.importFunction(directoryCode, mediaId, mediaName);
        return success("导入功能信息成功!");
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletResponse response) {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition",
                "attachment; filename=" + ExcelUtil.encoderFileName("function.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头

            final String directoryCode = getParameter("directoryCode");
            final String functionName = getParameter("params");

            int total = functionService.listTotal(directoryCode, functionName);
            ExcelExportDto dto = new ExcelExportDto(FIELDS, FIELDS_HEADER, total, new GetDataCallback() {
                @Override
                public List invoke(int pageIndex, int pageSize) {
                    try {
                        return functionService.queryFunction(directoryCode, functionName, pageIndex, pageSize);
                    }
                    catch (ServiceException e) {
                        logger.error("Query function information failed while export function.", e);
                    }
                    return null;
                }
            });
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export function.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export function.", e);
        }
    }
}
