/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2015/6/28 15:00:33                           */
/*==============================================================*/


drop table if exists ACCOUNT;

drop table if exists ACCOUNT_TYPE;

drop table if exists ADMIN;

drop table if exists ADMIN_ATTR;

drop table if exists ADMIN_ATTR_HISTORY;

drop table if exists ADMIN_HISTORY;

drop table if exists ADMIN_ROLE;

drop table if exists AREA;

drop table if exists AREA_RANGE;

drop table if exists AREA_TYPE;

drop table if exists ATTACHMENTS;

drop table if exists ATTR;

drop table if exists ATTR_TYPE;

drop table if exists ATTR_VALUE;

drop table if exists CHANNEL_TYPE;

drop table if exists CONFIG_ITEM;

drop table if exists CONFIG_ITEM_HISTORY;

drop table if exists CONFIG_ITEM_PARAM;

drop table if exists CONFIG_ITEM_PARAM_HISTORY;

drop table if exists CONFIG_ITEM_PARAM_VALUE;

drop table if exists CONTACT_CHANNEL;

drop table if exists CRON_TRIGGER;

drop table if exists CRON_TRIGGER_HISTORY;

drop table if exists DATA_TYPE;

drop table if exists DIRECTORY;

drop table if exists INPUT_TYPE;

drop table if exists MENU;

drop table if exists MESSAGE_ATTACHMENTS;

drop table if exists MESSAGE_BOX;

drop table if exists MESSAGE_HISTORY;

drop table if exists MESSAGE_TEMPLATE;

drop table if exists MESSAGE_TYPE;

drop table if exists MODULE;

drop table if exists OPERATOR;

drop table if exists OPERATOR_HISTORY;

drop table if exists OPERATOR_RESOURCE;

drop table if exists OPERATOR_ROLE_HISTORY;

drop table if exists OPERATOR_TYPE;

drop table if exists QRTZ_BLOB_TRIGGERS;

drop table if exists QRTZ_CALENDARS;

drop table if exists QRTZ_CRON_TRIGGERS;

drop table if exists QRTZ_FIRED_TRIGGERS;

drop table if exists QRTZ_JOB_DETAILS;

drop table if exists QRTZ_LOCKS;

drop table if exists QRTZ_PAUSED_TRIGGER_GRPS;

drop table if exists QRTZ_SCHEDULER_STATE;

drop table if exists QRTZ_SIMPLE_TRIGGERS;

drop table if exists QRTZ_SIMPROP_TRIGGERS;

drop table if exists QRTZ_TRIGGERS;

drop table if exists RESOURCE_TYPE;

drop table if exists ROLE;

drop table if exists ROLE_HISTORY;

drop table if exists ROLE_RESOURCE;

drop table if exists SEND_RECORD;

drop table if exists SIMPLE_TRIGGER;

drop table if exists SIMPLE_TRIGGER_HISTORY;

drop table if exists TASK;

drop table if exists TASK_HISTORY;

drop table if exists TASK_STATE;

drop table if exists TASK_TRIGGER;

drop table if exists TRANS_LOG;

drop table if exists TRANS_LOG_STACK;

drop table if exists TRIGGER_TYPE;

drop table if exists URL_RESOURCE;

/*==============================================================*/
/* Table: ACCOUNT                                               */
/*==============================================================*/
create table ACCOUNT
(
   ACCOUNT_ID           INT(8) not null auto_increment,
   ACCOUNT_TYPE         CHAR(1) not null,
   ACCOUNT_VALUE        VARCHAR(120) not null,
   OPERATOR_ID          INT(8) not null,
   CREATE_TIME          DATETIME not null,
   STATE                CHAR(1) not null,
   STATE_TIME           DATETIME not null,
   EXT1                 VARCHAR(120),
   EXT2                 VARCHAR(120),
   primary key (ACCOUNT_ID)
);

/*==============================================================*/
/* Table: ACCOUNT_TYPE                                          */
/*==============================================================*/
create table ACCOUNT_TYPE
(
   ACCOUNT_TYPE         CHAR(1) not null,
   ACCOUNT_TYPE_NAME    VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (ACCOUNT_TYPE)
);

INSERT INTO ACCOUNT_TYPE VALUES ('A','Admin','管理员账号');
INSERT INTO ACCOUNT_TYPE VALUES ('B','Business','商家账号');
INSERT INTO ACCOUNT_TYPE VALUES ('C','Community','社区管理员账号');
INSERT INTO ACCOUNT_TYPE VALUES ('M','Member','会员账号');
INSERT INTO ACCOUNT_TYPE VALUES ('Q','QQ','QQ账号');
INSERT INTO ACCOUNT_TYPE VALUES ('W','Weixin','微信账号');

/*==============================================================*/
/* Table: ADMIN                                                 */
/*==============================================================*/
create table ADMIN
(
   ADMIN_ID             INT(6) not null auto_increment,
   ADMIN_NAME           VARCHAR(60) not null,
   CREATED_TIME         DATETIME not null,
   STATE                CHAR(1) not null,
   STATE_DATE           DATETIME not null,
   EMAIL                VARCHAR(120),
   PHONE                VARCHAR(20),
   OPERATOR_ID          INT(8),
   ADDRESS              VARCHAR(255),
   primary key (ADMIN_ID)
);

/*==============================================================*/
/* Table: ADMIN_ATTR                                            */
/*==============================================================*/
create table ADMIN_ATTR
(
   ADMIN_ID             INT(8) not null,
   ATTR_ID              INT(4) not null,
   VALUE                VARCHAR(120),
   CREATE_TIME          DATETIME not null,
   primary key (ADMIN_ID, ATTR_ID)
);

/*==============================================================*/
/* Table: ADMIN_ATTR_HISTORY                                    */
/*==============================================================*/
create table ADMIN_ATTR_HISTORY
(
   ADMIN_ID             INT(8) not null,
   ATTR_ID              INT(4) not null,
   SEQ                  INT(4) not null,
   VALUE                VARCHAR(120),
   CREATE_TIME          DATETIME not null,
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPRATOR_ID    INT(8),
   primary key (ADMIN_ID, ATTR_ID, SEQ)
);

