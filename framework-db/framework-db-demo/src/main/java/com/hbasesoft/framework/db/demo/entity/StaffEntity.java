/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.demo.entity;

import java.util.Date;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2024年7月20日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.db.demo.entity <br>
 */
@Entity
@Table(name = "t_staff")
@Getter
@Setter
public class StaffEntity extends BaseEntity {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = 704766015383881438L;

    /** */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // DB2使用INT类型，但在Java中通常使用Long以适应更大的范围

    /** */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /** */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /** */
    @Column(name = "position")
    private String position;

    /** */
    @Column(name = "department")
    private String department;

    /** */
    @Column(name = "salary")
    private Double salary; // 在Java中DECIMAL类型通常映射为Double或BigDecimal

    /** */
    @Column(name = "hire_date")
    private Date hireDate;
}
