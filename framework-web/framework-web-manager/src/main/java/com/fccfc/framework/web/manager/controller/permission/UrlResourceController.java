package com.fccfc.framework.web.manager.controller.permission;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
import com.fccfc.framework.db.core.utils.PagerList;
import com.fccfc.framework.web.manager.bean.permission.UrlResourcePojo;
import com.fccfc.framework.web.manager.controller.AbstractController;
import com.fccfc.framework.web.manager.service.permission.UrlResourceService;

@Controller
@RequestMapping("/url")
public class UrlResourceController extends AbstractController {

	private static final String PAGE_ADD = "function/addUrl";

	private static final String PAGE_MODIFY = "function/modUrl";

	@Resource
	private UrlResourceService urlResourceService;

	/**
	 * 
	 * Description: 分页查询所有的URL资源信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	@ResponseBody
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Map<String, Object> queryUrlData() throws ServiceException {
		Map<String, Object> result = new HashMap<String, Object>();
		// 分页查询所有URL资源信息
		String functionId = getParameter("functionId");
		PagerList<UrlResourcePojo> urlList = (PagerList<UrlResourcePojo>) urlResourceService
				.queryUrlResource(NumberUtils.toLong(functionId),
						getPageIndex(), getPageSize());
		result.put("data", urlList);
		result.put("pageIndex", urlList.getPageIndex());
		result.put("pageSize", urlList.getPageSize());
		result.put("totalCount", urlList.getTotalCount());
		result.put("totalPage", urlList.getTotalPage());
		return result;
	}

	/**
	 * 
	 * Description: 跳转到添加页面<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param modelAndView
	 * @return
	 * @throws ServiceException
	 * <br>
	 */
	@RequestMapping("/toAdd")
	public ModelAndView toAddUrl(ModelAndView modelAndView)
			throws ServiceException {
		modelAndView.addObject("functionId", getParameter("functionId"));
		modelAndView.setViewName(PAGE_ADD);
		return modelAndView;
	}

	/**
	 * 
	 * Description: 添加URL资源信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws FrameworkException
	 * <br>
	 */
	@RequestMapping(value = "/addUrl", method = RequestMethod.POST)
	public ModelAndView addUrl(
			@ModelAttribute("urlResourcePojo") UrlResourcePojo urlResourcePojo)
			throws FrameworkException {
		urlResourceService.addUrlResource(urlResourcePojo);
		return success("新增URL资源信息成功!");
	}

	/**
	 * 
	 * Description: 批量删除URL资源信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @return
	 * @throws FrameworkException
	 * <br>
	 */
	@ResponseBody
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteUrl() throws FrameworkException {
		// 获取要删除的id
		String ids = getParameter("ids", "删除的URL资源信息标识不能为空");
		// 执行批量删除
		urlResourceService.deleteUrls(CommonUtil.splitIdsByLong(ids, ","));
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	/**
	 * 
	 * Description: 跳转到修改URL资源的页面<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param urlResourcePojo
	 * @return
	 * @throws FrameworkException
	 * <br>
	 */
	@RequestMapping(value = "/toModify")
	public ModelAndView toModify(ModelMap modelMap) throws FrameworkException {
		// 获取 resourceId
		String resourceId = getParameter("resourceId");
		// 根据 resourceId 查询 urlResourcePojo 对象
		modelMap.addAttribute("urlResourcePojo", urlResourceService.queryUrl(NumberUtils.toLong(resourceId)));
		return new ModelAndView(PAGE_MODIFY);
	}

	/**
	 * 
	 * Description: 修改URL资源信息<br>
	 * 
	 * @author 胡攀<br>
	 * @taskId <br>
	 * @param modelAndView
	 * @return
	 * @throws FrameworkException
	 * <br>
	 */
	@RequestMapping(value = "/modifyUrl", method = RequestMethod.POST)
	public ModelAndView modifyUrl(
			@ModelAttribute("urlResourcePojo") UrlResourcePojo urlResourcePojo)
			throws FrameworkException {
		// 调用修改方法
		urlResourceService.modifyUrl(urlResourcePojo);
		return success("修改URL资源信息成功!");
	}

	@ResponseBody
	@RequestMapping(value = "/checkName")
	public boolean checkName() {
		return urlResourceService.checkName(getParameter("resourceId"), getParameter("resourceName"));
	}
}
