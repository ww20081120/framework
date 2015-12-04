package com.fccfc.framework.web.manager.controller.common;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.config.core.bean.DirectoryPojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.common.DirectoryService;

/**
 * <Description> 目录结构 - 功能模块<br>
 *
 * @author 胡攀<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年10月31日 <br>
 * @see com.fccfc.framework.web.manager.controller.common <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/common/directory")
public class DirectoryController extends AbstractController {

    private static final String PAGE_MODIFY = "common/directory/modify";

    /**
     * directoryService
     */
    @Resource
    private DirectoryService directoryService;

    /**
     * Description: 查询所有目录结构<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<com.fccfc.framework.web.manager.bean.system.DirectoryPojo> list() throws FrameworkException {
        return directoryService.selectDirectory(getParameter("parentDirectoryCode"));
    }

    /**
     * Description: 跳转到添加目录的页面<br>
     *
     * @param modelAndView
     * @return <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public ModelAndView toAdd(ModelAndView modelAndView) {
        modelAndView.addObject("parentDirectoryCode", getParameter("parentDirectoryCode"));
        modelAndView.addObject("parentDirectoryName", getParameter("parentDirectoryName"));
        modelAndView.setViewName("function/addDirectory");
        return modelAndView;
    }

    /**
     * Description: 添加目录结构<br>
     *
     * @param directoryPojo
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addDirectory(DirectoryPojo directoryPojo) throws FrameworkException {
        directoryService.addDirectory(directoryPojo);
        return success("新增目录架构信息成功!");
    }

    /**
     * Description: 删除目录<br>
     *
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ResponseEntity<?> remove() throws FrameworkException {
        String directoryCode = getParameter("directoryCode", "删除的目录标识不能为空");
        directoryService.removeDirectory(directoryCode);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    /**
     * Description: 跳转到修改目录的页面<br>
     *
     * @param modelMap
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/toModify", method = RequestMethod.GET)
    public String toModify(ModelMap modelMap) throws FrameworkException {
        String directoryCode = getParameter("directoryCode");
        modelMap.addAttribute("parentDirectoryName", getParameter("parentDirectoryName"));
        modelMap.addAttribute("directoryPojo", directoryService.queryDirectoryByCode(directoryCode));
        return PAGE_MODIFY;
    }

    /**
     * Description: 修改目录结构<br>
     *
     * @param directoryPojo
     * @return
     * @throws FrameworkException <br>
     * @author 胡攀<br>
     * @taskId <br>
     */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public ModelAndView modify(DirectoryPojo directoryPojo) throws FrameworkException {
        directoryService.modifyDirectory(directoryPojo);
        return success("修改组织信息成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public boolean checkCode() {
        return directoryService.checkCode(getParameter("directoryCode"));
    }

    @ResponseBody
    @RequestMapping(value = "/checkName", method = RequestMethod.GET)
    public boolean checkName() {
        return directoryService.checkName(getParameter("directoryCode"), getParameter("directoryName"));
    }
}
