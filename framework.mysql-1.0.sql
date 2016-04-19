/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2016/4/19 9:54:25                            */
/*==============================================================*/


/*==============================================================*/
/* Table: QRTZ_BLOB_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_BLOB_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   BLOB_DATA            longblob comment 'BLOB_DATA',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_BLOB_TRIGGERS comment 'Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型';

/*==============================================================*/
/* Table: QRTZ_CALENDARS                                        */
/*==============================================================*/
create table QRTZ_CALENDARS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   CALENDAR_NAME        varchar(60) not null comment '日历名称',
   CALENDAR             longblob not null comment 'CALENDAR',
   primary key (SCHED_NAME, CALENDAR_NAME)
);

alter table QRTZ_CALENDARS comment '以 Blob 类型存储 Quartz 的 Calendar 信息';

/*==============================================================*/
/* Table: QRTZ_CRON_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_CRON_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   CRON_EXPRESSION      varchar(120) not null comment '表达式',
   TIME_ZONE_ID         varchar(80) comment '时区Id',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_CRON_TRIGGERS comment '存储 Cron Trigger，包括 Cron 表达式和时区信息';

/*==============================================================*/
/* Table: QRTZ_FIRED_TRIGGERS                                   */
/*==============================================================*/
create table QRTZ_FIRED_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   ENTRY_ID             varchar(95) not null comment 'ENTRY_ID',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   INSTANCE_NAME        varchar(60) not null comment '实例名',
   FIRED_TIME           double(13,0) not null comment 'FIRED_TIME',
   SCHED_TIME           double(13,0) not null comment 'SCHED_TIME',
   PRIORITY             double(10,0) not null comment 'PRIORITY',
   STATE                varchar(16) not null comment 'STATE',
   JOB_NAME             varchar(200) comment 'JOB_NAME',
   JOB_GROUP            varchar(200) comment 'JOB_GROUP',
   IS_NONCONCURRENT     varchar(1) comment 'IS_NONCONCURRENT',
   REQUESTS_RECOVERY    varchar(1) comment 'REQUESTS_RECOVERY',
   primary key (SCHED_NAME, ENTRY_ID)
);

alter table QRTZ_FIRED_TRIGGERS comment '存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息';

/*==============================================================*/
/* Table: QRTZ_JOB_DETAILS                                      */
/*==============================================================*/
create table QRTZ_JOB_DETAILS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   JOB_NAME             varchar(60) not null comment '任务名称',
   JOB_GROUP            varchar(60) not null comment '任务组名称',
   DESCRIPTION          varchar(250) comment '描述',
   JOB_CLASS_NAME       varchar(250) not null comment 'JOB的类名',
   IS_DURABLE           char(1) not null comment 'IS_DURABLE',
   IS_NONCONCURRENT     char(1) not null comment 'IS_NONCONCURRENT',
   IS_UPDATE_DATA       char(1) not null comment '是否更新数据',
   REQUESTS_RECOVERY    char(1) not null comment '可恢复标记',
   JOB_DATA             longblob comment 'JOB_DATA',
   primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

alter table QRTZ_JOB_DETAILS comment '存储每一个已配置的 Job 的详细信息';

/*==============================================================*/
/* Table: QRTZ_LOCKS                                            */
/*==============================================================*/
create table QRTZ_LOCKS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   LOCK_NAME            varchar(40) not null comment '锁名',
   primary key (SCHED_NAME, LOCK_NAME)
);

alter table QRTZ_LOCKS comment '存储程序的非观锁的信息(假如使用了悲观锁)';

/*==============================================================*/
/* Table: QRTZ_PAUSED_TRIGGER_GRPS                              */
/*==============================================================*/
create table QRTZ_PAUSED_TRIGGER_GRPS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   primary key (SCHED_NAME, TRIGGER_GROUP)
);

alter table QRTZ_PAUSED_TRIGGER_GRPS comment '存储已暂停的 Trigger 组的信息';

/*==============================================================*/
/* Table: QRTZ_SCHEDULER_STATE                                  */
/*==============================================================*/
create table QRTZ_SCHEDULER_STATE
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   INSTANCE_NAME        varchar(60) not null comment '实例名称',
   LAST_CHECKIN_TIME    bigint(13) not null comment 'LAST_CHECKIN_TIME',
   CHECKIN_INTERVAL     bigint(13) not null comment 'CHECKIN_INTERVAL',
   primary key (SCHED_NAME, INSTANCE_NAME)
);

alter table QRTZ_SCHEDULER_STATE comment '存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)';

/*==============================================================*/
/* Table: QRTZ_SIMPLE_TRIGGERS                                  */
/*==============================================================*/
create table QRTZ_SIMPLE_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   REPEAT_COUNT         integer(7) not null comment 'REPEAT_COUNT',
   REPEAT_INTERVAL      bigint(12) not null comment 'REPEAT_INTERVAL',
   TIMES_TRIGGERED      bigint(10) not null comment 'TIMES_TRIGGERED',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_SIMPLE_TRIGGERS comment ' 存储简单的 Trigger，包括重复次数，间隔，以及已触的次数';

/*==============================================================*/
/* Table: QRTZ_SIMPROP_TRIGGERS                                 */
/*==============================================================*/
create table QRTZ_SIMPROP_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   STR_PROP_1           varchar(512) comment 'STR_PROP_1',
   STR_PROP_2           varchar(512) comment 'STR_PROP_2',
   STR_PROP_3           varchar(512) comment 'STR_PROP_3',
   INT_PROP_1           bigint(10) comment 'INT_PROP_1',
   INT_PROP_2           bigint(10) comment 'INT_PROP_2',
   LONG_PROP_1          bigint(10) comment 'LONG_PROP_1',
   LONG_PROP_2          bigint(10) comment 'LONG_PROP_2',
   DEC_PROP_1           double(13,4) comment 'DEC_PROP_1',
   DEC_PROP_2           double(13,4) comment 'DEC_PROP_2',
   BOOL_PROP_1          char(1) comment 'BOOL_PROP_1',
   BOOL_PROP_2          char(1) comment 'BOOL_PROP_2',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

/*==============================================================*/
/* Table: QRTZ_TRIGGERS                                         */
/*==============================================================*/
create table QRTZ_TRIGGERS
(
   SCHED_NAME           varchar(20) not null comment '调度名称',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   TRIGGER_GROUP        varchar(60) not null comment '触发器组',
   JOB_NAME             varchar(60) not null comment '任务名称',
   JOB_GROUP            varchar(60) not null comment '任务组',
   DESCRIPTION          varchar(250) comment '描述',
   NEXT_FIRE_TIME       bigint(13) comment 'NEXT_FIRE_TIME',
   PREV_FIRE_TIME       bigint(13) comment 'PREV_FIRE_TIME',
   PRIORITY             bigint(10) comment 'PRIORITY',
   TRIGGER_STATE        varchar(16) not null comment 'TRIGGER_STATE',
   TRIGGER_TYPE         varchar(8) not null comment 'TRIGGER_TYPE',
   START_TIME           bigint(13) not null comment 'START_TIME',
   END_TIME             bigint(10) comment 'END_TIME',
   CALENDAR_NAME        varchar(200) comment 'CALENDAR_NAME',
   MISFIRE_INSTR        smallint comment 'MISFIRE_INSTR',
   JOB_DATA             longblob comment 'JOB_DATA',
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_TRIGGERS comment '存储已配置的 Trigger 的信息';

/*==============================================================*/
/* Table: t_job_cron_trigger                                    */
/*==============================================================*/
create table t_job_cron_trigger
(
   TRIGGER_ID           integer(8) not null auto_increment comment '触发器标识',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   CRON_EXPRESSION      varchar(120) not null comment 'CRON表达式',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   primary key (TRIGGER_ID)
);

alter table t_job_cron_trigger comment 'CRON表达式触发器';

/*==============================================================*/
/* Table: t_job_cron_trigger_history                            */
/*==============================================================*/
create table t_job_cron_trigger_history
(
   TRIGGER_ID           integer(8) not null comment '触发器标识',
   SEQ                  integer(4) not null comment '序列号',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   CRON_EXPRESSION      varchar(120) not null comment 'CRON表达式',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   UPDATE_TIME          datetime not null comment '修改时间',
   UPDATE_OPERATOR_ID   integer(8) comment '修改人标识',
   primary key (TRIGGER_ID, SEQ)
);

alter table t_job_cron_trigger_history comment 'CRON表达式触发器历史表';

/*==============================================================*/
/* Table: t_job_simple_trigger                                  */
/*==============================================================*/
create table t_job_simple_trigger
(
   TRIGGER_ID           integer(8) not null auto_increment comment '触发器标识',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   BEGIN_TIME           datetime not null comment '开始时间',
   END_TIME             datetime comment '结束时间',
   TIMES                integer(4) comment '执行次数',
   EXECUTE_INTERVAL     integer(4) not null comment '执行间隔',
   INTERVAL_UNIT        char(1) not null comment '间隔单位',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(4) comment '创建人标识',
   primary key (TRIGGER_ID)
);

alter table t_job_simple_trigger comment '简单触发器';

/*==============================================================*/
/* Table: t_job_simple_trigger_history                          */
/*==============================================================*/
create table t_job_simple_trigger_history
(
   TRIGGER_ID           integer(8) not null comment '触发器标识',
   SEQ                  integer(4) not null comment '序列号',
   TRIGGER_NAME         varchar(60) not null comment '触发器名称',
   BEGIN_TIME           datetime not null comment '开始时间',
   END_TIME             datetime comment '结束时间',
   TIMES                integer(4) comment '执行次数',
   EXECUTE_INTERVAL     integer(4) not null comment '执行间隔',
   INTERVAL_UNIT        char(1) not null comment '间隔单位',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   UPDATE_TIME          datetime not null comment '修改时间',
   UPDATE_OPERATOR_ID   integer(8) comment '修改人标识',
   primary key (TRIGGER_ID, SEQ)
);

alter table t_job_simple_trigger_history comment '简单触发器历史表';

/*==============================================================*/
/* Table: t_job_task                                            */
/*==============================================================*/
create table t_job_task
(
   TASK_ID              integer(8) not null auto_increment comment '任务标识',
   MODULE_CODE          varchar(10) comment '业务模块代码',
   TASK_NAME            varchar(60) not null comment '任务名称',
   CLASS_NAME           varchar(60) not null comment '执行类名',
   METHOD               varchar(20) not null comment '方法名',
   PRIORITY             smallint(1) not null comment '优先级',
   IS_CONCURRENT        char(1) comment '是否并发',
   TASK_STATE           char(1) not null comment '任务状态',
   LAST_EXECUTE_TIME    datetime comment '上次执行时间',
   NEXT_EXCUTE_DATE     datetime comment '下次执行时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   CREATE_TIME          datetime not null comment '创建时间',
   primary key (TASK_ID),
   key AK_Key_2 (TASK_NAME)
);

alter table t_job_task comment '任务表';

/*==============================================================*/
/* Table: t_job_task_history                                    */
/*==============================================================*/
create table t_job_task_history
(
   TASK_ID              integer(8) not null comment '任务标识',
   SEQ                  integer(8) not null comment '序列号',
   TASK_NAME            varchar(60) not null comment '任务名称',
   CLASS_NAME           varchar(60) not null comment '执行类名',
   METHOD               varchar(20) not null comment '方法名',
   MODULE_CODE          varchar(10) not null comment '业务模块代码',
   PRIORITY             smallint(1) not null comment '优先级',
   IS_CONCURRENT        char(1) comment '是否并发',
   TASK_STATE           char(1) not null comment '任务状态',
   LAST_EXECUTE_TIME    datetime comment '上次执行时间',
   NEXT_EXCUTE_DATE     datetime comment '下次执行时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   CREATE_TIME          datetime not null comment '创建时间',
   UPDATE_TIME          datetime not null comment '修改时间',
   UPDATE_OPERATOR_ID   integer(8) comment '修改人标识',
   primary key (TASK_ID, SEQ)
);

alter table t_job_task_history comment '任务表历史记录表';

/*==============================================================*/
/* Table: t_job_task_trigger                                    */
/*==============================================================*/
create table t_job_task_trigger
(
   TASK_ID              integer(8) not null comment '任务标识',
   TRIGGER_TYPE         char(1) not null comment '触发器类型',
   TRIGGER_ID           integer(8) not null comment '触发器标识',
   primary key (TASK_ID, TRIGGER_TYPE, TRIGGER_ID)
);

alter table t_job_task_trigger comment '任务触发器表';

/*==============================================================*/
/* Table: t_manager_account                                     */
/*==============================================================*/
create table t_manager_account
(
   ACCOUNT_ID           integer(8) not null auto_increment comment '账号标识',
   ACCOUNT_VALUE        varchar(120) not null comment '账号值',
   ACCOUNT_TYPE         varchar(1) not null comment '账号类型: P-平台新增账号；M-手机号；E-邮箱',
   OPERATOR_ID          integer(8) not null comment '操作员标识',
   CREATE_TIME          datetime not null comment '创建时间',
   STATE                char(1) not null comment '状态：A-可用；X-注销；',
   STATE_DATE           datetime not null comment '状态更新时间',
   EXT1                 varchar(120) comment '扩展属性1',
   EXT2                 varchar(120) comment '扩展属性2',
   primary key (ACCOUNT_ID)
);

alter table t_manager_account comment '账号表';

INSERT INTO t_manager_account (ACCOUNT_ID, ACCOUNT_VALUE, ACCOUNT_TYPE, OPERATOR_ID, CREATE_TIME, STATE, STATE_DATE) VALUES (1, 'admin', 'P', 1, CURRENT_TIMESTAMP, 'A', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_admin                                       */
/*==============================================================*/
create table t_manager_admin
(
   ADMIN_ID             integer(6) not null auto_increment comment '管理员标识',
   ADMIN_NAME           varchar(60) not null comment '管理员名称',
   OPERATOR_ID          integer(8) not null comment '操作员标识',
   HEAD_IMG             bigint(10),
   GENER                char(1) comment 'M-男；F-女；O- 保密(other)；',
   EMAIL                varchar(120) comment '电子邮件',
   PHONE                varchar(20) comment '电话',
   ADDRESS              varchar(255) comment '地址',
   CREATE_TIME          datetime not null comment '创建日期',
   STATE                char(1) not null comment '状态：A-可用；X-删除；',
   STATE_DATE           datetime not null comment '状态日期',
   primary key (ADMIN_ID)
);

alter table t_manager_admin comment '管理员';

INSERT INTO t_manager_admin (ADMIN_ID, ADMIN_NAME, OPERATOR_ID, HEAD_IMG, GENER, EMAIL, PHONE, ADDRESS, CREATE_TIME, STATE, STATE_DATE) VALUES (1, '最高管理员', 1, 0, 'O', 'admin@qq.com', '13088888888', '中国', CURRENT_TIMESTAMP, 'A', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_admin_attr                                  */
/*==============================================================*/
create table t_manager_admin_attr
(
   ADMIN_ID             integer(8) not null comment '管理员标识',
   ATTR_ID              integer(4) not null comment '属性标识',
   VALUE                varchar(120) comment '属性值',
   CREATE_TIME          datetime not null comment '创建时间',
   primary key (ADMIN_ID, ATTR_ID)
);

alter table t_manager_admin_attr comment '管理员属性表';

/*==============================================================*/
/* Table: t_manager_admin_attr_history                          */
/*==============================================================*/
create table t_manager_admin_attr_history
(
   ADMIN_ID             integer(8) not null comment '管理员标识',
   ATTR_ID              integer(4) not null comment '属性标识',
   SEQ                  integer(4) not null comment '序列',
   VALUE                varchar(120) comment '属性值',
   CREATE_TIME          datetime not null comment '创建时间',
   UPDATE_TIME          datetime not null comment '修改时间',
   UPDATE_OPRATOR_ID    integer(8) comment '修改人标识',
   primary key (ADMIN_ID, ATTR_ID, SEQ)
);

alter table t_manager_admin_attr_history comment '管理员属性历史表';

/*==============================================================*/
/* Table: t_manager_admin_history                               */
/*==============================================================*/
create table t_manager_admin_history
(
   ADMIN_ID             integer(6) not null auto_increment comment '管理员标识',
   ADMIN_NAME           varchar(60) not null comment '管理员名称',
   OPERATOR_ID          integer(8) not null comment '操作员标识',
   HEAD_IMG             bigint(10),
   GENER                char(1) comment 'M-男；F-女；O- 保密(other)；',
   EMAIL                varchar(120) comment '电子邮件',
   PHONE                varchar(20) comment '电话',
   ADDRESS              varchar(255) comment '地址',
   CREATED_TIME         datetime not null comment '创建日期',
   STATE                char(1) not null comment '状态：A-可用；X-删除；U-修改；',
   STATE_DATE           datetime not null comment '状态日期',
   UPDATE_TIME          datetime not null comment '修改时间',
   UPDATE_OPERATOR_ID   integer(8) comment '修改人标识',
   SEQ                  integer(4) not null comment '序列',
   primary key (ADMIN_ID, SEQ)
);

alter table t_manager_admin_history comment '管理员历史表';

/*==============================================================*/
/* Table: t_manager_button                                      */
/*==============================================================*/
create table t_manager_button
(
   BUTTON_ID            integer(6) not null,
   RESOURCE_ID          integer(6) comment '菜单标识',
   BUTTON_NAME          varchar(120) not null,
   RESOURCE_CODE        varchar(60) not null,
   primary key (BUTTON_ID)
);

alter table t_manager_button comment '菜单按钮表';

/*==============================================================*/
/* Table: t_manager_duty                                        */
/*==============================================================*/
create table t_manager_duty
(
   DUTY_ID              integer(8) not null auto_increment comment '岗位标识',
   DUTY_NAME            varchar(60) not null comment '岗位名称',
   ORG_ID               integer(6) comment '组织标识',
   CREATE_TIME          datetime not null comment '创建时间',
   EXT                  varchar(120),
   OPERATOR_ID          integer(8) comment '创建人标识',
   STATE                char(1) not null comment '状态：A-可用；X-删除；',
   STATE_DATE           datetime not null comment '状态日期',
   primary key (DUTY_ID)
);

alter table t_manager_duty comment '岗位';

INSERT INTO t_manager_duty (DUTY_ID, DUTY_NAME, ORG_ID, CREATE_TIME, STATE, STATE_DATE) VALUES (1, '管理员岗位', 1, CURRENT_TIMESTAMP, 'A', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_duty_role                                   */
/*==============================================================*/
create table t_manager_duty_role
(
   DUTY_ID              integer(8) not null comment '岗位标识',
   ROLE_ID              integer(6) not null comment '角色标识',
   primary key (ROLE_ID, DUTY_ID)
);

alter table t_manager_duty_role comment '岗位角色表';

INSERT INTO t_manager_duty_role (DUTY_ID, ROLE_ID) VALUES (1, 1);

/*==============================================================*/
/* Table: t_manager_menu                                        */
/*==============================================================*/
create table t_manager_menu
(
   RESOURCE_ID          integer(6) not null auto_increment comment '菜单标识',
   RESOURCE_CODE        VARCHAR(120) not null,
   PARENT_RESOURCE_ID   integer(4) comment '父菜单标识',
   MENU_NAME            varchar(20) not null comment '菜单名称',
   MODULE_CODE          varchar(10) comment '业务模块代码',
   SEQ                  integer(4) comment '序列',
   URL                  varchar(120) comment '访问地址',
   IS_LEAF              char(1) not null comment '是否为叶节点',
   ICON_URL             varchar(120) comment '图标URL',
   primary key (RESOURCE_ID)
);

alter table t_manager_menu comment '菜单表';

/*==============================================================*/
/* Table: t_manager_operator                                    */
/*==============================================================*/
create table t_manager_operator
(
   OPERATOR_ID          integer(8) not null auto_increment comment '操作员标识',
   OPERATOR_TYPE        char(1) not null comment '操作员类型：A-管理员；M-会员',
   OPERATOR_CODE        integer(8) comment '操作员代码',
   DUTY_ID              integer(8) not null comment '岗位标识',
   USER_NAME            varchar(60) comment '登录名称',
   PASSWORD             varchar(60) comment '登录密码',
   IS_LOCKED            char(1) not null comment '是否锁定：Y-锁定；N-正常',
   PWD_EXP_DATE         datetime not null comment '密码过期时间',
   CREATE_DATE          datetime not null comment '创建时间',
   REGIST_IP            varchar(16) comment '注册IP',
   LAST_IP              varchar(16) comment '最后访问IP',
   LAST_LOGIN_DATE      datetime not null comment '最后登录时间',
   LOGIN_FAIL           integer(4) comment '登录失败次数',
   STATE                char(1) not null comment '状态：A-可用；X-删除；',
   STATE_DATE           datetime not null comment '状态日期',
   primary key (OPERATOR_ID)
);

alter table t_manager_operator comment '操作员表';

INSERT INTO t_manager_operator (OPERATOR_ID, OPERATOR_TYPE, DUTY_ID, PASSWORD, IS_LOCKED, PWD_EXP_DATE, CREATE_DATE, LAST_LOGIN_DATE, STATE, STATE_DATE) VALUES (1, 'A', 1, 'C4CA4238A0B923820DCC509A6F75849B', 'N', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'A', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_org                                         */
/*==============================================================*/
create table t_manager_org
(
   ORG_ID               integer(6) not null auto_increment comment '组织标识',
   ORG_NAME             varchar(60) not null comment '组织名称',
   ORG_CODE             varchar(10) comment '组织代码',
   PARENT_ORG_ID        integer(6) comment '父组织标识',
   OWNER_AREA           varchar(16) comment '归属区域',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(8) comment '创建人标识',
   EXT                  varchar(120) comment '扩展字段',
   STATE                char(1) not null comment '状态：A-可用；X-删除；',
   STATE_DATE           datetime not null comment '状态日期',
   primary key (ORG_ID)
);

alter table t_manager_org comment '组织';

INSERT INTO t_manager_org (ORG_ID, ORG_NAME, ORG_CODE, PARENT_ORG_ID, CREATE_TIME, STATE, STATE_DATE) VALUES (1, 'DEFAULT', 'DEFAULT', NULL, CURRENT_TIMESTAMP,'A', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_role                                        */
/*==============================================================*/
create table t_manager_role
(
   ROLE_ID              integer(6) not null auto_increment comment '角色标识',
   MODULE_CODE          varchar(10) comment '业务模块代码',
   ROLE_NAME            varchar(60) not null comment '角色名称',
   CREATE_TIME          datetime not null comment '创建时间',
   primary key (ROLE_ID)
);

alter table t_manager_role comment '角色';

INSERT INTO t_manager_role (ROLE_ID, MODULE_CODE, ROLE_NAME, CREATE_TIME) VALUES (1, 'MANAGER', '最高管理员角色', CURRENT_TIMESTAMP);

/*==============================================================*/
/* Table: t_manager_role_history                                */
/*==============================================================*/
create table t_manager_role_history
(
   ROLE_ID              integer(6) not null auto_increment comment '角色标识',
   SEQ                  integer(4) not null,
   MODULE_CODE          varchar(10) comment '业务模块代码',
   ROLE_NAME            varchar(60) not null comment '角色名称',
   CREATE_TIME          datetime not null comment '创建时间',
   HIS_OPERATOR_ID      integer(8),
   HIS_CREATE_TIME      datetime not null,
   RESOURCES            text,
   primary key (ROLE_ID, SEQ)
);

alter table t_manager_role_history comment '角色历史';

/*==============================================================*/
/* Table: t_manager_role_resource                               */
/*==============================================================*/
create table t_manager_role_resource
(
   ROLE_ID              integer(6) not null comment '角色标识',
   RESOURCE_ID          integer(6) not null comment '资源标识',
   RESOURCE_TYPE        char(2) not null comment '01-菜单；02-组织',
   RESOURCE_CODE        varchar(60) not null,
   primary key (ROLE_ID, RESOURCE_ID)
);

alter table t_manager_role_resource comment '角色资源表';

/*==============================================================*/
/* Table: t_msg_message_attachment                              */
/*==============================================================*/
create table t_msg_message_attachment
(
   ATTACHMENTS_ID       integer(6) not null comment '附件标识',
   MESSAGE_ID           integer(6) not null comment '消息标识',
   primary key (ATTACHMENTS_ID, MESSAGE_ID)
);

alter table t_msg_message_attachment comment '消息附件表';

/*==============================================================*/
/* Table: t_msg_message_box                                     */
/*==============================================================*/
create table t_msg_message_box
(
   MESSAGE_ID           integer(6) not null auto_increment comment '消息标识',
   RECEIVERS            text not null comment '收件人',
   SENDER               varchar(120) comment '发件人',
   MESSAGE_TEMPLATE_ID  integer(4) not null comment '消息模板标识',
   SUBJECT              varchar(120) comment '标题',
   CONTENT              text comment '内容',
   ATTACHMENTS_NUM      smallint(3) not null comment '附件数量',
   CREATE_TIME          datetime not null comment '创建时间',
   SEND_TIME            datetime comment '最后一次发送时间',
   NEXT_SEND_TIME       datetime comment '下一次发送时间',
   SEND_TIMES           integer(4) not null comment '发送次数',
   EXTEND_ATTRS         text comment '扩展参数',
   primary key (MESSAGE_ID)
);

alter table t_msg_message_box comment '发件箱';

/*==============================================================*/
/* Table: t_msg_message_history                                 */
/*==============================================================*/
create table t_msg_message_history
(
   MESSAGE_ID           bigint(10) not null auto_increment comment '消息标识',
   RECEIVERS            text not null comment '收件人',
   SENDER               varchar(120) comment '发件人',
   MESSAGE_TYPE         char(1) not null comment '消息类型',
   MESSAGE_TEMPLATE_ID  integer(4) not null comment '消息模板标识',
   SUBJECT              varchar(120) comment '标题',
   CONTENT              text comment '内容',
   ATTACHMENTS_NUM      integer(4) not null comment '附件数量',
   CREATE_TIME          datetime not null comment '创建时间',
   SEND_TIME            datetime comment '最后一次发送时间',
   SEND_TIMES           integer(4) not null comment '发送次数',
   RESULT               varchar(255) not null comment '发送结果',
   EXP_DATE             datetime comment '失效时间',
   EXTEND_ATTRS         text comment '扩展参数',
   primary key (MESSAGE_ID)
);

alter table t_msg_message_history comment '消息记录历史表';

/*==============================================================*/
/* Table: t_msg_message_template                                */
/*==============================================================*/
create table t_msg_message_template
(
   MESSAGE_TEMPLATE_ID  integer(6) not null auto_increment comment '消息模板标识',
   MESSAGE_TEMPLATE_CODE varchar(20) not null comment '消息模板代码',
   NAME                 varchar(120) not null comment '名称',
   TEMPLATE             text comment '模板',
   STATE                char(1) not null comment '状态',
   CONTACT_CHANNEL_IDS  varchar(8) comment '接触渠到',
   STATE_TIME           datetime not null comment '状态时间',
   DELAY                integer(6) not null comment '延迟时间(秒)',
   RESEND_TIMES         integer(4) not null comment '失败重发次数',
   SAVE_HISTORY         char(1) not null comment '是否保留历史记录',
   SAVE_DAY             integer(4) not null comment '保留天数',
   CREATE_TIME          datetime comment '创建时间',
   primary key (MESSAGE_TEMPLATE_ID)
);

alter table t_msg_message_template comment '消息模板';

/*==============================================================*/
/* Table: t_msg_send_record                                     */
/*==============================================================*/
create table t_msg_send_record
(
   SEND_RECORD_ID       bigint(10) not null auto_increment comment '发送记录标识',
   MESSAGE_ID           bigint(10) not null comment '消息标识',
   CONTACT_CHANNEL_ID   smallint(2) not null comment '接触渠到',
   SEND_TIME            datetime not null comment '发送时间',
   RESULT               varchar(255) not null comment '发送结果',
   primary key (SEND_RECORD_ID)
);

alter table t_msg_send_record comment '发送记录表';

/*==============================================================*/
/* Table: t_sys_area                                            */
/*==============================================================*/
create table t_sys_area
(
   AREA_ID              bigint(10) not null auto_increment comment '区域标识',
   PARENT_AREA_ID       bigint(10) comment '父区域标识',
   country_id           smallint(3) not null,
   AREA_TYPE            char(1) comment '区域类型',
   AREA_NAME            varchar(20) comment '区域名称',
   AREA_CODE            varchar(20) comment '区域编码',
   SHORT_NAME           varchar(20) comment '简称',
   ZONE_CODE            varchar(10) comment '区号',
   ZIP_CODE             varchar(10) comment '邮编',
   PINYIN               varchar(60) comment '拼音',
   REMARK               varchar(120) comment '备注',
   primary key (AREA_ID)
);

alter table t_sys_area comment '区域';

INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (100000,null,'0','中国','','','','','',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110000,100000,'1','北京','','北京','','','Beijing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110100,110000,'2','北京市','','北京','010','100000','Beijing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110101,110100,'3','东城区','','东城','010','100010','Dongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110102,110100,'3','西城区','','西城','010','100032','Xicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110105,110100,'3','朝阳区','','朝阳','010','100020','Chaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110106,110100,'3','丰台区','','丰台','010','100071','Fengtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110107,110100,'3','石景山区','','石景山','010','100043','Shijingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110108,110100,'3','海淀区','','海淀','010','100089','Haidian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110109,110100,'3','门头沟区','','门头沟','010','102300','Mentougou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110111,110100,'3','房山区','','房山','010','102488','Fangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110112,110100,'3','通州区','','通州','010','101149','Tongzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110113,110100,'3','顺义区','','顺义','010','101300','Shunyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110114,110100,'3','昌平区','','昌平','010','102200','Changping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110115,110100,'3','大兴区','','大兴','010','102600','Daxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110116,110100,'3','怀柔区','','怀柔','010','101400','Huairou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110117,110100,'3','平谷区','','平谷','010','101200','Pinggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110228,110100,'3','密云县','','密云','010','101500','Miyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (110229,110100,'3','延庆县','','延庆','010','102100','Yanqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120000,100000,'1','天津','','天津','','','Tianjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120100,120000,'2','天津市','','天津','022','300000','Tianjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120101,120100,'3','和平区','','和平','022','300041','Heping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120102,120100,'3','河东区','','河东','022','300171','Hedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120103,120100,'3','河西区','','河西','022','300202','Hexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120104,120100,'3','南开区','','南开','022','300110','Nankai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120105,120100,'3','河北区','','河北','022','300143','Hebei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120106,120100,'3','红桥区','','红桥','022','300131','Hongqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120110,120100,'3','东丽区','','东丽','022','300300','Dongli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120111,120100,'3','西青区','','西青','022','300380','Xiqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120112,120100,'3','津南区','','津南','022','300350','Jinnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120113,120100,'3','北辰区','','北辰','022','300400','Beichen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120114,120100,'3','武清区','','武清','022','301700','Wuqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120115,120100,'3','宝坻区','','宝坻','022','301800','Baodi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120116,120100,'3','滨海新区','','滨海新区','022','300451','Binhaixinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120221,120100,'3','宁河县','','宁河','022','301500','Ninghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120223,120100,'3','静海县','','静海','022','301600','Jinghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (120225,120100,'3','蓟县','','蓟县','022','301900','Jixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130000,100000,'1','河北省','','河北','','','Hebei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130100,130000,'2','石家庄市','','石家庄','0311','050011','Shijiazhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130102,130100,'3','长安区','','长安','0311','050011','Chang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130104,130100,'3','桥西区','','桥西','0311','050091','Qiaoxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130105,130100,'3','新华区','','新华','0311','050051','Xinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130107,130100,'3','井陉矿区','','井陉矿区','0311','050100','Jingxingkuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130108,130100,'3','裕华区','','裕华','0311','050031','Yuhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130109,130100,'3','藁城区','','藁城','0311','052160','Gaocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130110,130100,'3','鹿泉区','','鹿泉','0311','050200','Luquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130111,130100,'3','栾城区','','栾城','0311','051430','Luancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130121,130100,'3','井陉县','','井陉','0311','050300','Jingxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130123,130100,'3','正定县','','正定','0311','050800','Zhengding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130125,130100,'3','行唐县','','行唐','0311','050600','Xingtang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130126,130100,'3','灵寿县','','灵寿','0311','050500','Lingshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130127,130100,'3','高邑县','','高邑','0311','051330','Gaoyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130128,130100,'3','深泽县','','深泽','0311','052560','Shenze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130129,130100,'3','赞皇县','','赞皇','0311','051230','Zanhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130130,130100,'3','无极县','','无极','0311','052460','Wuji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130131,130100,'3','平山县','','平山','0311','050400','Pingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130132,130100,'3','元氏县','','元氏','0311','051130','Yuanshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130133,130100,'3','赵县','','赵县','0311','051530','Zhaoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130181,130100,'3','辛集市','','辛集','0311','052360','Xinji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130183,130100,'3','晋州市','','晋州','0311','052260','Jinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130184,130100,'3','新乐市','','新乐','0311','050700','Xinle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130200,130000,'2','唐山市','','唐山','0315','063000','Tangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130202,130200,'3','路南区','','路南','0315','063000','Lunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130203,130200,'3','路北区','','路北','0315','063000','Lubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130204,130200,'3','古冶区','','古冶','0315','063100','Guye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130205,130200,'3','开平区','','开平','0315','063021','Kaiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130207,130200,'3','丰南区','','丰南','0315','063300','Fengnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130208,130200,'3','丰润区','','丰润','0315','064000','Fengrun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130209,130200,'3','曹妃甸区','','曹妃甸','0315','063200','Caofeidian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130223,130200,'3','滦县','','滦县','0315','063700','Luanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130224,130200,'3','滦南县','','滦南','0315','063500','Luannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130225,130200,'3','乐亭县','','乐亭','0315','063600','Laoting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130227,130200,'3','迁西县','','迁西','0315','064300','Qianxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130229,130200,'3','玉田县','','玉田','0315','064100','Yutian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130281,130200,'3','遵化市','','遵化','0315','064200','Zunhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130283,130200,'3','迁安市','','迁安','0315','064400','Qian an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130300,130000,'2','秦皇岛市','','秦皇岛','0335','066000','Qinhuangdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130302,130300,'3','海港区','','海港','0335','066000','Haigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130303,130300,'3','山海关区','','山海关','0335','066200','Shanhaiguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130304,130300,'3','北戴河区','','北戴河','0335','066100','Beidaihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130321,130300,'3','青龙满族自治县','','青龙','0335','066500','Qinglong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130322,130300,'3','昌黎县','','昌黎','0335','066600','Changli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130323,130300,'3','抚宁县','','抚宁','0335','066300','Funing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130324,130300,'3','卢龙县','','卢龙','0335','066400','Lulong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130400,130000,'2','邯郸市','','邯郸','0310','056002','Handan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130402,130400,'3','邯山区','','邯山','0310','056001','Hanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130403,130400,'3','丛台区','','丛台','0310','056002','Congtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130404,130400,'3','复兴区','','复兴','0310','056003','Fuxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130406,130400,'3','峰峰矿区','','峰峰矿区','0310','056200','Fengfengkuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130421,130400,'3','邯郸县','','邯郸','0310','056101','Handan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130423,130400,'3','临漳县','','临漳','0310','056600','Linzhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130424,130400,'3','成安县','','成安','0310','056700','Cheng an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130425,130400,'3','大名县','','大名','0310','056900','Daming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130426,130400,'3','涉县','','涉县','0310','056400','Shexian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130427,130400,'3','磁县','','磁县','0310','056500','Cixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130428,130400,'3','肥乡县','','肥乡','0310','057550','Feixiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130429,130400,'3','永年县','','永年','0310','057150','Yongnian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130430,130400,'3','邱县','','邱县','0310','057450','Qiuxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130431,130400,'3','鸡泽县','','鸡泽','0310','057350','Jize',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130432,130400,'3','广平县','','广平','0310','057650','Guangping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130433,130400,'3','馆陶县','','馆陶','0310','057750','Guantao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130434,130400,'3','魏县','','魏县','0310','056800','Weixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130435,130400,'3','曲周县','','曲周','0310','057250','Quzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130481,130400,'3','武安市','','武安','0310','056300','Wu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130500,130000,'2','邢台市','','邢台','0319','054001','Xingtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130502,130500,'3','桥东区','','桥东','0319','054001','Qiaodong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130503,130500,'3','桥西区','','桥西','0319','054000','Qiaoxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130521,130500,'3','邢台县','','邢台','0319','054001','Xingtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130522,130500,'3','临城县','','临城','0319','054300','Lincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130523,130500,'3','内丘县','','内丘','0319','054200','Neiqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130524,130500,'3','柏乡县','','柏乡','0319','055450','Baixiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130525,130500,'3','隆尧县','','隆尧','0319','055350','Longyao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130526,130500,'3','任县','','任县','0319','055150','Renxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130527,130500,'3','南和县','','南和','0319','054400','Nanhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130528,130500,'3','宁晋县','','宁晋','0319','055550','Ningjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130529,130500,'3','巨鹿县','','巨鹿','0319','055250','Julu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130530,130500,'3','新河县','','新河','0319','055650','Xinhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130531,130500,'3','广宗县','','广宗','0319','054600','Guangzong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130532,130500,'3','平乡县','','平乡','0319','054500','Pingxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130533,130500,'3','威县','','威县','0319','054700','Weixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130534,130500,'3','清河县','','清河','0319','054800','Qinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130535,130500,'3','临西县','','临西','0319','054900','Linxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130581,130500,'3','南宫市','','南宫','0319','055750','Nangong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130582,130500,'3','沙河市','','沙河','0319','054100','Shahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130600,130000,'2','保定市','','保定','0312','071052','Baoding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130602,130600,'3','新市区','','新市','0312','071051','Xinshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130603,130600,'3','北市区','','北市','0312','071000','Beishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130604,130600,'3','南市区','','南市','0312','071001','Nanshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130621,130600,'3','满城县','','满城','0312','072150','Mancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130622,130600,'3','清苑县','','清苑','0312','071100','Qingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130623,130600,'3','涞水县','','涞水','0312','074100','Laishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130624,130600,'3','阜平县','','阜平','0312','073200','Fuping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130625,130600,'3','徐水县','','徐水','0312','072550','Xushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130626,130600,'3','定兴县','','定兴','0312','072650','Dingxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130627,130600,'3','唐县','','唐县','0312','072350','Tangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130628,130600,'3','高阳县','','高阳','0312','071500','Gaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130629,130600,'3','容城县','','容城','0312','071700','Rongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130630,130600,'3','涞源县','','涞源','0312','074300','Laiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130631,130600,'3','望都县','','望都','0312','072450','Wangdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130632,130600,'3','安新县','','安新','0312','071600','Anxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130633,130600,'3','易县','','易县','0312','074200','Yixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130634,130600,'3','曲阳县','','曲阳','0312','073100','Quyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130635,130600,'3','蠡县','','蠡县','0312','071400','Lixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130636,130600,'3','顺平县','','顺平','0312','072250','Shunping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130637,130600,'3','博野县','','博野','0312','071300','Boye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130638,130600,'3','雄县','','雄县','0312','071800','Xiongxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130681,130600,'3','涿州市','','涿州','0312','072750','Zhuozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130682,130600,'3','定州市','','定州','0312','073000','Dingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130683,130600,'3','安国市','','安国','0312','071200','Anguo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130684,130600,'3','高碑店市','','高碑店','0312','074000','Gaobeidian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130700,130000,'2','张家口市','','张家口','0313','075000','Zhangjiakou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130702,130700,'3','桥东区','','桥东','0313','075000','Qiaodong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130703,130700,'3','桥西区','','桥西','0313','075061','Qiaoxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130705,130700,'3','宣化区','','宣化','0313','075100','Xuanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130706,130700,'3','下花园区','','下花园','0313','075300','Xiahuayuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130721,130700,'3','宣化县','','宣化','0313','075100','Xuanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130722,130700,'3','张北县','','张北','0313','076450','Zhangbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130723,130700,'3','康保县','','康保','0313','076650','Kangbao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130724,130700,'3','沽源县','','沽源','0313','076550','Guyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130725,130700,'3','尚义县','','尚义','0313','076750','Shangyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130726,130700,'3','蔚县','','蔚县','0313','075700','Yuxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130727,130700,'3','阳原县','','阳原','0313','075800','Yangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130728,130700,'3','怀安县','','怀安','0313','076150','Huai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130729,130700,'3','万全县','','万全','0313','076250','Wanquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130730,130700,'3','怀来县','','怀来','0313','075400','Huailai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130731,130700,'3','涿鹿县','','涿鹿','0313','075600','Zhuolu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130732,130700,'3','赤城县','','赤城','0313','075500','Chicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130733,130700,'3','崇礼县','','崇礼','0313','076350','Chongli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130800,130000,'2','承德市','','承德','0314','067000','Chengde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130802,130800,'3','双桥区','','双桥','0314','067000','Shuangqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130803,130800,'3','双滦区','','双滦','0314','067001','Shuangluan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130804,130800,'3','鹰手营子矿区','','鹰手营子矿区','0314','067200','Yingshouyingzikuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130821,130800,'3','承德县','','承德','0314','067400','Chengde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130822,130800,'3','兴隆县','','兴隆','0314','067300','Xinglong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130823,130800,'3','平泉县','','平泉','0314','067500','Pingquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130824,130800,'3','滦平县','','滦平','0314','068250','Luanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130825,130800,'3','隆化县','','隆化','0314','068150','Longhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130826,130800,'3','丰宁满族自治县','','丰宁','0314','068350','Fengning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130827,130800,'3','宽城满族自治县','','宽城','0314','067600','Kuancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130828,130800,'3','围场满族蒙古族自治县','','围场','0314','068450','Weichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130900,130000,'2','沧州市','','沧州','0317','061001','Cangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130902,130900,'3','新华区','','新华','0317','061000','Xinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130903,130900,'3','运河区','','运河','0317','061001','Yunhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130921,130900,'3','沧县','','沧县','0317','061000','Cangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130922,130900,'3','青县','','青县','0317','062650','Qingxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130923,130900,'3','东光县','','东光','0317','061600','Dongguang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130924,130900,'3','海兴县','','海兴','0317','061200','Haixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130925,130900,'3','盐山县','','盐山','0317','061300','Yanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130926,130900,'3','肃宁县','','肃宁','0317','062350','Suning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130927,130900,'3','南皮县','','南皮','0317','061500','Nanpi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130928,130900,'3','吴桥县','','吴桥','0317','061800','Wuqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130929,130900,'3','献县','','献县','0317','062250','Xianxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130930,130900,'3','孟村回族自治县','','孟村','0317','061400','Mengcun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130981,130900,'3','泊头市','','泊头','0317','062150','Botou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130982,130900,'3','任丘市','','任丘','0317','062550','Renqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130983,130900,'3','黄骅市','','黄骅','0317','061100','Huanghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (130984,130900,'3','河间市','','河间','0317','062450','Hejian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131000,130000,'2','廊坊市','','廊坊','0316','065000','Langfang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131002,131000,'3','安次区','','安次','0316','065000','Anci',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131003,131000,'3','广阳区','','广阳','0316','065000','Guangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131022,131000,'3','固安县','','固安','0316','065500','Gu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131023,131000,'3','永清县','','永清','0316','065600','Yongqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131024,131000,'3','香河县','','香河','0316','065400','Xianghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131025,131000,'3','大城县','','大城','0316','065900','Daicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131026,131000,'3','文安县','','文安','0316','065800','Wen an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131028,131000,'3','大厂回族自治县','','大厂','0316','065300','Dachang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131081,131000,'3','霸州市','','霸州','0316','065700','Bazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131082,131000,'3','三河市','','三河','0316','065200','Sanhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131100,130000,'2','衡水市','','衡水','0318','053000','Hengshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131102,131100,'3','桃城区','','桃城','0318','053000','Taocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131121,131100,'3','枣强县','','枣强','0318','053100','Zaoqiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131122,131100,'3','武邑县','','武邑','0318','053400','Wuyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131123,131100,'3','武强县','','武强','0318','053300','Wuqiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131124,131100,'3','饶阳县','','饶阳','0318','053900','Raoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131125,131100,'3','安平县','','安平','0318','053600','Anping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131126,131100,'3','故城县','','故城','0318','053800','Gucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131127,131100,'3','景县','','景县','0318','053500','Jingxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131128,131100,'3','阜城县','','阜城','0318','053700','Fucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131181,131100,'3','冀州市','','冀州','0318','053200','Jizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (131182,131100,'3','深州市','','深州','0318','053800','Shenzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140000,100000,'1','山西省','','山西','','','Shanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140100,140000,'2','太原市','','太原','0351','030082','Taiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140105,140100,'3','小店区','','小店','0351','030032','Xiaodian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140106,140100,'3','迎泽区','','迎泽','0351','030002','Yingze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140107,140100,'3','杏花岭区','','杏花岭','0351','030009','Xinghualing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140108,140100,'3','尖草坪区','','尖草坪','0351','030023','Jiancaoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140109,140100,'3','万柏林区','','万柏林','0351','030024','Wanbailin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140110,140100,'3','晋源区','','晋源','0351','030025','Jinyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140121,140100,'3','清徐县','','清徐','0351','030400','Qingxu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140122,140100,'3','阳曲县','','阳曲','0351','030100','Yangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140123,140100,'3','娄烦县','','娄烦','0351','030300','Loufan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140181,140100,'3','古交市','','古交','0351','030200','Gujiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140200,140000,'2','大同市','','大同','0352','037008','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140202,140200,'3','城区','','城区','0352','037008','Chengqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140203,140200,'3','矿区','','矿区','0352','037003','Kuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140211,140200,'3','南郊区','','南郊','0352','037001','Nanjiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140212,140200,'3','新荣区','','新荣','0352','037002','Xinrong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140221,140200,'3','阳高县','','阳高','0352','038100','Yanggao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140222,140200,'3','天镇县','','天镇','0352','038200','Tianzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140223,140200,'3','广灵县','','广灵','0352','037500','Guangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140224,140200,'3','灵丘县','','灵丘','0352','034400','Lingqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140225,140200,'3','浑源县','','浑源','0352','037400','Hunyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140226,140200,'3','左云县','','左云','0352','037100','Zuoyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140227,140200,'3','大同县','','大同','0352','037300','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140300,140000,'2','阳泉市','','阳泉','0353','045000','Yangquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140302,140300,'3','城区','','城区','0353','045000','Chengqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140303,140300,'3','矿区','','矿区','0353','045000','Kuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140311,140300,'3','郊区','','郊区','0353','045011','Jiaoqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140321,140300,'3','平定县','','平定','0353','045200','Pingding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140322,140300,'3','盂县','','盂县','0353','045100','Yuxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140400,140000,'2','长治市','','长治','0355','046000','Changzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140402,140400,'3','城区','','城区','0355','046011','Chengqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140411,140400,'3','郊区','','郊区','0355','046011','Jiaoqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140421,140400,'3','长治县','','长治','0355','047100','Changzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140423,140400,'3','襄垣县','','襄垣','0355','046200','Xiangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140424,140400,'3','屯留县','','屯留','0355','046100','Tunliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140425,140400,'3','平顺县','','平顺','0355','047400','Pingshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140426,140400,'3','黎城县','','黎城','0355','047600','Licheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140427,140400,'3','壶关县','','壶关','0355','047300','Huguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140428,140400,'3','长子县','','长子','0355','046600','Zhangzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140429,140400,'3','武乡县','','武乡','0355','046300','Wuxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140430,140400,'3','沁县','','沁县','0355','046400','Qinxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140431,140400,'3','沁源县','','沁源','0355','046500','Qinyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140481,140400,'3','潞城市','','潞城','0355','047500','Lucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140500,140000,'2','晋城市','','晋城','0356','048000','Jincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140502,140500,'3','城区','','城区','0356','048000','Chengqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140521,140500,'3','沁水县','','沁水','0356','048200','Qinshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140522,140500,'3','阳城县','','阳城','0356','048100','Yangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140524,140500,'3','陵川县','','陵川','0356','048300','Lingchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140525,140500,'3','泽州县','','泽州','0356','048012','Zezhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140581,140500,'3','高平市','','高平','0356','048400','Gaoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140600,140000,'2','朔州市','','朔州','0349','038500','Shuozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140602,140600,'3','朔城区','','朔城','0349','036000','Shuocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140603,140600,'3','平鲁区','','平鲁','0349','038600','Pinglu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140621,140600,'3','山阴县','','山阴','0349','036900','Shanyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140622,140600,'3','应县','','应县','0349','037600','Yingxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140623,140600,'3','右玉县','','右玉','0349','037200','Youyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140624,140600,'3','怀仁县','','怀仁','0349','038300','Huairen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140700,140000,'2','晋中市','','晋中','0354','030600','Jinzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140702,140700,'3','榆次区','','榆次','0354','030600','Yuci',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140721,140700,'3','榆社县','','榆社','0354','031800','Yushe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140722,140700,'3','左权县','','左权','0354','032600','Zuoquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140723,140700,'3','和顺县','','和顺','0354','032700','Heshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140724,140700,'3','昔阳县','','昔阳','0354','045300','Xiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140725,140700,'3','寿阳县','','寿阳','0354','045400','Shouyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140726,140700,'3','太谷县','','太谷','0354','030800','Taigu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140727,140700,'3','祁县','','祁县','0354','030900','Qixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140728,140700,'3','平遥县','','平遥','0354','031100','Pingyao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140729,140700,'3','灵石县','','灵石','0354','031300','Lingshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140781,140700,'3','介休市','','介休','0354','032000','Jiexiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140800,140000,'2','运城市','','运城','0359','044000','Yuncheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140802,140800,'3','盐湖区','','盐湖','0359','044000','Yanhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140821,140800,'3','临猗县','','临猗','0359','044100','Linyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140822,140800,'3','万荣县','','万荣','0359','044200','Wanrong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140823,140800,'3','闻喜县','','闻喜','0359','043800','Wenxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140824,140800,'3','稷山县','','稷山','0359','043200','Jishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140825,140800,'3','新绛县','','新绛','0359','043100','Xinjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140826,140800,'3','绛县','','绛县','0359','043600','Jiangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140827,140800,'3','垣曲县','','垣曲','0359','043700','Yuanqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140828,140800,'3','夏县','','夏县','0359','044400','Xiaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140829,140800,'3','平陆县','','平陆','0359','044300','Pinglu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140830,140800,'3','芮城县','','芮城','0359','044600','Ruicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140881,140800,'3','永济市','','永济','0359','044500','Yongji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140882,140800,'3','河津市','','河津','0359','043300','Hejin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140900,140000,'2','忻州市','','忻州','0350','034000','Xinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140902,140900,'3','忻府区','','忻府','0350','034000','Xinfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140921,140900,'3','定襄县','','定襄','0350','035400','Dingxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140922,140900,'3','五台县','','五台','0350','035500','Wutai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140923,140900,'3','代县','','代县','0350','034200','Daixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140924,140900,'3','繁峙县','','繁峙','0350','034300','Fanshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140925,140900,'3','宁武县','','宁武','0350','036700','Ningwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140926,140900,'3','静乐县','','静乐','0350','035100','Jingle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140927,140900,'3','神池县','','神池','0350','036100','Shenchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140928,140900,'3','五寨县','','五寨','0350','036200','Wuzhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140929,140900,'3','岢岚县','','岢岚','0350','036300','Kelan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140930,140900,'3','河曲县','','河曲','0350','036500','Hequ',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140931,140900,'3','保德县','','保德','0350','036600','Baode',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140932,140900,'3','偏关县','','偏关','0350','036400','Pianguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (140981,140900,'3','原平市','','原平','0350','034100','Yuanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141000,140000,'2','临汾市','','临汾','0357','041000','Linfen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141002,141000,'3','尧都区','','尧都','0357','041000','Yaodu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141021,141000,'3','曲沃县','','曲沃','0357','043400','Quwo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141022,141000,'3','翼城县','','翼城','0357','043500','Yicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141023,141000,'3','襄汾县','','襄汾','0357','041500','Xiangfen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141024,141000,'3','洪洞县','','洪洞','0357','041600','Hongtong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141025,141000,'3','古县','','古县','0357','042400','Guxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141026,141000,'3','安泽县','','安泽','0357','042500','Anze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141027,141000,'3','浮山县','','浮山','0357','042600','Fushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141028,141000,'3','吉县','','吉县','0357','042200','Jixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141029,141000,'3','乡宁县','','乡宁','0357','042100','Xiangning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141030,141000,'3','大宁县','','大宁','0357','042300','Daning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141031,141000,'3','隰县','','隰县','0357','041300','Xixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141032,141000,'3','永和县','','永和','0357','041400','Yonghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141033,141000,'3','蒲县','','蒲县','0357','041200','Puxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141034,141000,'3','汾西县','','汾西','0357','031500','Fenxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141081,141000,'3','侯马市','','侯马','0357','043000','Houma',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141082,141000,'3','霍州市','','霍州','0357','031400','Huozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141100,140000,'2','吕梁市','','吕梁','0358','033000','Lvliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141102,141100,'3','离石区','','离石','0358','033000','Lishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141121,141100,'3','文水县','','文水','0358','032100','Wenshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141122,141100,'3','交城县','','交城','0358','030500','Jiaocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141123,141100,'3','兴县','','兴县','0358','033600','Xingxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141124,141100,'3','临县','','临县','0358','033200','Linxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141125,141100,'3','柳林县','','柳林','0358','033300','Liulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141126,141100,'3','石楼县','','石楼','0358','032500','Shilou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141127,141100,'3','岚县','','岚县','0358','033500','Lanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141128,141100,'3','方山县','','方山','0358','033100','Fangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141129,141100,'3','中阳县','','中阳','0358','033400','Zhongyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141130,141100,'3','交口县','','交口','0358','032400','Jiaokou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141181,141100,'3','孝义市','','孝义','0358','032300','Xiaoyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (141182,141100,'3','汾阳市','','汾阳','0358','032200','Fenyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150000,100000,'1','内蒙古自治区','','内蒙古','','','Inner Mongolia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150100,150000,'2','呼和浩特市','','呼和浩特','0471','010000','Hohhot',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150102,150100,'3','新城区','','新城','0471','010050','Xincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150103,150100,'3','回民区','','回民','0471','010030','Huimin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150104,150100,'3','玉泉区','','玉泉','0471','010020','Yuquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150105,150100,'3','赛罕区','','赛罕','0471','010020','Saihan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150121,150100,'3','土默特左旗','','土默特左旗','0471','010100','Tumotezuoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150122,150100,'3','托克托县','','托克托','0471','010200','Tuoketuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150123,150100,'3','和林格尔县','','和林格尔','0471','011500','Helingeer',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150124,150100,'3','清水河县','','清水河','0471','011600','Qingshuihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150125,150100,'3','武川县','','武川','0471','011700','Wuchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150200,150000,'2','包头市','','包头','0472','014025','Baotou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150202,150200,'3','东河区','','东河','0472','014040','Donghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150203,150200,'3','昆都仑区','','昆都仑','0472','014010','Kundulun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150204,150200,'3','青山区','','青山','0472','014030','Qingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150205,150200,'3','石拐区','','石拐','0472','014070','Shiguai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150206,150200,'3','白云鄂博矿区','','白云鄂博矿区','0472','014080','Baiyunebokuangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150207,150200,'3','九原区','','九原','0472','014060','Jiuyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150221,150200,'3','土默特右旗','','土默特右旗','0472','014100','Tumoteyouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150222,150200,'3','固阳县','','固阳','0472','014200','Guyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150223,150200,'3','达尔罕茂明安联合旗','','达茂旗','0472','014500','Damaoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150300,150000,'2','乌海市','','乌海','0473','016000','Wuhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150302,150300,'3','海勃湾区','','海勃湾','0473','016000','Haibowan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150303,150300,'3','海南区','','海南','0473','016030','Hainan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150304,150300,'3','乌达区','','乌达','0473','016040','Wuda',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150400,150000,'2','赤峰市','','赤峰','0476','024000','Chifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150402,150400,'3','红山区','','红山','0476','024020','Hongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150403,150400,'3','元宝山区','','元宝山','0476','024076','Yuanbaoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150404,150400,'3','松山区','','松山','0476','024005','Songshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150421,150400,'3','阿鲁科尔沁旗','','阿鲁科尔沁旗','0476','025550','Alukeerqinqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150422,150400,'3','巴林左旗','','巴林左旗','0476','025450','Balinzuoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150423,150400,'3','巴林右旗','','巴林右旗','0476','025150','Balinyouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150424,150400,'3','林西县','','林西','0476','025250','Linxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150425,150400,'3','克什克腾旗','','克什克腾旗','0476','025350','Keshiketengqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150426,150400,'3','翁牛特旗','','翁牛特旗','0476','024500','Wengniuteqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150428,150400,'3','喀喇沁旗','','喀喇沁旗','0476','024400','Kalaqinqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150429,150400,'3','宁城县','','宁城','0476','024200','Ningcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150430,150400,'3','敖汉旗','','敖汉旗','0476','024300','Aohanqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150500,150000,'2','通辽市','','通辽','0475','028000','Tongliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150502,150500,'3','科尔沁区','','科尔沁','0475','028000','Keerqin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150521,150500,'3','科尔沁左翼中旗','','科尔沁左翼中旗','0475','029300','Keerqinzuoyizhongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150522,150500,'3','科尔沁左翼后旗','','科尔沁左翼后旗','0475','028100','Keerqinzuoyihouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150523,150500,'3','开鲁县','','开鲁','0475','028400','Kailu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150524,150500,'3','库伦旗','','库伦旗','0475','028200','Kulunqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150525,150500,'3','奈曼旗','','奈曼旗','0475','028300','Naimanqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150526,150500,'3','扎鲁特旗','','扎鲁特旗','0475','029100','Zhaluteqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150581,150500,'3','霍林郭勒市','','霍林郭勒','0475','029200','Huolinguole',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150600,150000,'2','鄂尔多斯市','','鄂尔多斯','0477','017004','Ordos',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150602,150600,'3','东胜区','','东胜','0477','017000','Dongsheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150621,150600,'3','达拉特旗','','达拉特旗','0477','014300','Dalateqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150622,150600,'3','准格尔旗','','准格尔旗','0477','017100','Zhungeerqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150623,150600,'3','鄂托克前旗','','鄂托克前旗','0477','016200','Etuokeqianqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150624,150600,'3','鄂托克旗','','鄂托克旗','0477','016100','Etuokeqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150625,150600,'3','杭锦旗','','杭锦旗','0477','017400','Hangjinqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150626,150600,'3','乌审旗','','乌审旗','0477','017300','Wushenqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150627,150600,'3','伊金霍洛旗','','伊金霍洛旗','0477','017200','Yijinhuoluoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150700,150000,'2','呼伦贝尔市','','呼伦贝尔','0470','021008','Hulunber',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150702,150700,'3','海拉尔区','','海拉尔','0470','021000','Hailaer',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150703,150700,'3','扎赉诺尔区','','扎赉诺尔','0470','021410','Zhalainuoer',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150721,150700,'3','阿荣旗','','阿荣旗','0470','162750','Arongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150722,150700,'3','莫力达瓦达斡尔族自治旗','','莫旗','0470','162850','Moqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150723,150700,'3','鄂伦春自治旗','','鄂伦春','0470','165450','Elunchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150724,150700,'3','鄂温克族自治旗','','鄂温','0470','021100','Ewen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150725,150700,'3','陈巴尔虎旗','','陈巴尔虎旗','0470','021500','Chenbaerhuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150726,150700,'3','新巴尔虎左旗','','新巴尔虎左旗','0470','021200','Xinbaerhuzuoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150727,150700,'3','新巴尔虎右旗','','新巴尔虎右旗','0470','021300','Xinbaerhuyouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150781,150700,'3','满洲里市','','满洲里','0470','021400','Manzhouli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150782,150700,'3','牙克石市','','牙克石','0470','022150','Yakeshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150783,150700,'3','扎兰屯市','','扎兰屯','0470','162650','Zhalantun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150784,150700,'3','额尔古纳市','','额尔古纳','0470','022250','Eerguna',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150785,150700,'3','根河市','','根河','0470','022350','Genhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150800,150000,'2','巴彦淖尔市','','巴彦淖尔','0478','015001','Bayan Nur',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150802,150800,'3','临河区','','临河','0478','015001','Linhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150821,150800,'3','五原县','','五原','0478','015100','Wuyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150822,150800,'3','磴口县','','磴口','0478','015200','Dengkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150823,150800,'3','乌拉特前旗','','乌拉特前旗','0478','014400','Wulateqianqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150824,150800,'3','乌拉特中旗','','乌拉特中旗','0478','015300','Wulatezhongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150825,150800,'3','乌拉特后旗','','乌拉特后旗','0478','015500','Wulatehouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150826,150800,'3','杭锦后旗','','杭锦后旗','0478','015400','Hangjinhouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150900,150000,'2','乌兰察布市','','乌兰察布','0474','012000','Ulanqab',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150902,150900,'3','集宁区','','集宁','0474','012000','Jining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150921,150900,'3','卓资县','','卓资','0474','012300','Zhuozi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150922,150900,'3','化德县','','化德','0474','013350','Huade',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150923,150900,'3','商都县','','商都','0474','013450','Shangdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150924,150900,'3','兴和县','','兴和','0474','013650','Xinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150925,150900,'3','凉城县','','凉城','0474','013750','Liangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150926,150900,'3','察哈尔右翼前旗','','察右前旗','0474','012200','Chayouqianqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150927,150900,'3','察哈尔右翼中旗','','察右中旗','0474','013550','Chayouzhongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150928,150900,'3','察哈尔右翼后旗','','察右后旗','0474','012400','Chayouhouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150929,150900,'3','四子王旗','','四子王旗','0474','011800','Siziwangqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (150981,150900,'3','丰镇市','','丰镇','0474','012100','Fengzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152200,150000,'2','兴安盟','','兴安盟','0482','137401','Hinggan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152201,152200,'3','乌兰浩特市','','乌兰浩特','0482','137401','Wulanhaote',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152202,152200,'3','阿尔山市','','阿尔山','0482','137800','Aershan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152221,152200,'3','科尔沁右翼前旗','','科右前旗','0482','137423','Keyouqianqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152222,152200,'3','科尔沁右翼中旗','','科右中旗','0482','029400','Keyouzhongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152223,152200,'3','扎赉特旗','','扎赉特旗','0482','137600','Zhalaiteqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152224,152200,'3','突泉县','','突泉','0482','137500','Tuquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152500,150000,'2','锡林郭勒盟','','锡林郭勒盟','0479','026000','Xilin Gol',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152501,152500,'3','二连浩特市','','二连浩特','0479','011100','Erlianhaote',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152502,152500,'3','锡林浩特市','','锡林浩特','0479','026021','Xilinhaote',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152522,152500,'3','阿巴嘎旗','','阿巴嘎旗','0479','011400','Abagaqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152523,152500,'3','苏尼特左旗','','苏尼特左旗','0479','011300','Sunitezuoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152524,152500,'3','苏尼特右旗','','苏尼特右旗','0479','011200','Suniteyouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152525,152500,'3','东乌珠穆沁旗','','东乌旗','0479','026300','Dongwuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152526,152500,'3','西乌珠穆沁旗','','西乌旗','0479','026200','Xiwuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152527,152500,'3','太仆寺旗','','太仆寺旗','0479','027000','Taipusiqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152528,152500,'3','镶黄旗','','镶黄旗','0479','013250','Xianghuangqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152529,152500,'3','正镶白旗','','正镶白旗','0479','013800','Zhengxiangbaiqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152530,152500,'3','正蓝旗','','正蓝旗','0479','027200','Zhenglanqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152531,152500,'3','多伦县','','多伦','0479','027300','Duolun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152900,150000,'2','阿拉善盟','','阿拉善盟','0483','750306','Alxa',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152921,152900,'3','阿拉善左旗','','阿拉善左旗','0483','750306','Alashanzuoqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152922,152900,'3','阿拉善右旗','','阿拉善右旗','0483','737300','Alashanyouqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (152923,152900,'3','额济纳旗','','额济纳旗','0483','735400','Ejinaqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210000,100000,'1','辽宁省','','辽宁','','','Liaoning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210100,210000,'2','沈阳市','','沈阳','024','110013','Shenyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210102,210100,'3','和平区','','和平','024','110001','Heping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210103,210100,'3','沈河区','','沈河','024','110011','Shenhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210104,210100,'3','大东区','','大东','024','110041','Dadong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210105,210100,'3','皇姑区','','皇姑','024','110031','Huanggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210106,210100,'3','铁西区','','铁西','024','110021','Tiexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210111,210100,'3','苏家屯区','','苏家屯','024','110101','Sujiatun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210112,210100,'3','浑南区','','浑南','024','110015','Hunnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210113,210100,'3','沈北新区','','沈北新区','024','110121','Shenbeixinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210114,210100,'3','于洪区','','于洪','024','110141','Yuhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210122,210100,'3','辽中县','','辽中','024','110200','Liaozhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210123,210100,'3','康平县','','康平','024','110500','Kangping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210124,210100,'3','法库县','','法库','024','110400','Faku',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210181,210100,'3','新民市','','新民','024','110300','Xinmin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210200,210000,'2','大连市','','大连','0411','116011','Dalian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210202,210200,'3','中山区','','中山','0411','116001','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210203,210200,'3','西岗区','','西岗','0411','116011','Xigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210204,210200,'3','沙河口区','','沙河口','0411','116021','Shahekou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210211,210200,'3','甘井子区','','甘井子','0411','116033','Ganjingzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210212,210200,'3','旅顺口区','','旅顺口','0411','116041','Lvshunkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210213,210200,'3','金州区','','金州','0411','116100','Jinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210224,210200,'3','长海县','','长海','0411','116500','Changhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210281,210200,'3','瓦房店市','','瓦房店','0411','116300','Wafangdian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210282,210200,'3','普兰店市','','普兰店','0411','116200','Pulandian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210283,210200,'3','庄河市','','庄河','0411','116400','Zhuanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210300,210000,'2','鞍山市','','鞍山','0412','114001','Anshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210302,210300,'3','铁东区','','铁东','0412','114001','Tiedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210303,210300,'3','铁西区','','铁西','0413','114013','Tiexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210304,210300,'3','立山区','','立山','0414','114031','Lishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210311,210300,'3','千山区','','千山','0415','114041','Qianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210321,210300,'3','台安县','','台安','0417','114100','Tai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210323,210300,'3','岫岩满族自治县','','岫岩','0418','114300','Xiuyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210381,210300,'3','海城市','','海城','0416','114200','Haicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210400,210000,'2','抚顺市','','抚顺','024','113008','Fushun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210402,210400,'3','新抚区','','新抚','024','113008','Xinfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210403,210400,'3','东洲区','','东洲','024','113003','Dongzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210404,210400,'3','望花区','','望花','024','113001','Wanghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210411,210400,'3','顺城区','','顺城','024','113006','Shuncheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210421,210400,'3','抚顺县','','抚顺','024','113006','Fushun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210422,210400,'3','新宾满族自治县','','新宾','024','113200','Xinbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210423,210400,'3','清原满族自治县','','清原','024','113300','Qingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210500,210000,'2','本溪市','','本溪','0414','117000','Benxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210502,210500,'3','平山区','','平山','0414','117000','Pingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210503,210500,'3','溪湖区','','溪湖','0414','117002','Xihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210504,210500,'3','明山区','','明山','0414','117021','Mingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210505,210500,'3','南芬区','','南芬','0414','117014','Nanfen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210521,210500,'3','本溪满族自治县','','本溪','0414','117100','Benxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210522,210500,'3','桓仁满族自治县','','桓仁','0414','117200','Huanren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210600,210000,'2','丹东市','','丹东','0415','118000','Dandong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210602,210600,'3','元宝区','','元宝','0415','118000','Yuanbao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210603,210600,'3','振兴区','','振兴','0415','118002','Zhenxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210604,210600,'3','振安区','','振安','0415','118001','Zhen an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210624,210600,'3','宽甸满族自治县','','宽甸','0415','118200','Kuandian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210681,210600,'3','东港市','','东港','0415','118300','Donggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210682,210600,'3','凤城市','','凤城','0415','118100','Fengcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210700,210000,'2','锦州市','','锦州','0416','121000','Jinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210702,210700,'3','古塔区','','古塔','0416','121001','Guta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210703,210700,'3','凌河区','','凌河','0416','121000','Linghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210711,210700,'3','太和区','','太和','0416','121011','Taihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210726,210700,'3','黑山县','','黑山','0416','121400','Heishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210727,210700,'3','义县','','义县','0416','121100','Yixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210781,210700,'3','凌海市','','凌海','0416','121200','Linghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210782,210700,'3','北镇市','','北镇','0416','121300','Beizhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210800,210000,'2','营口市','','营口','0417','115003','Yingkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210802,210800,'3','站前区','','站前','0417','115002','Zhanqian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210803,210800,'3','西市区','','西市','0417','115004','Xishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210804,210800,'3','鲅鱼圈区','','鲅鱼圈','0417','115007','Bayuquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210811,210800,'3','老边区','','老边','0417','115005','Laobian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210881,210800,'3','盖州市','','盖州','0417','115200','Gaizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210882,210800,'3','大石桥市','','大石桥','0417','115100','Dashiqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210900,210000,'2','阜新市','','阜新','0418','123000','Fuxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210902,210900,'3','海州区','','海州','0418','123000','Haizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210903,210900,'3','新邱区','','新邱','0418','123005','Xinqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210904,210900,'3','太平区','','太平','0418','123003','Taiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210905,210900,'3','清河门区','','清河门','0418','123006','Qinghemen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210911,210900,'3','细河区','','细河','0418','123000','Xihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210921,210900,'3','阜新蒙古族自治县','','阜新','0418','123100','Fuxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (210922,210900,'3','彰武县','','彰武','0418','123200','Zhangwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211000,210000,'2','辽阳市','','辽阳','0419','111000','Liaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211002,211000,'3','白塔区','','白塔','0419','111000','Baita',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211003,211000,'3','文圣区','','文圣','0419','111000','Wensheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211004,211000,'3','宏伟区','','宏伟','0419','111003','Hongwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211005,211000,'3','弓长岭区','','弓长岭','0419','111008','Gongchangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211011,211000,'3','太子河区','','太子河','0419','111000','Taizihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211021,211000,'3','辽阳县','','辽阳','0419','111200','Liaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211081,211000,'3','灯塔市','','灯塔','0419','111300','Dengta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211100,210000,'2','盘锦市','','盘锦','0427','124010','Panjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211102,211100,'3','双台子区','','双台子','0427','124000','Shuangtaizi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211103,211100,'3','兴隆台区','','兴隆台','0427','124010','Xinglongtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211121,211100,'3','大洼县','','大洼','0427','124200','Dawa',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211122,211100,'3','盘山县','','盘山','0427','124000','Panshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211200,210000,'2','铁岭市','','铁岭','024','112000','Tieling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211202,211200,'3','银州区','','银州','024','112000','Yinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211204,211200,'3','清河区','','清河','024','112003','Qinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211221,211200,'3','铁岭县','','铁岭','024','112000','Tieling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211223,211200,'3','西丰县','','西丰','024','112400','Xifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211224,211200,'3','昌图县','','昌图','024','112500','Changtu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211281,211200,'3','调兵山市','','调兵山','024','112700','Diaobingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211282,211200,'3','开原市','','开原','024','112300','Kaiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211300,210000,'2','朝阳市','','朝阳','0421','122000','Chaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211302,211300,'3','双塔区','','双塔','0421','122000','Shuangta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211303,211300,'3','龙城区','','龙城','0421','122000','Longcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211321,211300,'3','朝阳县','','朝阳','0421','122000','Chaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211322,211300,'3','建平县','','建平','0421','122400','Jianping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211324,211300,'3','喀喇沁左翼蒙古族自治县','','喀喇沁左翼','0421','122300','Kalaqinzuoyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211381,211300,'3','北票市','','北票','0421','122100','Beipiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211382,211300,'3','凌源市','','凌源','0421','122500','Lingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211400,210000,'2','葫芦岛市','','葫芦岛','0429','125000','Huludao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211402,211400,'3','连山区','','连山','0429','125001','Lianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211403,211400,'3','龙港区','','龙港','0429','125003','Longgang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211404,211400,'3','南票区','','南票','0429','125027','Nanpiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211421,211400,'3','绥中县','','绥中','0429','125200','Suizhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211422,211400,'3','建昌县','','建昌','0429','125300','Jianchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211481,211400,'3','兴城市','','兴城','0429','125100','Xingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211500,210000,'2','金普新区','','金普新区','0411','116100','Jinpuxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211501,211500,'3','金州新区','','金州新区','0411','116100','Jinzhouxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211502,211500,'3','普湾新区','','普湾新区','0411','116200','Puwanxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (211503,211500,'3','保税区','','保税区','0411','116100','Baoshuiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220000,100000,'1','吉林省','','吉林','','','Jilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220100,220000,'2','长春市','','长春','0431','130022','Changchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220102,220100,'3','南关区','','南关','0431','130022','Nanguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220103,220100,'3','宽城区','','宽城','0431','130051','Kuancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220104,220100,'3','朝阳区','','朝阳','0431','130012','Chaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220105,220100,'3','二道区','','二道','0431','130031','Erdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220106,220100,'3','绿园区','','绿园','0431','130062','Lvyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220112,220100,'3','双阳区','','双阳','0431','130600','Shuangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220113,220100,'3','九台区','','九台','0431','130500','Jiutai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220122,220100,'3','农安县','','农安','0431','130200','Nong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220182,220100,'3','榆树市','','榆树','0431','130400','Yushu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220183,220100,'3','德惠市','','德惠','0431','130300','Dehui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220200,220000,'2','吉林市','','吉林','0432','132011','Jilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220202,220200,'3','昌邑区','','昌邑','0432','132002','Changyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220203,220200,'3','龙潭区','','龙潭','0432','132021','Longtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220204,220200,'3','船营区','','船营','0432','132011','Chuanying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220211,220200,'3','丰满区','','丰满','0432','132013','Fengman',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220221,220200,'3','永吉县','','永吉','0432','132200','Yongji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220281,220200,'3','蛟河市','','蛟河','0432','132500','Jiaohe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220282,220200,'3','桦甸市','','桦甸','0432','132400','Huadian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220283,220200,'3','舒兰市','','舒兰','0432','132600','Shulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220284,220200,'3','磐石市','','磐石','0432','132300','Panshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220300,220000,'2','四平市','','四平','0434','136000','Siping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220302,220300,'3','铁西区','','铁西','0434','136000','Tiexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220303,220300,'3','铁东区','','铁东','0434','136001','Tiedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220322,220300,'3','梨树县','','梨树','0434','136500','Lishu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220323,220300,'3','伊通满族自治县','','伊通','0434','130700','Yitong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220381,220300,'3','公主岭市','','公主岭','0434','136100','Gongzhuling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220382,220300,'3','双辽市','','双辽','0434','136400','Shuangliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220400,220000,'2','辽源市','','辽源','0437','136200','Liaoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220402,220400,'3','龙山区','','龙山','0437','136200','Longshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220403,220400,'3','西安区','','西安','0437','136201','Xi an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220421,220400,'3','东丰县','','东丰','0437','136300','Dongfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220422,220400,'3','东辽县','','东辽','0437','136600','Dongliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220500,220000,'2','通化市','','通化','0435','134001','Tonghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220502,220500,'3','东昌区','','东昌','0435','134001','Dongchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220503,220500,'3','二道江区','','二道江','0435','134003','Erdaojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220521,220500,'3','通化县','','通化','0435','134100','Tonghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220523,220500,'3','辉南县','','辉南','0435','135100','Huinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220524,220500,'3','柳河县','','柳河','0435','135300','Liuhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220581,220500,'3','梅河口市','','梅河口','0435','135000','Meihekou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220582,220500,'3','集安市','','集安','0435','134200','Ji an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220600,220000,'2','白山市','','白山','0439','134300','Baishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220602,220600,'3','浑江区','','浑江','0439','134300','Hunjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220605,220600,'3','江源区','','江源','0439','134700','Jiangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220621,220600,'3','抚松县','','抚松','0439','134500','Fusong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220622,220600,'3','靖宇县','','靖宇','0439','135200','Jingyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220623,220600,'3','长白朝鲜族自治县','','长白','0439','134400','Changbai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220681,220600,'3','临江市','','临江','0439','134600','Linjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220700,220000,'2','松原市','','松原','0438','138000','Songyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220702,220700,'3','宁江区','','宁江','0438','138000','Ningjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220721,220700,'3','前郭尔罗斯蒙古族自治县','','前郭尔罗斯','0438','138000','Qianguoerluosi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220722,220700,'3','长岭县','','长岭','0438','131500','Changling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220723,220700,'3','乾安县','','乾安','0438','131400','Qian an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220781,220700,'3','扶余市','','扶余','0438','131200','Fuyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220800,220000,'2','白城市','','白城','0436','137000','Baicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220802,220800,'3','洮北区','','洮北','0436','137000','Taobei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220821,220800,'3','镇赉县','','镇赉','0436','137300','Zhenlai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220822,220800,'3','通榆县','','通榆','0436','137200','Tongyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220881,220800,'3','洮南市','','洮南','0436','137100','Taonan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (220882,220800,'3','大安市','','大安','0436','131300','Da an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222400,220000,'2','延边朝鲜族自治州','','延边','0433','133000','Yanbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222401,222400,'3','延吉市','','延吉','0433','133000','Yanji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222402,222400,'3','图们市','','图们','0433','133100','Tumen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222403,222400,'3','敦化市','','敦化','0433','133700','Dunhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222404,222400,'3','珲春市','','珲春','0433','133300','Hunchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222405,222400,'3','龙井市','','龙井','0433','133400','Longjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222406,222400,'3','和龙市','','和龙','0433','133500','Helong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222424,222400,'3','汪清县','','汪清','0433','133200','Wangqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (222426,222400,'3','安图县','','安图','0433','133600','Antu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230000,100000,'1','黑龙江省','','黑龙江','','','Heilongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230100,230000,'2','哈尔滨市','','哈尔滨','0451','150010','Harbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230102,230100,'3','道里区','','道里','0451','150010','Daoli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230103,230100,'3','南岗区','','南岗','0451','150006','Nangang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230104,230100,'3','道外区','','道外','0451','150020','Daowai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230108,230100,'3','平房区','','平房','0451','150060','Pingfang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230109,230100,'3','松北区','','松北','0451','150028','Songbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230110,230100,'3','香坊区','','香坊','0451','150036','Xiangfang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230111,230100,'3','呼兰区','','呼兰','0451','150500','Hulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230112,230100,'3','阿城区','','阿城','0451','150300','A cheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230113,230100,'3','双城区','','双城','0451','150100','Shuangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230123,230100,'3','依兰县','','依兰','0451','154800','Yilan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230124,230100,'3','方正县','','方正','0451','150800','Fangzheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230125,230100,'3','宾县','','宾县','0451','150400','Binxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230126,230100,'3','巴彦县','','巴彦','0451','151800','Bayan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230127,230100,'3','木兰县','','木兰','0451','151900','Mulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230128,230100,'3','通河县','','通河','0451','150900','Tonghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230129,230100,'3','延寿县','','延寿','0451','150700','Yanshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230183,230100,'3','尚志市','','尚志','0451','150600','Shangzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230184,230100,'3','五常市','','五常','0451','150200','Wuchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230200,230000,'2','齐齐哈尔市','','齐齐哈尔','0452','161005','Qiqihar',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230202,230200,'3','龙沙区','','龙沙','0452','161000','Longsha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230203,230200,'3','建华区','','建华','0452','161006','Jianhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230204,230200,'3','铁锋区','','铁锋','0452','161000','Tiefeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230205,230200,'3','昂昂溪区','','昂昂溪','0452','161031','Angangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230206,230200,'3','富拉尔基区','','富拉尔基','0452','161041','Fulaerji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230207,230200,'3','碾子山区','','碾子山','0452','161046','Nianzishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230208,230200,'3','梅里斯达斡尔族区','','梅里斯','0452','161021','Meilisi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230221,230200,'3','龙江县','','龙江','0452','161100','Longjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230223,230200,'3','依安县','','依安','0452','161500','Yi an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230224,230200,'3','泰来县','','泰来','0452','162400','Tailai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230225,230200,'3','甘南县','','甘南','0452','162100','Gannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230227,230200,'3','富裕县','','富裕','0452','161200','Fuyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230229,230200,'3','克山县','','克山','0452','161600','Keshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230230,230200,'3','克东县','','克东','0452','164800','Kedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230231,230200,'3','拜泉县','','拜泉','0452','164700','Baiquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230281,230200,'3','讷河市','','讷河','0452','161300','Nehe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230300,230000,'2','鸡西市','','鸡西','0467','158100','Jixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230302,230300,'3','鸡冠区','','鸡冠','0467','158100','Jiguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230303,230300,'3','恒山区','','恒山','0467','158130','Hengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230304,230300,'3','滴道区','','滴道','0467','158150','Didao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230305,230300,'3','梨树区','','梨树','0467','158160','Lishu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230306,230300,'3','城子河区','','城子河','0467','158170','Chengzihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230307,230300,'3','麻山区','','麻山','0467','158180','Mashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230321,230300,'3','鸡东县','','鸡东','0467','158200','Jidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230381,230300,'3','虎林市','','虎林','0467','158400','Hulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230382,230300,'3','密山市','','密山','0467','158300','Mishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230400,230000,'2','鹤岗市','','鹤岗','0468','154100','Hegang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230402,230400,'3','向阳区','','向阳','0468','154100','Xiangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230403,230400,'3','工农区','','工农','0468','154101','Gongnong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230404,230400,'3','南山区','','南山','0468','154104','Nanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230405,230400,'3','兴安区','','兴安','0468','154102','Xing an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230406,230400,'3','东山区','','东山','0468','154106','Dongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230407,230400,'3','兴山区','','兴山','0468','154105','Xingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230421,230400,'3','萝北县','','萝北','0468','154200','Luobei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230422,230400,'3','绥滨县','','绥滨','0468','156200','Suibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230500,230000,'2','双鸭山市','','双鸭山','0469','155100','Shuangyashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230502,230500,'3','尖山区','','尖山','0469','155100','Jianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230503,230500,'3','岭东区','','岭东','0469','155120','Lingdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230505,230500,'3','四方台区','','四方台','0469','155130','Sifangtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230506,230500,'3','宝山区','','宝山','0469','155131','Baoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230521,230500,'3','集贤县','','集贤','0469','155900','Jixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230522,230500,'3','友谊县','','友谊','0469','155800','Youyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230523,230500,'3','宝清县','','宝清','0469','155600','Baoqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230524,230500,'3','饶河县','','饶河','0469','155700','Raohe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230600,230000,'2','大庆市','','大庆','0459','163000','Daqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230602,230600,'3','萨尔图区','','萨尔图','0459','163001','Saertu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230603,230600,'3','龙凤区','','龙凤','0459','163711','Longfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230604,230600,'3','让胡路区','','让胡路','0459','163712','Ranghulu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230605,230600,'3','红岗区','','红岗','0459','163511','Honggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230606,230600,'3','大同区','','大同','0459','163515','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230621,230600,'3','肇州县','','肇州','0459','166400','Zhaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230622,230600,'3','肇源县','','肇源','0459','166500','Zhaoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230623,230600,'3','林甸县','','林甸','0459','166300','Lindian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230624,230600,'3','杜尔伯特蒙古族自治县','','杜尔伯特','0459','166200','Duerbote',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230700,230000,'2','伊春市','','伊春','0458','153000','Yichun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230702,230700,'3','伊春区','','伊春','0458','153000','Yichun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230703,230700,'3','南岔区','','南岔','0458','153100','Nancha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230704,230700,'3','友好区','','友好','0458','153031','Youhao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230705,230700,'3','西林区','','西林','0458','153025','Xilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230706,230700,'3','翠峦区','','翠峦','0458','153013','Cuiluan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230707,230700,'3','新青区','','新青','0458','153036','Xinqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230708,230700,'3','美溪区','','美溪','0458','153021','Meixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230709,230700,'3','金山屯区','','金山屯','0458','153026','Jinshantun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230710,230700,'3','五营区','','五营','0458','153033','Wuying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230711,230700,'3','乌马河区','','乌马河','0458','153011','Wumahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230712,230700,'3','汤旺河区','','汤旺河','0458','153037','Tangwanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230713,230700,'3','带岭区','','带岭','0458','153106','Dailing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230714,230700,'3','乌伊岭区','','乌伊岭','0458','153038','Wuyiling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230715,230700,'3','红星区','','红星','0458','153035','Hongxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230716,230700,'3','上甘岭区','','上甘岭','0458','153032','Shangganling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230722,230700,'3','嘉荫县','','嘉荫','0458','153200','Jiayin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230781,230700,'3','铁力市','','铁力','0458','152500','Tieli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230800,230000,'2','佳木斯市','','佳木斯','0454','154002','Jiamusi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230803,230800,'3','向阳区','','向阳','0454','154002','Xiangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230804,230800,'3','前进区','','前进','0454','154002','Qianjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230805,230800,'3','东风区','','东风','0454','154005','Dongfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230811,230800,'3','郊区','','郊区','0454','154004','Jiaoqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230822,230800,'3','桦南县','','桦南','0454','154400','Huanan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230826,230800,'3','桦川县','','桦川','0454','154300','Huachuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230828,230800,'3','汤原县','','汤原','0454','154700','Tangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230833,230800,'3','抚远县','','抚远','0454','156500','Fuyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230881,230800,'3','同江市','','同江','0454','156400','Tongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230882,230800,'3','富锦市','','富锦','0454','156100','Fujin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230900,230000,'2','七台河市','','七台河','0464','154600','Qitaihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230902,230900,'3','新兴区','','新兴','0464','154604','Xinxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230903,230900,'3','桃山区','','桃山','0464','154600','Taoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230904,230900,'3','茄子河区','','茄子河','0464','154622','Qiezihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (230921,230900,'3','勃利县','','勃利','0464','154500','Boli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231000,230000,'2','牡丹江市','','牡丹江','0453','157000','Mudanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231002,231000,'3','东安区','','东安','0453','157000','Dong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231003,231000,'3','阳明区','','阳明','0453','157013','Yangming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231004,231000,'3','爱民区','','爱民','0453','157009','Aimin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231005,231000,'3','西安区','','西安','0453','157000','Xi an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231024,231000,'3','东宁县','','东宁','0453','157200','Dongning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231025,231000,'3','林口县','','林口','0453','157600','Linkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231081,231000,'3','绥芬河市','','绥芬河','0453','157300','Suifenhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231083,231000,'3','海林市','','海林','0453','157100','Hailin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231084,231000,'3','宁安市','','宁安','0453','157400','Ning an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231085,231000,'3','穆棱市','','穆棱','0453','157500','Muling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231100,230000,'2','黑河市','','黑河','0456','164300','Heihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231102,231100,'3','爱辉区','','爱辉','0456','164300','Aihui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231121,231100,'3','嫩江县','','嫩江','0456','161400','Nenjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231123,231100,'3','逊克县','','逊克','0456','164400','Xunke',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231124,231100,'3','孙吴县','','孙吴','0456','164200','Sunwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231181,231100,'3','北安市','','北安','0456','164000','Bei an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231182,231100,'3','五大连池市','','五大连池','0456','164100','Wudalianchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231200,230000,'2','绥化市','','绥化','0455','152000','Suihua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231202,231200,'3','北林区','','北林','0455','152000','Beilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231221,231200,'3','望奎县','','望奎','0455','152100','Wangkui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231222,231200,'3','兰西县','','兰西','0455','151500','Lanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231223,231200,'3','青冈县','','青冈','0455','151600','Qinggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231224,231200,'3','庆安县','','庆安','0455','152400','Qing an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231225,231200,'3','明水县','','明水','0455','151700','Mingshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231226,231200,'3','绥棱县','','绥棱','0455','152200','Suileng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231281,231200,'3','安达市','','安达','0455','151400','Anda',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231282,231200,'3','肇东市','','肇东','0455','151100','Zhaodong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (231283,231200,'3','海伦市','','海伦','0455','152300','Hailun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232700,230000,'2','大兴安岭地区','','大兴安岭','0457','165000','DaXingAnLing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232701,232700,'3','加格达奇区','','加格达奇','0457','165000','Jiagedaqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232702,232700,'3','新林区','','新林','0457','165000','Xinlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232703,232700,'3','松岭区','','松岭','0457','165000','Songling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232704,232700,'3','呼中区','','呼中','0457','165000','Huzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232721,232700,'3','呼玛县','','呼玛','0457','165100','Huma',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232722,232700,'3','塔河县','','塔河','0457','165200','Tahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (232723,232700,'3','漠河县','','漠河','0457','165300','Mohe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310000,100000,'1','上海','','上海','','','Shanghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310100,310000,'2','上海市','','上海','021','200000','Shanghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310101,310100,'3','黄浦区','','黄浦','021','200001','Huangpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310104,310100,'3','徐汇区','','徐汇','021','200030','Xuhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310105,310100,'3','长宁区','','长宁','021','200050','Changning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310106,310100,'3','静安区','','静安','021','200040','Jing an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310107,310100,'3','普陀区','','普陀','021','200333','Putuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310108,310100,'3','闸北区','','闸北','021','200070','Zhabei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310109,310100,'3','虹口区','','虹口','021','200086','Hongkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310110,310100,'3','杨浦区','','杨浦','021','200082','Yangpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310112,310100,'3','闵行区','','闵行','021','201100','Minhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310113,310100,'3','宝山区','','宝山','021','201900','Baoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310114,310100,'3','嘉定区','','嘉定','021','201800','Jiading',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310115,310100,'3','浦东新区','','浦东','021','200135','Pudong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310116,310100,'3','金山区','','金山','021','200540','Jinshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310117,310100,'3','松江区','','松江','021','201600','Songjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310118,310100,'3','青浦区','','青浦','021','201700','Qingpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310120,310100,'3','奉贤区','','奉贤','021','201400','Fengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (310230,310100,'3','崇明县','','崇明','021','202150','Chongming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320000,100000,'1','江苏省','','江苏','','','Jiangsu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320100,320000,'2','南京市','','南京','025','210008','Nanjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320102,320100,'3','玄武区','','玄武','025','210018','Xuanwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320104,320100,'3','秦淮区','','秦淮','025','210001','Qinhuai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320105,320100,'3','建邺区','','建邺','025','210004','Jianye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320106,320100,'3','鼓楼区','','鼓楼','025','210009','Gulou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320111,320100,'3','浦口区','','浦口','025','211800','Pukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320113,320100,'3','栖霞区','','栖霞','025','210046','Qixia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320114,320100,'3','雨花台区','','雨花台','025','210012','Yuhuatai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320115,320100,'3','江宁区','','江宁','025','211100','Jiangning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320116,320100,'3','六合区','','六合','025','211500','Luhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320117,320100,'3','溧水区','','溧水','025','211200','Lishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320118,320100,'3','高淳区','','高淳','025','211300','Gaochun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320200,320000,'2','无锡市','','无锡','0510','214000','Wuxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320202,320200,'3','崇安区','','崇安','0510','214001','Chong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320203,320200,'3','南长区','','南长','0510','214021','Nanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320204,320200,'3','北塘区','','北塘','0510','214044','Beitang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320205,320200,'3','锡山区','','锡山','0510','214101','Xishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320206,320200,'3','惠山区','','惠山','0510','214174','Huishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320211,320200,'3','滨湖区','','滨湖','0510','214123','Binhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320281,320200,'3','江阴市','','江阴','0510','214431','Jiangyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320282,320200,'3','宜兴市','','宜兴','0510','214200','Yixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320300,320000,'2','徐州市','','徐州','0516','221003','Xuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320302,320300,'3','鼓楼区','','鼓楼','0516','221005','Gulou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320303,320300,'3','云龙区','','云龙','0516','221007','Yunlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320305,320300,'3','贾汪区','','贾汪','0516','221003','Jiawang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320311,320300,'3','泉山区','','泉山','0516','221006','Quanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320312,320300,'3','铜山区','','铜山','0516','221106','Tongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320321,320300,'3','丰县','','丰县','0516','221700','Fengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320322,320300,'3','沛县','','沛县','0516','221600','Peixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320324,320300,'3','睢宁县','','睢宁','0516','221200','Suining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320381,320300,'3','新沂市','','新沂','0516','221400','Xinyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320382,320300,'3','邳州市','','邳州','0516','221300','Pizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320400,320000,'2','常州市','','常州','0519','213000','Changzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320402,320400,'3','天宁区','','天宁','0519','213000','Tianning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320404,320400,'3','钟楼区','','钟楼','0519','213023','Zhonglou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320405,320400,'3','戚墅堰区','','戚墅堰','0519','213025','Qishuyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320411,320400,'3','新北区','','新北','0519','213022','Xinbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320412,320400,'3','武进区','','武进','0519','213100','Wujin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320481,320400,'3','溧阳市','','溧阳','0519','213300','Liyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320482,320400,'3','金坛市','','金坛','0519','213200','Jintan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320500,320000,'2','苏州市','','苏州','0512','215002','Suzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320505,320500,'3','虎丘区','','虎丘','0512','215004','Huqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320506,320500,'3','吴中区','','吴中','0512','215128','Wuzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320507,320500,'3','相城区','','相城','0512','215131','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320508,320500,'3','姑苏区','','姑苏','0512','215031','Gusu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320509,320500,'3','吴江区','','吴江','0512','215200','Wujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320581,320500,'3','常熟市','','常熟','0512','215500','Changshu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320582,320500,'3','张家港市','','张家港','0512','215600','Zhangjiagang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320583,320500,'3','昆山市','','昆山','0512','215300','Kunshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320585,320500,'3','太仓市','','太仓','0512','215400','Taicang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320600,320000,'2','南通市','','南通','0513','226001','Nantong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320602,320600,'3','崇川区','','崇川','0513','226001','Chongchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320611,320600,'3','港闸区','','港闸','0513','226001','Gangzha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320612,320600,'3','通州区','','通州','0513','226300','Tongzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320621,320600,'3','海安县','','海安','0513','226600','Hai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320623,320600,'3','如东县','','如东','0513','226400','Rudong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320681,320600,'3','启东市','','启东','0513','226200','Qidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320682,320600,'3','如皋市','','如皋','0513','226500','Rugao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320684,320600,'3','海门市','','海门','0513','226100','Haimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320700,320000,'2','连云港市','','连云港','0518','222002','Lianyungang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320703,320700,'3','连云区','','连云','0518','222042','Lianyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320706,320700,'3','海州区','','海州','0518','222003','Haizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320707,320700,'3','赣榆区','','赣榆','0518','222100','Ganyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320722,320700,'3','东海县','','东海','0518','222300','Donghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320723,320700,'3','灌云县','','灌云','0518','222200','Guanyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320724,320700,'3','灌南县','','灌南','0518','222500','Guannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320800,320000,'2','淮安市','','淮安','0517','223001','Huai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320802,320800,'3','清河区','','清河','0517','223001','Qinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320803,320800,'3','淮安区','','淮安','0517','223200','Huai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320804,320800,'3','淮阴区','','淮阴','0517','223300','Huaiyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320811,320800,'3','清浦区','','清浦','0517','223002','Qingpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320826,320800,'3','涟水县','','涟水','0517','223400','Lianshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320829,320800,'3','洪泽县','','洪泽','0517','223100','Hongze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320830,320800,'3','盱眙县','','盱眙','0517','211700','Xuyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320831,320800,'3','金湖县','','金湖','0517','211600','Jinhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320900,320000,'2','盐城市','','盐城','0515','224005','Yancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320902,320900,'3','亭湖区','','亭湖','0515','224005','Tinghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320903,320900,'3','盐都区','','盐都','0515','224055','Yandu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320921,320900,'3','响水县','','响水','0515','224600','Xiangshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320922,320900,'3','滨海县','','滨海','0515','224500','Binhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320923,320900,'3','阜宁县','','阜宁','0515','224400','Funing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320924,320900,'3','射阳县','','射阳','0515','224300','Sheyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320925,320900,'3','建湖县','','建湖','0515','224700','Jianhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320981,320900,'3','东台市','','东台','0515','224200','Dongtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (320982,320900,'3','大丰市','','大丰','0515','224100','Dafeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321000,320000,'2','扬州市','','扬州','0514','225002','Yangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321002,321000,'3','广陵区','','广陵','0514','225002','Guangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321003,321000,'3','邗江区','','邗江','0514','225002','Hanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321012,321000,'3','江都区','','江都','0514','225200','Jiangdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321023,321000,'3','宝应县','','宝应','0514','225800','Baoying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321081,321000,'3','仪征市','','仪征','0514','211400','Yizheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321084,321000,'3','高邮市','','高邮','0514','225600','Gaoyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321100,320000,'2','镇江市','','镇江','0511','212004','Zhenjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321102,321100,'3','京口区','','京口','0511','212003','Jingkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321111,321100,'3','润州区','','润州','0511','212005','Runzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321112,321100,'3','丹徒区','','丹徒','0511','212028','Dantu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321181,321100,'3','丹阳市','','丹阳','0511','212300','Danyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321182,321100,'3','扬中市','','扬中','0511','212200','Yangzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321183,321100,'3','句容市','','句容','0511','212400','Jurong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321200,320000,'2','泰州市','','泰州','0523','225300','Taizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321202,321200,'3','海陵区','','海陵','0523','225300','Hailing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321203,321200,'3','高港区','','高港','0523','225321','Gaogang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321204,321200,'3','姜堰区','','姜堰','0523','225500','Jiangyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321281,321200,'3','兴化市','','兴化','0523','225700','Xinghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321282,321200,'3','靖江市','','靖江','0523','214500','Jingjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321283,321200,'3','泰兴市','','泰兴','0523','225400','Taixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321300,320000,'2','宿迁市','','宿迁','0527','223800','Suqian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321302,321300,'3','宿城区','','宿城','0527','223800','Sucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321311,321300,'3','宿豫区','','宿豫','0527','223800','Suyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321322,321300,'3','沭阳县','','沭阳','0527','223600','Shuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321323,321300,'3','泗阳县','','泗阳','0527','223700','Siyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (321324,321300,'3','泗洪县','','泗洪','0527','223900','Sihong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330000,100000,'1','浙江省','','浙江','','','Zhejiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330100,330000,'2','杭州市','','杭州','0571','310026','Hangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330102,330100,'3','上城区','','上城','0571','310002','Shangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330103,330100,'3','下城区','','下城','0571','310006','Xiacheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330104,330100,'3','江干区','','江干','0571','310016','Jianggan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330105,330100,'3','拱墅区','','拱墅','0571','310011','Gongshu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330106,330100,'3','西湖区','','西湖','0571','310013','Xihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330108,330100,'3','滨江区','','滨江','0571','310051','Binjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330109,330100,'3','萧山区','','萧山','0571','311200','Xiaoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330110,330100,'3','余杭区','','余杭','0571','311100','Yuhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330122,330100,'3','桐庐县','','桐庐','0571','311500','Tonglu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330127,330100,'3','淳安县','','淳安','0571','311700','Chun an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330182,330100,'3','建德市','','建德','0571','311600','Jiande',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330183,330100,'3','富阳区','','富阳','0571','311400','Fuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330185,330100,'3','临安市','','临安','0571','311300','Lin an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330200,330000,'2','宁波市','','宁波','0574','315000','Ningbo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330203,330200,'3','海曙区','','海曙','0574','315000','Haishu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330204,330200,'3','江东区','','江东','0574','315040','Jiangdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330205,330200,'3','江北区','','江北','0574','315020','Jiangbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330206,330200,'3','北仑区','','北仑','0574','315800','Beilun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330211,330200,'3','镇海区','','镇海','0574','315200','Zhenhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330212,330200,'3','鄞州区','','鄞州','0574','315100','Yinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330225,330200,'3','象山县','','象山','0574','315700','Xiangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330226,330200,'3','宁海县','','宁海','0574','315600','Ninghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330281,330200,'3','余姚市','','余姚','0574','315400','Yuyao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330282,330200,'3','慈溪市','','慈溪','0574','315300','Cixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330283,330200,'3','奉化市','','奉化','0574','315500','Fenghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330300,330000,'2','温州市','','温州','0577','325000','Wenzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330302,330300,'3','鹿城区','','鹿城','0577','325000','Lucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330303,330300,'3','龙湾区','','龙湾','0577','325013','Longwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330304,330300,'3','瓯海区','','瓯海','0577','325005','Ouhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330322,330300,'3','洞头县','','洞头','0577','325700','Dongtou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330324,330300,'3','永嘉县','','永嘉','0577','325100','Yongjia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330326,330300,'3','平阳县','','平阳','0577','325400','Pingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330327,330300,'3','苍南县','','苍南','0577','325800','Cangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330328,330300,'3','文成县','','文成','0577','325300','Wencheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330329,330300,'3','泰顺县','','泰顺','0577','325500','Taishun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330381,330300,'3','瑞安市','','瑞安','0577','325200','Rui an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330382,330300,'3','乐清市','','乐清','0577','325600','Yueqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330400,330000,'2','嘉兴市','','嘉兴','0573','314000','Jiaxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330402,330400,'3','南湖区','','南湖','0573','314051','Nanhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330411,330400,'3','秀洲区','','秀洲','0573','314031','Xiuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330421,330400,'3','嘉善县','','嘉善','0573','314100','Jiashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330424,330400,'3','海盐县','','海盐','0573','314300','Haiyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330481,330400,'3','海宁市','','海宁','0573','314400','Haining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330482,330400,'3','平湖市','','平湖','0573','314200','Pinghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330483,330400,'3','桐乡市','','桐乡','0573','314500','Tongxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330500,330000,'2','湖州市','','湖州','0572','313000','Huzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330502,330500,'3','吴兴区','','吴兴','0572','313000','Wuxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330503,330500,'3','南浔区','','南浔','0572','313009','Nanxun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330521,330500,'3','德清县','','德清','0572','313200','Deqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330522,330500,'3','长兴县','','长兴','0572','313100','Changxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330523,330500,'3','安吉县','','安吉','0572','313300','Anji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330600,330000,'2','绍兴市','','绍兴','0575','312000','Shaoxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330602,330600,'3','越城区','','越城','0575','312000','Yuecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330603,330600,'3','柯桥区','','柯桥','0575','312030','Keqiao ',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330604,330600,'3','上虞区','','上虞','0575','312300','Shangyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330624,330600,'3','新昌县','','新昌','0575','312500','Xinchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330681,330600,'3','诸暨市','','诸暨','0575','311800','Zhuji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330683,330600,'3','嵊州市','','嵊州','0575','312400','Shengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330700,330000,'2','金华市','','金华','0579','321000','Jinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330702,330700,'3','婺城区','','婺城','0579','321000','Wucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330703,330700,'3','金东区','','金东','0579','321000','Jindong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330723,330700,'3','武义县','','武义','0579','321200','Wuyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330726,330700,'3','浦江县','','浦江','0579','322200','Pujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330727,330700,'3','磐安县','','磐安','0579','322300','Pan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330781,330700,'3','兰溪市','','兰溪','0579','321100','Lanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330782,330700,'3','义乌市','','义乌','0579','322000','Yiwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330783,330700,'3','东阳市','','东阳','0579','322100','Dongyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330784,330700,'3','永康市','','永康','0579','321300','Yongkang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330800,330000,'2','衢州市','','衢州','0570','324002','Quzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330802,330800,'3','柯城区','','柯城','0570','324100','Kecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330803,330800,'3','衢江区','','衢江','0570','324022','Qujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330822,330800,'3','常山县','','常山','0570','324200','Changshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330824,330800,'3','开化县','','开化','0570','324300','Kaihua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330825,330800,'3','龙游县','','龙游','0570','324400','Longyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330881,330800,'3','江山市','','江山','0570','324100','Jiangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330900,330000,'2','舟山市','','舟山','0580','316000','Zhoushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330902,330900,'3','定海区','','定海','0580','316000','Dinghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330903,330900,'3','普陀区','','普陀','0580','316100','Putuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330921,330900,'3','岱山县','','岱山','0580','316200','Daishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (330922,330900,'3','嵊泗县','','嵊泗','0580','202450','Shengsi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331000,330000,'2','台州市','','台州','0576','318000','Taizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331002,331000,'3','椒江区','','椒江','0576','318000','Jiaojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331003,331000,'3','黄岩区','','黄岩','0576','318020','Huangyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331004,331000,'3','路桥区','','路桥','0576','318050','Luqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331021,331000,'3','玉环县','','玉环','0576','317600','Yuhuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331022,331000,'3','三门县','','三门','0576','317100','Sanmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331023,331000,'3','天台县','','天台','0576','317200','Tiantai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331024,331000,'3','仙居县','','仙居','0576','317300','Xianju',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331081,331000,'3','温岭市','','温岭','0576','317500','Wenling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331082,331000,'3','临海市','','临海','0576','317000','Linhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331100,330000,'2','丽水市','','丽水','0578','323000','Lishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331102,331100,'3','莲都区','','莲都','0578','323000','Liandu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331121,331100,'3','青田县','','青田','0578','323900','Qingtian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331122,331100,'3','缙云县','','缙云','0578','321400','Jinyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331123,331100,'3','遂昌县','','遂昌','0578','323300','Suichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331124,331100,'3','松阳县','','松阳','0578','323400','Songyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331125,331100,'3','云和县','','云和','0578','323600','Yunhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331126,331100,'3','庆元县','','庆元','0578','323800','Qingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331127,331100,'3','景宁畲族自治县','','景宁','0578','323500','Jingning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331181,331100,'3','龙泉市','','龙泉','0578','323700','Longquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331200,330000,'2','舟山群岛新区','','舟山新区','0580','316000','Zhoushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331201,331200,'3','金塘岛','','金塘','0580','316000','Jintang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331202,331200,'3','六横岛','','六横','0580','316000','Liuheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331203,331200,'3','衢山岛','','衢山','0580','316000','Qushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331204,331200,'3','舟山本岛西北部','','舟山','0580','316000','Zhoushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331205,331200,'3','岱山岛西南部','','岱山','0580','316000','Daishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331206,331200,'3','泗礁岛','','泗礁','0580','316000','Sijiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331207,331200,'3','朱家尖岛','','朱家尖','0580','316000','Zhujiajian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331208,331200,'3','洋山岛','','洋山','0580','316000','Yangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331209,331200,'3','长涂岛','','长涂','0580','316000','Changtu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (331210,331200,'3','虾峙岛','','虾峙','0580','316000','Xiazhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340000,100000,'1','安徽省','','安徽','','','Anhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340100,340000,'2','合肥市','','合肥','0551','230001','Hefei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340102,340100,'3','瑶海区','','瑶海','0551','230011','Yaohai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340103,340100,'3','庐阳区','','庐阳','0551','230001','Luyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340104,340100,'3','蜀山区','','蜀山','0551','230031','Shushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340111,340100,'3','包河区','','包河','0551','230041','Baohe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340121,340100,'3','长丰县','','长丰','0551','231100','Changfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340122,340100,'3','肥东县','','肥东','0551','231600','Feidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340123,340100,'3','肥西县','','肥西','0551','231200','Feixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340124,340100,'3','庐江县','','庐江','0565','231500','Lujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340181,340100,'3','巢湖市','','巢湖','0565','238000','Chaohu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340200,340000,'2','芜湖市','','芜湖','0551','241000','Wuhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340202,340200,'3','镜湖区','','镜湖','0553','241000','Jinghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340203,340200,'3','弋江区','','弋江','0553','241000','Yijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340207,340200,'3','鸠江区','','鸠江','0553','241000','Jiujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340208,340200,'3','三山区','','三山','0553','241000','Sanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340221,340200,'3','芜湖县','','芜湖','0553','241100','Wuhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340222,340200,'3','繁昌县','','繁昌','0553','241200','Fanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340223,340200,'3','南陵县','','南陵','0553','242400','Nanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340225,340200,'3','无为县','','无为','0565','238300','Wuwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340300,340000,'2','蚌埠市','','蚌埠','0552','233000','Bengbu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340302,340300,'3','龙子湖区','','龙子湖','0552','233000','Longzihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340303,340300,'3','蚌山区','','蚌山','0552','233000','Bengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340304,340300,'3','禹会区','','禹会','0552','233010','Yuhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340311,340300,'3','淮上区','','淮上','0552','233002','Huaishang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340321,340300,'3','怀远县','','怀远','0552','233400','Huaiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340322,340300,'3','五河县','','五河','0552','233300','Wuhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340323,340300,'3','固镇县','','固镇','0552','233700','Guzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340400,340000,'2','淮南市','','淮南','0554','232001','Huainan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340402,340400,'3','大通区','','大通','0554','232033','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340403,340400,'3','田家庵区','','田家庵','0554','232000','Tianjiaan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340404,340400,'3','谢家集区','','谢家集','0554','232052','Xiejiaji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340405,340400,'3','八公山区','','八公山','0554','232072','Bagongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340406,340400,'3','潘集区','','潘集','0554','232082','Panji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340421,340400,'3','凤台县','','凤台','0554','232100','Fengtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340500,340000,'2','马鞍山市','','马鞍山','0555','243001','Ma anshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340503,340500,'3','花山区','','花山','0555','243000','Huashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340504,340500,'3','雨山区','','雨山','0555','243071','Yushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340506,340500,'3','博望区','','博望','0555','243131','Bowang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340521,340500,'3','当涂县','','当涂','0555','243100','Dangtu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340522,340500,'3','含山县','','含山','0555','238100','Hanshan ',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340523,340500,'3','和县','','和县','0555','238200','Hexian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340600,340000,'2','淮北市','','淮北','0561','235000','Huaibei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340602,340600,'3','杜集区','','杜集','0561','235000','Duji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340603,340600,'3','相山区','','相山','0561','235000','Xiangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340604,340600,'3','烈山区','','烈山','0561','235000','Lieshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340621,340600,'3','濉溪县','','濉溪','0561','235100','Suixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340700,340000,'2','铜陵市','','铜陵','0562','244000','Tongling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340702,340700,'3','铜官山区','','铜官山','0562','244000','Tongguanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340703,340700,'3','狮子山区','','狮子山','0562','244000','Shizishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340711,340700,'3','郊区','','郊区','0562','244000','Jiaoqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340721,340700,'3','铜陵县','','铜陵','0562','244100','Tongling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340800,340000,'2','安庆市','','安庆','0556','246001','Anqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340802,340800,'3','迎江区','','迎江','0556','246001','Yingjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340803,340800,'3','大观区','','大观','0556','246002','Daguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340811,340800,'3','宜秀区','','宜秀','0556','246003','Yixiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340822,340800,'3','怀宁县','','怀宁','0556','246100','Huaining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340823,340800,'3','枞阳县','','枞阳','0556','246700','Zongyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340824,340800,'3','潜山县','','潜山','0556','246300','Qianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340825,340800,'3','太湖县','','太湖','0556','246400','Taihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340826,340800,'3','宿松县','','宿松','0556','246500','Susong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340827,340800,'3','望江县','','望江','0556','246200','Wangjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340828,340800,'3','岳西县','','岳西','0556','246600','Yuexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (340881,340800,'3','桐城市','','桐城','0556','231400','Tongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341000,340000,'2','黄山市','','黄山','0559','245000','Huangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341002,341000,'3','屯溪区','','屯溪','0559','245000','Tunxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341003,341000,'3','黄山区','','黄山','0559','242700','Huangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341004,341000,'3','徽州区','','徽州','0559','245061','Huizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341021,341000,'3','歙县','','歙县','0559','245200','Shexian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341022,341000,'3','休宁县','','休宁','0559','245400','Xiuning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341023,341000,'3','黟县','','黟县','0559','245500','Yixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341024,341000,'3','祁门县','','祁门','0559','245600','Qimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341100,340000,'2','滁州市','','滁州','0550','239000','Chuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341102,341100,'3','琅琊区','','琅琊','0550','239000','Langya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341103,341100,'3','南谯区','','南谯','0550','239000','Nanqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341122,341100,'3','来安县','','来安','0550','239200','Lai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341124,341100,'3','全椒县','','全椒','0550','239500','Quanjiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341125,341100,'3','定远县','','定远','0550','233200','Dingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341126,341100,'3','凤阳县','','凤阳','0550','233100','Fengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341181,341100,'3','天长市','','天长','0550','239300','Tianchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341182,341100,'3','明光市','','明光','0550','239400','Mingguang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341200,340000,'2','阜阳市','','阜阳','0558','236033','Fuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341202,341200,'3','颍州区','','颍州','0558','236001','Yingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341203,341200,'3','颍东区','','颍东','0558','236058','Yingdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341204,341200,'3','颍泉区','','颍泉','0558','236045','Yingquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341221,341200,'3','临泉县','','临泉','0558','236400','Linquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341222,341200,'3','太和县','','太和','0558','236600','Taihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341225,341200,'3','阜南县','','阜南','0558','236300','Funan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341226,341200,'3','颍上县','','颍上','0558','236200','Yingshang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341282,341200,'3','界首市','','界首','0558','236500','Jieshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341300,340000,'2','宿州市','','宿州','0557','234000','Suzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341302,341300,'3','埇桥区','','埇桥','0557','234000','Yongqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341321,341300,'3','砀山县','','砀山','0557','235300','Dangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341322,341300,'3','萧县','','萧县','0557','235200','Xiaoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341323,341300,'3','灵璧县','','灵璧','0557','234200','Lingbi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341324,341300,'3','泗县','','泗县','0557','234300','Sixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341500,340000,'2','六安市','','六安','0564','237000','Lu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341502,341500,'3','金安区','','金安','0564','237005','Jin an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341503,341500,'3','裕安区','','裕安','0564','237010','Yu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341521,341500,'3','寿县','','寿县','0564','232200','Shouxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341522,341500,'3','霍邱县','','霍邱','0564','237400','Huoqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341523,341500,'3','舒城县','','舒城','0564','231300','Shucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341524,341500,'3','金寨县','','金寨','0564','237300','Jinzhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341525,341500,'3','霍山县','','霍山','0564','237200','Huoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341600,340000,'2','亳州市','','亳州','0558','236802','Bozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341602,341600,'3','谯城区','','谯城','0558','236800','Qiaocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341621,341600,'3','涡阳县','','涡阳','0558','233600','Guoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341622,341600,'3','蒙城县','','蒙城','0558','233500','Mengcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341623,341600,'3','利辛县','','利辛','0558','236700','Lixin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341700,340000,'2','池州市','','池州','0566','247100','Chizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341702,341700,'3','贵池区','','贵池','0566','247100','Guichi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341721,341700,'3','东至县','','东至','0566','247200','Dongzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341722,341700,'3','石台县','','石台','0566','245100','Shitai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341723,341700,'3','青阳县','','青阳','0566','242800','Qingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341800,340000,'2','宣城市','','宣城','0563','242000','Xuancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341802,341800,'3','宣州区','','宣州','0563','242000','Xuanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341821,341800,'3','郎溪县','','郎溪','0563','242100','Langxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341822,341800,'3','广德县','','广德','0563','242200','Guangde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341823,341800,'3','泾县','','泾县','0563','242500','Jingxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341824,341800,'3','绩溪县','','绩溪','0563','245300','Jixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341825,341800,'3','旌德县','','旌德','0563','242600','Jingde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (341881,341800,'3','宁国市','','宁国','0563','242300','Ningguo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350000,100000,'1','福建省','','福建','','','Fujian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350100,350000,'2','福州市','','福州','0591','350001','Fuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350102,350100,'3','鼓楼区','','鼓楼','0591','350001','Gulou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350103,350100,'3','台江区','','台江','0591','350004','Taijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350104,350100,'3','仓山区','','仓山','0591','350007','Cangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350105,350100,'3','马尾区','','马尾','0591','350015','Mawei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350111,350100,'3','晋安区','','晋安','0591','350011','Jin an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350121,350100,'3','闽侯县','','闽侯','0591','350100','Minhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350122,350100,'3','连江县','','连江','0591','350500','Lianjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350123,350100,'3','罗源县','','罗源','0591','350600','Luoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350124,350100,'3','闽清县','','闽清','0591','350800','Minqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350125,350100,'3','永泰县','','永泰','0591','350700','Yongtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350128,350100,'3','平潭县','','平潭','0591','350400','Pingtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350181,350100,'3','福清市','','福清','0591','350300','Fuqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350182,350100,'3','长乐市','','长乐','0591','350200','Changle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350200,350000,'2','厦门市','','厦门','0592','361003','Xiamen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350203,350200,'3','思明区','','思明','0592','361001','Siming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350205,350200,'3','海沧区','','海沧','0592','361026','Haicang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350206,350200,'3','湖里区','','湖里','0592','361006','Huli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350211,350200,'3','集美区','','集美','0592','361021','Jimei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350212,350200,'3','同安区','','同安','0592','361100','Tong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350213,350200,'3','翔安区','','翔安','0592','361101','Xiang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350300,350000,'2','莆田市','','莆田','0594','351100','Putian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350302,350300,'3','城厢区','','城厢','0594','351100','Chengxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350303,350300,'3','涵江区','','涵江','0594','351111','Hanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350304,350300,'3','荔城区','','荔城','0594','351100','Licheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350305,350300,'3','秀屿区','','秀屿','0594','351152','Xiuyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350322,350300,'3','仙游县','','仙游','0594','351200','Xianyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350400,350000,'2','三明市','','三明','0598','365000','Sanming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350402,350400,'3','梅列区','','梅列','0598','365000','Meilie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350403,350400,'3','三元区','','三元','0598','365001','Sanyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350421,350400,'3','明溪县','','明溪','0598','365200','Mingxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350423,350400,'3','清流县','','清流','0598','365300','Qingliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350424,350400,'3','宁化县','','宁化','0598','365400','Ninghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350425,350400,'3','大田县','','大田','0598','366100','Datian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350426,350400,'3','尤溪县','','尤溪','0598','365100','Youxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350427,350400,'3','沙县','','沙县','0598','365500','Shaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350428,350400,'3','将乐县','','将乐','0598','353300','Jiangle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350429,350400,'3','泰宁县','','泰宁','0598','354400','Taining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350430,350400,'3','建宁县','','建宁','0598','354500','Jianning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350481,350400,'3','永安市','','永安','0598','366000','Yong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350500,350000,'2','泉州市','','泉州','0595','362000','Quanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350502,350500,'3','鲤城区','','鲤城','0595','362000','Licheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350503,350500,'3','丰泽区','','丰泽','0595','362000','Fengze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350504,350500,'3','洛江区','','洛江','0595','362011','Luojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350505,350500,'3','泉港区','','泉港','0595','362114','Quangang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350521,350500,'3','惠安县','','惠安','0595','362100','Hui an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350524,350500,'3','安溪县','','安溪','0595','362400','Anxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350525,350500,'3','永春县','','永春','0595','362600','Yongchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350526,350500,'3','德化县','','德化','0595','362500','Dehua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350527,350500,'3','金门县','','金门','','','Jinmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350581,350500,'3','石狮市','','石狮','0595','362700','Shishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350582,350500,'3','晋江市','','晋江','0595','362200','Jinjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350583,350500,'3','南安市','','南安','0595','362300','Nan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350600,350000,'2','漳州市','','漳州','0596','363005','Zhangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350602,350600,'3','芗城区','','芗城','0596','363000','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350603,350600,'3','龙文区','','龙文','0596','363005','Longwen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350622,350600,'3','云霄县','','云霄','0596','363300','Yunxiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350623,350600,'3','漳浦县','','漳浦','0596','363200','Zhangpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350624,350600,'3','诏安县','','诏安','0596','363500','Zhao an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350625,350600,'3','长泰县','','长泰','0596','363900','Changtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350626,350600,'3','东山县','','东山','0596','363400','Dongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350627,350600,'3','南靖县','','南靖','0596','363600','Nanjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350628,350600,'3','平和县','','平和','0596','363700','Pinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350629,350600,'3','华安县','','华安','0596','363800','Hua an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350681,350600,'3','龙海市','','龙海','0596','363100','Longhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350700,350000,'2','南平市','','南平','0599','353000','Nanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350702,350700,'3','延平区','','延平','0600','353000','Yanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350703,350700,'3','建阳区','','建阳','0599','354200','Jianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350721,350700,'3','顺昌县','','顺昌','0605','353200','Shunchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350722,350700,'3','浦城县','','浦城','0606','353400','Pucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350723,350700,'3','光泽县','','光泽','0607','354100','Guangze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350724,350700,'3','松溪县','','松溪','0608','353500','Songxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350725,350700,'3','政和县','','政和','0609','353600','Zhenghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350781,350700,'3','邵武市','','邵武','0601','354000','Shaowu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350782,350700,'3','武夷山市','','武夷山','0602','354300','Wuyishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350783,350700,'3','建瓯市','','建瓯','0603','353100','Jianou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350800,350000,'2','龙岩市','','龙岩','0597','364000','Longyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350802,350800,'3','新罗区','','新罗','0597','364000','Xinluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350821,350800,'3','长汀县','','长汀','0597','366300','Changting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350822,350800,'3','永定区','','永定','0597','364100','Yongding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350823,350800,'3','上杭县','','上杭','0597','364200','Shanghang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350824,350800,'3','武平县','','武平','0597','364300','Wuping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350825,350800,'3','连城县','','连城','0597','366200','Liancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350881,350800,'3','漳平市','','漳平','0597','364400','Zhangping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350900,350000,'2','宁德市','','宁德','0593','352100','Ningde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350902,350900,'3','蕉城区','','蕉城','0593','352100','Jiaocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350921,350900,'3','霞浦县','','霞浦','0593','355100','Xiapu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350922,350900,'3','古田县','','古田','0593','352200','Gutian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350923,350900,'3','屏南县','','屏南','0593','352300','Pingnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350924,350900,'3','寿宁县','','寿宁','0593','355500','Shouning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350925,350900,'3','周宁县','','周宁','0593','355400','Zhouning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350926,350900,'3','柘荣县','','柘荣','0593','355300','Zherong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350981,350900,'3','福安市','','福安','0593','355000','Fu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (350982,350900,'3','福鼎市','','福鼎','0593','355200','Fuding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360000,100000,'1','江西省','','江西','','','Jiangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360100,360000,'2','南昌市','','南昌','0791','330008','Nanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360102,360100,'3','东湖区','','东湖','0791','330006','Donghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360103,360100,'3','西湖区','','西湖','0791','330009','Xihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360104,360100,'3','青云谱区','','青云谱','0791','330001','Qingyunpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360105,360100,'3','湾里区','','湾里','0791','330004','Wanli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360111,360100,'3','青山湖区','','青山湖','0791','330029','Qingshanhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360121,360100,'3','南昌县','','南昌','0791','330200','Nanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360122,360100,'3','新建县','','新建','0791','330100','Xinjian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360123,360100,'3','安义县','','安义','0791','330500','Anyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360124,360100,'3','进贤县','','进贤','0791','331700','Jinxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360200,360000,'2','景德镇市','','景德镇','0798','333000','Jingdezhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360202,360200,'3','昌江区','','昌江','0799','333000','Changjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360203,360200,'3','珠山区','','珠山','0800','333000','Zhushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360222,360200,'3','浮梁县','','浮梁','0802','333400','Fuliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360281,360200,'3','乐平市','','乐平','0801','333300','Leping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360300,360000,'2','萍乡市','','萍乡','0799','337000','Pingxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360302,360300,'3','安源区','','安源','0800','337000','Anyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360313,360300,'3','湘东区','','湘东','0801','337016','Xiangdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360321,360300,'3','莲花县','','莲花','0802','337100','Lianhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360322,360300,'3','上栗县','','上栗','0803','337009','Shangli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360323,360300,'3','芦溪县','','芦溪','0804','337053','Luxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360400,360000,'2','九江市','','九江','0792','332000','Jiujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360402,360400,'3','庐山区','','庐山','0792','332005','Lushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360403,360400,'3','浔阳区','','浔阳','0792','332000','Xunyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360421,360400,'3','九江县','','九江','0792','332100','Jiujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360423,360400,'3','武宁县','','武宁','0792','332300','Wuning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360424,360400,'3','修水县','','修水','0792','332400','Xiushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360425,360400,'3','永修县','','永修','0792','330300','Yongxiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360426,360400,'3','德安县','','德安','0792','330400','De an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360427,360400,'3','星子县','','星子','0792','332800','Xingzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360428,360400,'3','都昌县','','都昌','0792','332600','Duchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360429,360400,'3','湖口县','','湖口','0792','332500','Hukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360430,360400,'3','彭泽县','','彭泽','0792','332700','Pengze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360481,360400,'3','瑞昌市','','瑞昌','0792','332200','Ruichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360482,360400,'3','共青城市','','共青城','0792','332020','Gongqingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360500,360000,'2','新余市','','新余','0790','338025','Xinyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360502,360500,'3','渝水区','','渝水','0790','338025','Yushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360521,360500,'3','分宜县','','分宜','0790','336600','Fenyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360600,360000,'2','鹰潭市','','鹰潭','0701','335000','Yingtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360602,360600,'3','月湖区','','月湖','0701','335000','Yuehu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360622,360600,'3','余江县','','余江','0701','335200','Yujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360681,360600,'3','贵溪市','','贵溪','0701','335400','Guixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360700,360000,'2','赣州市','','赣州','0797','341000','Ganzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360702,360700,'3','章贡区','','章贡','0797','341000','Zhanggong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360703,360700,'3','南康区','','南康','0797','341400','Nankang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360721,360700,'3','赣县','','赣县','0797','341100','Ganxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360722,360700,'3','信丰县','','信丰','0797','341600','Xinfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360723,360700,'3','大余县','','大余','0797','341500','Dayu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360724,360700,'3','上犹县','','上犹','0797','341200','Shangyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360725,360700,'3','崇义县','','崇义','0797','341300','Chongyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360726,360700,'3','安远县','','安远','0797','342100','Anyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360727,360700,'3','龙南县','','龙南','0797','341700','Longnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360728,360700,'3','定南县','','定南','0797','341900','Dingnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360729,360700,'3','全南县','','全南','0797','341800','Quannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360730,360700,'3','宁都县','','宁都','0797','342800','Ningdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360731,360700,'3','于都县','','于都','0797','342300','Yudu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360732,360700,'3','兴国县','','兴国','0797','342400','Xingguo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360733,360700,'3','会昌县','','会昌','0797','342600','Huichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360734,360700,'3','寻乌县','','寻乌','0797','342200','Xunwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360735,360700,'3','石城县','','石城','0797','342700','Shicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360781,360700,'3','瑞金市','','瑞金','0797','342500','Ruijin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360800,360000,'2','吉安市','','吉安','0796','343000','Ji an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360802,360800,'3','吉州区','','吉州','0796','343000','Jizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360803,360800,'3','青原区','','青原','0796','343009','Qingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360821,360800,'3','吉安县','','吉安','0796','343100','Ji an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360822,360800,'3','吉水县','','吉水','0796','331600','Jishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360823,360800,'3','峡江县','','峡江','0796','331409','Xiajiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360824,360800,'3','新干县','','新干','0796','331300','Xingan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360825,360800,'3','永丰县','','永丰','0796','331500','Yongfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360826,360800,'3','泰和县','','泰和','0796','343700','Taihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360827,360800,'3','遂川县','','遂川','0796','343900','Suichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360828,360800,'3','万安县','','万安','0796','343800','Wan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360829,360800,'3','安福县','','安福','0796','343200','Anfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360830,360800,'3','永新县','','永新','0796','343400','Yongxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360881,360800,'3','井冈山市','','井冈山','0796','343600','Jinggangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360900,360000,'2','宜春市','','宜春','0795','336000','Yichun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360902,360900,'3','袁州区','','袁州','0795','336000','Yuanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360921,360900,'3','奉新县','','奉新','0795','330700','Fengxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360922,360900,'3','万载县','','万载','0795','336100','Wanzai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360923,360900,'3','上高县','','上高','0795','336400','Shanggao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360924,360900,'3','宜丰县','','宜丰','0795','336300','Yifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360925,360900,'3','靖安县','','靖安','0795','330600','Jing an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360926,360900,'3','铜鼓县','','铜鼓','0795','336200','Tonggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360981,360900,'3','丰城市','','丰城','0795','331100','Fengcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360982,360900,'3','樟树市','','樟树','0795','331200','Zhangshu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (360983,360900,'3','高安市','','高安','0795','330800','Gao an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361000,360000,'2','抚州市','','抚州','0794','344000','Fuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361002,361000,'3','临川区','','临川','0794','344000','Linchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361021,361000,'3','南城县','','南城','0794','344700','Nancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361022,361000,'3','黎川县','','黎川','0794','344600','Lichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361023,361000,'3','南丰县','','南丰','0794','344500','Nanfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361024,361000,'3','崇仁县','','崇仁','0794','344200','Chongren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361025,361000,'3','乐安县','','乐安','0794','344300','Le an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361026,361000,'3','宜黄县','','宜黄','0794','344400','Yihuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361027,361000,'3','金溪县','','金溪','0794','344800','Jinxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361028,361000,'3','资溪县','','资溪','0794','335300','Zixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361029,361000,'3','东乡县','','东乡','0794','331800','Dongxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361030,361000,'3','广昌县','','广昌','0794','344900','Guangchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361100,360000,'2','上饶市','','上饶','0793','334000','Shangrao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361102,361100,'3','信州区','','信州','0793','334000','Xinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361121,361100,'3','上饶县','','上饶','0793','334100','Shangrao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361122,361100,'3','广丰县','','广丰','0793','334600','Guangfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361123,361100,'3','玉山县','','玉山','0793','334700','Yushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361124,361100,'3','铅山县','','铅山','0793','334500','Yanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361125,361100,'3','横峰县','','横峰','0793','334300','Hengfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361126,361100,'3','弋阳县','','弋阳','0793','334400','Yiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361127,361100,'3','余干县','','余干','0793','335100','Yugan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361128,361100,'3','鄱阳县','','鄱阳','0793','333100','Poyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361129,361100,'3','万年县','','万年','0793','335500','Wannian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361130,361100,'3','婺源县','','婺源','0793','333200','Wuyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (361181,361100,'3','德兴市','','德兴','0793','334200','Dexing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370000,100000,'1','山东省','','山东','','','Shandong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370100,370000,'2','济南市','','济南','0531','250001','Jinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370102,370100,'3','历下区','','历下','0531','250014','Lixia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370103,370100,'3','市中区','','市中区','0531','250001','Shizhongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370104,370100,'3','槐荫区','','槐荫','0531','250117','Huaiyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370105,370100,'3','天桥区','','天桥','0531','250031','Tianqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370112,370100,'3','历城区','','历城','0531','250100','Licheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370113,370100,'3','长清区','','长清','0531','250300','Changqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370124,370100,'3','平阴县','','平阴','0531','250400','Pingyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370125,370100,'3','济阳县','','济阳','0531','251400','Jiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370126,370100,'3','商河县','','商河','0531','251600','Shanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370181,370100,'3','章丘市','','章丘','0531','250200','Zhangqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370200,370000,'2','青岛市','','青岛','0532','266001','Qingdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370202,370200,'3','市南区','','市南','0532','266001','Shinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370203,370200,'3','市北区','','市北','0532','266011','Shibei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370211,370200,'3','黄岛区','','黄岛','0532','266500','Huangdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370212,370200,'3','崂山区','','崂山','0532','266100','Laoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370213,370200,'3','李沧区','','李沧','0532','266021','Licang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370214,370200,'3','城阳区','','城阳','0532','266041','Chengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370281,370200,'3','胶州市','','胶州','0532','266300','Jiaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370282,370200,'3','即墨市','','即墨','0532','266200','Jimo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370283,370200,'3','平度市','','平度','0532','266700','Pingdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370285,370200,'3','莱西市','','莱西','0532','266600','Laixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370286,370200,'3','西海岸新区','','西海岸','0532','266500','Xihai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370300,370000,'2','淄博市','','淄博','0533','255039','Zibo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370302,370300,'3','淄川区','','淄川','0533','255100','Zichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370303,370300,'3','张店区','','张店','0533','255022','Zhangdian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370304,370300,'3','博山区','','博山','0533','255200','Boshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370305,370300,'3','临淄区','','临淄','0533','255400','Linzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370306,370300,'3','周村区','','周村','0533','255300','Zhoucun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370321,370300,'3','桓台县','','桓台','0533','256400','Huantai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370322,370300,'3','高青县','','高青','0533','256300','Gaoqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370323,370300,'3','沂源县','','沂源','0533','256100','Yiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370400,370000,'2','枣庄市','','枣庄','0632','277101','Zaozhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370402,370400,'3','市中区','','市中区','0632','277101','Shizhongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370403,370400,'3','薛城区','','薛城','0632','277000','Xuecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370404,370400,'3','峄城区','','峄城','0632','277300','Yicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370405,370400,'3','台儿庄区','','台儿庄','0632','277400','Taierzhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370406,370400,'3','山亭区','','山亭','0632','277200','Shanting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370481,370400,'3','滕州市','','滕州','0632','277500','Tengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370500,370000,'2','东营市','','东营','0546','257093','Dongying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370502,370500,'3','东营区','','东营','0546','257029','Dongying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370503,370500,'3','河口区','','河口','0546','257200','Hekou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370521,370500,'3','垦利县','','垦利','0546','257500','Kenli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370522,370500,'3','利津县','','利津','0546','257400','Lijin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370523,370500,'3','广饶县','','广饶','0546','257300','Guangrao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370600,370000,'2','烟台市','','烟台','0635','264010','Yantai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370602,370600,'3','芝罘区','','芝罘','0635','264001','Zhifu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370611,370600,'3','福山区','','福山','0635','265500','Fushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370612,370600,'3','牟平区','','牟平','0635','264100','Muping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370613,370600,'3','莱山区','','莱山','0635','264600','Laishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370634,370600,'3','长岛县','','长岛','0635','265800','Changdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370681,370600,'3','龙口市','','龙口','0635','265700','Longkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370682,370600,'3','莱阳市','','莱阳','0635','265200','Laiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370683,370600,'3','莱州市','','莱州','0635','261400','Laizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370684,370600,'3','蓬莱市','','蓬莱','0635','265600','Penglai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370685,370600,'3','招远市','','招远','0635','265400','Zhaoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370686,370600,'3','栖霞市','','栖霞','0635','265300','Qixia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370687,370600,'3','海阳市','','海阳','0635','265100','Haiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370700,370000,'2','潍坊市','','潍坊','0536','261041','Weifang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370702,370700,'3','潍城区','','潍城','0536','261021','Weicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370703,370700,'3','寒亭区','','寒亭','0536','261100','Hanting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370704,370700,'3','坊子区','','坊子','0536','261200','Fangzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370705,370700,'3','奎文区','','奎文','0536','261031','Kuiwen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370724,370700,'3','临朐县','','临朐','0536','262600','Linqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370725,370700,'3','昌乐县','','昌乐','0536','262400','Changle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370781,370700,'3','青州市','','青州','0536','262500','Qingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370782,370700,'3','诸城市','','诸城','0536','262200','Zhucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370783,370700,'3','寿光市','','寿光','0536','262700','Shouguang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370784,370700,'3','安丘市','','安丘','0536','262100','Anqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370785,370700,'3','高密市','','高密','0536','261500','Gaomi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370786,370700,'3','昌邑市','','昌邑','0536','261300','Changyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370800,370000,'2','济宁市','','济宁','0537','272119','Jining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370811,370800,'3','任城区','','任城','0537','272113','Rencheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370812,370800,'3','兖州区','','兖州','0537','272000','Yanzhou ',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370826,370800,'3','微山县','','微山','0537','277600','Weishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370827,370800,'3','鱼台县','','鱼台','0537','272300','Yutai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370828,370800,'3','金乡县','','金乡','0537','272200','Jinxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370829,370800,'3','嘉祥县','','嘉祥','0537','272400','Jiaxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370830,370800,'3','汶上县','','汶上','0537','272501','Wenshang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370831,370800,'3','泗水县','','泗水','0537','273200','Sishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370832,370800,'3','梁山县','','梁山','0537','272600','Liangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370881,370800,'3','曲阜市','','曲阜','0537','273100','Qufu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370883,370800,'3','邹城市','','邹城','0537','273500','Zoucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370900,370000,'2','泰安市','','泰安','0538','271000','Tai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370902,370900,'3','泰山区','','泰山','0538','271000','Taishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370911,370900,'3','岱岳区','','岱岳','0538','271000','Daiyue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370921,370900,'3','宁阳县','','宁阳','0538','271400','Ningyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370923,370900,'3','东平县','','东平','0538','271500','Dongping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370982,370900,'3','新泰市','','新泰','0538','271200','Xintai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (370983,370900,'3','肥城市','','肥城','0538','271600','Feicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371000,370000,'2','威海市','','威海','0631','264200','Weihai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371002,371000,'3','环翠区','','环翠','0631','264200','Huancui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371003,371000,'3','文登区','','文登','0631','266440','Wendeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371082,371000,'3','荣成市','','荣成','0631','264300','Rongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371083,371000,'3','乳山市','','乳山','0631','264500','Rushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371100,370000,'2','日照市','','日照','0633','276800','Rizhao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371102,371100,'3','东港区','','东港','0633','276800','Donggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371103,371100,'3','岚山区','','岚山','0633','276808','Lanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371121,371100,'3','五莲县','','五莲','0633','262300','Wulian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371122,371100,'3','莒县','','莒县','0633','276500','Juxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371200,370000,'2','莱芜市','','莱芜','0634','271100','Laiwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371202,371200,'3','莱城区','','莱城','0634','271199','Laicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371203,371200,'3','钢城区','','钢城','0634','271100','Gangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371300,370000,'2','临沂市','','临沂','0539','253000','Linyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371302,371300,'3','兰山区','','兰山','0539','276002','Lanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371311,371300,'3','罗庄区','','罗庄','0539','276022','Luozhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371312,371300,'3','河东区','','河东','0539','276034','Hedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371321,371300,'3','沂南县','','沂南','0539','276300','Yinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371322,371300,'3','郯城县','','郯城','0539','276100','Tancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371323,371300,'3','沂水县','','沂水','0539','276400','Yishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371324,371300,'3','兰陵县','','兰陵','0539','277700','Lanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371325,371300,'3','费县','','费县','0539','273400','Feixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371326,371300,'3','平邑县','','平邑','0539','273300','Pingyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371327,371300,'3','莒南县','','莒南','0539','276600','Junan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371328,371300,'3','蒙阴县','','蒙阴','0539','276200','Mengyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371329,371300,'3','临沭县','','临沭','0539','276700','Linshu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371400,370000,'2','德州市','','德州','0534','253000','Dezhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371402,371400,'3','德城区','','德城','0534','253012','Decheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371403,371400,'3','陵城区','','陵城','0534','253500','Lingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371422,371400,'3','宁津县','','宁津','0534','253400','Ningjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371423,371400,'3','庆云县','','庆云','0534','253700','Qingyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371424,371400,'3','临邑县','','临邑','0534','251500','Linyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371425,371400,'3','齐河县','','齐河','0534','251100','Qihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371426,371400,'3','平原县','','平原','0534','253100','Pingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371427,371400,'3','夏津县','','夏津','0534','253200','Xiajin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371428,371400,'3','武城县','','武城','0534','253300','Wucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371481,371400,'3','乐陵市','','乐陵','0534','253600','Leling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371482,371400,'3','禹城市','','禹城','0534','251200','Yucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371500,370000,'2','聊城市','','聊城','0635','252052','Liaocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371502,371500,'3','东昌府区','','东昌府','0635','252000','Dongchangfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371521,371500,'3','阳谷县','','阳谷','0635','252300','Yanggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371522,371500,'3','莘县','','莘县','0635','252400','Shenxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371523,371500,'3','茌平县','','茌平','0635','252100','Chiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371524,371500,'3','东阿县','','东阿','0635','252200','Dong e',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371525,371500,'3','冠县','','冠县','0635','252500','Guanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371526,371500,'3','高唐县','','高唐','0635','252800','Gaotang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371581,371500,'3','临清市','','临清','0635','252600','Linqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371600,370000,'2','滨州市','','滨州','0543','256619','Binzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371602,371600,'3','滨城区','','滨城','0543','256613','Bincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371603,371600,'3','沾化区','','沾化','0543','256800','Zhanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371621,371600,'3','惠民县','','惠民','0543','251700','Huimin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371622,371600,'3','阳信县','','阳信','0543','251800','Yangxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371623,371600,'3','无棣县','','无棣','0543','251900','Wudi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371625,371600,'3','博兴县','','博兴','0543','256500','Boxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371626,371600,'3','邹平县','','邹平','0543','256200','Zouping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371627,371600,'3','北海新区','','北海新区','0543','256200','Beihaixinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371700,370000,'2','菏泽市','','菏泽','0530','274020','Heze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371702,371700,'3','牡丹区','','牡丹','0530','274009','Mudan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371721,371700,'3','曹县','','曹县','0530','274400','Caoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371722,371700,'3','单县','','单县','0530','273700','Shanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371723,371700,'3','成武县','','成武','0530','274200','Chengwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371724,371700,'3','巨野县','','巨野','0530','274900','Juye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371725,371700,'3','郓城县','','郓城','0530','274700','Yuncheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371726,371700,'3','鄄城县','','鄄城','0530','274600','Juancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371727,371700,'3','定陶县','','定陶','0530','274100','Dingtao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (371728,371700,'3','东明县','','东明','0530','274500','Dongming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410000,100000,'1','河南省','','河南','','','Henan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410100,410000,'2','郑州市','','郑州','0371','450000','Zhengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410102,410100,'3','中原区','','中原','0371','450007','Zhongyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410103,410100,'3','二七区','','二七','0371','450052','Erqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410104,410100,'3','管城回族区','','管城','0371','450000','Guancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410105,410100,'3','金水区','','金水','0371','450003','Jinshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410106,410100,'3','上街区','','上街','0371','450041','Shangjie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410108,410100,'3','惠济区','','惠济','0371','450053','Huiji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410122,410100,'3','中牟县','','中牟','0371','451450','Zhongmu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410181,410100,'3','巩义市','','巩义','0371','451200','Gongyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410182,410100,'3','荥阳市','','荥阳','0371','450100','Xingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410183,410100,'3','新密市','','新密','0371','452300','Xinmi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410184,410100,'3','新郑市','','新郑','0371','451100','Xinzheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410185,410100,'3','登封市','','登封','0371','452470','Dengfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410200,410000,'2','开封市','','开封','0378','475001','Kaifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410202,410200,'3','龙亭区','','龙亭','0378','475100','Longting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410203,410200,'3','顺河回族区','','顺河','0378','475000','Shunhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410204,410200,'3','鼓楼区','','鼓楼','0378','475000','Gulou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410205,410200,'3','禹王台区','','禹王台','0378','475003','Yuwangtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410212,410200,'3','祥符区','','祥符','0378','475100','Xiangfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410221,410200,'3','杞县','','杞县','0378','475200','Qixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410222,410200,'3','通许县','','通许','0378','475400','Tongxu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410223,410200,'3','尉氏县','','尉氏','0378','475500','Weishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410225,410200,'3','兰考县','','兰考','0378','475300','Lankao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410300,410000,'2','洛阳市','','洛阳','0379','471000','Luoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410302,410300,'3','老城区','','老城','0379','471002','Laocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410303,410300,'3','西工区','','西工','0379','471000','Xigong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410304,410300,'3','瀍河回族区','','瀍河','0379','471002','Chanhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410305,410300,'3','涧西区','','涧西','0379','471003','Jianxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410306,410300,'3','吉利区','','吉利','0379','471012','Jili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410311,410300,'3','洛龙区','','洛龙','0379','471000','Luolong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410322,410300,'3','孟津县','','孟津','0379','471100','Mengjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410323,410300,'3','新安县','','新安','0379','471800','Xin an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410324,410300,'3','栾川县','','栾川','0379','471500','Luanchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410325,410300,'3','嵩县','','嵩县','0379','471400','Songxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410326,410300,'3','汝阳县','','汝阳','0379','471200','Ruyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410327,410300,'3','宜阳县','','宜阳','0379','471600','Yiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410328,410300,'3','洛宁县','','洛宁','0379','471700','Luoning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410329,410300,'3','伊川县','','伊川','0379','471300','Yichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410381,410300,'3','偃师市','','偃师','0379','471900','Yanshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410400,410000,'2','平顶山市','','平顶山','0375','467000','Pingdingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410402,410400,'3','新华区','','新华','0375','467002','Xinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410403,410400,'3','卫东区','','卫东','0375','467021','Weidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410404,410400,'3','石龙区','','石龙','0375','467045','Shilong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410411,410400,'3','湛河区','','湛河','0375','467000','Zhanhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410421,410400,'3','宝丰县','','宝丰','0375','467400','Baofeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410422,410400,'3','叶县','','叶县','0375','467200','Yexian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410423,410400,'3','鲁山县','','鲁山','0375','467300','Lushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410425,410400,'3','郏县','','郏县','0375','467100','Jiaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410481,410400,'3','舞钢市','','舞钢','0375','462500','Wugang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410482,410400,'3','汝州市','','汝州','0375','467500','Ruzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410500,410000,'2','安阳市','','安阳','0372','455000','Anyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410502,410500,'3','文峰区','','文峰','0372','455000','Wenfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410503,410500,'3','北关区','','北关','0372','455001','Beiguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410505,410500,'3','殷都区','','殷都','0372','455004','Yindu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410506,410500,'3','龙安区','','龙安','0372','455001','Long an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410522,410500,'3','安阳县','','安阳','0372','455000','Anyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410523,410500,'3','汤阴县','','汤阴','0372','456150','Tangyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410526,410500,'3','滑县','','滑县','0372','456400','Huaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410527,410500,'3','内黄县','','内黄','0372','456350','Neihuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410581,410500,'3','林州市','','林州','0372','456550','Linzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410600,410000,'2','鹤壁市','','鹤壁','0392','458030','Hebi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410602,410600,'3','鹤山区','','鹤山','0392','458010','Heshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410603,410600,'3','山城区','','山城','0392','458000','Shancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410611,410600,'3','淇滨区','','淇滨','0392','458000','Qibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410621,410600,'3','浚县','','浚县','0392','456250','Xunxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410622,410600,'3','淇县','','淇县','0392','456750','Qixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410700,410000,'2','新乡市','','新乡','0373','453000','Xinxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410702,410700,'3','红旗区','','红旗','0373','453000','Hongqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410703,410700,'3','卫滨区','','卫滨','0373','453000','Weibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410704,410700,'3','凤泉区','','凤泉','0373','453011','Fengquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410711,410700,'3','牧野区','','牧野','0373','453002','Muye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410721,410700,'3','新乡县','','新乡','0373','453700','Xinxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410724,410700,'3','获嘉县','','获嘉','0373','453800','Huojia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410725,410700,'3','原阳县','','原阳','0373','453500','Yuanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410726,410700,'3','延津县','','延津','0373','453200','Yanjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410727,410700,'3','封丘县','','封丘','0373','453300','Fengqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410728,410700,'3','长垣县','','长垣','0373','453400','Changyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410781,410700,'3','卫辉市','','卫辉','0373','453100','Weihui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410782,410700,'3','辉县市','','辉县','0373','453600','Huixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410800,410000,'2','焦作市','','焦作','0391','454002','Jiaozuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410802,410800,'3','解放区','','解放','0391','454000','Jiefang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410803,410800,'3','中站区','','中站','0391','454191','Zhongzhan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410804,410800,'3','马村区','','马村','0391','454171','Macun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410811,410800,'3','山阳区','','山阳','0391','454002','Shanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410821,410800,'3','修武县','','修武','0391','454350','Xiuwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410822,410800,'3','博爱县','','博爱','0391','454450','Boai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410823,410800,'3','武陟县','','武陟','0391','454950','Wuzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410825,410800,'3','温县','','温县','0391','454850','Wenxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410882,410800,'3','沁阳市','','沁阳','0391','454550','Qinyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410883,410800,'3','孟州市','','孟州','0391','454750','Mengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410900,410000,'2','濮阳市','','濮阳','0393','457000','Puyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410902,410900,'3','华龙区','','华龙','0393','457001','Hualong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410922,410900,'3','清丰县','','清丰','0393','457300','Qingfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410923,410900,'3','南乐县','','南乐','0393','457400','Nanle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410926,410900,'3','范县','','范县','0393','457500','Fanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410927,410900,'3','台前县','','台前','0393','457600','Taiqian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (410928,410900,'3','濮阳县','','濮阳','0393','457100','Puyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411000,410000,'2','许昌市','','许昌','0374','461000','Xuchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411002,411000,'3','魏都区','','魏都','0374','461000','Weidu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411023,411000,'3','许昌县','','许昌','0374','461100','Xuchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411024,411000,'3','鄢陵县','','鄢陵','0374','461200','Yanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411025,411000,'3','襄城县','','襄城','0374','461700','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411081,411000,'3','禹州市','','禹州','0374','461670','Yuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411082,411000,'3','长葛市','','长葛','0374','461500','Changge',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411100,410000,'2','漯河市','','漯河','0395','462000','Luohe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411102,411100,'3','源汇区','','源汇','0395','462000','Yuanhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411103,411100,'3','郾城区','','郾城','0395','462300','Yancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411104,411100,'3','召陵区','','召陵','0395','462300','Zhaoling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411121,411100,'3','舞阳县','','舞阳','0395','462400','Wuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411122,411100,'3','临颍县','','临颍','0395','462600','Linying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411200,410000,'2','三门峡市','','三门峡','0398','472000','Sanmenxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411202,411200,'3','湖滨区','','湖滨','0398','472000','Hubin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411221,411200,'3','渑池县','','渑池','0398','472400','Mianchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411222,411200,'3','陕县','','陕县','0398','472100','Shanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411224,411200,'3','卢氏县','','卢氏','0398','472200','Lushi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411281,411200,'3','义马市','','义马','0398','472300','Yima',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411282,411200,'3','灵宝市','','灵宝','0398','472500','Lingbao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411300,410000,'2','南阳市','','南阳','0377','473002','Nanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411302,411300,'3','宛城区','','宛城','0377','473001','Wancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411303,411300,'3','卧龙区','','卧龙','0377','473003','Wolong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411321,411300,'3','南召县','','南召','0377','474650','Nanzhao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411322,411300,'3','方城县','','方城','0377','473200','Fangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411323,411300,'3','西峡县','','西峡','0377','474550','Xixia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411324,411300,'3','镇平县','','镇平','0377','474250','Zhenping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411325,411300,'3','内乡县','','内乡','0377','474350','Neixiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411326,411300,'3','淅川县','','淅川','0377','474450','Xichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411327,411300,'3','社旗县','','社旗','0377','473300','Sheqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411328,411300,'3','唐河县','','唐河','0377','473400','Tanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411329,411300,'3','新野县','','新野','0377','473500','Xinye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411330,411300,'3','桐柏县','','桐柏','0377','474750','Tongbai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411381,411300,'3','邓州市','','邓州','0377','474150','Dengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411400,410000,'2','商丘市','','商丘','0370','476000','Shangqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411402,411400,'3','梁园区','','梁园','0370','476000','Liangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411403,411400,'3','睢阳区','','睢阳','0370','476100','Suiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411421,411400,'3','民权县','','民权','0370','476800','Minquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411422,411400,'3','睢县','','睢县','0370','476900','Suixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411423,411400,'3','宁陵县','','宁陵','0370','476700','Ningling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411424,411400,'3','柘城县','','柘城','0370','476200','Zhecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411425,411400,'3','虞城县','','虞城','0370','476300','Yucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411426,411400,'3','夏邑县','','夏邑','0370','476400','Xiayi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411481,411400,'3','永城市','','永城','0370','476600','Yongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411500,410000,'2','信阳市','','信阳','0376','464000','Xinyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411502,411500,'3','浉河区','','浉河','0376','464000','Shihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411503,411500,'3','平桥区','','平桥','0376','464100','Pingqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411521,411500,'3','罗山县','','罗山','0376','464200','Luoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411522,411500,'3','光山县','','光山','0376','465450','Guangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411523,411500,'3','新县','','新县','0376','465550','Xinxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411524,411500,'3','商城县','','商城','0376','465350','Shangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411525,411500,'3','固始县','','固始','0376','465250','Gushi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411526,411500,'3','潢川县','','潢川','0376','465150','Huangchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411527,411500,'3','淮滨县','','淮滨','0376','464400','Huaibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411528,411500,'3','息县','','息县','0376','464300','Xixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411600,410000,'2','周口市','','周口','0394','466000','Zhoukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411602,411600,'3','川汇区','','川汇','0394','466000','Chuanhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411621,411600,'3','扶沟县','','扶沟','0394','461300','Fugou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411622,411600,'3','西华县','','西华','0394','466600','Xihua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411623,411600,'3','商水县','','商水','0394','466100','Shangshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411624,411600,'3','沈丘县','','沈丘','0394','466300','Shenqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411625,411600,'3','郸城县','','郸城','0394','477150','Dancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411626,411600,'3','淮阳县','','淮阳','0394','466700','Huaiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411627,411600,'3','太康县','','太康','0394','461400','Taikang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411628,411600,'3','鹿邑县','','鹿邑','0394','477200','Luyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411681,411600,'3','项城市','','项城','0394','466200','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411700,410000,'2','驻马店市','','驻马店','0396','463000','Zhumadian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411702,411700,'3','驿城区','','驿城','0396','463000','Yicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411721,411700,'3','西平县','','西平','0396','463900','Xiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411722,411700,'3','上蔡县','','上蔡','0396','463800','Shangcai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411723,411700,'3','平舆县','','平舆','0396','463400','Pingyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411724,411700,'3','正阳县','','正阳','0396','463600','Zhengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411725,411700,'3','确山县','','确山','0396','463200','Queshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411726,411700,'3','泌阳县','','泌阳','0396','463700','Biyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411727,411700,'3','汝南县','','汝南','0396','463300','Runan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411728,411700,'3','遂平县','','遂平','0396','463100','Suiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (411729,411700,'3','新蔡县','','新蔡','0396','463500','Xincai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (419000,410000,'2','直辖县级','',' ','','','',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (419001,419000,'3','济源市','','济源','0391','454650','Jiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420000,100000,'1','湖北省','','湖北','','','Hubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420100,420000,'2','武汉市','','武汉','','430014','Wuhan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420102,420100,'3','江岸区','','江岸','027','430014','Jiang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420103,420100,'3','江汉区','','江汉','027','430021','Jianghan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420104,420100,'3','硚口区','','硚口','027','430033','Qiaokou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420105,420100,'3','汉阳区','','汉阳','027','430050','Hanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420106,420100,'3','武昌区','','武昌','027','430061','Wuchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420107,420100,'3','青山区','','青山','027','430080','Qingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420111,420100,'3','洪山区','','洪山','027','430070','Hongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420112,420100,'3','东西湖区','','东西湖','027','430040','Dongxihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420113,420100,'3','汉南区','','汉南','027','430090','Hannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420114,420100,'3','蔡甸区','','蔡甸','027','430100','Caidian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420115,420100,'3','江夏区','','江夏','027','430200','Jiangxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420116,420100,'3','黄陂区','','黄陂','027','432200','Huangpi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420117,420100,'3','新洲区','','新洲','027','431400','Xinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420200,420000,'2','黄石市','','黄石','0714','435003','Huangshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420202,420200,'3','黄石港区','','黄石港','0714','435000','Huangshigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420203,420200,'3','西塞山区','','西塞山','0714','435001','Xisaishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420204,420200,'3','下陆区','','下陆','0714','435005','Xialu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420205,420200,'3','铁山区','','铁山','0714','435006','Tieshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420222,420200,'3','阳新县','','阳新','0714','435200','Yangxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420281,420200,'3','大冶市','','大冶','0714','435100','Daye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420300,420000,'2','十堰市','','十堰','0719','442000','Shiyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420302,420300,'3','茅箭区','','茅箭','0719','442012','Maojian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420303,420300,'3','张湾区','','张湾','0719','442001','Zhangwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420304,420300,'3','郧阳区','','郧阳','0719','442500','Yunyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420322,420300,'3','郧西县','','郧西','0719','442600','Yunxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420323,420300,'3','竹山县','','竹山','0719','442200','Zhushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420324,420300,'3','竹溪县','','竹溪','0719','442300','Zhuxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420325,420300,'3','房县','','房县','0719','442100','Fangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420381,420300,'3','丹江口市','','丹江口','0719','442700','Danjiangkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420500,420000,'2','宜昌市','','宜昌','0717','443000','Yichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420502,420500,'3','西陵区','','西陵','0717','443000','Xiling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420503,420500,'3','伍家岗区','','伍家岗','0717','443001','Wujiagang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420504,420500,'3','点军区','','点军','0717','443006','Dianjun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420505,420500,'3','猇亭区','','猇亭','0717','443007','Xiaoting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420506,420500,'3','夷陵区','','夷陵','0717','443100','Yiling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420525,420500,'3','远安县','','远安','0717','444200','Yuan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420526,420500,'3','兴山县','','兴山','0717','443711','Xingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420527,420500,'3','秭归县','','秭归','0717','443600','Zigui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420528,420500,'3','长阳土家族自治县','','长阳','0717','443500','Changyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420529,420500,'3','五峰土家族自治县','','五峰','0717','443413','Wufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420581,420500,'3','宜都市','','宜都','0717','443300','Yidu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420582,420500,'3','当阳市','','当阳','0717','444100','Dangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420583,420500,'3','枝江市','','枝江','0717','443200','Zhijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420600,420000,'2','襄阳市','','襄阳','0710','441021','Xiangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420602,420600,'3','襄城区','','襄城','0710','441021','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420606,420600,'3','樊城区','','樊城','0710','441001','Fancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420607,420600,'3','襄州区','','襄州','0710','441100','Xiangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420624,420600,'3','南漳县','','南漳','0710','441500','Nanzhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420625,420600,'3','谷城县','','谷城','0710','441700','Gucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420626,420600,'3','保康县','','保康','0710','441600','Baokang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420682,420600,'3','老河口市','','老河口','0710','441800','Laohekou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420683,420600,'3','枣阳市','','枣阳','0710','441200','Zaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420684,420600,'3','宜城市','','宜城','0710','441400','Yicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420700,420000,'2','鄂州市','','鄂州','0711','436000','Ezhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420702,420700,'3','梁子湖区','','梁子湖','0711','436064','Liangzihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420703,420700,'3','华容区','','华容','0711','436030','Huarong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420704,420700,'3','鄂城区','','鄂城','0711','436000','Echeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420800,420000,'2','荆门市','','荆门','0724','448000','Jingmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420802,420800,'3','东宝区','','东宝','0724','448004','Dongbao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420804,420800,'3','掇刀区','','掇刀','0724','448124','Duodao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420821,420800,'3','京山县','','京山','0724','431800','Jingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420822,420800,'3','沙洋县','','沙洋','0724','448200','Shayang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420881,420800,'3','钟祥市','','钟祥','0724','431900','Zhongxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420900,420000,'2','孝感市','','孝感','0712','432100','Xiaogan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420902,420900,'3','孝南区','','孝南','0712','432100','Xiaonan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420921,420900,'3','孝昌县','','孝昌','0712','432900','Xiaochang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420922,420900,'3','大悟县','','大悟','0712','432800','Dawu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420923,420900,'3','云梦县','','云梦','0712','432500','Yunmeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420981,420900,'3','应城市','','应城','0712','432400','Yingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420982,420900,'3','安陆市','','安陆','0712','432600','Anlu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (420984,420900,'3','汉川市','','汉川','0712','432300','Hanchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421000,420000,'2','荆州市','','荆州','0716','434000','Jingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421002,421000,'3','沙市区','','沙市','0716','434000','Shashi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421003,421000,'3','荆州区','','荆州','0716','434020','Jingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421022,421000,'3','公安县','','公安','0716','434300','Gong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421023,421000,'3','监利县','','监利','0716','433300','Jianli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421024,421000,'3','江陵县','','江陵','0716','434101','Jiangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421081,421000,'3','石首市','','石首','0716','434400','Shishou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421083,421000,'3','洪湖市','','洪湖','0716','433200','Honghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421087,421000,'3','松滋市','','松滋','0716','434200','Songzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421100,420000,'2','黄冈市','','黄冈','0713','438000','Huanggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421102,421100,'3','黄州区','','黄州','0713','438000','Huangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421121,421100,'3','团风县','','团风','0713','438800','Tuanfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421122,421100,'3','红安县','','红安','0713','438401','Hong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421123,421100,'3','罗田县','','罗田','0713','438600','Luotian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421124,421100,'3','英山县','','英山','0713','438700','Yingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421125,421100,'3','浠水县','','浠水','0713','438200','Xishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421126,421100,'3','蕲春县','','蕲春','0713','435300','Qichun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421127,421100,'3','黄梅县','','黄梅','0713','435500','Huangmei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421181,421100,'3','麻城市','','麻城','0713','438300','Macheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421182,421100,'3','武穴市','','武穴','0713','435400','Wuxue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421200,420000,'2','咸宁市','','咸宁','0715','437000','Xianning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421202,421200,'3','咸安区','','咸安','0715','437000','Xian an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421221,421200,'3','嘉鱼县','','嘉鱼','0715','437200','Jiayu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421222,421200,'3','通城县','','通城','0715','437400','Tongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421223,421200,'3','崇阳县','','崇阳','0715','437500','Chongyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421224,421200,'3','通山县','','通山','0715','437600','Tongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421281,421200,'3','赤壁市','','赤壁','0715','437300','Chibi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421300,420000,'2','随州市','','随州','0722','441300','Suizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421303,421300,'3','曾都区','','曾都','0722','441300','Zengdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421321,421300,'3','随县','','随县','0722','441309','Suixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (421381,421300,'3','广水市','','广水','0722','432700','Guangshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422800,420000,'2','恩施土家族苗族自治州','','恩施','0718','445000','Enshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422801,422800,'3','恩施市','','恩施','0718','445000','Enshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422802,422800,'3','利川市','','利川','0718','445400','Lichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422822,422800,'3','建始县','','建始','0718','445300','Jianshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422823,422800,'3','巴东县','','巴东','0718','444300','Badong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422825,422800,'3','宣恩县','','宣恩','0718','445500','Xuanen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422826,422800,'3','咸丰县','','咸丰','0718','445600','Xianfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422827,422800,'3','来凤县','','来凤','0718','445700','Laifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (422828,422800,'3','鹤峰县','','鹤峰','0718','445800','Hefeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (429000,420000,'2','直辖县级','',' ','','','',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (429004,429000,'3','仙桃市','','仙桃','0728','433000','Xiantao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (429005,429000,'3','潜江市','','潜江','0728','433100','Qianjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (429006,429000,'3','天门市','','天门','0728','431700','Tianmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (429021,429000,'3','神农架林区','','神农架','0719','442400','Shennongjia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430000,100000,'1','湖南省','','湖南','','','Hunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430100,430000,'2','长沙市','','长沙','0731','410005','Changsha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430102,430100,'3','芙蓉区','','芙蓉','0731','410011','Furong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430103,430100,'3','天心区','','天心','0731','410004','Tianxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430104,430100,'3','岳麓区','','岳麓','0731','410013','Yuelu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430105,430100,'3','开福区','','开福','0731','410008','Kaifu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430111,430100,'3','雨花区','','雨花','0731','410011','Yuhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430112,430100,'3','望城区','','望城','0731','410200','Wangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430121,430100,'3','长沙县','','长沙','0731','410100','Changsha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430124,430100,'3','宁乡县','','宁乡','0731','410600','Ningxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430181,430100,'3','浏阳市','','浏阳','0731','410300','Liuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430200,430000,'2','株洲市','','株洲','0731','412000','Zhuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430202,430200,'3','荷塘区','','荷塘','0731','412000','Hetang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430203,430200,'3','芦淞区','','芦淞','0731','412000','Lusong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430204,430200,'3','石峰区','','石峰','0731','412005','Shifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430211,430200,'3','天元区','','天元','0731','412007','Tianyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430221,430200,'3','株洲县','','株洲','0731','412100','Zhuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430223,430200,'3','攸县','','攸县','0731','412300','Youxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430224,430200,'3','茶陵县','','茶陵','0731','412400','Chaling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430225,430200,'3','炎陵县','','炎陵','0731','412500','Yanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430281,430200,'3','醴陵市','','醴陵','0731','412200','Liling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430300,430000,'2','湘潭市','','湘潭','0731','411100','Xiangtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430302,430300,'3','雨湖区','','雨湖','0731','411100','Yuhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430304,430300,'3','岳塘区','','岳塘','0731','411101','Yuetang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430321,430300,'3','湘潭县','','湘潭','0731','411228','Xiangtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430381,430300,'3','湘乡市','','湘乡','0731','411400','Xiangxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430382,430300,'3','韶山市','','韶山','0731','411300','Shaoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430400,430000,'2','衡阳市','','衡阳','0734','421001','Hengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430405,430400,'3','珠晖区','','珠晖','0734','421002','Zhuhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430406,430400,'3','雁峰区','','雁峰','0734','421001','Yanfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430407,430400,'3','石鼓区','','石鼓','0734','421005','Shigu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430408,430400,'3','蒸湘区','','蒸湘','0734','421001','Zhengxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430412,430400,'3','南岳区','','南岳','0734','421900','Nanyue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430421,430400,'3','衡阳县','','衡阳','0734','421200','Hengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430422,430400,'3','衡南县','','衡南','0734','421131','Hengnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430423,430400,'3','衡山县','','衡山','0734','421300','Hengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430424,430400,'3','衡东县','','衡东','0734','421400','Hengdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430426,430400,'3','祁东县','','祁东','0734','421600','Qidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430481,430400,'3','耒阳市','','耒阳','0734','421800','Leiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430482,430400,'3','常宁市','','常宁','0734','421500','Changning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430500,430000,'2','邵阳市','','邵阳','0739','422000','Shaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430502,430500,'3','双清区','','双清','0739','422001','Shuangqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430503,430500,'3','大祥区','','大祥','0739','422000','Daxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430511,430500,'3','北塔区','','北塔','0739','422007','Beita',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430521,430500,'3','邵东县','','邵东','0739','422800','Shaodong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430522,430500,'3','新邵县','','新邵','0739','422900','Xinshao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430523,430500,'3','邵阳县','','邵阳','0739','422100','Shaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430524,430500,'3','隆回县','','隆回','0739','422200','Longhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430525,430500,'3','洞口县','','洞口','0739','422300','Dongkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430527,430500,'3','绥宁县','','绥宁','0739','422600','Suining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430528,430500,'3','新宁县','','新宁','0739','422700','Xinning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430529,430500,'3','城步苗族自治县','','城步','0739','422500','Chengbu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430581,430500,'3','武冈市','','武冈','0739','422400','Wugang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430600,430000,'2','岳阳市','','岳阳','0730','414000','Yueyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430602,430600,'3','岳阳楼区','','岳阳楼','0730','414000','Yueyanglou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430603,430600,'3','云溪区','','云溪','0730','414009','Yunxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430611,430600,'3','君山区','','君山','0730','414005','Junshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430621,430600,'3','岳阳县','','岳阳','0730','414100','Yueyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430623,430600,'3','华容县','','华容','0730','414200','Huarong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430624,430600,'3','湘阴县','','湘阴','0730','414600','Xiangyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430626,430600,'3','平江县','','平江','0730','414500','Pingjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430681,430600,'3','汨罗市','','汨罗','0730','414400','Miluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430682,430600,'3','临湘市','','临湘','0730','414300','Linxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430700,430000,'2','常德市','','常德','0736','415000','Changde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430702,430700,'3','武陵区','','武陵','0736','415000','Wuling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430703,430700,'3','鼎城区','','鼎城','0736','415101','Dingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430721,430700,'3','安乡县','','安乡','0736','415600','Anxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430722,430700,'3','汉寿县','','汉寿','0736','415900','Hanshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430723,430700,'3','澧县','','澧县','0736','415500','Lixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430724,430700,'3','临澧县','','临澧','0736','415200','Linli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430725,430700,'3','桃源县','','桃源','0736','415700','Taoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430726,430700,'3','石门县','','石门','0736','415300','Shimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430781,430700,'3','津市市','','津市','0736','415400','Jinshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430800,430000,'2','张家界市','','张家界','0744','427000','Zhangjiajie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430802,430800,'3','永定区','','永定','0744','427000','Yongding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430811,430800,'3','武陵源区','','武陵源','0744','427400','Wulingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430821,430800,'3','慈利县','','慈利','0744','427200','Cili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430822,430800,'3','桑植县','','桑植','0744','427100','Sangzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430900,430000,'2','益阳市','','益阳','0737','413000','Yiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430902,430900,'3','资阳区','','资阳','0737','413001','Ziyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430903,430900,'3','赫山区','','赫山','0737','413002','Heshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430921,430900,'3','南县','','南县','0737','413200','Nanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430922,430900,'3','桃江县','','桃江','0737','413400','Taojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430923,430900,'3','安化县','','安化','0737','413500','Anhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (430981,430900,'3','沅江市','','沅江','0737','413100','Yuanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431000,430000,'2','郴州市','','郴州','0735','423000','Chenzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431002,431000,'3','北湖区','','北湖','0735','423000','Beihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431003,431000,'3','苏仙区','','苏仙','0735','423000','Suxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431021,431000,'3','桂阳县','','桂阳','0735','424400','Guiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431022,431000,'3','宜章县','','宜章','0735','424200','Yizhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431023,431000,'3','永兴县','','永兴','0735','423300','Yongxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431024,431000,'3','嘉禾县','','嘉禾','0735','424500','Jiahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431025,431000,'3','临武县','','临武','0735','424300','Linwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431026,431000,'3','汝城县','','汝城','0735','424100','Rucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431027,431000,'3','桂东县','','桂东','0735','423500','Guidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431028,431000,'3','安仁县','','安仁','0735','423600','Anren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431081,431000,'3','资兴市','','资兴','0735','423400','Zixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431100,430000,'2','永州市','','永州','0746','425000','Yongzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431102,431100,'3','零陵区','','零陵','0746','425100','Lingling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431103,431100,'3','冷水滩区','','冷水滩','0746','425100','Lengshuitan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431121,431100,'3','祁阳县','','祁阳','0746','426100','Qiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431122,431100,'3','东安县','','东安','0746','425900','Dong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431123,431100,'3','双牌县','','双牌','0746','425200','Shuangpai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431124,431100,'3','道县','','道县','0746','425300','Daoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431125,431100,'3','江永县','','江永','0746','425400','Jiangyong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431126,431100,'3','宁远县','','宁远','0746','425600','Ningyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431127,431100,'3','蓝山县','','蓝山','0746','425800','Lanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431128,431100,'3','新田县','','新田','0746','425700','Xintian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431129,431100,'3','江华瑶族自治县','','江华','0746','425500','Jianghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431200,430000,'2','怀化市','','怀化','0745','418000','Huaihua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431202,431200,'3','鹤城区','','鹤城','0745','418000','Hecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431221,431200,'3','中方县','','中方','0745','418005','Zhongfang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431222,431200,'3','沅陵县','','沅陵','0745','419600','Yuanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431223,431200,'3','辰溪县','','辰溪','0745','419500','Chenxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431224,431200,'3','溆浦县','','溆浦','0745','419300','Xupu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431225,431200,'3','会同县','','会同','0745','418300','Huitong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431226,431200,'3','麻阳苗族自治县','','麻阳','0745','419400','Mayang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431227,431200,'3','新晃侗族自治县','','新晃','0745','419200','Xinhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431228,431200,'3','芷江侗族自治县','','芷江','0745','419100','Zhijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431229,431200,'3','靖州苗族侗族自治县','','靖州','0745','418400','Jingzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431230,431200,'3','通道侗族自治县','','通道','0745','418500','Tongdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431281,431200,'3','洪江市','','洪江','0745','418100','Hongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431300,430000,'2','娄底市','','娄底','0738','417000','Loudi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431302,431300,'3','娄星区','','娄星','0738','417000','Louxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431321,431300,'3','双峰县','','双峰','0738','417700','Shuangfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431322,431300,'3','新化县','','新化','0738','417600','Xinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431381,431300,'3','冷水江市','','冷水江','0738','417500','Lengshuijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (431382,431300,'3','涟源市','','涟源','0738','417100','Lianyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433100,430000,'2','湘西土家族苗族自治州','','湘西','0743','416000','Xiangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433101,433100,'3','吉首市','','吉首','0743','416000','Jishou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433122,433100,'3','泸溪县','','泸溪','0743','416100','Luxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433123,433100,'3','凤凰县','','凤凰','0743','416200','Fenghuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433124,433100,'3','花垣县','','花垣','0743','416400','Huayuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433125,433100,'3','保靖县','','保靖','0743','416500','Baojing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433126,433100,'3','古丈县','','古丈','0743','416300','Guzhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433127,433100,'3','永顺县','','永顺','0743','416700','Yongshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (433130,433100,'3','龙山县','','龙山','0743','416800','Longshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440000,100000,'1','广东省','','广东','','','Guangdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440100,440000,'2','广州市','','广州','020','510032','Guangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440103,440100,'3','荔湾区','','荔湾','020','510170','Liwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440104,440100,'3','越秀区','','越秀','020','510030','Yuexiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440105,440100,'3','海珠区','','海珠','020','510300','Haizhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440106,440100,'3','天河区','','天河','020','510665','Tianhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440111,440100,'3','白云区','','白云','020','510405','Baiyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440112,440100,'3','黄埔区','','黄埔','020','510700','Huangpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440113,440100,'3','番禺区','','番禺','020','511400','Panyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440114,440100,'3','花都区','','花都','020','510800','Huadu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440115,440100,'3','南沙区','','南沙','020','511458','Nansha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440117,440100,'3','从化区','','从化','020','510900','Conghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440118,440100,'3','增城区','','增城','020','511300','Zengcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440200,440000,'2','韶关市','','韶关','0751','512002','Shaoguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440203,440200,'3','武江区','','武江','0751','512026','Wujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440204,440200,'3','浈江区','','浈江','0751','512023','Zhenjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440205,440200,'3','曲江区','','曲江','0751','512101','Qujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440222,440200,'3','始兴县','','始兴','0751','512500','Shixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440224,440200,'3','仁化县','','仁化','0751','512300','Renhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440229,440200,'3','翁源县','','翁源','0751','512600','Wengyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440232,440200,'3','乳源瑶族自治县','','乳源','0751','512700','Ruyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440233,440200,'3','新丰县','','新丰','0751','511100','Xinfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440281,440200,'3','乐昌市','','乐昌','0751','512200','Lechang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440282,440200,'3','南雄市','','南雄','0751','512400','Nanxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440300,440000,'2','深圳市','','深圳','0755','518035','Shenzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440303,440300,'3','罗湖区','','罗湖','0755','518021','Luohu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440304,440300,'3','福田区','','福田','0755','518048','Futian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440305,440300,'3','南山区','','南山','0755','518051','Nanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440306,440300,'3','宝安区','','宝安','0755','518101','Bao an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440307,440300,'3','龙岗区','','龙岗','0755','518172','Longgang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440308,440300,'3','盐田区','','盐田','0755','518081','Yantian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440309,440300,'3','光明新区','','光明新区','0755','518100','Guangmingxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440310,440300,'3','坪山新区','','坪山新区','0755','518000','Pingshanxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440311,440300,'3','大鹏新区','','大鹏新区','0755','518000','Dapengxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440312,440300,'3','龙华新区','','龙华新区','0755','518100','Longhuaxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440400,440000,'2','珠海市','','珠海','0756','519000','Zhuhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440402,440400,'3','香洲区','','香洲','0756','519000','Xiangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440403,440400,'3','斗门区','','斗门','0756','519110','Doumen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440404,440400,'3','金湾区','','金湾','0756','519040','Jinwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440500,440000,'2','汕头市','','汕头','0754','515041','Shantou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440507,440500,'3','龙湖区','','龙湖','0754','515041','Longhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440511,440500,'3','金平区','','金平','0754','515041','Jinping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440512,440500,'3','濠江区','','濠江','0754','515071','Haojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440513,440500,'3','潮阳区','','潮阳','0754','515100','Chaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440514,440500,'3','潮南区','','潮南','0754','515144','Chaonan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440515,440500,'3','澄海区','','澄海','0754','515800','Chenghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440523,440500,'3','南澳县','','南澳','0754','515900','Nanao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440600,440000,'2','佛山市','','佛山','0757','528000','Foshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440604,440600,'3','禅城区','','禅城','0757','528000','Chancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440605,440600,'3','南海区','','南海','0757','528251','Nanhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440606,440600,'3','顺德区','','顺德','0757','528300','Shunde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440607,440600,'3','三水区','','三水','0757','528133','Sanshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440608,440600,'3','高明区','','高明','0757','528500','Gaoming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440700,440000,'2','江门市','','江门','0750','529000','Jiangmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440703,440700,'3','蓬江区','','蓬江','0750','529000','Pengjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440704,440700,'3','江海区','','江海','0750','529040','Jianghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440705,440700,'3','新会区','','新会','0750','529100','Xinhui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440781,440700,'3','台山市','','台山','0750','529200','Taishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440783,440700,'3','开平市','','开平','0750','529337','Kaiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440784,440700,'3','鹤山市','','鹤山','0750','529700','Heshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440785,440700,'3','恩平市','','恩平','0750','529400','Enping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440800,440000,'2','湛江市','','湛江','0759','524047','Zhanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440802,440800,'3','赤坎区','','赤坎','0759','524033','Chikan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440803,440800,'3','霞山区','','霞山','0759','524011','Xiashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440804,440800,'3','坡头区','','坡头','0759','524057','Potou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440811,440800,'3','麻章区','','麻章','0759','524094','Mazhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440823,440800,'3','遂溪县','','遂溪','0759','524300','Suixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440825,440800,'3','徐闻县','','徐闻','0759','524100','Xuwen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440881,440800,'3','廉江市','','廉江','0759','524400','Lianjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440882,440800,'3','雷州市','','雷州','0759','524200','Leizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440883,440800,'3','吴川市','','吴川','0759','524500','Wuchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440900,440000,'2','茂名市','','茂名','0668','525000','Maoming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440902,440900,'3','茂南区','','茂南','0668','525000','Maonan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440904,440900,'3','电白区','','电白','0668','525400','Dianbai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440981,440900,'3','高州市','','高州','0668','525200','Gaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440982,440900,'3','化州市','','化州','0668','525100','Huazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (440983,440900,'3','信宜市','','信宜','0668','525300','Xinyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441200,440000,'2','肇庆市','','肇庆','0758','526040','Zhaoqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441202,441200,'3','端州区','','端州','0758','526060','Duanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441203,441200,'3','鼎湖区','','鼎湖','0758','526070','Dinghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441223,441200,'3','广宁县','','广宁','0758','526300','Guangning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441224,441200,'3','怀集县','','怀集','0758','526400','Huaiji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441225,441200,'3','封开县','','封开','0758','526500','Fengkai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441226,441200,'3','德庆县','','德庆','0758','526600','Deqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441283,441200,'3','高要市','','高要','0758','526100','Gaoyao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441284,441200,'3','四会市','','四会','0758','526200','Sihui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441300,440000,'2','惠州市','','惠州','0752','516000','Huizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441302,441300,'3','惠城区','','惠城','0752','516008','Huicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441303,441300,'3','惠阳区','','惠阳','0752','516211','Huiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441322,441300,'3','博罗县','','博罗','0752','516100','Boluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441323,441300,'3','惠东县','','惠东','0752','516300','Huidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441324,441300,'3','龙门县','','龙门','0752','516800','Longmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441400,440000,'2','梅州市','','梅州','0753','514021','Meizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441402,441400,'3','梅江区','','梅江','0753','514000','Meijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441403,441400,'3','梅县区','','梅县','0753','514787','Meixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441422,441400,'3','大埔县','','大埔','0753','514200','Dabu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441423,441400,'3','丰顺县','','丰顺','0753','514300','Fengshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441424,441400,'3','五华县','','五华','0753','514400','Wuhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441426,441400,'3','平远县','','平远','0753','514600','Pingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441427,441400,'3','蕉岭县','','蕉岭','0753','514100','Jiaoling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441481,441400,'3','兴宁市','','兴宁','0753','514500','Xingning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441500,440000,'2','汕尾市','','汕尾','0660','516600','Shanwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441502,441500,'3','城区','','城区','0660','516600','Chengqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441521,441500,'3','海丰县','','海丰','0660','516400','Haifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441523,441500,'3','陆河县','','陆河','0660','516700','Luhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441581,441500,'3','陆丰市','','陆丰','0660','516500','Lufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441600,440000,'2','河源市','','河源','0762','517000','Heyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441602,441600,'3','源城区','','源城','0762','517000','Yuancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441621,441600,'3','紫金县','','紫金','0762','517400','Zijin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441622,441600,'3','龙川县','','龙川','0762','517300','Longchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441623,441600,'3','连平县','','连平','0762','517100','Lianping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441624,441600,'3','和平县','','和平','0762','517200','Heping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441625,441600,'3','东源县','','东源','0762','517583','Dongyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441700,440000,'2','阳江市','','阳江','0662','529500','Yangjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441702,441700,'3','江城区','','江城','0662','529500','Jiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441704,441700,'3','阳东区','','阳东','0662','529900','Yangdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441721,441700,'3','阳西县','','阳西','0662','529800','Yangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441781,441700,'3','阳春市','','阳春','0662','529600','Yangchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441800,440000,'2','清远市','','清远','0763','511500','Qingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441802,441800,'3','清城区','','清城','0763','511515','Qingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441803,441800,'3','清新区','','清新','0763','511810','Qingxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441821,441800,'3','佛冈县','','佛冈','0763','511600','Fogang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441823,441800,'3','阳山县','','阳山','0763','513100','Yangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441825,441800,'3','连山壮族瑶族自治县','','连山','0763','513200','Lianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441826,441800,'3','连南瑶族自治县','','连南','0763','513300','Liannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441881,441800,'3','英德市','','英德','0763','513000','Yingde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441882,441800,'3','连州市','','连州','0763','513400','Lianzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441900,440000,'2','东莞市','','东莞','0769','523888','Dongguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441901,441900,'3','莞城区','','莞城','0769','523128','Guancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441902,441900,'3','南城区','','南城','0769','523617','Nancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441904,441900,'3','万江区','','万江','0769','523039','Wanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441905,441900,'3','石碣镇','','石碣','0769','523290','Shijie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441906,441900,'3','石龙镇','','石龙','0769','523326','Shilong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441907,441900,'3','茶山镇','','茶山','0769','523380','Chashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441908,441900,'3','石排镇','','石排','0769','523346','Shipai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441909,441900,'3','企石镇','','企石','0769','523507','Qishi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441910,441900,'3','横沥镇','','横沥','0769','523471','Hengli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441911,441900,'3','桥头镇','','桥头','0769','523520','Qiaotou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441912,441900,'3','谢岗镇','','谢岗','0769','523592','Xiegang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441913,441900,'3','东坑镇','','东坑','0769','523451','Dongkeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441914,441900,'3','常平镇','','常平','0769','523560','Changping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441915,441900,'3','寮步镇','','寮步','0769','523411','Liaobu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441916,441900,'3','大朗镇','','大朗','0769','523770','Dalang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441917,441900,'3','麻涌镇','','麻涌','0769','523143','Machong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441918,441900,'3','中堂镇','','中堂','0769','523233','Zhongtang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441919,441900,'3','高埗镇','','高埗','0769','523282','Gaobu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441920,441900,'3','樟木头镇','','樟木头','0769','523619','Zhangmutou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441921,441900,'3','大岭山镇','','大岭山','0769','523835','Dalingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441922,441900,'3','望牛墩镇','','望牛墩','0769','523203','Wangniudun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441923,441900,'3','黄江镇','','黄江','0769','523755','Huangjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441924,441900,'3','洪梅镇','','洪梅','0769','523163','Hongmei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441925,441900,'3','清溪镇','','清溪','0769','523660','Qingxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441926,441900,'3','沙田镇','','沙田','0769','523988','Shatian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441927,441900,'3','道滘镇','','道滘','0769','523171','Daojiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441928,441900,'3','塘厦镇','','塘厦','0769','523713','Tangxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441929,441900,'3','虎门镇','','虎门','0769','523932','Humen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441930,441900,'3','厚街镇','','厚街','0769','523960','Houjie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441931,441900,'3','凤岗镇','','凤岗','0769','523690','Fenggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (441932,441900,'3','长安镇','','长安','0769','523850','Chang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442000,440000,'2','中山市','','中山','0760','528403','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442001,442000,'3','石岐区','','石岐','0760','528400','Shiqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442004,442000,'3','南区','','南区','0760','528400','Nanqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442005,442000,'3','五桂山区','','五桂山','0760','528458','Wuguishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442006,442000,'3','火炬开发区','','火炬','0760','528437','Huoju',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442007,442000,'3','黄圃镇','','黄圃','0760','528429','Huangpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442008,442000,'3','南头镇','','南头','0760','528421','Nantou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442009,442000,'3','东凤镇','','东凤','0760','528425','Dongfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442010,442000,'3','阜沙镇','','阜沙','0760','528434','Fusha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442011,442000,'3','小榄镇','','小榄','0760','528415','Xiaolan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442012,442000,'3','东升镇','','东升','0760','528400','Dongsheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442013,442000,'3','古镇镇','','古镇','0760','528422','Guzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442014,442000,'3','横栏镇','','横栏','0760','528478','Henglan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442015,442000,'3','三角镇','','三角','0760','528422','Sanjiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442016,442000,'3','民众镇','','民众','0760','528441','Minzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442017,442000,'3','南朗镇','','南朗','0760','528454','Nanlang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442018,442000,'3','港口镇','','港口','0760','528447','Gangkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442019,442000,'3','大涌镇','','大涌','0760','528476','Dayong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442020,442000,'3','沙溪镇','','沙溪','0760','528471','Shaxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442021,442000,'3','三乡镇','','三乡','0760','528463','Sanxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442022,442000,'3','板芙镇','','板芙','0760','528459','Banfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442023,442000,'3','神湾镇','','神湾','0760','528462','Shenwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (442024,442000,'3','坦洲镇','','坦洲','0760','528467','Tanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445100,440000,'2','潮州市','','潮州','0768','521000','Chaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445102,445100,'3','湘桥区','','湘桥','0768','521000','Xiangqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445103,445100,'3','潮安区','','潮安','0768','515638','Chao an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445122,445100,'3','饶平县','','饶平','0768','515700','Raoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445200,440000,'2','揭阳市','','揭阳','0633','522000','Jieyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445202,445200,'3','榕城区','','榕城','0633','522000','Rongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445203,445200,'3','揭东区','','揭东','0633','515500','Jiedong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445222,445200,'3','揭西县','','揭西','0633','515400','Jiexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445224,445200,'3','惠来县','','惠来','0633','515200','Huilai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445281,445200,'3','普宁市','','普宁','0633','515300','Puning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445300,440000,'2','云浮市','','云浮','0766','527300','Yunfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445302,445300,'3','云城区','','云城','0766','527300','Yuncheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445303,445300,'3','云安区','','云安','0766','527500','Yun an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445321,445300,'3','新兴县','','新兴','0766','527400','Xinxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445322,445300,'3','郁南县','','郁南','0766','527100','Yunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (445381,445300,'3','罗定市','','罗定','0766','527200','Luoding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450000,100000,'1','广西壮族自治区','','广西','','','Guangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450100,450000,'2','南宁市','','南宁','0771','530028','Nanning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450102,450100,'3','兴宁区','','兴宁','0771','530023','Xingning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450103,450100,'3','青秀区','','青秀','0771','530213','Qingxiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450105,450100,'3','江南区','','江南','0771','530031','Jiangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450107,450100,'3','西乡塘区','','西乡塘','0771','530001','Xixiangtang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450108,450100,'3','良庆区','','良庆','0771','530219','Liangqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450109,450100,'3','邕宁区','','邕宁','0771','530200','Yongning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450122,450100,'3','武鸣县','','武鸣','0771','530100','Wuming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450123,450100,'3','隆安县','','隆安','0771','532700','Long an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450124,450100,'3','马山县','','马山','0771','530600','Mashan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450125,450100,'3','上林县','','上林','0771','530500','Shanglin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450126,450100,'3','宾阳县','','宾阳','0771','530400','Binyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450127,450100,'3','横县','','横县','0771','530300','Hengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450128,450100,'3','埌东新区','','埌东','0771','530000','Langdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450200,450000,'2','柳州市','','柳州','0772','545001','Liuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450202,450200,'3','城中区','','城中','0772','545001','Chengzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450203,450200,'3','鱼峰区','','鱼峰','0772','545005','Yufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450204,450200,'3','柳南区','','柳南','0772','545007','Liunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450205,450200,'3','柳北区','','柳北','0772','545002','Liubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450221,450200,'3','柳江县','','柳江','0772','545100','Liujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450222,450200,'3','柳城县','','柳城','0772','545200','Liucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450223,450200,'3','鹿寨县','','鹿寨','0772','545600','Luzhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450224,450200,'3','融安县','','融安','0772','545400','Rong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450225,450200,'3','融水苗族自治县','','融水','0772','545300','Rongshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450226,450200,'3','三江侗族自治县','','三江','0772','545500','Sanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450227,450200,'3','柳东新区','','柳东','0772','545000','Liudong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450300,450000,'2','桂林市','','桂林','0773','541100','Guilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450302,450300,'3','秀峰区','','秀峰','0773','541001','Xiufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450303,450300,'3','叠彩区','','叠彩','0773','541001','Diecai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450304,450300,'3','象山区','','象山','0773','541002','Xiangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450305,450300,'3','七星区','','七星','0773','541004','Qixing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450311,450300,'3','雁山区','','雁山','0773','541006','Yanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450312,450300,'3','临桂区','','临桂','0773','541100','Lingui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450321,450300,'3','阳朔县','','阳朔','0773','541900','Yangshuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450323,450300,'3','灵川县','','灵川','0773','541200','Lingchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450324,450300,'3','全州县','','全州','0773','541503','Quanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450325,450300,'3','兴安县','','兴安','0773','541300','Xing an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450326,450300,'3','永福县','','永福','0773','541800','Yongfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450327,450300,'3','灌阳县','','灌阳','0773','541600','Guanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450328,450300,'3','龙胜各族自治县','','龙胜','0773','541700','Longsheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450329,450300,'3','资源县','','资源','0773','541400','Ziyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450330,450300,'3','平乐县','','平乐','0773','542400','Pingle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450331,450300,'3','荔浦县','','荔浦','0773','546600','Lipu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450332,450300,'3','恭城瑶族自治县','','恭城','0773','542500','Gongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450400,450000,'2','梧州市','','梧州','0774','543002','Wuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450403,450400,'3','万秀区','','万秀','0774','543000','Wanxiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450405,450400,'3','长洲区','','长洲','0774','543003','Changzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450406,450400,'3','龙圩区','','龙圩','0774','543002','Longxu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450421,450400,'3','苍梧县','','苍梧','0774','543100','Cangwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450422,450400,'3','藤县','','藤县','0774','543300','Tengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450423,450400,'3','蒙山县','','蒙山','0774','546700','Mengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450481,450400,'3','岑溪市','','岑溪','0774','543200','Cenxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450500,450000,'2','北海市','','北海','0779','536000','Beihai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450502,450500,'3','海城区','','海城','0779','536000','Haicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450503,450500,'3','银海区','','银海','0779','536000','Yinhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450512,450500,'3','铁山港区','','铁山港','0779','536017','Tieshangang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450521,450500,'3','合浦县','','合浦','0779','536100','Hepu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450600,450000,'2','防城港市','','防城港','0770','538001','Fangchenggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450602,450600,'3','港口区','','港口','0770','538001','Gangkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450603,450600,'3','防城区','','防城','0770','538021','Fangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450621,450600,'3','上思县','','上思','0770','535500','Shangsi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450681,450600,'3','东兴市','','东兴','0770','538100','Dongxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450700,450000,'2','钦州市','','钦州','0777','535099','Qinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450702,450700,'3','钦南区','','钦南','0777','535099','Qinnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450703,450700,'3','钦北区','','钦北','0777','535099','Qinbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450721,450700,'3','灵山县','','灵山','0777','535099','Lingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450722,450700,'3','浦北县','','浦北','0777','535099','Pubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450800,450000,'2','贵港市','','贵港','0775','537100','Guigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450802,450800,'3','港北区','','港北','0775','537100','Gangbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450803,450800,'3','港南区','','港南','0775','537100','Gangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450804,450800,'3','覃塘区','','覃塘','0775','537121','Qintang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450821,450800,'3','平南县','','平南','0775','537300','Pingnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450881,450800,'3','桂平市','','桂平','0775','537200','Guiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450900,450000,'2','玉林市','','玉林','0775','537000','Yulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450902,450900,'3','玉州区','','玉州','0775','537000','Yuzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450903,450900,'3','福绵区','','福绵','0775','537023','Fumian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450904,450900,'3','玉东新区','','玉东','0775','537000','Yudong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450921,450900,'3','容县','','容县','0775','537500','Rongxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450922,450900,'3','陆川县','','陆川','0775','537700','Luchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450923,450900,'3','博白县','','博白','0775','537600','Bobai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450924,450900,'3','兴业县','','兴业','0775','537800','Xingye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (450981,450900,'3','北流市','','北流','0775','537400','Beiliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451000,450000,'2','百色市','','百色','0776','533000','Baise',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451002,451000,'3','右江区','','右江','0776','533000','Youjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451021,451000,'3','田阳县','','田阳','0776','533600','Tianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451022,451000,'3','田东县','','田东','0776','531500','Tiandong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451023,451000,'3','平果县','','平果','0776','531400','Pingguo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451024,451000,'3','德保县','','德保','0776','533700','Debao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451025,451000,'3','靖西县','','靖西','0776','533800','Jingxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451026,451000,'3','那坡县','','那坡','0776','533900','Napo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451027,451000,'3','凌云县','','凌云','0776','533100','Lingyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451028,451000,'3','乐业县','','乐业','0776','533200','Leye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451029,451000,'3','田林县','','田林','0776','533300','Tianlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451030,451000,'3','西林县','','西林','0776','533500','Xilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451031,451000,'3','隆林各族自治县','','隆林','0776','533400','Longlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451100,450000,'2','贺州市','','贺州','0774','542800','Hezhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451102,451100,'3','八步区','','八步','0774','542800','Babu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451121,451100,'3','昭平县','','昭平','0774','546800','Zhaoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451122,451100,'3','钟山县','','钟山','0774','542600','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451123,451100,'3','富川瑶族自治县','','富川','0774','542700','Fuchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451124,451100,'3','平桂管理区','','平桂','0774','542800','Pingui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451200,450000,'2','河池市','','河池','0778','547000','Hechi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451202,451200,'3','金城江区','','金城江','0779','547000','Jinchengjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451221,451200,'3','南丹县','','南丹','0781','547200','Nandan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451222,451200,'3','天峨县','','天峨','0782','547300','Tiane',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451223,451200,'3','凤山县','','凤山','0783','547600','Fengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451224,451200,'3','东兰县','','东兰','0784','547400','Donglan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451225,451200,'3','罗城仫佬族自治县','','罗城','0785','546400','Luocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451226,451200,'3','环江毛南族自治县','','环江','0786','547100','Huanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451227,451200,'3','巴马瑶族自治县','','巴马','0787','547500','Bama',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451228,451200,'3','都安瑶族自治县','','都安','0788','530700','Du an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451229,451200,'3','大化瑶族自治县','','大化','0789','530800','Dahua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451281,451200,'3','宜州市','','宜州','0780','546300','Yizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451300,450000,'2','来宾市','','来宾','0772','546100','Laibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451302,451300,'3','兴宾区','','兴宾','0772','546100','Xingbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451321,451300,'3','忻城县','','忻城','0772','546200','Xincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451322,451300,'3','象州县','','象州','0772','545800','Xiangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451323,451300,'3','武宣县','','武宣','0772','545900','Wuxuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451324,451300,'3','金秀瑶族自治县','','金秀','0772','545799','Jinxiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451381,451300,'3','合山市','','合山','0772','546500','Heshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451400,450000,'2','崇左市','','崇左','0771','532299','Chongzuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451402,451400,'3','江州区','','江州','0771','532299','Jiangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451421,451400,'3','扶绥县','','扶绥','0771','532199','Fusui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451422,451400,'3','宁明县','','宁明','0771','532599','Ningming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451423,451400,'3','龙州县','','龙州','0771','532499','Longzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451424,451400,'3','大新县','','大新','0771','532399','Daxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451425,451400,'3','天等县','','天等','0771','532899','Tiandeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (451481,451400,'3','凭祥市','','凭祥','0771','532699','Pingxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460000,100000,'1','海南省','','海南','','','Hainan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460100,460000,'2','海口市','','海口','0898','570000','Haikou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460105,460100,'3','秀英区','','秀英','0898','570311','Xiuying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460106,460100,'3','龙华区','','龙华','0898','570145','Longhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460107,460100,'3','琼山区','','琼山','0898','571100','Qiongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460108,460100,'3','美兰区','','美兰','0898','570203','Meilan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460200,460000,'2','三亚市','','三亚','0898','572000','Sanya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460202,460200,'3','海棠区','','海棠','0898','572000','Haitang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460203,460200,'3','吉阳区','','吉阳','0898','572000','Jiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460204,460200,'3','天涯区','','天涯','0898','572000','Tianya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460205,460200,'3','崖州区','','崖州','0898','572000','Yazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460300,460000,'2','三沙市','','三沙','0898','573199','Sansha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460321,460300,'3','西沙群岛','','西沙','0898','572000','Xisha Islands',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460322,460300,'3','南沙群岛','','南沙','0898','573100','Nansha Islands',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (460323,460300,'3','中沙群岛','','中沙','0898','573100','Zhongsha Islands',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469000,460000,'2','直辖县级','',' ','','','',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469001,469000,'3','五指山市','','五指山','0898','572200','Wuzhishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469002,469000,'3','琼海市','','琼海','0898','571400','Qionghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469003,469000,'3','儋州市','','儋州','0898','571700','Danzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469005,469000,'3','文昌市','','文昌','0898','571339','Wenchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469006,469000,'3','万宁市','','万宁','0898','571500','Wanning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469007,469000,'3','东方市','','东方','0898','572600','Dongfang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469021,469000,'3','定安县','','定安','0898','571200','Ding an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469022,469000,'3','屯昌县','','屯昌','0898','571600','Tunchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469023,469000,'3','澄迈县','','澄迈','0898','571900','Chengmai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469024,469000,'3','临高县','','临高','0898','571800','Lingao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469025,469000,'3','白沙黎族自治县','','白沙','0898','572800','Baisha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469026,469000,'3','昌江黎族自治县','','昌江','0898','572700','Changjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469027,469000,'3','乐东黎族自治县','','乐东','0898','572500','Ledong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469028,469000,'3','陵水黎族自治县','','陵水','0898','572400','Lingshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469029,469000,'3','保亭黎族苗族自治县','','保亭','0898','572300','Baoting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (469030,469000,'3','琼中黎族苗族自治县','','琼中','0898','572900','Qiongzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500000,100000,'1','重庆','','重庆','','','Chongqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500100,500000,'2','重庆市','','重庆','023','400000','Chongqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500101,500100,'3','万州区','','万州','023','404000','Wanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500102,500100,'3','涪陵区','','涪陵','023','408000','Fuling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500103,500100,'3','渝中区','','渝中','023','400010','Yuzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500104,500100,'3','大渡口区','','大渡口','023','400080','Dadukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500105,500100,'3','江北区','','江北','023','400020','Jiangbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500106,500100,'3','沙坪坝区','','沙坪坝','023','400030','Shapingba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500107,500100,'3','九龙坡区','','九龙坡','023','400050','Jiulongpo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500108,500100,'3','南岸区','','南岸','023','400064','Nan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500109,500100,'3','北碚区','','北碚','023','400700','Beibei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500110,500100,'3','綦江区','','綦江','023','400800','Qijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500111,500100,'3','大足区','','大足','023','400900','Dazu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500112,500100,'3','渝北区','','渝北','023','401120','Yubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500113,500100,'3','巴南区','','巴南','023','401320','Banan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500114,500100,'3','黔江区','','黔江','023','409700','Qianjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500115,500100,'3','长寿区','','长寿','023','401220','Changshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500116,500100,'3','江津区','','江津','023','402260','Jiangjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500117,500100,'3','合川区','','合川','023','401520','Hechuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500118,500100,'3','永川区','','永川','023','402160','Yongchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500119,500100,'3','南川区','','南川','023','408400','Nanchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500120,500100,'3','璧山区','','璧山','023','402760','Bishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500151,500100,'3','铜梁区','','铜梁','023','402560','Tongliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500223,500100,'3','潼南县','','潼南','023','402660','Tongnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500226,500100,'3','荣昌县','','荣昌','023','402460','Rongchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500228,500100,'3','梁平县','','梁平','023','405200','Liangping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500229,500100,'3','城口县','','城口','023','405900','Chengkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500230,500100,'3','丰都县','','丰都','023','408200','Fengdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500231,500100,'3','垫江县','','垫江','023','408300','Dianjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500232,500100,'3','武隆县','','武隆','023','408500','Wulong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500233,500100,'3','忠县','','忠县','023','404300','Zhongxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500234,500100,'3','开县','','开县','023','405400','Kaixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500235,500100,'3','云阳县','','云阳','023','404500','Yunyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500236,500100,'3','奉节县','','奉节','023','404600','Fengjie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500237,500100,'3','巫山县','','巫山','023','404700','Wushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500238,500100,'3','巫溪县','','巫溪','023','405800','Wuxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500240,500100,'3','石柱土家族自治县','','石柱','023','409100','Shizhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500241,500100,'3','秀山土家族苗族自治县','','秀山','023','409900','Xiushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500242,500100,'3','酉阳土家族苗族自治县','','酉阳','023','409800','Youyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500243,500100,'3','彭水苗族土家族自治县','','彭水','023','409600','Pengshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500300,500000,'2','两江新区','','两江新区','023','400000','Liangjiangxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500301,500300,'3','北部新区','','北部新区','023','400000','Beibuxinqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500302,500300,'3','保税港区','','保税港区','023','400000','Baoshuigangqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (500303,500300,'3','工业园区','','工业园区','023','400000','Gongyeyuanqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510000,100000,'1','四川省','','四川','','','Sichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510100,510000,'2','成都市','','成都','028','610015','Chengdu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510104,510100,'3','锦江区','','锦江','028','610021','Jinjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510105,510100,'3','青羊区','','青羊','028','610031','Qingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510106,510100,'3','金牛区','','金牛','028','610036','Jinniu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510107,510100,'3','武侯区','','武侯','028','610041','Wuhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510108,510100,'3','成华区','','成华','028','610066','Chenghua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510112,510100,'3','龙泉驿区','','龙泉驿','028','610100','Longquanyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510113,510100,'3','青白江区','','青白江','028','610300','Qingbaijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510114,510100,'3','新都区','','新都','028','610500','Xindu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510115,510100,'3','温江区','','温江','028','611130','Wenjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510121,510100,'3','金堂县','','金堂','028','610400','Jintang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510122,510100,'3','双流县','','双流','028','610200','Shuangliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510124,510100,'3','郫县','','郫县','028','611730','Pixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510129,510100,'3','大邑县','','大邑','028','611330','Dayi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510131,510100,'3','蒲江县','','蒲江','028','611630','Pujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510132,510100,'3','新津县','','新津','028','611430','Xinjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510181,510100,'3','都江堰市','','都江堰','028','611830','Dujiangyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510182,510100,'3','彭州市','','彭州','028','611930','Pengzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510183,510100,'3','邛崃市','','邛崃','028','611530','Qionglai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510184,510100,'3','崇州市','','崇州','028','611230','Chongzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510300,510000,'2','自贡市','','自贡','0813','643000','Zigong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510302,510300,'3','自流井区','','自流井','0813','643000','Ziliujing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510303,510300,'3','贡井区','','贡井','0813','643020','Gongjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510304,510300,'3','大安区','','大安','0813','643010','Da an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510311,510300,'3','沿滩区','','沿滩','0813','643030','Yantan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510321,510300,'3','荣县','','荣县','0813','643100','Rongxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510322,510300,'3','富顺县','','富顺','0813','643200','Fushun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510400,510000,'2','攀枝花市','','攀枝花','0812','617000','Panzhihua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510402,510400,'3','东区','','东区','0812','617067','Dongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510403,510400,'3','西区','','西区','0812','617068','Xiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510411,510400,'3','仁和区','','仁和','0812','617061','Renhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510421,510400,'3','米易县','','米易','0812','617200','Miyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510422,510400,'3','盐边县','','盐边','0812','617100','Yanbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510500,510000,'2','泸州市','','泸州','0830','646000','Luzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510502,510500,'3','江阳区','','江阳','0830','646000','Jiangyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510503,510500,'3','纳溪区','','纳溪','0830','646300','Naxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510504,510500,'3','龙马潭区','','龙马潭','0830','646000','Longmatan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510521,510500,'3','泸县','','泸县','0830','646106','Luxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510522,510500,'3','合江县','','合江','0830','646200','Hejiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510524,510500,'3','叙永县','','叙永','0830','646400','Xuyong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510525,510500,'3','古蔺县','','古蔺','0830','646500','Gulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510600,510000,'2','德阳市','','德阳','0838','618000','Deyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510603,510600,'3','旌阳区','','旌阳','0838','618000','Jingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510623,510600,'3','中江县','','中江','0838','618100','Zhongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510626,510600,'3','罗江县','','罗江','0838','618500','Luojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510681,510600,'3','广汉市','','广汉','0838','618300','Guanghan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510682,510600,'3','什邡市','','什邡','0838','618400','Shifang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510683,510600,'3','绵竹市','','绵竹','0838','618200','Mianzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510700,510000,'2','绵阳市','','绵阳','0816','621000','Mianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510703,510700,'3','涪城区','','涪城','0816','621000','Fucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510704,510700,'3','游仙区','','游仙','0816','621022','Youxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510722,510700,'3','三台县','','三台','0816','621100','Santai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510723,510700,'3','盐亭县','','盐亭','0816','621600','Yanting',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510724,510700,'3','安县','','安县','0816','622650','Anxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510725,510700,'3','梓潼县','','梓潼','0816','622150','Zitong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510726,510700,'3','北川羌族自治县','','北川','0816','622750','Beichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510727,510700,'3','平武县','','平武','0816','622550','Pingwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510781,510700,'3','江油市','','江油','0816','621700','Jiangyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510800,510000,'2','广元市','','广元','0839','628000','Guangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510802,510800,'3','利州区','','利州','0839','628017','Lizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510811,510800,'3','昭化区','','昭化','0839','628017','Zhaohua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510812,510800,'3','朝天区','','朝天','0839','628017','Chaotian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510821,510800,'3','旺苍县','','旺苍','0839','628200','Wangcang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510822,510800,'3','青川县','','青川','0839','628100','Qingchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510823,510800,'3','剑阁县','','剑阁','0839','628300','Jiange',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510824,510800,'3','苍溪县','','苍溪','0839','628400','Cangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510900,510000,'2','遂宁市','','遂宁','0825','629000','Suining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510903,510900,'3','船山区','','船山','0825','629000','Chuanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510904,510900,'3','安居区','','安居','0825','629000','Anju',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510921,510900,'3','蓬溪县','','蓬溪','0825','629100','Pengxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510922,510900,'3','射洪县','','射洪','0825','629200','Shehong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (510923,510900,'3','大英县','','大英','0825','629300','Daying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511000,510000,'2','内江市','','内江','0832','641000','Neijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511002,511000,'3','市中区','','市中区','0832','641000','Shizhongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511011,511000,'3','东兴区','','东兴','0832','641100','Dongxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511024,511000,'3','威远县','','威远','0832','642450','Weiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511025,511000,'3','资中县','','资中','0832','641200','Zizhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511028,511000,'3','隆昌县','','隆昌','0832','642150','Longchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511100,510000,'2','乐山市','','乐山','0833','614000','Leshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511102,511100,'3','市中区','','市中区','0833','614000','Shizhongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511111,511100,'3','沙湾区','','沙湾','0833','614900','Shawan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511112,511100,'3','五通桥区','','五通桥','0833','614800','Wutongqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511113,511100,'3','金口河区','','金口河','0833','614700','Jinkouhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511123,511100,'3','犍为县','','犍为','0833','614400','Qianwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511124,511100,'3','井研县','','井研','0833','613100','Jingyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511126,511100,'3','夹江县','','夹江','0833','614100','Jiajiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511129,511100,'3','沐川县','','沐川','0833','614500','Muchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511132,511100,'3','峨边彝族自治县','','峨边','0833','614300','Ebian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511133,511100,'3','马边彝族自治县','','马边','0833','614600','Mabian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511181,511100,'3','峨眉山市','','峨眉山','0833','614200','Emeishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511300,510000,'2','南充市','','南充','0817','637000','Nanchong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511302,511300,'3','顺庆区','','顺庆','0817','637000','Shunqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511303,511300,'3','高坪区','','高坪','0817','637100','Gaoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511304,511300,'3','嘉陵区','','嘉陵','0817','637100','Jialing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511321,511300,'3','南部县','','南部','0817','637300','Nanbu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511322,511300,'3','营山县','','营山','0817','637700','Yingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511323,511300,'3','蓬安县','','蓬安','0817','637800','Peng an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511324,511300,'3','仪陇县','','仪陇','0817','637600','Yilong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511325,511300,'3','西充县','','西充','0817','637200','Xichong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511381,511300,'3','阆中市','','阆中','0817','637400','Langzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511400,510000,'2','眉山市','','眉山','028','620020','Meishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511402,511400,'3','东坡区','','东坡','028','620010','Dongpo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511403,511400,'3','彭山区','','彭山','028','620860','Pengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511421,511400,'3','仁寿县','','仁寿','028','620500','Renshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511423,511400,'3','洪雅县','','洪雅','028','620360','Hongya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511424,511400,'3','丹棱县','','丹棱','028','620200','Danling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511425,511400,'3','青神县','','青神','028','620460','Qingshen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511500,510000,'2','宜宾市','','宜宾','0831','644000','Yibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511502,511500,'3','翠屏区','','翠屏','0831','644000','Cuiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511503,511500,'3','南溪区','','南溪','0831','644100','Nanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511521,511500,'3','宜宾县','','宜宾','0831','644600','Yibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511523,511500,'3','江安县','','江安','0831','644200','Jiang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511524,511500,'3','长宁县','','长宁','0831','644300','Changning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511525,511500,'3','高县','','高县','0831','645150','Gaoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511526,511500,'3','珙县','','珙县','0831','644500','Gongxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511527,511500,'3','筠连县','','筠连','0831','645250','Junlian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511528,511500,'3','兴文县','','兴文','0831','644400','Xingwen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511529,511500,'3','屏山县','','屏山','0831','645350','Pingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511600,510000,'2','广安市','','广安','0826','638000','Guang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511602,511600,'3','广安区','','广安','0826','638000','Guang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511603,511600,'3','前锋区','','前锋','0826','638019','Qianfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511621,511600,'3','岳池县','','岳池','0826','638300','Yuechi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511622,511600,'3','武胜县','','武胜','0826','638400','Wusheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511623,511600,'3','邻水县','','邻水','0826','638500','Linshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511681,511600,'3','华蓥市','','华蓥','0826','638600','Huaying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511700,510000,'2','达州市','','达州','0818','635000','Dazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511702,511700,'3','通川区','','通川','0818','635000','Tongchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511703,511700,'3','达川区','','达川','0818','635000','Dachuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511722,511700,'3','宣汉县','','宣汉','0818','636150','Xuanhan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511723,511700,'3','开江县','','开江','0818','636250','Kaijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511724,511700,'3','大竹县','','大竹','0818','635100','Dazhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511725,511700,'3','渠县','','渠县','0818','635200','Quxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511781,511700,'3','万源市','','万源','0818','636350','Wanyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511800,510000,'2','雅安市','','雅安','0835','625000','Ya an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511802,511800,'3','雨城区','','雨城','0835','625000','Yucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511803,511800,'3','名山区','','名山','0835','625100','Mingshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511822,511800,'3','荥经县','','荥经','0835','625200','Yingjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511823,511800,'3','汉源县','','汉源','0835','625300','Hanyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511824,511800,'3','石棉县','','石棉','0835','625400','Shimian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511825,511800,'3','天全县','','天全','0835','625500','Tianquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511826,511800,'3','芦山县','','芦山','0835','625600','Lushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511827,511800,'3','宝兴县','','宝兴','0835','625700','Baoxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511900,510000,'2','巴中市','','巴中','0827','636000','Bazhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511902,511900,'3','巴州区','','巴州','0827','636001','Bazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511903,511900,'3','恩阳区','','恩阳','0827','636064','Enyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511921,511900,'3','通江县','','通江','0827','636700','Tongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511922,511900,'3','南江县','','南江','0827','636600','Nanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (511923,511900,'3','平昌县','','平昌','0827','636400','Pingchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (512000,510000,'2','资阳市','','资阳','028','641300','Ziyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (512002,512000,'3','雁江区','','雁江','028','641300','Yanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (512021,512000,'3','安岳县','','安岳','028','642350','Anyue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (512022,512000,'3','乐至县','','乐至','028','641500','Lezhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (512081,512000,'3','简阳市','','简阳','028','641400','Jianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513200,510000,'2','阿坝藏族羌族自治州','','阿坝','0837','624000','Aba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513221,513200,'3','汶川县','','汶川','0837','623000','Wenchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513222,513200,'3','理县','','理县','0837','623100','Lixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513223,513200,'3','茂县','','茂县','0837','623200','Maoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513224,513200,'3','松潘县','','松潘','0837','623300','Songpan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513225,513200,'3','九寨沟县','','九寨沟','0837','623400','Jiuzhaigou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513226,513200,'3','金川县','','金川','0837','624100','Jinchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513227,513200,'3','小金县','','小金','0837','624200','Xiaojin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513228,513200,'3','黑水县','','黑水','0837','623500','Heishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513229,513200,'3','马尔康县','','马尔康','0837','624000','Maerkang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513230,513200,'3','壤塘县','','壤塘','0837','624300','Rangtang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513231,513200,'3','阿坝县','','阿坝','0837','624600','Aba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513232,513200,'3','若尔盖县','','若尔盖','0837','624500','Ruoergai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513233,513200,'3','红原县','','红原','0837','624400','Hongyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513300,510000,'2','甘孜藏族自治州','','甘孜','0836','626000','Garze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513321,513300,'3','康定县','','康定','0836','626000','Kangding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513322,513300,'3','泸定县','','泸定','0836','626100','Luding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513323,513300,'3','丹巴县','','丹巴','0836','626300','Danba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513324,513300,'3','九龙县','','九龙','0836','626200','Jiulong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513325,513300,'3','雅江县','','雅江','0836','627450','Yajiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513326,513300,'3','道孚县','','道孚','0836','626400','Daofu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513327,513300,'3','炉霍县','','炉霍','0836','626500','Luhuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513328,513300,'3','甘孜县','','甘孜','0836','626700','Ganzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513329,513300,'3','新龙县','','新龙','0836','626800','Xinlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513330,513300,'3','德格县','','德格','0836','627250','Dege',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513331,513300,'3','白玉县','','白玉','0836','627150','Baiyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513332,513300,'3','石渠县','','石渠','0836','627350','Shiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513333,513300,'3','色达县','','色达','0836','626600','Seda',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513334,513300,'3','理塘县','','理塘','0836','627550','Litang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513335,513300,'3','巴塘县','','巴塘','0836','627650','Batang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513336,513300,'3','乡城县','','乡城','0836','627850','Xiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513337,513300,'3','稻城县','','稻城','0836','627750','Daocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513338,513300,'3','得荣县','','得荣','0836','627950','Derong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513400,510000,'2','凉山彝族自治州','','凉山','0834','615000','Liangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513401,513400,'3','西昌市','','西昌','0835','615000','Xichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513422,513400,'3','木里藏族自治县','','木里','0851','615800','Muli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513423,513400,'3','盐源县','','盐源','0836','615700','Yanyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513424,513400,'3','德昌县','','德昌','0837','615500','Dechang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513425,513400,'3','会理县','','会理','0838','615100','Huili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513426,513400,'3','会东县','','会东','0839','615200','Huidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513427,513400,'3','宁南县','','宁南','0840','615400','Ningnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513428,513400,'3','普格县','','普格','0841','615300','Puge',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513429,513400,'3','布拖县','','布拖','0842','616350','Butuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513430,513400,'3','金阳县','','金阳','0843','616250','Jinyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513431,513400,'3','昭觉县','','昭觉','0844','616150','Zhaojue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513432,513400,'3','喜德县','','喜德','0845','616750','Xide',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513433,513400,'3','冕宁县','','冕宁','0846','615600','Mianning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513434,513400,'3','越西县','','越西','0847','616650','Yuexi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513435,513400,'3','甘洛县','','甘洛','0848','616850','Ganluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513436,513400,'3','美姑县','','美姑','0849','616450','Meigu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (513437,513400,'3','雷波县','','雷波','0850','616550','Leibo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520000,100000,'1','贵州省','','贵州','','','Guizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520100,520000,'2','贵阳市','','贵阳','0851','550001','Guiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520102,520100,'3','南明区','','南明','0851','550001','Nanming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520103,520100,'3','云岩区','','云岩','0851','550001','Yunyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520111,520100,'3','花溪区','','花溪','0851','550025','Huaxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520112,520100,'3','乌当区','','乌当','0851','550018','Wudang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520113,520100,'3','白云区','','白云','0851','550014','Baiyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520115,520100,'3','观山湖区','','观山湖','0851','550009','Guanshanhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520121,520100,'3','开阳县','','开阳','0851','550300','Kaiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520122,520100,'3','息烽县','','息烽','0851','551100','Xifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520123,520100,'3','修文县','','修文','0851','550200','Xiuwen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520181,520100,'3','清镇市','','清镇','0851','551400','Qingzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520200,520000,'2','六盘水市','','六盘水','0858','553400','Liupanshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520201,520200,'3','钟山区','','钟山','0858','553000','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520203,520200,'3','六枝特区','','六枝','0858','553400','Liuzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520221,520200,'3','水城县','','水城','0858','553000','Shuicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520222,520200,'3','盘县','','盘县','0858','561601','Panxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520300,520000,'2','遵义市','','遵义','0852','563000','Zunyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520302,520300,'3','红花岗区','','红花岗','0852','563000','Honghuagang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520303,520300,'3','汇川区','','汇川','0852','563000','Huichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520321,520300,'3','遵义县','','遵义','0852','563100','Zunyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520322,520300,'3','桐梓县','','桐梓','0852','563200','Tongzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520323,520300,'3','绥阳县','','绥阳','0852','563300','Suiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520324,520300,'3','正安县','','正安','0852','563400','Zheng an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520325,520300,'3','道真仡佬族苗族自治县','','道真','0852','563500','Daozhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520326,520300,'3','务川仡佬族苗族自治县','','务川','0852','564300','Wuchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520327,520300,'3','凤冈县','','凤冈','0852','564200','Fenggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520328,520300,'3','湄潭县','','湄潭','0852','564100','Meitan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520329,520300,'3','余庆县','','余庆','0852','564400','Yuqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520330,520300,'3','习水县','','习水','0852','564600','Xishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520381,520300,'3','赤水市','','赤水','0852','564700','Chishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520382,520300,'3','仁怀市','','仁怀','0852','564500','Renhuai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520400,520000,'2','安顺市','','安顺','0853','561000','Anshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520402,520400,'3','西秀区','','西秀','0853','561000','Xixiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520421,520400,'3','平坝区','','平坝','0853','561100','Pingba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520422,520400,'3','普定县','','普定','0853','562100','Puding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520423,520400,'3','镇宁布依族苗族自治县','','镇宁','0853','561200','Zhenning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520424,520400,'3','关岭布依族苗族自治县','','关岭','0853','561300','Guanling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520425,520400,'3','紫云苗族布依族自治县','','紫云','0853','550800','Ziyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520500,520000,'2','毕节市','','毕节','0857','551700','Bijie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520502,520500,'3','七星关区','','七星关','0857','551700','Qixingguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520521,520500,'3','大方县','','大方','0857','551600','Dafang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520522,520500,'3','黔西县','','黔西','0857','551500','Qianxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520523,520500,'3','金沙县','','金沙','0857','551800','Jinsha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520524,520500,'3','织金县','','织金','0857','552100','Zhijin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520525,520500,'3','纳雍县','','纳雍','0857','553300','Nayong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520526,520500,'3','威宁彝族回族苗族自治县','','威宁','0857','553100','Weining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520527,520500,'3','赫章县','','赫章','0857','553200','Hezhang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520600,520000,'2','铜仁市','','铜仁','0856','554300','Tongren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520602,520600,'3','碧江区','','碧江','0856','554300','Bijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520603,520600,'3','万山区','','万山','0856','554200','Wanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520621,520600,'3','江口县','','江口','0856','554400','Jiangkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520622,520600,'3','玉屏侗族自治县','','玉屏','0856','554004','Yuping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520623,520600,'3','石阡县','','石阡','0856','555100','Shiqian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520624,520600,'3','思南县','','思南','0856','565100','Sinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520625,520600,'3','印江土家族苗族自治县','','印江','0856','555200','Yinjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520626,520600,'3','德江县','','德江','0856','565200','Dejiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520627,520600,'3','沿河土家族自治县','','沿河','0856','565300','Yuanhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (520628,520600,'3','松桃苗族自治县','','松桃','0856','554100','Songtao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522300,520000,'2','黔西南布依族苗族自治州','','黔西南','0859','562400','Qianxinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522301,522300,'3','兴义市 ','','兴义','0859','562400','Xingyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522322,522300,'3','兴仁县','','兴仁','0859','562300','Xingren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522323,522300,'3','普安县','','普安','0859','561500','Pu an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522324,522300,'3','晴隆县','','晴隆','0859','561400','Qinglong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522325,522300,'3','贞丰县','','贞丰','0859','562200','Zhenfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522326,522300,'3','望谟县','','望谟','0859','552300','Wangmo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522327,522300,'3','册亨县','','册亨','0859','552200','Ceheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522328,522300,'3','安龙县','','安龙','0859','552400','Anlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522600,520000,'2','黔东南苗族侗族自治州','','黔东南','0855','556000','Qiandongnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522601,522600,'3','凯里市','','凯里','0855','556000','Kaili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522622,522600,'3','黄平县','','黄平','0855','556100','Huangping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522623,522600,'3','施秉县','','施秉','0855','556200','Shibing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522624,522600,'3','三穗县','','三穗','0855','556500','Sansui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522625,522600,'3','镇远县','','镇远','0855','557700','Zhenyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522626,522600,'3','岑巩县','','岑巩','0855','557800','Cengong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522627,522600,'3','天柱县','','天柱','0855','556600','Tianzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522628,522600,'3','锦屏县','','锦屏','0855','556700','Jinping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522629,522600,'3','剑河县','','剑河','0855','556400','Jianhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522630,522600,'3','台江县','','台江','0855','556300','Taijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522631,522600,'3','黎平县','','黎平','0855','557300','Liping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522632,522600,'3','榕江县','','榕江','0855','557200','Rongjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522633,522600,'3','从江县','','从江','0855','557400','Congjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522634,522600,'3','雷山县','','雷山','0855','557100','Leishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522635,522600,'3','麻江县','','麻江','0855','557600','Majiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522636,522600,'3','丹寨县','','丹寨','0855','557500','Danzhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522700,520000,'2','黔南布依族苗族自治州','','黔南','0854','558000','Qiannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522701,522700,'3','都匀市','','都匀','0854','558000','Duyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522702,522700,'3','福泉市','','福泉','0854','550500','Fuquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522722,522700,'3','荔波县','','荔波','0854','558400','Libo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522723,522700,'3','贵定县','','贵定','0854','551300','Guiding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522725,522700,'3','瓮安县','','瓮安','0854','550400','Weng an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522726,522700,'3','独山县','','独山','0854','558200','Dushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522727,522700,'3','平塘县','','平塘','0854','558300','Pingtang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522728,522700,'3','罗甸县','','罗甸','0854','550100','Luodian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522729,522700,'3','长顺县','','长顺','0854','550700','Changshun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522730,522700,'3','龙里县','','龙里','0854','551200','Longli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522731,522700,'3','惠水县','','惠水','0854','550600','Huishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (522732,522700,'3','三都水族自治县','','三都','0854','558100','Sandu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530000,100000,'1','云南省','','云南','','','Yunnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530100,530000,'2','昆明市','','昆明','0871','650500','Kunming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530102,530100,'3','五华区','','五华','0871','650021','Wuhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530103,530100,'3','盘龙区','','盘龙','0871','650051','Panlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530111,530100,'3','官渡区','','官渡','0871','650200','Guandu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530112,530100,'3','西山区','','西山','0871','650118','Xishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530113,530100,'3','东川区','','东川','0871','654100','Dongchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530114,530100,'3','呈贡区','','呈贡','0871','650500','Chenggong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530122,530100,'3','晋宁县','','晋宁','0871','650600','Jinning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530124,530100,'3','富民县','','富民','0871','650400','Fumin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530125,530100,'3','宜良县','','宜良','0871','652100','Yiliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530126,530100,'3','石林彝族自治县','','石林','0871','652200','Shilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530127,530100,'3','嵩明县','','嵩明','0871','651700','Songming',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530128,530100,'3','禄劝彝族苗族自治县','','禄劝','0871','651500','Luquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530129,530100,'3','寻甸回族彝族自治县 ','','寻甸','0871','655200','Xundian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530181,530100,'3','安宁市','','安宁','0871','650300','Anning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530300,530000,'2','曲靖市','','曲靖','0874','655000','Qujing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530302,530300,'3','麒麟区','','麒麟','0874','655000','Qilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530321,530300,'3','马龙县','','马龙','0874','655100','Malong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530322,530300,'3','陆良县','','陆良','0874','655600','Luliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530323,530300,'3','师宗县','','师宗','0874','655700','Shizong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530324,530300,'3','罗平县','','罗平','0874','655800','Luoping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530325,530300,'3','富源县','','富源','0874','655500','Fuyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530326,530300,'3','会泽县','','会泽','0874','654200','Huize',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530328,530300,'3','沾益县','','沾益','0874','655331','Zhanyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530381,530300,'3','宣威市','','宣威','0874','655400','Xuanwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530400,530000,'2','玉溪市','','玉溪','0877','653100','Yuxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530402,530400,'3','红塔区','','红塔','0877','653100','Hongta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530421,530400,'3','江川县','','江川','0877','652600','Jiangchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530422,530400,'3','澄江县','','澄江','0877','652500','Chengjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530423,530400,'3','通海县','','通海','0877','652700','Tonghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530424,530400,'3','华宁县','','华宁','0877','652800','Huaning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530425,530400,'3','易门县','','易门','0877','651100','Yimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530426,530400,'3','峨山彝族自治县','','峨山','0877','653200','Eshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530427,530400,'3','新平彝族傣族自治县','','新平','0877','653400','Xinping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530428,530400,'3','元江哈尼族彝族傣族自治县','','元江','0877','653300','Yuanjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530500,530000,'2','保山市','','保山','0875','678000','Baoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530502,530500,'3','隆阳区','','隆阳','0875','678000','Longyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530521,530500,'3','施甸县','','施甸','0875','678200','Shidian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530522,530500,'3','腾冲县','','腾冲','0875','679100','Tengchong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530523,530500,'3','龙陵县','','龙陵','0875','678300','Longling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530524,530500,'3','昌宁县','','昌宁','0875','678100','Changning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530600,530000,'2','昭通市','','昭通','0870','657000','Zhaotong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530602,530600,'3','昭阳区','','昭阳','0870','657000','Zhaoyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530621,530600,'3','鲁甸县','','鲁甸','0870','657100','Ludian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530622,530600,'3','巧家县','','巧家','0870','654600','Qiaojia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530623,530600,'3','盐津县','','盐津','0870','657500','Yanjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530624,530600,'3','大关县','','大关','0870','657400','Daguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530625,530600,'3','永善县','','永善','0870','657300','Yongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530626,530600,'3','绥江县','','绥江','0870','657700','Suijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530627,530600,'3','镇雄县','','镇雄','0870','657200','Zhenxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530628,530600,'3','彝良县','','彝良','0870','657600','Yiliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530629,530600,'3','威信县','','威信','0870','657900','Weixin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530630,530600,'3','水富县','','水富','0870','657800','Shuifu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530700,530000,'2','丽江市','','丽江','0888','674100','Lijiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530702,530700,'3','古城区','','古城','0888','674100','Gucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530721,530700,'3','玉龙纳西族自治县','','玉龙','0888','674100','Yulong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530722,530700,'3','永胜县','','永胜','0888','674200','Yongsheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530723,530700,'3','华坪县','','华坪','0888','674800','Huaping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530724,530700,'3','宁蒗彝族自治县','','宁蒗','0888','674300','Ninglang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530800,530000,'2','普洱市','','普洱','0879','665000','Pu er',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530802,530800,'3','思茅区','','思茅','0879','665000','Simao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530821,530800,'3','宁洱哈尼族彝族自治县','','宁洱','0879','665100','Ninger',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530822,530800,'3','墨江哈尼族自治县','','墨江','0879','654800','Mojiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530823,530800,'3','景东彝族自治县','','景东','0879','676200','Jingdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530824,530800,'3','景谷傣族彝族自治县','','景谷','0879','666400','Jinggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530825,530800,'3','镇沅彝族哈尼族拉祜族自治县','','镇沅','0879','666500','Zhenyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530826,530800,'3','江城哈尼族彝族自治县','','江城','0879','665900','Jiangcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530827,530800,'3','孟连傣族拉祜族佤族自治县','','孟连','0879','665800','Menglian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530828,530800,'3','澜沧拉祜族自治县','','澜沧','0879','665600','Lancang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530829,530800,'3','西盟佤族自治县','','西盟','0879','665700','Ximeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530900,530000,'2','临沧市','','临沧','0883','677000','Lincang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530902,530900,'3','临翔区','','临翔','0883','677000','Linxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530921,530900,'3','凤庆县','','凤庆','0883','675900','Fengqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530922,530900,'3','云县','','云县','0883','675800','Yunxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530923,530900,'3','永德县','','永德','0883','677600','Yongde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530924,530900,'3','镇康县','','镇康','0883','677704','Zhenkang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530925,530900,'3','双江拉祜族佤族布朗族傣族自治县','','双江','0883','677300','Shuangjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530926,530900,'3','耿马傣族佤族自治县','','耿马','0883','677500','Gengma',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (530927,530900,'3','沧源佤族自治县','','沧源','0883','677400','Cangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532300,530000,'2','楚雄彝族自治州','','楚雄','0878','675000','Chuxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532301,532300,'3','楚雄市','','楚雄','0878','675000','Chuxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532322,532300,'3','双柏县','','双柏','0878','675100','Shuangbai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532323,532300,'3','牟定县','','牟定','0878','675500','Mouding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532324,532300,'3','南华县','','南华','0878','675200','Nanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532325,532300,'3','姚安县','','姚安','0878','675300','Yao an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532326,532300,'3','大姚县','','大姚','0878','675400','Dayao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532327,532300,'3','永仁县','','永仁','0878','651400','Yongren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532328,532300,'3','元谋县','','元谋','0878','651300','Yuanmou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532329,532300,'3','武定县','','武定','0878','651600','Wuding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532331,532300,'3','禄丰县','','禄丰','0878','651200','Lufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532500,530000,'2','红河哈尼族彝族自治州','','红河','0873','661400','Honghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532501,532500,'3','个旧市','','个旧','0873','661000','Gejiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532502,532500,'3','开远市','','开远','0873','661600','Kaiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532503,532500,'3','蒙自市','','蒙自','0873','661101','Mengzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532504,532500,'3','弥勒市','','弥勒','0873','652300','Mile ',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532523,532500,'3','屏边苗族自治县','','屏边','0873','661200','Pingbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532524,532500,'3','建水县','','建水','0873','654300','Jianshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532525,532500,'3','石屏县','','石屏','0873','662200','Shiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532527,532500,'3','泸西县','','泸西','0873','652400','Luxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532528,532500,'3','元阳县','','元阳','0873','662400','Yuanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532529,532500,'3','红河县','','红河县','0873','654400','Honghexian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532530,532500,'3','金平苗族瑶族傣族自治县','','金平','0873','661500','Jinping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532531,532500,'3','绿春县','','绿春','0873','662500','Lvchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532532,532500,'3','河口瑶族自治县','','河口','0873','661300','Hekou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532600,530000,'2','文山壮族苗族自治州','','文山','0876','663000','Wenshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532601,532600,'3','文山市','','文山','0876','663000','Wenshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532622,532600,'3','砚山县','','砚山','0876','663100','Yanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532623,532600,'3','西畴县','','西畴','0876','663500','Xichou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532624,532600,'3','麻栗坡县','','麻栗坡','0876','663600','Malipo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532625,532600,'3','马关县','','马关','0876','663700','Maguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532626,532600,'3','丘北县','','丘北','0876','663200','Qiubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532627,532600,'3','广南县','','广南','0876','663300','Guangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532628,532600,'3','富宁县','','富宁','0876','663400','Funing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532800,530000,'2','西双版纳傣族自治州','','西双版纳','0691','666100','Xishuangbanna',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532801,532800,'3','景洪市','','景洪','0691','666100','Jinghong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532822,532800,'3','勐海县','','勐海','0691','666200','Menghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532823,532800,'3','勐腊县','','勐腊','0691','666300','Mengla',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532900,530000,'2','大理白族自治州','','大理','0872','671000','Dali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532901,532900,'3','大理市','','大理','0872','671000','Dali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532922,532900,'3','漾濞彝族自治县','','漾濞','0872','672500','Yangbi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532923,532900,'3','祥云县','','祥云','0872','672100','Xiangyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532924,532900,'3','宾川县','','宾川','0872','671600','Binchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532925,532900,'3','弥渡县','','弥渡','0872','675600','Midu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532926,532900,'3','南涧彝族自治县','','南涧','0872','675700','Nanjian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532927,532900,'3','巍山彝族回族自治县','','巍山','0872','672400','Weishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532928,532900,'3','永平县','','永平','0872','672600','Yongping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532929,532900,'3','云龙县','','云龙','0872','672700','Yunlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532930,532900,'3','洱源县','','洱源','0872','671200','Eryuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532931,532900,'3','剑川县','','剑川','0872','671300','Jianchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (532932,532900,'3','鹤庆县','','鹤庆','0872','671500','Heqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533100,530000,'2','德宏傣族景颇族自治州','','德宏','0692','678400','Dehong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533102,533100,'3','瑞丽市','','瑞丽','0692','678600','Ruili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533103,533100,'3','芒市','','芒市','0692','678400','Mangshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533122,533100,'3','梁河县','','梁河','0692','679200','Lianghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533123,533100,'3','盈江县','','盈江','0692','679300','Yingjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533124,533100,'3','陇川县','','陇川','0692','678700','Longchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533300,530000,'2','怒江傈僳族自治州','','怒江','0886','673100','Nujiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533321,533300,'3','泸水县','','泸水','0886','673100','Lushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533323,533300,'3','福贡县','','福贡','0886','673400','Fugong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533324,533300,'3','贡山独龙族怒族自治县','','贡山','0886','673500','Gongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533325,533300,'3','兰坪白族普米族自治县','','兰坪','0886','671400','Lanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533400,530000,'2','迪庆藏族自治州','','迪庆','0887','674400','Deqen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533421,533400,'3','香格里拉市','','香格里拉','0887','674400','Xianggelila',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533422,533400,'3','德钦县','','德钦','0887','674500','Deqin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (533423,533400,'3','维西傈僳族自治县','','维西','0887','674600','Weixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540000,100000,'1','西藏自治区','','西藏','','','Tibet',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540100,540000,'2','拉萨市','','拉萨','0891','850000','Lhasa',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540102,540100,'3','城关区','','城关','0891','850000','Chengguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540121,540100,'3','林周县','','林周','0891','851600','Linzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540122,540100,'3','当雄县','','当雄','0891','851500','Dangxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540123,540100,'3','尼木县','','尼木','0891','851300','Nimu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540124,540100,'3','曲水县','','曲水','0891','850600','Qushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540125,540100,'3','堆龙德庆县','','堆龙德庆','0891','851400','Duilongdeqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540126,540100,'3','达孜县','','达孜','0891','850100','Dazi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540127,540100,'3','墨竹工卡县','','墨竹工卡','0891','850200','Mozhugongka',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540200,540000,'2','日喀则市','','日喀则','0892','857000','Rikaze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540202,540200,'3','桑珠孜区','','桑珠孜','0892','857000','Sangzhuzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540221,540200,'3','南木林县','','南木林','0892','857100','Nanmulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540222,540200,'3','江孜县','','江孜','0892','857400','Jiangzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540223,540200,'3','定日县','','定日','0892','858200','Dingri',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540224,540200,'3','萨迦县','','萨迦','0892','857800','Sajia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540225,540200,'3','拉孜县','','拉孜','0892','858100','Lazi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540226,540200,'3','昂仁县','','昂仁','0892','858500','Angren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540227,540200,'3','谢通门县','','谢通门','0892','858900','Xietongmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540228,540200,'3','白朗县','','白朗','0892','857300','Bailang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540229,540200,'3','仁布县','','仁布','0892','857200','Renbu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540230,540200,'3','康马县','','康马','0892','857500','Kangma',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540231,540200,'3','定结县','','定结','0892','857900','Dingjie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540232,540200,'3','仲巴县','','仲巴','0892','858800','Zhongba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540233,540200,'3','亚东县','','亚东','0892','857600','Yadong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540234,540200,'3','吉隆县','','吉隆','0892','858700','Jilong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540235,540200,'3','聂拉木县','','聂拉木','0892','858300','Nielamu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540236,540200,'3','萨嘎县','','萨嘎','0892','857800','Saga',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540237,540200,'3','岗巴县','','岗巴','0892','857700','Gangba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540300,540000,'2','昌都市','','昌都','0895','854000','Qamdo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540302,540300,'3','卡若区','','昌都','0895','854000','Karuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540321,540300,'3','江达县','','江达','0895','854100','Jiangda',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540322,540300,'3','贡觉县','','贡觉','0895','854200','Gongjue',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540323,540300,'3','类乌齐县','','类乌齐','0895','855600','Leiwuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540324,540300,'3','丁青县','','丁青','0895','855700','Dingqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540325,540300,'3','察雅县','','察雅','0895','854300','Chaya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540326,540300,'3','八宿县','','八宿','0895','854600','Basu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540327,540300,'3','左贡县','','左贡','0895','854400','Zuogong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540328,540300,'3','芒康县','','芒康','0895','854500','Mangkang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540329,540300,'3','洛隆县','','洛隆','0895','855400','Luolong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (540330,540300,'3','边坝县','','边坝','0895','855500','Bianba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542200,540000,'2','山南地区','','山南','0893','856000','Shannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542221,542200,'3','乃东县','','乃东','0893','856100','Naidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542222,542200,'3','扎囊县','','扎囊','0893','850800','Zhanang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542223,542200,'3','贡嘎县','','贡嘎','0893','850700','Gongga',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542224,542200,'3','桑日县','','桑日','0893','856200','Sangri',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542225,542200,'3','琼结县','','琼结','0893','856800','Qiongjie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542226,542200,'3','曲松县','','曲松','0893','856300','Qusong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542227,542200,'3','措美县','','措美','0893','856900','Cuomei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542228,542200,'3','洛扎县','','洛扎','0893','856600','Luozha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542229,542200,'3','加查县','','加查','0893','856400','Jiacha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542231,542200,'3','隆子县','','隆子','0893','856600','Longzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542232,542200,'3','错那县','','错那','0893','856700','Cuona',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542233,542200,'3','浪卡子县','','浪卡子','0893','851100','Langkazi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542400,540000,'2','那曲地区','','那曲','0896','852000','Nagqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542421,542400,'3','那曲县','','那曲','0896','852000','Naqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542422,542400,'3','嘉黎县','','嘉黎','0896','852400','Jiali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542423,542400,'3','比如县','','比如','0896','852300','Biru',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542424,542400,'3','聂荣县','','聂荣','0896','853500','Nierong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542425,542400,'3','安多县','','安多','0896','853400','Anduo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542426,542400,'3','申扎县','','申扎','0896','853100','Shenzha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542427,542400,'3','索县','','索县','0896','852200','Suoxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542428,542400,'3','班戈县','','班戈','0896','852500','Bange',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542429,542400,'3','巴青县','','巴青','0896','852100','Baqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542430,542400,'3','尼玛县','','尼玛','0896','852600','Nima',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542431,542400,'3','双湖县','','双湖','0896','852600','Shuanghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542500,540000,'2','阿里地区','','阿里','0897','859000','Ngari',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542521,542500,'3','普兰县','','普兰','0897','859500','Pulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542522,542500,'3','札达县','','札达','0897','859600','Zhada',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542523,542500,'3','噶尔县','','噶尔','0897','859400','Gaer',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542524,542500,'3','日土县','','日土','0897','859700','Ritu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542525,542500,'3','革吉县','','革吉','0897','859100','Geji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542526,542500,'3','改则县','','改则','0897','859200','Gaize',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542527,542500,'3','措勤县','','措勤','0897','859300','Cuoqin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542600,540000,'2','林芝地区','','林芝','0894','850400','Nyingchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542621,542600,'3','林芝县','','林芝','0894','850400','Linzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542622,542600,'3','工布江达县','','工布江达','0894','850300','Gongbujiangda',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542623,542600,'3','米林县','','米林','0894','850500','Milin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542624,542600,'3','墨脱县','','墨脱','0894','855300','Motuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542625,542600,'3','波密县','','波密','0894','855200','Bomi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542626,542600,'3','察隅县','','察隅','0894','855100','Chayu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (542627,542600,'3','朗县','','朗县','0894','856500','Langxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610000,100000,'1','陕西省','','陕西','','','Shaanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610100,610000,'2','西安市','','西安','029','710003','Xi an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610102,610100,'3','新城区','','新城','029','710004','Xincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610103,610100,'3','碑林区','','碑林','029','710001','Beilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610104,610100,'3','莲湖区','','莲湖','029','710003','Lianhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610111,610100,'3','灞桥区','','灞桥','029','710038','Baqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610112,610100,'3','未央区','','未央','029','710014','Weiyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610113,610100,'3','雁塔区','','雁塔','029','710061','Yanta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610114,610100,'3','阎良区','','阎良','029','710087','Yanliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610115,610100,'3','临潼区','','临潼','029','710600','Lintong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610116,610100,'3','长安区','','长安','029','710100','Chang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610122,610100,'3','蓝田县','','蓝田','029','710500','Lantian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610124,610100,'3','周至县','','周至','029','710400','Zhouzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610125,610100,'3','户县','','户县','029','710300','Huxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610126,610100,'3','高陵区','','高陵','029','710200','Gaoling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610200,610000,'2','铜川市','','铜川','0919','727100','Tongchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610202,610200,'3','王益区','','王益','0919','727000','Wangyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610203,610200,'3','印台区','','印台','0919','727007','Yintai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610204,610200,'3','耀州区','','耀州','0919','727100','Yaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610222,610200,'3','宜君县','','宜君','0919','727200','Yijun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610300,610000,'2','宝鸡市','','宝鸡','0917','721000','Baoji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610302,610300,'3','渭滨区','','渭滨','0917','721000','Weibin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610303,610300,'3','金台区','','金台','0917','721000','Jintai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610304,610300,'3','陈仓区','','陈仓','0917','721300','Chencang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610322,610300,'3','凤翔县','','凤翔','0917','721400','Fengxiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610323,610300,'3','岐山县','','岐山','0917','722400','Qishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610324,610300,'3','扶风县','','扶风','0917','722200','Fufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610326,610300,'3','眉县','','眉县','0917','722300','Meixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610327,610300,'3','陇县','','陇县','0917','721200','Longxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610328,610300,'3','千阳县','','千阳','0917','721100','Qianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610329,610300,'3','麟游县','','麟游','0917','721500','Linyou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610330,610300,'3','凤县','','凤县','0917','721700','Fengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610331,610300,'3','太白县','','太白','0917','721600','Taibai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610400,610000,'2','咸阳市','','咸阳','029','712000','Xianyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610402,610400,'3','秦都区','','秦都','029','712000','Qindu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610403,610400,'3','杨陵区','','杨陵','029','712100','Yangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610404,610400,'3','渭城区','','渭城','029','712000','Weicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610422,610400,'3','三原县','','三原','029','713800','Sanyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610423,610400,'3','泾阳县','','泾阳','029','713700','Jingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610424,610400,'3','乾县','','乾县','029','713300','Qianxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610425,610400,'3','礼泉县','','礼泉','029','713200','Liquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610426,610400,'3','永寿县','','永寿','029','713400','Yongshou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610427,610400,'3','彬县','','彬县','029','713500','Binxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610428,610400,'3','长武县','','长武','029','713600','Changwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610429,610400,'3','旬邑县','','旬邑','029','711300','Xunyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610430,610400,'3','淳化县','','淳化','029','711200','Chunhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610431,610400,'3','武功县','','武功','029','712200','Wugong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610481,610400,'3','兴平市','','兴平','029','713100','Xingping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610500,610000,'2','渭南市','','渭南','0913','714000','Weinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610502,610500,'3','临渭区','','临渭','0913','714000','Linwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610521,610500,'3','华县','','华县','0913','714100','Huaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610522,610500,'3','潼关县','','潼关','0913','714300','Tongguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610523,610500,'3','大荔县','','大荔','0913','715100','Dali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610524,610500,'3','合阳县','','合阳','0913','715300','Heyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610525,610500,'3','澄城县','','澄城','0913','715200','Chengcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610526,610500,'3','蒲城县','','蒲城','0913','715500','Pucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610527,610500,'3','白水县','','白水','0913','715600','Baishui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610528,610500,'3','富平县','','富平','0913','711700','Fuping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610581,610500,'3','韩城市','','韩城','0913','715400','Hancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610582,610500,'3','华阴市','','华阴','0913','714200','Huayin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610600,610000,'2','延安市','','延安','0911','716000','Yan an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610602,610600,'3','宝塔区','','宝塔','0911','716000','Baota',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610621,610600,'3','延长县','','延长','0911','717100','Yanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610622,610600,'3','延川县','','延川','0911','717200','Yanchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610623,610600,'3','子长县','','子长','0911','717300','Zichang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610624,610600,'3','安塞县','','安塞','0911','717400','Ansai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610625,610600,'3','志丹县','','志丹','0911','717500','Zhidan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610626,610600,'3','吴起县','','吴起','0911','717600','Wuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610627,610600,'3','甘泉县','','甘泉','0911','716100','Ganquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610628,610600,'3','富县','','富县','0911','727500','Fuxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610629,610600,'3','洛川县','','洛川','0911','727400','Luochuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610630,610600,'3','宜川县','','宜川','0911','716200','Yichuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610631,610600,'3','黄龙县','','黄龙','0911','715700','Huanglong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610632,610600,'3','黄陵县','','黄陵','0911','727300','Huangling',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610700,610000,'2','汉中市','','汉中','0916','723000','Hanzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610702,610700,'3','汉台区','','汉台','0916','723000','Hantai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610721,610700,'3','南郑县','','南郑','0916','723100','Nanzheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610722,610700,'3','城固县','','城固','0916','723200','Chenggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610723,610700,'3','洋县','','洋县','0916','723300','Yangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610724,610700,'3','西乡县','','西乡','0916','723500','Xixiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610725,610700,'3','勉县','','勉县','0916','724200','Mianxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610726,610700,'3','宁强县','','宁强','0916','724400','Ningqiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610727,610700,'3','略阳县','','略阳','0916','724300','Lueyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610728,610700,'3','镇巴县','','镇巴','0916','723600','Zhenba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610729,610700,'3','留坝县','','留坝','0916','724100','Liuba',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610730,610700,'3','佛坪县','','佛坪','0916','723400','Foping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610800,610000,'2','榆林市','','榆林','0912','719000','Yulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610802,610800,'3','榆阳区','','榆阳','0912','719000','Yuyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610821,610800,'3','神木县','','神木','0912','719300','Shenmu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610822,610800,'3','府谷县','','府谷','0912','719400','Fugu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610823,610800,'3','横山县','','横山','0912','719100','Hengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610824,610800,'3','靖边县','','靖边','0912','718500','Jingbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610825,610800,'3','定边县','','定边','0912','718600','Dingbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610826,610800,'3','绥德县','','绥德','0912','718000','Suide',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610827,610800,'3','米脂县','','米脂','0912','718100','Mizhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610828,610800,'3','佳县','','佳县','0912','719200','Jiaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610829,610800,'3','吴堡县','','吴堡','0912','718200','Wubu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610830,610800,'3','清涧县','','清涧','0912','718300','Qingjian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610831,610800,'3','子洲县','','子洲','0912','718400','Zizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610900,610000,'2','安康市','','安康','0915','725000','Ankang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610902,610900,'3','汉滨区','','汉滨','0915','725000','Hanbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610921,610900,'3','汉阴县','','汉阴','0915','725100','Hanyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610922,610900,'3','石泉县','','石泉','0915','725200','Shiquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610923,610900,'3','宁陕县','','宁陕','0915','711600','Ningshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610924,610900,'3','紫阳县','','紫阳','0915','725300','Ziyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610925,610900,'3','岚皋县','','岚皋','0915','725400','Langao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610926,610900,'3','平利县','','平利','0915','725500','Pingli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610927,610900,'3','镇坪县','','镇坪','0915','725600','Zhenping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610928,610900,'3','旬阳县','','旬阳','0915','725700','Xunyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (610929,610900,'3','白河县','','白河','0915','725800','Baihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611000,610000,'2','商洛市','','商洛','0914','726000','Shangluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611002,611000,'3','商州区','','商州','0914','726000','Shangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611021,611000,'3','洛南县','','洛南','0914','726100','Luonan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611022,611000,'3','丹凤县','','丹凤','0914','726200','Danfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611023,611000,'3','商南县','','商南','0914','726300','Shangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611024,611000,'3','山阳县','','山阳','0914','726400','Shanyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611025,611000,'3','镇安县','','镇安','0914','711500','Zhen an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611026,611000,'3','柞水县','','柞水','0914','711400','Zhashui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611100,610000,'2','西咸新区','','西咸','029','712000','Xixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611101,611100,'3','空港新城','','空港','0374','461000','Konggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611102,611100,'3','沣东新城','','沣东','029','710000','Fengdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611103,611100,'3','秦汉新城','','秦汉','029','712000','Qinhan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611104,611100,'3','沣西新城','','沣西','029','710000','Fengxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (611105,611100,'3','泾河新城','','泾河','029','713700','Jinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620000,100000,'1','甘肃省','','甘肃','','','Gansu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620100,620000,'2','兰州市','','兰州','0931','730030','Lanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620102,620100,'3','城关区','','城关','0931','730030','Chengguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620103,620100,'3','七里河区','','七里河','0931','730050','Qilihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620104,620100,'3','西固区','','西固','0931','730060','Xigu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620105,620100,'3','安宁区','','安宁','0931','730070','Anning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620111,620100,'3','红古区','','红古','0931','730084','Honggu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620121,620100,'3','永登县','','永登','0931','730300','Yongdeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620122,620100,'3','皋兰县','','皋兰','0931','730200','Gaolan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620123,620100,'3','榆中县','','榆中','0931','730100','Yuzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620200,620000,'2','嘉峪关市','','嘉峪关','0937','735100','Jiayuguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620201,620200,'3','雄关区','','雄关','0937','735100','Xiongguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620202,620200,'3','长城区','','长城','0937','735106','Changcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620203,620200,'3','镜铁区','','镜铁','0937','735100','Jingtie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620300,620000,'2','金昌市','','金昌','0935','737100','Jinchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620302,620300,'3','金川区','','金川','0935','737100','Jinchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620321,620300,'3','永昌县','','永昌','0935','737200','Yongchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620400,620000,'2','白银市','','白银','0943','730900','Baiyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620402,620400,'3','白银区','','白银','0943','730900','Baiyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620403,620400,'3','平川区','','平川','0943','730913','Pingchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620421,620400,'3','靖远县','','靖远','0943','730600','Jingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620422,620400,'3','会宁县','','会宁','0943','730700','Huining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620423,620400,'3','景泰县','','景泰','0943','730400','Jingtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620500,620000,'2','天水市','','天水','0938','741000','Tianshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620502,620500,'3','秦州区','','秦州','0938','741000','Qinzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620503,620500,'3','麦积区','','麦积','0938','741020','Maiji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620521,620500,'3','清水县','','清水','0938','741400','Qingshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620522,620500,'3','秦安县','','秦安','0938','741600','Qin an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620523,620500,'3','甘谷县','','甘谷','0938','741200','Gangu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620524,620500,'3','武山县','','武山','0938','741300','Wushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620525,620500,'3','张家川回族自治县','','张家川','0938','741500','Zhangjiachuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620600,620000,'2','武威市','','武威','0935','733000','Wuwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620602,620600,'3','凉州区','','凉州','0935','733000','Liangzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620621,620600,'3','民勤县','','民勤','0935','733300','Minqin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620622,620600,'3','古浪县','','古浪','0935','733100','Gulang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620623,620600,'3','天祝藏族自治县','','天祝','0935','733200','Tianzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620700,620000,'2','张掖市','','张掖','0936','734000','Zhangye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620702,620700,'3','甘州区','','甘州','0936','734000','Ganzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620721,620700,'3','肃南裕固族自治县','','肃南','0936','734400','Sunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620722,620700,'3','民乐县','','民乐','0936','734500','Minle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620723,620700,'3','临泽县','','临泽','0936','734200','Linze',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620724,620700,'3','高台县','','高台','0936','734300','Gaotai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620725,620700,'3','山丹县','','山丹','0936','734100','Shandan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620800,620000,'2','平凉市','','平凉','0933','744000','Pingliang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620802,620800,'3','崆峒区','','崆峒','0933','744000','Kongtong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620821,620800,'3','泾川县','','泾川','0933','744300','Jingchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620822,620800,'3','灵台县','','灵台','0933','744400','Lingtai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620823,620800,'3','崇信县','','崇信','0933','744200','Chongxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620824,620800,'3','华亭县','','华亭','0933','744100','Huating',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620825,620800,'3','庄浪县','','庄浪','0933','744600','Zhuanglang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620826,620800,'3','静宁县','','静宁','0933','743400','Jingning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620900,620000,'2','酒泉市','','酒泉','0937','735000','Jiuquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620902,620900,'3','肃州区','','肃州','0937','735000','Suzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620921,620900,'3','金塔县','','金塔','0937','735300','Jinta',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620922,620900,'3','瓜州县','','瓜州','0937','736100','Guazhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620923,620900,'3','肃北蒙古族自治县','','肃北','0937','736300','Subei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620924,620900,'3','阿克塞哈萨克族自治县','','阿克塞','0937','736400','Akesai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620981,620900,'3','玉门市','','玉门','0937','735200','Yumen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (620982,620900,'3','敦煌市','','敦煌','0937','736200','Dunhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621000,620000,'2','庆阳市','','庆阳','0934','745000','Qingyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621002,621000,'3','西峰区','','西峰','0934','745000','Xifeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621021,621000,'3','庆城县','','庆城','0934','745100','Qingcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621022,621000,'3','环县','','环县','0934','745700','Huanxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621023,621000,'3','华池县','','华池','0934','745600','Huachi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621024,621000,'3','合水县','','合水','0934','745400','Heshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621025,621000,'3','正宁县','','正宁','0934','745300','Zhengning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621026,621000,'3','宁县','','宁县','0934','745200','Ningxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621027,621000,'3','镇原县','','镇原','0934','744500','Zhenyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621100,620000,'2','定西市','','定西','0932','743000','Dingxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621102,621100,'3','安定区','','安定','0932','743000','Anding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621121,621100,'3','通渭县','','通渭','0932','743300','Tongwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621122,621100,'3','陇西县','','陇西','0932','748100','Longxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621123,621100,'3','渭源县','','渭源','0932','748200','Weiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621124,621100,'3','临洮县','','临洮','0932','730500','Lintao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621125,621100,'3','漳县','','漳县','0932','748300','Zhangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621126,621100,'3','岷县','','岷县','0932','748400','Minxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621200,620000,'2','陇南市','','陇南','0939','746000','Longnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621202,621200,'3','武都区','','武都','0939','746000','Wudu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621221,621200,'3','成县','','成县','0939','742500','Chengxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621222,621200,'3','文县','','文县','0939','746400','Wenxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621223,621200,'3','宕昌县','','宕昌','0939','748500','Dangchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621224,621200,'3','康县','','康县','0939','746500','Kangxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621225,621200,'3','西和县','','西和','0939','742100','Xihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621226,621200,'3','礼县','','礼县','0939','742200','Lixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621227,621200,'3','徽县','','徽县','0939','742300','Huixian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (621228,621200,'3','两当县','','两当','0939','742400','Liangdang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622900,620000,'2','临夏回族自治州','','临夏','0930','731100','Linxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622901,622900,'3','临夏市','','临夏','0930','731100','Linxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622921,622900,'3','临夏县','','临夏','0930','731800','Linxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622922,622900,'3','康乐县','','康乐','0930','731500','Kangle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622923,622900,'3','永靖县','','永靖','0930','731600','Yongjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622924,622900,'3','广河县','','广河','0930','731300','Guanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622925,622900,'3','和政县','','和政','0930','731200','Hezheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622926,622900,'3','东乡族自治县','','东乡族','0930','731400','Dongxiangzu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (622927,622900,'3','积石山保安族东乡族撒拉族自治县','','积石山','0930','731700','Jishishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623000,620000,'2','甘南藏族自治州','','甘南','0941','747000','Gannan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623001,623000,'3','合作市','','合作','0941','747000','Hezuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623021,623000,'3','临潭县','','临潭','0941','747500','Lintan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623022,623000,'3','卓尼县','','卓尼','0941','747600','Zhuoni',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623023,623000,'3','舟曲县','','舟曲','0941','746300','Zhouqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623024,623000,'3','迭部县','','迭部','0941','747400','Diebu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623025,623000,'3','玛曲县','','玛曲','0941','747300','Maqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623026,623000,'3','碌曲县','','碌曲','0941','747200','Luqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (623027,623000,'3','夏河县','','夏河','0941','747100','Xiahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630000,100000,'1','青海省','','青海','','','Qinghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630100,630000,'2','西宁市','','西宁','0971','810000','Xining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630102,630100,'3','城东区','','城东','0971','810007','Chengdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630103,630100,'3','城中区','','城中','0971','810000','Chengzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630104,630100,'3','城西区','','城西','0971','810001','Chengxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630105,630100,'3','城北区','','城北','0971','810003','Chengbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630121,630100,'3','大通回族土族自治县','','大通','0971','810100','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630122,630100,'3','湟中县','','湟中','0971','811600','Huangzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630123,630100,'3','湟源县','','湟源','0971','812100','Huangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630200,630000,'2','海东市','','海东','0972','810700','Haidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630202,630200,'3','乐都区','','乐都','0972','810700','Ledu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630221,630200,'3','平安县','','平安','0972','810600','Ping an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630222,630200,'3','民和回族土族自治县','','民和','0972','810800','Minhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630223,630200,'3','互助土族自治县','','互助','0972','810500','Huzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630224,630200,'3','化隆回族自治县','','化隆','0972','810900','Hualong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (630225,630200,'3','循化撒拉族自治县','','循化','0972','811100','Xunhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632200,630000,'2','海北藏族自治州','','海北','0970','812200','Haibei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632221,632200,'3','门源回族自治县','','门源','0970','810300','Menyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632222,632200,'3','祁连县','','祁连','0970','810400','Qilian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632223,632200,'3','海晏县','','海晏','0970','812200','Haiyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632224,632200,'3','刚察县','','刚察','0970','812300','Gangcha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632300,630000,'2','黄南藏族自治州','','黄南','0973','811300','Huangnan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632321,632300,'3','同仁县','','同仁','0973','811300','Tongren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632322,632300,'3','尖扎县','','尖扎','0973','811200','Jianzha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632323,632300,'3','泽库县','','泽库','0973','811400','Zeku',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632324,632300,'3','河南蒙古族自治县','','河南','0973','811500','Henan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632500,630000,'2','海南藏族自治州','','海南','0974','813000','Hainan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632521,632500,'3','共和县','','共和','0974','813000','Gonghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632522,632500,'3','同德县','','同德','0974','813200','Tongde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632523,632500,'3','贵德县','','贵德','0974','811700','Guide',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632524,632500,'3','兴海县','','兴海','0974','813300','Xinghai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632525,632500,'3','贵南县','','贵南','0974','813100','Guinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632600,630000,'2','果洛藏族自治州','','果洛','0975','814000','Golog',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632621,632600,'3','玛沁县','','玛沁','0975','814000','Maqin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632622,632600,'3','班玛县','','班玛','0975','814300','Banma',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632623,632600,'3','甘德县','','甘德','0975','814100','Gande',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632624,632600,'3','达日县','','达日','0975','814200','Dari',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632625,632600,'3','久治县','','久治','0975','624700','Jiuzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632626,632600,'3','玛多县','','玛多','0975','813500','Maduo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632700,630000,'2','玉树藏族自治州','','玉树','0976','815000','Yushu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632701,632700,'3','玉树市','','玉树','0976','815000','Yushu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632722,632700,'3','杂多县','','杂多','0976','815300','Zaduo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632723,632700,'3','称多县','','称多','0976','815100','Chenduo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632724,632700,'3','治多县','','治多','0976','815400','Zhiduo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632725,632700,'3','囊谦县','','囊谦','0976','815200','Nangqian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632726,632700,'3','曲麻莱县','','曲麻莱','0976','815500','Qumalai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632800,630000,'2','海西蒙古族藏族自治州','','海西','0977','817000','Haixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632801,632800,'3','格尔木市','','格尔木','0977','816000','Geermu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632802,632800,'3','德令哈市','','德令哈','0977','817000','Delingha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632821,632800,'3','乌兰县','','乌兰','0977','817100','Wulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632822,632800,'3','都兰县','','都兰','0977','816100','Dulan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (632823,632800,'3','天峻县','','天峻','0977','817200','Tianjun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640000,100000,'1','宁夏回族自治区','','宁夏','','','Ningxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640100,640000,'2','银川市','','银川','0951','750004','Yinchuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640104,640100,'3','兴庆区','','兴庆','0951','750001','Xingqing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640105,640100,'3','西夏区','','西夏','0951','750021','Xixia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640106,640100,'3','金凤区','','金凤','0951','750011','Jinfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640121,640100,'3','永宁县','','永宁','0951','750100','Yongning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640122,640100,'3','贺兰县','','贺兰','0951','750200','Helan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640181,640100,'3','灵武市','','灵武','0951','750004','Lingwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640200,640000,'2','石嘴山市','','石嘴山','0952','753000','Shizuishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640202,640200,'3','大武口区','','大武口','0952','753000','Dawukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640205,640200,'3','惠农区','','惠农','0952','753600','Huinong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640221,640200,'3','平罗县','','平罗','0952','753400','Pingluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640300,640000,'2','吴忠市','','吴忠','0953','751100','Wuzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640302,640300,'3','利通区','','利通','0953','751100','Litong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640303,640300,'3','红寺堡区','','红寺堡','0953','751900','Hongsibao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640323,640300,'3','盐池县','','盐池','0953','751500','Yanchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640324,640300,'3','同心县','','同心','0953','751300','Tongxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640381,640300,'3','青铜峡市','','青铜峡','0953','751600','Qingtongxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640400,640000,'2','固原市','','固原','0954','756000','Guyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640402,640400,'3','原州区','','原州','0954','756000','Yuanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640422,640400,'3','西吉县','','西吉','0954','756200','Xiji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640423,640400,'3','隆德县','','隆德','0954','756300','Longde',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640424,640400,'3','泾源县','','泾源','0954','756400','Jingyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640425,640400,'3','彭阳县','','彭阳','0954','756500','Pengyang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640500,640000,'2','中卫市','','中卫','0955','751700','Zhongwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640502,640500,'3','沙坡头区','','沙坡头','0955','755000','Shapotou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640521,640500,'3','中宁县','','中宁','0955','751200','Zhongning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (640522,640500,'3','海原县','','海原','0955','751800','Haiyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650000,100000,'1','新疆维吾尔自治区','','新疆','','','Xinjiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650100,650000,'2','乌鲁木齐市','','乌鲁木齐','0991','830002','Urumqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650102,650100,'3','天山区','','天山','0991','830002','Tianshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650103,650100,'3','沙依巴克区','','沙依巴克','0991','830000','Shayibake',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650104,650100,'3','新市区','','新市','0991','830011','Xinshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650105,650100,'3','水磨沟区','','水磨沟','0991','830017','Shuimogou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650106,650100,'3','头屯河区','','头屯河','0991','830022','Toutunhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650107,650100,'3','达坂城区','','达坂城','0991','830039','Dabancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650109,650100,'3','米东区','','米东','0991','830019','Midong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650121,650100,'3','乌鲁木齐县','','乌鲁木齐','0991','830063','Wulumuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650200,650000,'2','克拉玛依市','','克拉玛依','0990','834000','Karamay',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650202,650200,'3','独山子区','','独山子','0992','834021','Dushanzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650203,650200,'3','克拉玛依区','','克拉玛依','0990','834000','Kelamayi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650204,650200,'3','白碱滩区','','白碱滩','0990','834008','Baijiantan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (650205,650200,'3','乌尔禾区','','乌尔禾','0990','834012','Wuerhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652100,650000,'2','吐鲁番地区','','吐鲁番','0995','838000','Turpan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652101,652100,'3','吐鲁番市','','吐鲁番','0995','838000','Tulufan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652122,652100,'3','鄯善县','','鄯善','0995','838200','Shanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652123,652100,'3','托克逊县','','托克逊','0995','838100','Tuokexun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652200,650000,'2','哈密地区','','哈密','0902','839000','Hami',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652201,652200,'3','哈密市','','哈密','0902','839000','Hami',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652222,652200,'3','巴里坤哈萨克自治县','','巴里坤','0902','839200','Balikun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652223,652200,'3','伊吾县','','伊吾','0902','839300','Yiwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652300,650000,'2','昌吉回族自治州','','昌吉','0994','831100','Changji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652301,652300,'3','昌吉市','','昌吉','0994','831100','Changji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652302,652300,'3','阜康市','','阜康','0994','831500','Fukang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652323,652300,'3','呼图壁县','','呼图壁','0994','831200','Hutubi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652324,652300,'3','玛纳斯县','','玛纳斯','0994','832200','Manasi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652325,652300,'3','奇台县','','奇台','0994','831800','Qitai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652327,652300,'3','吉木萨尔县','','吉木萨尔','0994','831700','Jimusaer',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652328,652300,'3','木垒哈萨克自治县','','木垒','0994','831900','Mulei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652700,650000,'2','博尔塔拉蒙古自治州','','博尔塔拉','0909','833400','Bortala',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652701,652700,'3','博乐市','','博乐','0909','833400','Bole',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652702,652700,'3','阿拉山口市','','阿拉山口','0909','833400','Alashankou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652722,652700,'3','精河县','','精河','0909','833300','Jinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652723,652700,'3','温泉县','','温泉','0909','833500','Wenquan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652800,650000,'2','巴音郭楞蒙古自治州','','巴音郭楞','0996','841000','Bayingol',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652801,652800,'3','库尔勒市','','库尔勒','0996','841000','Kuerle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652822,652800,'3','轮台县','','轮台','0996','841600','Luntai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652823,652800,'3','尉犁县','','尉犁','0996','841500','Yuli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652824,652800,'3','若羌县','','若羌','0996','841800','Ruoqiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652825,652800,'3','且末县','','且末','0996','841900','Qiemo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652826,652800,'3','焉耆回族自治县','','焉耆','0996','841100','Yanqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652827,652800,'3','和静县','','和静','0996','841300','Hejing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652828,652800,'3','和硕县','','和硕','0996','841200','Heshuo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652829,652800,'3','博湖县','','博湖','0996','841400','Bohu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652900,650000,'2','阿克苏地区','','阿克苏','0997','843000','Aksu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652901,652900,'3','阿克苏市','','阿克苏','0997','843000','Akesu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652922,652900,'3','温宿县','','温宿','0997','843100','Wensu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652923,652900,'3','库车县','','库车','0997','842000','Kuche',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652924,652900,'3','沙雅县','','沙雅','0997','842200','Shaya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652925,652900,'3','新和县','','新和','0997','842100','Xinhe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652926,652900,'3','拜城县','','拜城','0997','842300','Baicheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652927,652900,'3','乌什县','','乌什','0997','843400','Wushi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652928,652900,'3','阿瓦提县','','阿瓦提','0997','843200','Awati',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (652929,652900,'3','柯坪县','','柯坪','0997','843600','Keping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653000,650000,'2','克孜勒苏柯尔克孜自治州','','克孜勒苏','0908','845350','Kizilsu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653001,653000,'3','阿图什市','','阿图什','0908','845350','Atushi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653022,653000,'3','阿克陶县','','阿克陶','0908','845550','Aketao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653023,653000,'3','阿合奇县','','阿合奇','0997','843500','Aheqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653024,653000,'3','乌恰县','','乌恰','0908','845450','Wuqia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653100,650000,'2','喀什地区','','喀什','0998','844000','Kashgar',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653101,653100,'3','喀什市','','喀什','0998','844000','Kashi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653121,653100,'3','疏附县','','疏附','0998','844100','Shufu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653122,653100,'3','疏勒县','','疏勒','0998','844200','Shule',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653123,653100,'3','英吉沙县','','英吉沙','0998','844500','Yingjisha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653124,653100,'3','泽普县','','泽普','0998','844800','Zepu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653125,653100,'3','莎车县','','莎车','0998','844700','Shache',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653126,653100,'3','叶城县','','叶城','0998','844900','Yecheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653127,653100,'3','麦盖提县','','麦盖提','0998','844600','Maigaiti',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653128,653100,'3','岳普湖县','','岳普湖','0998','844400','Yuepuhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653129,653100,'3','伽师县','','伽师','0998','844300','Jiashi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653130,653100,'3','巴楚县','','巴楚','0998','843800','Bachu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653131,653100,'3','塔什库尔干塔吉克自治县','','塔什库尔干塔吉克','0998','845250','Tashikuergantajike',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653200,650000,'2','和田地区','','和田','0903','848000','Hotan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653201,653200,'3','和田市','','和田市','0903','848000','Hetianshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653221,653200,'3','和田县','','和田县','0903','848000','Hetianxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653222,653200,'3','墨玉县','','墨玉','0903','848100','Moyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653223,653200,'3','皮山县','','皮山','0903','845150','Pishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653224,653200,'3','洛浦县','','洛浦','0903','848200','Luopu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653225,653200,'3','策勒县','','策勒','0903','848300','Cele',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653226,653200,'3','于田县','','于田','0903','848400','Yutian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (653227,653200,'3','民丰县','','民丰','0903','848500','Minfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654000,650000,'2','伊犁哈萨克自治州','','伊犁','0999','835100','Ili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654002,654000,'3','伊宁市','','伊宁','0999','835000','Yining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654003,654000,'3','奎屯市','','奎屯','0992','833200','Kuitun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654004,654000,'3','霍尔果斯市','','霍尔果斯','0999','835221','Huoerguosi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654021,654000,'3','伊宁县','','伊宁','0999','835100','Yining',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654022,654000,'3','察布查尔锡伯自治县','','察布查尔锡伯','0999','835300','Chabuchaerxibo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654023,654000,'3','霍城县','','霍城','0999','835200','Huocheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654024,654000,'3','巩留县','','巩留','0999','835400','Gongliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654025,654000,'3','新源县','','新源','0999','835800','Xinyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654026,654000,'3','昭苏县','','昭苏','0999','835600','Zhaosu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654027,654000,'3','特克斯县','','特克斯','0999','835500','Tekesi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654028,654000,'3','尼勒克县','','尼勒克','0999','835700','Nileke',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654200,650000,'2','塔城地区','','塔城','0901','834700','Qoqek',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654201,654200,'3','塔城市','','塔城','0901','834700','Tacheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654202,654200,'3','乌苏市','','乌苏','0992','833000','Wusu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654221,654200,'3','额敏县','','额敏','0901','834600','Emin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654223,654200,'3','沙湾县','','沙湾','0993','832100','Shawan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654224,654200,'3','托里县','','托里','0901','834500','Tuoli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654225,654200,'3','裕民县','','裕民','0901','834800','Yumin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654226,654200,'3','和布克赛尔蒙古自治县','','和布克赛尔','0990','834400','Hebukesaier',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654300,650000,'2','阿勒泰地区','','阿勒泰','0906','836500','Altay',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654301,654300,'3','阿勒泰市','','阿勒泰','0906','836500','Aletai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654321,654300,'3','布尔津县','','布尔津','0906','836600','Buerjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654322,654300,'3','富蕴县','','富蕴','0906','836100','Fuyun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654323,654300,'3','福海县','','福海','0906','836400','Fuhai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654324,654300,'3','哈巴河县','','哈巴河','0906','836700','Habahe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654325,654300,'3','青河县','','青河','0906','836200','Qinghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (654326,654300,'3','吉木乃县','','吉木乃','0906','836800','Jimunai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659000,650000,'2','直辖县级','',' ','','','',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659001,659000,'3','石河子市','','石河子','0993','832000','Shihezi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659002,659000,'3','阿拉尔市','','阿拉尔','0997','843300','Aral',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659003,659000,'3','图木舒克市','','图木舒克','0998','843806','Tumxuk',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659004,659000,'3','五家渠市','','五家渠','0994','831300','Wujiaqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659005,659000,'3','北屯市','','北屯','0906','836000','Beitun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659006,659000,'3','铁门关市','','铁门关','0906','836000','Tiemenguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (659007,659000,'3','双河市','','双河','0909','833408','Shuanghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710000,100000,'1','台湾','','台湾','','','Taiwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710100,710000,'2','台北市','','台北','02','1','Taipei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710101,710100,'3','松山区','','松山','02','105','Songshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710102,710100,'3','信义区','','信义','02','110','Xinyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710103,710100,'3','大安区','','大安','02','106','Da an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710104,710100,'3','中山区','','中山','02','104','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710105,710100,'3','中正区','','中正','02','100','Zhongzheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710106,710100,'3','大同区','','大同','02','103','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710107,710100,'3','万华区','','万华','02','108','Wanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710108,710100,'3','文山区','','文山','02','116','Wenshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710109,710100,'3','南港区','','南港','02','115','Nangang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710110,710100,'3','内湖区','','内湖','02','114','Nahu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710111,710100,'3','士林区','','士林','02','111','Shilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710112,710100,'3','北投区','','北投','02','112','Beitou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710200,710000,'2','高雄市','','高雄','07','8','Kaohsiung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710201,710200,'3','盐埕区','','盐埕','07','803','Yancheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710202,710200,'3','鼓山区','','鼓山','07','804','Gushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710203,710200,'3','左营区','','左营','07','813','Zuoying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710204,710200,'3','楠梓区','','楠梓','07','811','Nanzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710205,710200,'3','三民区','','三民','07','807','Sanmin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710206,710200,'3','新兴区','','新兴','07','800','Xinxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710207,710200,'3','前金区','','前金','07','801','Qianjin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710208,710200,'3','苓雅区','','苓雅','07','802','Lingya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710209,710200,'3','前镇区','','前镇','07','806','Qianzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710210,710200,'3','旗津区','','旗津','07','805','Qijin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710211,710200,'3','小港区','','小港','07','812','Xiaogang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710212,710200,'3','凤山区','','凤山','07','830','Fengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710213,710200,'3','林园区','','林园','07','832','Linyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710214,710200,'3','大寮区','','大寮','07','831','Daliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710215,710200,'3','大树区','','大树','07','840','Dashu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710216,710200,'3','大社区','','大社','07','815','Dashe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710217,710200,'3','仁武区','','仁武','07','814','Renwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710218,710200,'3','鸟松区','','鸟松','07','833','Niaosong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710219,710200,'3','冈山区','','冈山','07','820','Gangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710220,710200,'3','桥头区','','桥头','07','825','Qiaotou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710221,710200,'3','燕巢区','','燕巢','07','824','Yanchao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710222,710200,'3','田寮区','','田寮','07','823','Tianliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710223,710200,'3','阿莲区','','阿莲','07','822','Alian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710224,710200,'3','路竹区','','路竹','07','821','Luzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710225,710200,'3','湖内区','','湖内','07','829','Huna',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710226,710200,'3','茄萣区','','茄萣','07','852','Qieding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710227,710200,'3','永安区','','永安','07','828','Yong an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710228,710200,'3','弥陀区','','弥陀','07','827','Mituo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710229,710200,'3','梓官区','','梓官','07','826','Ziguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710230,710200,'3','旗山区','','旗山','07','842','Qishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710231,710200,'3','美浓区','','美浓','07','843','Meinong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710232,710200,'3','六龟区','','六龟','07','844','Liugui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710233,710200,'3','甲仙区','','甲仙','07','847','Jiaxian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710234,710200,'3','杉林区','','杉林','07','846','Shanlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710235,710200,'3','内门区','','内门','07','845','Namen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710236,710200,'3','茂林区','','茂林','07','851','Maolin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710237,710200,'3','桃源区','','桃源','07','848','Taoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710238,710200,'3','那玛夏区','','那玛夏','07','849','Namaxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710300,710000,'2','基隆市','','基隆','02','2','Keelung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710301,710300,'3','中正区','','中正','02','202','Zhongzheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710302,710300,'3','七堵区','','七堵','02','206','Qidu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710303,710300,'3','暖暖区','','暖暖','02','205','Nuannuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710304,710300,'3','仁爱区','','仁爱','02','200','Renai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710305,710300,'3','中山区','','中山','02','203','Zhongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710306,710300,'3','安乐区','','安乐','02','204','Anle',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710307,710300,'3','信义区','','信义','02','201','Xinyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710400,710000,'2','台中市','','台中','04','4','Taichung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710401,710400,'3','中区','','中区','04','400','Zhongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710402,710400,'3','东区','','东区','04','401','Dongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710403,710400,'3','南区','','南区','04','402','Nanqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710404,710400,'3','西区','','西区','04','403','Xiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710405,710400,'3','北区','','北区','04','404','Beiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710406,710400,'3','西屯区','','西屯','04','407','Xitun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710407,710400,'3','南屯区','','南屯','04','408','Nantun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710408,710400,'3','北屯区','','北屯','04','406','Beitun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710409,710400,'3','丰原区','','丰原','04','420','Fengyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710410,710400,'3','东势区','','东势','04','423','Dongshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710411,710400,'3','大甲区','','大甲','04','437','Dajia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710412,710400,'3','清水区','','清水','04','436','Qingshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710413,710400,'3','沙鹿区','','沙鹿','04','433','Shalu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710414,710400,'3','梧栖区','','梧栖','04','435','Wuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710415,710400,'3','后里区','','后里','04','421','Houli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710416,710400,'3','神冈区','','神冈','04','429','Shengang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710417,710400,'3','潭子区','','潭子','04','427','Tanzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710418,710400,'3','大雅区','','大雅','04','428','Daya',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710419,710400,'3','新社区','','新社','04','426','Xinshe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710420,710400,'3','石冈区','','石冈','04','422','Shigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710421,710400,'3','外埔区','','外埔','04','438','Waipu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710422,710400,'3','大安区','','大安','04','439','Da an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710423,710400,'3','乌日区','','乌日','04','414','Wuri',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710424,710400,'3','大肚区','','大肚','04','432','Dadu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710425,710400,'3','龙井区','','龙井','04','434','Longjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710426,710400,'3','雾峰区','','雾峰','04','413','Wufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710427,710400,'3','太平区','','太平','04','411','Taiping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710428,710400,'3','大里区','','大里','04','412','Dali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710429,710400,'3','和平区','','和平','04','424','Heping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710500,710000,'2','台南市','','台南','06','7','Tainan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710501,710500,'3','东区','','东区','06','701','Dongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710502,710500,'3','南区','','南区','06','702','Nanqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710504,710500,'3','北区','','北区','06','704','Beiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710506,710500,'3','安南区','','安南','06','709','Annan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710507,710500,'3','安平区','','安平','06','708','Anping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710508,710500,'3','中西区','','中西','06','700','Zhongxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710509,710500,'3','新营区','','新营','06','730','Xinying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710510,710500,'3','盐水区','','盐水','06','737','Yanshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710511,710500,'3','白河区','','白河','06','732','Baihe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710512,710500,'3','柳营区','','柳营','06','736','Liuying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710513,710500,'3','后壁区','','后壁','06','731','Houbi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710514,710500,'3','东山区','','东山','06','733','Dongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710515,710500,'3','麻豆区','','麻豆','06','721','Madou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710516,710500,'3','下营区','','下营','06','735','Xiaying',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710517,710500,'3','六甲区','','六甲','06','734','Liujia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710518,710500,'3','官田区','','官田','06','720','Guantian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710519,710500,'3','大内区','','大内','06','742','Dana',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710520,710500,'3','佳里区','','佳里','06','722','Jiali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710521,710500,'3','学甲区','','学甲','06','726','Xuejia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710522,710500,'3','西港区','','西港','06','723','Xigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710523,710500,'3','七股区','','七股','06','724','Qigu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710524,710500,'3','将军区','','将军','06','725','Jiangjun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710525,710500,'3','北门区','','北门','06','727','Beimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710526,710500,'3','新化区','','新化','06','712','Xinhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710527,710500,'3','善化区','','善化','06','741','Shanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710528,710500,'3','新市区','','新市','06','744','Xinshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710529,710500,'3','安定区','','安定','06','745','Anding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710530,710500,'3','山上区','','山上','06','743','Shanshang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710531,710500,'3','玉井区','','玉井','06','714','Yujing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710532,710500,'3','楠西区','','楠西','06','715','Nanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710533,710500,'3','南化区','','南化','06','716','Nanhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710534,710500,'3','左镇区','','左镇','06','713','Zuozhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710535,710500,'3','仁德区','','仁德','06','717','Rende',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710536,710500,'3','归仁区','','归仁','06','711','Guiren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710537,710500,'3','关庙区','','关庙','06','718','Guanmiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710538,710500,'3','龙崎区','','龙崎','06','719','Longqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710539,710500,'3','永康区','','永康','06','710','Yongkang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710600,710000,'2','新竹市','','新竹','03','3','Hsinchu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710601,710600,'3','东区','','东区','03','300','Dongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710602,710600,'3','北区','','北区','03','','Beiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710603,710600,'3','香山区','','香山','03','','Xiangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710700,710000,'2','嘉义市','','嘉义','05','6','Chiayi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710701,710700,'3','东区','','东区','05','600','Dongqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710702,710700,'3','西区','','西区','05','600','Xiqu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710800,710000,'2','新北市','','新北','02','2','New Taipei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710801,710800,'3','板桥区','','板桥','02','220','Banqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710802,710800,'3','三重区','','三重','02','241','Sanzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710803,710800,'3','中和区','','中和','02','235','Zhonghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710804,710800,'3','永和区','','永和','02','234','Yonghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710805,710800,'3','新庄区','','新庄','02','242','Xinzhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710806,710800,'3','新店区','','新店','02','231','Xindian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710807,710800,'3','树林区','','树林','02','238','Shulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710808,710800,'3','莺歌区','','莺歌','02','239','Yingge',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710809,710800,'3','三峡区','','三峡','02','237','Sanxia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710810,710800,'3','淡水区','','淡水','02','251','Danshui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710811,710800,'3','汐止区','','汐止','02','221','Xizhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710812,710800,'3','瑞芳区','','瑞芳','02','224','Ruifang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710813,710800,'3','土城区','','土城','02','236','Tucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710814,710800,'3','芦洲区','','芦洲','02','247','Luzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710815,710800,'3','五股区','','五股','02','248','Wugu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710816,710800,'3','泰山区','','泰山','02','243','Taishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710817,710800,'3','林口区','','林口','02','244','Linkou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710818,710800,'3','深坑区','','深坑','02','222','Shenkeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710819,710800,'3','石碇区','','石碇','02','223','Shiding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710820,710800,'3','坪林区','','坪林','02','232','Pinglin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710821,710800,'3','三芝区','','三芝','02','252','Sanzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710822,710800,'3','石门区','','石门','02','253','Shimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710823,710800,'3','八里区','','八里','02','249','Bali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710824,710800,'3','平溪区','','平溪','02','226','Pingxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710825,710800,'3','双溪区','','双溪','02','227','Shuangxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710826,710800,'3','贡寮区','','贡寮','02','228','Gongliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710827,710800,'3','金山区','','金山','02','208','Jinshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710828,710800,'3','万里区','','万里','02','207','Wanli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (710829,710800,'3','乌来区','','乌来','02','233','Wulai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712200,710000,'2','宜兰县','','宜兰','03','2','Yilan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712201,712200,'3','宜兰市','','宜兰','03','260','Yilan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712221,712200,'3','罗东镇','','罗东','03','265','Luodong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712222,712200,'3','苏澳镇','','苏澳','03','270','Suao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712223,712200,'3','头城镇','','头城','03','261','Toucheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712224,712200,'3','礁溪乡','','礁溪','03','262','Jiaoxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712225,712200,'3','壮围乡','','壮围','03','263','Zhuangwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712226,712200,'3','员山乡','','员山','03','264','Yuanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712227,712200,'3','冬山乡','','冬山','03','269','Dongshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712228,712200,'3','五结乡','','五结','03','268','Wujie',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712229,712200,'3','三星乡','','三星','03','266','Sanxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712230,712200,'3','大同乡','','大同','03','267','Datong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712231,712200,'3','南澳乡','','南澳','03','272','Nanao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712300,710000,'2','桃园县','','桃园','03','3','Taoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712301,712300,'3','桃园市','','桃园','03','330','Taoyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712302,712300,'3','中坜市','','中坜','03','320','Zhongli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712303,712300,'3','平镇市','','平镇','03','324','Pingzhen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712304,712300,'3','八德市','','八德','03','334','Bade',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712305,712300,'3','杨梅市','','杨梅','03','326','Yangmei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712306,712300,'3','芦竹市','','芦竹','03','338','Luzhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712321,712300,'3','大溪镇','','大溪','03','335','Daxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712324,712300,'3','大园乡','','大园','03','337','Dayuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712325,712300,'3','龟山乡','','龟山','03','333','Guishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712327,712300,'3','龙潭乡','','龙潭','03','325','Longtan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712329,712300,'3','新屋乡','','新屋','03','327','Xinwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712330,712300,'3','观音乡','','观音','03','328','Guanyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712331,712300,'3','复兴乡','','复兴','03','336','Fuxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712400,710000,'2','新竹县','','新竹','03','3','Hsinchu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712401,712400,'3','竹北市','','竹北','03','302','Zhubei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712421,712400,'3','竹东镇','','竹东','03','310','Zhudong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712422,712400,'3','新埔镇','','新埔','03','305','Xinpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712423,712400,'3','关西镇','','关西','03','306','Guanxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712424,712400,'3','湖口乡','','湖口','03','303','Hukou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712425,712400,'3','新丰乡','','新丰','03','304','Xinfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712426,712400,'3','芎林乡','','芎林','03','307','Xionglin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712427,712400,'3','横山乡','','横山','03','312','Hengshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712428,712400,'3','北埔乡','','北埔','03','314','Beipu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712429,712400,'3','宝山乡','','宝山','03','308','Baoshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712430,712400,'3','峨眉乡','','峨眉','03','315','Emei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712431,712400,'3','尖石乡','','尖石','03','313','Jianshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712432,712400,'3','五峰乡','','五峰','03','311','Wufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712500,710000,'2','苗栗县','','苗栗','037','3','Miaoli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712501,712500,'3','苗栗市','','苗栗','037','360','Miaoli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712521,712500,'3','苑里镇','','苑里','037','358','Yuanli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712522,712500,'3','通霄镇','','通霄','037','357','Tongxiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712523,712500,'3','竹南镇','','竹南','037','350','Zhunan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712524,712500,'3','头份镇','','头份','037','351','Toufen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712525,712500,'3','后龙镇','','后龙','037','356','Houlong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712526,712500,'3','卓兰镇','','卓兰','037','369','Zhuolan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712527,712500,'3','大湖乡','','大湖','037','364','Dahu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712528,712500,'3','公馆乡','','公馆','037','363','Gongguan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712529,712500,'3','铜锣乡','','铜锣','037','366','Tongluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712530,712500,'3','南庄乡','','南庄','037','353','Nanzhuang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712531,712500,'3','头屋乡','','头屋','037','362','Touwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712532,712500,'3','三义乡','','三义','037','367','Sanyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712533,712500,'3','西湖乡','','西湖','037','368','Xihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712534,712500,'3','造桥乡','','造桥','037','361','Zaoqiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712535,712500,'3','三湾乡','','三湾','037','352','Sanwan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712536,712500,'3','狮潭乡','','狮潭','037','354','Shitan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712537,712500,'3','泰安乡','','泰安','037','365','Tai an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712700,710000,'2','彰化县','','彰化','04','5','Changhua',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712701,712700,'3','彰化市','','彰化市','04','500','Zhanghuashi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712721,712700,'3','鹿港镇','','鹿港','04','505','Lugang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712722,712700,'3','和美镇','','和美','04','508','Hemei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712723,712700,'3','线西乡','','线西','04','507','Xianxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712724,712700,'3','伸港乡','','伸港','04','509','Shengang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712725,712700,'3','福兴乡','','福兴','04','506','Fuxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712726,712700,'3','秀水乡','','秀水','04','504','Xiushui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712727,712700,'3','花坛乡','','花坛','04','503','Huatan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712728,712700,'3','芬园乡','','芬园','04','502','Fenyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712729,712700,'3','员林镇','','员林','04','510','Yuanlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712730,712700,'3','溪湖镇','','溪湖','04','514','Xihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712731,712700,'3','田中镇','','田中','04','520','Tianzhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712732,712700,'3','大村乡','','大村','04','515','Dacun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712733,712700,'3','埔盐乡','','埔盐','04','516','Puyan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712734,712700,'3','埔心乡','','埔心','04','513','Puxin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712735,712700,'3','永靖乡','','永靖','04','512','Yongjing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712736,712700,'3','社头乡','','社头','04','511','Shetou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712737,712700,'3','二水乡','','二水','04','530','Ershui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712738,712700,'3','北斗镇','','北斗','04','521','Beidou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712739,712700,'3','二林镇','','二林','04','526','Erlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712740,712700,'3','田尾乡','','田尾','04','522','Tianwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712741,712700,'3','埤头乡','','埤头','04','523','Pitou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712742,712700,'3','芳苑乡','','芳苑','04','528','Fangyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712743,712700,'3','大城乡','','大城','04','527','Dacheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712744,712700,'3','竹塘乡','','竹塘','04','525','Zhutang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712745,712700,'3','溪州乡','','溪州','04','524','Xizhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712800,710000,'2','南投县','','南投','049','5','Nantou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712801,712800,'3','南投市','','南投市','049','540','Nantoushi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712821,712800,'3','埔里镇','','埔里','049','545','Puli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712822,712800,'3','草屯镇','','草屯','049','542','Caotun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712823,712800,'3','竹山镇','','竹山','049','557','Zhushan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712824,712800,'3','集集镇','','集集','049','552','Jiji',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712825,712800,'3','名间乡','','名间','049','551','Mingjian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712826,712800,'3','鹿谷乡','','鹿谷','049','558','Lugu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712827,712800,'3','中寮乡','','中寮','049','541','Zhongliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712828,712800,'3','鱼池乡','','鱼池','049','555','Yuchi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712829,712800,'3','国姓乡','','国姓','049','544','Guoxing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712830,712800,'3','水里乡','','水里','049','553','Shuili',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712831,712800,'3','信义乡','','信义','049','556','Xinyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712832,712800,'3','仁爱乡','','仁爱','049','546','Renai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712900,710000,'2','云林县','','云林','05','6','Yunlin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712901,712900,'3','斗六市','','斗六','05','640','Douliu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712921,712900,'3','斗南镇','','斗南','05','630','Dounan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712922,712900,'3','虎尾镇','','虎尾','05','632','Huwei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712923,712900,'3','西螺镇','','西螺','05','648','Xiluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712924,712900,'3','土库镇','','土库','05','633','Tuku',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712925,712900,'3','北港镇','','北港','05','651','Beigang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712926,712900,'3','古坑乡','','古坑','05','646','Gukeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712927,712900,'3','大埤乡','','大埤','05','631','Dapi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712928,712900,'3','莿桐乡','','莿桐','05','647','Citong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712929,712900,'3','林内乡','','林内','05','643','Linna',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712930,712900,'3','二仑乡','','二仑','05','649','Erlun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712931,712900,'3','仑背乡','','仑背','05','637','Lunbei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712932,712900,'3','麦寮乡','','麦寮','05','638','Mailiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712933,712900,'3','东势乡','','东势','05','635','Dongshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712934,712900,'3','褒忠乡','','褒忠','05','634','Baozhong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712935,712900,'3','台西乡','','台西','05','636','Taixi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712936,712900,'3','元长乡','','元长','05','655','Yuanchang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712937,712900,'3','四湖乡','','四湖','05','654','Sihu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712938,712900,'3','口湖乡','','口湖','05','653','Kouhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (712939,712900,'3','水林乡','','水林','05','652','Shuilin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713000,710000,'2','嘉义县','','嘉义','05','6','Chiayi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713001,713000,'3','太保市','','太保','05','612','Taibao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713002,713000,'3','朴子市','','朴子','05','613','Puzi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713023,713000,'3','布袋镇','','布袋','05','625','Budai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713024,713000,'3','大林镇','','大林','05','622','Dalin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713025,713000,'3','民雄乡','','民雄','05','621','Minxiong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713026,713000,'3','溪口乡','','溪口','05','623','Xikou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713027,713000,'3','新港乡','','新港','05','616','Xingang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713028,713000,'3','六脚乡','','六脚','05','615','Liujiao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713029,713000,'3','东石乡','','东石','05','614','Dongshi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713030,713000,'3','义竹乡','','义竹','05','624','Yizhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713031,713000,'3','鹿草乡','','鹿草','05','611','Lucao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713032,713000,'3','水上乡','','水上','05','608','Shuishang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713033,713000,'3','中埔乡','','中埔','05','606','Zhongpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713034,713000,'3','竹崎乡','','竹崎','05','604','Zhuqi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713035,713000,'3','梅山乡','','梅山','05','603','Meishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713036,713000,'3','番路乡','','番路','05','602','Fanlu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713037,713000,'3','大埔乡','','大埔','05','607','Dapu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713038,713000,'3','阿里山乡','','阿里山','05','605','Alishan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713300,710000,'2','屏东县','','屏东','08','9','Pingtung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713301,713300,'3','屏东市','','屏东','08','900','Pingdong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713321,713300,'3','潮州镇','','潮州','08','920','Chaozhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713322,713300,'3','东港镇','','东港','08','928','Donggang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713323,713300,'3','恒春镇','','恒春','08','946','Hengchun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713324,713300,'3','万丹乡','','万丹','08','913','Wandan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713325,713300,'3','长治乡','','长治','08','908','Changzhi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713326,713300,'3','麟洛乡','','麟洛','08','909','Linluo',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713327,713300,'3','九如乡','','九如','08','904','Jiuru',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713328,713300,'3','里港乡','','里港','08','905','Ligang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713329,713300,'3','盐埔乡','','盐埔','08','907','Yanpu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713330,713300,'3','高树乡','','高树','08','906','Gaoshu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713331,713300,'3','万峦乡','','万峦','08','923','Wanluan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713332,713300,'3','内埔乡','','内埔','08','912','Napu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713333,713300,'3','竹田乡','','竹田','08','911','Zhutian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713334,713300,'3','新埤乡','','新埤','08','925','Xinpi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713335,713300,'3','枋寮乡','','枋寮','08','940','Fangliao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713336,713300,'3','新园乡','','新园','08','932','Xinyuan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713337,713300,'3','崁顶乡','','崁顶','08','924','Kanding',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713338,713300,'3','林边乡','','林边','08','927','Linbian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713339,713300,'3','南州乡','','南州','08','926','Nanzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713340,713300,'3','佳冬乡','','佳冬','08','931','Jiadong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713341,713300,'3','琉球乡','','琉球','08','929','Liuqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713342,713300,'3','车城乡','','车城','08','944','Checheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713343,713300,'3','满州乡','','满州','08','947','Manzhou',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713344,713300,'3','枋山乡','','枋山','08','941','Fangshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713345,713300,'3','三地门乡','','三地门','08','901','Sandimen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713346,713300,'3','雾台乡','','雾台','08','902','Wutai',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713347,713300,'3','玛家乡','','玛家','08','903','Majia',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713348,713300,'3','泰武乡','','泰武','08','921','Taiwu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713349,713300,'3','来义乡','','来义','08','922','Laiyi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713350,713300,'3','春日乡','','春日','08','942','Chunri',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713351,713300,'3','狮子乡','','狮子','08','943','Shizi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713352,713300,'3','牡丹乡','','牡丹','08','945','Mudan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713400,710000,'2','台东县','','台东','089','9','Taitung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713401,713400,'3','台东市','','台东','089','950','Taidong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713421,713400,'3','成功镇','','成功','089','961','Chenggong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713422,713400,'3','关山镇','','关山','089','956','Guanshan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713423,713400,'3','卑南乡','','卑南','089','954','Beinan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713424,713400,'3','鹿野乡','','鹿野','089','955','Luye',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713425,713400,'3','池上乡','','池上','089','958','Chishang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713426,713400,'3','东河乡','','东河','089','959','Donghe',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713427,713400,'3','长滨乡','','长滨','089','962','Changbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713428,713400,'3','太麻里乡','','太麻里','089','963','Taimali',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713429,713400,'3','大武乡','','大武','089','965','Dawu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713430,713400,'3','绿岛乡','','绿岛','089','951','Lvdao',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713431,713400,'3','海端乡','','海端','089','957','Haiduan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713432,713400,'3','延平乡','','延平','089','953','Yanping',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713433,713400,'3','金峰乡','','金峰','089','964','Jinfeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713434,713400,'3','达仁乡','','达仁','089','966','Daren',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713435,713400,'3','兰屿乡','','兰屿','089','952','Lanyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713500,710000,'2','花莲县','','花莲','03','9','Hualien',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713501,713500,'3','花莲市','','花莲','03','970','Hualian',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713521,713500,'3','凤林镇','','凤林','03','975','Fenglin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713522,713500,'3','玉里镇','','玉里','03','981','Yuli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713523,713500,'3','新城乡','','新城','03','971','Xincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713524,713500,'3','吉安乡','','吉安','03','973','Ji an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713525,713500,'3','寿丰乡','','寿丰','03','974','Shoufeng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713526,713500,'3','光复乡','','光复','03','976','Guangfu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713527,713500,'3','丰滨乡','','丰滨','03','977','Fengbin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713528,713500,'3','瑞穗乡','','瑞穗','03','978','Ruisui',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713529,713500,'3','富里乡','','富里','03','983','Fuli',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713530,713500,'3','秀林乡','','秀林','03','972','Xiulin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713531,713500,'3','万荣乡','','万荣','03','979','Wanrong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713532,713500,'3','卓溪乡','','卓溪','03','982','Zhuoxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713600,710000,'2','澎湖县','','澎湖','06','8','Penghu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713601,713600,'3','马公市','','马公','06','880','Magong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713621,713600,'3','湖西乡','','湖西','06','885','Huxi',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713622,713600,'3','白沙乡','','白沙','06','884','Baisha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713623,713600,'3','西屿乡','','西屿','06','881','Xiyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713624,713600,'3','望安乡','','望安','06','882','Wang an',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713625,713600,'3','七美乡','','七美','06','883','Qimei',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713700,710000,'2','金门县','','金门','082','8','Jinmen',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713701,713700,'3','金城镇','','金城','082','893','Jincheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713702,713700,'3','金湖镇','','金湖','082','891','Jinhu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713703,713700,'3','金沙镇','','金沙','082','890','Jinsha',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713704,713700,'3','金宁乡','','金宁','082','892','Jinning',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713705,713700,'3','烈屿乡','','烈屿','082','894','Lieyu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713706,713700,'3','乌丘乡','','乌丘','082','896','Wuqiu',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713800,710000,'2','连江县','','连江','0836','2','Lienchiang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713801,713800,'3','南竿乡','','南竿','0836','209','Nangan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713802,713800,'3','北竿乡','','北竿','0836','210','Beigan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713803,713800,'3','莒光乡','','莒光','0836','211','Juguang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (713804,713800,'3','东引乡','','东引','0836','212','Dongyin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810000,100000,'1','香港特别行政区','','香港','','','Hong Kong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810100,810000,'2','香港岛','','香港岛','00852','999077','Hong Kong Island',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810101,810100,'3','中西区','','中西区','00852','999077','Central and Western District',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810102,810100,'3','湾仔区','','湾仔区','00852','999077','Wan Chai District',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810103,810100,'3','东区','','东区','00852','999077','Eastern District',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810104,810100,'3','南区','','南区','00852','999077','Southern District',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810200,810000,'2','九龙','','九龙','00852','999077','Kowloon',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810201,810200,'3','油尖旺区','','油尖旺','00852','999077','Yau Tsim Mong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810202,810200,'3','深水埗区','','深水埗','00852','999077','Sham Shui Po',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810203,810200,'3','九龙城区','','九龙城','00852','999077','Jiulongcheng',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810204,810200,'3','黄大仙区','','黄大仙','00852','999077','Wong Tai Sin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810205,810200,'3','观塘区','','观塘','00852','999077','Kwun Tong',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810300,810000,'2','新界','','新界','00852','999077','New Territories',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810301,810300,'3','荃湾区','','荃湾','00852','999077','Tsuen Wan',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810302,810300,'3','屯门区','','屯门','00852','999077','Tuen Mun',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810303,810300,'3','元朗区','','元朗','00852','999077','Yuen Long',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810304,810300,'3','北区','','北区','00852','999077','North District',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810305,810300,'3','大埔区','','大埔','00852','999077','Tai Po',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810306,810300,'3','西贡区','','西贡','00852','999077','Sai Kung',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810307,810300,'3','沙田区','','沙田','00852','999077','Sha Tin',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810308,810300,'3','葵青区','','葵青','00852','999077','Kwai Tsing',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (810309,810300,'3','离岛区','','离岛','00852','999077','Outlying Islands',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820000,100000,'1','澳门特别行政区','','澳门','','','Macau',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820100,820000,'2','澳门半岛','','澳门半岛','00853','999078','MacauPeninsula',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820101,820100,'3','花地玛堂区','','花地玛堂区','00853','999078','Nossa Senhora de Fatima',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820102,820100,'3','圣安多尼堂区','','圣安多尼堂区','00853','999078','Santo Antonio',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820103,820100,'3','大堂区','','大堂','00853','999078','Datang',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820104,820100,'3','望德堂区','','望德堂区','00853','999078','Sao Lazaro',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820105,820100,'3','风顺堂区','','风顺堂区','00853','999078','Sao Lourenco',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820200,820000,'2','氹仔岛','','氹仔岛','00853','999078','Taipa',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820201,820200,'3','嘉模堂区','','嘉模堂区','00853','999078','Our Lady Of Carmel Parish',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820300,820000,'2','路环岛','','路环岛','00853','999078','Coloane',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (820301,820300,'3','圣方济各堂区','','圣方济各堂区','00853','999078','St Francis Xavier Parish',1,'');
INSERT INTO t_sys_area (AREA_ID, PARENT_AREA_ID, AREA_TYPE, AREA_NAME,AREA_CODE,SHORT_NAME,ZONE_CODE,ZIP_CODE,PINYIN,COUNTRY_ID,REMARK) 
VALUES (900000,100000,'1','钓鱼岛','','钓鱼岛','','','DiaoyuDao',1,'');

/*==============================================================*/
/* Table: t_sys_area_range                                      */
/*==============================================================*/
create table t_sys_area_range
(
   AREA_ID              bigint(10) not null comment '区域标识',
   SEQ                  int(6) not null comment '序列',
   LONGITUDE            varchar(20) not null comment '经度',
   LATITUDE             varchar(20) not null comment '纬度',
   primary key (AREA_ID, SEQ)
);

alter table t_sys_area_range comment '区域范围';

/*==============================================================*/
/* Table: t_sys_attachment                                      */
/*==============================================================*/
create table t_sys_attachment
(
   ATTACHMENT_ID        integer(6) not null auto_increment comment '附件标识',
   ATTACHMENT_TYPE      varchar(20) not null comment '附件类型',
   ATTACHMENT_NAME      varchar(60) not null comment '附件文件名',
   IS_REMOTE            char(1) not null comment '是否为远程文件',
   FILE_SIZE            bigint(10) comment '文件大小',
   FILE_PATH            varchar(255) not null comment '文件路径',
   DOWNLOADS_NUM        integer(8) not null comment '下载次数',
   IS_PICTURE           char(1) not null comment '是否是图片',
   IS_THUMB             char(1) not null comment '是否生成缩略图',
   THUMB_PATH           varchar(255) comment '缩略图地址',
   CREATE_TIME          datetime not null comment '创建时间',
   EXP_TIME             datetime comment '失效时间',
   UPDATE_TIME          VARCHAR(14) comment '更新时间',
   user_group           varchar(60),
   user                 varchar(60),
   permission           char(3),
   primary key (ATTACHMENT_ID)
);

alter table t_sys_attachment comment '附件表';

/*==============================================================*/
/* Table: t_sys_attr                                            */
/*==============================================================*/
create table t_sys_attr
(
   ATTR_ID              integer(6) not null auto_increment comment '属性标识',
   ATTR_NAME            varchar(60) not null comment '名称',
   ATTR_TYPE            char(1) not null comment '属性类型',
   PARENT_ATTR_ID       integer(4) comment '父属性标识',
   ATTR_CODE            varchar(120) not null comment '属性代码',
   VISIBLE              char(1) not null comment '是否可见',
   INSTANTIATABLE       char(1) not null comment '是否可实例化',
   DEFAULT_VALUE        varchar(120) comment '缺省值',
   INPUT_TYPE           char(1) not null comment '输入方式',
   DATA_TYPE            char(1) not null comment '数据类型',
   VALUE_SCRIPT         varchar(2000) comment '取值校验规则',
   primary key (ATTR_ID)
);

alter table t_sys_attr comment '属性表';

/*==============================================================*/
/* Table: t_sys_attr_value                                      */
/*==============================================================*/
create table t_sys_attr_value
(
   ATTR_ID              integer(6) not null comment '属性标识',
   ATTR_VALUE_ID        integer(6) not null auto_increment comment '属性值标识',
   VALUE_MARK           varchar(60) not null comment '取值说明',
   VALUE                varchar(120) comment '取值',
   LINK_ATTR_ID         integer(6) comment '联动属性标识',
   primary key (ATTR_VALUE_ID)
);

alter table t_sys_attr_value comment '属性值';

/*==============================================================*/
/* Table: t_sys_config_item                                     */
/*==============================================================*/
create table t_sys_config_item
(
   CONFIG_ITEM_ID       integer(6) not null auto_increment comment '配置项标识',
   MODULE_CODE          varchar(10) comment '业务模块代码',
   CONFIG_ITEM_CODE     varchar(120) not null comment '配置项代码',
   CONFIG_ITEM_NAME     varchar(120) not null comment '配置项名称',
   IS_VISIABLE          char(1) not null comment '是否可见',
   UPDATE_TIME          datetime comment '更新时间',
   REMARK               varchar(255) comment '备注',
   primary key (CONFIG_ITEM_ID)
);

alter table t_sys_config_item comment '配置项';

insert into t_sys_config_item (config_item_id, module_code, config_item_code, config_item_name, is_visiable, update_time, remark)
values (1, 'MANAGER', 'SYSTEM', '系统配置', 'Y', now(), null);

/*==============================================================*/
/* Table: t_sys_config_item_history                             */
/*==============================================================*/
create table t_sys_config_item_history
(
   CONFIG_ITEM_ID       integer(6) not null comment '配置项标识',
   SEQ                  integer(4) not null comment '序列号',
   MODULE_CODE          varchar(10) comment '模块代码',
   CONFIG_ITEM_CODE     varchar(120) not null comment '配置项代码',
   CONFIG_ITEM_NAME     varchar(120) not null comment '配置项名称',
   IS_VISIABLE          char(1) not null comment '是否可见',
   UPDATE_TIME          datetime not null comment '更新时间',
   REMARK               varchar(255) comment '备注',
   OPERATOR_ID          integer(8) comment '操作员标识',
   CHANNEL_ID           smallint(2) comment '渠道标识',
   primary key (CONFIG_ITEM_ID, SEQ)
);

alter table t_sys_config_item_history comment '配置项历史表';

/*==============================================================*/
/* Table: t_sys_config_item_param                               */
/*==============================================================*/
create table t_sys_config_item_param
(
   CONFIG_ITEM_ID       integer(6) not null comment '配置项标识',
   PARAM_CODE           varchar(120) not null comment '参数编码',
   PARAM_NAME           varchar(120) not null comment '参数名称',
   PARAM_VALUE          varchar(1000) comment '参数取值',
   DEFAULT_PARAM_VALUE  varchar(1000) comment '缺省值',
   DATA_TYPE            char(1) not null comment '数据类型',
   INPUT_TYPE           char(1) not null comment '输入方式',
   VALUE_SCRIPT         varchar(2000) comment '取值校验规则',
   UPDATE_TIME          datetime not null comment '更新时间',
   REMARK               varchar(255) comment '备注',
   primary key (CONFIG_ITEM_ID, PARAM_CODE)
);

alter table t_sys_config_item_param comment '配置项参数';

insert into t_sys_config_item_param (config_item_id, param_code, param_name, param_value, default_param_value, data_type, input_type, value_script, update_time, remark)
values (1, 'RESOURCE_BASE_URL', '资源路径前缀', null, null, 'C', '5', null, now(), null);
insert into t_sys_config_item_param (config_item_id, param_code, param_name, param_value, default_param_value, data_type, input_type, value_script, update_time, remark)
values (1, 'HOME_PAGE_URL', '首页路径', 'permission/org', null, 'C', '5', null, now(), null);
insert into t_sys_config_item_param (config_item_id, param_code, param_name, param_value, default_param_value, data_type, input_type, value_script, update_time, remark)
values (1, 'OPERTOR_LOG_EVENT_TYPE', '操作日志事件类型', 'O', null, 'C', '5', null, now(), null);

/*==============================================================*/
/* Table: t_sys_config_item_param_history                       */
/*==============================================================*/
create table t_sys_config_item_param_history
(
   CONFIG_ITEM_ID       integer(6) not null comment '配置项标识',
   PARAM_CODE           varchar(120) not null comment '参数编码',
   SEQ                  integer(4) not null comment '序列号',
   PARAM_NAME           varchar(120) not null comment '参数名称',
   PARAM_VALUE          varchar(1000) comment '参数取值',
   DEFAULT_PARAM_VALUE  varchar(1000) comment '缺省值',
   DATA_TYPE            char(1) comment '数据类型',
   INPUT_TYPE           char(1) comment '输入方式',
   VALUE_SCRIPT         varchar(2000) comment '取值校验规则',
   UPDATE_TIME          datetime not null comment '更新时间',
   REMARK               varchar(255) comment '备注',
   OPERATOR_ID          integer(8) comment '操作员标识',
   CHANNEL_ID           smallint(2) comment '渠道标识',
   primary key (CONFIG_ITEM_ID, PARAM_CODE, SEQ)
);

alter table t_sys_config_item_param_history comment '配置项参数历史表';

/*==============================================================*/
/* Table: t_sys_config_item_param_value                         */
/*==============================================================*/
create table t_sys_config_item_param_value
(
   PARAM_VALUE_ID       integer(6) not null auto_increment comment '参数取值标识',
   CONFIG_ITEM_ID       integer(6) not null comment '配置项标识',
   PARAM_CODE           varchar(120) not null comment '参数编码',
   VALUE                varchar(60) comment '取值',
   REMARK               varchar(255) comment '备注',
   primary key (PARAM_VALUE_ID)
);

alter table t_sys_config_item_param_value comment '配置项参数选择值';

/*==============================================================*/
/* Table: t_sys_contact_channel                                 */
/*==============================================================*/
create table t_sys_contact_channel
(
   CONTACT_CHANNEL_ID   smallint(2) not null comment '接触渠道',
   CHANNEL_TYPE         char(2) not null comment '渠道类型',
   CONTACT_CHANNEL_NAME varchar(20) not null comment '名称',
   REMARK               varchar(120) comment '说明',
   primary key (CONTACT_CHANNEL_ID)
);

alter table t_sys_contact_channel comment '接触渠到';

/*==============================================================*/
/* Table: t_sys_country                                         */
/*==============================================================*/
create table t_sys_country
(
   country_id           smallint(3) not null,
   country_name         varchar(20) not null,
   short_name           varchar(20),
   country_code         varchar(10),
   english_name         varchar(60),
   remark               varchar(255),
   primary key (country_id)
);

alter table t_sys_country comment '国家';

insert into t_sys_country (country_id,country_name,short_name,country_code,english_name,remark) values
(1, '中华人民共和国', '中国', 'CN', 'China', '');

/*==============================================================*/
/* Table: t_sys_dictionary                                      */
/*==============================================================*/
create table t_sys_dictionary
(
   DICT_CODE            varchar(60) not null comment '字典代码',
   DICT_NAME            varchar(60) not null comment '字典名称',
   REMARK               varchar(120) comment '备注',
   primary key (DICT_CODE)
);

alter table t_sys_dictionary comment '数据字典表';

INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('CHANNEL_TYPE', '渠道类型', '渠道类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('AREA_TYPE', '区域类型', '区域类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('DATA_TYPE', '数据类型', '数据类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('INPUT_TYPE', '输入方式', '输入方式');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('ATTR_TYPE', '属性类型', '属性类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('TASK_STATE', '任务状态', '任务状态');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('TRIGGER_TYPE', '触发器类型', '触发器类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('MESSAGE_TYPE', '消息类型', '消息类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('OPERATOR_TYPE', '操作员类型', '操作员类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('ACCOUNT_TYPE', '账号类型', '账号类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('RESOURCE_TYPE', '资源类型', '资源类型');
INSERT INTO t_sys_dictionary (DICT_CODE, DICT_NAME, REMARK) VALUES ('EVENT_TYPE', '事件类型', '事件类型');





/*==============================================================*/
/* Table: t_sys_dictionary_data                                 */
/*==============================================================*/
create table t_sys_dictionary_data
(
   DICT_DATA_ID         integer(6) not null auto_increment comment '字典数据表标识',
   DICT_CODE            varchar(60) not null comment '字典代码',
   DICT_DATA_NAME       varchar(60) not null comment '字典数据名',
   DICT_DATA_VALUE      varchar(8) not null comment '字典数据值',
   IS_FIXED             char(1) not null comment '是否固定',
   IS_CANCEL            char(1) comment '是否可以删除',
   primary key (DICT_DATA_ID)
);

alter table t_sys_dictionary_data comment '字典数据表';

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (1, 'CHANNEL_TYPE', 'Http消息', '01', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (2, 'CHANNEL_TYPE', 'WebSocket消息', '02', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (3, 'CHANNEL_TYPE', '短信信息', '03', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (4, 'CHANNEL_TYPE', '邮件', '04', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (5, 'CHANNEL_TYPE', 'dubbo协议', '05', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (6, 'CHANNEL_TYPE', 'WebService', '06', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (7, 'CHANNEL_TYPE', '站内消息', '07', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (8, 'AREA_TYPE', '国家', 'A', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (9, 'AREA_TYPE', '省、直辖市', 'P', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (10, 'AREA_TYPE', '市', 'C', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (11, 'AREA_TYPE', '区,县', 'D', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (12, 'AREA_TYPE', '社区', 'O', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (13, 'AREA_TYPE', '小区', 'G', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (14, 'DATA_TYPE', '字符Character', 'C', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (15, 'DATA_TYPE', '整数Number', 'N', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (16, 'DATA_TYPE', '密码Password', 'P', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (17, 'DATA_TYPE', '浮点数Float', 'F', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (18, 'DATA_TYPE', '对象类型', 'O', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (19, 'INPUT_TYPE', '不可编辑', '1', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (20, 'INPUT_TYPE', '单选', '2', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (21, 'INPUT_TYPE', '多选', '3', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (22, 'INPUT_TYPE', '时间选择框', '4', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (23, 'INPUT_TYPE', '对象类型', '5', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (24, 'ATTR_TYPE', '管理员属性', 'A', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (25, 'ATTR_TYPE', '会员属性', 'M', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (26, 'TASK_STATE', '初始状态', 'I', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (27, 'TASK_STATE', '等待状态', 'W', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (28, 'TASK_STATE', '暂停状态', 'P', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (29, 'TASK_STATE', '正常执行', 'A', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (30, 'TASK_STATE', '阻塞状态', 'B', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (31, 'TASK_STATE', '错误状态', 'E', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (32, 'TASK_STATE', '完成状态', 'C', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (33, 'TRIGGER_TYPE', '简单触发器类型', '1', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (34, 'TRIGGER_TYPE', '简单触发器类型', '2', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (35, 'MESSAGE_TYPE', '文本消息', 'T', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (36, 'MESSAGE_TYPE', '图片消息', 'P', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (37, 'MESSAGE_TYPE', '语音消息', 'V', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (38, 'MESSAGE_TYPE', '多媒体消息', 'M', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (39, 'OPERATOR_TYPE', '管理员', 'A', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (40, 'OPERATOR_TYPE', '会员', 'M', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (41, 'ACCOUNT_TYPE', '平台账号', 'P', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (42, 'ACCOUNT_TYPE', '统一平台账号', 'U', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (43, 'ACCOUNT_TYPE', '统一平台邮箱账号', 'UE', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (44, 'ACCOUNT_TYPE', '统一平台手机账号', 'UM', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (45, 'ACCOUNT_TYPE', '腾讯QQ账号', 'TQ', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (46, 'ACCOUNT_TYPE', '腾讯微信账号', 'TW', 'N', 'N');

INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (47, 'EVENT_TYPE', '操作事件', 'O', 'N', 'N');
INSERT INTO t_sys_dictionary_data (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (48, 'EVENT_TYPE', '积分事件', 'I', 'N', 'N');

/*==============================================================*/
/* Table: t_sys_event                                           */
/*==============================================================*/
create table t_sys_event
(
   EVENT_ID             integer(4) not null auto_increment comment '事件标识',
   EVENT_TYPE           char(1) not null comment '事件类型',
   PARAMS_NAME          varchar(120) comment '参数名',
   EVENT_NAME           varchar(20) not null comment '事件名称',
   REMARK               varchar(120) comment '备注',
   primary key (EVENT_ID)
);

alter table t_sys_event comment '事件表';

/*==============================================================*/
/* Table: t_sys_module                                          */
/*==============================================================*/
create table t_sys_module
(
   MODULE_CODE          varchar(10) not null comment '业务模块代码',
   PARENT_MODULE_CODE   varchar(10) comment '父业务模块编码',
   MODULE_NAME          varchar(20) not null comment '业务模块名称',
   primary key (MODULE_CODE)
);

alter table t_sys_module comment '业务模块（域）';

INSERT INTO t_sys_module (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('COMMON',NULL,'公共模块');
INSERT INTO t_sys_module (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('PORTAL','COMMON','系统门户');
INSERT INTO t_sys_module (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('MONITOR','COMMON','系统监控模块');
INSERT INTO t_sys_module (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('MANAGER','COMMON','后台管理模块');

/*==============================================================*/
/* Table: t_sys_operate_log                                     */
/*==============================================================*/
create table t_sys_operate_log
(
   OPERATE_LOG_ID       bigint(10) not null auto_increment comment '操作日志标识',
   EVENT_ID             integer(4) comment '事件标识',
   IP                   varchar(15) comment 'IP地址',
   CREATE_TIME          datetime not null comment '创建时间',
   OPERATOR_ID          integer(8) comment '操作员标识',
   PARAMS_VALUE         varchar(255) comment '参数',
   primary key (OPERATE_LOG_ID)
);

alter table t_sys_operate_log comment '记录用户的操作';

/*==============================================================*/
/* Table: t_sys_trans_log                                       */
/*==============================================================*/
create table t_sys_trans_log
(
   TRANS_ID             varchar(36) not null comment '事务标识',
   BEGIN_TIME           datetime not null comment '开始时间',
   END_TIME             datetime not null comment '结束时间',
   CONSUME_TIME         integer(8) not null comment '执行时间(毫秒)',
   INPUT_PARAM          text comment '入参',
   OUTPUT_PARAM         text comment '出参',
   SQL_LOG              text comment 'SQL日志',
   EXCEPTION_LOG        text comment '异常信息',
   CONTACT_CHANNEL_ID   smallint(2) comment '接触渠到',
   primary key (TRANS_ID)
);

alter table t_sys_trans_log comment '事务日志';

/*==============================================================*/
/* Table: t_sys_trans_log_stack                                 */
/*==============================================================*/
create table t_sys_trans_log_stack
(
   STACK_ID             varchar(36) not null comment '栈标识',
   SEQ                  integer(4) not null comment '序列',
   TRANS_ID             varchar(36) not null comment '事务标识',
   PARENT_STACK_ID      varchar(36) comment '父栈标识',
   METHOD               text not null comment '方法标识',
   BEGIN_TIME           datetime not null comment '开始时间',
   END_TIME             datetime not null comment '结束时间',
   CONSUME_TIME         integer(8) not null comment '执行时间（毫秒）',
   INPUT_PARAM          text comment '入参',
   OUTPUT_PARAM         text comment '出参',
   IS_SUCCESS           char(1) not null comment '是否成功',
   primary key (STACK_ID)
);

alter table t_sys_trans_log_stack comment '事务日志执行栈';

alter table QRTZ_BLOB_TRIGGERS add constraint FK_QRTZ_BLO_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_CRON_TRIGGERS add constraint FK_QRTZ_CRO_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPLE_TRIGGERS add constraint FK_QRTZ_SIM_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPROP_TRIGGERS add constraint FK_Ref_quartz_5 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_TRIGGERS add constraint FK_QRTZ_TRI_QRTZ_JOB foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP)
      references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP);

alter table t_job_task add constraint FK_FK_MODULE_CODE_TASK_MODULE_CODE foreign key (MODULE_CODE)
      references t_sys_module (MODULE_CODE) on delete restrict on update restrict;

alter table t_job_task_trigger add constraint FK_TASK_ID_TASK_TRIGGER_TASK_ID foreign key (TASK_ID)
      references t_job_task (TASK_ID) on delete restrict on update restrict;

alter table t_manager_account add constraint FK_OPERATOR_ID_OPERATOR_ACCOUNT_OPERATOR_ID foreign key (OPERATOR_ID)
      references t_manager_operator (OPERATOR_ID) on delete restrict on update restrict;

alter table t_manager_admin add constraint FK_OPERATOR_ID_ADMIN_OPERATOR_ID foreign key (OPERATOR_ID)
      references t_manager_operator (OPERATOR_ID) on delete restrict on update restrict;

alter table t_manager_admin_attr add constraint FK_ADMIN_ID_ADMIN_ATTR_ADMIN_ID foreign key (ADMIN_ID)
      references t_manager_admin (ADMIN_ID) on delete restrict on update restrict;

alter table t_manager_admin_attr add constraint FK_ATTR_ID_ADMIN_ATTR_ATTR_ID foreign key (ATTR_ID)
      references t_sys_attr (ATTR_ID) on delete restrict on update restrict;

alter table t_manager_button add constraint FK_FK_MENU_BUTTON_MENU foreign key (RESOURCE_ID)
      references t_manager_menu (RESOURCE_ID) on delete restrict on update restrict;

alter table t_manager_duty add constraint FK_FK_ORG_ID_DUTY_ORG_ID foreign key (ORG_ID)
      references t_manager_org (ORG_ID) on delete restrict on update restrict;

alter table t_manager_duty_role add constraint FK_FK_DUTY_ID_DUTY_ROLE_DUTY_ID foreign key (DUTY_ID)
      references t_manager_duty (DUTY_ID) on delete restrict on update restrict;

alter table t_manager_duty_role add constraint FK_FK_ROLE_ID_DUTY_ROLE_ROLE_ID foreign key (ROLE_ID)
      references t_manager_role (ROLE_ID) on delete restrict on update restrict;

alter table t_manager_menu add constraint FK_FK_MENU_ID_MENU_MENU_ID foreign key (PARENT_RESOURCE_ID)
      references t_manager_menu (RESOURCE_ID) on delete restrict on update restrict;

alter table t_manager_menu add constraint FK_FK_MODULE_CODE_MENU_MODULE_CODE foreign key (MODULE_CODE)
      references t_sys_module (MODULE_CODE) on delete restrict on update restrict;

alter table t_manager_operator add constraint FK_FK_DUTY_ID_OPERATOR_DUTY_ID foreign key (DUTY_ID)
      references t_manager_duty (DUTY_ID) on delete restrict on update restrict;

alter table t_manager_org add constraint FK_FK_ORG_ID_PARENT_ORG_ID foreign key (PARENT_ORG_ID)
      references t_manager_org (ORG_ID) on delete restrict on update restrict;

alter table t_manager_role add constraint FK_FK_MODULE_CODE_ROLE_MODULE_CODE foreign key (MODULE_CODE)
      references t_sys_module (MODULE_CODE) on delete restrict on update restrict;

alter table t_manager_role_resource add constraint FK_ROLE_ID_ROLE_RESOURCE_ROLE_ID foreign key (ROLE_ID)
      references t_manager_role (ROLE_ID) on delete restrict on update restrict;

alter table t_msg_message_attachment add constraint FK_ATTACHMENTS_MESSAGE_ATTACHMENTS foreign key (ATTACHMENTS_ID)
      references t_sys_attachment (ATTACHMENT_ID) on delete restrict on update restrict;

alter table t_msg_message_box add constraint FK_MESSAGE_TEMPLATE_MESSAGE_BOX foreign key (MESSAGE_TEMPLATE_ID)
      references t_msg_message_template (MESSAGE_TEMPLATE_ID) on delete restrict on update restrict;

alter table t_msg_send_record add constraint FK_CONTACT_CHANNEL_SEND_RECORD foreign key (CONTACT_CHANNEL_ID)
      references t_sys_contact_channel (CONTACT_CHANNEL_ID) on delete restrict on update restrict;

alter table t_sys_area add constraint FK_AREA_ID_AREA_PARENT_AREA_ID foreign key (PARENT_AREA_ID)
      references t_sys_area (AREA_ID) on delete restrict on update restrict;

alter table t_sys_area add constraint FK_FK_area_country foreign key (country_id)
      references t_sys_country (country_id) on delete restrict on update restrict;

alter table t_sys_area_range add constraint FK_AREA_RAN_ARRE foreign key (AREA_ID)
      references t_sys_area (AREA_ID) on delete restrict on update restrict;

alter table t_sys_attr_value add constraint FK_ATTR_ID_ATTR_VALUE_ATTR_ID foreign key (ATTR_ID)
      references t_sys_attr (ATTR_ID) on delete restrict on update restrict;

alter table t_sys_attr_value add constraint FK_ATTR_VAL_ATTR_ID_A_LINK foreign key (LINK_ATTR_ID)
      references t_sys_attr (ATTR_ID) on delete restrict on update restrict;

alter table t_sys_config_item add constraint FK_FK_MODULE_CODE_CONFIG_MODULE_CODE foreign key (MODULE_CODE)
      references t_sys_module (MODULE_CODE) on delete restrict on update restrict;

alter table t_sys_config_item_param add constraint FK_CONFIG_ITEM_ID_CONFIG_ITEM_PARAM_ITEM_ID foreign key (CONFIG_ITEM_ID)
      references t_sys_config_item (CONFIG_ITEM_ID) on delete restrict on update restrict;

alter table t_sys_config_item_param_value add constraint FK_CONFIG_I_CONFIG_IT_CONFIG_V foreign key (CONFIG_ITEM_ID, PARAM_CODE)
      references t_sys_config_item_param (CONFIG_ITEM_ID, PARAM_CODE) on delete restrict on update restrict;

alter table t_sys_dictionary_data add constraint FK_FK_DICTIONARY_DICTIONARY_DATA foreign key (DICT_CODE)
      references t_sys_dictionary (DICT_CODE) on delete restrict on update restrict;

alter table t_sys_module add constraint FK_FK_MODULE_CODE_PARENT_MODULE_CODE foreign key (PARENT_MODULE_CODE)
      references t_sys_module (MODULE_CODE) on delete restrict on update restrict;

alter table t_sys_operate_log add constraint FK_FK_OPERATE_LOG_EVENT foreign key (EVENT_ID)
      references t_sys_event (EVENT_ID) on delete restrict on update restrict;

alter table t_sys_trans_log add constraint FK_CONTACT_CHANNEL_ID_TRANS_LOG_CONTACT_CHANNEL_ID foreign key (CONTACT_CHANNEL_ID)
      references t_sys_contact_channel (CONTACT_CHANNEL_ID) on delete restrict on update restrict;

alter table t_sys_trans_log_stack add constraint FK_FK_TRANS_LOG_STACK_TRANS_LOG foreign key (TRANS_ID)
      references t_sys_trans_log (TRANS_ID) on delete restrict on update restrict;

