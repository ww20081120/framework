package com.hbasesoft.framework.db.demo.entity;

import org.hibernate.annotations.GenericGenerator;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <Description> T_MEM_ACCOUNT的Entity<br>
 * 
 * @author 工具生成<br>
 * @version 1.0<br>
 * @CreateDate 2018年05月01日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.BaseEntity.bean.BaseEntity <br>
 */
@Entity(name = "T_COURSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEntity extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 3095712295731014196L;

    /** course */
    public static final String COURSE_NAME = "courseName";

    /** ID */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;

    /** course name */
    @Column(name = "COURSE_NAME")
    private String courseName;

    /** remark */
    @Column(name = "REMARK")
    private String remark;

}
