/*==============================================================*/
/* DBMS name:      ORACLE Version 11g                           */
/* Created on:     2015/7/11 11:05:38                           */
/*==============================================================*/


create sequence SEQ_ACCOUNT
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_ADMIN
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_AREA
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_ATTACHMENTS
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_ATTR
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_ATTR_VALUE
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_CONFIG_ITEM
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_CONFIG_ITEM_PARAM
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_CRON_TRIGGER
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_MENU
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_MESSAGE_BOX
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_MESSAGE_HISTORY
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_MESSAGE_TEMPLATE
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_OPERATE_LOG
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle;

create sequence SEQ_OPERATOR
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
 cache 20;

create sequence SEQ_ROLE
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_SIMPLE_TRIGGER
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence SEQ_URL_RESOURCE
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence S_CONFIG_ITEM_PARAM_VALUE
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence S_SEND_RECORD
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

create sequence S_TASK
increment by 1
start with 1000
 maxvalue 99999999999
 minvalue 1
nocycle
 cache 20;

/*==============================================================*/
/* Table: ACCOUNT                                               */
/*==============================================================*/
create table ACCOUNT 
(
   ACCOUNT_ID           NUMBER(8)            not null,
   ACCOUNT_VALUE        VARCHAR2(120)        not null,
   ACCOUNT_TYPE         VARCHAR2(2)          not null,
   OPERATOR_ID          NUMBER(8)            not null,
   CREATE_TIME          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_TIME           DATE                 not null,
   EXT1                 VARCHAR2(120),
   EXT2                 VARCHAR2(120),
   constraint PK_ACCOUNT primary key (ACCOUNT_ID)
);

/*==============================================================*/
/* Table: ADMIN                                                 */
/*==============================================================*/
create table ADMIN 
(
   ADMIN_ID             NUMBER(6)            not null,
   ADMIN_NAME           VARCHAR2(60)         not null,
   CREATED_TIME         DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   EMAIL                VARCHAR2(120),
   PHONE                VARCHAR2(20),
   OPERATOR_ID          NUMBER(8),
   ADDRESS              VARCHAR2(255),
   constraint PK_ADMIN primary key (ADMIN_ID)
);

/*==============================================================*/
/* Table: ADMIN_ATTR                                            */
/*==============================================================*/
create table ADMIN_ATTR 
(
   ADMIN_ID             NUMBER(8)            not null,
   ATTR_ID              NUMBER(4)            not null,
   VALUE                VARCHAR2(120),
   CREATE_TIME          DATE                 not null,
   constraint PK_ADMIN_ATTR primary key (ADMIN_ID, ATTR_ID)
);

/*==============================================================*/
/* Table: ADMIN_ATTR_HISTORY                                    */
/*==============================================================*/
create table ADMIN_ATTR_HISTORY 
(
   ADMIN_ID             NUMBER(8)            not null,
   ATTR_ID              NUMBER(4)            not null,
   SEQ                  NUMBER(4)            not null,
   VALUE                VARCHAR2(120),
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPRATOR_ID    NUMBER(8),
   constraint PK_ADMIN_ATTR_HISTORY primary key (ADMIN_ID, ATTR_ID, SEQ)
);

/*==============================================================*/
/* Table: ADMIN_HISTORY                                         */
/*==============================================================*/
create table ADMIN_HISTORY 
(
   ADMIN_ID             NUMBER(6)            not null,
   SEQ                  NUMBER(4)            not null,
   ADMIN_NAME           VARCHAR2(60)         not null,
   CREATED_TIME         DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   EMAIL                VARCHAR2(120),
   PHONE                VARCHAR2(20),
   ADDRESS              VARCHAR2(255),
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_ADMIN_HISTORY primary key (ADMIN_ID, SEQ)
);

/*==============================================================*/
/* Table: ADMIN_ROLE                                            */
/*==============================================================*/
create table ADMIN_ROLE 
(
   ADMIN_ID             NUMBER(8)            not null,
   ROLE_ID              NUMBER(4)            not null,
   CREATE_TIME          DATE                 not null,
   constraint PK_ADMIN_ROLE primary key (ADMIN_ID, ROLE_ID)
);

/*==============================================================*/
/* Table: AREA                                                  */
/*==============================================================*/
create table AREA 
(
   AREA_ID              NUMBER(10)           not null,
   PARENT_AREA_ID       NUMBER(10),
   AREA_TYPE            CHAR(1)              not null,
   AREA_NAME            VARCHAR2(20)         not null,
   AREA_CODE            VARCHAR2(10),
   REMARK               VARCHAR2(120),
   constraint PK_AREA primary key (AREA_ID)
);

/*==============================================================*/
/* Table: AREA_RANGE                                            */
/*==============================================================*/
create table AREA_RANGE 
(
   AREA_ID              NUMBER(10)           not null,
   SEQ                  NUMBER(6)            not null,
   LONGITUDE            VARCHAR2(20)         not null,
   LATITUDE             VARCHAR2(20)         not null,
   constraint PK_AREA_RANGE primary key (AREA_ID, SEQ)
);

/*==============================================================*/
/* Table: ATTACHMENTS                                           */
/*==============================================================*/
create table ATTACHMENTS 
(
   ATTACHMENTS_ID       NUMBER(6)            not null,
   ATTACHMENTS_TYPE     VARCHAR2(20)         not null,
   ATTACHMENTS_NAME     VARCHAR2(60)         not null,
   IS_REMOTE            CHAR(1)              not null,
   FILE_SIZE            NUMBER(10),
   FILE_PATH            VARCHAR2(255)        not null,
   DOWNLOADS_NUM        NUMBER(8)            not null,
   IS_PICTURE           CHAR(1)              not null,
   IS_THUMB             CHAR(1)              not null,
   THUMB_PATH           VARCHAR2(255),
   CREATE_TIME          DATE                 not null,
   EXP_TIME             DATE,
   constraint PK_ATTACHMENTS primary key (ATTACHMENTS_ID)
);

