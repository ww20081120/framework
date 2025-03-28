package com.hbasesoft.framework.db.demo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

/**
 * <Description> T_MEM_ACCOUNT的Entity<br>
 *
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2018年05月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.BaseEntity.bean.BaseEntity <br>
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
    @Column(name = "id")
    private String id;

    /** name */
    @Column(name = "name")
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

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getId() {
        return id;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param id <br>
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getName() {
        return name;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param name <br>
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public int getAge() {
        return age;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param age <br>
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public Integer getScore() {
        return score;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param score <br>
     */
    public void setScore(final Integer score) {
        this.score = score;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Description: <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param courseName <br>
     */
    public void setCourseName(final String courseName) {
        this.courseName = courseName;
    }

}
