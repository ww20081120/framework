#!/bin/bash

# 代码质量检查开关（从 pom.xml 读取）
CODE_QUALITY_CHECKS_ENABLED=$(grep -m1 'code.quality.checks.enabled' "$(git rev-parse --show-toplevel)/pom.xml" | sed 's/.*>\(.*\)<.*/\1/')

if [ "$CODE_QUALITY_CHECKS_ENABLED" != "true" ]; then
    echo "⚠️ 代码质量检查已禁用 (code.quality.checks.enabled=$CODE_QUALITY_CHECKS_ENABLED)"
    echo "💡 如需启用检查，请在 pom.xml 中设置 code.quality.checks.enabled=true"
    exit 0
fi

# 设置 JAVA_HOME（SpotBugs 等插件需要 Java 21+）
if [ -z "$JAVA_HOME" ] || ! "$JAVA_HOME/bin/java" -version 2>&1 | grep -q 'version "2[1-9]'; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home"
fi

# 查找项目根目录（包含 pom.xml 的目录）
PROJECT_ROOT=$(git rev-parse --show-toplevel)
if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
    echo "⚠️  无法找到项目根目录的 pom.xml"
    exit 1
fi

echo "========================================"
echo "开始执行代码质量检查..."
echo "项目根目录: $PROJECT_ROOT"
echo "========================================"

# CheckStyle 检查
echo "1. 执行 CheckStyle 检查..."
cd "$PROJECT_ROOT"
mvn checkstyle:check
RESULT=$?
if [ $RESULT -ne 0 ]; then
    echo "❌ CheckStyle 检查失败，请修复代码风格问题后再提交"
    exit $RESULT
fi
echo "✅ CheckStyle 检查通过"

# PMD 检查（需要从项目根目录运行）
echo "2. 执行 PMD 检查..."
cd "$PROJECT_ROOT"
mvn pmd:check
RESULT=$?
if [ $RESULT -ne 0 ]; then
    echo "❌ PMD 检查失败，请修复代码质量问题后再提交"
    echo "💡 查看 target/pmd.xml 获取详细报告"
    exit $RESULT
fi
echo "✅ PMD 检查通过"

# SpotBugs 检查
echo "3. 执行 SpotBugs 检查..."
cd "$PROJECT_ROOT"
mvn spotbugs:check
RESULT=$?
if [ $RESULT -ne 0 ]; then
    echo "❌ SpotBugs 检查失败，请修复潜在 Bug 后再提交"
    exit $RESULT
fi
echo "✅ SpotBugs 检查通过"

echo "========================================"
echo "✅ 所有代码质量检查通过！"
echo "========================================"
exit 0
