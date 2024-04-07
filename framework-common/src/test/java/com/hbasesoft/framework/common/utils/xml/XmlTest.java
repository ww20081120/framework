/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.xml;


import org.junit.jupiter.api.Test;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public class XmlTest {

    /** number */
    private static final int NUM_10 = 10;

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void bean2xml() {

        Student student = new Student();
        student.setAge(NUM_10);
        student.setName("小明");
        student.setRemark("小明是位好同学，<hello>年年三好学生👩‍🎓");

        System.out.println(XmlBeanUtil.object2Xml(student));
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     *         <br>
     */
    @Test
    public void xml2bean() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><student><name>小明</name><age>10</age><remark>"
            + "<![CDATA[小明是位好<abcdedf>同学，年年三好学生👩‍🎓]]></remark></student>";
        Student student = XmlBeanUtil.xml2Object(xml, Student.class);
        System.out.println(student.getAge());
        System.out.println(student.getName());
        System.out.println(student.getRemark());
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    public static class Student {

        /** name */
        @XmlElement
        private String name;

        /** age */
        @XmlElement
        private int age;

        /** remark */
        @XmlElement
        @XmlJavaTypeAdapter(CDATAAdapter.class)
        private String remark;
    }

}