/*==============================================================*/
/* Table: ATTR                                                  */
/*==============================================================*/
create table ATTR 
(
   ATTR_ID              NUMBER(6)            not null,
   ATTR_NAME            VARCHAR2(60)         not null,
   ATTR_TYPE            CHAR(1)              not null,
   PARENT_ATTR_ID       NUMBER(4),
   ATTR_CODE            VARCHAR2(120)        not null,
   VISIBLE              CHAR(1)              not null,
   INSTANTIATABLE       CHAR(1)              not null,
   DEFAULT_VALUE        VARCHAR2(120),
   INPUT_TYPE           CHAR(1)              not null,
   DATA_TYPE            CHAR(1)              not null,
   VALUE_SCRIPT         VARCHAR2(2000),
   constraint PK_ATTR primary key (ATTR_ID)
);

/*==============================================================*/
/* Table: ATTR_VALUE                                            */
/*==============================================================*/
create table ATTR_VALUE 
(
   ATTR_ID              NUMBER(6)            not null,
   ATTR_VALUE_ID        NUMBER(6)            not null,
   VALUE_MARK           VARCHAR2(60)         not null,
   VALUE                VARCHAR2(120),
   LINK_ATTR_ID         NUMBER(6),
   constraint PK_ATTR_VALUE primary key (ATTR_ID, ATTR_VALUE_ID)
);

