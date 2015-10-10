package com.fccfc.framework.message.core.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.fccfc.framework.db.core.BaseEntity;

/**
 * <Description> ATTACHMENTS的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年11月30日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "ATTACHMENTS")
public class AttachmentsPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** ATTACHMENTS_ID */
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ATTACHMENTS")
//    @SequenceGenerator(name = "SEQ_ATTACHMENTS", sequenceName = "SEQ_ATTACHMENTS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTACHMENTS_ID")
    private Long attachmentsId;

    /** ATTACHMENTS_TYPE */
    @Column(name = "ATTACHMENTS_TYPE")
    private String attachmentsType;

    /** ATTACHMENTS_NAME */
    @Column(name = "ATTACHMENTS_NAME")
    private String attachmentsName;

    /** IS_REMOTE */
    @Column(name = "IS_REMOTE")
    private String isRemote;

    /** FILE_SIZE */
    @Column(name = "FILE_SIZE")
    private Long fileSize;

    /** FILE_PATH */
    @Column(name = "FILE_PATH")
    private String filePath;

    /** DOWNLOADS_NUM */
    @Column(name = "DOWNLOADS_NUM")
    private Integer downloadsNum;

    /** IS_PICTURE */
    @Column(name = "IS_PICTURE")
    private String isPicture;

    /** IS_THUMB */
    @Column(name = "IS_THUMB")
    private String isThumb;

    /** THUMB_PATH */
    @Column(name = "THUMB_PATH")
    private String thumbPath;

    /** CREATE_TIME */
    @Column(name = "CREATE_TIME")
    private java.util.Date createTime;

    /** EXP_TIME */
    @Column(name = "EXP_TIME")
    private java.util.Date expTime;

    /** UPDATE_TIME */
    @Column(name = "UPDATE_TIME")
    private String updateTime;

    public Long getAttachmentsId() {
        return attachmentsId;
    }

    public void setAttachmentsId(Long attachmentsId) {
        this.attachmentsId = attachmentsId;
    }

    public String getAttachmentsType() {
        return this.attachmentsType;
    }

    public void setAttachmentsType(String attachmentsType) {
        this.attachmentsType = attachmentsType;
    }

    public String getAttachmentsName() {
        return this.attachmentsName;
    }

    public void setAttachmentsName(String attachmentsName) {
        this.attachmentsName = attachmentsName;
    }

    public String getIsRemote() {
        return this.isRemote;
    }

    public void setIsRemote(String isRemote) {
        this.isRemote = isRemote;
    }

    public Long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getDownloadsNum() {
        return this.downloadsNum;
    }

    public void setDownloadsNum(Integer downloadsNum) {
        this.downloadsNum = downloadsNum;
    }

    public String getIsPicture() {
        return this.isPicture;
    }

    public void setIsPicture(String isPicture) {
        this.isPicture = isPicture;
    }

    public String getIsThumb() {
        return this.isThumb;
    }

    public void setIsThumb(String isThumb) {
        this.isThumb = isThumb;
    }

    public String getThumbPath() {
        return this.thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public java.util.Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.util.Date getExpTime() {
        return this.expTime;
    }

    public void setExpTime(java.util.Date expTime) {
        this.expTime = expTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
