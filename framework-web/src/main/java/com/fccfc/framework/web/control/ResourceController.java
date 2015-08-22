/**
 * 
 */
package com.fccfc.framework.web.control;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.FrameworkException;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.Assert;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.date.DateConstants;
import com.fccfc.framework.common.utils.date.DateUtil;
import com.fccfc.framework.common.utils.io.IOUtil;
import com.fccfc.framework.common.utils.logger.Logger;
import com.fccfc.framework.common.utils.security.DataUtil;
import com.fccfc.framework.config.core.Configuration;
import com.fccfc.framework.message.core.bean.AttachmentsPojo;
import com.fccfc.framework.web.WebConstant;
import com.fccfc.framework.web.service.ResourceService;
//import com.qiniu.api.auth.digest.Mac;
//import com.qiniu.api.config.Config;
//import com.qiniu.api.rs.PutPolicy;
import com.github.cage.Cage;
import com.github.cage.GCage;

/**
 * <Description> <br>
 * 
 * @author 伟<br>
 * @version 1.0<br>
 * @CreateDate 2014-11-30 <br>
 * @see com.fccfc.framework.web.control <br>
 */
@Controller
@RequestMapping("/resource")
public class ResourceController {

    /**
     * NO_IMAGE
     */
    private static final String NO_IMAGE = "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";

    private static byte[] noImageArr;

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
    }

    /**
     * Description: <br>
     * 
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param resourceId <br>
     * @param isThumb <br>
     * @param reqHeader <br>
     * @return <br>
     * @throws Exception <br>
     */
    @ResponseBody
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public Object download(@RequestParam("mediaId") Integer resourceId,
        @RequestParam(value = "isThumb", required = false) String isThumb, @RequestHeader HttpHeaders reqHeader,
        HttpServletResponse response) throws Exception {
        Assert.notNull(resourceId, "资源标识不能为空");
        AttachmentsPojo attachment = null;
        try {
            attachment = resourceService.downloadResource(resourceId, "1".equals(isThumb));
        }
        catch (Exception e) {
            logger.warn(e.getMessage(), e);
            if (noImageArr == null) {
                noImageArr = DataUtil.base64Decode(NO_IMAGE);
            }
            response.getOutputStream().write(noImageArr);
            return null;
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
            file = new File(Configuration.getString("RESOURCE.PATH") + attachment.getThumbPath());
        }
        else {
            file = new File(Configuration.getString("RESOURCE.PATH") + attachment.getFilePath());
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

        for (Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
            MultipartFile file = fileEntry.getValue();
            checkFile(file.getContentType(), file.getSize());
        }

        String path = GlobalConstants.PATH_SPLITOR + DateUtil.date2String(new Date(), DateConstants.DATE_FORMAT_10_2);
        File dir = new File(Configuration.getString("RESOURCE.PATH") + path);
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
            for (Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
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

                results
                    .add(saveFile(file.getName(), contentType, currentDate, expDate, path + fileName, file.getSize()));
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
            throw new ServiceException(ErrorCodeDef.UNSPORT_MEDIA_TYPE_20010, "不支持的媒体类型");
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
     * @return <br>
     */
    @ResponseBody
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg");
        String verifyCode = CommonUtil.getRandomNumber(5);

        Cage cage = new GCage();
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            cage.draw(verifyCode, os);
            request.getSession().setAttribute(WebConstant.SESSION_VERIFY_CODE, verifyCode);
        }
        catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException e) {
                    logger.error("OutputStream Error！", e);
                }
            }
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