/*==============================================================*/
/* Table: CONFIG_ITEM                                           */
/*==============================================================*/
create table CONFIG_ITEM 
(
   CONFIG_ITEM_ID       NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   CONFIG_ITEM_CODE     VARCHAR2(120)        not null,
   CONFIG_ITEM_NAME     VARCHAR2(120)        not null,
   IS_VISIABLE          CHAR(1)              not null,
   UPDATE_TIME          DATE,
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM primary key (CONFIG_ITEM_ID)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_HISTORY                                   */
/*==============================================================*/
create table CONFIG_ITEM_HISTORY 
(
   CONFIG_ITEM_ID       NUMBER(4)            not null,
   SEQ                  NUMBER(4)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   DIRECTORY_CODE       VARCHAR2(10)         not null,
   CONFIG_ITEM_CODE     VARCHAR2(120)        not null,
   CONFIG_ITEM_NAME     VARCHAR2(120)        not null,
   IS_VISIABLE          CHAR(1)              not null,
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   OPERATOR_ID          NUMBER(8),
   CHANNEL_ID           NUMBER(4),
   constraint PK_CONFIG_ITEM_HISTORY primary key (CONFIG_ITEM_ID, SEQ)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM                                     */
/*==============================================================*/
create table CONFIG_ITEM_PARAM 
(
   CONFIG_ITEM_ID       NUMBER(6)            not null,
   PARAM_CODE           VARCHAR2(120)        not null,
   PARAM_NAME           VARCHAR2(120)        not null,
   PARAM_VALUE          VARCHAR2(1000),
   DEFAULT_PARAM_VALUE  VARCHAR2(1000),
   DATA_TYPE            CHAR(1)              not null,
   INPUT_TYPE           CHAR(1)              not null,
   VALUE_SCRIPT         VARCHAR2(2000),
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM_PARAM primary key (CONFIG_ITEM_ID, PARAM_CODE)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_HISTORY                             */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_HISTORY 
(
   CONFIG_ITEM_ID       NUMBER(4)            not null,
   PARAM_CODE           VARCHAR2(120)        not null,
   SEQ                  NUMBER(4)            not null,
   PARAM_NAME           VARCHAR2(120)        not null,
   PARAM_VALUE          VARCHAR2(1000),
   DEFAULT_PARAM_VALUE  VARCHAR2(1000),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR2(2000),
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   OPERATOR_ID          NUMBER(8),
   CHANNEL_ID           NUMBER(4),
   constraint PK_CONFIG_ITEM_PARAM_HISTORY primary key (CONFIG_ITEM_ID, PARAM_CODE, SEQ)
);

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_VALUE                               */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_VALUE 
(
   CONFIG_ITEM_ID       NUMBER(6)            not null,
   PARAM_CODE           VARCHAR2(120)        not null,
   PARAM_VALUE_ID       NUMBER(4)            not null,
   VALUE_MARK           VARCHAR2(20)         not null,
   VALUE                VARCHAR2(60),
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM_PARAM_VALUE primary key (CONFIG_ITEM_ID, PARAM_CODE, PARAM_VALUE_ID)
);

/*==============================================================*/
/* Table: CONTACT_CHANNEL                                       */
/*==============================================================*/
create table CONTACT_CHANNEL 
(
   CONTACT_CHANNEL_ID   NUMBER(2)            not null,
   CHANNEL_TYPE         CHAR(2)              not null,
   CONTACT_CHANNEL_NAME VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_CONTACT_CHANNEL primary key (CONTACT_CHANNEL_ID)
);

/*==============================================================*/
/* Table: CRON_TRIGGER                                          */
/*==============================================================*/
create table CRON_TRIGGER 
(
   TRIGGER_ID           NUMBER(8)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   CRON_EXPRESSION      VARCHAR2(120)        not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   constraint PK_CRON_TRIGGER primary key (TRIGGER_ID)
);

/*==============================================================*/
/* Table: CRON_TRIGGER_HISTORY                                  */
/*==============================================================*/
create table CRON_TRIGGER_HISTORY 
(
   TRIGGER_ID           NUMBER(8)            not null,
   SEQ                  NUMBER(4)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   CRON_EXPRESSION      VARCHAR2(120)        not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_CRON_TRIGGER_HISTORY primary key (TRIGGER_ID, SEQ)
);

/*==============================================================*/
/* Table: DICTIONARY                                            */
/*==============================================================*/
create table DICTIONARY 
(
   DICT_CODE            VARCHAR2(60)         not null,
   DICT_NAME            VARCHAR2(60)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_DICTIONARY primary key (DICT_CODE)
);

comment on table DICTIONARY is
'数据字典表';

INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('CHANNEL_TYPE', '渠道类型', '渠道类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('AREA_TYPE', '区域类型', '区域类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('DATA_TYPE', '数据类型', '数据类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('INPUT_TYPE', '输入方式', '输入方式');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('ATTR_TYPE', '属性类型', '属性类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('TASK_STATE', '任务状态', '任务状态');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('TRIGGER_TYPE', '触发器类型', '触发器类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('MESSAGE_TYPE', '消息类型', '消息类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('OPERATOR_TYPE', '操作员类型', '操作员类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('ACCOUNT_TYPE', '账号类型', '账号类型');
INSERT INTO DICTIONARY (DICT_CODE, DICT_NAME, REMARK) VALUES ('RESOURCE_TYPE', '资源类型', '资源类型');




/*==============================================================*/
/* Table: DICTIONARY_DATA                                       */
/*==============================================================*/
create table DICTIONARY_DATA 
(
   DICT_DATA_ID         NUMBER(6)            not null,
   DICT_CODE            VARCHAR2(60)         not null,
   DICT_DATA_NAME       VARCHAR2(60)         not null,
   DICT_DATA_VALUE      VARCHAR2(8)          not null,
   IS_FIXED             CHAR(1)              not null,
   IS_CANCEL            CHAR(1),
   constraint PK_DICTIONARY_DATA primary key (DICT_DATA_ID)
);

comment on table DICTIONARY_DATA is
'字典数据表';

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (1, 'CHANNEL_TYPE', 'Http消息', '01', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (2, 'CHANNEL_TYPE', 'WebSocket消息', '02', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (3, 'CHANNEL_TYPE', '短信信息', '03', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (4, 'CHANNEL_TYPE', '邮件', '04', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (5, 'CHANNEL_TYPE', 'dubbo协议', '05', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (6, 'CHANNEL_TYPE', 'WebService', '06', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (7, 'CHANNEL_TYPE', '站内消息', '07', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (8, 'AREA_TYPE', '国家', 'A', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (9, 'AREA_TYPE', '省、直辖市', 'P', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (10, 'AREA_TYPE', '市', 'C', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (11, 'AREA_TYPE', '区,县', 'D', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (12, 'AREA_TYPE', '社区', 'O', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (13, 'AREA_TYPE', '小区', 'G', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (14, 'DATA_TYPE', '字符Character', 'C', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (15, 'DATA_TYPE', '整数Number', 'N', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (16, 'DATA_TYPE', '密码Password', 'P', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (17, 'DATA_TYPE', '浮点数Float', 'F', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (18, 'DATA_TYPE', '对象类型', 'O', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (19, 'INPUT_TYPE', '不可编辑', '1', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (20, 'INPUT_TYPE', '单选', '2', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (21, 'INPUT_TYPE', '多选', '3', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (22, 'INPUT_TYPE', '时间选择框', '4', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (23, 'INPUT_TYPE', '对象类型', '5', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (24, 'ATTR_TYPE', '管理员属性', 'A', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (25, 'ATTR_TYPE', '会员属性', 'M', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (26, 'TASK_STATE', '初始状态', 'I', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (27, 'TASK_STATE', '等待状态', 'W', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (28, 'TASK_STATE', '暂停状态', 'P', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (29, 'TASK_STATE', '正常执行', 'A', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (30, 'TASK_STATE', '阻塞状态', 'B', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (31, 'TASK_STATE', '错误状态', 'E', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (32, 'TASK_STATE', '完成状态', 'C', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (33, 'TRIGGER_TYPE', '简单触发器类型', '1', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (34, 'TRIGGER_TYPE', '简单触发器类型', '2', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (35, 'MESSAGE_TYPE', '文本消息', 'T', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (36, 'MESSAGE_TYPE', '图片消息', 'P', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (37, 'MESSAGE_TYPE', '语音消息', 'V', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (38, 'MESSAGE_TYPE', '多媒体消息', 'M', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (39, 'OPERATOR_TYPE', '管理员', 'A', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (40, 'OPERATOR_TYPE', '会员', 'M', 'N', 'N');

INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (41, 'ACCOUNT_TYPE', '平台账号', 'P', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (42, 'ACCOUNT_TYPE', '统一平台账号', 'U', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (43, 'ACCOUNT_TYPE', '统一平台邮箱账号', 'UE', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (44, 'ACCOUNT_TYPE', '统一平台手机账号', 'UM', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (45, 'ACCOUNT_TYPE', '腾讯QQ账号', 'TQ', 'N', 'N');
INSERT INTO DICTIONARY_DATA (DICT_DATA_ID, DICT_CODE, DICT_DATA_NAME, DICT_DATA_VALUE, IS_FIXED, IS_CANCEL) VALUES (46, 'ACCOUNT_TYPE', '腾讯微信账号', 'TW', 'N', 'N');

/*==============================================================*/
/* Table: DIRECTORY                                             */
/*==============================================================*/
create table DIRECTORY 
(
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   DIRECTORY_NAME       VARCHAR2(20)         not null,
   PARENT_DIRECTORY_CODE VARCHAR2(20),
   REMARK               VARCHAR2(64),
   constraint PK_DIRECTORY primary key (DIRECTORY_CODE)
);

comment on column DIRECTORY.DIRECTORY_CODE is
'目录代码';

/*==============================================================*/
/* Table: EVENT                                                 */
/*==============================================================*/
create table EVENT 
(
   EVENT_ID             NUMBER(4)            not null,
   PARAMS_NAME          VARCHAR2(120),
   EVENT_NAME           VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_EVENT primary key (EVENT_ID)
);

/*==============================================================*/
/* Table: MENU                                                  */
/*==============================================================*/
create table MENU 
(
   RESOURCE_ID          NUMBER(6)            not null,
   PARENT_RESOURCE_ID   NUMBER(4),
   SEQ                  NUMBER(4)            not null,
   MENU_NAME            VARCHAR2(20)         not null,
   URL                  VARCHAR2(120)        not null,
   IS_LEAF              CHAR(1)              not null,
   ICON_URL             VARCHAR2(120),
   constraint PK_MENU primary key (RESOURCE_ID)
);

/*==============================================================*/
/* Table: MENU_URL_RESOURCE                                     */
/*==============================================================*/
create table MENU_URL_RESOURCE 
(
   RESOURCE_ID          NUMBER(6),
   URL_RESOURCE_ID      NUMBER(6)
);

comment on table MENU_URL_RESOURCE is
'菜单关联哪些权限';

/*==============================================================*/
/* Table: MESSAGE_ATTACHMENTS                                   */
/*==============================================================*/
create table MESSAGE_ATTACHMENTS 
(
   ATTACHMENTS_ID       NUMBER(10)           not null,
   MESSAGE_ID           NUMBER(10)           not null,
   constraint PK_MESSAGE_ATTACHMENTS primary key (ATTACHMENTS_ID, MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_BOX                                           */
/*==============================================================*/
create table MESSAGE_BOX 
(
   MESSAGE_ID           NUMBER(6)            not null,
   RECEIVERS            CLOB                 not null,
   SENDER               VARCHAR2(120),
   MESSAGE_TEMPLATE_ID  NUMBER(4)            not null,
   SUBJECT              VARCHAR2(120),
   CONTENT              CLOB,
   ATTACHMENTS_NUM      NUMBER(3)            not null,
   CREATE_TIME          DATE                 not null,
   SEND_TIME            DATE,
   NEXT_SEND_TIME       DATE,
   SEND_TIMES           NUMBER(4)            not null,
   EXTEND_ATTRS         CLOB,
   constraint PK_MESSAGE_BOX primary key (MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_HISTORY                                       */
/*==============================================================*/
create table MESSAGE_HISTORY 
(
   MESSAGE_ID           NUMBER(10)           not null,
   RECEIVERS            CLOB                 not null,
   SENDER               VARCHAR2(120),
   MESSAGE_TYPE         CHAR(1)              not null,
   MESSAGE_TEMPLATE_ID  NUMBER(4)            not null,
   SUBJECT              VARCHAR2(120),
   CONTENT              CLOB,
   ATTACHMENTS_NUM      NUMBER(3)            not null,
   CREATE_TIME          DATE                 not null,
   SEND_TIME            DATE,
   SEND_TIMES           NUMBER(4)            not null,
   RESULT               VARCHAR2(255)        not null,
   EXP_DATE             DATE,
   EXTEND_ATTRS         CLOB,
   constraint PK_MESSAGE_HISTORY primary key (MESSAGE_ID)
);

/*==============================================================*/
/* Table: MESSAGE_TEMPLATE                                      */
/*==============================================================*/
create table MESSAGE_TEMPLATE 
(
   MESSAGE_TEMPLATE_ID  NUMBER(6)            not null,
   MESSAGE_TEMPLATE_CODE VARCHAR2(20)         not null,
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   NAME                 VARCHAR2(120)        not null,
   TEMPLATE             CLOB,
   STATE                CHAR(1)              not null,
   CONTACT_CHANNEL_IDS  VARCHAR2(8),
   STATE_TIME           DATE                 not null,
   DELAY                NUMBER(6)            not null,
   RESEND_TIMES         NUMBER(4)            not null,
   SAVE_HISTORY         CHAR(1)              not null,
   SAVE_DAY             NUMBER(4)            not null,
   CREATE_TIME          DATE,
   constraint PK_MESSAGE_TEMPLATE primary key (MESSAGE_TEMPLATE_ID)
);

/*==============================================================*/
/* Table: MODULE                                                */
/*==============================================================*/
create table MODULE 
(
   MODULE_CODE          VARCHAR2(10)         not null,
   PARENT_MODULE_CODE   VARCHAR2(10),
   MODULE_NAME          VARCHAR2(20)         not null,
   constraint PK_MODULE primary key (MODULE_CODE)
);

INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('COMMON',NULL,'公共模块');
INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('PORTAL','COMMON','系统门户');
INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('MONITOR','COMMON','系统监控模块');

/*==============================================================*/
/* Table: OPERATE_LOG                                           */
/*==============================================================*/
create table OPERATE_LOG 
(
   OPERATE_LOG_ID       NUMBER(10)           not null,
   EVENT_ID             NUMBER(4),
   MODULE_CODE          VARCHAR2(10),
   IP                   VARCHAR2(15),
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   PARAMS_VALUE         VARCHAR2(255),
   constraint PK_OPERATE_LOG primary key (OPERATE_LOG_ID)
);

comment on table OPERATE_LOG is
'记录用户的操作';

/*==============================================================*/
/* Table: OPERATOR                                              */
/*==============================================================*/
create table OPERATOR 
(
   OPERATOR_ID          NUMBER(8)            not null,
   OPERATOR_TYPE        CHAR(1)              not null,
   OPERATOR_CODE        NUMBER(10),
   USER_NAME            VARCHAR2(60),
   PASSWORD             VARCHAR2(60),
   CREATE_DATE          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   IS_LOCKED            CHAR(1)              not null,
   PWD_EXP_DATE         DATE,
   LOGIN_FAIL           NUMBER(4)            not null,
   REGIST_IP            VARCHAR2(16),
   LAST_IP              VARCHAR2(16),
   LAST_LOGIN_DATE      DATE,
   constraint PK_OPERATOR primary key (OPERATOR_ID)
);

/*==============================================================*/
/* Table: OPERATOR_HISTORY                                      */
/*==============================================================*/
create table OPERATOR_HISTORY 
(
   OPERATOR_ID          NUMBER(8)            not null,
   SEQ                  NUMBER(8)            not null,
   OPERATOR_TYPE        CHAR(1)              not null,
   OPERATOR_CODE        VARCHAR2(10)         not null,
   USER_NAME            VARCHAR2(60),
   PASSWORD             VARCHAR2(60),
   CREATE_DATE          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   IS_LOCKED            CHAR(1)              not null,
   PWD_EXP_DATE         DATE,
   LOGIN_FAIL            NUMBER(4)           not null,
   REGIST_IP            VARCHAR2(16),
   LAST_IP              VARCHAR2(16),
   LAST_LOGIN_DATE      DATE,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_OPERATOR_HISTORY primary key (OPERATOR_ID, SEQ)
);

/*==============================================================*/
/* Table: OPERATOR_RESOURCE                                     */
/*==============================================================*/
create table OPERATOR_RESOURCE 
(
   OPERATOR_ID          NUMBER(8)            not null,
   RESOURCE_ID          NUMBER(6)            not null,
   RESOURCE_TYPE        NUMBER(1)            not null,
   constraint PK_OPERATOR_RESOURCE primary key (OPERATOR_ID, RESOURCE_ID, RESOURCE_TYPE)
);

/*==============================================================*/
/* Table: OPERATOR_ROLE_HISTORY                                 */
/*==============================================================*/
create table OPERATOR_ROLE_HISTORY 
(
   OPERATOR_ID          NUMBER(8)            not null,
   ROLE_ID              NUMBER(4)            not null,
   SEQ                  NUMBER(4)            not null,
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_OPERATOR_ROLE_HISTORY primary key (OPERATOR_ID, ROLE_ID, SEQ)
);

/*==============================================================*/
/* Table: QRTZ_BLOB_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_BLOB_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   BLOB_DATA            BLOB,
   constraint PK_QRTZ_BLOB_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

comment on table QRTZ_BLOB_TRIGGERS is
'Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)';

/*==============================================================*/
/* Table: QRTZ_CALENDARS                                        */
/*==============================================================*/
create table QRTZ_CALENDARS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   CALENDAR_NAME        VARCHAR2(60)         not null,
   CALENDAR             BLOB                 not null,
   constraint PK_QRTZ_CALENDARS primary key (SCHED_NAME, CALENDAR_NAME)
);

comment on table QRTZ_CALENDARS is
'以 Blob 类型存储 Quartz 的 Calendar 信息';

/*==============================================================*/
/* Table: QRTZ_CRON_TRIGGERS                                    */
/*==============================================================*/
create table QRTZ_CRON_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   CRON_EXPRESSION      VARCHAR2(120)        not null,
   TIME_ZONE_ID         VARCHAR2(80),
   constraint PK_QRTZ_CRON_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

comment on table QRTZ_CRON_TRIGGERS is
'存储 Cron Trigger，包括 Cron 表达式和时区信息';

/*==============================================================*/
/* Table: QRTZ_FIRED_TRIGGERS                                   */
/*==============================================================*/
create table QRTZ_FIRED_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   ENTRY_ID             VARCHAR2(95)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   INSTANCE_NAME        VARCHAR2(60)         not null,
   FIRED_TIME           NUMBER(13)           not null,
   SCHED_TIME           NUMBER(13)           not null,
   PRIORITY             NUMBER(10)           not null,
   STATE                VARCHAR2(16)         not null,
   JOB_NAME             VARCHAR2(200),
   JOB_GROUP            VARCHAR2(200),
   IS_NONCONCURRENT     VARCHAR2(1),
   REQUESTS_RECOVERY    VARCHAR2(1),
   constraint PK_QRTZ_FIRED_TRIGGERS primary key (SCHED_NAME, ENTRY_ID)
);

comment on table QRTZ_FIRED_TRIGGERS is
'存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息';

/*==============================================================*/
/* Table: QRTZ_JOB_DETAILS                                      */
/*==============================================================*/
create table QRTZ_JOB_DETAILS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   JOB_NAME             VARCHAR2(60)         not null,
   JOB_GROUP            VARCHAR2(60)         not null,
   DESCRIPTION          VARCHAR2(250),
   JOB_CLASS_NAME       VARCHAR2(250)        not null,
   IS_DURABLE           CHAR(1)              not null,
   IS_NONCONCURRENT     CHAR(1)              not null,
   IS_UPDATE_DATA       CHAR(1)              not null,
   REQUESTS_RECOVERY    CHAR(1)              not null,
   JOB_DATA             BLOB,
   constraint PK_QRTZ_JOB_DETAILS primary key (SCHED_NAME, JOB_NAME, JOB_GROUP)
);

comment on table QRTZ_JOB_DETAILS is
'存储每一个已配置的 Job 的详细信息';

/*==============================================================*/
/* Table: QRTZ_LOCKS                                            */
/*==============================================================*/
create table QRTZ_LOCKS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   LOCK_NAME            VARCHAR2(40)         not null,
   constraint PK_QRTZ_LOCKS primary key (SCHED_NAME, LOCK_NAME)
);

comment on table QRTZ_LOCKS is
'存储程序的非观锁的信息(假如使用了悲观锁)';

/*==============================================================*/
/* Table: QRTZ_PAUSED_TRIGGER_GRPS                              */
/*==============================================================*/
create table QRTZ_PAUSED_TRIGGER_GRPS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   constraint PK_QRTZ_PAUSED_TRIGGER_GRPS primary key (SCHED_NAME, TRIGGER_GROUP)
);

comment on table QRTZ_PAUSED_TRIGGER_GRPS is
'存储已暂停的 Trigger 组的信息';

/*==============================================================*/
/* Table: QRTZ_SCHEDULER_STATE                                  */
/*==============================================================*/
create table QRTZ_SCHEDULER_STATE 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   INSTANCE_NAME        VARCHAR2(60)         not null,
   LAST_CHECKIN_TIME    NUMBER(13)           not null,
   CHECKIN_INTERVAL     NUMBER(13)           not null,
   constraint PK_QRTZ_SCHEDULER_STATE primary key (SCHED_NAME, INSTANCE_NAME)
);

comment on table QRTZ_SCHEDULER_STATE is
'存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)';

/*==============================================================*/
/* Table: QRTZ_SIMPLE_TRIGGERS                                  */
/*==============================================================*/
create table QRTZ_SIMPLE_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   REPEAT_COUNT         NUMBER(7)            not null,
   REPEAT_INTERVAL      NUMBER(12)           not null,
   TIMES_TRIGGERED      NUMBER(10)           not null,
   constraint PK_QRTZ_SIMPLE_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

comment on table QRTZ_SIMPLE_TRIGGERS is
' 存储简单的 Trigger，包括重复次数，间隔，以及已触的次数';

/*==============================================================*/
/* Table: QRTZ_SIMPROP_TRIGGERS                                 */
/*==============================================================*/
create table QRTZ_SIMPROP_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   STR_PROP_1           VARCHAR2(512),
   STR_PROP_2           VARCHAR2(512),
   STR_PROP_3           VARCHAR2(512),
   INT_PROP_1           NUMBER(10),
   INT_PROP_2           NUMBER(10),
   LONG_PROP_1          NUMBER(10),
   LONG_PROP_2          NUMBER(10),
   DEC_PROP_1           NUMBER(13,4),
   DEC_PROP_2           NUMBER(13,4),
   BOOL_PROP_1          VARCHAR2(1),
   BOOL_PROP_2          VARCHAR2(1),
   constraint PK_QRTZ_SIMPROP_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

/*==============================================================*/
/* Table: QRTZ_TRIGGERS                                         */
/*==============================================================*/
create table QRTZ_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   JOB_NAME             VARCHAR2(60)         not null,
   JOB_GROUP            VARCHAR2(60)         not null,
   DESCRIPTION          VARCHAR2(250),
   NEXT_FIRE_TIME       NUMBER(13),
   PREV_FIRE_TIME       NUMBER(13),
   PRIORITY             NUMBER(10),
   TRIGGER_STATE        VARCHAR2(16)         not null,
   TRIGGER_TYPE         VARCHAR2(8)          not null,
   START_TIME           NUMBER(13)           not null,
   END_TIME             NUMBER(10),
   CALENDAR_NAME        VARCHAR2(200),
   MISFIRE_INSTR        SMALLINT,
   JOB_DATA             BLOB,
   constraint PK_QRTZ_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
);

comment on table QRTZ_TRIGGERS is
'存储已配置的 Trigger 的信息';

/*==============================================================*/
/* Table: RESOURCES                                             */
/*==============================================================*/
create table RESOURCES 
(
   RESOURCE_ID          NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10),
   RESOURCE_TYPE        CHAR(1)              not null,
   constraint PK_RESOURCES primary key (RESOURCE_ID)
);

comment on table RESOURCES is
'资源表';

/*==============================================================*/
/* Table: ROLE                                                  */
/*==============================================================*/
create table ROLE 
(
   ROLE_ID              NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   ROLE_NAME            VARCHAR2(60)         not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   constraint PK_ROLE primary key (ROLE_ID)
);

/*==============================================================*/
/* Table: ROLE_HISTORY                                          */
/*==============================================================*/
create table ROLE_HISTORY 
(
   ROLE_ID              NUMBER(4)            not null,
   SEQ                  NUMBER(4)            not null,
   ROLE_NAME            VARCHAR2(60)         not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_ROLE_HISTORY primary key (ROLE_ID, SEQ)
);

/*==============================================================*/
/* Table: ROLE_RESOURCE                                         */
/*==============================================================*/
create table ROLE_RESOURCE 
(
   ROLE_ID              NUMBER(4)            not null,
   RESOURCE_ID          NUMBER(6)            not null,
   RESOURCE_TYPE        NUMBER(1)            not null,
   constraint PK_ROLE_RESOURCE primary key (ROLE_ID, RESOURCE_ID, RESOURCE_TYPE)
);

/*==============================================================*/
/* Table: SEND_RECORD                                           */
/*==============================================================*/
create table SEND_RECORD 
(
   SEND_RECORD_ID       NUMBER(10)           not null,
   MESSAGE_ID           NUMBER(10)           not null,
   CONTACT_CHANNEL_ID   NUMBER(2)            not null,
   SEND_TIME            DATE                 not null,
   RESULT               VARCHAR2(255)        not null,
   constraint PK_SEND_RECORD primary key (SEND_RECORD_ID)
);

/*==============================================================*/
/* Table: SIMPLE_TRIGGER                                        */
/*==============================================================*/
create table SIMPLE_TRIGGER 
(
   TRIGGER_ID           NUMBER(8)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE,
   TIMES                NUMBER(4),
   EXECUTE_INTERVAL     NUMBER(4)            not null,
   INTERVAL_UNIT        CHAR(1)              not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   constraint PK_SIMPLE_TRIGGER primary key (TRIGGER_ID)
);

/*==============================================================*/
/* Table: SIMPLE_TRIGGER_HISTORY                                */
/*==============================================================*/
create table SIMPLE_TRIGGER_HISTORY 
(
   TRIGGER_ID           NUMBER(8)            not null,
   SEQ                  NUMBER(4)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE,
   TIMES                NUMBER(4),
   EXECUTE_INTERVAL     NUMBER(4)            not null,
   INTERVAL_UNIT        CHAR(1)              not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          NUMBER(8),
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_SIMPLE_TRIGGER_HISTORY primary key (TRIGGER_ID, SEQ)
);

/*==============================================================*/
/* Table: TASK                                                  */
/*==============================================================*/
create table TASK 
(
   TASK_ID              NUMBER(8)            not null,
   TASK_NAME            VARCHAR2(60)         not null,
   CLASS_NAME           VARCHAR2(60)         not null,
   METHOD               VARCHAR2(20)         not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   PRIORITY             NUMBER(1)            not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1)              not null,
   LAST_EXECUTE_TIME    DATE,
   NEXT_EXCUTE_DATE     DATE,
   OPERATOR_ID          NUMBER(8),
   CREATE_TIME          DATE                 not null,
   constraint PK_TASK primary key (TASK_ID),
   constraint AK_KEY_2_TASK unique (TASK_NAME)
);

/*==============================================================*/
/* Table: TASK_HISTORY                                          */
/*==============================================================*/
create table TASK_HISTORY 
(
   TASK_ID              NUMBER(8)            not null,
   SEQ                  NUMBER(8)            not null,
   TASK_NAME            VARCHAR2(60)         not null,
   CLASS_NAME           VARCHAR2(60)         not null,
   METHOD               VARCHAR2(20)         not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   PRIORITY             NUMBER(1)            not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1)              not null,
   LAST_EXECUTE_TIME    DATE,
   NEXT_EXCUTE_DATE     DATE,
   OPERATOR_ID          NUMBER(8),
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   NUMBER(8),
   constraint PK_TASK_HISTORY primary key (TASK_ID, SEQ)
);

/*==============================================================*/
/* Table: TASK_TRIGGER                                          */
/*==============================================================*/
create table TASK_TRIGGER 
(
   TASK_ID              NUMBER(8)            not null,
   TRIGGER_TYPE         CHAR(1)              not null,
   TRIGGER_ID           NUMBER(8)            not null,
   constraint PK_TASK_TRIGGER primary key (TASK_ID, TRIGGER_TYPE, TRIGGER_ID)
);

/*==============================================================*/
/* Table: TRANS_LOG                                             */
/*==============================================================*/
create table TRANS_LOG 
(
   TRANS_ID             VARCHAR2(36)         not null,
   MODULE_CODE          VARCHAR2(10),
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE                 not null,
   CONSUME_TIME         NUMBER(8)            not null,
   INPUT_PARAM          CLOB,
   OUTPUT_PARAM         CLOB,
   SQL_LOG              CLOB,
   EXCEPTION_LOG        CLOB,
   CONTACT_CHANNEL_ID   NUMBER(2),
   constraint PK_TRANS_LOG primary key (TRANS_ID)
);

/*==============================================================*/
/* Table: TRANS_LOG_STACK                                       */
/*==============================================================*/
create table TRANS_LOG_STACK 
(
   STACK_ID             VARCHAR2(36)         not null,
   SEQ                  NUMBER(4)            not null,
   TRANS_ID             VARCHAR2(36)         not null,
   PARENT_STACK_ID      VARCHAR2(36),
   METHOD               CLOB                 not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE                 not null,
   CONSUME_TIME         NUMBER(8)            not null,
   INPUT_PARAM          CLOB,
   OUTPUT_PARAM         CLOB,
   IS_SUCCESS           CHAR(1)              not null,
   constraint PK_TRANS_LOG_STACK primary key (STACK_ID)
);

/*==============================================================*/
/* Table: URL_RESOURCE                                          */
/*==============================================================*/
create table URL_RESOURCE 
(
   RESOURCE_ID          NUMBER(6)            not null,
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   RESOURCE_NAME        VARCHAR2(60)         not null,
   URL                  VARCHAR2(120)        not null,
   EVENT_ID             NUMBER(4)            not null,
   REMARK               VARCHAR2(255),
   constraint PK_URL_RESOURCE primary key (RESOURCE_ID)
);

alter table ACCOUNT
   add constraint FK_ACCOUNT_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID);

alter table ADMIN
   add constraint FK_ADMIN_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID);

alter table ADMIN_ATTR
   add constraint FK_ADMIN_AT_ADMIN_ID__ADMIN foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID);

alter table ADMIN_ATTR
   add constraint FK_ADMIN_AT_ATTR_ID_A_ATTR foreign key (ATTR_ID)
      references ATTR (ATTR_ID);

alter table ADMIN_ROLE
   add constraint FK_ADMIN_RO_ADMIN_ID__ADMIN foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID);

alter table ADMIN_ROLE
   add constraint FK_ADMIN_RO_ROLE_ID_A_ROLE foreign key (ROLE_ID)
      references ROLE (ROLE_ID);

alter table AREA
   add constraint FK_AREA_AREA_ID_A_AREA foreign key (PARENT_AREA_ID)
      references AREA (AREA_ID);

alter table AREA_RANGE
   add constraint FK_AREA_RAN_ARRE foreign key (AREA_ID)
      references AREA (AREA_ID);

alter table ATTR_VALUE
   add constraint FK_ATTR_VAL_ATTR_ID_A_ATTR foreign key (ATTR_ID)
      references ATTR (ATTR_ID);

alter table ATTR_VALUE
   add constraint FK_ATTR_VAL_ATTR_ID_A_LINK foreign key (LINK_ATTR_ID)
      references ATTR (ATTR_ID);

alter table CONFIG_ITEM
   add constraint FK_CONFIG_I_DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE);

alter table CONFIG_ITEM
   add constraint FK_CONFIG_I_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table CONFIG_ITEM_PARAM
   add constraint FK_CONFIG_I_CONFIG_IT_CONFIG_I foreign key (CONFIG_ITEM_ID)
      references CONFIG_ITEM (CONFIG_ITEM_ID);

alter table CONFIG_ITEM_PARAM_VALUE
   add constraint FK_CONFIG_I_CONFIG_IT_CONFIG_V foreign key (CONFIG_ITEM_ID, PARAM_CODE)
      references CONFIG_ITEM_PARAM (CONFIG_ITEM_ID, PARAM_CODE);

alter table DICTIONARY_DATA
   add constraint FK_DICTIONA_FK_DICTIO_DICTIONA foreign key (DICT_CODE)
      references DICTIONARY (DICT_CODE);

alter table DIRECTORY
   add constraint FK_DIRECTOR_DIRECTORY_DIRECTOR foreign key (PARENT_DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE);

alter table MENU
   add constraint FK_MENU_FK_MENU_R_RESOURCE foreign key (RESOURCE_ID)
      references RESOURCES (RESOURCE_ID);

alter table MENU
   add constraint FK_MENU_FK_RESOUR_MENU foreign key (PARENT_RESOURCE_ID)
      references MENU (RESOURCE_ID);

alter table MENU_URL_RESOURCE
   add constraint FK_MENU_URL_FK_MENU_U_MENU foreign key (RESOURCE_ID)
      references MENU (RESOURCE_ID);

alter table MENU_URL_RESOURCE
   add constraint FK_MENU_URL_FK_MENU_U_URL_RESO foreign key (URL_RESOURCE_ID)
      references URL_RESOURCE (RESOURCE_ID);

alter table MESSAGE_ATTACHMENTS
   add constraint FK_MESSAGE__ATTACHMEN_ATTACHME foreign key (ATTACHMENTS_ID)
      references ATTACHMENTS (ATTACHMENTS_ID);

alter table MESSAGE_BOX
   add constraint FK_MESSAGE__MESSAGE_T_MESSAGE_ foreign key (MESSAGE_TEMPLATE_ID)
      references MESSAGE_TEMPLATE (MESSAGE_TEMPLATE_ID);

alter table MESSAGE_TEMPLATE
   add constraint FK_MESSAGE__DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE);

alter table MODULE
   add constraint FK_MODULE_MODULE_CO_MODULE foreign key (PARENT_MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table OPERATE_LOG
   add constraint FK_OPERATE__FK_OPERAT_EVENT foreign key (EVENT_ID)
      references EVENT (EVENT_ID);

alter table OPERATE_LOG
   add constraint FK_OPERATE__FK_OPERAT_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table OPERATOR_RESOURCE
   add constraint FK_OPERATOR_FK_OPERAT_RESOURCE foreign key (RESOURCE_ID)
      references RESOURCES (RESOURCE_ID);

alter table OPERATOR_RESOURCE
   add constraint FK_OPERATOR_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID);

alter table QRTZ_BLOB_TRIGGERS
   add constraint FK_QRTZ_BLO_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_CRON_TRIGGERS
   add constraint FK_QRTZ_CRO_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPLE_TRIGGERS
   add constraint FK_QRTZ_SIM_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_SIMPROP_TRIGGERS
   add constraint FK_QRTZ_SIM_REFERENCE_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP);

alter table QRTZ_TRIGGERS
   add constraint FK_QRTZ_TRI_QRTZ_JOB foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP)
      references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP);

alter table RESOURCES
   add constraint FK_RESOURCE_FK_RESOUR_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table ROLE
   add constraint FK_ROLE_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table ROLE_RESOURCE
   add constraint FK_ROLE_RES_FK_ROLE_R_RESOURCE foreign key (RESOURCE_ID)
      references RESOURCES (RESOURCE_ID);

alter table ROLE_RESOURCE
   add constraint FK_ROLE_RES_ROLE_ID_R_ROLE foreign key (ROLE_ID)
      references ROLE (ROLE_ID);

alter table SEND_RECORD
   add constraint FK_SEND_REC_CONTACT_C_CONTACT_ foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID);

alter table TASK
   add constraint FK_TASK_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table TASK_TRIGGER
   add constraint FK_TASK_TRI_TASK_ID_T_TASK foreign key (TASK_ID)
      references TASK (TASK_ID);

alter table TRANS_LOG
   add constraint FK_TRANS_LO_CONTACT_C_CONTACT_ foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID);

alter table TRANS_LOG
   add constraint FK_TRANS_LO_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE);

alter table TRANS_LOG_STACK
   add constraint FK_TRANS_LO_FK_TRANS__TRANS_LO foreign key (TRANS_ID)
      references TRANS_LOG (TRANS_ID);

alter table URL_RESOURCE
   add constraint FK_URL_RESO_DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE);

alter table URL_RESOURCE
   add constraint FK_URL_RESO_FK_URL_RE_RESOURCE foreign key (RESOURCE_ID)
      references RESOURCES (RESOURCE_ID);

