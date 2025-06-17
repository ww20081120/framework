package com.hbasesoft.framework.ai.demo.jmanus.planning.model.po;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 计划模版实体类， 用于存储计划模版的基本信息
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_template")
public class PlanTemplatePo extends BaseEntity {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "plan_template_id", length = 32)
    private String planTemplateId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "user_request", length = 4000)
    private String userRequest;

    @Column(name = "create_time", nullable = false)
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    private Date updateTime;
}
