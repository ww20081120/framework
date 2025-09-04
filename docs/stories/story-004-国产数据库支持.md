# 用户故事: 支持达梦和人大金仓数据库

## 1. Story (故事)

作为一个开发者
我希望Framework4.X的`framework-db`模块能够支持达梦(DM)和人大金仓(KingbaseES)数据库
以便于在使用这些国产数据库的信创项目中无缝使用Framework框架。

## 2. Acceptance Criteria (验收标准)

### 2.1 驱动集成与连接
*   GIVEN 我在项目中引入了Framework4.X和达梦/人大金仓的JDBC驱动依赖
*   AND 在`application.yml`中正确配置了数据库连接信息
*   WHEN 应用启动时
*   THEN `framework-db`应能成功加载并使用对应的JDBC驱动连接到达梦/人大金仓数据库。

### 2.2 基本CRUD操作
*   GIVEN 应用已连接到达梦/人大金仓数据库
*   WHEN 我使用`framework-db`的DAO接口或QueryWrapper执行基本的增删改查操作
*   THEN 这些操作应能正确执行，数据能被正确地持久化和检索。

### 2.3 SQL方言适配
*   GIVEN 我使用`framework-db`的高级查询功能（如分页）
*   WHEN 查询被发送到数据库
*   THEN 生成的SQL语句应符合达梦/人大金仓的语法要求，能被数据库正确解析和执行。

### 2.4 Demo验证
*   GIVEN `framework-db-demo`模块
*   WHEN 我将其配置为使用达梦/人大金仓数据库并运行
*   THEN Demo中的所有功能测试用例应能通过。

## 3. Tasks / Subtasks (任务/子任务)
- [ ] 获取达梦(DM)和人大金仓(KingbaseES)数据库的JDBC驱动及其使用文档。
- [ ] 搭建达梦和人大金仓数据库的测试环境。
- [ ] 在`framework-db`模块中集成达梦和人大金仓的JDBC驱动依赖（或提供明确的引入指南）。
- [ ] 开发或配置针对达梦/人大金仓的SQL方言支持（Dialect）。
- [ ] 修复在连接、基本CRUD和高级查询中可能出现的兼容性问题。
- [ ] 运行`framework-db-demo`，确保其在达梦/人大金仓环境下能正常工作。
- [ ] 编写配置和使用指南文档。

## 4. Dev Notes (开发说明)
*   需要仔细阅读达梦和人大金仓的JDBC驱动文档，了解其特性和配置方式。
*   SQL方言适配是关键，需要处理好分页、特定函数、数据类型映射等问题。
*   注意事务管理、连接池配置等方面可能存在的差异。
*   应提供清晰的Maven依赖引入示例和配置样例。

## 5. Testing (测试)
*   在不同版本的达梦和人大金仓数据库上进行测试。
*   执行`framework-db`的所有单元测试，确保在新数据库上行为一致。
*   进行压力测试，评估在国产数据库上的性能表现。

## 6. Dev Agent Record (开发代理记录)
*   **Agent Model Used**: Qwen Code (*dev)
*   **Completion Notes List**:
*   **File List**:
*   **Change Log**:
*   **Debug Log References**:

## 7. QA Results (QA结果)
_待QA工程师填写_

## 8. Status (状态)
Draft

## 9. Metadata (元数据)
*   **Story ID**: S-004-DB-Support
*   **Epic**: 国产化支持 (E-001-Localization)
*   **Feature**: 核心国产化适配
*   **Related PRD**: [docs/prd/prd-国产化支持.md](../../docs/prd/prd-国产化支持.md)
*   **Priority**: High
*   **Estimate**: 13 story points