package com.hbasesoft.framework.common.utils.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.UtilException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XmlBeanUtil {

    /**
     * @param xmlStr 字符串
     * @param c 对象Class类型
     * @param <T> T
     * @return 对象实例
     * @throws UtilException
     */
    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(final String xmlStr, final Class<T> c) throws UtilException {
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
    public static String object2Xml(final Object object) throws UtilException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // 编码格式,默认为utf-8
            try (StringWriter writer = new StringWriter();) {
                marshaller.marshal(object, writer);
                String xml = writer.toString();
                xml = StringUtils.replace(xml, "&lt;", "<");
                xml = StringUtils.replace(xml, "&gt;", ">");
                xml = StringUtils.replace(xml, "&amp;", "&");
                xml = StringUtils.replace(xml, "&#13;", "\n");
                xml = StringUtils.replace(xml, "&#xd;", GlobalConstants.BLANK);
                return xml;
            }
        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }

    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param object
     * @return Element
     * @throws UtilException <br>
     */
    public static Element object2Element(final Object object) throws UtilException {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // 编码格式,默认为utf-8
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true); // 是否省略xml头信息

            Writer writer = new StringWriter();
            marshaller.marshal(object, writer);

            Document document = DocumentHelper.parseText(writer.toString()); // 创建根节点
            return document.getRootElement();

        }
        catch (Exception e) {
            throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
        }

    }
}
