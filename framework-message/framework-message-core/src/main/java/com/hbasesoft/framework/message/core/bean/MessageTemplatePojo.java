package com.hbasesoft.framework.message.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> MESSAGE_TEMPLATE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "T_MSG_MESSAGE_TEMPLATE")
public class MessageTemplatePojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** MESSAGE_TEMPLATE_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_TEMPLATE_ID")
    private Integer messageTemplateId;

    /** MESSAGE_TEMPLATE_CODE */
    @Column(name = "MESSAGE_TEMPLATE_CODE")
    private String messageTemplateCode;

    /** DIRECTORY_CODE */
    @Column(name = "DIRECTORY_CODE")
    private String directoryCode;

    /** NAME */
    @Column(name = "NAME")
    private String name;

    /** TEMPLATE */
    @Column(name = "TEMPLATE")
    private String template;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** CONTACT_CHANNEL_IDS */
    @Column(name = "CONTACT_CHANNEL_IDS")
    private String contactChannelIds;

    /** STATE_TIME */
    @Column(name = "STATE_TIME")
    private java.util.Date stateTime;

    /** DELAY */
    @Column(name = "DELAY")
    private Integer delay;

    /** RESEND_TIMES */
    @Column(name = "RESEND_TIMES")
    private Integer resendTimes;

    /** SAVE_HISTORY */
    @Column(name = "SAVE_HISTORY")
    private String saveHistory;

    /** SAVE_DAY */
    @Column(name = "SAVE_DAY")
    private Integer saveDay;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    public Integer getMessageTemplateId() {
        return this.messageTemplateId;
    }

    public void setMessageTemplateId(Integer messageTemplateId) {
        this.messageTemplateId = messageTemplateId;
    }

    public String getMessageTemplateCode() {
        return this.messageTemplateCode;
    }

    public void setMessageTemplateCode(String messageTemplateCode) {
        this.messageTemplateCode = messageTemplateCode;
    }

    public String getDirectoryCode() {
        return this.directoryCode;
    }

    public void setDirectoryCode(String directoryCode) {
        this.directoryCode = directoryCode;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContactChannelIds() {
        return this.contactChannelIds;
    }

    public void setContactChannelIds(String contactChannelIds) {
        this.contactChannelIds = contactChannelIds;
    }

    public java.util.Date getStateTime() {
        return this.stateTime;
    }

    public void setStateTime(java.util.Date stateTime) {
        this.stateTime = stateTime;
    }

    public Integer getDelay() {
        return this.delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getResendTimes() {
        return this.resendTimes;
    }

    public void setResendTimes(Integer resendTimes) {
        this.resendTimes = resendTimes;
    }

    public String getSaveHistory() {
        return this.saveHistory;
    }

    public void setSaveHistory(String saveHistory) {
        this.saveHistory = saveHistory;
    }

    public Integer getSaveDay() {
        return this.saveDay;
    }

    public void setSaveDay(Integer saveDay) {
        this.saveDay = saveDay;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

}
