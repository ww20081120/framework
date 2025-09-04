/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <Description> Test object for verifying object parameter handling <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class TestObject {
    
    private String name;
    
    private int age;
    
    private Date birthDate;
    
    private LocalDate localDate;
    
    private LocalDateTime localDateTime;
    
    private boolean active;
    
    private double score;
    
    // Constructors
    public TestObject() {
    }
    
    public TestObject(String name, int age, Date birthDate, LocalDate localDate, LocalDateTime localDateTime, 
                      boolean active, double score) {
        this.name = name;
        this.age = age;
        this.birthDate = birthDate;
        this.localDate = localDate;
        this.localDateTime = localDateTime;
        this.active = active;
        this.score = score;
    }
    
    // Getters and setters
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
    
    public Date getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    public LocalDate getLocalDate() {
        return localDate;
    }
    
    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }
    
    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    
    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", birthDate=" + birthDate +
                ", localDate=" + localDate +
                ", localDateTime=" + localDateTime +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
}
