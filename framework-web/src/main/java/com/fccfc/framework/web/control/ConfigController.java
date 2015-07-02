package com.fccfc.framework.web.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.utils.bean.JsonUtil;
import com.fccfc.framework.web.service.ConfigService;

/**
 * 参数配置Controller
 * 
 * @author skysun
 */
@Controller
public class ConfigController {

    /**
     * configService
     */
    @Resource
    private ConfigService configService;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    public String listConfigItems(HttpServletRequest request) throws FrameworkException {
        // 查询所有配置项目录
        List<Map<String, Object>> configItems = configService.queryConfigCatalogs();
        String data = JsonUtil.writeObj2JSON(configItems);

        // 查询所有模块
        List<Map<String, Object>> modules = configService.queryModules();
        String moduleData = JsonUtil.writeObj2JSON(modules);

        // 查询输入方式
        List<Map<String, Object>> inputTypes = configService.queryInputTypes();

        // 查询数据类型
        List<Map<String, Object>> dataTypes = configService.queryDataTypes();

        request.setAttribute("data", data);
        request.setAttribute("moduleData", moduleData);
        request.setAttribute("inputTypes", JsonUtil.writeObj2JSON(inputTypes));
        request.setAttribute("dataTypes", JsonUtil.writeObj2JSON(dataTypes));
        return "config/index";
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    // @RequestMapping("/system/config/qryItem")
    public String queryConfigItem(HttpServletRequest request) throws FrameworkException {
        String directory = request.getParameter("directory");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("directory", directory);
        return JsonUtil.writeObj2JSON(configService.queryConfigItems(paramMap));
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    // @RequestMapping("/system/config/addItem")
    public String addConfigItem(HttpServletRequest request) throws FrameworkException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String configCode = request.getParameter("configCode");
        String configName = request.getParameter("configName");
        String module = request.getParameter("module");
        String directory = request.getParameter("directory");
        String vasiable = request.getParameter("visible");
        String remark = request.getParameter("remark");
        if (StringUtils.isEmpty(configName) || StringUtils.isEmpty(module) || StringUtils.isEmpty(module)
            || StringUtils.isEmpty(directory) || StringUtils.isEmpty(vasiable)) {
            throw new FrameworkException('1', "config item can,t be null");
        }
        paramMap.put("configName", configName);
        paramMap.put("module", module);
        paramMap.put("directory", directory);
        paramMap.put("vasiable", vasiable);
        paramMap.put("remark", remark);
        paramMap.put("configCode", configCode);
        configService.addConfigItem(paramMap);
        return "";
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    // @RequestMapping("/system/config/queryParams")
    public String queryParams(HttpServletRequest request) throws FrameworkException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("itemId", request.getParameter("itemId"));
        List<Map<String, Object>> params = configService.queryParams(paramMap);
        return JsonUtil.writeObj2JSON(params);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    public String addParams(HttpServletRequest request) throws FrameworkException {
        String paramCode = request.getParameter("paramCode");
        String paramName = request.getParameter("paramName");
        String dataType = request.getParameter("dataType");
        String defaultValue = request.getParameter("defaultValue");
        String valueScript = request.getParameter("valueScript");
        String paramRemark = request.getParameter("paramRemark");
        String paramValues = request.getParameter("paramValues");
        String itemId = request.getParameter("itemId");
        String inputType = request.getParameter("inputType");

//        ConfigPojo config = new ConfigPojo();
//        config.setDataType(dataType);
//        config.setDefaultValue(defaultValue);
//        config.setParamCode(paramCode);
//        config.setParamValue(paramValues);
//        config.setParamRemark(paramRemark);
//        config.setInputType(inputType);
//        config.setItemId(itemId);
//        config.setParamName(paramName);
//        config.setValueScript(valueScript);
        int type = Integer.parseInt(inputType);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("itemId", itemId);
        paramMap.put("itemId", itemId);
        paramMap.put("paramCode", paramCode);
        paramMap.put("paramName", paramName);
        paramMap.put("paramValue", paramValues);
        paramMap.put("defaultValue", defaultValue);
        paramMap.put("dataType", dataType);
        paramMap.put("inputType", inputType);
        paramMap.put("valueScript", valueScript);
        paramMap.put("paramRemark", paramRemark);
        switch (type) {
        // 输入方式-不可编辑
            case 1:

                break;
            // 输入方式-单选框 |复选框
            case 2:
            case 3:
                break;
            case 4:
                break;

            default:
                break;
        }
        configService.addParam(paramMap);
        return "";
    }
}
