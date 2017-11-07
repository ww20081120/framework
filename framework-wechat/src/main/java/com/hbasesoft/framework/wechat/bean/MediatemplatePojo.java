package com.hbasesoft.framework.wechat.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> WEIXIN_MEDIATEMPLATE的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2017年04月17日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "WEIXIN_MEDIATEMPLATE")
public class MediatemplatePojo extends BaseEntity {

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

    /** templdatename */
    @Column(name = "templdatename")
    private String templdatename;

    /** sc_filepath */
    @Column(name = "sc_filepath")
    private String scFilepath;

    /** type */
    @Column(name = "type")
    private String type;

    /** group_id */
    @Column(name = "group_id")
    private String groupId;

    /** org_code */
    @Column(name = "org_code")
    private String orgCode;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTempldatename() {
        return this.templdatename;
    }

    public void setTempldatename(String templdatename) {
        this.templdatename = templdatename;
    }

    public String getScFilepath() {
        return this.scFilepath;
    }

    public void setScFilepath(String scFilepath) {
        this.scFilepath = scFilepath;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOrgCode() {
        return this.orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

}
