package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_AUTORESPONSE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_AUTORESPONSE")
public class AutoresponsePojo extends BaseEntity {

    public static final String ACCOUNT_ID = "accountid";

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

    /** addtime */
    @Column(name = "addtime")
    private String addtime;

    /** keyword */
    @Column(name = "keyword")
    private String keyword;

    /** msgtype */
    @Column(name = "msgtype")
    private String msgtype;

    /** rescontent */
    @Column(name = "rescontent")
    private String rescontent;

    /** templatename */
    @Column(name = "templatename")
    private String templatename;

    /** accountid */
    @Column(name = "accountid")
    private String accountid;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddtime() {
        return this.addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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

    public String getTemplatename() {
        return this.templatename;
    }

    public void setTemplatename(String templatename) {
        this.templatename = templatename;
    }

    public String getAccountid() {
        return this.accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

}
