package com.fccfc.framework.web.manager.controller.permission;

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
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.config.core.bean.ModulePojo;
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.bean.permission.FunctionPojo;
import com.fccfc.framework.web.manager.bean.permission.MenuPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.FunctionService;
import com.fccfc.framework.web.manager.service.permission.MenuService;

/**
 * <Description> 菜单控制 <br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月20日 <br>
 * @see com.fccfc.framework.web.manager.controller.menu <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/menu")
public class MenuController extends AbstractController {

    private static final String PAGE_INDEX = "menu/menu";

    private static final String PAGE_ADD = "menu/addMenu";

    private static final String PAGE_ADD_BUTTON = "menu/addMenuBtn";

    private static final String PAGE_MODIFY = "menu/modMenu";

    private static final String PAGE_MODIFY_BUTTON = "menu/modMenuBtn";

    @Resource
    private MenuService menuService;

    @Resource
    private FunctionService functionService;

    @RequestMapping()
    public ModelAndView index() throws ServiceException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ModulePojo> moduleList = menuService.queryModule();
        map.put("moduleList", moduleList);
        return new ModelAndView(PAGE_INDEX, map);
    }

    @ResponseBody
    @RequestMapping("/list")
    public String list() throws FrameworkException {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter("resourceId", "parentResourceId", "menuName",
            "isLeaf", "childrenMenu");
        List<MenuPojo> list = menuService.queryMenu(getParameter("moduleCode"), MenuPojo.TYPE_MENU);
        return JSON.toJSONString(list, filter, SerializerFeature.WriteMapNullValue);
    }

    @ResponseBody
    @RequestMapping("/listButton")
    public Map<String, Object> listButton() throws FrameworkException {
        String moduleCode = getParameter("moduleCode");
        String menuResourceId = getParameter("menuResourceId");
        PagerList<MenuPojo> list = (PagerList<MenuPojo>) menuService.listMenuButton(moduleCode, menuResourceId,
            getPageIndex(), getPageSize());

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("data", list);
        result.put("pageIndex", list.getPageIndex());
        result.put("pageSize", list.getPageSize());
        result.put("totalCount", list.getTotalCount());
        result.put("totalPage", list.getTotalPage());
        return result;
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

    @ResponseBody
    @RequestMapping("/listFunction")
    public List<FunctionPojo> listFunction() throws FrameworkException {
        return functionService.queryFunction();
    }

    @RequestMapping(value = "/addMenu", method = RequestMethod.POST)
    public ModelAndView addMenu(MenuPojo menuPojo) throws FrameworkException {
        menuPojo.setType(MenuPojo.TYPE_MENU);
        menuService.addMenu(menuPojo);
        return success("新增菜单信息成功!");
    }

    @RequestMapping(value = "/addMenuBtn", method = RequestMethod.POST)
    public ModelAndView addButton(MenuPojo menuPojo) throws FrameworkException {
        menuPojo.setType(MenuPojo.TYPE_BUTTON);
        menuService.addMenu(menuPojo);
        return success("新增菜单按钮信息成功!");
    }

    @RequestMapping("/toModify")
    public String toModify(ModelMap modelMap) throws ServiceException {
        MenuPojo menuPojo = menuService.queryById(NumberUtils.toLong(getParameter("resourceId")));
        modelMap.addAttribute("menuPojo", menuPojo);
        return PAGE_MODIFY;
    }

    @RequestMapping("/toModifyBtn")
    public String toModifyMenuBtn(ModelMap modelMap) throws ServiceException {
        MenuPojo menuPojo = menuService.queryById(NumberUtils.toLong(getParameter("resourceId")));
        modelMap.addAttribute("menuPojo", menuPojo);
        return PAGE_MODIFY_BUTTON;
    }

    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modifyMenu(MenuPojo menuPojo) throws FrameworkException {
        menuService.modifyMenu(menuPojo);
        return success("修改菜单信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/modify/seq", method = RequestMethod.POST)
    public ResponseEntity<?> modifyMenuSeq() throws FrameworkException {
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
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/checkName")
    public boolean checkName() {
        return menuService.checkName(getParameter("resourceId"), getParameter("menuName"));
    }

}