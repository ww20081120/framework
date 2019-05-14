/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.URLUtil;
import com.hbasesoft.framework.common.utils.engine.VelocityParseFactory;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.wechat.bean.MediaUploadPojo;
import com.hbasesoft.framework.wechat.bean.MediatemplatePojo;
import com.hbasesoft.framework.wechat.bean.MenuentityPojo;
import com.hbasesoft.framework.wechat.bean.NewsitemPojo;
import com.hbasesoft.framework.wechat.bean.QrcodeParamsPojo;
import com.hbasesoft.framework.wechat.bean.TexttemplatePojo;
import com.hbasesoft.framework.wechat.bean.resp.Article;
import com.hbasesoft.framework.wechat.bean.resp.Image;
import com.hbasesoft.framework.wechat.bean.resp.ImageMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.NewsMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.TextMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.Video;
import com.hbasesoft.framework.wechat.bean.resp.VideoMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.Voice;
import com.hbasesoft.framework.wechat.bean.resp.VoiceMessageResp;
import com.hbasesoft.framework.wechat.dao.WechatDao;
import com.hbasesoft.framework.wechat.util.WechatUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年6月4日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.actsports.portal.service.impl <br>
 */
public abstract class AbstractMessageHandler implements WechatMessageHandler, ApplicationContextAware {

    @Resource
    protected WechatDao wechatDao;

    protected ApplicationContext applicationContext;

    private static final String QRCODE_URL = "bindingSite";
    
    private static final String REPLACE_URL ="siteBinding/addSite";
//    @Value("${server.image.url}")
//    private String imagePath;
//
//    @Value("${server.wx.url}")
//    private String serverPath;

    protected String getTextMsg(String templateId, String fromUserName, String toUserName, String eventKey, String appId) throws ServiceException {
        TexttemplatePojo textTemplate = wechatDao.get(TexttemplatePojo.class, templateId);
        String content = textTemplate.getContent();
        TextMessageResp textMessage = new TextMessageResp();
        textMessage.setToUserName(toUserName);
        textMessage.setFromUserName(fromUserName);
        textMessage.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setCreateTime(new Date().getTime());
        if(CommonUtil.isNotEmpty(eventKey)){
        	//判断是否为地址二维码
        	String addrId = StringUtils.substringAfterLast(eventKey, "ADDR_");
    		//如果是地址，则替换欢迎语
    		if(CommonUtil.isNotEmpty(addrId)){
    			QrcodeParamsPojo qrcodeParamsPojo = wechatDao.getEntity(QrcodeParamsPojo.class, addrId);
    			//如果地址二维码信息存在，则替换关注欢迎语中的关键字
    			if(!CommonUtil.isNull(qrcodeParamsPojo)){
    				//解析json数据
    				JSONObject obj = JSONObject.parseObject(qrcodeParamsPojo.getDatas());
    				
					Map<String, String> map = new HashMap<String, String>();
					//URL中不能出现中文 所以需要转码
					map.put("gardenName", URLUtil.encode(obj.getString("gardenName")));
					map.put("addrId", URLUtil.encode(obj.getString("attrId")));
					map.put("gardenCode", URLUtil.encode(obj.getString("gardenCode")));
					map.put("orgCode", URLUtil.encode(obj.getString("orgCode")));
					map.put("shortName", URLUtil.encode(obj.getString("shortName")));
					map.put("wxAppId", appId);
					//不在url中出现的无需转码
					map.put("garden", obj.getString("gardenName"));
					
					content = VelocityParseFactory.parse("kfMessage", content, map);
    			} 
    		}
        } 
        textMessage.setContent(content);
        return WechatUtil.textMessageToXml(textMessage);
    }

	protected String getImageMsg(MenuentityPojo entity, String fromUserName, String toUserName)
        throws ServiceException {
        String templateId = entity.getTemplateid();
        String accountId = entity.getAccountid();
        List<MediaUploadPojo> weixinFiletemplateMedia = wechatDao.findByProperty(MediaUploadPojo.class,
            "filetemplateId", templateId);
        MediaUploadPojo weixinFiletemplateMediaPojo = null;
        String mediaId = null;
        for (MediaUploadPojo w : weixinFiletemplateMedia) {
            if (accountId.equals(w.getAccountid())) {
                weixinFiletemplateMediaPojo = w;
            }
        }
        if (null != weixinFiletemplateMediaPojo) {
            mediaId = weixinFiletemplateMediaPojo.getMediaId();
        }

        Image image = new Image();
        image.setMediaId(mediaId);

        ImageMessageResp imageMessage = new ImageMessageResp();
        imageMessage.setImage(image);
        imageMessage.setToUserName(toUserName);
        imageMessage.setFromUserName(fromUserName);
        imageMessage.setMsgType(WechatUtil.REQ_MESSAGE_TYPE_IMAGE);
        imageMessage.setCreateTime(new Date().getTime());

        return WechatUtil.imageMessageToXml(imageMessage);
    }

