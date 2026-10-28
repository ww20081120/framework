/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.utils.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.UtilException;

/**
 * <Description> EnvProperty 环境变量配置后端及 PropertyHolder 占位符整链解析测试<br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年10月28日 <br>
 * @since V4.5.2<br>
 * @see com.hbasesoft.framework.common.utils.config.EnvProperty <br>
 */
@DisplayName("EnvProperty 环境变量配置后端测试")
class EnvPropertyTest {

    /** 测试用不存在环境变量的键名，确保解析失败走 fail-fast 分支 */
    private static final String NOT_SET_KEY = "__FC_TEST_NOT_SET_VAR_XYZ__";

    /** 测试用自引用占位符键名 */
    private static final String SELF_REF_KEY = "self.ref";

    /**
     * Description: 构造一个只返回指定键值映射的匿名 Property，模拟自定义配置源<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param mapping 固定返回的键值对（key 相同时返回 value，否则返回 null）
     * @return 只含固定映射的 Property <br>
     */
    private static Property fixedProperty(final Map<String, String> mapping) {
        return new Property() {

            /**
             * Description: 返回固定映射中的值<br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @param property 配置键
             * @return 固定映射中的值，无则 null <br>
             */
            @Override
            public String getProperty(final String property) {
                return mapping.get(property);
            }

            /**
             * Description: 返回固定映射<br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 固定映射 <br>
             */
            @Override
            public Map<String, String> getProperties() {
                return mapping;
            }
        };
    }

    @Test
    @DisplayName("占位符应通过整条配置链解析到环境变量")
    void shouldResolveEnvPlaceholderViaWholeChainLookup() {
        Property custom = fixedProperty(Map.of("env.path", "${PATH}"));

        String value = PropertyHolder.getProperty(custom, "env.path");

        assertThat(value).isEqualTo(System.getenv("PATH"));
    }

    @Test
    @DisplayName("占位符解析不到时应快速失败并抛出 CONFIG_NOT_SET")
    void shouldFailFastWhenPlaceholderUnresolvable() {
        Property custom = fixedProperty(Map.of("broken.key", "${" + NOT_SET_KEY + "}"));

        assertThatThrownBy(() -> PropertyHolder.getProperty(custom, "broken.key"))
            .isInstanceOf(UtilException.class)
            .hasMessageContaining(NOT_SET_KEY);
    }

    @Test
    @DisplayName("EnvProperty 应能读取真实环境变量")
    void envPropertyReadsRealEnvironment() {
        EnvProperty envProperty = new EnvProperty();

        assertThat(envProperty.getProperty("PATH")).isNotBlank();
        assertThat(envProperty.getProperties()).containsKey("PATH");
    }

    @Test
    @DisplayName("自引用占位符不应死循环，应原样返回")
    void selfReferencePlaceholderDoesNotLoop() {
        Property custom = fixedProperty(Map.of(SELF_REF_KEY, "${" + SELF_REF_KEY + "}"));

        String value = PropertyHolder.getProperty(custom, SELF_REF_KEY);

        assertThat(value).isEqualTo("${" + SELF_REF_KEY + "}");
    }
}
