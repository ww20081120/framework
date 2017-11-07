package com.hbasesoft.framework.wechat.bean.resp;

public class TextMessageResp extends BaseMessageResp {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3957793193186620252L;

    // 回复的消息内容
    private String Content;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
