package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity(name = "T_STUDENT")
public class StudentEntity extends BaseEntity {

    public static final String NAME = "name";
    public static final String AGE = "age";

    private static final long serialVersionUID = -5443184537634014662L;

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "AGE")
    private Integer age;

    @Transient
    private Integer score;

    @Transient
    private String courseName;

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(final int age) { this.age = age; }
    public Integer getScore() { return score; }
    public void setScore(final Integer score) { this.score = score; }
    public String getCourseName() { return courseName; }
    public void setCourseName(final String courseName) { this.courseName = courseName; }
}
