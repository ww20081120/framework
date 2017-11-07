package com.hbasesoft.framework.wechat.bean.req;

/**
 * 图片消息
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
public class ImageMessage extends BaseMessage {
	/**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -1936175021722992875L;
    // 图片链接
	private String PicUrl;

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
}
