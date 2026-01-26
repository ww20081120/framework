#!/bin/bash

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
