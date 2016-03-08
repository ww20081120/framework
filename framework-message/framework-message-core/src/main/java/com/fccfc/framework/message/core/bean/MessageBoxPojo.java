package com.fccfc.framework.message.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> MESSAGE_BOX的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_MSG_MESSAGE_BOX")
public class MessageBoxPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** MESSAGE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    /** RECEIVERS */
    @Column(name = "RECEIVERS")
    private String receivers;

    /** SENDER */
    @Column(name = "SENDER")
    private String sender;

    /** MESSAGE_TYPE */
    @Column(name = "MESSAGE_TYPE")
    private String messageType;

    /** MESSAGE_TEMPLATE_ID */
    @Column(name = "MESSAGE_TEMPLATE_ID")
    private Integer messageTemplateId;

    /** SUBJECT */
    @Column(name = "SUBJECT")
    private String subject;

    /** CONTENT */
    @Column(name = "CONTENT")
    private String content;

    /** ATTACHMENTS_NUM */
    @Column(name = "ATTACHMENTS_NUM")
    private Integer attachmentsNum;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** SEND_TIME */
    @Column(name = "SEND_TIME")
    private java.util.Date sendTime;

    /** NEXT_SEND_TIME */
    @Column(name = "NEXT_SEND_TIME")
    private java.util.Date nextSendTime;

    /** SEND_TIMES */
    @Column(name = "SEND_TIMES")
    private Integer sendTimes;

    /** SEND_TIMES */
    @Column(name = "EXTEND_ATTRS")
    private String extendAttrs;

    public Long getMessageId() {
        return this.messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getReceivers() {
        return this.receivers;
    }

    public void setReceivers(String receivers) {
        this.receivers = receivers;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Integer getMessageTemplateId() {
        return this.messageTemplateId;
    }

    public void setMessageTemplateId(Integer messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAttachmentsNum() {
        return this.attachmentsNum;
    }

    public void setAttachmentsNum(Integer attachmentsNum) {
        this.attachmentsNum = attachmentsNum;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getSendTime() {
        return this.sendTime;
    }

    public void setSendTime(java.util.Date sendTime) {
        this.sendTime = sendTime;
    }

    public java.util.Date getNextSendTime() {
        return this.nextSendTime;
    }

    public void setNextSendTime(java.util.Date nextSendTime) {
        this.nextSendTime = nextSendTime;
    }

    public Integer getSendTimes() {
        return this.sendTimes;
    }

    public void setSendTimes(Integer sendTimes) {
        this.sendTimes = sendTimes;
    }

    public String getExtendAttrs() {
        return extendAttrs;
    }

    public void setExtendAttrs(String extendAttrs) {
        this.extendAttrs = extendAttrs;
    }
}
