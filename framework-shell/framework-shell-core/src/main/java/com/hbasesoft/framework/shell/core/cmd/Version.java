/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.shell.core.cmd;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.beust.jcommander.JCommander;
import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.shell.core.Assert;
import com.hbasesoft.framework.shell.core.CommandHandler;
import com.hbasesoft.framework.shell.core.Shell;
import com.hbasesoft.framework.shell.core.cmd.Version.Option;
import com.hbasesoft.framework.shell.core.vo.AbstractOption;

import lombok.Getter;
import lombok.Setter;

import java.io.PrintStream;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2019年7月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.vcc.tools.command <br>
 */
@Component
public class Version implements CommandHandler<Option> {

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param option <br>
     */
    @Override
    public void execute(final JCommander cmd, final Option option, final Shell shell) {
        String nexus = PropertyHolder.getProperty("project.server.nexus");
        Assert.notEmpty(nexus, "升级服务器地址未配置，请检查project.server.nexus配置项");

        String version = PropertyHolder.getProperty("project.version");
        Assert.notEmpty(version, "请增加 project.version=@project.version@ 配置");

        String artifactId = PropertyHolder.getProperty("project.artifactId");
        Assert.notEmpty(artifactId, "请增加 project.artifactId=@project.artifactId@ 配置");

        String groupId = PropertyHolder.getProperty("project.groupId");
        Assert.notEmpty(groupId, "请增加 project.groupId=@project.groupId@ 配置");

        String gids = StringUtils.replace(groupId, ".", "/");

        String basePath = new StringBuilder().append(nexus).append("/repository/maven-releases/").append(gids)
            .append('/').append(artifactId).append('/').toString();

        String lastVersion = getLastVersion(basePath + "maven-metadata.xml", shell);
        Assert.notEmpty(lastVersion, "未找到升级配置文件");

        String filePath = new StringBuilder().append(basePath).append(lastVersion).append('/').append(artifactId)
            .append('-').append(lastVersion).append(".jar").toString();

        PrintStream shellOut = shell.getOut();
        if (version.compareTo(lastVersion) >= 0) {
            shellOut.println("当前已经是最新版本!");
            shellOut.println("下载地址: " + filePath);
        }
        else {
            shellOut.println("最新版本为：" + lastVersion);
            shellOut.println("请使用下面的命令进行下载升级: curl -O " + filePath);
        }
    }

    private String getLastVersion(final String url, final Shell shell) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(url);

            // 创建XPath对象
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            NodeList nodeList = (NodeList) xpath.evaluate("/metadata/versioning/versions/version", doc,
                XPathConstants.NODESET);

            String v = GlobalConstants.BLANK;
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (i == 0) {
                    v = nodeList.item(i).getTextContent();
                }
                else {
                    String c = nodeList.item(i).getTextContent();
                    if (c.compareTo(v) > 0) {
                        v = c;
                    }
                }
            }
            return v;

        }
        catch (Exception e) {
            shell.getOut().println("获取最新版本失败" + e.getMessage());
        }
        return null;
    }

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public String toString() {
        return "查看最新版本";
    }

    @Getter
    @Setter
    public static class Option extends AbstractOption {
    }
}
