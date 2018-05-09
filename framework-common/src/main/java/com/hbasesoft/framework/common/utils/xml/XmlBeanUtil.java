package com.hbasesoft.framework.common.utils.xml;

import java.io.CharArrayWriter;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * <Description> XML 工具<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2016年8月18日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common.utils.xml <br>
 */
public class XmlBeanUtil {

    protected static Logger logger = new Logger(XmlBeanUtil.class);

    private static final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

    /**
     * @param xmlStr 字符串
     * @param c 对象Class类型
     * @return 对象实例
     * @throws UtilException
     */
    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(String xmlStr, Class<T> c) throws UtilException {
        if (StringUtils.isEmpty(xmlStr)) {
            return null;
        }
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
            return t;
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }
    }

    /**
     * @param object 对象
     * @return 返回xmlStr
     * @throws UtilException
     */
    public static String object2Xml(Object object) throws UtilException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式,默认为utf-8
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);// 是否省略xml头信息

            Writer writer = new CharArrayWriter();
            XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(writer);
            xmlStreamWriter.writeStartDocument((String) marshaller.getProperty(Marshaller.JAXB_ENCODING), "1.0");

            marshaller.marshal(object, xmlStreamWriter);
            xmlStreamWriter.writeEndDocument();
            xmlStreamWriter.close();
            return writer.toString();
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }

    }

    public static Element object2Element(Object object) throws UtilException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式,默认为utf-8
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);// 是否省略xml头信息

            Writer writer = new CharArrayWriter();
            marshaller.marshal(object, writer);

            Document document = DocumentHelper.parseText(writer.toString()); // 创建根节点
            return document.getRootElement();

        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }

    }
}
