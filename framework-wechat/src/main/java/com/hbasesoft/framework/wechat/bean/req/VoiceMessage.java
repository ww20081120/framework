package com.hbasesoft.framework.wechat.bean.req;

/**
 * 音频消息
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
public class VoiceMessage extends BaseMessage {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3434581379574885848L;

    // 媒体ID
    private String MediaId;

    // 语音格式
    private String Format;

    public String getMediaId() {
        return MediaId;
    }

    public void setMediaId(String mediaId) {
        MediaId = mediaId;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }
}
