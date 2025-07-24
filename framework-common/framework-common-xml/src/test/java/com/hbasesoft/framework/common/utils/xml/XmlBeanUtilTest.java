/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.xml;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.hbasesoft.framework.common.utils.UtilException;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <Description> XmlBeanUtil Test <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @CreateDate 2025年8月15日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.xml <br>
 */
public class XmlBeanUtilTest {

    @Test
    public void testXml2Object() throws UtilException {
        // 测试XML转对象
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><student><name>小明</name><age>10</age><remark>"
                + "<![CDATA[小明是位好同学]]></remark></student>";
        
        Student student = XmlBeanUtil.xml2Object(xml, Student.class);
        assertNotNull(student);
        assertEquals("小明", student.getName());
        assertEquals(10, student.getAge());
        assertEquals("小明是位好同学", student.getRemark());
        
        // 测试空XML
        Student nullStudent = XmlBeanUtil.xml2Object(null, Student.class);
        assertNull(nullStudent);
    }

    @Test
    public void testObject2Xml() throws UtilException {
        // 测试对象转XML
        Student student = new Student();
        student.setAge(10);
        student.setName("小明");
        student.setRemark("小明是位好同学");

        String xml = XmlBeanUtil.object2Xml(student);
        assertNotNull(xml);
        assertTrue(xml.contains("<name>小明</name>"));
        assertTrue(xml.contains("<age>10</age>"));
        // 注意：由于CDATA的存在，这里可能需要特殊处理
    }

    @Test
    public void testObject2Element() throws UtilException {
        // 测试对象转Element
        Student student = new Student();
        student.setAge(10);
        student.setName("小明");
        student.setRemark("小明是位好同学");

        assertDoesNotThrow(() -> {
            org.dom4j.Element element = XmlBeanUtil.object2Element(student);
            assertNotNull(element);
            assertEquals("student", element.getName());
        });
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "student")
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

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}