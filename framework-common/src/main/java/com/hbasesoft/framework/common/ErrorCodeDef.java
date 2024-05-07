/**
 * 
 */
package com.hbasesoft.framework.common;

import static com.hbasesoft.framework.common.GlobalConstants.SYSTEM_PREFIX;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <Description> 框架的错误码定义，0～999 为框架保留区域的错误码<br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCodeDef implements ErrorCode {

    /** 成功 */
    SUCCESS(0, "成功"),

    /** 系统错误 */
    SYSTEM_ERROR(SYSTEM_PREFIX + 1, "系统错误"),

    /** 缓存失败 */
    CACHE_ERROR(SYSTEM_PREFIX + 2, "缓存失败"),

    /** class:{0} 不包含局部变量表信息，请使用编译器选项 javac -g:'{'vars'}'来编译源文件。 */
    CAN_NOT_FIND_VER_NAME(SYSTEM_PREFIX + 3, "class:{0} 不包含局部变量表信息，请使用编译器选项 javac -g:'{'vars'}'来编译源文件。"),

    /** 解析模板失败 */
    PARSE_TEPLATE_ERROR(SYSTEM_PREFIX + 4, "解析模板失败"),

    /** 初始化sql失败，未找到{0}_{1}的sql */
    INIT_SQL_ERROR(SYSTEM_PREFIX + 5, "初始化sql失败，未找到{0}_{1}的sql"),

    /** 读取sql文件失败,路径[{0}] */
    CAN_NOT_FIND_SQL_FILE(SYSTEM_PREFIX + 6, "读取sql文件失败,路径[{0}]"),

    /** Clazz[{0}] Method[{1}]含有多个ResultCallback参数 */
    ERROR_RESULT_CALL_BACK(SYSTEM_PREFIX + 7, "Clazz[{0}] Method[{1}]含有多个ResultCallback参数"),

    /** Clazz[{0}] Method[{1}]中Pagesize 和 PageIndex 必须同时设置 */
    PAGE_SIZE_PAGE_INDEX_BOTH(SYSTEM_PREFIX + 8, "Clazz[{0}] Method[{1}]中Pagesize 和 PageIndex 必须同时设置"),

    /** OGNL 处理错误 */
    OGNL_ERROR(SYSTEM_PREFIX + 9, "OGNL 处理错误"),

    /** 查询失败 */
    QUERY_ERROR(SYSTEM_PREFIX + 10, "查询失败"),

    /** 执行sql失败 */
    EXECUTE_ERROR(SYSTEM_PREFIX + 11, "执行sql失败"),

    /** 批量失败 */
    BATCH_EXECUTE_ERROR(SYSTEM_PREFIX + 12, "批量失败"),

    /** 保存失败 */
    SAVE_ERROR(SYSTEM_PREFIX + 13, "保存失败"),

    /** 序列化失败{0} */
    SERIALIZE_ERROR(SYSTEM_PREFIX + 14, "序列化失败{0}"),

    /** 反序列化失败{0} */
    UNSERIALIZE_ERROR(SYSTEM_PREFIX + 15, "反序列化失败{0}"),

    /** XML 转换失败 */
    XML_TRANS_ERROR(SYSTEM_PREFIX + 16, "XML 转换失败"),

    /** 缓存模式没有设置 */
    CACHE_MODEL_NOT_SET(SYSTEM_PREFIX + 17, "缓存模式没有设置"),

    /** redis地址{0}未配置 */
    REDIS_ADDRESS_NOT_SET(SYSTEM_PREFIX + 18, "redis地址{0}未配置"),

    /** 数据库链接不存在 name=[{0}] */
    DB_DATASOURCE_NOT_SET(SYSTEM_PREFIX + 19, "数据库链接不存在 name=[{0}]"),

    /** 缩放图片失败 */
    IMAGE_ZOOM_ERROR(SYSTEM_PREFIX + 20, "缩放图片失败"),

    /** 读取内容体失败 */
    READ_PARAM_ERROR(SYSTEM_PREFIX + 21, "读取内容体失败"),

    /** 读取jar文件内容失败 */
    READ_FILE_ERROR(SYSTEM_PREFIX + 22, "读取{0}文件内容失败"),

    /** 写文件失败 */
    WRITE_FILE_ERROR(SYSTEM_PREFIX + 23, "写文件失败"),

    /** MD5 ERROR */
    MD5_ERROR(SYSTEM_PREFIX + 24, "MD5 ERROR"),

    /** base64 */
    BASE64_ERROR(SYSTEM_PREFIX + 25, "base64"),

    /** 执行JS脚本错误 */
    EVAL_JAVASCRIPT_ERROR(SYSTEM_PREFIX + 26, "执行JS脚本错误"),

    /** {0}不能为空 */
    PARAM_NOT_NULL(SYSTEM_PREFIX + 27, "{0}不能为空"),

    /** 缓存Key设置失败 */
    CACHE_KEY_ERROR(SYSTEM_PREFIX + 28, "缓存Key设置失败"),

    /** 代理目标不存在{0} */
    PROXY_TARGET_NOT_FOUND(SYSTEM_PREFIX + 29, "代理目标不存在{0}"),

    /** 重复消息{0} */
    DULPLICATE_MESSAGE(SYSTEM_PREFIX + 30, "重复消息{0}"),

    /** 保存的数量过多，请采用batchExecute替代 */
    TOO_MANY_OBJECTS(SYSTEM_PREFIX + 31, "保存的数量过多，请采用batchExecute替代"),

    /** DAO接口{0}中泛型未设置，请检查 */
    GENERIC_TYPE_ERROR(SYSTEM_PREFIX + 32, "DAO接口{0}中泛型未设置，请检查"),

    /** 不支持延迟消息 */
    UNSPORT_DELAY_MESSAGE(SYSTEM_PREFIX + 33, "不支持延迟消息"),

    /** ID 不能为空 */
    ID_IS_NULL(SYSTEM_PREFIX + 34, "ID 不能为空"),

    /** 未找到分布式事务的发送者 */
    TRASCATION_SENDER_NOT_FOUND(SYSTEM_PREFIX + 35, "未找到分布式事务的发送者"),

    /** 未找到{0}协议的实现者 */
    TRASACTION_RETRY_SENDER_NOT_FOUND(SYSTEM_PREFIX + 36, "未找到{0}协议的实现者"),

    /** 未找到transId生成工厂 */
    TRANS_ID_GENERATOR_FACTORY_NOT_FOUND(SYSTEM_PREFIX + 37, "未找到transId生成工厂"),

    /** 未找到客户端信息生成工厂 */
    TRANS_CLIENT_INFO_FACTORY_NOT_FOUND(SYSTEM_PREFIX + 38, "未找到客户端信息生成工厂"),

    /** 加密错误 */
    ENCRYPTION_ERROR(SYSTEM_PREFIX + 39, "加密错误"),

    /** 解密错误 */
    DECRYPTION_ERROR(SYSTEM_PREFIX + 40, "解密错误"),

    /** SHA256 ERROR */
    SHA256_ERROR(SYSTEM_PREFIX + 41, "SHA256 ERROR"),

    /** {0}.db.url未配置 */
    DB_URL_NOT_SET(SYSTEM_PREFIX + 42, "{0}.db.url未配置"),

    /** 查询的属性名是空的 */
    DAO_PROPERTY_IS_EMPTY(SYSTEM_PREFIX + 43, "查询的属性名是空的"),

    /** 缓存未配置,请参考cache.mode配置 */
    CACHE_NOT_FOUND(SYSTEM_PREFIX + 44, "缓存未配置,请参考cache.mode配置"),

    /** 消息模式没有设置，请参考message.model配置 */
    MESSAGE_MODEL_NOT_SET(SYSTEM_PREFIX + 45, "消息模式没有设置，请参考message.model配置"),

    /** 未找到消息中间件 */
    MESSAGE_MIDDLE_NOT_FOUND(SYSTEM_PREFIX + 46, "未找到消息中间件"),

    /** 流程未匹配{0} */
    FLOW_NOT_MATCH(SYSTEM_PREFIX + 47, "流程未匹配{0}"),

    /** 流程组件未找到 */
    FLOW_COMPONENT_NOT_FOUND(SYSTEM_PREFIX + 48, "流程组件未找到"),

    /** 不支持的事务类型{0} */
    UNSUPPORT_TRASACTION_TYPE(SYSTEM_PREFIX + 49, "不支持的事务类型{0}"),

    /** SQL不支持星号 */
    UNSUUPORT_ASTERISK(SYSTEM_PREFIX + 50, "SQL不支持星号"),

    /** Job 服务的注册中心未配置,请参考job.register.url配置 */
    JOB_REGISTER_URL_IS_NULL(SYSTEM_PREFIX + 51, "Job 服务的注册中心未配置,请参考job.register.url配置"),

    /** 注册中心的命名空间未配置，请参考job.register.namespace配置 */
    JOB_REGISTER_NAMESPACE_IS_NULL(SYSTEM_PREFIX + 52, "注册中心的命名空间未配置，请参考job.register.namespace配置"),

    /** 消息中心的生产者创建失败 */
    MESSAGE_MODEL_P_CREATE_ERROR(SYSTEM_PREFIX + 53, "消息中心的生产者创建失败"),

    /** 消息中心的消费者创建失败 */
    MESSAGE_MODEL_C_CREATE_ERROR(SYSTEM_PREFIX + 54, "消息中心的消费者创建失败"),

    /** 消息中心的生产者发送消息失败 */
    MESSAGE_MODEL_P_SEND_ERROR(SYSTEM_PREFIX + 55, "消息中心的生产者发送消息失败"),

    /** STATE MACHINE 的开始状态未配置 */
    BEGIN_STATE_NOT_EMPTY(SYSTEM_PREFIX + 56, "STATE MACHINE 的开始状态未配置"),

    /** STATE MACHINE 的结束状态未配置 */
    END_STATE_NOT_EMPTY(SYSTEM_PREFIX + 57, "STATE MACHINE 的结束状态未配置"),

    /** STATE MACHINE 的流程控制不能为空 */
    CONTROL_NOT_NULL(SYSTEM_PREFIX + 58, "STATE MACHINE 的流程控制不能为空"),

    /** 流程中事件不能为空 */
    EVENT_NOT_EMPTY(SYSTEM_PREFIX + 59, "流程中事件不能为空"),

    /** 状态未匹配上{0} */
    STATE_NOT_MATCH(SYSTEM_PREFIX + 60, "状态未匹配上{0}"),

    /** 未找到失败状态的配置 */
    ERROR_STATE_NOT_FOUND(SYSTEM_PREFIX + 61, "未找到失败状态的配置"),

    /** STATE MACHINE的FlowBean 必须实现 StateMachineFlowBean */
    FLOW_MUST_HAS_STATE(SYSTEM_PREFIX + 62, "STATE MACHINE的FlowBean 必须实现 StateMachineFlowBean"),

    /** 未找到状态{0}对应的事件{1} */
    EVENT_NOT_FOUND(SYSTEM_PREFIX + 63, "未找到状态{0}对应的事件{1}"),

    /** JDK 序列化失败[{0}] */
    JDK_SERIALIZE_ERROR(SYSTEM_PREFIX + 64, "JDK 序列化失败[{0}]"),

    /** JDK 反序列化失败[{0}] */
    JDK_UNSERIALIZE_ERROR(SYSTEM_PREFIX + 65, "JDK 反序列化失败[{0}]"),

    /** 参数的内容大小超过65535个字节 */
    ARGUMENTS_SIZE_TOO_LARGE(SYSTEM_PREFIX + 66, "参数的内容大小超过65535个字节"),

    /** {0}未配置，请检查配置文件 */
    CONFIG_NOT_SET(SYSTEM_PREFIX + 67, "{0}未配置，请检查配置文件"),

    /** {0}参数错误 */
    PARAM_ERROR(SYSTEM_PREFIX + 68, "{0}参数错误"),

    /** 创建临时目录{0}失败，请检查目前权限 */
    CREATE_TEMP_FILE_ERROR(SYSTEM_PREFIX + 69, "创建临时目录{0}失败，请检查目前权限"),

    /** 大语言模型错误：{0} */
    LLM_ERROR(SYSTEM_PREFIX + 70, "大语言模型错误:{0}"),

    /** {0}重复了 */
    PARAM_REPEAT(SYSTEM_PREFIX + 71, "{0}重复了"),

    /** 流程执行超过最大深度{0} */
    FLOW_STACK_OVERFLOW(SYSTEM_PREFIX + 72, "流程执行超过最大深度{0}"),

    /** 压缩文件的路径不能再源文件的路径中 */
    ZIP_PATH_IS_IN_SRC_PATH(SYSTEM_PREFIX + 73, "压缩文件的路径不能再源文件的路径中"),;

    /** code */
    private final int code;

    /** msg */
    private final String msg;

}
