package com.hbasesoft.framework.wechat.bean.openapiResp;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * 图文model
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
@XmlType()  
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)  
@XmlAccessorType(XmlAccessType.NONE)  
@XmlRootElement(name = "item")  
public class Article extends BaseEntity {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1480855814444347245L;

    // 图文消息名称
    @XmlElement(name = "Title")
    private String Title;

    // 图文消息描述
    @XmlElement(name = "Description")
    private String Description;

    // 图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80，限制图片链接的域名需要与开发者填写的基本资料中的Url一致
    @XmlElement(name = "PicUrl")
    private String Picurl;

    // 点击图文消息跳转链接
    @XmlElement(name = "Url")
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

	public String getPicurl() {
		return Picurl;
	}

	public void setPicurl(String picurl) {
		Picurl = picurl;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}


}
