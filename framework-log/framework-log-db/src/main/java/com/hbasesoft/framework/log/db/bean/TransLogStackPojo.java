package com.hbasesoft.framework.log.db.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> TRANS_LOG_STACK的Pojo<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2014年12月06日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.common.bean.BaseEntity <br>
 */
@Entity(name = "T_SYS_TRANS_LOG_STACK")
public class TransLogStackPojo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /** STACK_ID */
    @Id
    @GeneratedValue(generator = "assignedGenerator")
    @GenericGenerator(name = "assignedGenerator", strategy = "assigned")
    @Column(name = "STACK_ID")
    private String stackId;

    /** TRANS_ID */
    @Column(name = "TRANS_ID")
    private String transId;

    /** PARENT_STACK_ID */
    @Column(name = "PARENT_STACK_ID")
    private String parentStackId;

    /** METHOD */
    @Column(name = "METHOD")
    private String method;

    /** BEGIN_TIME */
    @Column(name = "BEGIN_TIME")
    private java.util.Date beginTime;

    /** END_TIME */
    @Column(name = "END_TIME")
    private java.util.Date endTime;

    /** CONSUME_TIME */
    @Column(name = "CONSUME_TIME")
    private Long consumeTime;

    /** INPUT_PARAM */
    @Column(name = "INPUT_PARAM")
    private String inputParam;

    /** OUTPUT_PARAM */
    @Column(name = "OUTPUT_PARAM")
    private String outputParam;

    /** IS_SUCCESS */
    @Column(name = "IS_SUCCESS")
    private String isSuccess;

    /** SEQ */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ")
    private Integer seq;

    public String getStackId() {
        return this.stackId;
    }

    public void setStackId(String stackId) {
        this.stackId = stackId;
    }

    public String getTransId() {
        return this.transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getParentStackId() {
        return this.parentStackId;
    }

    public void setParentStackId(String parentStackId) {
        this.parentStackId = parentStackId;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public java.util.Date getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(java.util.Date beginTime) {
        this.beginTime = beginTime;
    }

    public java.util.Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(java.util.Date endTime) {
        this.endTime = endTime;
    }

    public Long getConsumeTime() {
        return this.consumeTime;
    }

    public void setConsumeTime(Long consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getInputParam() {
        return this.inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
    }

    public String getOutputParam() {
        return this.outputParam;
    }

    public void setOutputParam(String outputParam) {
        this.outputParam = outputParam;
    }

    public String getIsSuccess() {
        return this.isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

}
