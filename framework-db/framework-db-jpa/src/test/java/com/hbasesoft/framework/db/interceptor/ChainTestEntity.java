/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.db.interceptor;

import com.hbasesoft.framework.db.core.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 拦截链集成测试用的最小实体——仅为 H2 内存库提供可持久化映射，不承载任何业务语义。
 *
 * @author 王伟
 * @version 1.0
 * @since 4.4.3
 */
@Entity
@Table(name = "t_chain_test")
public class ChainTestEntity extends BaseEntity {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 名称——谓词注入与实体改写断言的观测字段 */
    private String name;

    /**
     * 获取主键。
     *
     * @return 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键。
     *
     * @param id 主键
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * 获取名称。
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称。
     *
     * @param name 名称
     */
    public void setName(final String name) {
        this.name = name;
    }
}
