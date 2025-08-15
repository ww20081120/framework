# 用户故事: 集成国密算法 (SM2/SM3/SM4) 工具类

## 1. Story (故事)

作为一个开发者
我希望Framework4.X能在`framework-common`模块中提供一套简单易用的国家商用密码算法（SM2/SM3/SM4）工具类
以便于我能方便地在我的应用中实现基础的国密加解密和签名功能，满足合规要求。

## 2. Acceptance Criteria (验收标准)

### 2.1 功能实现
*   GIVEN 我的项目依赖了`com.hbasesoft.framework:framework-common-core` *   WHEN 我调用`framework-common`中提供的SM2/SM3/SM4工具类进行加解密、签名验签、哈希计算
*   THEN 功能应能正确执行，并返回符合国家标准的计算结果。

### 2.2 易用性
*   GIVEN 我是Framework4.X的开发者
*   WHEN 我需要使用国密算法
*   THEN 我可以通过调用`framework-common`中的静态工具方法快速集成，无需复杂的配置。

### 2.3 标准符合性
*   GIVEN Framework4.X提供的国密工具类实现
*   WHEN 由第三方机构进行检测
*   THEN 实现应符合国家商用密码管理局发布的相关标准。

## 3. Tasks / Subtasks (任务/子任务)
- [ ] 调研国密算法（SM2/SM3/SM4）的标准规范和成熟的Java实现方案（如 Bouncy Castle）。
- [ ] 在`framework-common-core`模块中创建`com.hbasesoft.framework.common.security.gm`包。
- [ ] 实现SM2/SM3/SM4的封装工具类（如`SM2Util`, `SM3Util`, `SM4Util`），提供静态方法。
- [ ] 编写单元测试，覆盖所有工具类的功能点和边界条件，使用官方测试数据集。
- [ ] 在`framework-common-core`的`README.md`或JavaDoc中添加国密工具类的使用说明和示例。
- [ ] （可选）提供一个简单的配置类或常量类，方便管理国密相关的OID等常量。

## 4. Dev Notes (开发说明)
*   优先集成和封装成熟的第三方库（如 Bouncy Castle），避免重复造轮子。
*   工具类设计应追求简单、无状态、静态方法调用。
*   API命名应清晰，例如 `SM4Util.encrypt(byte[], byte[])`。
*   需要处理好密钥的输入格式（如字节数组、十六进制字符串）。
*   考虑性能，但首要保证正确性和标准符合性。

## 5. Testing (测试)
*   编写全面的单元测试，使用官方或权威来源的测试数据集验证算法正确性。
*   进行边界条件测试（如空输入、超长输入）。
*   （如果可能）邀请第三方安全机构进行初步评估。

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
*   **Story ID**: S-003-GM-Support
*   **Epic**: 国产化支持 (E-001-Localization)
*   **Feature**: 安全增强
*   **Related PRD**: [docs/prd/prd-国产化支持.md](../../docs/prd/prd-国产化支持.md)
*   **Priority**: High
*   **Estimate**: 8 story points