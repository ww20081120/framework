package com.hbasesoft.framework.wechat.bean.req;

/**
 * 链接消息
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
public class LinkMessage extends BaseMessage {
	/**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -8005621493310480071L;
    // 消息标题
	private String Title;
	// 消息描述
	private String Description;
	// 消息链接
	private String Url;

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}
}
