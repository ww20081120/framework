package com.fccfc.framework.web.manager.bean.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> ANNOUNCEMENT的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2015年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.BaseEntity <br>
 */
@Entity(name = "ANNOUNCEMENT")
public class AnnouncementPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /** 初始状态 */
    public static final String ANNOUNCEMENT_STATE_INITIAL = "I";
    
    /** 审核通过状态 */
    public static final String ANNOUNCEMENT_STATE_SUCCESS = "S";
    
    /** 审核不通过状态 */
    public static final String ANNOUNCEMENT_STATE_FAILURE = "F";

    /** ANNOUNCEMENT_ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ANNOUNCEMENT_ID")
    private Integer announcementId;

    /** TITLE */
    @Column(name = "TITLE")
    private String title;

    /** CONTENT */
    @Column(name = "CONTENT")
    private String content;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** STATE */
    @Column(name = "STATE")
    private String state;

    /** STATE_DATE */
    @Column(name = "STATE_DATE")
    private java.util.Date stateDate;

    /** OPERATOR_ID */
    @Column(name = "OPERATOR_ID")
    private Integer operatorId;

    /** COMMENTS */
    @Column(name = "COMMENTS")
    private String comments;

    public Integer getAnnouncementId() {
        return this.announcementId;
    }

    public void setAnnouncementId(Integer announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public java.util.Date getStateDate() {
        return this.stateDate;
    }

    public void setStateDate(java.util.Date stateDate) {
        this.stateDate = stateDate;
    }

    public Integer getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
