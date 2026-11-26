/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.nacos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.ServiceLoader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hbasesoft.framework.common.utils.PropertyHolder;
import com.hbasesoft.framework.common.utils.config.Property;

/**
 * <Description> NacosProperty（Property SPI 的 Nacos 配置中心后端）单元测试。不触网：拉取
 * 动作经覆写 loadConfigContent / resolveServerAddr 注入假实现，覆盖惰性关闭、fail-fast、
 * yml 扁平化与占位符透传四条主线 <br>
 *
 * PropertyHolder 集成路径（真连 Nacos）说明：ServiceLoader 装配链与占位符整链递归已由
 * EnvPropertyTest（framework-common-core）与本地 ServiceLoader 发现测试钉住；启动容器 env
 * 注入 NACOS_CONFIG_DATAID 后经真 Nacos 的端到端验证（数据库在 Bootstrap.before 连通 =
 * 值来自 Nacos 后端）放部署侧冒烟（framework-cloud scripts/deploy/smoke-test.sh 与
 * dockerize 验证），单元测试不依赖外部 Nacos 实例 <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年10月28日 <br>
 * @since V4.5.3<br>
 * @see com.hbasesoft.framework.common.nacos.NacosProperty <br>
 */
@DisplayName("NacosProperty 配置中心后端测试")
class NacosPropertyTest {

    /** 嵌套 yml 样例（三级嵌套 + 标量 + null 值 + 空嵌套 Map） */
    private static final String NESTED_YML = "master:\n"
        + "  db:\n"
        + "    url: jdbc:mysql://mysql:3306/prod\n"
        + "    username: ${MASTER_DB_USERNAME}\n"
        + "    password: null\n"
        + "cache:\n"
        + "  model: REDIS\n";

    /**
     * Description: 构造一个以固定 yml 内容充当 Nacos 拉取结果的 NacosProperty <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param dataId dataId（进入 fail-fast 消息上下文）
     * @param content 固定返回的 yml 内容
     * @return 注入假拉取结果的 NacosProperty <br>
     */
    private static NacosProperty fixedContentProperty(final String dataId, final String content) {
        return new NacosProperty(dataId) {

            /**
             * Description: 返回固定内容，不触网 <br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 固定 yml 内容 <br>
             */
            @Override
            String loadConfigContent() {
                return content;
            }
        };
    }

    @Test
    @DisplayName("未设置 NACOS_CONFIG_DATAID 时应惰性关闭：getProperty 恒 null、getProperties 恒空")
    void shouldBeLazyDisabledWithoutDataIdEnv() {
        NacosProperty property = new NacosProperty((String) null);

        assertThat(property.getProperty("master.db.url")).isNull();
        assertThat(property.getProperties()).isEmpty();
    }

    @Test
    @DisplayName("惰性关闭时不应触发任何拉取动作")
    void shouldNotFetchWhenDisabled() {
        NacosProperty property = new NacosProperty((String) null) {

            /**
             * Description: 关闭态被拉取即测试失败（惰性语义被破坏）<br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 不应有返回 <br>
             */
            @Override
            String loadConfigContent() {
                throw new AssertionError("惰性关闭态不应触发 Nacos 拉取");
            }
        };

        assertThat(property.getProperty("any.key")).isNull();
        assertThat(property.getProperties()).isEmpty();
    }

