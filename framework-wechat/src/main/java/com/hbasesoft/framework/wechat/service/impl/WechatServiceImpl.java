/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.cache.core.annotation.Cache;
import com.hbasesoft.framework.cache.core.annotation.Key;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.Assert;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.ContextHolder;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.bean.JsonUtil;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.io.HttpUtil;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.xml.XmlBeanUtil;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.wechat.CacheCodeDef;
import com.hbasesoft.framework.wechat.ErrorCodeDef;
import com.hbasesoft.framework.wechat.WechatConstant;
import com.hbasesoft.framework.wechat.WechatEventCodeDef;
import com.hbasesoft.framework.wechat.bean.AccountPojo;
import com.hbasesoft.framework.wechat.bean.AutoresponsePojo;
import com.hbasesoft.framework.wechat.bean.ExpandconfigPojo;
import com.hbasesoft.framework.wechat.bean.OpenapiChannelPojo;
import com.hbasesoft.framework.wechat.bean.msg.UnifiedOrderResult;
import com.hbasesoft.framework.wechat.bean.msg.WXResult;
import com.hbasesoft.framework.wechat.bean.msg.WxMessage;
import com.hbasesoft.framework.wechat.bean.msg.WxTemplate;
import com.hbasesoft.framework.wechat.bean.resp.TextMessageResp;
import com.hbasesoft.framework.wechat.dao.WechatDao;
import com.hbasesoft.framework.wechat.executor.WechatExpandExecutor;
import com.hbasesoft.framework.wechat.executor.WechatThreadPoolExecutor;
import com.hbasesoft.framework.wechat.handler.WechatMessageHandler;
import com.hbasesoft.framework.wechat.service.WechatService;
import com.hbasesoft.framework.wechat.util.SignUtil;
import com.hbasesoft.framework.wechat.util.WechatUtil;
import com.hbasesoft.framework.wechat.util.wechatSDK.WXPayUtil;
import com.hbasesoft.framework.wechat.vo.WechatAccount;

/**
 * <Description> <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月17日 <br>
 * @see com.hbasesoft.framework.wechat.service.impl <br>
 * @since V1.0<br>
 */
@SuppressWarnings("deprecation")
@Service("WechatServiceImpl")
public class WechatServiceImpl implements WechatService {

    @Resource
    private WechatDao wechatDao;

    private final WechatThreadPoolExecutor threadPoolExecutor = new WechatThreadPoolExecutor();

