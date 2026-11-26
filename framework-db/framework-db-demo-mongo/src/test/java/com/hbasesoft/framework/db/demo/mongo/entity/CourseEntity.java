package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "T_COURSE")
@Getter
@Setter
public class CourseEntity extends BaseEntity {

    private static final long serialVersionUID = 3095712295731014196L;

    public static final String COURSE_NAME = "courseName";

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "COURSE_NAME")
    private String courseName;

    @Column(name = "REMARK")
    private String remark;
}
