package com.hbasesoft.framework.wechat.bean.msg;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> 模板消息返回结果<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月23日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.common.bean <br>
 */
public class WXResult extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -2853132897575700667L;

    /**
     * 0成功,其他失败
     */
    private String errcode;

    /**
     * 返回信息
     */
    private String errmsg;

    private String msgid;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