    @Test
    @DisplayName("启用但拉取失败时应 fail-fast 抛 IllegalStateException")
    void shouldFailFastWhenFetchFails() {
        NacosProperty property = new NacosProperty("cloud-bootstrap-system-prod.yml") {

            /**
             * Description: 模拟 Nacos 不可达 / 超时的拉取失败 <br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 不应有返回 <br>
             */
            @Override
            String loadConfigContent() {
                throw new IllegalStateException("模拟 Nacos 不可达");
            }
        };

        assertThatThrownBy(() -> property.getProperty("master.db.url"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("模拟 Nacos 不可达");
    }

    @Test
    @DisplayName("启用但 NACOS_SERVER_ADDR 缺失时应 fail-fast 且不触网")
    void shouldFailFastWhenServerAddrMissing() {
        NacosProperty property = new NacosProperty("cloud-bootstrap-system-prod.yml") {

            /**
             * Description: 地址缺失；若控制流仍然触网则测试失败 <br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return null（模拟环境变量缺失） <br>
             */
            @Override
            String resolveServerAddr() {
                return null;
            }

            /**
             * Description: 正常实现（先走 resolveServerAddr 校验，不应到达网络调用）<br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 不应有返回 <br>
             */
            @Override
            String loadConfigContent() {
                return super.loadConfigContent();
            }
        };

        assertThatThrownBy(() -> property.getProperty("master.db.url"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("NACOS_SERVER_ADDR")
            .hasMessageContaining("cloud-bootstrap-system-prod.yml");
    }

    @Test
    @DisplayName("嵌套 yml 应扁平化为点分键（null 值跳过），getProperty 命中取值")
    void shouldFlattenNestedYmlToDottedKeys() {
        Map<String, String> flatMap = NacosPropertyTest.flattenForTest(NESTED_YML);

        assertThat(flatMap).containsEntry("master.db.url", "jdbc:mysql://mysql:3306/prod")
            .containsEntry("cache.model", "REDIS")
            .doesNotContainKey("master.db.password");
    }

    @Test
    @DisplayName("拉取的 yml 应经扁平化后按点分键命中")
    void shouldResolveDottedKeyFromFetchedYml() {
        NacosProperty property = fixedContentProperty("cloud-bootstrap-tenant-prod.yml", NESTED_YML);

        assertThat(property.getProperty("master.db.url")).isEqualTo("jdbc:mysql://mysql:3306/prod");
        assertThat(property.getProperty("master.db.not-exist")).isNull();
        assertThat(property.getProperties()).containsEntry("cache.model", "REDIS");
    }

    @Test
    @DisplayName("含占位符的值应原样透出，占位符递归交由 PropertyHolder 整链解析")
    void shouldReturnValueWithPlaceholderAsIs() {
        NacosProperty property = fixedContentProperty("cloud-bootstrap-system-prod.yml", NESTED_YML);

        assertThat(property.getProperty("master.db.username")).isEqualTo("${MASTER_DB_USERNAME}");

        // PropertyHolder 整链递归：Nacos 值中的 ${PATH} 经链上 EnvProperty 解析到真实环境变量
        //（样例键换用 PATH——测试机必然存在的环境变量，避免 CONFIG_NOT_SET fail-fast 干扰断言）
        Property withEnvPlaceholder = fixedContentProperty("d.yml", "master.db.username: ${PATH}\n");
        assertThat(PropertyHolder.getProperty(withEnvPlaceholder, "master.db.username"))
            .isEqualTo(System.getenv("PATH"));
    }

    @Test
    @DisplayName("yml 解析失败时应 fail-fast 且消息含 dataId")
    void shouldFailFastWhenYmlBroken() {
        NacosProperty property = fixedContentProperty("cloud-bootstrap-gateway-prod.yml", "{{{ not-yaml");

        assertThatThrownBy(() -> property.getProperty("spring.cloud.gateway.routes"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("cloud-bootstrap-gateway-prod.yml")
            .hasMessageContaining("解析失败");
    }

    @Test
    @DisplayName("同线程拉取进行中再入 getProperty 应短路返回 null（防 logback 初始化期重入 NPE）")
    void shouldShortCircuitReentrantCallDuringLoad() {
        NacosProperty property = new NacosProperty("cloud-bootstrap-system-prod.yml") {

            /**
             * Description: 拉取期间同线程再入 getProperty，模拟 logback 配置期回读配置链 <br>
             *
             * @author 王伟<br>
             * @taskId <br>
             * @return 固定 yml 内容 <br>
             */
            @Override
            String loadConfigContent() {
                // 再入（同一线程）：应短路返回 null 而非递归拉取
                assertThat(getProperty("master.db.url")).isNull();
                assertThat(getProperties()).isEmpty();
                return NESTED_YML;
            }
        };

        assertThat(property.getProperty("master.db.url")).isEqualTo("jdbc:mysql://mysql:3306/prod");
        // 拉取完成后不再处于 LOADING 态，正常命中
        assertThat(property.getProperty("cache.model")).isEqualTo("REDIS");
    }

    @Test
    @DisplayName("NacosProperty 应经 ServiceLoader 注册到 Property SPI 链")
    void shouldBeDiscoveredViaServiceLoader() {
        boolean found = ServiceLoader.load(Property.class).stream()
            .anyMatch(provider -> provider.type() == NacosProperty.class);

        assertThat(found).as("META-INF/services 注册文件应能被 ServiceLoader 发现").isTrue();
    }

    /**
     * Description: 暴露静态扁平化逻辑给测试（避免为测静态方法而放大可见面）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param yamlContent yml 内容
     * @return 扁平化 Map <br>
     */
    private static Map<String, String> flattenForTest(final String yamlContent) {
        return fixedContentProperty("test.yml", yamlContent).getProperties();
    }
}
