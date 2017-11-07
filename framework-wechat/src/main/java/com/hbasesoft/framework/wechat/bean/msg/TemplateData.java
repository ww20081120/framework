package com.hbasesoft.framework.wechat.bean.msg;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description>模板消息小数据对象 <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.common.bean <br>
 */
public class TemplateData extends BaseEntity {
    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -211910538110502107L;

    private String value;

    private String color;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
