/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.utils.config;

import java.util.Map;

/**
 * <Description> 环境变量配置项，作为 Property SPI 的环境变量后端，经 ServiceLoader 注册到
 * PropertyHolder 配置链头，使框架预启动链（Spring 容器尚未就绪阶段）可以通过 ${ENV_KEY}
 * 占位符读取进程环境变量，避免密钥等敏感信息以字面量内嵌到配置文件中<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年10月28日 <br>
 * @since V4.5.2<br>
 * @see com.hbasesoft.framework.common.utils.config.Property <br>
 */
public class EnvProperty implements Property {

    /**
     * Description: 获取环境变量配置项，环境变量不存在时返回 null，由配置链继续向后查找<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param property property
     * @return <br>
     */
    @Override
    public String getProperty(final String property) {
        return System.getenv(property);
    }

    /**
     * Description: 获取全部环境变量配置项<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return <br>
     */
    @Override
    public Map<String, String> getProperties() {
        return System.getenv();
    }
}
