-- 插入默认大模型配置
-- id字段为自增主键，无需指定，数据库会自动生成
INSERT INTO dynamic_models (
    id,
    base_url,
    api_key,
    model_name,
    model_description,
    type,
    is_default,
    temperature,
    completions_path
) VALUES (
     1,
    'https://api-inference.modelscope.cn',
    'ms-375d4f20-87a5-4f25-a090-2568666c502f',
    'Qwen/Qwen3-Coder-480B-A35B-Instruct',
    'Qwen3 Coder模型',
    'chat',
    1,
    0.7,
    '/v1/chat/completions'
);

-- 注意：后续构建agent时需要关联model_id字段
-- 该值为上述插入记录后数据库生成的id值
-- 可通过以下查询获取：
-- SELECT id FROM dynamic_models WHERE model_name = 'Qwen/Qwen3-Coder-480B-A35B-Instruct';

-- 插入默认agent配置
-- model_id关联到上面插入的模型记录
-- 绑定关系说明：dynamic_agents.model_id -> dynamic_models.id
INSERT INTO dynamic_agents (
    agent_name,
    agent_description,
    next_step_prompt,
    class_name,
    model_id,
    namespace,
    built_in
) VALUES (
    'DEFAULT_AGENT',
    'A versatile default agent that can handle various user requests using file operations and shell commands. Perfect for general tasks that may involve file operations, system operations, or text processing.',
    '您是一位专业的系统操作员，能够处理文件操作和执行shell命令。

在处理用户请求时，请遵循以下指南：
1) 分析请求以确定所需的工具
2) 对于文件操作：
   - 验证文件类型和访问权限
   - 执行必要的文件操作（读取/写入/追加）
   - 完成后保存更改
3) 对于系统操作：
   - 检查命令安全性
   - 执行命令并适当处理错误
   - 验证命令结果
4) 跟踪所有操作及其结果

实现我的目标的下一步应该是什么？

请记住：
1. 操作前验证所有输入和路径
2. 为每个任务选择最合适的工具：
   - 使用text_file_operator进行文件操作
   - 任务完成时使用terminate
3. 优雅地处理错误
4. 重要：您必须在回复中至少使用一个工具来取得进展！

逐步思考：
1. 需要哪些核心操作？
2. 哪种工具组合最合适？
3. 如何处理潜在的错误？
4. 预期的结果是什么？
5. 如何验证成功？',
    'DefaultAgent',
    1,
    'default',
    false
);

INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'bash');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'browser_use');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'data_split_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'terminate');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'doc_loader');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'file_merge_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'mapreduce_finalize_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'form_input');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'google_search');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'inner_storage_content_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'jsx_generator_operator');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'vue_component_generator');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'map_output_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'planning');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'ppt_generator_operator');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'python_execute');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'reduce_operation_tool');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'table_processor');
INSERT INTO `dynamic_agent_tools` (`agent_id`, `tool_key`) VALUES (1, 'text_file_operator');
