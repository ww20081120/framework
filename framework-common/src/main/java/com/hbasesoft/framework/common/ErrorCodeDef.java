/**
 * 
 */
package com.hbasesoft.framework.common;

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
public interface ErrorCodeDef {

    // ************************************************************************************
    // * ---------------------------------请求成功区域 必须小于10000-------------------------------------------
    // ************************************************************************************/

    /** 成功 */
    int SUCCESS = 0;

    // ************************************************************************************
    // * ---------------------------------系统错误区域 1～ 399 -------------------------------------------
    // ************************************************************************************/
    /** 系统错误域 */
    int SYSTEM_PREFIX = 0;

    /** 系统错误 */
    int SYSTEM_ERROR = SYSTEM_PREFIX + 1;

    /** 缓存失败 */
    int CACHE_ERROR = SYSTEM_PREFIX + 2;

    /** 不包含局部变量表信息，请使用编译器选项 javac -g */
    int CAN_NOT_FIND_VER_NAME = SYSTEM_PREFIX + 3;

    /** 解析模板失败 */
    int PARSE_TEPLATE_ERROR = SYSTEM_PREFIX + 4;

    /** 初始化SQL失败 */
    int INIT_SQL_ERROR = SYSTEM_PREFIX + 5;

    /** 找不到SQL文件 */
    int CAN_NOT_FIND_SQL_FILE = SYSTEM_PREFIX + 6;

    /** 多个ResultCallback参数 */
    int ERROR_RESULT_CALL_BACK = SYSTEM_PREFIX + 7;

    /** Pagesize 和 PageIndex 必须同时设置 */
    int PAGE_SIZE_PAGE_INDEX_BOTH = SYSTEM_PREFIX + 8;

    /** OGNL 处理错误 */
    int OGNL_ERROR = SYSTEM_PREFIX + 9;

    /** 查询失败 */
    int QUERY_ERROR = SYSTEM_PREFIX + 10;

    /** 执行sql失败 */
    int EXECUTE_ERROR = SYSTEM_PREFIX + 11;

    /** 批量失败 */
    int BATCH_EXECUTE_ERROR = SYSTEM_PREFIX + 12;

    /** 保存失败 */
    int SAVE_ERROR = SYSTEM_PREFIX + 13;

    /** 序列化失败 */
    int SERIALIZE_ERROR = SYSTEM_PREFIX + 14;

    /** 反序列化失败 */
    int UNSERIALIZE_ERROR = SYSTEM_PREFIX + 15;

    /** XML 转换失败 */
    int XML_TRANS_ERROR = SYSTEM_PREFIX + 16;

    /** 缓存模式没有设置 */
    int CACHE_MODEL_NOT_SET = SYSTEM_PREFIX + 17;

    /** redis 地址没有配置 */
    int REDIS_ADDRESS_NOT_SET = SYSTEM_PREFIX + 18;

    /** 数据库链接没有设置 */
    int DB_DATASOURCE_NOT_SET = SYSTEM_PREFIX + 19;

    /** 缩放图片失败 */
    int IMAGE_ZOOM_ERROR = SYSTEM_PREFIX + 20;

    /** 读取内容体失败 */
    int READ_PARAM_ERROR = SYSTEM_PREFIX + 21;

    /** 读取jar文件内容失败 */
    int READ_FILE_ERROR = SYSTEM_PREFIX + 22;

    /** 写文件失败 */
    int WRITE_FILE_ERROR = SYSTEM_PREFIX + 23;

    /** MD5 ERROR */
    int MD5_ERROR = SYSTEM_PREFIX + 24;

    /** base64 */
    int BASE64_ERROR = SYSTEM_PREFIX + 25;

    /** 执行JS脚本错误 */
    int EVAL_JAVASCRIPT_ERROR = SYSTEM_PREFIX + 26;

    /** 不能为空 */
    int PARAM_NOT_NULL = SYSTEM_PREFIX + 27;

    /** 缓存Key设置失败 */
    int CACHE_KEY_ERROR = SYSTEM_PREFIX + 28;

    /** 代理目标不存在 */
    int PROXY_TARGET_NOT_FOUND = SYSTEM_PREFIX + 29;

    /** 重复消息 */
    int DULPLICATE_MESSAGE = SYSTEM_PREFIX + 30;

    /** 保存的数量过多，请采用batchExecute替代 */
    int TOO_MANY_OBJECTS = SYSTEM_PREFIX + 31;

    /** DAO泛型设置有问题 */
    int GENERIC_TYPE_ERROR = SYSTEM_PREFIX + 32;

    /** 不支持延迟消息 */
    int UNSPORT_DELAY_MESSAGE = SYSTEM_PREFIX + 32;

