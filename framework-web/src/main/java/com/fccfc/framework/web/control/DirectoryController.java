package com.fccfc.framework.web.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.bean.JsonUtil;
import com.fccfc.framework.config.core.bean.DirectoryPojo;
import com.fccfc.framework.web.service.DirectoryService;

/**
 * 
 * <Description> <br> 
 *  
 * @author yang.zhipeng <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年7月2日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.control <br>
 */
@Controller
public class DirectoryController {

    /**
     * directoryService
     */
    @Resource
    private DirectoryService directoryService;

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param code <br>
     * @param root <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    public String listDirectory(@RequestParam(value = "id", required = false) String code,
        @RequestParam("root") String root) throws FrameworkException {
        Assert.notEmpty(root, "父节点代码不能为空");
        if (CommonUtil.isEmpty(code)) {
            code = root;
        }

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<DirectoryPojo> directList = directoryService.queryDirectoryByParentCode(code);
        if (CommonUtil.isNotEmpty(directList)) {
            Map<String, Object> dict;
            for (DirectoryPojo pojo : directList) {
                dict = new HashMap<String, Object>();
                dict.put("id", pojo.getDirectoryCode());
                dict.put("name", pojo.getDirectoryName());
                dict.put("remark", pojo.getRemark());
                dict.put("isParent", true);
                result.add(dict);
            }
        }

        return JsonUtil.writeObj2JSON(result);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param parentCode <br>
     * @param code <br>
     * @param name <br>
     * @param remark <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    public ResponseEntity<String> addDirectory(@RequestParam("pid") String parentCode, @RequestParam("id") String code,
        @RequestParam("name") String name, @RequestParam(value = "remark", required = false) String remark)
        throws FrameworkException {
        Assert.notEmpty(parentCode, "父目录不能为空");
        Assert.notEmpty(code, "目录代码不能为空");
        Assert.notEmpty(name, "目录名称不能为空");
        DirectoryPojo pojo = new DirectoryPojo();
        pojo.setDirectoryCode(code);
        pojo.setDirectoryName(name);
        pojo.setParentDirectoryCode(parentCode);
        pojo.setRemark(remark);
        directoryService.addDirectory(pojo);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param code <br>
     * @param name <br>
     * @param remark <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    public ResponseEntity<String> modifyDirectory(@RequestParam("id") String code, @RequestParam("name") String name,
        @RequestParam(value = "remark", required = false) String remark) throws FrameworkException {
        Assert.notEmpty(code, "目录代码不能为空");
        Assert.notEmpty(name, "目录名称不能为空");
        DirectoryPojo pojo = new DirectoryPojo();
        pojo.setDirectoryCode(code);
        pojo.setDirectoryName(name);
        pojo.setRemark(remark);
        directoryService.modifyDirectory(pojo);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param code <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    public ResponseEntity<String> deleteDirectory(@RequestParam("id") String code) throws FrameworkException {
        Assert.notEmpty(code, "目录代码不能为空");
        directoryService.deleteDirectory(code);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
