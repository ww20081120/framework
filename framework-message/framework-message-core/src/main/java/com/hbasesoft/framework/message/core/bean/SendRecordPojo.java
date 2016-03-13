package com.hbasesoft.framework.message.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> SEND_RECORD的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_MSG_SEND_RECORD")
public class SendRecordPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** SEND_RECORD_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEND_RECORD_ID")
    private Long sendRecordId;

    /** MESSAGE_ID */
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    /** CONTACT_CHANNEL_ID */
    @Column(name = "CONTACT_CHANNEL_ID")
    private Integer contactChannelId;

    /** SEND_TIME */
    @Column(name = "SEND_TIME")
    private java.util.Date sendTime;

    /** RESULT */
    @Column(name = "RESULT")
    private String result;

    public Long getSendRecordId() {
        return this.sendRecordId;
    }

    public void setSendRecordId(Long sendRecordId) {
        this.sendRecordId = sendRecordId;
    }

    public Long getMessageId() {
        return this.messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Integer getContactChannelId() {
        return this.contactChannelId;
    }

    public void setContactChannelId(Integer contactChannelId) {
        this.contactChannelId = contactChannelId;
    }

    public java.util.Date getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(java.util.Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
