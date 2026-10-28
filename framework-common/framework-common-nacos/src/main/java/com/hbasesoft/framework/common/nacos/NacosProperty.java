/****************************************************************************************
 Copyright © 2003-2012 hbasesoft Corporation. All rights reserved. Reproduction or       <br>
 transmission in whole or in part, in any form or by any means, electronic, mechanical <br>
 or otherwise, is prohibited without the prior written consent of the copyright owner. <br>
 ****************************************************************************************/

package com.hbasesoft.framework.common.nacos;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.hbasesoft.framework.common.utils.config.Property;

/**
 * <Description> Nacos 配置中心配置项，作为 Property SPI 的 Nacos 后端，经 ServiceLoader 注册到
 * PropertyHolder 配置链，使框架预启动链（Bootstrap.before，Spring 容器尚未就绪阶段）可以直接
 * 消费外置到 Nacos 配置中心的键（数据库连接、管理员种子、缓存/文件存储 SDK 初始化等），补齐
 * LocalProperty 只认 classpath yml + EnvProperty 只认环境变量的缺口 <br>
 *
 * 启用语义（惰性触发）：环境变量 NACOS_CONFIG_DATAID 存在时启用，首次 getProperty 才同步拉取
 * （构造期零网络零阻塞）；环境变量不存在时恒返回 null / 空 Map，dev / demo / it 档零影响 <br>
 *
 * fail-fast 语义：启用（NACOS_CONFIG_DATAID 有值）但 NACOS_SERVER_ADDR 缺失、Nacos 不可达、
 * 拉取超时、dataId 不存在或 yml 解析失败时抛 IllegalStateException——prod 语义：Nacos 不可达拒启 <br>
 *
 * 占位符语义：命中键的值（含 ${X} 占位符）原样返回，占位符递归解析由 PropertyHolder 负责
 * （经整链查到 EnvProperty 的环境变量值等），本类不做占位符展开 <br>
 *
 * 键空间：本类只提供 Nacos dataId 中的点分配置键（master.db.* 等），与环境变量键
 * （EnvProperty，全大写）空间不相交，与 LocalProperty 兜底键互斥同名，故 PropertyHolder
 * SPI 链中与 EnvProperty 的装配顺序无关紧要 <br>
 *
 * 重入语义：首次拉取会触发 nacos-client 类初始化，其日志组件绑定可能级联触发 slf4j/logback
 * 初始化（框架预启动链早于日志系统就绪），logback 配置期回读配置链（如 OTLP appender 取
 * project.name）会重入本类——同线程拉取进行中的重入调用短路返回 null，由链上 LocalProperty
 * 兜底本地键，杜绝类初始化中途重入拉取导致的 NPE <br>
 *
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2026年10月28日 <br>
 * @since V4.5.3<br>
 * @see com.hbasesoft.framework.common.utils.config.Property <br>
 */
public class NacosProperty implements Property {

    /** 启用开关环境变量：值为本服务的外置配置 dataId，未设置时本后端整体惰性关闭 */
    private static final String DATAID_ENV_KEY = "NACOS_CONFIG_DATAID";

    /** Nacos 服务器地址环境变量：启用时缺失即 fail-fast 拒启 */
    private static final String SERVER_ADDR_ENV_KEY = "NACOS_SERVER_ADDR";

    /** 配置分组环境变量：未设置时回落 DEFAULT_GROUP */
    private static final String GROUP_ENV_KEY = "NACOS_CONFIG_GROUP";

    /** 鉴权用户名环境变量（可选，Nacos 服务端开启鉴权时注入） */
    private static final String USERNAME_ENV_KEY = "NACOS_USERNAME";

    /** 鉴权密码环境变量（可选，Nacos 服务端开启鉴权时注入） */
    private static final String PASSWORD_ENV_KEY = "NACOS_PASSWORD";

    /** 配置分组缺省值 */
    private static final String DEFAULT_GROUP = "DEFAULT_GROUP";

    /** 拉取超时（毫秒）：超时即 fail-fast，拒绝以缺配置状态继续启动 */
    private static final long FETCH_TIMEOUT_MILLS = 5000L;

