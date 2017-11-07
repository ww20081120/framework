package com.hbasesoft.framework.wechat.bean.req;

/**
 * 文本消息
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
public class TextMessage extends BaseMessage {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 2716391166648156379L;

    // 消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}