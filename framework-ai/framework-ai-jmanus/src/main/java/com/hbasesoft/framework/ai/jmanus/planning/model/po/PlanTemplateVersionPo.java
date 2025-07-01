package com.hbasesoft.framework.ai.jmanus.planning.model.po;

import com.hbasesoft.framework.db.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 计划模板版本实体类， 用于保存计划模板的各个版本
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_template_version")
public class PlanTemplateVersionPo extends BaseEntity {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_template_id", nullable = false, length = 32)
    private String planTemplateId;

    @Column(name = "version_index", nullable = false)
    private Integer versionIndex;

    @Column(name = "plan_json", columnDefinition = "TEXT", nullable = false)
    private String planJson;

    @Column(name = "create_time", nullable = false)
    private Date createTime;
}
