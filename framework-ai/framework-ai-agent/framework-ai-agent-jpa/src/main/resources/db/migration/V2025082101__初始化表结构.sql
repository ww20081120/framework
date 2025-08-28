-- MySQL database schema for Spring AI Alibaba JManus

-- 系统配置表
DROP TABLE IF EXISTS system_config;
CREATE TABLE system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_group VARCHAR(255) NOT NULL COMMENT '配置组',
    config_sub_group VARCHAR(225) NOT NULL COMMENT '配置子组',
    config_key VARCHAR(255) NOT NULL COMMENT '配置键',
    config_path VARCHAR(255) NOT NULL UNIQUE COMMENT '配置项完整路径',
    config_value TEXT COMMENT '配置值',
    default_value TEXT COMMENT '默认值',
    description TEXT COMMENT '配置描述',
    input_type VARCHAR(255) NOT NULL COMMENT '输入类型',
    options_json TEXT COMMENT '选项JSON字符串，用于存储SELECT类型选项数据',
    update_time DATETIME NOT NULL COMMENT '最后更新时间',
    create_time DATETIME NOT NULL COMMENT '创建时间'
) COMMENT='系统配置表';

-- 计划执行记录表
DROP TABLE IF EXISTS plan_execution_record;
CREATE TABLE plan_execution_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    plan_id VARCHAR(255) NOT NULL UNIQUE COMMENT '计划ID',
    gmt_create DATETIME NOT NULL COMMENT '创建时间',
    gmt_modified DATETIME NOT NULL COMMENT '修改时间',
    plan_execution_record TEXT COMMENT '计划执行记录'
) COMMENT='计划执行记录表';

-- 计划模板表
DROP TABLE IF EXISTS plan_template;
CREATE TABLE plan_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    plan_template_id VARCHAR(50) NOT NULL UNIQUE COMMENT '计划模板ID',
    title VARCHAR(255) COMMENT '标题',
    user_request VARCHAR(4000) COMMENT '用户请求',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间'
) COMMENT='计划模板表';

-- 计划模板版本表
DROP TABLE IF EXISTS plan_template_version;
CREATE TABLE plan_template_version (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    plan_template_id VARCHAR(50) NOT NULL COMMENT '计划模板ID',
    version_index INT NOT NULL COMMENT '版本索引',
    plan_json TEXT NOT NULL COMMENT '计划JSON',
    create_time DATETIME NOT NULL COMMENT '创建时间'
) COMMENT='计划模板版本表';

-- 动态模型表
DROP TABLE IF EXISTS dynamic_models;
CREATE TABLE dynamic_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    base_url VARCHAR(255) NOT NULL COMMENT '基础URL',
    api_key VARCHAR(255) NOT NULL COMMENT 'API密钥',
    headers VARCHAR(2048) COMMENT '请求头',
    model_name VARCHAR(255) NOT NULL COMMENT '模型名称',
    model_description VARCHAR(1000) NOT NULL COMMENT '模型描述',
    type VARCHAR(255) NOT NULL COMMENT '模型类型',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认模型',
    temperature DOUBLE COMMENT '温度参数',
    top_p DOUBLE COMMENT 'Top P参数',
    completions_path VARCHAR(255) COMMENT '补全路径'
) COMMENT='动态模型表';

-- 定时任务表
DROP TABLE IF EXISTS cron_task;
CREATE TABLE cron_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    cron_name VARCHAR(255) NOT NULL COMMENT '定时任务名称',
    cron_time VARCHAR(255) NOT NULL COMMENT 'Cron表达式',
    plan_desc VARCHAR(255) NOT NULL COMMENT '计划描述',
    status INT NOT NULL COMMENT '状态',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    last_executed_time DATETIME COMMENT '最后执行时间',
    plan_template_id VARCHAR(255) COMMENT '计划模板ID'
) COMMENT='定时任务表';

-- 命名空间表
DROP TABLE IF EXISTS namespace;
CREATE TABLE namespace (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '命名空间名称',
    code VARCHAR(255) NOT NULL COMMENT '命名空间代码',
    description VARCHAR(1024) COMMENT '描述',
    host VARCHAR(255) COMMENT '主机地址'
) COMMENT='命名空间表';

-- MCP配置表
DROP TABLE IF EXISTS mcp_config;
CREATE TABLE mcp_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    mcp_server_name VARCHAR(255) NOT NULL UNIQUE COMMENT 'MCP服务器名称',
    connection_type VARCHAR(255) NOT NULL COMMENT '连接类型',
    connection_config VARCHAR(4000) NOT NULL COMMENT '连接配置',
    status VARCHAR(10) DEFAULT 'ENABLE' COMMENT '状态'
) COMMENT='MCP配置表';

-- 动态记忆表
DROP TABLE IF EXISTS dynamic_memories;
CREATE TABLE dynamic_memories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    memory_id VARCHAR(255) NOT NULL COMMENT '记忆ID',
    memory_name VARCHAR(255) NOT NULL COMMENT '记忆名称',
    create_time DATETIME NOT NULL COMMENT '创建时间'
) COMMENT='动态记忆表';

-- 动态代理表
DROP TABLE IF EXISTS dynamic_agents;
CREATE TABLE dynamic_agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    agent_name VARCHAR(255) NOT NULL UNIQUE COMMENT '代理名称',
    agent_description VARCHAR(1000) NOT NULL COMMENT '代理描述',
    system_prompt TEXT COMMENT '系统提示词（已弃用）',
    next_step_prompt TEXT NOT NULL COMMENT '下一步提示词',
    class_name VARCHAR(255) NOT NULL COMMENT '类名',
    model_id BIGINT COMMENT '模型ID',
    namespace VARCHAR(255) COMMENT '命名空间',
    built_in BOOLEAN DEFAULT FALSE COMMENT '是否内置'
) COMMENT='动态代理表';

-- 动态代理工具表（集合表）
DROP TABLE IF EXISTS dynamic_agent_tools;
CREATE TABLE dynamic_agent_tools (
    agent_id BIGINT NOT NULL COMMENT '代理ID',
    tool_key VARCHAR(255) COMMENT '工具键',
    FOREIGN KEY (agent_id) REFERENCES dynamic_agents(id)
) COMMENT='动态代理工具表';

-- 提示词表
DROP TABLE IF EXISTS prompt;
CREATE TABLE prompt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    prompt_name VARCHAR(255) NOT NULL COMMENT '提示词名称',
    namespace VARCHAR(255) COMMENT '命名空间',
    message_type VARCHAR(255) NOT NULL COMMENT '消息类型',
    type VARCHAR(255) NOT NULL COMMENT '类型',
    built_in BOOLEAN NOT NULL COMMENT '是否内置',
    prompt_description VARCHAR(1024) NOT NULL COMMENT '提示词描述',
    prompt_content TEXT NOT NULL COMMENT '提示词内容',
    UNIQUE KEY unique_namespace_prompt (namespace, prompt_name)
) COMMENT='提示词表';
