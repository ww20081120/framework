package com.hbasesoft.framework.web.permission.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.service.common.ModuleService;
import com.hbasesoft.framework.web.permission.bean.MenuPojo;
import com.hbasesoft.framework.web.permission.service.MenuService;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.config.core.bean.ModulePojo;

/**
 * <Description> 菜单控制 <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @see com.hbasesoft.framework.web.manager.controller.menu <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/permission/menu")
public class MenuController extends BaseController {

    private static final String PAGE_INDEX = "permission/menu/index";

    private static final String PAGE_ADD = "permission/menu/add";

    private static final String PAGE_ADD_BUTTON = "permission/menu/addBtn";

    private static final String PAGE_MODIFY = "permission/menu/modify";

    private static final String PAGE_MODIFY_BUTTON = "permission/menu/modifyBtn";

    @Resource
    private MenuService menuService;

    @Resource
    private ModuleService moduleService;

    @RequestMapping()
    public ModelAndView index() throws ServiceException {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, ModulePojo> moduleMap = moduleService.selectAllModule();
        map.put("moduleList", moduleMap.values());
        return new ModelAndView(PAGE_INDEX, map);
    }

    @ResponseBody
    @RequestMapping("/list")
    public String list() throws FrameworkException {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("resourceId", "parentResourceId", "menuName",
            "isLeaf", "childrenMenu");
        List<MenuPojo> list = menuService.queryMenu(getParameter("moduleCode"));
        return JSON.toJSONString(list, filter, SerializerFeature.WriteMapNullValue);
    }

    @RequestMapping("/toAdd")
    public ModelAndView toAdd(ModelAndView modelAndView) throws ServiceException {
        modelAndView.addObject("parentResourceId", getParameter("parentResourceId"));
        modelAndView.addObject("parentMenuName", getParameter("parentMenuName"));
        modelAndView.addObject("moduleCode", getParameter("moduleCode"));
        modelAndView.addObject("moduleName", getParameter("moduleName"));
        modelAndView.setViewName(PAGE_ADD);
        return modelAndView;
    }

    @RequestMapping("/toAddBtn")
    public ModelAndView toAddButton(ModelAndView modelAndView) throws ServiceException {
        modelAndView.addObject("parentResourceId", getParameter("parentResourceId"));
        modelAndView.addObject("parentMenuName", getParameter("parentMenuName"));
        modelAndView.addObject("moduleCode", getParameter("moduleCode"));
        modelAndView.addObject("moduleName", getParameter("moduleName"));
        modelAndView.setViewName(PAGE_ADD_BUTTON);
        return modelAndView;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addMenu(MenuPojo menuPojo) throws FrameworkException {
        menuService.addMenu(menuPojo);
        return success("新增菜单信息成功!");
    }

    @RequestMapping(value = "/addBtn", method = RequestMethod.POST)
    public ModelAndView addButton(MenuPojo menuPojo) throws FrameworkException {

        return success("新增菜单按钮信息成功!");
    }

    @RequestMapping("/toModify")
    public String toModify(ModelMap modelMap) throws ServiceException {
        MenuPojo menuPojo = menuService.queryById(NumberUtils.toLong(getParameter("resourceId")));
        modelMap.addAttribute("menuPojo", menuPojo);
        return PAGE_MODIFY;
    }

    @RequestMapping("/toModifyBtn")
    public String toModifyBtn(ModelMap modelMap) throws ServiceException {
        MenuPojo menuPojo = menuService.queryById(NumberUtils.toLong(getParameter("resourceId")));
        modelMap.addAttribute("menuPojo", menuPojo);
        return PAGE_MODIFY_BUTTON;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(MenuPojo menuPojo) throws FrameworkException {
        menuService.modifyMenu(menuPojo);
        return success("修改菜单信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/seq/modify", method = RequestMethod.POST)
    public ResponseEntity<?> modifySeq() throws FrameworkException {
        String menus = getParameter("menus", "修改的菜单不存在");
        JSONArray array = JSONArray.parseArray(menus);
        List<MenuPojo> menuList = new ArrayList<MenuPojo>();
        MenuPojo menu;
        for (Object obj : array) {
            JSONObject jObj = (JSONObject) obj;
            menu = new MenuPojo();
            menu.setResourceId(jObj.getLong("id"));
            menu.setParentResourceId(jObj.getLong("pid"));
            menu.setSeq(jObj.getLong("seq"));
            menu.setIsLeaf(jObj.getString("isLeaf"));
            menuList.add(menu);
        }
        menuService.modifyMenuSeq(menuList);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> remove() throws FrameworkException {
        String ids = getParameter("resourceId", "删除的菜单标识不能为空");
        menuService.remove(ids);
        return new ResponseEntity<Object>(GlobalConstants.BLANK, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/checkName")
    public boolean checkName() {
        return menuService.checkName(getParameter("resourceId"), getParameter("menuName"));
    }

}