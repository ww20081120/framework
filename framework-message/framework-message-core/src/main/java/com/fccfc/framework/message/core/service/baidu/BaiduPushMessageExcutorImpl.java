/**
 * 
 */
package com.fccfc.framework.message.core.service.baidu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.fccfc.framework.common.ErrorCodeDef;
import com.fccfc.framework.common.GlobalConstants;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.common.utils.CommonUtil;
import com.fccfc.framework.common.utils.UtilException;
import com.fccfc.framework.common.utils.io.HttpClientUtil;
import com.fccfc.framework.message.api.Attachment;
import com.fccfc.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.message.service.baidu <br>
 */
@Service
public class BaiduPushMessageExcutorImpl implements MessageExcutor {

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.message.service.AbstractMessageService#sendMessage(java.lang.String, java.lang.String,
     * java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public String sendMessage(String title, String content, String sender, String[] receiver,
        List<Attachment> attachments) throws ServiceException {
        try {
            return pushMsg(receiver[0], null, title, content);
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.BAIDUYUN_SEND_ERROR_10034, " Baidu push failed, reason: ${0}.",
                e.getMessage());
        }
    }

    public String pushMsg(String user_id, String channel_id, String title, String description)
        throws UnsupportedEncodingException, UtilException {
        String url = "http://channel.api.duapp.com/rest/2.0/channel/channel";// Configuration.getString(CacheConstant.BAIDUYUN_URL);//
        String method = "push_msg";
        String apikey = "wNTs4veiwjRS6y9ZWmXj3fSl";// Configuration.getString(CacheConstant.BAIDUYUN_APIKEY);
        String secret = "jLj7WGaDLPOeKdez2Pu944Nb3ntarla4";// Configuration.getString(CacheConstant.BAIDUYUN_SECRETKEY);
        // 推送类型，取值范围为：1～3
        // 1：单个人，必须指定user_id 和 channel_id （指定用户的指定设备）或者user_id（指定用户的所有设备）
        // 2：一群人，必须指定 tag
        // 3：所有人，无需指定tag、user_id、channel_id
        int push_type = 1;
        // 设备类型
        // 1：浏览器设备；
        // 2：PC设备；
        // 3：Andriod设备；
        // 4：iOS设备；
        // 5：Windows Phone设备；
        int device_type = 3;
        // 消息类型
        // 0：消息（透传给应用的消息体）
        // 1：通知（对应设备上的消息通知）
        // 默认值为0。
        int message_type = 1;
        // 推送信息
        String messages = getMessage(title, description);
        // 消息标识。
        // 指定消息标识，必须和messages一一对应。相同消息标识的消息会自动覆盖。
        String msg_keys = UUID.randomUUID().toString();
        // 用户发起请求时的Unix时间戳。本次请求签名的有效时间为该时间戳+10分钟。
        String timestamp = Long.toString(new Date().getTime());

        Map<String, String> parameters = new TreeMap<String, String>();
        parameters.put("method", method);
        parameters.put("apikey", apikey);
        parameters.put("user_id", user_id);
        if (null != channel_id) {
            parameters.put("channel_id", channel_id);
        }
        parameters.put("push_type", push_type + "");
        parameters.put("device_type", device_type + "");
        parameters.put("message_type", message_type + "");
        parameters.put("messages", messages);
        parameters.put("msg_keys", msg_keys);
        parameters.put("timestamp", timestamp + "");
        String sign = getSignature(url, parameters, secret);
        parameters.put("sign", sign);
        String responseStr = HttpClientUtil.post(url, parameters);
        return responseStr;
    }

    /**
     * 封装发送的消息
     * 
     * @param title 标题
     * @param description 内容
     * @return 结果
     */
    public String getMessage(String title, String description) {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("description", description);

        return json.toString();
    }

    /**
     * 获取签名
     * 
     * @param url 地址
     * @param parameters 参数
     * @param secret 密钥
     * @return 结果
     * @throws UnsupportedEncodingException
     * @throws UtilException
     */
    public String getSignature(String url, Map<String, String> paramMap, String secret)
        throws UnsupportedEncodingException, UtilException {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new HashMap<String, String>(paramMap);
        sortedParams = sortMapByKey(sortedParams);
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder baseString = new StringBuilder();
        baseString.append("POST");
        baseString.append(url);
        for (String key : sortedParams.keySet()) {
            if (null != key && !"".equals(key)) {
                baseString.append(key).append("=").append(sortedParams.get(key));
            }
            sortedParams.get(key);
        }
        baseString.append(secret);
        String encodeString = URLEncoder.encode(baseString.toString(), GlobalConstants.DEFAULT_CHARSET);
        String sign = CommonUtil.md5(encodeString);
        return sign;
    }

    /**
     * 排序map
     * 
     * @param unsort_map 原map
     * @return 排序后的map
     */
    private SortedMap<String, String> sortMapByKey(Map<String, String> unsort_map) {
        TreeMap<String, String> result = new TreeMap<String, String>();
        Object[] unsort_key = unsort_map.keySet().toArray();
        Arrays.sort(unsort_key);
        for (int i = 0; i < unsort_key.length; i++) {
            result.put(unsort_key[i].toString(), unsort_map.get(unsort_key[i]));
        }
        return result.tailMap(result.firstKey());
    }
}
