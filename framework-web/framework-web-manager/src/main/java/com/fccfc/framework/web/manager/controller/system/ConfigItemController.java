/**************************************************************************************** 
 Copyright © 2003-2012 fccfc Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.fccfc.framework.web.manager.controller.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.date.DateConstants;
import com.fccfc.framework.common.utils.date.DateUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.config.core.bean.ConfigItemPojo;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.core.utils.excel.ExcelExportDto;
import com.fccfc.framework.web.core.utils.excel.ExcelUtil;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamPojo;
import com.fccfc.framework.web.manager.bean.system.ConfigItemParamValuePojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.system.ConfigItemService;

/**
 * <Description> <br>
 * 
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年11月24日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.manager.controller.configitem <br>
 */
@Controller
@RequestMapping("/config")
public class ConfigItemController extends AbstractController {

    private Logger logger = new Logger(ConfigItemController.class);

    private static final String[] CONFIG_ITEM_FIELDS = new String[] {
        "configItemId", "directoryCode", "moduleCode", "configItemCode", "configItemName", "isVisiable", "updateTime",
        "remark"
    };

    private static final Map<String, String> CONFIG_ITEM_FIELDS_HEADER = new HashMap<String, String>();

    static {
        CONFIG_ITEM_FIELDS_HEADER.put("configItemId", "配置项标识");
        CONFIG_ITEM_FIELDS_HEADER.put("directoryCode", "目录代码");
        CONFIG_ITEM_FIELDS_HEADER.put("moduleCode", "业务模块代码");
        CONFIG_ITEM_FIELDS_HEADER.put("configItemCode", "配置项代码");
        CONFIG_ITEM_FIELDS_HEADER.put("configItemName", "配置项名称");
        CONFIG_ITEM_FIELDS_HEADER.put("isVisiable", "是否可见");
        CONFIG_ITEM_FIELDS_HEADER.put("updateTime", "更新时间");
        CONFIG_ITEM_FIELDS_HEADER.put("remark", "备注");
    }

    private static final String[] CONFIG_ITEM_PARAM_FIELDS = new String[] {
        "configItemId", "paramCode", "paramName", "paramValue", "defaultParamValue", "dataType", "inputType",
        "valueScript", "updateTime", "remark"
    };

    private static final Map<String, String> CONFIG_ITEM_PARAM_FIELDS_HEADER = new HashMap<String, String>();

    static {
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("configItemId", "配置项标识");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("paramCode", "参数编码");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("paramName", "参数名称");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("paramValue", "参数取值");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("defaultParamValue", "缺省值");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("dataType", "数据类型");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("inputType", "输入方式");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("valueScript", "取值校验规则");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("updateTime", "更新时间");
        CONFIG_ITEM_PARAM_FIELDS_HEADER.put("remark", "备注");
    }

    private static final String[] CONFIG_ITEM_PARAM_VALUE_FIELDS = new String[] {
        "configItemId", "paramCode", "paramValueId", "valueMark", "value", "remark"
    };

    private static final Map<String, String> CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER = new HashMap<String, String>();

    static {
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("configItemId", "配置项标识");
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("paramCode", "参数编码");
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("paramValueId", "参数取值标识");
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("valueMark", "取值说明");
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("value", "取值");
        CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER.put("remark", "备注");
    }

