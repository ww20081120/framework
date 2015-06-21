package com.fccfc.framework.web.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fccfc.framework.api.FrameworkException;
import com.fccfc.framework.api.bean.config.DirectoryPojo;
import com.fccfc.framework.core.utils.Assert;
import com.fccfc.framework.core.utils.CommonUtil;
import com.fccfc.framework.core.utils.JsonUtil;
import com.fccfc.framework.web.service.DirectoryService;

@Controller
public class DirectoryController {

    @Resource
    private DirectoryService directoryService;

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

    @ResponseBody
    public ResponseEntity<String> deleteDirectory(@RequestParam("id") String code) throws FrameworkException {
        Assert.notEmpty(code, "目录代码不能为空");
        directoryService.deleteDirectory(code);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
