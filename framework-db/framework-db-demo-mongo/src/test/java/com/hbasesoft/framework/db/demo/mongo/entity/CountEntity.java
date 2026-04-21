package com.hbasesoft.framework.db.demo.mongo.entity;

import com.hbasesoft.framework.db.core.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountEntity extends BaseEntity {

    private static final long serialVersionUID = -3115703439314376932L;

    private Integer total;
    private String name;
}
