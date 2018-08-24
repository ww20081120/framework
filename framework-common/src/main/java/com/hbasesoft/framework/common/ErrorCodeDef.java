/**
 * 
 */
package com.hbasesoft.framework.common;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2014年12月3日 <br>
 * @since V1.0<br>
 * @see com.hbasesoft.framework.common <br>
 */
public interface ErrorCodeDef {

    /************************************************************************************
     * ---------------------------------请求成功区域 必须小于10000-------------------------------------------
     ************************************************************************************/

    /** 成功 */
    int SUCCESS = 0;

    /************************************************************************************
     * ---------------------------------系统错误区域 必须以1开头的5位数-------------------------------------------
     ************************************************************************************/

    /** 系统错误 */
    int SYSTEM_ERROR_10001 = 10001;

    /** 缓存失败 */
    int CACHE_ERROR_10002 = 10002;

    /** 不包含局部变量表信息，请使用编译器选项 javac -g */
    int CAN_NOT_FIND_VER_NAME_10003 = 10003;

    /** 解析模板失败 */
    int PARSE_TEPLATE_ERROR_10004 = 10004;

    /** 初始化SQL失败 */
    int INIT_SQL_ERROR_10005 = 10005;

    /** 找不到SQL文件 */
    int CAN_NOT_FIND_SQL_FILE_10006 = 10006;

    /** 多个ResultCallback参数 */
    int ERROR_RESULT_CALL_BACK_10007 = 10007;

    /** Pagesize 和 PageIndex 必须同时设置 */
    int PAGE_SIZE_PAGE_INDEX_BOTH_10008 = 10008;

    /** OGNL 处理错误 */
    int OGNL_ERROR_10009 = 10009;

    /** 查询失败 */
    int QUERY_ERROR_10010 = 10010;

    /** 执行sql失败 */
    int EXECUTE_ERROR_10011 = 10011;

    /** 批量失败 */
    int BATCH_EXECUTE_ERROR_10012 = 10012;

    /** 保存失败 */
    int SAVE_ERROR_10013 = 10013;

    /** 根据ID查询失败 */
    int GET_BY_ID_ERROR_10014 = 10014;

    /** 根据Bean查询失败 */
    int GET_BY_ENTITY_ERROR_10015 = 10015;

    /** 更新失败 */
    int UPDATE_ERROR_10016 = 10016;

    /** 删除失败 */
    int DELETE_ERROR_10017 = 10017;

    /** deleteById */
    int DELETE_BY_ID_ERROR_10018 = 10018;

    /** 查询一个集合 */
    int SELECT_LIST_ERROR_10019 = 10019;

    /** 缩放图片失败 */
    int IMAGE_ZOOM_10020 = 10020;

    /** 执行任务失败 */
    int SCHEDULE_TASK_ERROR_10021 = 10021;

    /** 暂停任务失败 */
    int PAUSE_TASK_ERROR_10022 = 10022;

    /** 恢复任务失败 */
    int RESUME_TASK_ERROR_10023 = 10023;

    /** 删除任务失败 */
    int REMOVE_TASK_ERROR_10024 = 10024;

    /** 菜单数据有问题 */
    int MENU_DATA_ERROR_10025 = 10025;

    /** 下载资源失败 */
    int DOWN_LOAD_RESOURCE_ERROR_10026 = 10026;

    /** 读取内容体失败 */
    int READ_PARAM_ERROR_10027 = 10027;

    /** 读取jar文件内容失败 */
    int READ_FILE_ERROR_10028 = 10028;

    /** 写文件失败 */
    int WRITE_FILE_ERROR_10029 = 10029;

    /** 栈溢出异常，启动失败 */
    int STACK_OVERFLOW_ERROR_10030 = 10030;

    /** 不支持的API类型 */
    int UNSUPPORT_API_10031 = 10031;

    /** 邮件发送失败 */
    int EMAIL_SEND_ERROR_10032 = 10032;

    /** 短信发送失败 */
    int SMS_SEND_ERROR_10033 = 10033;

    /** 百度云推送错误 */
    int BAIDUYUN_SEND_ERROR_10034 = 10034;

    /** http请求错误 */
    int HTTP_REQUEST_ERROR_10035 = 10035;

