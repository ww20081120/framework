package com.hbasesoft.framework.wechat.bean.resp;

import java.util.List;

public class NewsMessageResp extends BaseMessageResp {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 118640502308960148L;

    // 图文消息个数，限制为10条以内
    private int ArticleCount;

    // 多条图文消息信息，默认第一个item为大图	
    private List<Article> Articles;

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
}
