/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.wechat.controller;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.cache.core.CacheHelper;
import com.hbasesoft.framework.common.FrameworkException;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.date.DateConstants;
import com.hbasesoft.framework.common.utils.date.DateUtil;
import com.hbasesoft.framework.common.utils.logger.Logger;
import com.hbasesoft.framework.wechat.CacheCodeDef;
import com.hbasesoft.framework.wechat.ErrorCodeDef;
import com.hbasesoft.framework.wechat.WebConstant;
import com.hbasesoft.framework.wechat.WechatConstant;
import com.hbasesoft.framework.wechat.bean.OpenapiChannelPojo;
import com.hbasesoft.framework.wechat.service.WechatService;
import com.hbasesoft.framework.wechat.util.MD5Encoder;
import com.hbasesoft.framework.wechat.util.WechatUtil;

/**
 * <Description> <br>
 * 
 * @author 查思玮<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年4月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.wechat.controller <br>
 */
@Controller
@RequestMapping("/wechat")
public class WeChatOpenApiController extends BaseController {

    private static Logger logger = new Logger(WeChatOpenApiController.class);

    @Resource(name = "WechatServiceImpl")
    private WechatService wechatService;

    @RequestMapping(value = "/connect/oauth2/authorize", method = RequestMethod.GET)
    public void wechatAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info(" start authorize ");

        String appId = getParameter("appid", ErrorCodeDef.APPID_NULL);
        String redirectUri = getParameter("redirect_uri", ErrorCodeDef.REDICECT_URI_NULL);
        String scope = getParameter("scope", ErrorCodeDef.SCOPE_NULL);
        String state = getParameter("state", ErrorCodeDef.STATE_NULL);

        OpenapiChannelPojo openapiChannel = wechatService.getOpenapiChannelByAppId(appId);

        if (!CommonUtil.isNull(openapiChannel)) {
            boolean flag = false;
            PathMatcher matcher = new AntPathMatcher();
            String hosts = openapiChannel.getHost();
            if(CommonUtil.isNotEmpty(hosts)){
            	String[] hostsArr = hosts.split(GlobalConstants.SPLITOR);
            	for(String host:hostsArr){
            		if (host.substring(host.length() - 1).equals(GlobalConstants.PATH_SPLITOR)) {
                        host += "**";
                    }
                    else {
                        host += "/**";
                    }
                    if (matcher.match(host, redirectUri)) {
                        flag = true;
                        break;
                    }
                }

                // 匹配域名
                if (flag) {
                    // 将参数存入缓存，key为随机生成的32位数并用MD5加密
                    String stateKey = MD5Encoder.encode(CommonUtil.getRandomChar(32));

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("state", state);
                    map.put("redirectUri", redirectUri);
                    CacheHelper.getCache().put(CacheCodeDef.WX_OAUTH_STATE_PARAM, WechatConstant.TOKEN_CACHE_TIME,
                        stateKey, map);

                    String authUrl = WechatUtil.getOauth2Url(appId, "/wechat" + WebConstant.OATH_CALLBACK_URI, stateKey,
                        scope, PropertyHolder.getProperty("server.wx.url", GlobalConstants.BLANK) + request.getContextPath());

                    logger.info(">>> [微信OAuth2认证授权跳转] location = [{0}]", authUrl);
                    response.sendRedirect(authUrl);
                }
            }

        }

