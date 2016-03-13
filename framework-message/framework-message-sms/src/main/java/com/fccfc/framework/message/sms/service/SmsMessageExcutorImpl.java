/**
 * 
 */
package com.hbasesoft.framework.message.sms.service;

import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.config.core.ConfigHelper;
import com.hbasesoft.framework.message.api.Attachment;
import com.hbasesoft.framework.message.core.service.MessageExcutor;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.message.service.sms <br>
 */
@Service
public class SmsMessageExcutorImpl implements MessageExcutor {

    private static final String CHANNEL_ID = "SMS";

    /*
     * (non-Javadoc)
     * @see com.hbasesoft.framework.message.service.AbstractMessageService#sendMessage(java.lang.String, java.lang.String,
     * java.lang.String, java.util.List, java.util.List)
     */
    @Override
    public String sendMessage(String title, String content, String sender, String[] receiver,
        List<Attachment> attachments) throws ServiceException {
        HttpClient client = new HttpClient();
        client.getParams().setSoTimeout(60 * 1000); // 设置超时时间
        client.getParams().setContentCharset(GlobalConstants.DEFAULT_CHARSET);
        String receives = getReceives(receiver);

        try {
            String url = getSendUrl(content, receives);
            HttpMethod method = new GetMethod(url);
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, GlobalConstants.DEFAULT_CHARSET);
            client.executeMethod(method);
            return method.getResponseBodyAsString();
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.SMS_SEND_ERROR_10033, e.getMessage(), e);
        }
    }

    /**
     * 获取接收人
     * 
     * @param receiver 接收人
     * @return 接收人
     */
    private String getReceives(String[] receiver) {
        StringBuilder receives = new StringBuilder();
        if (CommonUtil.isNotEmpty(receiver)) {
            for (int i = 0; i < receiver.length; i++) {
                receives.append(receiver[i]);

                if (i != (receiver.length - 1)) {
                    receives.append(GlobalConstants.SPLITOR);
                }
            }
        }
        return receives.toString();
    }

    /**
     * 获取发送的链接
     * 
     * @param content 发送内容
     * @param receives 接收人
     * @return 返回链接
     * @throws Exception <br>
     */
    private String getSendUrl(String content, String receives) throws Exception {
        String url = ConfigHelper.getString("SMS.SMS_URL");
        String username = ConfigHelper.getString("SMS.SMS_USERNAME");
        String password = ConfigHelper.getString("SMS.SMS_PASSWORD");

        url = StringUtils.replace(url, "${username}", username);
        url = StringUtils.replace(url, "${password}", DataUtil.md5(password));
        url = StringUtils.replace(url, "${content}", URLEncoder.encode(content, GlobalConstants.DEFAULT_CHARSET));
        url = StringUtils.replace(url, "${receives}", receives);

        return url;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String getChannelId() {
        return CHANNEL_ID;
    }
}
