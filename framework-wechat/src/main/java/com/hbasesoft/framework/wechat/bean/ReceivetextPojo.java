package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_RECEIVETEXT的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_RECEIVETEXT")
public class ReceivetextPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "ID")
    private String id;

    /** content */
    @Column(name = "content")
    private String content;

    /** createtime */
    @Column(name = "createtime")
    private java.util.Date createtime;

    /** fromusername */
    @Column(name = "fromusername")
    private String fromusername;

    /** msgid */
    @Column(name = "msgid")
    private String msgid;

    /** msgtype */
    @Column(name = "msgtype")
    private String msgtype;

    /** rescontent */
    @Column(name = "rescontent")
    private String rescontent;

    /** response */
    @Column(name = "response")
    private String response;

    /** tousername */
    @Column(name = "tousername")
    private String tousername;

    /** accountid */
    @Column(name = "accountid")
    private String accountid;

    /** nickname */
    @Column(name = "nickname")
    private String nickname;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public java.util.Date getCreatetime() {
        return this.createtime;
    }

    public void setCreatetime(java.util.Date createtime) {
        this.createtime = createtime;
    }

    public String getFromusername() {
        return this.fromusername;
    }

    public void setFromusername(String fromusername) {
        this.fromusername = fromusername;
    }

    public String getMsgid() {
        return this.msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getRescontent() {
        return this.rescontent;
    }

    public void setRescontent(String rescontent) {
        this.rescontent = rescontent;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTousername() {
        return this.tousername;
    }

    public void setTousername(String tousername) {
        this.tousername = tousername;
    }

    public String getAccountid() {
        return this.accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