    protected String getVoiceMsg(MenuentityPojo entity, String fromUserName, String toUserName)
        throws ServiceException {
        String templateId = entity.getTemplateid();
        String accountId = entity.getAccountid();
        List<MediaUploadPojo> weixinFiletemplateMedia = wechatDao.findByProperty(MediaUploadPojo.class,
            "filetemplateId", templateId);
        MediaUploadPojo weixinFiletemplateMediaPojo = null;
        String mediaId = null;
        for (MediaUploadPojo w : weixinFiletemplateMedia) {
            if (accountId.equals(w.getAccountid())) {
                weixinFiletemplateMediaPojo = w;
            }
        }
        if (null != weixinFiletemplateMediaPojo) {
            mediaId = weixinFiletemplateMediaPojo.getMediaId();
        }
        Voice voice = new Voice();
        voice.setMediaId(mediaId);

        VoiceMessageResp voiceMessageResp = new VoiceMessageResp();
        voiceMessageResp.setVoice(voice);
        voiceMessageResp.setToUserName(toUserName);
        voiceMessageResp.setFromUserName(fromUserName);
        voiceMessageResp.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_VOICE);
        voiceMessageResp.setCreateTime(new Date().getTime());

        return WechatUtil.voiceMessageToXml(voiceMessageResp);
    }

    protected String getVideoMsg(MenuentityPojo entity, String fromUserName, String toUserName)
        throws ServiceException {
        String templateId = entity.getTemplateid();
        String accountId = entity.getAccountid();
        List<MediaUploadPojo> weixinFiletemplateMedia = wechatDao.findByProperty(MediaUploadPojo.class,
            "filetemplateId", templateId);
        MediaUploadPojo weixinFiletemplateMediaPojo = null;
        String mediaId = null;
        for (MediaUploadPojo w : weixinFiletemplateMedia) {
            if (accountId.equals(w.getAccountid())) {
                weixinFiletemplateMediaPojo = w;
            }
        }
        if (null != weixinFiletemplateMediaPojo) {
            mediaId = weixinFiletemplateMediaPojo.getMediaId();
        }

        MediatemplatePojo weixinFiletemplatePojo = wechatDao.findUniqueByProperty(MediatemplatePojo.class, "id",
            templateId);

        Video video = new Video();
        video.setMediaId(mediaId);
        video.setTitle(weixinFiletemplatePojo.getTempldatename());
        // video.setDescription(weixinFiletemplatePojo.getIntroduction());

        VideoMessageResp videoMessageResp = new VideoMessageResp();
        videoMessageResp.setVideo(video);
        videoMessageResp.setToUserName(toUserName);
        videoMessageResp.setFromUserName(fromUserName);
        videoMessageResp.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_VIDEO);
        videoMessageResp.setCreateTime(new Date().getTime());

        return WechatUtil.videoMessageToXml(videoMessageResp);
    }

    protected String getTextMsgByContent(String content, String fromUserName, String toUserName) {
        TextMessageResp textMessage = new TextMessageResp();
        textMessage.setToUserName(toUserName);
        textMessage.setFromUserName(fromUserName);
        textMessage.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setContent(content);
        return WechatUtil.textMessageToXml(textMessage);
    }

    protected String getTextMsg(String accountId, String templateName, String fromUserName, String toUserName)
        throws ServiceException {
        DetachedCriteria criteria = DetachedCriteria.forClass(TexttemplatePojo.class);
        criteria.add(Restrictions.eq(TexttemplatePojo.ACCOUNT_ID, accountId));
        criteria.add(Restrictions.eq(TexttemplatePojo.TEMPLATE_NAME, templateName));

        TexttemplatePojo textTemplate = wechatDao.getCriteriaQuery(criteria);
        String content = textTemplate.getContent();
        TextMessageResp textMessage = new TextMessageResp();
        textMessage.setToUserName(toUserName);
        textMessage.setFromUserName(fromUserName);
        textMessage.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setCreateTime(new Date().getTime());
        textMessage.setContent(content);
        return WechatUtil.textMessageToXml(textMessage);
    }

	protected String getNewsItem(String templateId, String fromUserName, String toUserName, String imagePath,
			String serverPath, String eventKey, String appId) throws ServiceException {
        List<NewsitemPojo> newsList = wechatDao.findByProperty(NewsitemPojo.class, NewsitemPojo.TEMPLATE_ID,
            templateId); // wechatTemplateService.selectNewsItemByTemplateId(templateId);
        List<Article> articleList = new ArrayList<Article>();
        
        for (NewsitemPojo news : newsList) {
        	Article article = new Article();
        	
        	String url = news.getUrl();
        	String description = news.getDescription();
        	String addrId = null;
        	String title = news.getTitle();
        	if(CommonUtil.isNotEmpty(eventKey)){
            	//判断是否为地址二维码（为地址二维码时应为单图文消息）
            	addrId = StringUtils.substringAfterLast(eventKey, "ADDR_");
        		//如果是地址，则替换欢迎语
        		if(CommonUtil.isNotEmpty(addrId)){
        			QrcodeParamsPojo qrcodeParamsPojo = wechatDao.getEntity(QrcodeParamsPojo.class, addrId);
        			//如果地址二维码信息存在，则替换关注欢迎语中的关键字
        			if(!CommonUtil.isNull(qrcodeParamsPojo)){
        				//解析json数据
        				JSONObject obj = JSONObject.parseObject(qrcodeParamsPojo.getDatas());
        				
    					Map<String, String> map = new HashMap<String, String>();
    					//URL中不能出现中文 所以需要转码
    					map.put("gardenName", URLUtil.encode(obj.getString("gardenName")));
    					map.put("addrId", URLUtil.encode(obj.getString("attrId")));
    					map.put("gardenCode", URLUtil.encode(obj.getString("gardenCode")));
    					map.put("orgCode", URLUtil.encode(obj.getString("orgCode")));
    					map.put("shortName", URLUtil.encode(obj.getString("shortName")));
    					map.put("wxAppId", appId);
    					//不在url中出现的无需转码
    					map.put("garden", obj.getString("gardenName"));
    					
    					url = VelocityParseFactory.parse("kfMessage", url, map);
    					description = VelocityParseFactory.parse("kfMessage", description, map);
    					title = VelocityParseFactory.parse("kfMessage", title, map);
        			} 
        		}
            }
        	
        	//如果没有url的情况下跟原来一样处理
            if (CommonUtil.isNotEmpty(url)) {
            	//如果有url 1：关注事件的推送url没有小区二维码url直接推送 2：扫码事件中必须是小区二维码才推送
            	if (-1 != url.indexOf(QRCODE_URL) && 
            			CommonUtil.isNotEmpty(eventKey) && eventKey.indexOf("ADDR_") == -1) {
            		return null;
                } 
            } 
        	
        	article.setPicUrl(imagePath + "/" + news.getImagepath());
            if (CommonUtil.isNotEmpty(news.getContent()) || CommonUtil.isEmpty(news.getUrl())) {
                url = serverPath + "/article/" + news.getId();
            }
            if(CommonUtil.isEmpty(addrId) && url.indexOf(QRCODE_URL) != -1){
                Map<String, String> map = new HashMap<String, String>();
                //URL涓­涓嶈兘鍑虹幇涓­鏂 鎵€浠ラ渶瑕佽浆鐮
                map.put("gardenName", GlobalConstants.BLANK);
                map.put("addrId", GlobalConstants.BLANK);
                map.put("gardenCode", GlobalConstants.BLANK);
                map.put("orgCode", GlobalConstants.BLANK);
                map.put("shortName", GlobalConstants.BLANK);
                map.put("wxAppId", GlobalConstants.BLANK);
                map.put("garden", GlobalConstants.BLANK);
                
                url = url.substring(0, url.indexOf(QRCODE_URL)) +REPLACE_URL;
                description = VelocityParseFactory.parse("kfMessage", description, map);
                title = VelocityParseFactory.parse("kfMessage", title, map);
            }
            article.setTitle(title);
            article.setUrl(url);
            article.setDescription(description);
            articleList.add(article);
        }
        NewsMessageResp newsResp = new NewsMessageResp();
        newsResp.setCreateTime(new Date().getTime());
        newsResp.setToUserName(toUserName);
        newsResp.setFromUserName(fromUserName);
        newsResp.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_NEWS);
        newsResp.setArticleCount(articleList.size());
        newsResp.setArticles(articleList);
        return WechatUtil.newsMessageToXml(newsResp);
    }

    /*protected String getExpendMsg(String templateId, String msgId, String toUserName, AccountPojo entity,
        String content, Map<String, String> requestMap, String imagePath, String serverPath) throws FrameworkException {

        ExpandconfigPojo expandconfigEntity = wechatDao.get(ExpandconfigPojo.class, templateId);
        String className = expandconfigEntity.getClassname();
        WechatMessageHandler messageHandler = applicationContext.getBean(className, WechatMessageHandler.class);
        if (messageHandler != null) {
            return messageHandler.process(msgId, toUserName, entity, content, requestMap, imagePath, serverPath);
        }
        return null;
    }

    protected String getExpendMsg(ExpandconfigPojo expandconfigEntity, String msgId, String toUserName,
        AccountPojo entity, String content, Map<String, String> requestMap, String imagePath, String serverPath) throws FrameworkException {

        String className = expandconfigEntity.getClassname();
        WechatMessageHandler messageHandler = applicationContext.getBean(className, WechatMessageHandler.class);
        if (messageHandler != null) {
            return messageHandler.process(msgId, toUserName, entity, content, requestMap, imagePath, serverPath);
        }
        return null;
    }*/

    protected boolean matchKeyword(String key, String content) {
        if (StringUtils.startsWith(key, "^")) {
            return content.matches(key);
        }
        else {
            return StringUtils.equals(key, content);
        }
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param applicationContext
     * @throws BeansException <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
