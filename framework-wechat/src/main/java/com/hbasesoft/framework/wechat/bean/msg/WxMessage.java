package com.hbasesoft.framework.wechat.bean.msg;


import com.alibaba.fastjson.JSONObject;
import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * Created by renrui on 2017/8/2.
 */
public class WxMessage extends BaseEntity {
    private static final long serialVersionUID = -5261886937651344514L;
    private String touser;
    //文本 text 图片 image 语言 voice 视频 video
    private String msgtype;

    public void setText(JSONObject text) {
        this.text = text;
    }

    private JSONObject text;
    private String image;
    private String voice;
    private String video;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public JSONObject getText() {
        return text;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
