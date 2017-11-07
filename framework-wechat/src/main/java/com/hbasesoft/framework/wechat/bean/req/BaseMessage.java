package com.hbasesoft.framework.wechat.bean.req;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * 消息基类（普通用户 -> 公众帐号）
 * 
 * @author 捷微团队
 * @date 2013-05-19
 */
public class BaseMessage extends BaseEntity {
/**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -3402346601605755860L;
    // 开发者微信号
    private String ToUserName;

    // 发送方帐号（一个OpenID）
    private String FromUserName;

    // 消息创建时间 （整型）
    private long CreateTime;

    // 消息类型（text/image/location/link）
    private String MsgType;

    // 消息id，64位整型
    private long MsgId;

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

    public long getMsgId() {
        return MsgId;
    }

    public void setMsgId(long msgId) {
        MsgId = msgId;
    }
}
