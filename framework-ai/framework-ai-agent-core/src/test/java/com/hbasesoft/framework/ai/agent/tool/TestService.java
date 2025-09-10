/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.ai.agent.tool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <Description> Test service for verifying enhanced parameter handling <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2025年9月1日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.ai.agent.tool <br>
 */
public class TestService {

    @Action(description = "Test method with various parameter types including dates")
    public String testWithDates(@ActionParam(description = "Name parameter") String name,
        @ActionParam(description = "Birth date parameter") Date birthDate,
        @ActionParam(description = "Local date parameter") LocalDate localDate,
        @ActionParam(description = "Local datetime parameter") LocalDateTime localDateTime) {
        return "Test with dates: name=" + name + ", birthDate=" + birthDate + ", localDate=" + localDate
            + ", localDateTime=" + localDateTime;
    }

    @Action(description = "Test method with object parameter")
    public String testWithObject(@ActionParam(description = "Test object parameter") TestObject testObject) {
        return "Test with object: " + testObject.toString();
    }

    @Action(description = "Test method with primitive types")
    public String testWithPrimitives(@ActionParam(description = "Byte parameter") byte byteValue,
        @ActionParam(description = "Short parameter") short shortValue,
        @ActionParam(description = "Float parameter") float floatValue,
        @ActionParam(description = "Char parameter") char charValue) {
        return "Test with primitives: byte=" + byteValue + ", short=" + shortValue + ", float=" + floatValue + ", char="
            + charValue;
    }

    @Action(description = "Test method with list of objects")
    public String testWithObjectList(@ActionParam(description = "List of test objects") List<TestObject> testObjects) {
        StringBuilder sb = new StringBuilder();
        sb.append("Test with object list: [");
        if (testObjects != null) {
            for (int i = 0; i < testObjects.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(testObjects.get(i).toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
