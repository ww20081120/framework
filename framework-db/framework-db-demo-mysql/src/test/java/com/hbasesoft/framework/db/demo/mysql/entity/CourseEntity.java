package com.hbasesoft.framework.db.demo.mysql.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "T_COURSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity {

    private static final long serialVersionUID = 3095712295731014196L;

    public static final String COURSE_NAME = "courseName";

    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    @Column(name = "COURSE_NAME")
    private String courseName;

    @Column(name = "REMARK")
    private String remark;
}
