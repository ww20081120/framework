package com.hbasesoft.framework.db.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> T_MEM_ACCOUNT的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2018年05月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
@Getter
@Setter
@Entity(name = "T_STUDENT")
public class StudentEntity extends BaseEntity {

    /** name */
    public static final String NAME = "name";

    /** age */
    public static final String AGE = "age";

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -5443184537634014662L;

    /** ID */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "ID")
    private String id;

    /** name */
    @Column(name = "NAME")
    private String name;

    /** age */
    @Column(name = "AGE")
    private Integer age;

    /** score */
    @Transient
    private Integer score;

    /** course name */
    @Transient
    private String courseName;

}
