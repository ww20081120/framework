package com.hbasesoft.framework.wechat.bean.openapiResp;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.hbasesoft.framework.db.core.BaseEntity;

@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)  
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlType(name = "xml")  
@XmlRootElement(name = "xml")  
public class NewsMessageResp extends BaseEntity {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 118640502308960148L;
    
    // 图文消息个数，限制为10条以内
    private int ArticleCount;

    // 多条图文消息信息，默认第一个item为大图	
    @XmlElementWrapper(name = "Articles")  
    @XmlElement(name = "item") 
    private List<Article> Articles;

 // 接收方帐号（收到的OpenID）
    private String ToUserName;

    // 开发者微信号
    private String FromUserName;

    // 消息创建时间 （整型）
    private long CreateTime;

    // 消息类型（text/music/news）
    private String MsgType;
    
    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }


	public List<Article> getArticles() {
		return Articles;
	}

	public void setArticles(List<Article> articles) {
		Articles = articles;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}
    
    
}
