/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.util.Arrays;

/**
 * <Description> Test service with array parameters for testing array support <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月10日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class ArraySupportTestService {
    
    @Action(description = "Test method with array parameters")
    public String testWithArrays(
        @ActionParam(description = "Name parameter") String name,
        @ActionParam(description = "Integer array parameter") int[] numbers,
        @ActionParam(description = "String array parameter") String[] tags,
        @ActionParam(description = "Boolean array parameter") boolean[] flags) {
        
        StringBuilder result = new StringBuilder();
        result.append("Test with arrays: name=").append(name);
        result.append(", numbers=").append(Arrays.toString(numbers));
        result.append(", tags=").append(Arrays.toString(tags));
        result.append(", flags=").append(Arrays.toString(flags));
        return result.toString();
    }
    
    @Action(description = "Test method with object containing arrays")
    public String testWithObjectContainingArrays(
        @ActionParam(description = "Test object with array fields") ArraySupportTestObject testObject) {
        
        return "Test with object containing arrays: " + testObject.toString();
    }
    
    @Action(description = "Test method with string array parameter only")
    public String testWithStringArray(
        @ActionParam(description = "String array parameter") String[] values) {
        
        return "Test with string array: " + Arrays.toString(values);
    }
}