    /**
     * Description: <br>
     *
     * @param msgId
     * @param fromUserName
     * @param toUserName
     * @param msgType
     * @param content
     * @return
     * @throws ServiceException
     * @throws VccException <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public String coreService(String accountId, String message, String imagePath, String serverPath)
        throws ServiceException {
        LoggerUtil.info("------------微信客户端发送请求---------------------  {0}", message);
        Map<String, String> requestMap = WechatUtil.parseXml(message);
        if (CommonUtil.isEmpty(requestMap)) {
            return "";
        }

        // 发送方帐号（open_id）
        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
        String msgType = requestMap.get("MsgType");
        String msgId = requestMap.get("MsgId");
        // 消息内容
        String content = requestMap.get("Content");
        AccountPojo account = wechatDao.getEntity(AccountPojo.class, accountId);
        if (account == null) {
            return null;
        }

        String sys_accountId = account.getId();
        LoggerUtil.info("-sys_accountId--------" + sys_accountId);

        TextMessageResp textMessage = null;
        WechatMessageHandler handler = ContextHolder.getContext().getBean(msgType + "MessageHandler",
            WechatMessageHandler.class);
        String respMsg = null;

        // if (null != openApiPushMessageHandler) {
        // // 采用异步消息方式推送给第三方
        // openApiPushMessageHandler.asynProcess(msgId, fromUserName, account, content, requestMap, imagePath,
        // serverPath, message);
        // }

        if (handler != null) {
            try {
                respMsg = handler.process(msgId, fromUserName, account, content, requestMap, imagePath, serverPath,
                    message);
                // 异步消息
                EventData data = new EventData();
                data.put("msgId", msgId);
                data.put("fromUserName", fromUserName);
                data.put("content", content);
                data.put("message", message);
                data.put("imagePath", imagePath);
                data.put("serverPath", serverPath);
                data.put("accountId", accountId);
                threadPoolExecutor.execute(new WechatExpandExecutor(data));
                // WechatUtil.asyncWechatExpand(msgId, fromUserName, content,
                // imagePath, serverPath, message, accountId);
                // for (ExpandconfigPojo expandConfig : weixinExpandconfigEntityLst) {
                // expandHandler = ContextHolder.getContext().getBean(expandConfig.getClassname(),
                // WechatMessageHandler.class);
                // if (handler != null && expandHandler != null) {
                // expandHandler.asynProcess(msgId, fromUserName, account, content, requestMap,
                // imagePath, serverPath, message);
                // }
                // }

                List<ExpandconfigPojo> weixinExpandconfigEntityLst = CacheHelper.getCache()
                    .get(CacheCodeDef.EXPAND_CONFIG_CACHE, CacheCodeDef.EXPAND_CONFIG_CACHE);
                if (CommonUtil.isEmpty(weixinExpandconfigEntityLst)) {
                    DetachedCriteria criteria = DetachedCriteria.forClass(ExpandconfigPojo.class);
                    criteria.addOrder(Order.desc(ExpandconfigPojo.ORDER));
                    weixinExpandconfigEntityLst = wechatDao.getListByCriteriaQuery(criteria);
                    CacheHelper.getCache().put(CacheCodeDef.EXPAND_CONFIG_CACHE, CacheCodeDef.CONFIG_SAVE_TIME,
                        CacheCodeDef.EXPAND_CONFIG_CACHE, weixinExpandconfigEntityLst);
                }

                // 回复扩展
                if (CommonUtil.isEmpty(respMsg)) {

                    if (CommonUtil.isNotEmpty(weixinExpandconfigEntityLst)) {

                        // 同步消息
                        WechatMessageHandler expandHandler = null;
                        for (ExpandconfigPojo expandConfig : weixinExpandconfigEntityLst) {
                            expandHandler = ContextHolder.getContext().getBean(expandConfig.getClassname(),
                                WechatMessageHandler.class);
                            if (handler != null) {
                                respMsg = expandHandler.process(msgId, fromUserName, account, content, requestMap,
                                    imagePath, serverPath, message);
                                if (CommonUtil.isNotEmpty(respMsg)) {
                                    break;
                                }
                            }
                        }

                        if (CommonUtil.isEmpty(respMsg)) {
                            respMsg = "success";
                        }
                    }

                }

            }
            catch (Exception e) {
                LoggerUtil.error("处理微信消息失败", e);
                textMessage = new TextMessageResp();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setMsgType(WechatUtil.RESP_MESSAGE_TYPE_TEXT);
                textMessage.setContent("服务器繁忙，请稍候尝试！");
                respMsg = WechatUtil.textMessageToXml(textMessage);
            }
        }
        return respMsg;
    }

    /**
     * Description: <br>
     *
     * @param accountId
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public String wechatAccess(String accountId, String signature, String timestamp, String nonce, String echostr) {

        LoggerUtil.info(CommonUtil.messageFormat(
            "==========> access wechat validate info [signature={0},timestamp={1},nonce={2},echostr={3}]", signature,
            timestamp, nonce, echostr));

        String result = null;
        AccountPojo account = wechatDao.get(AccountPojo.class, accountId);
        if (SignUtil.checkSignature(account.getAccounttoken(), signature, timestamp, nonce)) {
            result = echostr;
        }

        LoggerUtil.info("<========== access wechat validate info [echostr={0}]", result);
        return result;
    }

    /**
     * Description: <br>
     *
     * @param url
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountPojo matchAccount(String appid) {
        AccountPojo account = this.wechatDao.findUniqueByProperty(AccountPojo.class, AccountPojo.ACCOUNT_APPID, appid);
        LoggerUtil.info("matchAccount appid = [{0}],account = [{0}]", appid, account);
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public String getAccessToken(String appId) {
        AccountPojo accountPojo = CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, appId);
        if (accountPojo == null) {
            accountPojo = getAccessTokenFormDb(appId);
            CacheHelper.getCache().put(CacheCodeDef.WX_ACCOUNT_INFO, WechatConstant.TOKEN_CACHE_TIME, appId,
                accountPojo);
        }
        if (CommonUtil.isEmpty(accountPojo.getAccountaccesstoken()) || accountPojo.getAddtokentime() == null
            || DateUtil.getCurrentTime() - accountPojo.getAddtokentime().getTime() > WechatConstant.TOKEN_TIME) {
            refreshAccessToken(appId, accountPojo.getAccountappsecret());
            accountPojo.setAccountaccesstoken(getAccessToken(appId));
        }
        return accountPojo.getAccountaccesstoken();
    }

    /**
     * Description: <br>
     *
     * @param appId
     * @param appKey
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    private AccountPojo getAccessTokenFormDb(String appId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AccountPojo.class);
        criteria.add(Restrictions.eq(AccountPojo.ACCOUNT_APPID, appId));
        AccountPojo account = wechatDao.getCriteriaQuery(criteria);
        Assert.notNull(account, ErrorCodeDef.APPID_NULL);
        return account;
    }

    /**
     * Description: <br>
     *
     * @param appId
     * @param appKey <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String refreshAccessToken(String appId, String appKey) {
        String accessToken = null;
        String url = MessageFormat.format(WechatConstant.ACCESS_TOKEN_URL, appId, appKey);
        LoggerUtil.info("getAccessToken url [{0}]", url);
        String jsonStr = HttpUtil.doGet(url);
        LoggerUtil.info("getAccessToken 获取token wechat response: [{0}]", jsonStr);

        JSONObject obj = JSONObject.parseObject(jsonStr);
        String errorCode = obj.getString("errcode");
        if (CommonUtil.isEmpty(errorCode)) {
            accessToken = obj.getString("access_token");

            DetachedCriteria criteria = DetachedCriteria.forClass(AccountPojo.class);
            criteria.add(Restrictions.eq(AccountPojo.ACCOUNT_APPID, appId));
            criteria.add(Restrictions.eq(AccountPojo.ACCOUNT_SECRET, appKey));
            AccountPojo account = wechatDao.getCriteriaQuery(criteria);
            if (account != null) {
                account.setAccountaccesstoken(accessToken);
                account.setAddtokentime(new Date());
                account.setJsapiticket(null);
                wechatDao.saveOrUpdate(account);
                // 更新能力开放绑定表
                OpenapiChannelPojo oc = getOpenapiChannelByAppIdAndeAppSecret(appId, appKey);
                if (oc != null) {
                    oc.setAccessToken(accessToken);
                    wechatDao.saveOrUpdate(oc);
                }
                CacheHelper.getCache().put(CacheCodeDef.WX_ACCOUNT_INFO, WechatConstant.TOKEN_CACHE_TIME, appId,
                    account);
            }
        }
        else {
            CacheHelper.getCache().put(CacheCodeDef.WX_ACCESS_TOKEN_ERROR, appId, obj.toString());
            LoggerUtil.error("获取AssessToken失败:[" + jsonStr + "]");
            throw new FrameworkException(ErrorCodeDef.APPID_SECRET_ERROR);
        }
        return accessToken;
    }

    /**
     * Description: <br>
     *
     * @param accessToken
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED )
    public String getJsApiTicket(String appId) {

        LoggerUtil.info("get JsApiTicket, appId = [{0}]", appId);

        AccountPojo accountPojo = CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, appId);
        if (accountPojo == null) {
            accountPojo = getAccessTokenFormDb(appId);
            CacheHelper.getCache().put(CacheCodeDef.WX_ACCOUNT_INFO, WechatConstant.TOKEN_CACHE_TIME, appId,
                accountPojo);
        }
        
        if (CommonUtil.isEmpty(accountPojo.getJsapiticket()) || accountPojo.getJsapitickettime() == null
            || DateUtil.getCurrentTime() - accountPojo.getJsapitickettime().getTime() > WechatConstant.TOKEN_TIME) {
            refreshJsapiTicket(appId);
            accountPojo.setJsapiticket(getJsApiTicket(appId));

        }

        return accountPojo.getJsapiticket();

    }

    /**
     * Description: <br>
     *
     * @param accessToken <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String refreshJsapiTicket(String appId) {
    	
    	AccountPojo accountPojo = getAccessTokenFormDb(appId);
    	Assert.notEmpty(accountPojo.getAccountaccesstoken(), ErrorCodeDef.ACCESS_TOKEN_NULL);
        
    	String url = MessageFormat.format(WechatConstant.JSAPI_TICKET_URL, accountPojo.getAccountaccesstoken());
        LoggerUtil.info("通过accessToken获取jsapi_ticket-url:[{0}]", url);
        String jsonStr = HttpUtil.doGet(url);
        LoggerUtil.info("getticket 获取jsapiticket wechat response: " + jsonStr);
        JSONObject obj = JSONObject.parseObject(jsonStr);
        String ticket = obj.getString("ticket");
        LoggerUtil.info("获取到的ticket为：" + ticket);

        if (CommonUtil.isNotEmpty(ticket)) {
            	accountPojo.setJsapiticket(ticket);
            	accountPojo.setJsapitickettime(new Date());
                wechatDao.saveOrUpdate(accountPojo);

                CacheHelper.getCache().put(CacheCodeDef.WX_ACCOUNT_INFO, WechatConstant.TOKEN_CACHE_TIME,
                		accountPojo.getAccountappid(), accountPojo);
        }
        else {
            LoggerUtil.error(obj.getString("errmsg"));
            if ("40001".equals(obj.getString("errcode"))) {
                refreshAccessToken(accountPojo.getAccountappid(), accountPojo.getAccountappsecret());
            }
            else {
                throw new FrameworkException(ErrorCodeDef.ACCESS_TOKEN_ERROR);
            }
        }
        return ticket;
    }

    /**
     * Description: <br>
     *
     * @param code
     * @param appId
     * @param appKey
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public String getOpenId4Oauth2(String code, String appId, String appKey) {
        String url = MessageFormat.format(WechatConstant.OAUTH2_TOKEN_URL, appId, appKey, code);
        LoggerUtil.info("auth2.0 根据code查询openid url[{0}]", url);
        String jsonStr = HttpUtil.doGet(url);
        LoggerUtil.info("auth2.0 查询结果反馈:[{0}]", jsonStr);

        JSONObject obj = JSONObject.parseObject(jsonStr);
        String errorCode = obj.getString("errcode");
        return CommonUtil.isEmpty(errorCode) ? obj.getString("openid") : null;
    }

    /**
     * Description: <br>
     *
     * @param accessToken
     * @param absolutePath
     * @param media_id
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    public String mediaDown(String accessToken, String absolutePath, String media_id) {
        String url = MessageFormat.format(WechatConstant.MEDIA_DOWN_URL, accessToken, media_id);
        LoggerUtil.info("begin mediaDownmediaDown 获取mediaDown [{0}]", url);

        String dirPath = DateUtil.getCurrentTimestamp();
        File dir = new File(absolutePath, dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String pictureName = CommonUtil.getRandomNumber(4) + ".jpg";
        String path = new File(dir, pictureName).getAbsolutePath();
        EventData data = new EventData();
        data.put("filePath", path);
        data.put("url", url);
        EventEmmiter.emmit(WechatEventCodeDef.WECHAT_FILE_UPLOAD, data);
        LoggerUtil.info("end mediaDownmediaDown 获取mediaDown [{0}]", data);

        LoggerUtil.info("保存到数据库中的路径：{0}", dirPath + File.separator + pictureName);
        return dirPath + File.separator + pictureName;
    }

    /**
     * Description: <br>
     *
     * @param key
     * @param paramMap
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public UnifiedOrderResult createWechatOrder(String key, Map<String, String> paramMap) {
        try {
            // 签名，详见签名生成算法
            paramMap.put("sign", WechatUtil.sign(key, paramMap));
            String content = WechatUtil.map2xml(paramMap);
            LoggerUtil.info("---begin 发送微信支付订单[{0}]", content);
            String result = HttpUtil.doPost(WechatConstant.ORDER_API, content, "text/xml");
            LoggerUtil.info("---end 发送微信支付订单result[{0}]", result);
            return XmlBeanUtil.xml2Object(result, UnifiedOrderResult.class);
        }
        catch (UtilException e) {
            LoggerUtil.error("签名校验失败", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        String key = "eiy7ZoAHcPdtgC4uT2AFuDNFUudAXILZ";
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("nonce_str", CommonUtil.getRandomChar(20));
        // paramMap.put("time_expire", DateUtil.getCurrentTimestamp());
        paramMap.put("fee_type", "CNY");
        paramMap.put("mch_id", "1271056701");
        paramMap.put("body", "IC卡充值");
        paramMap.put("notify_url", "http://weixin.towngasvcc.com/vcc-wx/charge/notice/wechat");
        paramMap.put("device_info", "WEB");
        paramMap.put("out_trade_no", "99" + DateUtil.getCurrentTimestamp() + "0001");
        paramMap.put("appid", "wxd4c0c0a7cd7f9ce1");
        paramMap.put("total_fee", "7500");
        paramMap.put("trade_type", "NATIVE");

        // 签名，详见签名生成算法
        paramMap.put("sign", WechatUtil.sign(key, paramMap));

        String content = WechatUtil.map2xml(paramMap);
        System.out.println("---begin 发送微信支付订单[" + content + "]");
        String result = HttpUtil.doPost(WechatConstant.ORDER_API, content, "text/xml");
        System.out.println("---end 发送微信支付订单result[" + result + "]");
        System.out.println(XmlBeanUtil.xml2Object(result, UnifiedOrderResult.class));

    }

    /**
     * Description: <br>
     *
     * @param key
     * @param paramMap
     * @param p12Path
     * @return <br>
     * @author 王伟<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, String> refund(String key, Map<String, String> paramMap, String p12Path) {
        String mchId = paramMap.get("mch_id");
        try {
            // 签名，详见签名生成算法
            paramMap.put("sign", WechatUtil.sign(key, paramMap));
            String content = WechatUtil.map2xml(paramMap);
            LoggerUtil.info("---begin 发送微信退费请求[{0}]", content);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream in = null;
            try {
                in = new FileInputStream(new File(p12Path));
                keyStore.load(in, mchId.toCharArray());
            }
            finally {
                IOUtils.closeQuietly(in);
            }

            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] {
                "TLSv1"
            }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

            String result = HttpUtil.doPost(httpclient, WechatConstant.REFUND_API, content, "text/xml");

            LoggerUtil.info("---end 发送微信退费请求result[{0}]", result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            String oldSign = resultMap.remove("sign");
            String newSign = WechatUtil.sign(key, resultMap);
            Assert.equals(oldSign, newSign, ErrorCodeDef.REFUND_HASH_ERROR, oldSign, newSign);
            return resultMap;
        }
        catch (Exception e) {
            LoggerUtil.error(e);
            throw new UtilException(ErrorCodeDef.REFUND_ERROR, e);
        }
    }

    /**
     * Description: <br>
     *
     * @param appId
     * @return <br>
     * @author 查思玮<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public WechatAccount getAccountByappId(String appId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(AccountPojo.class);
        criteria.add(Restrictions.eq(AccountPojo.ACCOUNT_APPID, appId));
        criteria.add(Restrictions.eq(AccountPojo.STATE, AccountPojo.STATE_AVALIBLE));
        AccountPojo account = wechatDao.getCriteriaQuery(criteria);

        if (!CommonUtil.isNull(account)) {
            if (DateUtil.getCurrentTime() - account.getAddtokentime().getTime() > WechatConstant.TOKEN_TIME) {// accesstoken失效
                refreshAccessToken(appId, account.getAccountappsecret());
                return getAccountByappId(appId);
            }
            else {
                WechatAccount acct = new WechatAccount();
                acct.setAppCode(account.getAccountappid());
                acct.setAppSecret(account.getAccountappsecret());
                return acct;
            }
        }
        else {
            return null;
        }

    }

    /**
     * Description: <br>
     *
     * @param appId
     * @param secret
     * @return <br>
     * @author 查思玮<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public OpenapiChannelPojo getOpenapiChannelByAppIdAndeAppSecret(String appId, String secret) {
        DetachedCriteria criteria = DetachedCriteria.forClass(OpenapiChannelPojo.class);
        criteria.add(Restrictions.eq(OpenapiChannelPojo.APP_CODE, appId));
        criteria.add(Restrictions.eq(OpenapiChannelPojo.APP_SECRET, secret));
        criteria.add(Restrictions.eq(OpenapiChannelPojo.STATE, OpenapiChannelPojo.AVALIABLE));
        return wechatDao.getCriteriaQuery(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public OpenapiChannelPojo getOpenapiChannelByAppId(String appId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(OpenapiChannelPojo.class);
        criteria.add(Restrictions.eq(OpenapiChannelPojo.APP_CODE, appId));
        criteria.add(Restrictions.eq(OpenapiChannelPojo.STATE, OpenapiChannelPojo.AVALIABLE));
        OpenapiChannelPojo pojo = wechatDao.getCriteriaQuery(criteria);
        if (pojo != null && CommonUtil.isEmpty(pojo.getAccessToken())) {
            AccountPojo account = wechatDao.get(AccountPojo.class, pojo.getAccountId());
            if (account != null && account.getAddtokentime() != null
                && DateUtil.getCurrentTime() - account.getAddtokentime().getTime() > WechatConstant.TOKEN_TIME) {// accesstoken失效
                refreshAccessToken(appId, account.getAccountappsecret());
                return getOpenapiChannelByAppId(appId);
            }
            else {
                if (account != null) {
                    pojo.setAccessToken(account.getAccountaccesstoken());
                    wechatDao.saveOrUpdate(pojo);
                }
            }
        }
        return wechatDao.getCriteriaQuery(criteria);
    }

    /**
     * Description: <br>
     *
     * @param accessToken
     * @return <br>
     * @author 查思玮<br>
     * @taskId <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public OpenapiChannelPojo getOpenapiChannelByAccessToken(String accessToken) {
        DetachedCriteria criteria = DetachedCriteria.forClass(OpenapiChannelPojo.class);
        criteria.add(Restrictions.eq(OpenapiChannelPojo.ACCESS_TOKEN, accessToken));
        criteria.add(Restrictions.eq(OpenapiChannelPojo.STATE, OpenapiChannelPojo.AVALIABLE));
        return wechatDao.getCriteriaQuery(criteria);
    }

    /**
     * 微信模板发送消息
     *
     * @param wxTemplate 模板信息
     * @return WXResult WXResult
     * @throws RemoteServiceException
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public WXResult sendMessageByTemplate(WxTemplate wxTemplate, String appId) {

        String transId = CommonUtil.getTransactionID();
        LoggerUtil.info("start sendMessageByTemplate  [{0}]", transId);

        // openID 不存在不发送
        if (wxTemplate == null || StringUtils.isEmpty(wxTemplate.getTouser())) {
            LoggerUtil.info("模板信息为null ,或者发送对象openID为空");
            return null;
        }

        String accessToken = getAccessToken(appId);

        String url = MessageFormat.format(WechatConstant.TEMPLATE_MESSAGE, accessToken);
        String body = JsonUtil.writeObj2FormatJson(wxTemplate);

        LoggerUtil.info("发送微信模板消息-[{0}]  url:[{1}]---body[{2}]", transId, url, body);
        String jsonStr = HttpUtil.doPost(url, body, WechatConstant.APPLICATION_JSON_UTF_8);
        LoggerUtil.info("发送微信模板消息结果代码:[{0}]|[{1}]", transId, jsonStr);
        JSONObject obj = JSONObject.parseObject(jsonStr);
        WXResult wxResult = new WXResult();
        wxResult.setErrcode(obj.getString("errcode"));
        if (CommonUtil.isEmpty(wxResult.getErrcode())) {
            wxResult.setMsgid(obj.getString("msgid"));
        }
        return wxResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public WXResult sendMessage(WxMessage wxMessage, String accessToken) {
        LoggerUtil.info("start sendMessage");
        String msgUrl = MessageFormat.format(WechatConstant.CUSTOM_URL, accessToken);
        String body = JsonUtil.writeObj2FormatJson(wxMessage);
        String ret = HttpUtil.doPost(msgUrl, body, WechatConstant.APPLICATION_JSON_UTF_8);
        JSONObject obj = JSONObject.parseObject(ret);
        WXResult wxResult = new WXResult();
        wxResult.setErrcode(obj.getString("errcode"));
        if (CommonUtil.isEmpty(wxResult.getErrcode())) {
            wxResult.setMsgid(obj.getString("msgid"));
        }
        return wxResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<AccountPojo> getAllAccessToken() {
        List<AccountPojo> acctList = wechatDao.loadAll(AccountPojo.class);
        return acctList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public String checkSignature(String openapiChannelId) {

        String result = WechatConstant.FAIL_CODE;
        OpenapiChannelPojo openapiChannel = wechatDao.get(OpenapiChannelPojo.class, openapiChannelId);
        if (null != openapiChannel && null != openapiChannel.getPushUrl()) {
            String timestamp = DateUtil.getCurrentTimestamp();
            String nonce = CommonUtil.getRandomNumber(32);
            String echostr = CommonUtil.getRandomChar(16);

            String[] pushUrlArray = openapiChannel.getPushUrl().split(GlobalConstants.SPLITOR);
            String[] tokenArray = openapiChannel.getToken().split(GlobalConstants.SPLITOR);
            // 当两者数量相等时，才会发送请求
            if (pushUrlArray.length == tokenArray.length) {
                for (int i = 0; i < pushUrlArray.length; i++) {
                    String signature = SignUtil.signature(tokenArray[i], timestamp, nonce).toLowerCase();

                    StringBuffer pushUrl = new StringBuffer(pushUrlArray[i]);
                    if (pushUrl.toString().indexOf("?") == -1) {
                        pushUrl.append("?");
                    }
                    else {
                        pushUrl.append("&");
                    }
                    pushUrl.append("signature=").append(signature).append("&nonce=").append(nonce).append("&timestamp=")
                        .append(timestamp).append("&echostr=").append(echostr);
                    LoggerUtil.info("start pushUrl doGet , openapiChannelId = [{0}], pushUrl = [{1}]", openapiChannelId,
                        pushUrl);
                    String echostrResp = HttpUtil.doGet(pushUrl.toString());
                    LoggerUtil.info("<========== pushUrl doGet validate info [echostrResp={0}]", echostrResp);
                    if (!echostr.equals(echostrResp)) {
                        return pushUrlArray[i];
                    }
                }
                openapiChannel.setOpenapiState(OpenapiChannelPojo.AVALIABLE);
                wechatDao.updateEntity(openapiChannel);
                result = WechatConstant.SUCCESS;
            }

        }
        return result;
    }

    // public static void main(String[] args) {
    // // String timestamp = DateUtil.getCurrentTimestamp();
    // // String nonce = CommonUtil.getRandomNumber(32);
    // // String signature = SignUtil.signature("MqjTest", timestamp, nonce);
    // // String echostr = CommonUtil.getRandomChar(16);
    // //
    // // StringBuffer pushUrl = new StringBuffer("http://localhost:8080/ff8080815be6ab36015bf145dad20016?wechat");
    // //
    // pushUrl.append("&signature=").append(signature).append("&nonce=").append(nonce).append("&timestamp=").append(timestamp).append("&echostr=").append(echostr);
    // // LoggerUtil.info("start pushUrl doGet , openapiChannelId = [{0}], pushUrl = [{1}]", 123, pushUrl);
    // // String echostrResp = HttpUtil.doGet(pushUrl.toString());
    // // LoggerUtil.info("<========== pushUrl doGet validate info [echostrResp={0}]", echostrResp);
    // System.out.println(
    // SignUtil.checkSignature("mms", "B296A51F111235C2E1855ED6821746CD3D6BB878", "1496645349", "3015559136"));
    // }

    /**
     * Description: <br>
     * 
     * @author 查思玮<br>
     * @taskId <br>
     * @param actionName
     * @param sceneStr
     * @return <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Cache(node = CacheCodeDef.WX_SPREAD_QC_CACHE, key = "$!{sceneStr}")
    public String getSpreadQcUrl(@Key("sceneStr") String sceneStr, String accessToken) {
        JSONObject paramJson = new JSONObject();
        JSONObject sceneJson = new JSONObject();
        JSONObject actionInoJson = new JSONObject();
        sceneJson.put("scene_str", sceneStr);
        actionInoJson.put("scene", sceneJson);
        paramJson.put("action_name", "QR_LIMIT_STR_SCENE");
        paramJson.put("action_info", actionInoJson);
        String url = MessageFormat.format(WechatConstant.SPREAD_QRCODE_URL, accessToken);
        LoggerUtil.info("获得员工推广永久二维码url:[{0}]---body[{1}]", url, paramJson.toJSONString());
        String jsonStr = HttpUtil.doPost(url, paramJson.toJSONString(), WechatConstant.APPLICATION_JSON_UTF_8);
        LoggerUtil.info("获得员工推广永久二维码url:代码:[{0}]", jsonStr);
        JSONObject obj = JSONObject.parseObject(jsonStr);
        return obj.get("url").toString();
    }

    /**
     * Description: <br>
     * 
     * @author liuxianan<br>
     * @taskId <br>
     * @param sceneStr
     * @param accessToken
     * @return <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Cache(node = CacheCodeDef.WX_TEMP_SPREAD_QC_CACHE, key = "$!{sceneStr}",
        expireTime = CacheCodeDef.MONTH_CACHE_TIME)
    public String getTempSpreadQcUrl(@Key("sceneStr") String sceneStr, String accessToken) {
        JSONObject paramJson = new JSONObject();
        JSONObject sceneJson = new JSONObject();
        JSONObject actionInoJson = new JSONObject();
        sceneJson.put("scene_str", sceneStr);
        actionInoJson.put("scene", sceneJson);
        paramJson.put("action_name", "QR_LIMIT_STR_SCENE");
        paramJson.put("expire_seconds", CacheCodeDef.MONTH_CACHE_TIME);
        paramJson.put("action_info", actionInoJson);
        String url = MessageFormat.format(WechatConstant.SPREAD_TEMP_QRCODE_URL, accessToken);
        LoggerUtil.info("获得员工推广临时二维码url:[{0}]---body[{1}]", url, paramJson.toJSONString());
        String jsonStr = HttpUtil.doPost(url, paramJson.toJSONString(), WechatConstant.APPLICATION_JSON_UTF_8);
        LoggerUtil.info("获得员工推广临时二维码url:代码:[{0}]", jsonStr);
        JSONObject obj = JSONObject.parseObject(jsonStr);
        return obj.get("url").toString();
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public List<ExpandconfigPojo> queryAllExpandConfig() {
        DetachedCriteria criteria = DetachedCriteria.forClass(ExpandconfigPojo.class);
        criteria.addOrder(Order.desc(ExpandconfigPojo.ORDER));
        List<ExpandconfigPojo> weixinExpandconfigEntityLst = wechatDao.getListByCriteriaQuery(criteria);
        CacheHelper.getCache().put(CacheCodeDef.EXPAND_CONFIG_CACHE, CacheCodeDef.CONFIG_SAVE_TIME,
            CacheCodeDef.EXPAND_CONFIG_CACHE, weixinExpandconfigEntityLst);

        return weixinExpandconfigEntityLst;
    }

    /**
     * Description: <br>
     * 
     * @author 查思玮<br>
     * @taskId <br>
     * @param id
     * @return <br>
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountPojo getAccountById(String id) {
        return wechatDao.get(AccountPojo.class, id);
    }

    /**
     * 通过accessToken openId 获取用户信息
     * 
     * @param openId
     * @return {city,sex,headimgurl,nickname,province}
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JSONObject getUserInfoMessage(String openId, String appId) {
        String url = MessageFormat.format(WechatConstant.USER_INFO, getAccessToken(appId), openId);
        LoggerUtil.info("获取用户信息:[{0}]", url);
        String jsonStr = HttpUtil.doGet(url);
        LoggerUtil.info("获取用户信息结果:[{0}]", jsonStr);
        JSONObject obj = JSONObject.parseObject(jsonStr);
        String errorCode = obj.getString("errcode");
        if (CommonUtil.isEmpty(errorCode)) {
            return obj;
        }
        else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AutoresponsePojo getAutoResponse(String accountId, String content) {
        List<AutoresponsePojo> autoResponses = wechatDao.findByProperty(AutoresponsePojo.class,
            AutoresponsePojo.ACCOUNT_ID, accountId);

        AutoresponsePojo autoResponse = null;
        @SuppressWarnings("unused")
        AutoresponsePojo defaultResp = null;
    
        for (AutoresponsePojo r : autoResponses) {
            if (matchKeyword(r.getKeyword(), content)) {
                LoggerUtil.info("---------sys_accountId----查询结果----" + r);
                autoResponse = r;
                break;
            } else if (com.hbasesoft.framework.common.utils.CommonUtil.isEmpty(r.getKeyword())) {
                defaultResp = r;
            }
        }
        return autoResponse;
    }
    
    protected boolean matchKeyword(String key, String content) {
        if (StringUtils.startsWith(key, "^")) {
            return content.matches(key);
        }
        else {
            return StringUtils.equals(key, content);
        }
    }
}
