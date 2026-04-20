/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.common.utils.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.dom4j.Element;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.UtilException;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * XML 工具类测试
 * <p>
 * 测试覆盖范围：
 * <ul>
 * <li>XmlBeanUtil 的所有公共方法</li>
 * <li>CDATAAdapter 的 marshal 和 unmarshal 方法</li>
 * <li>XmlFilterReader 的 read 方法</li>
 * <li>XML 序列化和反序列化</li>
 * <li>异常处理</li>
 * </ul>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年6月7日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.xml <br>
 */
@DisplayName("XML 工具类测试")
public class XmlTest {

    /** 测试常量 */
    private static final int TEST_AGE = 10;

    private static final String TEST_NAME = "小明";

    private static final String TEST_REMARK = "小明是位好同学，<hello>年年三好学生👩‍🎓";

    @Test
    @DisplayName("测试对象转 XML - 基本功能")
    public void testObject2Xml() {
        // Given
        Student student = new Student();
        student.setAge(TEST_AGE);
        student.setName(TEST_NAME);
        student.setRemark(TEST_REMARK);

        // When
        String xml = XmlBeanUtil.object2Xml(student);

        // Then
        assertNotNull(xml);
        assertTrue(xml.contains("<?xml version=\"1.0\""));
        assertTrue(xml.contains("<student>"));
        assertTrue(xml.contains("<name>" + TEST_NAME + "</name>"));
        assertTrue(xml.contains("<age>" + TEST_AGE + "</age>"));
        assertTrue(xml.contains("<remark>"));
        assertTrue(xml.contains("<![CDATA["));
        assertTrue(xml.contains("]]>"));
    }

    @Test
    @DisplayName("测试对象转 XML - 包含特殊字符")
    public void testObject2XmlWithSpecialCharacters() {
        // Given
        Student student = new Student();
        student.setAge(TEST_AGE);
        student.setName("<test>&\"'特殊字符</test>");
        student.setRemark("包含<>&\"'字符的备注");

        // When
        String xml = XmlBeanUtil.object2Xml(student);

        // Then
        assertNotNull(xml);
        assertTrue(xml.contains("<test>"));
        assertTrue(xml.contains("&"));
    }

    @Test
    @DisplayName("测试对象转 XML - null 对象抛出异常")
    public void testObject2XmlWithNull() {
        // When & Then
        UtilException ex = assertThrows(UtilException.class, () -> XmlBeanUtil.object2Xml(null));
        assertTrue(ex.getMessage().contains("XML"));
    }

    @Test
    @DisplayName("测试 XML 转对象 - 基本功能")
    public void testXml2Object() {
        // Given
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><student><name>" + TEST_NAME + "</name><age>" + TEST_AGE
            + "</age><remark>" + "<![CDATA[" + TEST_REMARK + "]]></remark></student>";

        // When
        Student student = XmlBeanUtil.xml2Object(xml, Student.class);

        // Then
        assertNotNull(student);
        assertEquals(TEST_NAME, student.getName());
        assertEquals(TEST_AGE, student.getAge());
        assertEquals(TEST_REMARK, student.getRemark());
    }

    @Test
    @DisplayName("测试 XML 转对象 - null XML 返回 null")
    public void testXml2ObjectWithNull() {
        // When
        Student student = XmlBeanUtil.xml2Object(null, Student.class);

        // Then
        assertNull(student);
    }

    @Test
    @DisplayName("测试 XML 转对象 - 空字符串返回 null")
    public void testXml2ObjectWithEmptyString() {
        // When
        Student student = XmlBeanUtil.xml2Object("", Student.class);

        // Then
        assertNull(student);
    }

    @Test
    @DisplayName("测试 XML 转对象 - 无效 XML 抛出异常")
    public void testXml2ObjectWithInvalidXml() {
        // Given
        String invalidXml = "<student><name>测试</name>";

        // When & Then
        assertThrows(UtilException.class, () -> XmlBeanUtil.xml2Object(invalidXml, Student.class));
    }

