package com.hbasesoft.framework.wechat.bean.resp;

import javax.xml.bind.annotation.XmlRootElement;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * 图文model
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
@XmlRootElement(name = "Articles")  
public class Article extends BaseEntity {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 1480855814444347245L;

    // 图文消息名称
    private String Title;

    // 图文消息描述
    private String Description;

    // 图片链接，支持JPG、PNG格式，较好的效果为大图640*320，小图80*80，限制图片链接的域名需要与开发者填写的基本资料中的Url一致
    private String PicUrl;

    // 点击图文消息跳转链接
    private String Url;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return null == Description ? "" : Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPicUrl() {
        return null == PicUrl ? "" : PicUrl;
    }

    public void setPicUrl(String picUrl) {
        PicUrl = picUrl;
    }

    public String getUrl() {
        return null == Url ? "" : Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

}
