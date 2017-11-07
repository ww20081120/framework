package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_MENUENTITY的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_MENUENTITY")
public class MenuentityPojo extends BaseEntity {

    public static final String ACCOUNT_ID = "accountid";

    public static final String MENU_KEY = "menukey";

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

    /** menukey */
    @Column(name = "menukey")
    private String menukey;

    /** name */
    @Column(name = "name")
    private String name;

    /** orders */
    @Column(name = "orders")
    private String orders;

    /** msgtype */
    @Column(name = "msgtype")
    private String msgtype;

    /** templateid */
    @Column(name = "templateid")
    private String templateid;

    /** type */
    @Column(name = "type")
    private String type;

    /** url */
    @Column(name = "url")
    private String url;

    /** fatherid */
    @Column(name = "fatherid")
    private String fatherid;

    /** accountid */
    @Column(name = "accountid")
    private String accountid;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenukey() {
        return this.menukey;
    }

    public void setMenukey(String menukey) {
        this.menukey = menukey;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrders() {
        return this.orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getMsgtype() {
        return this.msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTemplateid() {
        return this.templateid;
    }

    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFatherid() {
        return this.fatherid;
    }

    public void setFatherid(String fatherid) {
        this.fatherid = fatherid;
    }

    public String getAccountid() {
        return this.accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

}
