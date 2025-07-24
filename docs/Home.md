### 1.简介

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Framework属于企业级底层开发框架，集成了log、cache、db、message、rule、tx、ai等模块，每块都以模块形式组织，可以根据项目需要获取模块。我们的初衷是屏蔽项目中各种第三方库之间的版本冲突，打造一套屏蔽底层中间件的全新API，提高项目代码的适配能力。
 
 + [framework-common](基础工具) 定义公用的常量、工具类 采用了spring-boot方式启动， 启动类为Application， 也可以支持web方式启动。
+ [framework-log](跟踪日志) 分布式集成日志模块，详细的记录了每个方法执行的参数、返回结果、执行时间，可以很方便的排查问题或告警，通过远程接口上传服务器（支持直连服务端，也支持通过kafka发送）
+ [framework-cache](缓存) 定义了缓存的获取。  支持注解方式访问缓存， 支持基于Redis的分布式锁
+ [framework-db](数据库) 是简单易用的轻量级DAO(Data Access Object)框架，它集成了Hibernate实体维护和Mybaits SQL分离的两大优势，提供了非入侵式API，可以与Hibernate、SpringJdbc等数据库框架很好的集成 
+ [framework-job](任务) 定时任务，支持quartz、xxl-job、[ElasticJob](http://elasticjob.io)简单封装的定时器，支持分布式、分片等功能
+ [framework-message](异步消息) 消息模块，通过简单的api发布和订阅事件， 目前支持kafka、redis、rocketMq
+ [framework-rule](规则引擎) 规则引擎，基于json的轻量级规则引擎， 支持多种插件及扩展， 例如：基于状态机的工作流引擎
+ [framework-tx](分布式事务) 分布式事务，支持各种远程接口、同步异步消息。
+ [framework-ai](人工智能) AI功能模块，基于langchain4j和国内大模型API，提供自然语言处理、图像处理、NL2SQL等功能。
+ [framework-dependencies] 项目依赖，解决版本包依赖问题
+ [framework-shell] 控制台方式提供命令操作，支持自定义各种命令，做各种小工具使用。
+ [framework-langchain4j] 对langchain4j的补充，支持国内的大模型，让大家更高效的开发AIGC应用。

jdk1.8请使用framework3.X版本，framework4.X已升级至jdk21版本

### 2.框架的由来

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Hibernate我用了2年半， 13年下半年去中兴软创用了一年SQL服务（软创内部框架）， 14年在京东驻场用了2个月的MyBatis，综合了一下这些项目，各有各的优缺点。例如针对复杂业务SQL，hibernate明显能力不足，简单的功能MyBaties也要弄死人，所以一直在思考一个问题有没有一个框架能扬长避短，把大家的优点都发挥出来。 当时在某网站上看了一个帖子介绍了minidao，思路很新颖，拜读了源码。 从此框架之路走起。（ 为什么不直接使用minidao，一是这个项目不火、更新节奏也不快，使用风险较大， 二是软创内部使用的都是自己的框架，连spring都没有，hibernate更不可能，jdk都处于1.4、1.5版本，不可能直接使用minidao ）

* 14年7月份左右在软创内部gitlab上发布了第一个版本easydao，主要是结合软创当时的系统框架在其之上封装了一层。
* 14年10月份在github上发布了[easydao](https://github.com/ww20081120/easydao) 剥离掉软创内部框架依赖，使其可以不依赖软创的框架，可以结合spring和hibernate，或者可以单独使用jdbc来使用。
* 15年6月开始framework-0.1版本的设计，数据库已经用的很爽了，但是一个项目不仅仅是数据库，还有很多其他东西， 当时针对的是web项目规划了很多模块，类似于现在的web结构， 做了job可以在线管理，消息、rpc、缓存等等功能
* 16年1月22日正式发布1.0版本
* 16年7月21日发布2.0版本，web模块和jeecg合并单独组成framework-manager， framework专门解决项目底层问题
* 17年9月24日发布3.0版本，升级了spring boot版本至2.0， 去掉了dubbox这个rpc框架，引入spring-cloud框架。前端也放弃了jeecg，基于[ant-design-pro](https://pro.ant.design/index-cn) 实现的一套web框架(目前还未从项目中分离出来，暂未开源)
* 20年2月4日发布了3.4版本， 增加了framework-tx模块，正式支持分布式事务。
* 23年人工智能比较火，又增加了framework-langchain4j，专门扩展国内的一些大模型。

### 3.采用项目
1.  [中兴视通网上营业厅项目V1.0](https://www.seecom.com.cn)
2.  [咪咕在线客服V1.0](https://kf.migu.cn)
3.  中国实践教育平台V2.0
4.  大丰科创园微信项目V1.0
5.  苏州市总工会微信V1.0
6.  佛山港华网上营业厅项目V1.0
7.  苏州港华网上营业厅项目V1.0
8.  苏州体育局微信活动运营项目V1.0
9.  苏州市防汛排涝物资管理系统V1.0
10.  [港华集团网上营业厅项目](https://www.towngasvcc.com)
11. E网通项目
12. 港华紫荆微信项目
13. 港华物联网平台
