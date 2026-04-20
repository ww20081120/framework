# Framework 4.X

## 1. 简介

Framework 属于企业级底层开发框架，集成了 log、cache、db、message、rule、tx、ai 等模块，每块都以模块形式组织，可以根据项目需要获取模块。我们的初衷是屏蔽项目中各种第三方库之间的版本冲突，打造一套屏蔽底层中间件的全新 API，提高项目代码的适配能力。

+ [framework-common](modules/common) 定义公用的常量、工具类，采用了 spring-boot 方式启动，启动类为 Application，也可以支持 web 方式启动。
+ [framework-tracing](modules/tracing) 分布式链路追踪模块，支持 SkyWalking、Zipkin 等主流追踪系统。
+ [framework-log](modules/tracing) 分布式集成日志模块，详细的记录了每个方法执行的参数、返回结果、执行时间，可以很方便的排查问题或告警，通过远程接口上传服务器（支持直连服务端，也支持通过 kafka 发送）
+ [framework-cache](modules/cache) 定义了缓存的获取。支持注解方式访问缓存，支持基于 Redis 的分布式锁
+ [framework-db](modules/database) 是简单易用的轻量级 DAO(Data Access Object) 框架，它集成了 Hibernate 实体维护和 Mybaits SQL 分离的两大优势，提供了非入侵式 API，可以与 Hibernate、SpringJdbc 等数据库框架很好的集成
+ [framework-job](modules/job) 定时任务，支持 quartz、xxl-job、ElasticJob 简单封装的定时器，支持分布式、分片等功能
+ [framework-message](modules/message) 消息模块，通过简单的 api 发布和订阅事件，目前支持 kafka、redis、rocketMq
+ [framework-rule](modules/rule) 规则引擎，基于 json 的轻量级规则引擎，支持多种插件及扩展，例如：基于状态机的工作流引擎
+ [framework-tx](modules/tx) 分布式事务，支持各种远程接口、同步异步消息。
+ [framework-ai](modules/ai) AI 功能模块，集成了多种 AI 能力：
  + framework-ai-core 核心 AI 能力封装，提供统一的 AI 服务接口
  + framework-ai-spring Spring 集成的 AI 模块，简化 AI 在 Spring 项目中的使用
  + framework-ai-agent 支持智能体开发，提供复杂的 AI 交互能力
  + framework-ai-agentscope 基于 AgentScope 的 AI 智能体开发框架
  + framework-ai-demo 包含 OpenAI 和 AgentScope 的示例项目
+ framework-dependencies 项目依赖，解决版本包依赖问题
+ framework-shell 控制台方式提供命令操作，支持自定义各种命令，做各种小工具使用。

> JDK 1.8 请使用 Framework 3.X 版本，Framework 4.X 已升级至 JDK 21 版本

## 2. 框架的由来

Hibernate 我用了 2 年半，13 年下半年去中兴软创用了一年 SQL 服务（软创内部框架），14 年在京东驻场用了 2 个月的 MyBatis，综合了一下这些项目，各有各的优缺点。例如针对复杂业务 SQL，hibernate 明显能力不足，简单的功能 MyBaties 也要弄死人，所以一直在思考一个问题有没有一个框架能扬长避短，把大家的优点都发挥出来。当时在某网站上看了一个帖子介绍了 minidao，思路很新颖，拜读了源码。从此框架之路走起。

* 14 年 7 月份左右在软创内部 gitlab 上发布了第一个版本 easydao，主要是结合软创当时的系统框架在其之上封装了一层。
* 14 年 10 月在 github 上发布了 [easydao](https://github.com/ww20081120/easydao) 剥离掉软创内部框架依赖，使其可以不依赖软创的框架，可以结合 spring 和 hibernate，或者可以单独使用 jdbc 来使用。
* 15 年 6 月开始 framework-0.1 版本的设计，数据库已经用的很爽了，但是一个项目不仅仅是数据库，还有很多其他东西，当时针对的是 web 项目规划了很多模块，类似于现在的 web 结构，做了 job 可以在线管理，消息、rpc、缓存等等功能
* 16 年 1 月 22 日正式发布 1.0 版本
* 16 年 7 月 21 日发布 2.0 版本，web 模块和 jeecg 合并单独组成 framework-manager，framework 专门解决项目底层问题
* 17 年 9 月 24 日发布 3.0 版本，升级了 spring boot 版本至 2.0，去掉了 dubbox 这个 rpc 框架，引入 spring-cloud 框架。前端也放弃了 jeecg，基于 [ant-design-pro](https://pro.ant.design/index-cn) 实现的一套 web 框架
* 20 年 2 月 4 日发布了 3.4 版本，增加了 framework-tx 模块，正式支持分布式事务。
* 23 年人工智能比较火，增加了 framework-ai 模块，集成了多种 AI 能力，包括智能体、OpenAI 和 AgentScope 等子模块。

## 3. 采用项目

1. [中兴视通网上营业厅项目 V1.0](https://www.seecom.com.cn)
2. [咪咕在线客服 V1.0](https://kf.migu.cn)
3. 中国实践教育平台 V2.0
4. 大丰科创园微信项目 V1.0
5. 苏州市总工会微信 V1.0
6. 佛山港华网上营业厅项目 V1.0
7. 苏州港华网上营业厅项目 V1.0
8. 苏州体育局微信活动运营项目 V1.0
9. 苏州市防汛排涝物资管理系统 V1.0
10. [港华集团网上营业厅项目](https://www.towngasvcc.com)
11. E 网通项目
12. 港华紫荆微信项目
13. 港华物联网平台

## 4. 快速入门

### 4.1 下载编译

```bash
# 下载代码
git clone https://github.com/ww20081120/framework.git

# 进入代码目录
cd framework

# 使用 maven 编译项目，并跳过测试
mvn clean install -Dmaven.test.skip=true
```

### 4.2 创建项目

```bash
# 创建工程目录
mkdir ~/framework-test && cd ~/framework-test

# 初始化 git
git init

# 创建目录结构
mkdir -p src/main/java src/main/resources
```

### 4.3 配置 pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.hbasesoft.framework</groupId>
        <artifactId>framework</artifactId>
        <version>4.2.0</version>
    </parent>
    <groupId>com.hbasesoft.test</groupId>
    <artifactId>framework-test</artifactId>
    <version>0.1</version>
    <dependencies>
        <dependency>
            <groupId>com.hbasesoft.framework</groupId>
            <artifactId>framework-common</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 4.4 创建 application.yml

```yaml
project:
  name: framework-test

spring:
  application:
    name: ${project.name}
```

### 4.5 创建启动类

```java
package com.hbasesoft.framework.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.hbasesoft.framework.common.Bootstrap;

@SpringBootApplication
public class Application {
    public static void main(final String[] args) throws Exception {
        Bootstrap.before();
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        Bootstrap.after(context);
    }
}
```

### 4.6 启动

```bash
mvn spring-boot:run
```

## 5. 开发指南

* JDK 21+
