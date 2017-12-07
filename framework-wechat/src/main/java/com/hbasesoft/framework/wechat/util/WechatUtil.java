package com.hbasesoft.framework.wechat.util;

import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.ServiceException;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.URLUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;
import com.hbasesoft.framework.common.utils.security.DataUtil;
import com.hbasesoft.framework.common.utils.xml.XmlUtil;
import com.hbasesoft.framework.message.core.event.EventData;
import com.hbasesoft.framework.message.core.event.EventEmmiter;
import com.hbasesoft.framework.wechat.WechatConstant;
import com.hbasesoft.framework.wechat.WechatEventCodeDef;
import com.hbasesoft.framework.wechat.bean.resp.Article;
import com.hbasesoft.framework.wechat.bean.resp.ImageMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.MusicMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.NewsMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.TextMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.VideoMessageResp;
import com.hbasesoft.framework.wechat.bean.resp.VoiceMessageResp;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class WechatUtil {

    /**
     * 返回消息类型：文本
     */
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";

    /**
     * 返回消息类型：音乐
     */
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";

    /**
     * 返回消息类型：图文
     */
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";

    /**
     * 返回消息类型：图片
     */
    public static final String RESP_MESSAGE_TYPE_IMAGE = "image";

    /**
     * 返回消息类型：音频
     */
    public static final String RESP_MESSAGE_TYPE_VOICE = "voice";

    /**
     * 返回消息类型：视频
     */
    public static final String RESP_MESSAGE_TYPE_VIDEO = "video";

    /**
     * 请求消息类型：文本
     */
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";

    /**
     * 请求消息类型：图片
     */
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";

    /**
     * 请求消息类型：链接
     */
    public static final String REQ_MESSAGE_TYPE_LINK = "link";

    /**
     * 请求消息类型：地理位置
     */
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";

    /**
     * 请求消息类型：音频
     */
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";

    /**
     * 请求消息类型：推送
     */
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";

    /**
     * 请求消息类型：视频
     */
    public static final String REQ_MESSAGE_TYPE_VIDEO = "video";

    /**
     * 事件类型：subscribe(订阅)
     */
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";

    /**
     * 事件类型：unsubscribe(取消订阅)
     */
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";

    /**
     * 事件类型：CLICK(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_CLICK = "CLICK";

    /**
     * 事件类型：VIEW(自定义菜单点击事件)
     */
    public static final String EVENT_TYPE_VIEW = "VIEW";

    /**
     * 事件类型：LOCATION(位置上报)
     */
    public static final String EVENT_TYPE_LOCATION = "LOCATION";
    
    /**
     * 事件类型：SCAN(用户已关注，扫码推送)
     */
    public static final String EVENT_TYPE_SCAN = "SCAN";

    /**
     * 解析微信发来的请求（XML）
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> parseXml(String message) {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        try {
            // 读取输入流
            Document document = DocumentHelper.parseText(message);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 得到根元素的所有子节点
            List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (Element e : elementList) {
                map.put(e.getName(), e.getText());
            }
            return map;
        }
        catch (Exception e) {
            throw new ServiceException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }

    }

    /**
     * 文本消息对象转换成xml
     * 
     * @param textMessage 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessageResp textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    /**
     * 音乐消息对象转换成xml
     * 
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String musicMessageToXml(MusicMessageResp musicMessage) {
        xstream.alias("xml", musicMessage.getClass());
        return xstream.toXML(musicMessage);
    }

    /**
     * 图文消息对象转换成xml
     * 
     * @param newsMessage 图文消息对象
     * @return xml
     */
    public static String newsMessageToXml(NewsMessageResp newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }

    /**
     * 音频消息对象转换成xml
     * 
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String voiceMessageToXml(VoiceMessageResp voiceMessage) {
        xstream.alias("xml", voiceMessage.getClass());
        return xstream.toXML(voiceMessage);
    }

    /**
     * 视频消息对象转换成xml
     * 
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String videoMessageToXml(VideoMessageResp videoMessage) {
        xstream.alias("xml", videoMessage.getClass());
        return xstream.toXML(videoMessage);
    }

    /**
     * 图像消息对象转换成xml
     * 
     * @param musicMessage 音乐消息对象
     * @return xml
     */
    public static String imageMessageToXml(ImageMessageResp imageMessage) {
        xstream.alias("xml", imageMessage.getClass());
        return xstream.toXML(imageMessage);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     * 
     * @date 2013-05-19
     */
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @SuppressWarnings("rawtypes")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    }
                    else {
                        writer.write(text);
                    }
                }
            };
        }
    });

    public static String getOauth2Url(String appId, String redirectURI, String expendParam, String scope) {
        String redirectUrl = URLUtil.encode(new StringBuilder()
            .append(PropertyHolder.getProperty("server.wx.url", GlobalConstants.BLANK)).append(redirectURI)
            .append(redirectURI.indexOf("?") == -1 ? "?appId=" : "&appId=").append(appId).toString());
        return MessageFormat.format(WechatConstant.OAUTH2_CODE_URL, appId, redirectUrl, scope, expendParam);
    }

    /**
     * Description: <br>
     * 
     * @author zhasiwei<br>
     * @taskId <br>
     * @param subsCode
     * @param templateId
     * @param param <br>
     */
    public static void syncSendMessageByTemplate(String subsCode, String orgCode, String templateId,
        Map<String, String> params, String url, String appId, String openId) {
        LoggerUtil.info(
            "syncSendMessageByTemplate subsCode = [{0}], orgCode = [{1}], templateId = [{2}], url = [{3}], appId = [{4}], openId = [{5}], params = [{6}]",
            subsCode, orgCode, templateId, url, appId, openId, String.valueOf(params));
        EventData data = new EventData();
        data.put("subsCode", subsCode);
        data.put("orgCode", orgCode);
        data.put("templateId", templateId);
        data.put("url", url);
        // data.put("userId", userId);
        data.put("appId", appId);
        data.put("openId", openId);
        data.put("params", params);
        EventEmmiter.emmit(WechatEventCodeDef.WECHAT_TEMPLATE_PUSH, data);
    }

    /**
     * Description: 异步发送消息给第三方<br>
     * 
     * @author zhasiwei<br>
     * @taskId <br>
     * @param subsCode
     * @param templateId
     * @param param <br>
     */
    public static void asyncSendMessage(String pushUrl, String message, String accessToken, String needResp) {
        LoggerUtil.info("asyncSendMessage pushUrl = [{0}], message = [{1}]", pushUrl, message);
        EventData data = new EventData();
        data.put("pushUrl", pushUrl);
        data.put("message", message);
        data.put("accessToken", accessToken);
        data.put("needResp", needResp);
        EventEmmiter.emmit(WechatEventCodeDef.WECHAT_OPENAPI_PUSH, data);
    }

    /**
     * Description: 异步调用扩展插件<br>
     * 
     * @author zhasiwei<br>
     * @taskId <br>
     * @param subsCode
     * @param templateId
     * @param param <br>
     */
    // public static void asyncWechatExpand(String msgId, String fromUserName,
    // String content, String imagePath, String serverPath, String message, String accountId) {
    // LoggerUtil.info(
    // "asyncWechatExpand message = [{0}]", message);
    // EventData data = new EventData();
    // data.put("msgId", msgId);
    // data.put("fromUserName", fromUserName);
    // data.put("content", content);
    // data.put("imagePath", imagePath);
    // data.put("serverPath", serverPath);
    // data.put("message", message);
    // data.put("accountId", accountId);
    //
    //
    // ThreadPoolExecutor executorPool = new ThreadPoolExecutor(8, 64, 16, TimeUnit.SECONDS,
    // new LinkedBlockingQueue<Runnable>());
    // MonitorThread monitor = new MonitorThread(executorPool, 30);
    // Thread monitorThread = new Thread(monitor);
    // monitorThread.start();
    // executorPool.execute(new WechatExpandExecutor(data));
    // }

    /**
     * 签名
     * 
     * @param params
     * @param key
     * @return
     * @throws UtilException
     */
    public static String sign(String key, Map<String, String> map) throws UtilException {
        Map<String, String> treeMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        // 排序
        treeMap.putAll(map);
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> entry : treeMap.entrySet()) {
            if (CommonUtil.isEmpty(entry.getValue())) {
                continue;
            }
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        sb.append("key=");
        sb.append(key);
        return DataUtil.md5(sb.toString()).toUpperCase();
    }

    public static String map2xml(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (Entry<String, String> entry : map.entrySet()) {
            if (CommonUtil.isNotEmpty(entry.getValue())) {
                sb.append(CommonUtil.messageFormat("<{0}>{1}</{2}>", entry.getKey(), entry.getValue(), entry.getKey()));
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> xml2map(String content) {
        Map<String, String> map = new HashMap<String, String>();
        Document doc = XmlUtil.parseXML(content);
        Element root = doc.getRootElement();
        // 得到根元素的所有子节点
        List<Element> elementList = root.elements();
        // 遍历所有子节点
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        return map;
    }

}
