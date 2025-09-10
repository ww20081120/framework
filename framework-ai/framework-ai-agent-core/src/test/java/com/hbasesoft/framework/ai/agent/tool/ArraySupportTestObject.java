/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

/**
 * <Description> Test object with array fields for testing array support <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class ArraySupportTestObject {
    
    private String name;
    
    private int[] numbers;
    
    private String[] tags;
    
    private boolean[] flags;
    
    // Default constructor
    public ArraySupportTestObject() {
    }
    
    // Constructor with parameters
    public ArraySupportTestObject(String name, int[] numbers, String[] tags, boolean[] flags) {
        this.name = name;
        this.numbers = numbers;
        this.tags = tags;
        this.flags = flags;
    }
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int[] getNumbers() {
        return numbers;
    }
    
    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }
    
    public String[] getTags() {
        return tags;
    }
    
    public void setTags(String[] tags) {
        this.tags = tags;
    }
    
    public boolean[] getFlags() {
        return flags;
    }
    
    public void setFlags(boolean[] flags) {
        this.flags = flags;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArraySupportTestObject{name='").append(name).append("', ");
        sb.append("numbers=[");
        if (numbers != null) {
            for (int i = 0; i < numbers.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(numbers[i]);
            }
        }
        sb.append("], tags=[");
        if (tags != null) {
            for (int i = 0; i < tags.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("'").append(tags[i]).append("'");
            }
        }
        sb.append("], flags=[");
        if (flags != null) {
            for (int i = 0; i < flags.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(flags[i]);
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