    @Resource
    private ConfigItemService configItemService;

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return <br>
     */
    @RequestMapping(method = RequestMethod.GET)
    public String toConfigItem() {
        return "configitem/configitem";
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
    @RequestMapping(value = "/queryConfigItemPager", method = RequestMethod.GET)
    public Map<String, Object> queryConfigItemPager() throws FrameworkException {
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<ConfigItemPojo> dictList = (PagerList<ConfigItemPojo>) configItemService
            .queryConfigItemPager(getPageIndex(), getPageSize());
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
    @RequestMapping(value = "/queryItemParamPager", method = RequestMethod.GET)
    public Map<String, Object> queryItemParamPager() throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId");
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<ConfigItemParamPojo> dictList = (PagerList<ConfigItemParamPojo>) configItemService
            .queryConfigItemParamPager(configItemId, getPageIndex(), getPageSize());
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
    @RequestMapping(value = "/queryParamValuePager", method = RequestMethod.GET)
    public Map<String, Object> queryParamValuePager() throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId");
        String paramCode = getParameter("paramCode");
        Map<String, Object> result = new HashMap<String, Object>();
        PagerList<ConfigItemParamValuePojo> dictList = (PagerList<ConfigItemParamValuePojo>) configItemService
            .queryConfigItemParamValuePager(configItemId, paramCode, getPageIndex(), getPageSize());
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
     * @throws FrameworkException <br>
     */
    @RequestMapping("/toAddConfigItem")
    public ModelAndView toAddConfigItem(ModelAndView modelAndView) throws FrameworkException {
        modelAndView.setViewName("configitem/addConfigItem");
        return modelAndView;
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
    @RequestMapping(value = "/checkConfigItemCode", method = RequestMethod.GET)
    public boolean checkConfigItemCode() throws FrameworkException {
        boolean result = false;
        String configItemCode = getParameter("configItemCode");
        String oldConfigItemCode = getParameter("oldConfigItemCode");
        if (configItemCode.equals(oldConfigItemCode)) {
            result = true;
            return result;
        }
        ConfigItemPojo paramPojo = new ConfigItemPojo();
        paramPojo.setConfigItemCode(configItemCode);
        ConfigItemPojo pojo = configItemService.queryConfigItem(paramPojo);
        if (null == pojo) {
            result = true;
        }
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
    @RequestMapping(value = "/queryDirectoryCode", method = RequestMethod.GET)
    public String queryDirectoryCode() throws FrameworkException {
        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("directoryName", "directoryCode");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(configItemService.queryDirectoryCode(), filter, SerializerFeature.WriteMapNullValue);
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
    @RequestMapping(value = "/queryModuleCode", method = RequestMethod.GET)
    public String queryModuleCode() throws FrameworkException {
        // 指定返回的json字段
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("moduleName", "moduleCode");

        // 将数据集合转换成json并返回给调用者
        return JSON.toJSONString(configItemService.queryModuleCode(), filter, SerializerFeature.WriteMapNullValue);
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/addConfigItem", method = RequestMethod.POST)
    public ModelAndView addConfigItem(@ModelAttribute("configItemPojo") ConfigItemPojo configItemPojo)
        throws FrameworkException {
        configItemPojo.setUpdateTime(new Date());
        configItemService.addConfigItem(configItemPojo);
        return success("新增配置项成功!");
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
    @RequestMapping(value = "/toModifyConfigItem")
    public String toModifyConfigItem(ModelMap modelMap) throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId");
        modelMap.addAttribute("configItemPojo", configItemService.queryConfigItem(configItemId));
        return "configitem/modifyConfigItem";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modifyConfigItem", method = RequestMethod.POST)
    public ModelAndView modifyConfigItem(@ModelAttribute("configItemPojo") ConfigItemPojo configItemPojo)
        throws FrameworkException {
        configItemPojo.setUpdateTime(new Date());
        ConfigItemPojo oldPojo = new ConfigItemPojo();
        oldPojo.setConfigItemId(getIntegerParameter("OldConfigItemId"));
        oldPojo.setDirectoryCode(getParameter("oldDirectoryCode"));
        oldPojo.setModuleCode(getParameter("oldModuleCode"));
        oldPojo.setConfigItemCode(getParameter("oldConfigItemCode"));
        oldPojo.setConfigItemName(getParameter("oldConfigItemName"));
        oldPojo.setIsVisiable(getParameter("oldIsVisiable"));
        oldPojo.setUpdateTime(DateUtil.string2Date(getParameter("oldUpdateTime"), DateConstants.DATETIME_FORMAT_19));
        oldPojo.setRemark(getParameter("oldRemark"));

        List<ConfigItemPojo> pojoList = new ArrayList<ConfigItemPojo>();
        pojoList.add(oldPojo);
        configItemService.addConfigItemHistory(pojoList);
        configItemService.modifyConfigItem(configItemPojo);
        return success("修改配置项成功!");
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
    @RequestMapping(value = "/deleteConfigItem", method = RequestMethod.POST)
    public ResponseEntity<?> deleteConfigItem() throws FrameworkException {
        String configItemIds = getParameter("configItemIds", "删除的配置项标识不能为空");
        Integer[] ids = CommonUtil.splitId(configItemIds, ",");
        List<ConfigItemPojo> pojoList = new ArrayList<ConfigItemPojo>();
        for (Integer id : ids) {
            pojoList.add(configItemService.queryConfigItem(id));
        }
        configItemService.addConfigItemHistory(pojoList);
        // 执行批量删除
        configItemService.deleteConfigItems(ids);

        return new ResponseEntity<Object>(HttpStatus.OK);
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
    @RequestMapping(value = "/checkHasParam", method = {
        RequestMethod.POST, RequestMethod.GET
    })
    public boolean checkHasParam() throws FrameworkException {
        boolean hasParam = false;
        String configItemIds = getParameter("configItemIds", "配置项标识不能为空");
        Integer[] ids = CommonUtil.splitId(configItemIds, ",");
        for (Integer configItemId : ids) {
            List<ConfigItemParamPojo> list = configItemService.queryConfigItemParamPager(configItemId, getPageIndex(),
                getPageSize());
            if (CommonUtil.isNotEmpty(list)) {
                hasParam = true;
                return hasParam;
            }
        }
        return hasParam;
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
    @RequestMapping("/toAddConfigItemParam")
    public ModelAndView toAddConfigItemParam(ModelAndView modelAndView) throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId", "配置项标识不能为空");
        modelAndView.addObject("configItemId", configItemId);
        modelAndView.setViewName("configitem/addConfigItemParam");
        return modelAndView;
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
    @RequestMapping(value = "/checkParamCode")
    public boolean checkParamCode() throws FrameworkException {
        boolean result = false;
        String paramCode = getParameter("paramCode");
        String oldParamCode = getParameter("oldParamCode");
        if (paramCode.equals(oldParamCode)) {
            result = true;
            return result;
        }
        ConfigItemParamPojo pojo = configItemService.queryConfigItemParam(null, paramCode);
        if (null == pojo) {
            result = true;
        }
        return result;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemParamPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/addConfigItemParam", method = RequestMethod.POST)
    public ModelAndView addConfigItemParam(
        @ModelAttribute("configItemParamPojo") ConfigItemParamPojo configItemParamPojo) throws FrameworkException {
        configItemParamPojo.setUpdateTime(new Date());
        configItemService.addConfigItemParam(configItemParamPojo);
        return success("新增配置项参数成功!");
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
    @RequestMapping(value = "/toModifyConfigItemParam")
    public String toModifyConfigItemParam(ModelMap modelMap) throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId");
        String paramCode = getParameter("paramCode");
        modelMap.addAttribute("configItemParamPojo", configItemService.queryConfigItemParam(configItemId, paramCode));
        return "configitem/modifyConfigItemParam";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemParamPojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modifyConfigItemParam", method = RequestMethod.POST)
    public ModelAndView modifyConfigItemParam(
        @ModelAttribute("configItemParamPojo") ConfigItemParamPojo configItemParamPojo) throws FrameworkException {
        String oldParamCode = getParameter("oldParamCode");
        configItemParamPojo.setUpdateTime(new Date());
        ConfigItemParamPojo oldPojo = new ConfigItemParamPojo();
        oldPojo.setConfigItemId(getIntegerParameter("oldConfigItemId"));
        oldPojo.setParamCode(getParameter("oldParamCode"));
        oldPojo.setParamName(getParameter("oldParamName"));
        oldPojo.setParamValue(getParameter("oldParamValue"));
        oldPojo.setDefaultParamValue(getParameter("oldDefaultParamValue"));
        oldPojo.setDataType(getParameter("oldDataType"));
        oldPojo.setInputType(getParameter("oldInputType"));
        oldPojo.setValueScript(getParameter("oldValueScript"));
        oldPojo.setUpdateTime(DateUtil.string2Date(getParameter("oldUpdateTime"), DateConstants.DATETIME_FORMAT_19));
        oldPojo.setRemark(getParameter("oldRemark"));
        List<ConfigItemParamPojo> list = new ArrayList<ConfigItemParamPojo>();
        list.add(oldPojo);
        configItemService.addConfigItemParamHistory(list);
        configItemService.modifyConfigItemParam(configItemParamPojo, oldParamCode);
        return success("修改配置项参数成功!");
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
    @RequestMapping(value = "/checkHasParamValue", method = {
        RequestMethod.POST, RequestMethod.GET
    })
    public boolean checkHasParamValue() throws FrameworkException {
        boolean hasParam = false;
        String configItemIds = getParameter("configItemIds", "配置项标识不能为空");
        Integer[] ids = CommonUtil.splitId(configItemIds, ",");
        String paramCodes = getParameter("paramCodes", "配置项参数编码不能为空");
        String[] codes = StringUtils.split(paramCodes, ",");
        for (int i = 0; i < ids.length; i++) {
            List<ConfigItemParamValuePojo> list = configItemService.queryConfigItemParamValuePager(ids[i], codes[i],
                getPageIndex(), getPageSize());
            if (CommonUtil.isNotEmpty(list)) {
                hasParam = true;
                return hasParam;
            }
        }
        return hasParam;
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
    @RequestMapping(value = "/deleteConfigItemParam", method = RequestMethod.POST)
    public ResponseEntity<?> deleteConfigItemParam() throws FrameworkException {
        String configItemIds = getParameter("configItemIds", "删除的配置项标识不能为空");
        String paramCodes = getParameter("paramCodes", "删除的配置项参数编码不能为空");
        Integer[] ids = CommonUtil.splitId(configItemIds, ",");
        String[] codes = StringUtils.split(paramCodes, ",");
        List<ConfigItemParamPojo> pojoList = new ArrayList<ConfigItemParamPojo>();
        for (int i = 0; i < ids.length; i++) {
            pojoList.add(configItemService.queryConfigItemParam(ids[i], codes[i]));
            configItemService.deleteConfigItemParams(ids[i], codes[i]);
        }
        configItemService.addConfigItemParamHistory(pojoList);
        return new ResponseEntity<Object>(HttpStatus.OK);
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
    @RequestMapping("/toAddConfigItemParamValue")
    public ModelAndView toAddConfigItemParamValue(ModelAndView modelAndView) throws FrameworkException {
        Integer configItemId = getIntegerParameter("configItemId", "配置项标识不能为空");
        String paramCode = getParameter("paramCode", "参数代码不能为空");
        modelAndView.addObject("configItemId", configItemId);
        modelAndView.addObject("paramCode", paramCode);
        modelAndView.setViewName("configitem/addConfigItemParamValue");
        return modelAndView;
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemParamValuePojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/addConfigItemParamValue", method = RequestMethod.POST)
    public ModelAndView addConfigItemParamValue(
        @ModelAttribute("configItemParamValuePojo") ConfigItemParamValuePojo configItemParamValuePojo)
            throws FrameworkException {
        configItemService.addConfigItemParamValue(configItemParamValuePojo);
        return success("新增配置项参数值成功!");
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
    @RequestMapping(value = "/toModifyConfigItemParamValue")
    public String toModifyConfigItemParamValue(ModelMap modelMap) throws FrameworkException {
        Integer paramValueId = getIntegerParameter("paramValueId");
        modelMap.addAttribute("configItemParamValuePojo", configItemService.queryConfigItemParamValue(paramValueId));
        return "configitem/modifyConfigItemParamValue";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param configItemParamValuePojo
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/modifyConfigItemParamValue", method = RequestMethod.POST)
    public ModelAndView modifyConfigItemParamValue(
        @ModelAttribute("configItemParamValuePojo") ConfigItemParamValuePojo configItemParamValuePojo)
            throws FrameworkException {
        configItemService.modifyConfigItemParamValue(configItemParamValuePojo);
        return success("修改配置项参数值成功!");
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
    @RequestMapping(value = "/deleteConfigItemParamValue", method = RequestMethod.POST)
    public ResponseEntity<?> deleteConfigItemParamValue() throws FrameworkException {
        String paramValueIds = getParameter("paramValueIds", "删除的配置项参数值标识不能为空");
        Integer[] ids = CommonUtil.splitId(paramValueIds, ",");
        // 执行批量删除
        configItemService.deleteConfigItemParamValues(ids);
        return new ResponseEntity<Object>(HttpStatus.OK);
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
    @RequestMapping(value = "/toImportConfigItem", method = RequestMethod.GET)
    public String toImportConfigItem(ModelMap modelMap) throws FrameworkException {
        return "configitem/importConfigItem";
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
    @RequestMapping(value = "/toImportConfigItemParam", method = RequestMethod.GET)
    public String toImportConfigItemParam(ModelMap modelMap) throws FrameworkException {
        return "configitem/importConfigItemParam";
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
    @RequestMapping(value = "/toImportConfigItemParamValue", method = RequestMethod.GET)
    public String toImportConfigItemParamValue(ModelMap modelMap) throws FrameworkException {
        return "configitem/importConfigItemParamValue";
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @return
     * @throws FrameworkException <br>
     */
    @RequestMapping(value = "/importConfigItem", method = RequestMethod.POST)
    public ModelAndView importConfigItem() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        configItemService.importConfigItem(mediaId, mediaName);
        return success("导入配置项成功!");
    }

    @RequestMapping(value = "/importConfigItemParam", method = RequestMethod.POST)
    public ModelAndView importConfigItemParam() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        configItemService.importConfigItemParam(mediaId, mediaName);
        return success("导入配置项参数成功!");
    }

    @RequestMapping(value = "/importConfigItemParamValue", method = RequestMethod.POST)
    public ModelAndView importConfigItemParamValue() throws FrameworkException {
        String mediaId = getParameter("mediaId");
        String mediaName = getParameter("mediaName");
        configItemService.importConfigItemParamValue(mediaId, mediaName);
        return success("导入配置项参数值成功!");
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "/exporConfigItem", method = RequestMethod.GET)
    public void exporConfigItem(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("配置项.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<ConfigItemPojo> list = configItemService.queryConfigItemPager(-1, -1);
            ExcelExportDto<ConfigItemPojo> dto = new ExcelExportDto<ConfigItemPojo>(CONFIG_ITEM_FIELDS,
                CONFIG_ITEM_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export config item.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export config item.", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "/exportConfigItemParam", method = RequestMethod.GET)
    public void exportConfigItemParam(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition", "attachment; filename=" + ExcelUtil.encoderFileName("配置项参数.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<ConfigItemParamPojo> list = configItemService.queryConfigItemParamPager(null, -1, -1);
            ExcelExportDto<ConfigItemParamPojo> dto = new ExcelExportDto<ConfigItemParamPojo>(CONFIG_ITEM_PARAM_FIELDS,
                CONFIG_ITEM_PARAM_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export config item param.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export config item param.", e);
        }
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param response
     * @throws Exception <br>
     */
    @RequestMapping(value = "/exportConfigItemParamValue", method = RequestMethod.GET)
    public void exportConfigItemParamValue(HttpServletResponse response) throws Exception {
        try {
            response.reset();// 清空输出流
            response.setHeader("cache-control", "public");
            response.setHeader("Pragma", "public");
            response.setHeader("Content-disposition",
                "attachment; filename=" + ExcelUtil.encoderFileName("配置项参数取值.xls"));
            response.setContentType("application/msexcel");// 设定输出文件头
            List<ConfigItemParamValuePojo> list = configItemService.queryConfigItemParamValuePager(null, null, -1, -1);
            ExcelExportDto<ConfigItemParamValuePojo> dto = new ExcelExportDto<ConfigItemParamValuePojo>(
                CONFIG_ITEM_PARAM_VALUE_FIELDS, CONFIG_ITEM_PARAM_VALUE_FIELDS_HEADER, list);
            ExcelUtil.exportExcel(response.getOutputStream(), dto);

        }
        catch (IOException e) {
            logger.error("It's appear exception [IOException] while export config item param value.", e);
        }
        catch (ServiceException e) {
            logger.error("It's appear exception [ServiceException] while export config item param value.", e);
        }
    }
}
