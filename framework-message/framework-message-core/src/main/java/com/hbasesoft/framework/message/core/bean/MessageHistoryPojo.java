package com.hbasesoft.framework.message.core.bean;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> MESSAGE_HISTORY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
public class MessageHistoryPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** MESSAGE_ID */
    @Id
    // @GeneratedValue(generator = "assignedGenerator")
    // @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
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

    /** SEND_TIMES */
    @Column(name = "SEND_TIMES")
    private Integer sendTimes;

    /** RESULT */
    @Column(name = "RESULT")
    private String result;

    /** RESULT */
    @Column(name = "EXP_DATE")
    private Date expDate;

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

    public Integer getSendTimes() {
        return this.sendTimes;
    }

    public void setSendTimes(Integer sendTimes) {
        this.sendTimes = sendTimes;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getExtendAttrs() {
        return extendAttrs;
    }

    public void setExtendAttrs(String extendAttrs) {
        this.extendAttrs = extendAttrs;
    }
}