    /** MD5 ERROR */
    int MD5_ERROR_10036 = 10036;

    /** base64 */
    int BASE64_ERROR_10037 = 10037;

    /** 个推推送失败 */
    int GE_TUI_ERROR_10038 = 10038;

    /** 区域缓存失败 */
    int AREA_INITIAL_ERROR_10039 = 10039;

    /** 请使用包装类型的数组 */
    int LIST_PARAM_ERROR_10040 = 10040;

    /** 资源文件不存在 */
    int RESOUCE_NOT_EXIST_ERROR_10041 = 10041;

    /** 执行JS脚本错误 */
    int EVAL_JAVASCRIPT_ERROR_10042 = 10042;

    /** 执行所有任务失败 */
    int EXECUTE_ALL_TAKS_ERROR = 10043;

    /** 不支持的方法 */
    int UNSPORT_METHOD_ERROR = 10045;

    /** 加载模块失败 */
    int MODULE_LOADER_ERROR = 10046;

    /** 不能为空 */
    int NOT_NULL = 10047;

    /** 必须为空 */
    int IS_NULL = 10048;

    /** False */
    int IS_FALSE = 10049;

    /** True */
    int IS_TRUE = 10050;

    /** 相等 */
    int EQUALS = 10051;

    /** 不相等 */
    int NOT_EQUALS = 10052;

    /** 获取同步锁失败 */
    int GET_CACHE_LOCK_ERROR = 10053;

    /** 缓存Key设置失败 */
    int CACHE_KEY_ERROR = 10054;

    /** 代理目标不存在 */
    int PROXY_TARGET_NOT_FOUND = 10055;

    /** 重复消息 */
    int DULPLICATE_MESSAGE = 10056;

    /************************************************************************************
     * ---------------------------------用户错误区域 必须大于20000的5位数-------------------------------------------
     ************************************************************************************/

    /** access token 过期 */
    int ACCESS_TOKEN_EXPIRED = 20001;

    /** 用户名或密码错误 */
    int USER_NAME_OR_PASSWORD_ERROR = 20002;

    /** 资源ID为找到 */
    int RESOURCE_ID_ERROR = 20003;

    /** 账号已经存在 */
    int ACCOUNT_EXSIST = 20004;

    /** 操作员不存在 */
    int OPERATOR_NOT_EXSIST = 20007;

    /** 账号已锁定 */
    int ACCOUNT_IS_LOCK = 20008;

    /** 状态错误 */
    int STATE_ERROR = 20009;

    /** 不支持的媒体类型 */
    int UNSPORT_MEDIA_TYPE = 20010;

    /** 文件大小超限 */
    int FILE_IS_TO_LARGER_20011 = 20011;

    /** 文件类型不支持 */
    int UNSPORT_CONTENT_TYPE_20012 = 20012;

    /** 文件未找到 */
    int FILE_NOT_FIND_20013 = 20013;

    /** 第三方帐号不支持修改密码 */
    int UNSPORT_UPDATE_PASSWORD_20014 = 20014;

    /** 新老密码不能一致 */
    int NEW_PASSWORD_ERROR_20015 = 20015;

    /** 渠道不存在 */
    int CONTACT_CHANNEL_NOT_EXIST_20016 = 20016;

    /** 生成上传token失败 */
    int QI_NIU_UP_TOKEN_ERROR_20018 = 20018;

    /** 密码和验证码不能同时为空 */
    int VERIFY_CODE_IS_NULL_20019 = 20019;

    /** 不支持的日志类型 */
    int UNSPORT_LOGGER_TYPE = 20020;

    /** 保存任务失败 */
    int SAVE_TASK_OR_TASK_TRIGGER_ERROR = 20021;

    /** 向redis中保存数据失败 */
    int PUT_VALUE_ERROR_20022 = 20022;

    /** 获取redis中数据失败 */
    int GET_VALUE_ERROR_20023 = 20023;

    /** 删除redis中数据失败 */
    int REMOVE_VALUE_ERROR_20024 = 20024;

    /** 验证码错误 */
    int VERIFY_CODE_ERROR = 20025;

    /** 保存TASK历史记录失败 */
    int SAVE_HIS_ERROR_20026 = 20026;