    /** 延迟消息必须大于1s */
    int DELAY_TIME_TOO_SHORT = SYSTEM_PREFIX + 34;

    /** 未找到分布式事务的发送者 */
    int TRASCATION_SENDER_NOT_FOUND = SYSTEM_PREFIX + 35;

    /** 未找到{0}协议的实现者 */
    int TRASACTION_RETRY_SENDER_NOT_FOUND = SYSTEM_PREFIX + 36;

    /** 未找到transId生成工厂 */
    int TRANS_ID_GENERATOR_FACTORY_NOT_FOUND = SYSTEM_PREFIX + 37;

    /** 未找到客户端信息生成工厂 */
    int TRANS_CLIENT_INFO_FACTORY_NOT_FOUND = SYSTEM_PREFIX + 38;

    /** 加密错误 */
    int ENCRYPTION_ERROR = SYSTEM_PREFIX + 39;

    /** 解密错误 */
    int DECRYPTION_ERROR = SYSTEM_PREFIX + 40;

    /** SHA256 ERROR */
    int SHA256_ERROR = SYSTEM_PREFIX + 41;

    /** 数据库地址没有配置 */
    int DB_URL_NOT_SET = SYSTEM_PREFIX + 42;

    /** 查询的属性名是空的 */
    int DAO_PROPERTY_IS_EMPTY = SYSTEM_PREFIX + 43;

    /** 缓存未配置 */
    int CACHE_NOT_FOUND = SYSTEM_PREFIX + 44;

    /** 消息模式没有设置 */
    int MESSAGE_MODEL_NOT_SET = SYSTEM_PREFIX + 45;

    /** 未找到消息中间件 */
    int MESSAGE_MIDDLE_NOT_FOUND = SYSTEM_PREFIX + 46;

    /** 流程未匹配 */
    int FLOW_NOT_MATCH = SYSTEM_PREFIX + 47;

    /** 流程组件未找到 */
    int FLOW_COMPONENT_NOT_FOUND = SYSTEM_PREFIX + 48;

    /**
     * 不支持的事务类型
     */
    int UNSUPPORT_TRASACTION_TYPE = SYSTEM_PREFIX + 49;

    /** SQL不支持星号 */
    int UNSUUPORT_ASTERISK = SYSTEM_PREFIX + 50;

    /** Job 服务的注册中心未配置 */
    int JOB_REGISTER_URL_IS_NULL = SYSTEM_PREFIX + 51;

    /** 注册中心的命名空间未配置 */
    int JOB_REGISTER_NAMESPACE_IS_NULL = SYSTEM_PREFIX + 52;

    /** 消息中心的生产者创建失败 */
    int MESSAGE_MODEL_P_CREATE_ERROR = SYSTEM_PREFIX + 53;

    /** 消息中心的消费者创建失败 */
    int MESSAGE_MODEL_C_CREATE_ERROR = SYSTEM_PREFIX + 54;

    /** 消息中心的生产者发送消息失败 */
    int MESSAGE_MODEL_P_SEND_ERROR = SYSTEM_PREFIX + 55;

    /** STATE MACHINE 的开始状态未配置 */
    int BEGIN_STATE_NOT_EMPTY = SYSTEM_PREFIX + 56;

    /** STATE MACHINE 的结束状态未配置 */
    int END_STATE_NOT_EMPTY = SYSTEM_PREFIX + 57;

    /** STATE MACHINE 的流程控制不能为空 */
    int CONTROL_NOT_NULL = SYSTEM_PREFIX + 58;

    /** 流程中事件不能为空 */
    int EVENT_NOT_EMPTY = SYSTEM_PREFIX + 59;

    /** 状态未匹配上 */
    int STATE_NOT_MATCH = SYSTEM_PREFIX + 60;

    /** 未找到失败状态的配置 */
    int ERROR_STATE_NOT_FOUND = SYSTEM_PREFIX + 61;

    /** STATE MACHINE的FlowBean 必须实现 StateMachineFlowBean */
    int FLOW_MUST_HAS_STATE = SYSTEM_PREFIX + 62;

    /** 未找到状态所对应的事件 */
    int EVENT_NOT_FOUND = SYSTEM_PREFIX + 63;

    /** JDK 序列化失败 */
    int JDK_SERIALIZE_ERROR = SYSTEM_PREFIX + 64;

    /** JDK 反序列化失败 */
    int JDK_UNSERIALIZE_ERROR = SYSTEM_PREFIX + 65;

    /** 参数的内容大小超过65535个字节 */
    int ARGUMENTS_SIZE_TOO_LARGE = SYSTEM_PREFIX + 66;

    /** ID 不能为空 */
    int ID_IS_NULL = SYSTEM_PREFIX + 67;

}
