/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.wechat.bean.MediaUploadPojo;
import com.hbasesoft.framework.wechat.bean.MediatemplatePojo;
import com.hbasesoft.framework.wechat.bean.MenuentityPojo;
import com.hbasesoft.framework.wechat.bean.NewsitemPojo;
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

//    @Value("${server.image.url}")
//    private String imagePath;
//
//    @Value("${server.wx.url}")
//    private String serverPath;

    protected String getTextMsg(String templateId, String fromUserName, String toUserName) throws ServiceException {
        TexttemplatePojo textTemplate = wechatDao.get(TexttemplatePojo.class, templateId);
        String content = textTemplate.getContent();
        TextMessageResp textMessage = new TextMessageResp();
        textMessage.setToUserName(toUserName);
        textMessage.setFromUserName(fromUserName);
        textMessage.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_TEXT);
        textMessage.setCreateTime(new Date().getTime());
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

    protected String getNewsItem(String templateId, String fromUserName, String toUserName, String imagePath, String serverPath) throws ServiceException {

        List<NewsitemPojo> newsList = wechatDao.findByProperty(NewsitemPojo.class, NewsitemPojo.TEMPLATE_ID,
            templateId); // wechatTemplateService.selectNewsItemByTemplateId(templateId);
        List<Article> articleList = new ArrayList<Article>();
        for (NewsitemPojo news : newsList) {
            Article article = new Article();
            article.setTitle(news.getTitle());
            article.setPicUrl(imagePath + "/" + news.getImagepath());
            String url = news.getUrl();
            if (CommonUtil.isNotEmpty(news.getContent()) || CommonUtil.isEmpty(news.getUrl())) {
                url = serverPath + "/article/" + news.getId();
            }
            article.setUrl(url);
            article.setDescription(news.getDescription());
            articleList.add(article);
        }
        NewsMessageResp newsResp = new NewsMessageResp();
        newsResp.setCreateTime(new Date().getTime());
        newsResp.setToUserName(toUserName);
        newsResp.setFromUserName(fromUserName);
        newsResp.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_NEWS);
        newsResp.setArticleCount(newsList.size());
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
