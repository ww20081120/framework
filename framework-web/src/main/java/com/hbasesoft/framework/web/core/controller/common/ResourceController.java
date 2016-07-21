package com.hbasesoft.framework.web.core.controller.common;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.cage.Cage;
import com.github.cage.GCage;
import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.date.DateConstants;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.io.IOUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.message.core.bean.AttachmentsPojo;
import com.hbasesoft.framework.web.core.WebConstant;
import com.hbasesoft.framework.web.core.controller.BaseController;
import com.hbasesoft.framework.web.core.service.common.ResourceService;
import com.hbasesoft.framework.web.core.utils.WebUtil;

/**
 * <Description> <br>
 *
 * @author wk <br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015/11/3 <br>
 * @see com.hbasesoft.framework.web.manager.controller.common <br>
 * @since V1.0<br>
 */
@Controller
@RequestMapping("/common/resource")
public class ResourceController extends BaseController {

    /**
     * logger
     */
    private Logger logger = new Logger(ResourceController.class);

    /**
     * mediaSize
     */
    private Map<String, Long> mediaSize;

    /**
     * contentTypes
     */
    private Map<String, String> contentTypes;

    /**
     * meidaTypes
     */
    private Map<String, MediaType> meidaTypes;

    /**
     * resourceService
     */
    @javax.annotation.Resource
    private ResourceService resourceService;

