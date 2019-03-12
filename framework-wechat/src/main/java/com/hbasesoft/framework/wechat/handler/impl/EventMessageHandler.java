/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.handler.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jasypt.commons.CommonUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.wechat.WechatEventCodeDef;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.bean.MenuentityPojo;
import com.hbasesoft.framework.wechat.bean.SubscribePojo;
import com.hbasesoft.framework.wechat.handler.AbstractMessageHandler;
import com.hbasesoft.framework.wechat.handler.WechatMessageHandler;
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
@Service("eventMessageHandler")
public class EventMessageHandler extends AbstractMessageHandler {

    private static Logger logger = new Logger(EventMessageHandler.class);

    @Resource(name = "locationMessageHandler")
    private WechatMessageHandler locationMessageHandler;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgId
     * @param toUserName
     * @param entity
     * @param content
     * @return
     * @throws VccException <br>
     */
    @Override
    public String process(String msgId, String toUserName, AccountPojo entity, String content,
        Map<String, String> requestMap, String imagePath, String serverPath, String message) throws FrameworkException {
        String respMessage = null;
        // 事件类型
        String eventType = requestMap.get("Event");
        
        logger.info("------------微信客户端发送请求------------------【微信触发类型 " + eventType + "】事件推送---");

        // 订阅
        if (WechatUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)) {
            respMessage = doSubscribeResponse(requestMap, toUserName, entity.getWeixinAccountid(), entity.getId(), entity.getAccountappid(),imagePath, serverPath);
        }
        // 取消订阅
        else if (WechatUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)) {
            // TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
        }
        // 自定义菜单点击事件
        else if (WechatUtil.EVENT_TYPE_CLICK.equals(eventType)) {
            respMessage = doMenuEvent(msgId, toUserName, entity, content, requestMap, imagePath, serverPath);
        }
        else if (WechatUtil.EVENT_TYPE_LOCATION.equals(eventType)) {
            respMessage = locationMessageHandler.process(msgId, toUserName, entity, content, requestMap, imagePath, serverPath, message);
        } else if (WechatUtil.EVENT_TYPE_SCAN.equals(eventType)) {
        	respMessage = doScanResponse(requestMap, toUserName, entity.getWeixinAccountid(), entity.getId(), entity.getAccountappid(),imagePath, serverPath);
        }

        return respMessage;
    }

    /**
     * 针对事件消息
     * 
     * @param requestMap
     * @param textMessage
     * @param bundler
     * @param respMessage
     * @param toUserName
     * @param fromUserName
     * @throws FrameworkException
     */
    private String doSubscribeResponse(Map<String, String> requestMap, String toUserName, String fromUserName,
        String accountId, String appId,String imagePath, String serverPath) throws FrameworkException {
        String respMessage = null;
        //获取事件中的值
        String eventKey = requestMap.get("EventKey");
        List<SubscribePojo> lst = null;
        //燃气管家扫描小区二维码没有关注的情况下会进关注事件,但是需要推送扫码的消息
        if (CommonUtil.isNotEmpty(eventKey) && eventKey.indexOf("ADDR_") != -1) {
        	lst = querySubscribeOrScanList(accountId, SubscribePojo.SCAN);
        } else {
        	lst = querySubscribeOrScanList(accountId, SubscribePojo.SUBSCRIBE);
        }
        
        
        if (lst.size() != 0) {
            SubscribePojo subscribe = lst.get(0);
            String type = subscribe.getMsgtype();
            if (WechatUtil.REQ_MESSAGE_TYPE_TEXT.equals(type)) {
                respMessage = getTextMsg(subscribe.getTemplateid(), fromUserName, toUserName, eventKey, appId);
            }
            else if (WechatUtil.RESP_MESSAGE_TYPE_NEWS.equals(type)) {
                respMessage = getNewsItem(subscribe.getTemplateid(), fromUserName, toUserName, imagePath, serverPath, eventKey, appId);
            }
        }

        if (CommonUtils.isNotEmpty(toUserName)) {
            //String paramId = StringUtils.substringAfterLast(enventKey, "VCC_");
            logger.info("用户首次关注 openId[{0}，appId{1}] 注册账号，paramId[{2}]", toUserName,appId, eventKey);
            
            EventData data = new EventData();
            data.put("openid", toUserName);
            data.put("appId", appId);
            data.put("paramId", eventKey);
            EventEmmiter.emmit(WechatEventCodeDef.WECHAT_SUBSCRIBER, data);
        }
        
        return respMessage;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param msgId
     * @param toUserName
     * @param entity
     * @param content
     * @param requestMap
     * @return
     * @throws ServiceException
     */
    String doMenuEvent(String msgId, String toUserName, AccountPojo entity, String content,
        Map<String, String> requestMap, String imagePath, String serverPath) throws FrameworkException {
        String fromUserName = entity.getWeixinAccountid();
        String respMessage = null;
        String key = requestMap.get("EventKey");
        // 自定义菜单CLICK类型
        DetachedCriteria criteria = DetachedCriteria.forClass(MenuentityPojo.class);
        criteria.add(Restrictions.eq(MenuentityPojo.ACCOUNT_ID, entity.getId()));
        criteria.add(Restrictions.eq(MenuentityPojo.MENU_KEY, key));
        MenuentityPojo menuEntity = wechatDao.getCriteriaQuery(criteria);

        if (menuEntity != null && CommonUtil.isNotEmpty(menuEntity.getTemplateid())) {
            String type = menuEntity.getMsgtype();
            if (WechatUtil.RESP_MESSAGE_TYPE_TEXT.equals(type)) {
                respMessage = getTextMsg(menuEntity.getTemplateid(), fromUserName, toUserName, null, null);
            }
            else if (WechatUtil.RESP_MESSAGE_TYPE_NEWS.equals(type)) {
                respMessage = getNewsItem(menuEntity.getTemplateid(), fromUserName, toUserName, imagePath, serverPath, null, null);
            }
            else if (WechatUtil.RESP_MESSAGE_TYPE_IMAGE.equals(type)) {
                respMessage = getImageMsg(menuEntity, fromUserName, toUserName);
            }
            else if (WechatUtil.RESP_MESSAGE_TYPE_VOICE.equals(type)) {
                respMessage = getVoiceMsg(menuEntity, fromUserName, toUserName);
            }
            else if (WechatUtil.RESP_MESSAGE_TYPE_VIDEO.equals(type)) {
                respMessage = getVideoMsg(menuEntity, fromUserName, toUserName);
            }
//            else if ("expand".equals(type)) {
//                respMessage = getExpendMsg(menuEntity.getTemplateid(), msgId, toUserName, entity, content, requestMap, imagePath, serverPath);
//            }
        }
        return respMessage;
    }
    
    /**
     * 针对关注用户扫描带参二维码处理
     * 
     * @param requestMap
     * @param textMessage
     * @param bundler
     * @param respMessage
     * @param toUserName
     * @param fromUserName
     * @throws FrameworkException
     */
    private String doScanResponse(Map<String, String> requestMap, String toUserName, String fromUserName,
        String accountId, String appId,String imagePath, String serverPath) throws FrameworkException {
    	
		 String respMessage = null;
		 //获取事件中的值
		 String eventKey = requestMap.get("EventKey");
		 List<SubscribePojo> lst = querySubscribeOrScanList(accountId, SubscribePojo.SCAN);
		
		 if (lst.size() != 0) {
		     SubscribePojo subscribe = lst.get(0);
		     String type = subscribe.getMsgtype();
		     if (WechatUtil.REQ_MESSAGE_TYPE_TEXT.equals(type)) {
		         respMessage = getTextMsg(subscribe.getTemplateid(), fromUserName, toUserName, eventKey, appId);
		     }
		     else if (WechatUtil.RESP_MESSAGE_TYPE_NEWS.equals(type)) {
		         respMessage = getNewsItem(subscribe.getTemplateid(), fromUserName, toUserName, imagePath, serverPath, eventKey, appId);
		     }
		 }
        if (CommonUtils.isNotEmpty(toUserName)) {
//            String paramId = StringUtils.substringAfterLast(enventKey, "VCC_");
            logger.info("关注用户扫描带参二维码处理 openId[{0}，appId{1}] 注册账号，paramId[{2}]", toUserName,appId, eventKey);
            
            EventData data = new EventData();
            data.put("openid", toUserName);
            data.put("appId", appId);
            data.put("paramId", eventKey);
            EventEmmiter.emmit(WechatEventCodeDef.WECHAT_SCAN, data);
        }
        
        return respMessage;
    }

	private List<SubscribePojo> querySubscribeOrScanList(String accountId, String dataType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SubscribePojo.class);
		 criteria.add(Restrictions.eq(SubscribePojo.ACCOUNT_ID, accountId));
		 criteria.add(Restrictions.eq(SubscribePojo.DATA_TYPE, dataType));
		 List<SubscribePojo> lst = wechatDao.getListByCriteriaQuery(criteria);
		return lst;
	}

	@Override
	public void asynProcess(String msgId, String toUserName, AccountPojo entity, String content,
			Map<String, String> requestMap, String imagePath, String serverPath, String message) throws FrameworkException {
	}
}