    /** 删除TASK相关信息失败 */
    int DEL_TASK_ERROR_20027 = 20027;

    /** 上传图片最大宽度超出 */
    int IMAGE_WIDTH_ERROR_20028 = 20028;

    /** 序列化失败 */
    int SERIALIZE_ERROR = 20029;

    /** 反序列化失败 */
    int UNSERIALIZE_ERROR = 20030;

    /** 没权限 */
    int NO_PERMISSION = 20031;

    /** XML 转换失败 */
    int XML_TRANS_ERROR = 20032;

    /** 缓存模式没有设置 */
    int CACHE_MODEL_NOT_SET = 20033;

    /** redis 地址没有配置 */
    int REDIS_ADDRESS_NOT_SET = 20034;

    /** 数据库链接没有设置 */
    int DB_DATASOURCE_NOT_SET = 20035;

    /** 数据库地址没有配置 */
    int DB_URL_NOT_SET = 20036;

    /** 数据库用户名没有配置 */
    int DB_USERNAME_NOT_SET = 20037;

    /** 数据库密码没有设置 */
    int DB_PASSWORD_NOT_SET = 20038;

    /** 查询的属性名是空的 */
    int DAO_PROPERTY_IS_EMPTY = 20039;

    /** ID 不能为空 */
    int ID_IS_NULL = 20040;

    /** 模版代码是空的 */
    int TEMPLATE_CODE_IS_EMPTY = 20041;

    /** 消息是空的 */
    int MESSAGE_IS_EMPTY = 20042;

    /** 未找到模版 */
    int TEMPLATE_NOT_FOUND = 20043;

    /** 接受人不存在 */
    int RECEIVER_IS_EMPTY = 20044;

    /** 缓存未配置 */
    int CACHE_NOT_FOUND = 20045;

    /** 消息模式没有设置 */
    int MESSAGE_MODEL_NOT_SET = 20046;

    /** 未找到消息中间件 */
    int MESSAGE_MIDDLE_NOT_FOUND = 20047;

    /** 流程未匹配 */
    int FLOW_NOT_MATCH = 20048;

    /** 流程组件未找到 */
    int FLOW_COMPONENT_NOT_FOUND = 20049;

    /**
     * 流程组件和子流程都未配置
     */
    int FLOW_COMPONENT_INSTANCE_OR_CHILDREN_NOT_FOUND = 20050;

    /**
     * 不支持的事务类型
     */
    int UNSUPPORT_TRASACTION_TYPE = 20051;

    /** SQL不支持星号 */
    int UNSUUPORT_ASTERISK = 20052;

    /** Job 服务的注册中心未配置 */
    int JOB_REGISTER_URL_IS_NULL = 20053;

    /** 注册中心的命名空间未配置 */
    int JOB_REGISTER_NAMESPACE_IS_NULL = 20054;

    /** 消息中心的生产者组名未指定 */
    int MESSAGE_MODEL_EMPTY_P_GROUP_NAME = 20055;

    /** 消息中心的消费者组名未指定 */
    int MESSAGE_MODEL_EMPTY_C_GROUP_NAME = 20056;

    /** 消息中心的生产者创建失败 */
    int MESSAGE_MODEL_P_CREATE_ERROR = 20057;

    /** 消息中心的消费者创建失败 */
    int MESSAGE_MODEL_C_CREATE_ERROR = 20058;

    /** 消息中心的生产者发送消息失败 */
    int MESSAGE_MODEL_P_SEND_ERROR = 20059;

    /** STATE MACHINE 的开始状态未配置 */
    int BEGIN_STATE_NOT_EMPTY = 20060;

    /** STATE MACHINE 的结束状态未配置 */
    int END_STATE_NOT_EMPTY = 20061;

    /** STATE MACHINE 的流程控制不能为空 */
    int CONTROL_NOT_NULL = 20062;

    /** 流程中事件不能为空 */
    int EVENT_NOT_EMPTY = 20063;

    /** 状态未匹配上 */
    int STATE_NOT_MATCH = 20064;

    /** 未找到失败状态的配置 */
    int ERROR_STATE_NOT_FOUND = 20065;
}
