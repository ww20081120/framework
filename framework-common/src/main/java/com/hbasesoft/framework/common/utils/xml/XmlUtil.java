package com.hbasesoft.framework.common.utils.xml;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;

import com.hbasesoft.framework.common.ErrorCodeDef;
import com.hbasesoft.framework.common.utils.CommonUtil;
import com.hbasesoft.framework.common.utils.UtilException;
import com.hbasesoft.framework.common.utils.logger.Logger;

/**
 * xml工具类(dom4j)
 * 
 * @author meiguiyang
 */
public final class XmlUtil {
    /**
     * debug日志
     */
    private static Logger logger = new Logger(XmlUtil.class);

    /**
     * <默认构造函数>
     */
    private XmlUtil() {
    }

    /**
     * 根据输入流得到doc
     * 
     * @param stream 输入流
     * @return doc
     * @throws DocumentException DocumentException
     */
    public static Document getDocument(InputStream stream) throws UtilException {
        Document doc = null;
        if (stream == null) {
            logger.error("stream is null");
        }
        else {
            try {
                SAXReader reader = new SAXReader();
                doc = reader.read(stream);
            }
            catch (DocumentException e) {
                logger.error("Can't parse the stream as XML.", e);
                throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
            }
        }
        return doc;
    }

    /**
     * 根据URL得到doc
     * 
     * @param url url
     * @return doc doc
     * @throws DocumentException DocumentException
     */
    public static Document getDocument(URL url) throws UtilException {
        Document doc = null;
        if (url == null) {
            logger.error("url is null");
        }
        else {
            try {
                SAXReader reader = new SAXReader();
                doc = reader.read(url);
            }
            catch (DocumentException e) {
                logger.error("Can't parse the stream as XML.", e);
                throw new UtilException(ErrorCodeDef.XML_TRANS_ERROR, e);
            }
        }
        return doc;
    }

    /**
     * 根据文件得到doc
     * 
     * @param xmlFile xml文件
     * @return doc
     */
    public static Document parseXMLFile(File xmlFile) {
        Document doc = null;
        if (xmlFile == null || !xmlFile.exists() || !xmlFile.isFile()) {
            logger.error("xmlFile is null or error");
        }
        else {
            FileInputStream in = null;
            try {
                in = new FileInputStream(xmlFile);
                doc = getDocument(in);
            }
            catch (FileNotFoundException e) {
                logger.error("File does not exists.");
            }
            catch (UtilException e) {
                logger.error("Can't parse the file's content as XML. File is[" + xmlFile.getPath() + "]", e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }
        }
        return doc;
    }

    /**
     * 根据xml字符串得到doc
     * 
     * @param xml xml字符串
     * @return doc
     */
    public static Document parseXML(String xml) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        }
        catch (DocumentException e) {
            logger.error("Can't parse the content as XML. Content is[" + xml + "]", e);
        }
        return doc;
    }

    /**
     * 将doc写入文件
     * 
     * @param path 文件路径
     * @param doc doc
     * @param encoding 字符集
     */
    public static void writeToFile(String path, Document doc, String encoding) {
        if (path == null) {
            return;
        }
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding(encoding);
        XMLWriter writer = null;
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path), encoding);
            writer = new XMLWriter(out, format);
            writer.write(doc);
        }
        catch (IOException e) {
            logger.error("IO error when writing to file", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    logger.error("IO error when closing writer", e);
                }
            }
        }
    }

    /**
     * 根据xpath得到元素列表
     * 
     * @param node 节点
     * @param xpath xpath
     * @return 子节点列表
     */
    @SuppressWarnings("unchecked")
    public static List<Node> selectNodes(Node node, String xpath, Namespace... namespaces) {
        if (node == null || isBlank(xpath)) {
            return null;
        }
        if (CommonUtil.isNotEmpty(namespaces)) {
            XPath path = buildXPath(node, xpath, namespaces);
            return path.selectNodes(node);
        }
        return node.selectNodes(xpath);
    }

    /**
     * 根据xpath得到节点
     * 
     * @param node 节点
     * @param xpath xpath
     * @return 子节点
     */
    public static Node selectSingleNode(Node node, String xpath, Namespace... namespaces) {
        if (node == null || isBlank(xpath)) {
            return null;
        }
        if (CommonUtil.isNotEmpty(namespaces)) {
            XPath path = buildXPath(node, xpath, namespaces);
            return path.selectSingleNode(node);
        }
        return node.selectSingleNode(xpath);
    }

    /**
     * 根据xpath得到节点值
     * 
     * @param node 节点
     * @param xpath xpath
     * @return 子节点值
     */
    public static String selectSingleNodeValue(Node node, String xpath, Namespace... namespaces) {
        String value = null;
        Node subNode = selectSingleNode(node, xpath, namespaces);
        if (subNode != null) {
            value = subNode.getText();
        }
        return isBlank(value) ? "" : value;
    }

    /**
     * 得到节点的属性值
     * 
     * @param node 节点
     * @param attrName 属性名
     * @return 属性值
     */
    public static String getAttributeValue(Node node, String attrName, Namespace... namespaces) {
        String value = null;
        if (node != null && !isBlank(attrName)) {
            Node subNode = selectSingleNode(node, "@" + attrName, namespaces);
            if (subNode != null) {
                value = subNode.getText();
            }
        }
        return isBlank(value) ? "" : value;
    }

    /**
     * 得到节点的子节点值
     * 
     * @param node 节点
     * @param nodeName 子节点
     * @return 子节点值
     */
    public static String getNodeValue(Node node, String nodeName, Namespace... namespaces) {
        String value = null;
        if (node != null && !isBlank(nodeName)) {
            Node subNode = selectSingleNode(node, nodeName, namespaces);
            if (subNode != null) {
                value = subNode.getText();
            }
        }
        return isBlank(value) ? "" : value;
    }

    /**
     * 根据schema验证xml
     * 
     * @param xsdUrl schema文件url
     * @param node xml节点
     * @return 验证结果
     * @throws IOException
     * @throws SAXException
     */
    public static boolean validateXsd(URL xsdUrl, Node node) throws SAXException, IOException {
        if (xsdUrl == null || node == null) {
            logger.error("xsdUrl or node is null");
            return false;
        }
        return validateXsd(xsdUrl, new DocumentSource(node));
    }

    /**
     * 根据schema验证xml
     * 
     * @param xsdUrl schema文件url
     * @param source xml输入流
     * @return 验证结果
     * @throws SAXException
     * @throws IOException
     */
    public static boolean validateXsd(URL xsdUrl, Source source) throws IOException, SAXException {
        boolean flag = false;
        if (xsdUrl == null || source == null) {
            logger.error("xsdUrl or source is null");
        }
        else {
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = factory.newSchema(xsdUrl);
            Validator validator = schema.newValidator();
            validator.validate(source);
            flag = true;
        }
        return flag;
    }

    public static XPath buildXPath(Node node, String xpath, Namespace... namespaces) {
        XPath path = node.createXPath(xpath);
        if (CommonUtil.isNotEmpty(namespaces)) {
            Map<String, String> nsMap = new HashMap<String, String>();
            for (Namespace ns : namespaces) {
                nsMap.put(ns.getPrefix(), ns.getUri());
            }
            path.setNamespaceURIs(nsMap);
        }
        return path;
    }
}