    @Test
    @DisplayName("测试对象转 Element - 基本功能")
    public void testObject2Element() {
        // Given
        Student student = new Student();
        student.setAge(TEST_AGE);
        student.setName(TEST_NAME);
        student.setRemark(TEST_REMARK);

        // When
        Element element = XmlBeanUtil.object2Element(student);

        // Then
        assertNotNull(element);
        assertEquals("student", element.getName());
        assertEquals(TEST_NAME, element.elementText("name"));
        assertEquals(String.valueOf(TEST_AGE), element.elementText("age"));
        assertTrue(element.elementText("remark").contains(TEST_REMARK));
    }

    @Test
    @DisplayName("测试对象转 Element - null 对象抛出异常")
    public void testObject2ElementWithNull() {
        // When & Then
        assertThrows(UtilException.class, () -> XmlBeanUtil.object2Element(null));
    }

    @Test
    @DisplayName("测试 CDATAAdapter - marshal 方法")
    public void testCDATAMarshal() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();
        String value = "测试内容<特殊字符>";

        // When
        String result = adapter.marshal(value);

        // Then
        assertEquals("<![CDATA[测试内容<特殊字符>]]>", result);
    }

    @Test
    @DisplayName("测试 CDATAAdapter - marshal null 值")
    public void testCDATAMarshalNull() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();

        // When
        String result = adapter.marshal(null);

        // Then
        assertEquals("<![CDATA[null]]>", result);
    }

    @Test
    @DisplayName("测试 CDATAAdapter - unmarshal 方法 - 包含 CDATA 包装")
    public void testCDATAUnmarshalWithWrapper() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();
        String value = "<![CDATA[测试内容]]>";

        // When
        String result = adapter.unmarshal(value);

        // Then
        assertEquals("测试内容", result);
    }

    @Test
    @DisplayName("测试 CDATAAdapter - unmarshal 方法 - 不包含 CDATA 包装")
    public void testCDATAUnmarshalWithoutWrapper() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();
        String value = "普通内容";

        // When
        String result = adapter.unmarshal(value);

        // Then
        assertEquals("普通内容", result);
    }

    @Test
    @DisplayName("测试 CDATAAdapter - unmarshal null 值")
    public void testCDATAUnmarshalNull() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();

        // When
        String result = adapter.unmarshal(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("测试 CDATAAdapter - unmarshal 空字符串")
    public void testCDATAUnmarshalEmpty() throws Exception {
        // Given
        CDATAAdapter adapter = new CDATAAdapter();

        // When
        String result = adapter.unmarshal("");

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("测试 XmlFilterReader - 读取正常字符")
    public void testXmlFilterReaderNormalCharacters() throws Exception {
        // Given
        String testString = "正常字符测试";
        StringReader reader = new StringReader(testString);
        XmlFilterReader filterReader = new XmlFilterReader(reader);
        char[] buffer = new char[100];

        // When
        int readCount = filterReader.read(buffer, 0, buffer.length);

        // Then
        assertEquals(testString.length(), readCount);
        assertEquals(testString, new String(buffer, 0, readCount));
    }

    @Test
    @DisplayName("测试 XmlFilterReader - 过滤控制字符")
    public void testXmlFilterReaderControlCharacters() throws Exception {
        // Given
        String testString = "ABC\u0000\u0001\u0002DEF"; // 包含控制字符
        StringReader reader = new StringReader(testString);
        XmlFilterReader filterReader = new XmlFilterReader(reader);
        char[] buffer = new char[100];

        // When
        int readCount = filterReader.read(buffer, 0, buffer.length);
        String result = new String(buffer, 0, readCount);

        // Then
        assertFalse(result.contains("\u0000"));
        assertFalse(result.contains("\u0001"));
        assertFalse(result.contains("\u0002"));
        // 控制字符应该被替换为空格
        assertTrue(result.matches("ABC   DEF"));
    }

    @Test
    @DisplayName("测试 XmlFilterReader - 混合字符和控制字符")
    public void testXmlFilterReaderMixedCharacters() throws Exception {
        // Given
        StringBuilder sb = new StringBuilder();
        sb.append("Start");
        sb.append('\u0005'); // 控制字符
        sb.append("Middle");
        sb.append('\u0007'); // 控制字符
        sb.append("End");

        StringReader reader = new StringReader(sb.toString());
        XmlFilterReader filterReader = new XmlFilterReader(reader);
        char[] buffer = new char[100];

        // When
        int readCount = filterReader.read(buffer, 0, buffer.length);
        String result = new String(buffer, 0, readCount);

        // Then
        assertEquals("Start Middle End", result);
    }

    @Test
    @DisplayName("测试 XML 往返转换 - 对象 -> XML -> 对象")
    public void testXmlRoundTrip() {
        // Given
        Student original = new Student();
        original.setAge(TEST_AGE);
        original.setName(TEST_NAME);
        original.setRemark(TEST_REMARK);

        // When
        String xml = XmlBeanUtil.object2Xml(original);
        Student restored = XmlBeanUtil.xml2Object(xml, Student.class);

        // Then
        assertNotNull(restored);
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getAge(), restored.getAge());
        assertEquals(original.getRemark(), restored.getRemark());
    }

    @Test
    @DisplayName("测试 XML 往返转换 - 对象 -> Element -> 提取属性")
    public void testObjectToElementToAttributes() {
        // Given
        Student student = new Student();
        student.setAge(20);
        student.setName("张三");
        student.setRemark("优秀学生");

        // When
        Element element = XmlBeanUtil.object2Element(student);

        // Then
        assertNotNull(element);
        assertEquals("student", element.getName());
        assertEquals(3, element.elements().size());
        assertNotNull(element.element("name"));
        assertNotNull(element.element("age"));
        assertNotNull(element.element("remark"));
    }

    @Test
    @DisplayName("测试 XML 格式化输出")
    public void testXmlFormattedOutput() {
        // Given
        Student student = new Student();
        student.setAge(TEST_AGE);
        student.setName(TEST_NAME);
        student.setRemark(TEST_REMARK);

        // When
        String xml = XmlBeanUtil.object2Xml(student);

        // Then
        assertTrue(xml.contains("\n")); // 应该包含换行符（格式化输出）
        assertTrue(xml.contains("  ")); // 应该包含缩进
    }

    @Test
    @DisplayName("测试复杂对象 XML 序列化")
    public void testComplexObjectSerialization() {
        // Given
        School school = new School();
        school.setName("测试学校");

        Student student1 = new Student();
        student1.setName("学生1");
        student1.setAge(10);
        student1.setRemark("备注1");

        Student student2 = new Student();
        student2.setName("学生2");
        student2.setAge(11);
        student2.setRemark("备注2");

        school.getStudents().add(student1);
        school.getStudents().add(student2);

        // When
        String xml = XmlBeanUtil.object2Xml(school);

        // Then
        assertTrue(xml.contains("<school>"));
        assertTrue(xml.contains("<name>测试学校</name>"));
        assertTrue(xml.contains("<students>"));
        assertTrue(xml.contains("<name>学生1</name>"));
    }

    @Test
    @DisplayName("测试空对象 XML 序列化")
    public void testEmptyObjectSerialization() {
        // Given
        Student student = new Student();

        // When
        String xml = XmlBeanUtil.object2Xml(student);

        // Then
        assertNotNull(xml);
        assertTrue(xml.contains("<student>"));
    }

    @Test
    @DisplayName("测试中文内容 XML 处理")
    public void testChineseContentXmlProcessing() {
        // Given
        Student student = new Student();
        student.setName("张三李四王五");
        student.setAge(15);
        student.setRemark("这是一个包含中文🎉和特殊符号<>&\"'的测试内容");

        // When
        String xml = XmlBeanUtil.object2Xml(student);
        Student result = XmlBeanUtil.xml2Object(xml, Student.class);

        // Then
        assertEquals("张三李四王五", result.getName());
        assertTrue(result.getRemark().contains("中文🎉"));
        assertTrue(result.getRemark().contains("<>&\"'"));
    }

    // ========== 测试用实体类 ==========

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

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement
    public static class School {

        @XmlElement
        private String name;

        @XmlElement(name = "students")
        private java.util.List<Student> students = new java.util.ArrayList<>();
    }
}
