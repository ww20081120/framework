package com.hbasesoft.framework.db.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

/**
 * <Description> T_MEM_ACCOUNT的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2018年05月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.api.bean.BaseEntity <br>
 */
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
    private int age;

    /** score */
    @Transient
    private Integer score;

    /** course name */
    @Transient
    private String courseName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

}