    /** 空串判据 */
    private static final String EMPTY = "";

    /** 同线程拉取中标记：logback 初始化期重入getProperty 时短路返回 null，由链上 LocalProperty 兜底 */
    private static final ThreadLocal<Boolean> LOADING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** 外置配置 dataId（来自启用开关环境变量；null 表示本后端关闭） */
    private final String dataId;

    /** 扁平化后的配置缓存（点分键 → 值；首次 getProperty 惰性装载，由本实例监视器保护） */
    private Map<String, String> cachedProperties;

    /**
     * Description: 构造 Nacos 配置后端，启用开关取环境变量 NACOS_CONFIG_DATAID（构造期
     * 不做任何网络动作）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     */
    public NacosProperty() {
        this(System.getenv(DATAID_ENV_KEY));
    }

    /**
     * Description: 以指定 dataId 构造（包级可见，供单元测试注入，绕过环境变量）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param dataId 外置配置 dataId，null 表示本后端关闭 <br>
     */
    NacosProperty(final String dataId) {
        this.dataId = dataId;
    }

    /**
     * Description: 获取配置项：未启用（无 NACOS_CONFIG_DATAID）恒返回 null；已启用时首次
     * 调用同步拉取并缓存，命中返回原值（${X} 占位符不展开，由 PropertyHolder 整链递归），
     * 未命中返回 null（链继续向后查找）。同线程拉取进行中再入（如 nacos-client 触发
     * slf4j/logback 初始化、logback 回读配置链）时短路返回 null 交由链上 LocalProperty
     * 兜底——nacos-client 自身类初始化依赖 slf4j 绑定，重入拉取会在类初始化中途触发
     * NPE，短路是确定性的防重入语义<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param property 配置键（点分形态）
     * @return 配置值，未启用、拉取中再入或未命中返回 null <br>
     */
    @Override
    public String getProperty(final String property) {
        if (isEmpty(dataId) || LOADING.get()) {
            return null;
        }
        return ensureLoaded().get(property);
    }

    /**
     * Description: 获取全部配置项：未启用或拉取中再入恒返回空 Map；已启用返回扁平化 Map 的副本 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 点分键 → 值的扁平 Map <br>
     */
    @Override
    public Map<String, String> getProperties() {
        if (isEmpty(dataId) || LOADING.get()) {
            return Collections.emptyMap();
        }
        return new HashMap<>(ensureLoaded());
    }

    /**
     * Description: 惰性装载（同步互斥 + 同线程再入标记）：只装载一次，失败不缓存
     * （下次调用重试并重现 fail-fast）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 扁平化配置 Map <br>
     */
    private Map<String, String> ensureLoaded() {
        synchronized (this) {
            if (cachedProperties == null) {
                LOADING.set(Boolean.TRUE);
                try {
                    cachedProperties = loadAndFlatten();
                }
                finally {
                    LOADING.set(Boolean.FALSE);
                }
            }
            return cachedProperties;
        }
    }

    /**
     * Description: 拉取并扁平化：拉取阶段异常透传 IllegalStateException（消息已含上下文），
     * 解析阶段异常统一包装为 IllegalStateException（fail-fast 拒启）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 扁平化配置 Map <br>
     */
    private Map<String, String> loadAndFlatten() {
        String content = loadConfigContent();
        try {
            return flatten(content);
        }
        catch (RuntimeException e) {
            throw new IllegalStateException("Nacos 配置 dataId[" + dataId + "] yml 解析失败：" + e.getMessage(), e);
        }
    }