/*==============================================================*/
/* Table: ADMIN_HISTORY                                         */
/*==============================================================*/
create table ADMIN_HISTORY
(
   ADMIN_ID             INT(6) not null,
   SEQ                  INT(4) not null,
   ADMIN_NAME           VARCHAR(60) not null,
   CREATED_TIME         DATETIME not null,
   STATE                CHAR(1) not null,
   STATE_DATE           DATETIME not null,
   EMAIL                VARCHAR(120),
   PHONE                VARCHAR(20),
   ADDRESS              VARCHAR(255),
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (ADMIN_ID, SEQ)
);

/*==============================================================*/
/* Table: ADMIN_ROLE                                            */
/*==============================================================*/
create table ADMIN_ROLE
(
   ADMIN_ID             INT(8) not null,
   ROLE_ID              INT(4) not null,
   CREATE_TIME          DATETIME not null,
   primary key (ADMIN_ID, ROLE_ID)
);

/*==============================================================*/
/* Table: AREA                                                  */
/*==============================================================*/
create table AREA
(
   AREA_ID              INT(10) not null auto_increment,
   PARENT_AREA_ID       INT(10),
   AREA_TYPE            CHAR(1) not null,
   AREA_NAME            VARCHAR(20) not null,
   AREA_CODE            VARCHAR(10),
   REMARK               VARCHAR(120),
   primary key (AREA_ID)
);

/*==============================================================*/
/* Table: AREA_RANGE                                            */
/*==============================================================*/
create table AREA_RANGE
(
   AREA_ID              INT(10) not null,
   SEQ                  INT(6) not null,
   LONGITUDE            VARCHAR(20) not null,
   LATITUDE             VARCHAR(20) not null,
   primary key (AREA_ID, SEQ)
);

/*==============================================================*/
/* Table: AREA_TYPE                                             */
/*==============================================================*/
create table AREA_TYPE
(
   AREA_TYPE            CHAR(1) not null,
   AREA_TYPE_NAME       VARCHAR(10) not null,
   REMARK               VARCHAR(60),
   primary key (AREA_TYPE)
);

INSERT INTO AREA_TYPE (AREA_TYPE,AREA_TYPE_NAME,REMARK) VALUES 
('A','Country','国家'),
('P','Province','省、直辖市'),
('C','City','市'),
('D','District','区,县'),
('O','Community','社区'),
('G','Garden','小区');

/*==============================================================*/
/* Table: ATTACHMENTS                                           */
/*==============================================================*/
create table ATTACHMENTS
(
   ATTACHMENTS_ID       BIGINT not null auto_increment,
   ATTACHMENTS_TYPE     VARCHAR(20) not null,
   ATTACHMENTS_NAME     VARCHAR(60) not null,
   IS_REMOTE            CHAR(1) not null,
   FILE_SIZE            INT(10),
   FILE_PATH            VARCHAR(255) not null,
   DOWNLOADS_NUM        INT(8) not null,
   IS_PICTURE           CHAR(1) not null,
   IS_THUMB             CHAR(1) not null,
   THUMB_PATH           VARCHAR(255),
   CREATE_TIME          DATETIME not null,
   EXP_TIME             DATETIME,
   primary key (ATTACHMENTS_ID)
);

/*==============================================================*/
/* Table: ATTR                                                  */
/*==============================================================*/
create table ATTR
(
   ATTR_ID              INT(4) not null auto_increment,
   ATTR_NAME            VARCHAR(60) not null,
   ATTR_TYPE            CHAR(1) not null,
   PARENT_ATTR_ID       INT(4),
   ATTR_CODE            VARCHAR(120) not null,
   VISIBLE              CHAR(1) not null,
   INSTANTIATABLE       CHAR(1) not null,
   DEFAULT_VALUE        VARCHAR(120),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR(2000),
   primary key (ATTR_ID)
);

/*==============================================================*/
/* Table: ATTR_TYPE                                             */
/*==============================================================*/
create table ATTR_TYPE
(
   ATTR_TYPE            CHAR(1) not null,
   ATTR_TYPE_NAME       VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (ATTR_TYPE)
);

INSERT INTO ATTR_TYPE VALUES
('A','Admin','管理员属性'),
('B','Business','商家属性'),
('C','Community','社区管理员属性'),
('M','Member','会员属性');

/*==============================================================*/
/* Table: ATTR_VALUE                                            */
/*==============================================================*/
create table ATTR_VALUE
(
   ATTR_ID              INT(6) not null auto_increment,
   ATTR_VALUE_ID        INT(6) not null,
   VALUE_MARK           VARCHAR(60) not null,
   VALUE                VARCHAR(120),
   LINK_ATTR_ID         INT(6),
   primary key (ATTR_ID, ATTR_VALUE_ID)
);

/*==============================================================*/
/* Table: CHANNEL_TYPE                                          */
/*==============================================================*/
create table CHANNEL_TYPE
(
   CHANNEL_TYPE         INT(2) not null,
   CHANNEL_TYPE_NAME    VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (CHANNEL_TYPE)
);

INSERT INTO CHANNEL_TYPE(CHANNEL_TYPE, CHANNEL_TYPE_NAME, REMARK) VALUES 
(1, 'Http', 'Http消息'),
(2, 'WebSocket', 'WebSocket消息'),
(3, 'SMS', '短信信息'),
(4, 'Email', '邮件'),
(5, 'DUBBO', 'dubbo协议'),
(6, 'WebService', 'WebService'),
(7, 'Desktop', '站内消息');

