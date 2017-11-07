package com.hbasesoft.framework.wechat.bean.msg;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> 模板消息对象<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.common.bean <br>
 */
public class WxTemplate extends BaseEntity {
    
    public static final String TOP_COLOR = "#000";
    
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 6286202733300199535L;

    /**
     * OPENID
     */
    private String touser;

    /**
     * 模板id
     */
    private String template_id;

    private String url;

    private String topcolor;

    private Map<String, TemplateData> data;

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public void setTopcolor(String topcolor) {
        this.topcolor = topcolor;
    }

    public Map<String, TemplateData> getData() {
        return data;
    }

    public void setData(Map<String, TemplateData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