    /**
     * Description: 从 Nacos 同步拉取 dataId 原始内容（yml 字符串）。包级可见供测试覆写注入
     * 假内容；生产实现：地址/分组/凭证取自环境变量，dataId 不存在、连接失败、超时均
     * IllegalStateException fail-fast<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return dataId 原始 yml 内容 <br>
     */
    String loadConfigContent() {
        String serverAddr = resolveServerAddr();
        if (isEmpty(serverAddr)) {
            throw new IllegalStateException("Nacos 配置后端已启用（NACOS_CONFIG_DATAID=" + dataId
                + "）但环境变量 NACOS_SERVER_ADDR 缺失，无法定位配置中心，拒绝启动");
        }
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        String username = System.getenv(USERNAME_ENV_KEY);
        String password = System.getenv(PASSWORD_ENV_KEY);
        if (!isEmpty(username)) {
            properties.setProperty(PropertyKeyConst.USERNAME, username);
        }
        if (!isEmpty(password)) {
            properties.setProperty(PropertyKeyConst.PASSWORD, password);
        }
        String group = resolveGroup();
        try {
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, FETCH_TIMEOUT_MILLS);
            if (content == null) {
                throw new IllegalStateException("Nacos 配置中心[" + serverAddr + "]不存在 dataId[" + dataId
                    + "]（group=" + group + "），请确认配置已发布（push-nacos-config.sh / seeder），拒绝启动");
            }
            return content;
        }
        catch (NacosException e) {
            throw new IllegalStateException("Nacos 配置中心[" + serverAddr + "]拉取 dataId[" + dataId
                + "]（group=" + group + "）失败：" + e.getMessage() + "，拒绝启动", e);
        }
    }

    /**
     * Description: 解析 Nacos 服务器地址（包级可见供测试覆写，模拟缺失场景）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 环境变量 NACOS_SERVER_ADDR 的值 <br>
     */
    String resolveServerAddr() {
        return System.getenv(SERVER_ADDR_ENV_KEY);
    }

    /**
     * Description: 解析配置分组：环境变量 NACOS_CONFIG_GROUP 未设置时回落 DEFAULT_GROUP <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @return 配置分组 <br>
     */
    private String resolveGroup() {
        String group = System.getenv(GROUP_ENV_KEY);
        return isEmpty(group) ? DEFAULT_GROUP : group;
    }

    /**
     * Description: 将 yml 内容解析并扁平化为点分键 Map（对齐 LocalProperty 的 application.yml
     * 装载语义：嵌套 Map 递归拼接点分键、空 Map 与 null 值跳过、标量 toString）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param yamlContent yml 字符串
     * @return 点分键 → 值的扁平 Map（内容为空时返回空 Map） <br>
     */
    private static Map<String, String> flatten(final String yamlContent) {
        Map<String, String> flatMap = new HashMap<>();
        if (isEmpty(yamlContent)) {
            return flatMap;
        }
        Object root = new Yaml().load(yamlContent);
        if (root == null) {
            return flatMap;
        }
        if (!(root instanceof Map)) {
            throw new IllegalStateException("yml 根节点不是键值映射（实际类型 " + root.getClass().getName() + "）");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> rootMap = (Map<String, Object>) root;
        for (Entry<String, Object> entry : rootMap.entrySet()) {
            transfer(entry.getKey(), entry.getValue(), flatMap);
        }
        return flatMap;
    }

    /**
     * Description: 递归扁平化：嵌套 Map 递归下钻拼接点分键，标量值 toString 落表 <br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param key 当前层键（可已带父级前缀）
     * @param value 当前层值
     * @param flatMap 扁平化结果落表 <br>
     */
    @SuppressWarnings("unchecked")
    private static void transfer(final String key, final Object value, final Map<String, String> flatMap) {
        if (value == null) {
            return;
        }
        if (value instanceof Map) {
            Map<String, Object> children = (Map<String, Object>) value;
            for (Entry<String, Object> entry : children.entrySet()) {
                transfer(key + "." + entry.getKey(), entry.getValue(), flatMap);
            }
            return;
        }
        flatMap.put(key, value.toString());
    }

    /**
     * Description: 空串判据（不引入 commons-lang3，保持模块最小依赖）<br>
     *
     * @author 王伟<br>
     * @taskId <br>
     * @param value 待判字符串
     * @return null 或空串返回 true <br>
     */
    private static boolean isEmpty(final String value) {
        return value == null || EMPTY.equals(value);
    }
}