/*==============================================================*/
/* Table: CONFIG_ITEM                                           */
/*==============================================================*/
create table CONFIG_ITEM
(
   CONFIG_ITEM_ID       int(4) not null auto_increment,
   MODULE_CODE          varchar(10) not null,
   DIRECTORY_CODE       varchar(20) not null,
   CONFIG_ITEM_CODE     varchar(120) not null,
   CONFIG_ITEM_NAME     varchar(120) not null,
   IS_VISIABLE          char(1) not null,
   UPDATE_TIME          DATETIME,
   REMARK               VARCHAR(255),
   primary key (CONFIG_ITEM_ID)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_HISTORY                                   */
/*==============================================================*/
create table CONFIG_ITEM_HISTORY
(
   CONFIG_ITEM_ID       int(4) not null,
   SEQ                  int(4) not null,
   MODULE_CODE          varchar(10) not null,
   DIRECTORY_CODE       varchar(10) not null,
   CONFIG_ITEM_CODE     varchar(120) not null,
   CONFIG_ITEM_NAME     varchar(120) not null,
   IS_VISIABLE          char(1) not null,
   UPDATE_TIME          DATETIME not null,
   REMARK               VARCHAR(255),
   OPERATOR_ID          int(8),
   CHANNEL_ID           int(4),
   primary key (CONFIG_ITEM_ID, SEQ)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM                                     */
/*==============================================================*/
create table CONFIG_ITEM_PARAM
(
   CONFIG_ITEM_ID       int(4) not null auto_increment,
   PARAM_CODE           varchar(120) not null,
   PARAM_NAME           varchar(120) not null,
   PARAM_VALUE          varchar(1000),
   DEFAULT_PARAM_VALUE  VARCHAR(1000),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR(2000),
   UPDATE_TIME          DATETIME not null,
   REMARK               varchar(255),
   primary key (CONFIG_ITEM_ID, PARAM_CODE)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_HISTORY                             */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_HISTORY
(
   CONFIG_ITEM_ID       int(4) not null,
   PARAM_CODE           varchar(120) not null,
   SEQ                  int(4) not null,
   PARAM_NAME           varchar(120) not null,
   PARAM_VALUE          varchar(1000),
   DEFAULT_PARAM_VALUE  VARCHAR(1000),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR(2000),
   UPDATE_TIME          DATETIME not null,
   REMARK               varchar(255),
   OPERATOR_ID          INT(8),
   CHANNEL_ID           INT(4),
   primary key (CONFIG_ITEM_ID, PARAM_CODE, SEQ)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_VALUE                               */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_VALUE
(
   CONFIG_ITEM_ID       INT(4) not null auto_increment,
   PARAM_CODE           VARCHAR(120) not null,
   PARAM_VALUE_ID       INT(6) not null,
   VALUE_MARK           VARCHAR(20) not null,
   VALUE                VARCHAR(60),
   REMARK               VARCHAR(255),
   primary key (CONFIG_ITEM_ID, PARAM_CODE, PARAM_VALUE_ID)
);

/*==============================================================*/
/* Table: CONTACT_CHANNEL                                       */
/*==============================================================*/
create table CONTACT_CHANNEL
(
   CONTACT_CHANNEL_ID   INT(2) not null,
   CHANNEL_TYPE         INT(2) not null,
   CONTACT_CHANNEL_NAME VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (CONTACT_CHANNEL_ID)
);

/*==============================================================*/
/* Table: CRON_TRIGGER                                          */
/*==============================================================*/
create table CRON_TRIGGER
(
   TRIGGER_ID           int(8) not null auto_increment,
   TRIGGER_NAME         VARCHAR(60) not null,
   CRON_EXPRESSION      VARCHAR(120) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          int(8),
   primary key (TRIGGER_ID)
);

/*==============================================================*/
/* Table: CRON_TRIGGER_HISTORY                                  */
/*==============================================================*/
create table CRON_TRIGGER_HISTORY
(
   TRIGGER_ID           int(8) not null,
   SEQ                  INT(4) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   CRON_EXPRESSION      VARCHAR(120) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          int(8),
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (TRIGGER_ID, SEQ)
);

/*==============================================================*/
/* Table: DATA_TYPE                                             */
/*==============================================================*/
create table DATA_TYPE
(
   DATA_TYPE            CHAR(1) not null,
   DATA_TYPE_NAME       VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (DATA_TYPE)
);

INSERT INTO DATA_TYPE(DATA_TYPE,DATA_TYPE_NAME,REMARK) VALUES('C','Character','字符Character');
INSERT INTO DATA_TYPE(DATA_TYPE,DATA_TYPE_NAME,REMARK) VALUES('N','Number','整数Number');
INSERT INTO DATA_TYPE(DATA_TYPE,DATA_TYPE_NAME,REMARK) VALUES('P','Password','密码Password');
INSERT INTO DATA_TYPE(DATA_TYPE,DATA_TYPE_NAME,REMARK) VALUES('F','Float','浮点数Float');
INSERT INTO DATA_TYPE(DATA_TYPE,DATA_TYPE_NAME,REMARK) VALUES('O','Object','对象类型');
COMMIT;

/*==============================================================*/
/* Table: DIRECTORY                                             */
/*==============================================================*/
create table DIRECTORY
(
   DIRECTORY_CODE       varchar(20) not null,
   DIRECTORY_NAME       varchar(20) not null,
   PARENT_DIRECTORY_CODE varchar(20),
   REMARK               varchar(64),
   primary key (DIRECTORY_CODE)
);

/*==============================================================*/
/* Table: INPUT_TYPE                                            */
/*==============================================================*/
create table INPUT_TYPE
(
   INPUT_TYPE           CHAR(1) not null,
   INPUT_TYPE_NAME      VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (INPUT_TYPE)
);

insert into INPUT_TYPE (INPUT_TYPE, INPUT_TYPE_NAME, REMARK) values ('1', 'Disable', '不可编辑 Disable');
insert into INPUT_TYPE (INPUT_TYPE, INPUT_TYPE_NAME, REMARK) values ('2', 'Single Choice', '单选Single Choice');
insert into INPUT_TYPE (INPUT_TYPE, INPUT_TYPE_NAME, REMARK) values ('3', 'Multi-Choice', '多选Multi-Choice');
insert into INPUT_TYPE (INPUT_TYPE, INPUT_TYPE_NAME, REMARK) values ('4', 'Date Selector', '时间选择框Date Selector');
insert into INPUT_TYPE (INPUT_TYPE, INPUT_TYPE_NAME, REMARK) values ('5', 'Text', '文本录入Text');
commit;

/*==============================================================*/
/* Table: MENU                                                  */
/*==============================================================*/
create table MENU
(
   MENU_ID              INT(4) not null auto_increment,
   MODULE_CODE          VARCHAR(10) not null,
   SEQ                  INT(4) not null,
   MENU_NAME            VARCHAR(20) not null,
   PARENT_ID            INT(4),
   IS_LEAF              CHAR(1) not null,
   RESOURCE_ID          INT(6),
   ICON_URL             VARCHAR(120),
   primary key (MENU_ID)
);

/*==============================================================*/
/* Table: MESSAGE_ATTACHMENTS                                   */
/*==============================================================*/
create table MESSAGE_ATTACHMENTS
(
   ATTACHMENTS_ID       BIGINT not null,
   MESSAGE_ID           BIGINT not null,
   primary key (ATTACHMENTS_ID, MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_BOX                                           */
/*==============================================================*/
create table MESSAGE_BOX
(
   MESSAGE_ID           bigint not null auto_increment,
   RECEIVERS            TEXT not null,
   SENDER               VARCHAR(120),
   MESSAGE_TYPE         CHAR(1) not null,
   MESSAGE_TEMPLATE_ID  INT(4) not null,
   SUBJECT              VARCHAR(120),
   CONTENT              TEXT,
   ATTACHMENTS_NUM      INT(3) not null,
   CREATE_TIME          DATETIME not null,
   SEND_TIME            DATETIME,
   NEXT_SEND_TIME       DATETIME,
   SEND_TIMES           INT(4) not null,
   EXTEND_ATTRS         TEXT,
   primary key (MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_HISTORY                                       */
/*==============================================================*/
create table MESSAGE_HISTORY
(
   MESSAGE_ID           bigint not null auto_increment,
   RECEIVERS            TEXT not null,
   SENDER               VARCHAR(120),
   MESSAGE_TYPE         CHAR(1) not null,
   MESSAGE_TEMPLATE_ID  INT(4) not null,
   SUBJECT              VARCHAR(120),
   CONTENT              TEXT,
   ATTACHMENTS_NUM      INT(3) not null,
   CREATE_TIME          DATETIME not null,
   SEND_TIME            DATETIME,
   SEND_TIMES           INT(4) not null,
   RESULT               VARCHAR(255) not null,
   EXP_DATE             DATE,
   EXTEND_ATTRS         TEXT,
   primary key (MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_TEMPLATE                                      */
/*==============================================================*/
create table MESSAGE_TEMPLATE
(
   MESSAGE_TEMPLATE_ID  INT(4) not null auto_increment,
   MESSAGE_TEMPLATE_CODE VARCHAR(20) not null,
   DIRECTORY_CODE       VARCHAR(20) not null,
   NAME                 VARCHAR(120) not null,
   TEMPLATE             TEXT,
   STATE                CHAR(1) not null,
   CONTACT_CHANNEL_IDS  VARCHAR(8),
   STATE_TIME           DATETIME not null,
   DELAY                INT(6) not null,
   RESEND_TIMES         INT(4) not null,
   SAVE_HISTORY         CHAR(1) not null,
   SAVE_DAY             INT(4) not null,
   CREATE_TIME          DATETIME,
   primary key (MESSAGE_TEMPLATE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_TYPE                                          */
/*==============================================================*/
create table MESSAGE_TYPE
(
   MESSAGE_TYPE         CHAR(1) not null,
   MESSAGE_TYPE_NAME    VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (MESSAGE_TYPE)
);

INSERT INTO MESSAGE_TYPE VALUES
('T','Text','文本消息'),
('P','Picture','图片消息'),
('V','Voice','语音消息'),
('M','Media','多媒体消息');

/*==============================================================*/
/* Table: MODULE                                                */
/*==============================================================*/
create table MODULE
(
   MODULE_CODE          varchar(10) not null,
   PARENT_MODULE_CODE   varchar(10),
   MODULE_NAME          varchar(20) not null,
   primary key (MODULE_CODE)
);

INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES 
('COMMON',NULL,'公共模块'),
('PORTAL','COMMON','系统门户'),
('MESSAGE', 'COMMON', '消息中心'),
('MONITOR','COMMON','系统监控模块');

/*==============================================================*/
/* Table: OPERATOR                                              */
/*==============================================================*/
create table OPERATOR
(
   OPERATOR_ID          INT(8) not null auto_increment,
   OPERATOR_TYPE        CHAR(1) not null,
   OPERATOR_CODE        INT(10),
   USER_NAME            VARCHAR(60),
   PASSWORD             VARCHAR(60),
   CREATE_DATE          DATETIME not null,
   STATE                CHAR(1) not null,
   STATE_DATE           DATETIME not null,
   IS_LOCKED            CHAR(1) not null,
   PWD_EXP_DATE         DATETIME,
   LOGIN_FAIL           INT(4) not null,
   REGIST_IP            VARCHAR(16),
   LAST_IP              VARCHAR(16),
   LAST_LOGIN_DATE      DATETIME,
   primary key (OPERATOR_ID)
);

/*==============================================================*/
/* Table: OPERATOR_HISTORY                                      */
/*==============================================================*/
create table OPERATOR_HISTORY
(
   OPERATOR_ID          INT(8) not null,
   SEQ                  INT(8) not null,
   OPERATOR_TYPE        CHAR(1) not null,
   OPERATOR_CODE        VARCHAR(10) not null,
   USER_NAME            VARCHAR(60),
   PASSWORD             VARCHAR(60),
   CREATE_DATE          DATETIME not null,
   STATE                CHAR(1) not null,
   STATE_DATE           DATETIME not null,
   IS_LOCKED            CHAR(1) not null,
   PWD_EXP_DATE         DATETIME,
   LOGIN_FAIL           INT(4) not null,
   REGIST_IP            VARCHAR(16),
   LAST_IP              VARCHAR(16),
   LAST_LOGIN_DATE      DATETIME,
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (OPERATOR_ID, SEQ)
);

/*==============================================================*/
/* Table: OPERATOR_RESOURCE                                     */
/*==============================================================*/
create table OPERATOR_RESOURCE
(
   OPERATOR_ID          INT(8) not null,
   RESOURCE_ID          INT(6) not null,
   RESOURCE_TYPE        INT(1) not null,
   primary key (OPERATOR_ID, RESOURCE_ID, RESOURCE_TYPE)
);

/*==============================================================*/
/* Table: OPERATOR_ROLE_HISTORY                                 */
/*==============================================================*/
create table OPERATOR_ROLE_HISTORY
(
   OPERATOR_ID          INT(8) not null,
   ROLE_ID              INT(4) not null,
   SEQ                  INT(4) not null,
   CREATE_TIME          DATETIME not null,
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (OPERATOR_ID, ROLE_ID, SEQ)
);

/*==============================================================*/
/* Table: OPERATOR_TYPE                                         */
/*==============================================================*/
create table OPERATOR_TYPE
(
   OPERATOR_TYPE        CHAR(1) not null,
   OPERATOR_TYPE_NAME   VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (OPERATOR_TYPE)
);

INSERT INTO OPERATOR_TYPE VALUES ('A','Admin','管理员');
INSERT INTO OPERATOR_TYPE VALUES ('M','Member','会员');
INSERT INTO OPERATOR_TYPE VALUES ('C','Community','物业管理员');
INSERT INTO OPERATOR_TYPE VALUES ('B','Business','商家');

/*==============================================================*/
/* Table: QRTZ_BLOB_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_BLOB_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   BLOB_DATA            BLOB,
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_BLOB_TRIGGERS comment 'Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型';

/*==============================================================*/
/* Table: QRTZ_CALENDARS                                        */
/*==============================================================*/
create table QRTZ_CALENDARS
(
   SCHED_NAME           VARCHAR(20) not null,
   CALENDAR_NAME        VARCHAR(60) not null,
   CALENDAR             BLOB not null,
   primary key (SCHED_NAME, CALENDAR_NAME)
);

alter table QRTZ_CALENDARS comment '以 Blob 类型存储 Quartz 的 Calendar 信息';

/*==============================================================*/
/* Table: QRTZ_CRON_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_CRON_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   CRON_EXPRESSION      VARCHAR(120) not null,
   TIME_ZONE_ID         VARCHAR(80),
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_CRON_TRIGGERS comment '存储 Cron Trigger，包括 Cron 表达式和时区信息';

/*==============================================================*/
/* Table: QRTZ_FIRED_TRIGGERS                                   */
/*==============================================================*/
create table QRTZ_FIRED_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   ENTRY_ID             VARCHAR(95) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   INSTANCE_NAME        VARCHAR(60) not null,
   FIRED_TIME           BIGINT(13) not null,
   SCHED_TIME           BIGINT(13) not null,
   PRIORITY             INTEGER not null,
   STATE                VARCHAR(16) not null,
   JOB_NAME             VARCHAR(200),
   JOB_GROUP            VARCHAR(200),
   IS_NONCONCURRENT     VARCHAR(1),
   REQUESTS_RECOVERY    VARCHAR(1),
   primary key (SCHED_NAME, ENTRY_ID)
);

alter table QRTZ_FIRED_TRIGGERS comment '存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息';

/*==============================================================*/
/* Table: QRTZ_JOB_DETAILS                                      */
/*==============================================================*/
create table QRTZ_JOB_DETAILS
(
   SCHED_NAME           VARCHAR(20) not null,
   JOB_NAME             VARCHAR(60) not null,
   JOB_GROUP            VARCHAR(60) not null,
   DESCRIPTION          VARCHAR(250),
   JOB_CLASS_NAME       VARCHAR(250) not null,
   IS_DURABLE           CHAR(1) not null,
   IS_NONCONCURRENT     CHAR(1) not null,
   IS_UPDATE_DATA       CHAR(1) not null,
   REQUESTS_RECOVERY    CHAR(1) not null,
   JOB_DATA             BLOB,
   primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

alter table QRTZ_JOB_DETAILS comment '存储每一个已配置的 Job 的详细信息';

/*==============================================================*/
/* Table: QRTZ_LOCKS                                            */
/*==============================================================*/
create table QRTZ_LOCKS
(
   SCHED_NAME           VARCHAR(20) not null,
   LOCK_NAME            VARCHAR(40) not null,
   primary key (SCHED_NAME, LOCK_NAME)
);

alter table QRTZ_LOCKS comment '存储程序的非观锁的信息(假如使用了悲观锁)';

/*==============================================================*/
/* Table: QRTZ_PAUSED_TRIGGER_GRPS                              */
/*==============================================================*/
create table QRTZ_PAUSED_TRIGGER_GRPS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   primary key (SCHED_NAME, TRIGGER_GROUP)
);

alter table QRTZ_PAUSED_TRIGGER_GRPS comment '存储已暂停的 Trigger 组的信息';

/*==============================================================*/
/* Table: QRTZ_SCHEDULER_STATE                                  */
/*==============================================================*/
create table QRTZ_SCHEDULER_STATE
(
   SCHED_NAME           VARCHAR(20) not null,
   INSTANCE_NAME        VARCHAR(60) not null,
   LAST_CHECKIN_TIME    BIGINT(13) not null,
   CHECKIN_INTERVAL     BIGINT(13) not null,
   primary key (SCHED_NAME, INSTANCE_NAME)
);

alter table QRTZ_SCHEDULER_STATE comment '存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)';

/*==============================================================*/
/* Table: QRTZ_SIMPLE_TRIGGERS                                  */
/*==============================================================*/
create table QRTZ_SIMPLE_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   REPEAT_COUNT         BIGINT(7) not null,
   REPEAT_INTERVAL      BIGINT(12) not null,
   TIMES_TRIGGERED      BIGINT(10) not null,
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_SIMPLE_TRIGGERS comment ' 存储简单的 Trigger，包括重复次数，间隔，以及已触的次数';

/*==============================================================*/
/* Table: QRTZ_SIMPROP_TRIGGERS                                 */
/*==============================================================*/
create table QRTZ_SIMPROP_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   STR_PROP_1           VARCHAR(512),
   STR_PROP_2           VARCHAR(512),
   STR_PROP_3           VARCHAR(512),
   INT_PROP_1           INT,
   INT_PROP_2           INT,
   LONG_PROP_1          BIGINT,
   LONG_PROP_2          BIGINT,
   DEC_PROP_1           NUMERIC(13,4),
   DEC_PROP_2           NUMERIC(13,4),
   BOOL_PROP_1          VARCHAR(1),
   BOOL_PROP_2          VARCHAR(1),
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

/*==============================================================*/
/* Table: QRTZ_TRIGGERS                                         */
/*==============================================================*/
create table QRTZ_TRIGGERS
(
   SCHED_NAME           VARCHAR(20) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   TRIGGER_GROUP        VARCHAR(60) not null,
   JOB_NAME             VARCHAR(60) not null,
   JOB_GROUP            VARCHAR(60) not null,
   DESCRIPTION          VARCHAR(250),
   NEXT_FIRE_TIME       BIGINT(13),
   PREV_FIRE_TIME       BIGINT(13),
   PRIORITY             INTEGER,
   TRIGGER_STATE        VARCHAR(16) not null,
   TRIGGER_TYPE         VARCHAR(8) not null,
   START_TIME           BIGINT(13) not null,
   END_TIME             BIGINT(13),
   CALENDAR_NAME        VARCHAR(200),
   MISFIRE_INSTR        SMALLINT(2),
   JOB_DATA             BLOB,
   primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

alter table QRTZ_TRIGGERS comment '存储已配置的 Trigger 的信息';

/*==============================================================*/
/* Table: RESOURCE_TYPE                                         */
/*==============================================================*/
create table RESOURCE_TYPE
(
   RESOURCE_TYPE        INT(1) not null,
   RESOURCE_TYPE_NAME   VARCHAR(20) not null,
   REMARK               VARCHAR(60),
   primary key (RESOURCE_TYPE)
);

/*==============================================================*/
/* Table: ROLE                                                  */
/*==============================================================*/
create table ROLE
(
   ROLE_ID              INT(4) not null auto_increment,
   MODULE_CODE          VARCHAR(10) not null,
   ROLE_NAME            VARCHAR(60) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          INT(8),
   primary key (ROLE_ID)
);

/*==============================================================*/
/* Table: ROLE_HISTORY                                          */
/*==============================================================*/
create table ROLE_HISTORY
(
   ROLE_ID              INT(4) not null,
   SEQ                  INT(4) not null,
   ROLE_NAME            VARCHAR(60) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          INT(8),
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (ROLE_ID, SEQ)
);

/*==============================================================*/
/* Table: ROLE_RESOURCE                                         */
/*==============================================================*/
create table ROLE_RESOURCE
(
   ROLE_ID              INT(4) not null,
   RESOURCE_ID          INT(6) not null,
   RESOURCE_TYPE        INT(1) not null,
   primary key (ROLE_ID, RESOURCE_ID, RESOURCE_TYPE)
);

/*==============================================================*/
/* Table: SEND_RECORD                                           */
/*==============================================================*/
create table SEND_RECORD
(
   SEND_RECORD_ID       BIGINT not null auto_increment,
   MESSAGE_ID           BIGINT not null,
   CONTACT_CHANNEL_ID   INT(2) not null,
   SEND_TIME            DATETIME not null,
   RESULT               VARCHAR(255) not null,
   primary key (SEND_RECORD_ID)
);

/*==============================================================*/
/* Table: SIMPLE_TRIGGER                                        */
/*==============================================================*/
create table SIMPLE_TRIGGER
(
   TRIGGER_ID           int(8) not null auto_increment,
   TRIGGER_NAME         VARCHAR(60) not null,
   BEGIN_TIME           DATETIME not null,
   END_TIME             DATETIME,
   TIMES                int(4),
   EXECUTE_INTERVAL     int(4) not null,
   INTERVAL_UNIT        CHAR(1) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          int(8),
   primary key (TRIGGER_ID)
);

/*==============================================================*/
/* Table: SIMPLE_TRIGGER_HISTORY                                */
/*==============================================================*/
create table SIMPLE_TRIGGER_HISTORY
(
   TRIGGER_ID           int(8) not null,
   SEQ                  int(4) not null,
   TRIGGER_NAME         VARCHAR(60) not null,
   BEGIN_TIME           DATETIME not null,
   END_TIME             DATETIME,
   TIMES                int(4),
   EXECUTE_INTERVAL     int(4) not null,
   INTERVAL_UNIT        CHAR(1) not null,
   CREATE_TIME          DATETIME not null,
   OPERATOR_ID          int(8),
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (TRIGGER_ID, SEQ)
);

/*==============================================================*/
/* Table: TASK                                                  */
/*==============================================================*/
create table TASK
(
   TASK_ID              int(8) not null auto_increment,
   TASK_NAME            varchar(60) not null,
   CLASS_NAME           VARCHAR(60) not null,
   METHOD               VARCHAR(20) not null,
   MODULE_CODE          VARCHAR(10) not null,
   PRIORITY             int(1) not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1) not null,
   LAST_EXECUTE_TIME    DATETIME,
   NEXT_EXCUTE_DATE     DATETIME,
   OPERATOR_ID          int(8),
   CREATE_TIME          DATETIME not null,
   primary key (TASK_ID),
   key AK_Key_2 (TASK_NAME)
);

/*==============================================================*/
/* Table: TASK_HISTORY                                          */
/*==============================================================*/
create table TASK_HISTORY
(
   TASK_ID              int(8) not null,
   SEQ                  INT(8) not null,
   TASK_NAME            varchar(60) not null,
   CLASS_NAME           VARCHAR(60) not null,
   METHOD               VARCHAR(20) not null,
   MODULE_CODE          VARCHAR(10) not null,
   PRIORITY             int(1) not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1) not null,
   LAST_EXECUTE_TIME    DATETIME,
   NEXT_EXCUTE_DATE     DATETIME,
   OPERATOR_ID          int(8),
   CREATE_TIME          DATETIME not null,
   UPDATE_TIME          DATETIME not null,
   UPDATE_OPERATOR_ID   INT(8),
   primary key (TASK_ID, SEQ)
);

/*==============================================================*/
/* Table: TASK_STATE                                            */
/*==============================================================*/
create table TASK_STATE
(
   TASK_STATE           CHAR(1) not null,
   TASK_STATE_NAME      VARCHAR(20) not null,
   REMARK               VARCHAR(120),
   primary key (TASK_STATE)
);

INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('I','INITIAL','初始状态');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('W','WAITING','等待状态');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('P','PAUSED','暂停状态');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('A','ACQUIRED','正常执行');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('B','BLOCKED','阻塞状态');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('E','ERROR','错误状态');
INSERT INTO TASK_STATE (TASK_STATE,TASK_STATE_NAME,REMARK) VALUES ('C','COMPLETE','完成状态');

/*==============================================================*/
/* Table: TASK_TRIGGER                                          */
/*==============================================================*/
create table TASK_TRIGGER
(
   TASK_ID              INT(8) not null,
   TRIGGER_TYPE         INT(1) not null,
   TRIGGER_ID           INT(8) not null,
   primary key (TASK_ID, TRIGGER_TYPE, TRIGGER_ID)
);

/*==============================================================*/
/* Table: TRANS_LOG                                             */
/*==============================================================*/
create table TRANS_LOG
(
   TRANS_ID             varchar(36) not null,
   MODULE_CODE          varchar(10),
   BEGIN_TIME           DATETIME not null,
   END_TIME             DATETIME not null,
   CONSUME_TIME         int(8) not null,
   INPUT_PARAM          TEXT,
   OUTPUT_PARAM         TEXT,
   SQL_LOG              longtext,
   EXCEPTION_LOG        longtext,
   CONTACT_CHANNEL_ID   INT(2),
   primary key (TRANS_ID)
);

/*==============================================================*/
/* Table: TRANS_LOG_STACK                                       */
/*==============================================================*/
create table TRANS_LOG_STACK
(
   STACK_ID             varchar(36) not null,
   SEQ                  INT(4) not null,
   TRANS_ID             VARCHAR(36) not null,
   PARENT_STACK_ID      VARCHAR(36),
   METHOD               TEXT not null,
   BEGIN_TIME           DATETIME not null,
   END_TIME             DATETIME not null,
   CONSUME_TIME         INT(8) not null,
   INPUT_PARAM          TEXT,
   OUTPUT_PARAM         TEXT,
   IS_SUCCESS           CHAR(1) not null,
   primary key (STACK_ID)
);

/*==============================================================*/
/* Table: TRIGGER_TYPE                                          */
/*==============================================================*/
create table TRIGGER_TYPE
(
   TRIGGER_TYPE         INT(1) not null,
   TRIGGER_TYPE_NAME    varchar(20) not null,
   REMARK               varchar(120),
   primary key (TRIGGER_TYPE)
);

INSERT INTO TRIGGER_TYPE(TRIGGER_TYPE,TRIGGER_TYPE_NAME,REMARK) VALUES(1,'简单触发器类型','简单触发器可以设置开始时间、结束时间、执行间隔、执行次数等');
INSERT INTO TRIGGER_TYPE(TRIGGER_TYPE,TRIGGER_TYPE_NAME,REMARK) VALUES(2,'Cron表达式触发器类型','Cron表达式触发器采用Cron表达式来设置触发器的');

/*==============================================================*/
/* Table: URL_RESOURCE                                          */
/*==============================================================*/
create table URL_RESOURCE
(
   RESOURCE_ID          INT(6) not null auto_increment,
   DIRECTORY_CODE       VARCHAR(20) not null,
   RESOURCE_NAME        VARCHAR(60) not null,
   URL                  VARCHAR(120) not null,
   EXECUTE_CLASS        VARCHAR(120),
   EXECUTE_METHOD       VARCHAR(60),
   MODULE_CODE          VARCHAR(10) not null,
   REMARK               VARCHAR(255),
   primary key (RESOURCE_ID)
);

alter table ACCOUNT add constraint FK_ACCOUNT_TYPE_ACCOUNT foreign key (ACCOUNT_TYPE)
      references ACCOUNT_TYPE (ACCOUNT_TYPE) on delete restrict on update restrict;

alter table ACCOUNT add constraint FK_OPERATOR_ID_OPERATOR_IDENTITY_OPERATOR_ID foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID) on delete restrict on update restrict;

alter table ADMIN add constraint FK_OPERATOR_ID_ADMIN_OPERATOR_ID foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID) on delete restrict on update restrict;

alter table ADMIN_ATTR add constraint FK_ADMIN_ID_ADMIN_ATTR_ADMIN_ID foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID) on delete restrict on update restrict;

alter table ADMIN_ATTR add constraint FK_ATTR_ID_ADMIN_ATTR_ATTR_ID foreign key (ATTR_ID)
      references ATTR (ATTR_ID) on delete restrict on update restrict;

alter table ADMIN_ROLE add constraint FK_ADMIN_ID_ADMIN_ROLE_ADMIN_ID foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID) on delete restrict on update restrict;

alter table ADMIN_ROLE add constraint FK_ROLE_ID_ADMIN_ROLE_ROLE_ID foreign key (ROLE_ID)
      references ROLE (ROLE_ID) on delete restrict on update restrict;

alter table AREA add constraint FK_AREA_ID_AREA_PARENT_AREA_ID foreign key (PARENT_AREA_ID)
      references AREA (AREA_ID) on delete restrict on update restrict;

alter table AREA add constraint FK_AREA_TYPE_AREA_AREA_TYPE foreign key (AREA_TYPE)
      references AREA_TYPE (AREA_TYPE) on delete restrict on update restrict;

alter table AREA_RANGE add constraint FK_AREA_AREA_RANGE foreign key (AREA_ID)
      references AREA (AREA_ID) on delete restrict on update restrict;

alter table ATTR add constraint FK_ATTR_TYPE_ATTR_ATTR_TYPE foreign key (ATTR_TYPE)
      references ATTR_TYPE (ATTR_TYPE) on delete restrict on update restrict;

alter table ATTR add constraint FK_DATA_TYPE_ATTR_DATA_TYPE foreign key (DATA_TYPE)
      references DATA_TYPE (DATA_TYPE) on delete restrict on update restrict;

alter table ATTR add constraint FK_INPUT_TYPE_ATTR_INPUT_TYPE foreign key (INPUT_TYPE)
      references INPUT_TYPE (INPUT_TYPE) on delete restrict on update restrict;

alter table ATTR_VALUE add constraint FK_ATTR_ID_ATTR_VALUE_ATTR_ID foreign key (ATTR_ID)
      references ATTR (ATTR_ID) on delete restrict on update restrict;

alter table ATTR_VALUE add constraint FK_ATTR_ID_ATTR_VALUE_LINK_ATTR_ID foreign key (LINK_ATTR_ID)
      references ATTR (ATTR_ID) on delete restrict on update restrict;

alter table CONFIG_ITEM add constraint FK_DIRECTORY_CODE_CONFIG_ITEM_DIRECTORY_CODE foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE) on delete restrict on update restrict;

alter table CONFIG_ITEM add constraint FK_MODULE_CODE_CONFIG_ITEM_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table CONFIG_ITEM_PARAM add constraint FK_CONFIG_ITEM_ID_CONFIG_ITEM_PARAM_ITEM_ID foreign key (CONFIG_ITEM_ID)
      references CONFIG_ITEM (CONFIG_ITEM_ID) on delete restrict on update restrict;

alter table CONFIG_ITEM_PARAM add constraint FK_DATA_TYPE_CONFIG_ITEM_DATA_TYPE foreign key (DATA_TYPE)
      references DATA_TYPE (DATA_TYPE) on delete restrict on update restrict;

alter table CONFIG_ITEM_PARAM add constraint FK_INPUT_TYPE_CONFIG_ITEM_PARAM_INPUT_TYPE foreign key (INPUT_TYPE)
      references INPUT_TYPE (INPUT_TYPE) on delete restrict on update restrict;

alter table CONFIG_ITEM_PARAM_VALUE add constraint FK_CONFIG_ITEM_PARAM_CONFIG_ITEM_PARAM_VALUE foreign key (CONFIG_ITEM_ID, PARAM_CODE)
      references CONFIG_ITEM_PARAM (CONFIG_ITEM_ID, PARAM_CODE) on delete restrict on update restrict;

alter table CONTACT_CHANNEL add constraint FK_CHANNEL_TYPE_CONTACT_CHANNEL_CHANNEL_TYPE foreign key (CHANNEL_TYPE)
      references CHANNEL_TYPE (CHANNEL_TYPE) on delete restrict on update restrict;

alter table DIRECTORY add constraint FK_DIRECTORY_CODE_PARENT_DIRECTORY_CODE foreign key (PARENT_DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE);

alter table MENU add constraint FK_MENU_MENU_MENU_ID foreign key (PARENT_ID)
      references MENU (MENU_ID) on delete restrict on update restrict;

alter table MENU add constraint FK_MODULE_MENU_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table MENU add constraint FK_RESOURCE_ID_MENU_RESOURCE_ID foreign key (RESOURCE_ID)
      references URL_RESOURCE (RESOURCE_ID) on delete restrict on update restrict;

alter table MESSAGE_ATTACHMENTS add constraint FK_ATTACHMENTS_MESSAGE_ATTACHMENTS foreign key (ATTACHMENTS_ID)
      references ATTACHMENTS (ATTACHMENTS_ID) on delete restrict on update restrict;

alter table MESSAGE_BOX add constraint FK_MESSAGE_TEMPLATE_MESSAGE_BOX foreign key (MESSAGE_TEMPLATE_ID)
      references MESSAGE_TEMPLATE (MESSAGE_TEMPLATE_ID) on delete restrict on update restrict;

alter table MESSAGE_BOX add constraint FK_MESSAGE_TYPE_SEND_BOX foreign key (MESSAGE_TYPE)
      references MESSAGE_TYPE (MESSAGE_TYPE) on delete restrict on update restrict;

alter table MESSAGE_TEMPLATE add constraint FK_DIRECTORY_MESSAGE_TEMPLATE foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE) on delete restrict on update restrict;

alter table MODULE add constraint FK_MODULE_CODE_PARENT_MODULE_CODE foreign key (PARENT_MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table OPERATOR add constraint FK_OPERATOR_TYPE_OPERATOR_OPERATOR_TYPE foreign key (OPERATOR_TYPE)
      references OPERATOR_TYPE (OPERATOR_TYPE) on delete restrict on update restrict;

alter table OPERATOR_RESOURCE add constraint FK_OPERATOR_ID_OPERATOR_RESOURCE_OPERATOR_ID foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID) on delete restrict on update restrict;

alter table OPERATOR_RESOURCE add constraint FK_RESOURCE_TYPE_OPERATOR_RESOURCE_RESOURCE_TYPE foreign key (RESOURCE_TYPE)
      references RESOURCE_TYPE (RESOURCE_TYPE) on delete restrict on update restrict;

alter table QRTZ_BLOB_TRIGGERS add constraint FK_Reference_5 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_CRON_TRIGGERS add constraint FK_Reference_3 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPLE_TRIGGERS add constraint FK_Reference_2 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPROP_TRIGGERS add constraint FK_Reference_4 foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_TRIGGERS add constraint FK_Reference_1 foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP)
      references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP);

alter table ROLE add constraint FK_MODULE_CODE_ROLE_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table ROLE_RESOURCE add constraint FK_RESOURCE_TYPE_ROLE_RESOURCE_RESOURCE_TYPE foreign key (RESOURCE_TYPE)
      references RESOURCE_TYPE (RESOURCE_TYPE) on delete restrict on update restrict;

alter table ROLE_RESOURCE add constraint FK_ROLE_ID_ROLE_RESOURCE_ROLE_ID foreign key (ROLE_ID)
      references ROLE (ROLE_ID) on delete restrict on update restrict;

alter table SEND_RECORD add constraint FK_CONTACT_CHANNEL_SEND_RECORD foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID) on delete restrict on update restrict;

alter table TASK add constraint FK_MODULE_CODE_TASK_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table TASK add constraint FK_TASK_STATE_TASK_TASK_STATE foreign key (TASK_STATE)
      references TASK_STATE (TASK_STATE) on delete restrict on update restrict;

alter table TASK_TRIGGER add constraint FK_TASK_ID_TASK_TRIGGER_TASK_ID foreign key (TASK_ID)
      references TASK (TASK_ID) on delete restrict on update restrict;

alter table TASK_TRIGGER add constraint FK_TRIGGER_TYPE_TASK_TRIGGER_TRIGGER_TYPE foreign key (TRIGGER_TYPE)
      references TRIGGER_TYPE (TRIGGER_TYPE) on delete restrict on update restrict;

alter table TRANS_LOG add constraint FK_CONTACT_CHANNEL_ID_TRANS_LOG_CONTACT_CHANNEL_ID foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID) on delete restrict on update restrict;

alter table TRANS_LOG add constraint FK_MODULE_CODE_TRANS_LOG_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

alter table URL_RESOURCE add constraint FK_DIRECTORY_CODE_URL_RESOURCE_DIRECTORY_CODE foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE) on delete restrict on update restrict;

alter table URL_RESOURCE add constraint FK_MODULE_CODE_URL_RESOURCE_MODULE_CODE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE) on delete restrict on update restrict;

