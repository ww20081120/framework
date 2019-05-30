/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.bean.AutoresponsePojo;
import com.hbasesoft.framework.wechat.bean.ChangeQrcodeParamPojo;
import com.hbasesoft.framework.wechat.bean.ExpandconfigPojo;
import com.hbasesoft.framework.wechat.bean.OpenapiChannelPojo;
import com.hbasesoft.framework.wechat.bean.msg.UnifiedOrderResult;
import com.hbasesoft.framework.wechat.bean.msg.WXResult;
import com.hbasesoft.framework.wechat.bean.msg.WxMessage;
import com.hbasesoft.framework.wechat.bean.msg.WxTemplate;
import com.hbasesoft.framework.wechat.vo.WechatAccount;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat.service <br>
 */
public interface WechatService {

    /**
     * Description: 微信接入服务<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return <br>
     */
    String wechatAccess(String accountId, String signature, String timestamp, String nonce, String echostr);

    /**
     * Description: 第三方微信接入服务<br>
     * 
     * @param openapiChannelId
     * @param token
     * @return
     */
    String checkSignature(String openapiChannelId);

    /**
     * Description: 微信接口服务 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message
     * @param accountId
     * @return <br>
     */
    String coreService(String accountId, String message, String imagePath, String serverPath);

    /**
     * Description: 根据请求的url， 匹配出是哪个微信<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param url
     * @return <br>
     */
    AccountPojo matchAccount(String url);

    /**
     * Description: 获取微信accessToken<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param accountId
     * @return <br>
     */
    List<AccountPojo> getAllAccessToken();

    /**
     * Description: 获取微信accessTokenList<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param accountId
     * @return <br>
     */
    String getAccessToken(String appId);

    /**
     * Description: 刷新accessToken<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param accountId <br>
     */
    String refreshAccessToken(String appId, String appKey);

    /**
     * Description: 获取jsApiTicket<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    String getJsApiTicket(String appId);

    /**
     * Description: 刷新jsapi ticket<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param accountId <br>
     * @return
     */
    String refreshJsapiTicket(String accessToken);

    /**
     * Description: 通过appid,AppSecret,code得到openId<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param code
     * @param appId
     * @param appKey
     * @return <br>
     */
    String getOpenId4Oauth2(String code, String appId, String appKey);

    /**
     * Description: 文件下载<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param accountId
     * @param absolutePath
     * @param media_id
     * @return <br>
     */
    String mediaDown(String accessToken, String absolutePath, String media_id);

    /**
     * Description: 微信支付下订单 <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param paramMap
     * @return <br>
     */
    UnifiedOrderResult createWechatOrder(String key, Map<String, String> paramMap);

    /**
     * Description: 退费<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param paramMap
     * @param p12Path
     * @return <br>
     */
    Map<String, String> refund(String key, Map<String, String> paramMap, String p12Path);

    /**
     * Description: 根据appid获得微信account<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param key
     * @param paramMap
     * @param p12Path
     * @return <br>
     */
    WechatAccount getAccountByappId(String appId);

    OpenapiChannelPojo getOpenapiChannelByAppId(String appId);

    OpenapiChannelPojo getOpenapiChannelByAppIdAndeAppSecret(String appId, String secret);

    OpenapiChannelPojo getOpenapiChannelByAccessToken(String accessToken);

    // String getOpenId4Oauth2(String code,String url);

    // String getOauth2Url2(String url,String redirectURI, String expendParam, String scope, String serverName);

    WXResult sendMessageByTemplate(WxTemplate wxTemplate, String appId);
    
    /**
     * 
     * Description: 获取永久推广二维码<br> 
     *  
     * @author 查思玮<br>
     * @taskId <br>
     * @param sceneStr:场景值ID（字符串形式的ID），字符串类型，长度限制为1到64  
     * @param accessToken
     * @return <br>
     */
    String getSpreadQcUrl(String sceneStr, String accessToken);
    
    /**
     * 
     * Description: 获取临时二维码<br> 
     *  
     * @author liu.xianan<br>
     * @taskId <br>
     * @param sceneStr:场景值ID（字符串形式的ID），字符串类型，长度限制为1到28 
     * @param accessToken
     * @return <br>
     */
    String getTempSpreadQcUrl(String sceneStr, String accessToken);

    WXResult sendMessage(WxMessage wxMessage,String accessToken);

    List<ExpandconfigPojo> queryAllExpandConfig();
    
    AccountPojo getAccountById(String id);
    
    JSONObject getUserInfoMessage(String openId,String appId);
    
    /**
     * 
     * Description:获取自动回复内容 <br> 
     *  
     * @author liuxianan<br>
     * @taskId <br>
     * @param accountId
     * @param content
     * @return <br>
     */
    AutoresponsePojo getAutoResponse(String accountId, String content);
    
    /**
     * 
     * Description: <br> 
     *  
     * @author zhasiwei<br>
     * @taskId <br>
     * @param orgCode
     * @param appId
     * @return
     * @throws ServiceException <br>
     */
    ChangeQrcodeParamPojo getChangeQrcodeParamPojo(String orgCode,String appId, String usedFlag) throws ServiceException;
	
}
