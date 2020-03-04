/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.rule.demo.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2017年9月5日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.rule.plugin.transaction.entity <br>
 */
@Entity(name = "Employee")
public class Employee {

    /**
     * serialVersionUID <br>
     */
    private static final long serialVersionUID = -1869998461647323272L;

    /** ID */
    @Id
    @GeneratedValue(generator = "paymentableGenerator")
    @GenericGenerator(name = "paymentableGenerator", strategy = "uuid")
    @Column(name = "ID")
    private String id;

    /** name */
    @Column(name = "name")
    private String name;

    /** age */
    @Column(name = "age")
    private int age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * <Description> <br>
     * 
     * @author 王伟<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate Mar 2, 2020 <br>
     * @since V1.0<br>
     * @see com.hbasesoft.framework.rule.demo.bean <br>
     */
    @Getter
    @Setter
    public static class AAA {
        
        /** entity */
        private Employee entity;

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param args <br>
     */
    public static void main(String[] args) {

        // 构造上下文：准备比如变量定义等等表达式运行需要的上下文数据
        EvaluationContext context = new StandardEvaluationContext();

        // 创建解析器：提供SpelExpressionParser默认实现
        ExpressionParser parser = new SpelExpressionParser();

        // 解析表达式：使用ExpressionParser来解析表达式为相应的Expression对象
        Expression expression = parser.parseExpression("entity?.name eq 'C'");

        AAA flowBean = new AAA();
        Employee emp = new Employee();
        emp.setName("C");
        flowBean.entity = emp;

        Object value = expression.getValue(context, flowBean);
        System.out.println(value);

    }
}
