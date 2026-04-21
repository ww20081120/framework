package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "T_STUDENT")
@Getter
@Setter
public class StudentEntity extends BaseEntity {

    public static final String NAME = "name";
    public static final String AGE = "age";

    private static final long serialVersionUID = -5443184537634014662L;

    @Id
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
}
