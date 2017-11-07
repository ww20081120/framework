package com.hbasesoft.framework.wechat.bean.resp;

/**
 * 音乐消息
 * 
 * @author Administrator
 */
public class MusicMessageResp extends BaseMessageResp {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 711004143933637348L;

    // 音乐
    private Music Music;

    public Music getMusic() {
        return Music;
    }

    public void setMusic(Music music) {
        Music = music;
    }
}
