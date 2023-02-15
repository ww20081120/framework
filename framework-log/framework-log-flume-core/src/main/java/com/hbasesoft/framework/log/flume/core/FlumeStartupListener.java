/**************************************************************************************** 
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/
package com.hbasesoft.framework.log.flume.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.hbasesoft.framework.common.GlobalConstants;
import com.hbasesoft.framework.common.InitializationException;
import com.hbasesoft.framework.common.StartupListener;
import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.logger.LoggerUtil;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate Aug 17, 2022 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.log.flume.core <br>
 */
public class FlumeStartupListener implements StartupListener {

    /** 配置文件目录 */
    private static final String CONFIG_DIR = GlobalConstants.FILE_STORAGE_PATH + "/flume/conf";

    /** 配置文件名称 */
    private static final String FILE_NAME = PropertyHolder.getProjectName() + ".conf";

    /**
     * Description: <br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param context <br>
     */
    @Override
    public void complete(final ApplicationContext context) {
        String filePath = PropertyHolder.getProperty("logservice.flume.config", "flume-nc-log.conf");
        String name = PropertyHolder.getProperty("logservice.flume.name");
        if (StringUtils.isEmpty(name)) {
            return;
        }

        File dir = new File(CONFIG_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        File configFile = new File(dir, FILE_NAME);
        // 获取到所有的配置写入到配置文件中
        Map<String, String> configMap = PropertyHolder.getProperties();
        if (MapUtils.isNotEmpty(configMap)) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath);
                OutputStream out = new FileOutputStream(configFile);) {
                if (in == null) {
                    throw new FileNotFoundException(filePath + "文件找不到！");
                }
                IOUtils.copy(in, out);
                out.flush();
            }
            catch (IOException e) {
                LoggerUtil.error(e);
                throw new InitializationException(e);
            }
            LoggerUtil.info("日志采集任务开始启动。。。");
            org.apache.flume.node.Application.main(new String[] {
                "agent", "--name", name, "--conf-file", configFile.getAbsolutePath()
            });
            LoggerUtil.info("日志采集任务启动成功！");

        }
    }
}