    /**
     * ResourceController
     */
    public ResourceController() {
        mediaSize = new HashMap<String, Long>();
        mediaSize.put("text", 1024 * 1024 * 2014l);
        mediaSize.put("image", 1024 * 1024 * 1024l);
        mediaSize.put("voice", 2 * 1024 * 1024 * 1024l);
        mediaSize.put("file", 2 * 1024 * 1024 * 1024l);

        contentTypes = new HashMap<String, String>();
        contentTypes.put("text/xml", "xml");
        contentTypes.put("application/xml", "xml");
        contentTypes.put("text/html", "html");
        contentTypes.put("text/plain", "txt");
        contentTypes.put("application/x-javascript", "js");
        contentTypes.put("text/css", "css");
        contentTypes.put("application/json", "json");
        contentTypes.put("image/pjpeg", "jpg");
        contentTypes.put("image/x-png", "png");
        contentTypes.put("application/x-jpg", "jpg");
        contentTypes.put("image/jpeg", "jpg");
        contentTypes.put("image/jpg", "jpg");
        contentTypes.put("application/x-png", "png");
        contentTypes.put("image/png", "png");
        contentTypes.put("image/gif", "gif");
        contentTypes.put("audio/mp3", "mp3");
        contentTypes.put("audio/amr", "amr");
        contentTypes.put("application/octet-stream", "file");
        contentTypes.put("application/vnd.ms-excel", "xls");
        contentTypes.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

        meidaTypes = new HashMap<String, MediaType>();
        meidaTypes.put("xml", MediaType.APPLICATION_XML);
        meidaTypes.put("html", MediaType.TEXT_HTML);
        meidaTypes.put("txt", MediaType.TEXT_PLAIN);
        meidaTypes.put("js", MediaType.valueOf("application/x-javascript"));
        meidaTypes.put("css", MediaType.valueOf("text/css"));
        meidaTypes.put("json", MediaType.valueOf("application/json"));
        meidaTypes.put("jpg", MediaType.IMAGE_JPEG);
        meidaTypes.put("png", MediaType.IMAGE_PNG);
        meidaTypes.put("gif", MediaType.IMAGE_GIF);
        meidaTypes.put("mp3", MediaType.valueOf("audio/mp3"));
        meidaTypes.put("amr", MediaType.valueOf("audio/amr"));
        meidaTypes.put("file", MediaType.APPLICATION_OCTET_STREAM);
        meidaTypes.put("xls", MediaType.valueOf("application/vnd.ms-excel"));
        meidaTypes.put("xlsx", MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param resourceId <br>
     * @param isThumb <br>
     * @param reqHeader <br>
     * @param response <br>
     * @return <br>
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public Object download(@RequestParam("mediaId") Integer resourceId,
        @RequestParam(value = "isThumb", required = false) String isThumb, @RequestHeader HttpHeaders reqHeader,
        HttpServletResponse response) throws Exception {
        AttachmentsPojo attachment = null;
        try {
            Assert.notNull(resourceId, "资源标识不能为空");
            attachment = resourceService.downloadResource(resourceId, "1".equals(isThumb));
        }
        catch (Exception e) {
            logger.warn(e.getMessage());
            HttpHeaders httpHeader = new HttpHeaders();
            httpHeader.set("Content-Type", "text/html;charset=utf-8");
            return new ResponseEntity<String>(e.getMessage(), httpHeader, HttpStatus.NOT_FOUND);
        }

        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.setExpires(System.currentTimeMillis() + 315360000);
        httpHeader.setLastModified(System.currentTimeMillis());
        httpHeader.setCacheControl("max-age=315360000");
        httpHeader.setContentType(getMediaType(attachment.getAttachmentsType()));
        httpHeader.setContentDispositionFormData("attachment", attachment.getAttachmentsName());

        if (reqHeader.getIfModifiedSince() > 0) {
            return new ResponseEntity<Resource>(httpHeader, HttpStatus.NOT_MODIFIED);
        }

        File file;
        if ("Y".equals(attachment.getIsPicture()) && "1".equals(isThumb)
            && StringUtils.isNotEmpty(attachment.getThumbPath())) {
            file = new File(ConfigHelper.getString(WebConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getThumbPath());
        }
        else {
            file = new File(ConfigHelper.getString(WebConstant.CONFIG_ITEM_RESOURCE_PATH) + attachment.getFilePath());
        }
        return new ResponseEntity<Resource>(new FileSystemResource(file), httpHeader, HttpStatus.OK);
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param request <br>
     * @return <br>
     * @throws FrameworkException <br>
     */
    @ResponseBody
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public Map<String, Object> upload(HttpServletRequest request) throws FrameworkException {
        if (!(request instanceof MultipartHttpServletRequest)) {
            throw new ServiceException(ErrorCodeDef.FILE_NOT_FIND_20013, "未找到上传的文件");
        }

        boolean isBase64 = StringUtils.equals(request.getParameter("base64"), "true");

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (CommonUtil.isEmpty(fileMap)) {
            throw new ServiceException(ErrorCodeDef.FILE_NOT_FIND_20013, "未找到上传的文件");
        }

        for (Map.Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
            MultipartFile file = fileEntry.getValue();
            checkFile(file.getContentType(), file.getSize());
        }

        String path = GlobalConstants.PATH_SPLITOR + DateUtil.date2String(new Date(), DateConstants.DATE_FORMAT_10_2);
        File dir = new File(ConfigHelper.getString(WebConstant.CONFIG_ITEM_RESOURCE_PATH) + path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.MINUTE, 10);
        Date expDate = calendar.getTime();

        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        try {
            int i = 0;
            for (Map.Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
                MultipartFile file = fileEntry.getValue();
                String contentType = contentTypes.get(file.getContentType());
                String type = getType(contentType);
                String fileName = GlobalConstants.PATH_SPLITOR + DateUtil.getCurrentTimestamp() + (i++) + "."
                    + contentType;

                if (isBase64) {
                    String content = IOUtil.readString(file.getInputStream());
                    byte[] fileContent = DataUtil.base64Decode(content);
                    IOUtil.writeFile(fileContent, new File(dir.getAbsolutePath() + fileName));
                }
                else {
                    IOUtil.copyFileFromInputStream(dir.getAbsolutePath() + fileName, file.getInputStream(),
                        "text".equals(type) ? GlobalConstants.DEFAULT_CHARSET : null);
                }
                results.add(saveFile(file.getOriginalFilename(), contentType, currentDate, expDate, path + fileName,
                    file.getSize()));
            }

        }
        catch (IOException e) {
            throw new ServiceException(ErrorCodeDef.READ_PARAM_ERROR_10027, e);
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("medias", results);
        return result;

    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param fileType <br>
     * @param fileSize <br>
     * @throws ServiceException <br>
     */
    private void checkFile(String fileType, Long fileSize) throws ServiceException {
        String contentType = contentTypes.get(fileType);
        if (CommonUtil.isEmpty(contentType)) {
            throw new ServiceException(ErrorCodeDef.UNSPORT_CONTENT_TYPE_20012, "文件类型不支持");
        }

        String type = getType(contentType);

        Long size = mediaSize.get(type);
        if (size == null) {
            throw new ServiceException(ErrorCodeDef.UNSPORT_MEDIA_TYPE, "不支持的媒体类型");
        }

        logger.info("接收到大小为{0}的文件" + fileSize);
        if (fileSize - size > 0) {
            throw new ServiceException(ErrorCodeDef.FILE_IS_TO_LARGER_20011, "文件大小超限");
        }
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param fileName <br>
     * @param contentType <br>
     * @param currentDate <br>
     * @param expDate <br>
     * @param path <br>
     * @param fileSize <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    private Map<String, Object> saveFile(String fileName, String contentType, Date currentDate, Date expDate,
        String path, long fileSize) throws ServiceException {
        AttachmentsPojo pojo = new AttachmentsPojo();
        pojo.setAttachmentsName(fileName);
        pojo.setAttachmentsType(contentType);
        pojo.setCreateTime(currentDate);
        pojo.setDownloadsNum(0);
        pojo.setExpTime(expDate);
        pojo.setFilePath(path);
        pojo.setFileSize(fileSize);

        String type = getType(contentType);
        pojo.setIsPicture("image".equals(type) ? "Y" : "N");
        pojo.setIsThumb("N");
        pojo.setIsRemote("N");
        resourceService.saveAttachment(pojo);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("msgtype", type);
        result.put("mediaId", pojo.getAttachmentsId());
        result.put("mediaName", fileName);
        result.put("createTime", DateUtil.date2String(currentDate, DateConstants.DATETIME_FORMAT_14));
        return result;
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @return <br>
     */
    public String getType(String type) {
        String tempType = "," + type + ",";

        if (",xml,html,json,css,js,txt,".indexOf(tempType) != -1) {
            return "text";
        }
        else if (",jpg,png,gif,".indexOf(tempType) != -1) {
            return "image";
        }
        else if (",mp3,amr,".indexOf(tempType) != -1) {
            return "voice";
        }
        else {
            return "file";
        }
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param type <br>
     * @return <br>
     */
    private MediaType getMediaType(String type) {
        if (CommonUtil.isNotEmpty(type)) {
            MediaType mediaType = meidaTypes.get(type);
            if (mediaType != null) {
                return mediaType;
            }
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * Description: <br>
     *
     * @author yang.zhipeng<br>
     * @taskId <br>
     * @param request <br>
     * @param response <br>
     */
    @ResponseBody
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        String verifyCode = CommonUtil.getRandomNumber(5);

        Cage cage = new GCage();
        OutputStream os = null;
        try {
            WebUtil.setAttribute(WebConstant.SESSION_VERIFY_CODE, verifyCode);
            os = response.getOutputStream();
            cage.draw(verifyCode, os);
        }
        catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(os);
        }
    }

    public Map<String, String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(Map<String, String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public Map<String, MediaType> getMeidaTypes() {
        return meidaTypes;
    }

    public void setMeidaTypes(Map<String, MediaType> meidaTypes) {
        this.meidaTypes = meidaTypes;
    }
}