        // 不匹配域名，返回错误页面
        throw new ServiceException(ErrorCodeDef.REDIRECT_URI_ERROR);
    }

    @RequestMapping(value = WebConstant.OATH_CALLBACK_URI, method = RequestMethod.GET)
    public void oath2Callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = getParameter("code", ErrorCodeDef.CODE_NULL);
        String stateKey = getParameter("state", ErrorCodeDef.STATE_NULL);

        Map<String, String> map = new HashMap<String, String>();
        map = CacheHelper.getCache().get(CacheCodeDef.WX_OAUTH_STATE_PARAM, stateKey);
        String redirectUri = map.get("redirectUri");
        String state = map.get("state");

        redirectUri += "?code=" + code + "&state=" + state;

        logger.info("登录成功 跳转地址[{0}]", redirectUri);
        logger.info("callbackUrl decode =[{0}]", URLDecoder.decode(redirectUri, "UTF-8"));
        logger.info("redirectUri = [{0}]", redirectUri);
        response.sendRedirect(redirectUri);
    }

    @RequestMapping(value = "/cgi-bin/token", method = RequestMethod.GET)
    @ResponseBody
    public String getToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info(" start getToken ");

        String appId = getParameter("appid", ErrorCodeDef.APPID_NULL);
        String appKey = getParameter("secret", ErrorCodeDef.SECTRET_NULL);

        JSONObject resq = new JSONObject();

        OpenapiChannelPojo openapiChannel = wechatService.getOpenapiChannelByAppIdAndeAppSecret(appId, appKey);
        if (openapiChannel != null) {
            try {
                String accessToken = wechatService.getAccessToken(appId);
                long accessTime = 0;
                if( CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, appId + CacheCodeDef.WX_ACCOUNT_TIME) != null) {
                    accessTime =  CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, appId + CacheCodeDef.WX_ACCOUNT_TIME);
                }
                int expiresIn = 0;
                if(accessTime != 0) {
                    // VCC accessToken 每小时更新，所以是3600秒
                    expiresIn = 3600 - (int) ((DateUtil.getCurrentDate().getTime() - accessTime)/1000);
                }else {
                    expiresIn = -1;
                }
                logger.info(">>> [get Token] accessToken = [{0}]", accessToken);
                resq.put("access_token", accessToken);
                resq.put("expires_in", expiresIn >= 0 ? expiresIn : 7200);
            }
            catch (FrameworkException e) {
                String errorMsg = PropertyHolder.getErrorMessage(e.getCode());
                logger.info(">>> [get Token] errorMsg = [{0}]", errorMsg);
                resq.put("errmsg", errorMsg);
                resq.put("errcode", e.getCode());
            }
        }
        else {
            resq.put("errmsg", "appId或appSercet无效");
            resq.put("errcode", ErrorCodeDef.APPID_SECRET_ERROR);
        }
        return resq.toString();
    }

    @RequestMapping(value = "/cgi-bin/ticket/getticket", method = RequestMethod.GET)
    @ResponseBody
    public String getticket(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info(" start getticket ");

        String accessToken = getParameter("access_token", ErrorCodeDef.ACCESS_TOKEN_NULL);

        OpenapiChannelPojo openapiChannel = wechatService.getOpenapiChannelByAccessToken(accessToken);

        JSONObject resq = new JSONObject();
        if (openapiChannel != null) {
            try {
                String jsApiTicket = wechatService.getJsApiTicket(openapiChannel.getAppCode());
                long jsticketTime = 0;
                if(CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, jsApiTicket + CacheCodeDef.WX_JSTICKET_TIME) != null) {
                    jsticketTime = CacheHelper.getCache().get(CacheCodeDef.WX_ACCOUNT_INFO, jsApiTicket + CacheCodeDef.WX_JSTICKET_TIME);
                }
                int expiresIn = 0;
                if(jsticketTime != 0) {
                 // VCC jsticket 每小时更新，所以是3600秒
                    expiresIn = 3600 - (int) ((DateUtil.getCurrentDate().getTime() - jsticketTime)/1000);
                }else {
                    expiresIn = -1;
                }
                
                logger.info(">>> [get jsApiTicket] jsApiTicket = [{0}]", accessToken);
                resq.put("ticket", jsApiTicket);
                resq.put("expires_in", expiresIn >= 0 ? expiresIn : 7200);
            }
            catch (FrameworkException e) {
                String errorMsg = PropertyHolder.getErrorMessage(e.getCode());
                logger.info(">>> [get Token] errorMsg = [{0}]", errorMsg);
                resq.put("errmsg", errorMsg);
                resq.put("errcode", e.getCode());
            }
        }
        else {
            resq.put("errmsg", "accessToken无效");
            resq.put("errcode", ErrorCodeDef.ACCESS_TOKEN_ERROR);
        }

        return resq.toString();

    }
    
}
