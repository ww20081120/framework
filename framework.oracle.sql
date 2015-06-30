/*==============================================================*/
/* DBMS name:      ORACLE Version 11g                           */
/* Created on:     2015-06-30 16:49:48                          */
/*==============================================================*/



-- Type package declaration
create or replace package PDTypes  
as
    TYPE ref_cursor IS REF CURSOR;
end;

-- Integrity package declaration
create or replace package IntegrityPackage AS
 procedure InitNestLevel;
 function GetNestLevel return number;
 procedure NextNestLevel;
 procedure PreviousNestLevel;
 end IntegrityPackage;
/

-- Integrity package definition
create or replace package body IntegrityPackage AS
 NestLevel number;

-- Procedure to initialize the trigger nest level
 procedure InitNestLevel is
 begin
 NestLevel := 0;
 end;


-- Function to return the trigger nest level
 function GetNestLevel return number is
 begin
 if NestLevel is null then
     NestLevel := 0;
 end if;
 return(NestLevel);
 end;

-- Procedure to increase the trigger nest level
 procedure NextNestLevel is
 begin
 if NestLevel is null then
     NestLevel := 0;
 end if;
 NestLevel := NestLevel + 1;
 end;

-- Procedure to decrease the trigger nest level
 procedure PreviousNestLevel is
 begin
 NestLevel := NestLevel - 1;
 end;

 end IntegrityPackage;
/


drop trigger "CompoundDeleteTrigger_account"
/

drop trigger "CompoundInsertTrigger_account"
/

drop trigger "CompoundUpdateTrigger_account"
/

drop trigger "tib_account"
/

drop trigger "CompoundDeleteTrigger_admin"
/

drop trigger "CompoundInsertTrigger_admin"
/

drop trigger "CompoundUpdateTrigger_admin"
/

drop trigger "tib_admin"
/

drop trigger "CompoundDeleteTrigger_area"
/

drop trigger "CompoundInsertTrigger_area"
/

drop trigger "CompoundUpdateTrigger_area"
/

drop trigger "tib_area"
/

drop trigger "CompoundDeleteTrigger_attachme"
/

drop trigger "CompoundInsertTrigger_attachme"
/

drop trigger "CompoundUpdateTrigger_attachme"
/

drop trigger "tib_attachments"
/

drop trigger "CompoundDeleteTrigger_attr"
/

drop trigger "CompoundInsertTrigger_attr"
/

drop trigger "CompoundUpdateTrigger_attr"
/

drop trigger "tib_attr"
/

drop trigger "CompoundDeleteTrigger_attr_val"
/

drop trigger "CompoundInsertTrigger_attr_val"
/

drop trigger "CompoundUpdateTrigger_attr_val"
/

drop trigger "tib_attr_value"
/

drop trigger "CompoundDeleteTrigger_config_i"
/

drop trigger "CompoundInsertTrigger_config_i"
/

drop trigger "CompoundUpdateTrigger_config_i"
/

drop trigger "tib_config_item"
/

drop trigger "CompoundDeleteTrigger_config_i"
/

drop trigger "CompoundInsertTrigger_config_i"
/

drop trigger "CompoundUpdateTrigger_config_i"
/

drop trigger "tib_config_item_param"
/

drop trigger "CompoundDeleteTrigger_config_i"
/

drop trigger "CompoundInsertTrigger_config_i"
/

drop trigger "CompoundUpdateTrigger_config_i"
/

drop trigger "tib_config_item_param_value"
/

drop trigger "CompoundDeleteTrigger_cron_tri"
/

drop trigger "CompoundInsertTrigger_cron_tri"
/

drop trigger "CompoundUpdateTrigger_cron_tri"
/

drop trigger "tib_cron_trigger"
/

drop trigger "CompoundDeleteTrigger_menu"
/

drop trigger "CompoundInsertTrigger_menu"
/

drop trigger "CompoundUpdateTrigger_menu"
/

drop trigger "tib_menu"
/

drop trigger "CompoundDeleteTrigger_message_"
/

drop trigger "CompoundInsertTrigger_message_"
/

drop trigger "CompoundUpdateTrigger_message_"
/

drop trigger "tib_message_box"
/

drop trigger "CompoundDeleteTrigger_message_"
/

drop trigger "CompoundInsertTrigger_message_"
/

drop trigger "CompoundUpdateTrigger_message_"
/

drop trigger "tib_message_history"
/

drop trigger "CompoundDeleteTrigger_message_"
/

drop trigger "CompoundInsertTrigger_message_"
/

drop trigger "CompoundUpdateTrigger_message_"
/

drop trigger "tib_message_template"
/

drop trigger "CompoundDeleteTrigger_operator"
/

drop trigger "CompoundInsertTrigger_operator"
/

drop trigger "CompoundUpdateTrigger_operator"
/

drop trigger "tib_operator"
/

drop trigger "CompoundDeleteTrigger_role"
/

drop trigger "CompoundInsertTrigger_role"
/

drop trigger "CompoundUpdateTrigger_role"
/

drop trigger "tib_role"
/

drop trigger "CompoundDeleteTrigger_send_rec"
/

drop trigger "CompoundInsertTrigger_send_rec"
/

drop trigger "CompoundUpdateTrigger_send_rec"
/

drop trigger "tib_send_record"
/

drop trigger "CompoundDeleteTrigger_simple_t"
/

drop trigger "CompoundInsertTrigger_simple_t"
/

drop trigger "CompoundUpdateTrigger_simple_t"
/

drop trigger "tib_simple_trigger"
/

drop trigger "CompoundDeleteTrigger_task"
/

drop trigger "CompoundInsertTrigger_task"
/

drop trigger "CompoundUpdateTrigger_task"
/

drop trigger "tib_task"
/

drop trigger "CompoundDeleteTrigger_url_reso"
/

drop trigger "CompoundInsertTrigger_url_reso"
/

drop trigger "CompoundUpdateTrigger_url_reso"
/

drop trigger "tib_url_resource"
/

alter table ACCOUNT
   drop constraint FK_ACCOUNT_ACCOUNT_T_ACCOUNT_
/

alter table ACCOUNT
   drop constraint FK_ACCOUNT_OPERATOR__OPERATOR
/

alter table ADMIN
   drop constraint FK_ADMIN_OPERATOR__OPERATOR
/

alter table ADMIN_ATTR
   drop constraint FK_ADMIN_AT_ADMIN_ID__ADMIN
/

alter table ADMIN_ATTR
   drop constraint FK_ADMIN_AT_ATTR_ID_A_ATTR
/

alter table ADMIN_ROLE
   drop constraint FK_ADMIN_RO_ADMIN_ID__ADMIN
/

alter table ADMIN_ROLE
   drop constraint FK_ADMIN_RO_ROLE_ID_A_ROLE
/

alter table AREA
   drop constraint FK_AREA_AREA_ID_A_AREA
/

alter table AREA
   drop constraint FK_AREA_AREA_TYPE_AREA_TYP
/

alter table AREA_RANGE
   drop constraint FK_AREA_RAN_AREA_AREA_AREA
/

alter table ATTR
   drop constraint FK_ATTR_ATTR_TYPE_ATTR_TYP
/

alter table ATTR
   drop constraint FK_ATTR_DATA_TYPE_DATA_TYP
/

alter table ATTR
   drop constraint FK_ATTR_INPUT_TYP_INPUT_TY
/

alter table ATTR_VALUE
   drop constraint FK_ATTR_VAL_ATTR_ID_A_ATTR
/

alter table ATTR_VALUE
   drop constraint FK_ATTR_VAL_ATTR_ID_A_ATTR
/

alter table CONFIG_ITEM
   drop constraint FK_CONFIG_I_DIRECTORY_DIRECTOR
/

alter table CONFIG_ITEM
   drop constraint FK_CONFIG_I_MODULE_CO_MODULE
/

alter table CONFIG_ITEM_PARAM
   drop constraint FK_CONFIG_I_CONFIG_IT_CONFIG_I
/

alter table CONFIG_ITEM_PARAM
   drop constraint FK_CONFIG_I_DATA_TYPE_DATA_TYP
/

alter table CONFIG_ITEM_PARAM
   drop constraint FK_CONFIG_I_INPUT_TYP_INPUT_TY
/

alter table CONFIG_ITEM_PARAM_VALUE
   drop constraint FK_CONFIG_I_CONFIG_IT_CONFIG_I
/

alter table CONTACT_CHANNEL
   drop constraint FK_CONTACT__CHANNEL_T_CHANNEL_
/

alter table DIRECTORY
   drop constraint FK_DIRECTOR_DIRECTORY_DIRECTOR
/

alter table MENU
   drop constraint FK_MENU_MENU_MENU_MENU
/

alter table MENU
   drop constraint FK_MENU_MODULE_ME_MODULE
/

alter table MENU
   drop constraint FK_MENU_RESOURCE__URL_RESO
/

alter table MESSAGE_ATTACHMENTS
   drop constraint FK_MESSAGE__ATTACHMEN_ATTACHME
/

alter table MESSAGE_BOX
   drop constraint FK_MESSAGE__MESSAGE_T_MESSAGE_
/

alter table MESSAGE_BOX
   drop constraint FK_MESSAGE__MESSAGE_T_MESSAGE_
/

alter table MESSAGE_TEMPLATE
   drop constraint FK_MESSAGE__DIRECTORY_DIRECTOR
/

alter table MODULE
   drop constraint FK_MODULE_MODULE_CO_MODULE
/

alter table OPERATOR
   drop constraint FK_OPERATOR_OPERATOR__OPERATOR
/

alter table OPERATOR_RESOURCE
   drop constraint FK_OPERATOR_OPERATOR__OPERATOR
/

alter table OPERATOR_RESOURCE
   drop constraint FK_OPERATOR_RESOURCE__RESOURCE
/

alter table QRTZ_BLOB_TRIGGERS
   drop constraint FK_QRTZ_BLO_REFERENCE_QRTZ_TRI
/

alter table QRTZ_CRON_TRIGGERS
   drop constraint FK_QRTZ_CRO_REFERENCE_QRTZ_TRI
/

alter table QRTZ_SIMPLE_TRIGGERS
   drop constraint FK_QRTZ_SIM_REFERENCE_QRTZ_TRI
/

alter table QRTZ_SIMPROP_TRIGGERS
   drop constraint FK_QRTZ_SIM_REFERENCE_QRTZ_TRI
/

alter table QRTZ_TRIGGERS
   drop constraint FK_QRTZ_TRI_REFERENCE_QRTZ_JOB
/

alter table ROLE
   drop constraint FK_ROLE_MODULE_CO_MODULE
/

alter table ROLE_RESOURCE
   drop constraint FK_ROLE_RES_RESOURCE__RESOURCE
/

alter table ROLE_RESOURCE
   drop constraint FK_ROLE_RES_ROLE_ID_R_ROLE
/

alter table SEND_RECORD
   drop constraint FK_SEND_REC_CONTACT_C_CONTACT_
/

alter table TASK
   drop constraint FK_TASK_MODULE_CO_MODULE
/

alter table TASK
   drop constraint FK_TASK_TASK_STAT_TASK_STA
/

alter table TASK_TRIGGER
   drop constraint FK_TASK_TRI_TASK_ID_T_TASK
/

alter table TASK_TRIGGER
   drop constraint FK_TASK_TRI_TRIGGER_T_TRIGGER_
/

alter table TRANS_LOG
   drop constraint FK_TRANS_LO_CONTACT_C_CONTACT_
/

alter table TRANS_LOG
   drop constraint FK_TRANS_LO_MODULE_CO_MODULE
/

alter table URL_RESOURCE
   drop constraint FK_URL_RESO_DIRECTORY_DIRECTOR
/

alter table URL_RESOURCE
   drop constraint FK_URL_RESO_MODULE_CO_MODULE
/

drop table ACCOUNT cascade constraints
/

drop table ACCOUNT_TYPE cascade constraints
/

drop table ADMIN cascade constraints
/

drop table ADMIN_ATTR cascade constraints
/

drop table ADMIN_ATTR_HISTORY cascade constraints
/

drop table ADMIN_HISTORY cascade constraints
/

drop table ADMIN_ROLE cascade constraints
/

drop table AREA cascade constraints
/

drop table AREA_RANGE cascade constraints
/

drop table AREA_TYPE cascade constraints
/

drop table ATTACHMENTS cascade constraints
/

drop table ATTR cascade constraints
/

drop table ATTR_TYPE cascade constraints
/

drop table ATTR_VALUE cascade constraints
/

drop table CHANNEL_TYPE cascade constraints
/

drop table CONFIG_ITEM cascade constraints
/

drop table CONFIG_ITEM_HISTORY cascade constraints
/

drop table CONFIG_ITEM_PARAM cascade constraints
/

drop table CONFIG_ITEM_PARAM_HISTORY cascade constraints
/

drop table CONFIG_ITEM_PARAM_VALUE cascade constraints
/

drop table CONTACT_CHANNEL cascade constraints
/

drop table CRON_TRIGGER cascade constraints
/

drop table CRON_TRIGGER_HISTORY cascade constraints
/

drop table DATA_TYPE cascade constraints
/

drop table DIRECTORY cascade constraints
/

drop table INPUT_TYPE cascade constraints
/

drop table MENU cascade constraints
/

drop table MESSAGE_ATTACHMENTS cascade constraints
/

drop table MESSAGE_BOX cascade constraints
/

drop table MESSAGE_HISTORY cascade constraints
/

drop table MESSAGE_TEMPLATE cascade constraints
/

drop table MESSAGE_TYPE cascade constraints
/

drop table MODULE cascade constraints
/

drop table OPERATOR cascade constraints
/

drop table OPERATOR_HISTORY cascade constraints
/

drop table OPERATOR_RESOURCE cascade constraints
/

drop table OPERATOR_ROLE_HISTORY cascade constraints
/

drop table OPERATOR_TYPE cascade constraints
/

drop table QRTZ_BLOB_TRIGGERS cascade constraints
/

drop table QRTZ_CALENDARS cascade constraints
/

drop table QRTZ_CRON_TRIGGERS cascade constraints
/

drop table QRTZ_FIRED_TRIGGERS cascade constraints
/

drop table QRTZ_JOB_DETAILS cascade constraints
/

drop table QRTZ_LOCKS cascade constraints
/

drop table QRTZ_PAUSED_TRIGGER_GRPS cascade constraints
/

drop table QRTZ_SCHEDULER_STATE cascade constraints
/

drop table QRTZ_SIMPLE_TRIGGERS cascade constraints
/

drop table QRTZ_SIMPROP_TRIGGERS cascade constraints
/

drop table QRTZ_TRIGGERS cascade constraints
/

drop table RESOURCE_TYPE cascade constraints
/

drop table ROLE cascade constraints
/

drop table ROLE_HISTORY cascade constraints
/

drop table ROLE_RESOURCE cascade constraints
/

drop table SEND_RECORD cascade constraints
/

drop table SIMPLE_TRIGGER cascade constraints
/

drop table SIMPLE_TRIGGER_HISTORY cascade constraints
/

drop table TASK cascade constraints
/

drop table TASK_HISTORY cascade constraints
/

drop table TASK_STATE cascade constraints
/

drop table TASK_TRIGGER cascade constraints
/

drop table TRANS_LOG cascade constraints
/

drop table TRANS_LOG_STACK cascade constraints
/

drop table TRIGGER_TYPE cascade constraints
/

drop table URL_RESOURCE cascade constraints
/

drop sequence S_ACCOUNT
/

drop sequence S_ADMIN
/

drop sequence S_AREA
/

drop sequence S_ATTACHMENTS
/

drop sequence S_ATTR
/

drop sequence S_ATTR_VALUE
/

drop sequence S_CONFIG_ITEM
/

drop sequence S_CONFIG_ITEM_PARAM
/

drop sequence S_CONFIG_ITEM_PARAM_VALUE
/

drop sequence S_CRON_TRIGGER
/

drop sequence S_MENU
/

drop sequence S_MESSAGE_BOX
/

drop sequence S_MESSAGE_HISTORY
/

drop sequence S_MESSAGE_TEMPLATE
/

drop sequence S_OPERATOR
/

drop sequence S_ROLE
/

drop sequence S_SEND_RECORD
/

drop sequence S_SIMPLE_TRIGGER
/

drop sequence S_TASK
/

drop sequence S_URL_RESOURCE
/

create sequence S_ACCOUNT
/

create sequence S_ADMIN
/

create sequence S_AREA
/

create sequence S_ATTACHMENTS
/

create sequence S_ATTR
/

create sequence S_ATTR_VALUE
/

create sequence S_CONFIG_ITEM
/

create sequence S_CONFIG_ITEM_PARAM
/

create sequence S_CONFIG_ITEM_PARAM_VALUE
/

create sequence S_CRON_TRIGGER
/

create sequence S_MENU
/

create sequence S_MESSAGE_BOX
/

create sequence S_MESSAGE_HISTORY
/

create sequence S_MESSAGE_TEMPLATE
/

create sequence S_OPERATOR
/

create sequence S_ROLE
/

create sequence S_SEND_RECORD
/

create sequence S_SIMPLE_TRIGGER
/

create sequence S_TASK
/

create sequence S_URL_RESOURCE
/

/*==============================================================*/
/* Table: ACCOUNT                                               */
/*==============================================================*/
create table ACCOUNT 
(
   ACCOUNT_ID           NUMBER(6)            not null,
   ACCOUNT_TYPE         CHAR(1)              not null,
   ACCOUNT_VALUE        VARCHAR2(120)        not null,
   OPERATOR_ID          INTEGER              not null,
   CREATE_TIME          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_TIME           DATE                 not null,
   EXT1                 VARCHAR2(120),
   EXT2                 VARCHAR2(120),
   constraint PK_ACCOUNT primary key (ACCOUNT_ID)
)
/

/*==============================================================*/
/* Table: ACCOUNT_TYPE                                          */
/*==============================================================*/
create table ACCOUNT_TYPE 
(
   ACCOUNT_TYPE         CHAR(1)              not null,
   ACCOUNT_TYPE_NAME    VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_ACCOUNT_TYPE primary key (ACCOUNT_TYPE)
)
/

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
   ADMIN_ID             NUMBER(6)            not null,
   ADMIN_NAME           VARCHAR2(60)         not null,
   CREATED_TIME         DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   EMAIL                VARCHAR2(120),
   PHONE                VARCHAR2(20),
   OPERATOR_ID          INTEGER,
   ADDRESS              VARCHAR2(255),
   constraint PK_ADMIN primary key (ADMIN_ID)
)
/

/*==============================================================*/
/* Table: ADMIN_ATTR                                            */
/*==============================================================*/
create table ADMIN_ATTR 
(
   ADMIN_ID             INTEGER              not null,
   ATTR_ID              INTEGER              not null,
   VALUE                VARCHAR2(120),
   CREATE_TIME          DATE                 not null,
   constraint PK_ADMIN_ATTR primary key (ADMIN_ID, ATTR_ID)
)
/

/*==============================================================*/
/* Table: ADMIN_ATTR_HISTORY                                    */
/*==============================================================*/
create table ADMIN_ATTR_HISTORY 
(
   ADMIN_ID             INTEGER              not null,
   ATTR_ID              INTEGER              not null,
   SEQ                  INTEGER              not null,
   VALUE                VARCHAR2(120),
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPRATOR_ID    INTEGER,
   constraint PK_ADMIN_ATTR_HISTORY primary key (ADMIN_ID, ATTR_ID, SEQ)
)
/

/*==============================================================*/
/* Table: ADMIN_HISTORY                                         */
/*==============================================================*/
create table ADMIN_HISTORY 
(
   ADMIN_ID             INTEGER              not null,
   SEQ                  INTEGER              not null,
   ADMIN_NAME           VARCHAR2(60)         not null,
   CREATED_TIME         DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   EMAIL                VARCHAR2(120),
   PHONE                VARCHAR2(20),
   ADDRESS              VARCHAR2(255),
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_ADMIN_HISTORY primary key (ADMIN_ID, SEQ)
)
/

/*==============================================================*/
/* Table: ADMIN_ROLE                                            */
/*==============================================================*/
create table ADMIN_ROLE 
(
   ADMIN_ID             INTEGER              not null,
   ROLE_ID              INTEGER              not null,
   CREATE_TIME          DATE                 not null,
   constraint PK_ADMIN_ROLE primary key (ADMIN_ID, ROLE_ID)
)
/

/*==============================================================*/
/* Table: AREA                                                  */
/*==============================================================*/
create table AREA 
(
   AREA_ID              NUMBER(6)            not null,
   PARENT_AREA_ID       INTEGER,
   AREA_TYPE            CHAR(1)              not null,
   AREA_NAME            VARCHAR2(20)         not null,
   AREA_CODE            VARCHAR2(10),
   REMARK               VARCHAR2(120),
   constraint PK_AREA primary key (AREA_ID)
)
/

/*==============================================================*/
/* Table: AREA_RANGE                                            */
/*==============================================================*/
create table AREA_RANGE 
(
   AREA_ID              INTEGER              not null,
   SEQ                  INTEGER              not null,
   LONGITUDE            VARCHAR2(20)         not null,
   LATITUDE             VARCHAR2(20)         not null,
   constraint PK_AREA_RANGE primary key (AREA_ID, SEQ)
)
/

/*==============================================================*/
/* Table: AREA_TYPE                                             */
/*==============================================================*/
create table AREA_TYPE 
(
   AREA_TYPE            CHAR(1)              not null,
   AREA_TYPE_NAME       VARCHAR2(10)         not null,
   REMARK               VARCHAR2(60),
   constraint PK_AREA_TYPE primary key (AREA_TYPE)
)
/

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
   ATTACHMENTS_ID       NUMBER(6)            not null,
   ATTACHMENTS_TYPE     VARCHAR2(20)         not null,
   ATTACHMENTS_NAME     VARCHAR2(60)         not null,
   IS_REMOTE            CHAR(1)              not null,
   FILE_SIZE            INTEGER,
   FILE_PATH            VARCHAR2(255)        not null,
   DOWNLOADS_NUM        INTEGER              not null,
   IS_PICTURE           CHAR(1)              not null,
   IS_THUMB             CHAR(1)              not null,
   THUMB_PATH           VARCHAR2(255),
   CREATE_TIME          DATE                 not null,
   EXP_TIME             DATE,
   constraint PK_ATTACHMENTS primary key (ATTACHMENTS_ID)
)
/

/*==============================================================*/
/* Table: ATTR                                                  */
/*==============================================================*/
create table ATTR 
(
   ATTR_ID              NUMBER(6)            not null,
   ATTR_NAME            VARCHAR2(60)         not null,
   ATTR_TYPE            CHAR(1)              not null,
   PARENT_ATTR_ID       INTEGER,
   ATTR_CODE            VARCHAR2(120)        not null,
   VISIBLE              CHAR(1)              not null,
   INSTANTIATABLE       CHAR(1)              not null,
   DEFAULT_VALUE        VARCHAR2(120),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR2(2000),
   constraint PK_ATTR primary key (ATTR_ID)
)
/

/*==============================================================*/
/* Table: ATTR_TYPE                                             */
/*==============================================================*/
create table ATTR_TYPE 
(
   ATTR_TYPE            CHAR(1)              not null,
   ATTR_TYPE_NAME       VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_ATTR_TYPE primary key (ATTR_TYPE)
)
/

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
   ATTR_ID              NUMBER(6)            not null,
   ATTR_VALUE_ID        INTEGER              not null,
   VALUE_MARK           VARCHAR2(60)         not null,
   VALUE                VARCHAR2(120),
   LINK_ATTR_ID         INTEGER,
   constraint PK_ATTR_VALUE primary key (ATTR_ID, ATTR_VALUE_ID)
)
/

/*==============================================================*/
/* Table: CHANNEL_TYPE                                          */
/*==============================================================*/
create table CHANNEL_TYPE 
(
   CHANNEL_TYPE         INTEGER              not null,
   CHANNEL_TYPE_NAME    VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_CHANNEL_TYPE primary key (CHANNEL_TYPE)
)
/

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
   CONFIG_ITEM_ID       NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   CONFIG_ITEM_CODE     VARCHAR2(120)        not null,
   CONFIG_ITEM_NAME     VARCHAR2(120)        not null,
   IS_VISIABLE          CHAR(1)              not null,
   UPDATE_TIME          DATE,
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM primary key (CONFIG_ITEM_ID)
)
/

/*==============================================================*/
/* Table: CONFIG_ITEM_HISTORY                                   */
/*==============================================================*/
create table CONFIG_ITEM_HISTORY 
(
   CONFIG_ITEM_ID       INTEGER              not null,
   SEQ                  INTEGER              not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   DIRECTORY_CODE       VARCHAR2(10)         not null,
   CONFIG_ITEM_CODE     VARCHAR2(120)        not null,
   CONFIG_ITEM_NAME     VARCHAR2(120)        not null,
   IS_VISIABLE          CHAR(1)              not null,
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   OPERATOR_ID          INTEGER,
   CHANNEL_ID           INTEGER,
   constraint PK_CONFIG_ITEM_HISTORY primary key (CONFIG_ITEM_ID, SEQ)
)
/

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
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR2(2000),
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM_PARAM primary key (CONFIG_ITEM_ID, PARAM_CODE)
)
/

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_HISTORY                             */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_HISTORY 
(
   CONFIG_ITEM_ID       INTEGER              not null,
   PARAM_CODE           VARCHAR2(120)        not null,
   SEQ                  INTEGER              not null,
   PARAM_NAME           VARCHAR2(120)        not null,
   PARAM_VALUE          VARCHAR2(1000),
   DEFAULT_PARAM_VALUE  VARCHAR2(1000),
   DATA_TYPE            CHAR(1),
   INPUT_TYPE           CHAR(1),
   VALUE_SCRIPT         VARCHAR2(2000),
   UPDATE_TIME          DATE                 not null,
   REMARK               VARCHAR2(255),
   OPERATOR_ID          INTEGER,
   CHANNEL_ID           INTEGER,
   constraint PK_CONFIG_ITEM_PARAM_HISTORY primary key (CONFIG_ITEM_ID, PARAM_CODE, SEQ)
)
/

/*==============================================================*/
/* Table: CONFIG_ITEM_PARAM_VALUE                               */
/*==============================================================*/
create table CONFIG_ITEM_PARAM_VALUE 
(
   CONFIG_ITEM_ID       NUMBER(6)            not null,
   PARAM_CODE           VARCHAR2(120)        not null,
   PARAM_VALUE_ID       INTEGER              not null,
   VALUE_MARK           VARCHAR2(20)         not null,
   VALUE                VARCHAR2(60),
   REMARK               VARCHAR2(255),
   constraint PK_CONFIG_ITEM_PARAM_VALUE primary key (CONFIG_ITEM_ID, PARAM_CODE, PARAM_VALUE_ID)
)
/

/*==============================================================*/
/* Table: CONTACT_CHANNEL                                       */
/*==============================================================*/
create table CONTACT_CHANNEL 
(
   CONTACT_CHANNEL_ID   INTEGER              not null,
   CHANNEL_TYPE         INTEGER              not null,
   CONTACT_CHANNEL_NAME VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_CONTACT_CHANNEL primary key (CONTACT_CHANNEL_ID)
)
/

/*==============================================================*/
/* Table: CRON_TRIGGER                                          */
/*==============================================================*/
create table CRON_TRIGGER 
(
   TRIGGER_ID           NUMBER(6)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   CRON_EXPRESSION      VARCHAR2(120)        not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   constraint PK_CRON_TRIGGER primary key (TRIGGER_ID)
)
/

/*==============================================================*/
/* Table: CRON_TRIGGER_HISTORY                                  */
/*==============================================================*/
create table CRON_TRIGGER_HISTORY 
(
   TRIGGER_ID           INTEGER              not null,
   SEQ                  INTEGER              not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   CRON_EXPRESSION      VARCHAR2(120)        not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_CRON_TRIGGER_HISTORY primary key (TRIGGER_ID, SEQ)
)
/

/*==============================================================*/
/* Table: DATA_TYPE                                             */
/*==============================================================*/
create table DATA_TYPE 
(
   DATA_TYPE            CHAR(1)              not null,
   DATA_TYPE_NAME       VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_DATA_TYPE primary key (DATA_TYPE)
)
/

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
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   DIRECTORY_NAME       VARCHAR2(20)         not null,
   PARENT_DIRECTORY_CODE VARCHAR2(20),
   REMARK               VARCHAR2(64),
   constraint PK_DIRECTORY primary key (DIRECTORY_CODE)
)
/

/*==============================================================*/
/* Table: INPUT_TYPE                                            */
/*==============================================================*/
create table INPUT_TYPE 
(
   INPUT_TYPE           CHAR(1)              not null,
   INPUT_TYPE_NAME      VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_INPUT_TYPE primary key (INPUT_TYPE)
)
/

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
   MENU_ID              NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   SEQ                  INTEGER              not null,
   MENU_NAME            VARCHAR2(20)         not null,
   PARENT_ID            INTEGER,
   IS_LEAF              CHAR(1)              not null,
   RESOURCE_ID          INTEGER,
   ICON_URL             VARCHAR2(120),
   constraint PK_MENU primary key (MENU_ID)
)
/

/*==============================================================*/
/* Table: MESSAGE_ATTACHMENTS                                   */
/*==============================================================*/
create table MESSAGE_ATTACHMENTS 
(
   ATTACHMENTS_ID       INTEGER              not null,
   MESSAGE_ID           INTEGER              not null,
   constraint PK_MESSAGE_ATTACHMENTS primary key (ATTACHMENTS_ID, MESSAGE_ID)
)
/

/*==============================================================*/
/* Table: MESSAGE_BOX                                           */
/*==============================================================*/
create table MESSAGE_BOX 
(
   MESSAGE_ID           NUMBER(6)            not null,
   RECEIVERS            CLOB                 not null,
   SENDER               VARCHAR2(120),
   MESSAGE_TYPE         CHAR(1)              not null,
   MESSAGE_TEMPLATE_ID  INTEGER              not null,
   SUBJECT              VARCHAR2(120),
   CONTENT              CLOB,
   ATTACHMENTS_NUM      INTEGER              not null,
   CREATE_TIME          DATE                 not null,
   SEND_TIME            DATE,
   NEXT_SEND_TIME       DATE,
   SEND_TIMES           INTEGER              not null,
   EXTEND_ATTRS         CLOB,
   constraint PK_MESSAGE_BOX primary key (MESSAGE_ID)
)
/

/*==============================================================*/
/* Table: MESSAGE_HISTORY                                       */
/*==============================================================*/
create table MESSAGE_HISTORY 
(
   MESSAGE_ID           NUMBER(6)            not null,
   RECEIVERS            CLOB                 not null,
   SENDER               VARCHAR2(120),
   MESSAGE_TYPE         CHAR(1)              not null,
   MESSAGE_TEMPLATE_ID  INTEGER              not null,
   SUBJECT              VARCHAR2(120),
   CONTENT              CLOB,
   ATTACHMENTS_NUM      INTEGER              not null,
   CREATE_TIME          DATE                 not null,
   SEND_TIME            DATE,
   SEND_TIMES           INTEGER              not null,
   RESULT               VARCHAR2(255)        not null,
   EXP_DATE             DATE,
   EXTEND_ATTRS         CLOB,
   constraint PK_MESSAGE_HISTORY primary key (MESSAGE_ID)
)
/

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
   DELAY                INTEGER              not null,
   RESEND_TIMES         INTEGER              not null,
   SAVE_HISTORY         CHAR(1)              not null,
   SAVE_DAY             INTEGER              not null,
   CREATE_TIME          DATE,
   constraint PK_MESSAGE_TEMPLATE primary key (MESSAGE_TEMPLATE_ID)
)
/

/*==============================================================*/
/* Table: MESSAGE_TYPE                                          */
/*==============================================================*/
create table MESSAGE_TYPE 
(
   MESSAGE_TYPE         CHAR(1)              not null,
   MESSAGE_TYPE_NAME    VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_MESSAGE_TYPE primary key (MESSAGE_TYPE)
)
/

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
   MODULE_CODE          VARCHAR2(10)         not null,
   PARENT_MODULE_CODE   VARCHAR2(10),
   MODULE_NAME          VARCHAR2(20)         not null,
   constraint PK_MODULE primary key (MODULE_CODE)
)
/

INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('COMMON',NULL,'公共模块');
INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('PORTAL','COMMON','系统门户');
INSERT INTO MODULE (MODULE_CODE,PARENT_MODULE_CODE,MODULE_NAME) VALUES ('MONITOR','COMMON','系统监控模块');

/*==============================================================*/
/* Table: OPERATOR                                              */
/*==============================================================*/
create table OPERATOR 
(
   OPERATOR_ID          NUMBER(6)            not null,
   OPERATOR_TYPE        CHAR(1)              not null,
   OPERATOR_CODE        INTEGER,
   USER_NAME            VARCHAR2(60),
   PASSWORD             VARCHAR2(60),
   CREATE_DATE          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   IS_LOCKED            CHAR(1)              not null,
   PWD_EXP_DATE         DATE,
   LOGIN_FAIL           INTEGER              not null,
   REGIST_IP            VARCHAR2(16),
   LAST_IP              VARCHAR2(16),
   LAST_LOGIN_DATE      DATE,
   constraint PK_OPERATOR primary key (OPERATOR_ID)
)
/

/*==============================================================*/
/* Table: OPERATOR_HISTORY                                      */
/*==============================================================*/
create table OPERATOR_HISTORY 
(
   OPERATOR_ID          INTEGER              not null,
   SEQ                  INTEGER              not null,
   OPERATOR_TYPE        CHAR(1)              not null,
   OPERATOR_CODE        VARCHAR2(10)         not null,
   USER_NAME            VARCHAR2(60),
   PASSWORD             VARCHAR2(60),
   CREATE_DATE          DATE                 not null,
   STATE                CHAR(1)              not null,
   STATE_DATE           DATE                 not null,
   IS_LOCKED            CHAR(1)              not null,
   PWD_EXP_DATE         DATE,
   LOGIN_FAIL           INTEGER              not null,
   REGIST_IP            VARCHAR2(16),
   LAST_IP              VARCHAR2(16),
   LAST_LOGIN_DATE      DATE,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_OPERATOR_HISTORY primary key (OPERATOR_ID, SEQ)
)
/

/*==============================================================*/
/* Table: OPERATOR_RESOURCE                                     */
/*==============================================================*/
create table OPERATOR_RESOURCE 
(
   OPERATOR_ID          INTEGER              not null,
   RESOURCE_ID          INTEGER              not null,
   RESOURCE_TYPE        INTEGER              not null,
   constraint PK_OPERATOR_RESOURCE primary key (OPERATOR_ID, RESOURCE_ID, RESOURCE_TYPE)
)
/

/*==============================================================*/
/* Table: OPERATOR_ROLE_HISTORY                                 */
/*==============================================================*/
create table OPERATOR_ROLE_HISTORY 
(
   OPERATOR_ID          INTEGER              not null,
   ROLE_ID              INTEGER              not null,
   SEQ                  INTEGER              not null,
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_OPERATOR_ROLE_HISTORY primary key (OPERATOR_ID, ROLE_ID, SEQ)
)
/

/*==============================================================*/
/* Table: OPERATOR_TYPE                                         */
/*==============================================================*/
create table OPERATOR_TYPE 
(
   OPERATOR_TYPE        CHAR(1)              not null,
   OPERATOR_TYPE_NAME   VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_OPERATOR_TYPE primary key (OPERATOR_TYPE)
)
/

INSERT INTO OPERATOR_TYPE VALUES ('A','Admin','管理员');
INSERT INTO OPERATOR_TYPE VALUES ('M','Member','会员');
INSERT INTO OPERATOR_TYPE VALUES ('C','Community','物业管理员');
INSERT INTO OPERATOR_TYPE VALUES ('B','Business','商家');

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
)
/

comment on table QRTZ_BLOB_TRIGGERS is
'Trigger 作为 Blob 类型存储(用于 Quartz 用户用 JDBC 创建他们自己定制的 Trigger 类型，JobStore 并不知道如何存储实例的时候)'
/

/*==============================================================*/
/* Table: QRTZ_CALENDARS                                        */
/*==============================================================*/
create table QRTZ_CALENDARS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   CALENDAR_NAME        VARCHAR2(60)         not null,
   CALENDAR             BLOB                 not null,
   constraint PK_QRTZ_CALENDARS primary key (SCHED_NAME, CALENDAR_NAME)
)
/

comment on table QRTZ_CALENDARS is
'以 Blob 类型存储 Quartz 的 Calendar 信息'
/

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
)
/

comment on table QRTZ_CRON_TRIGGERS is
'存储 Cron Trigger，包括 Cron 表达式和时区信息'
/

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
   FIRED_TIME           INTEGER              not null,
   SCHED_TIME           INTEGER              not null,
   PRIORITY             INTEGER              not null,
   STATE                VARCHAR2(16)         not null,
   JOB_NAME             VARCHAR2(200),
   JOB_GROUP            VARCHAR2(200),
   IS_NONCONCURRENT     VARCHAR2(1),
   REQUESTS_RECOVERY    VARCHAR2(1),
   constraint PK_QRTZ_FIRED_TRIGGERS primary key (SCHED_NAME, ENTRY_ID)
)
/

comment on table QRTZ_FIRED_TRIGGERS is
'存储与已触发的 Trigger 相关的状态信息，以及相联 Job 的执行信息'
/

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
)
/

comment on table QRTZ_JOB_DETAILS is
'存储每一个已配置的 Job 的详细信息'
/

/*==============================================================*/
/* Table: QRTZ_LOCKS                                            */
/*==============================================================*/
create table QRTZ_LOCKS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   LOCK_NAME            VARCHAR2(40)         not null,
   constraint PK_QRTZ_LOCKS primary key (SCHED_NAME, LOCK_NAME)
)
/

comment on table QRTZ_LOCKS is
'存储程序的非观锁的信息(假如使用了悲观锁)'
/

/*==============================================================*/
/* Table: QRTZ_PAUSED_TRIGGER_GRPS                              */
/*==============================================================*/
create table QRTZ_PAUSED_TRIGGER_GRPS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   constraint PK_QRTZ_PAUSED_TRIGGER_GRPS primary key (SCHED_NAME, TRIGGER_GROUP)
)
/

comment on table QRTZ_PAUSED_TRIGGER_GRPS is
'存储已暂停的 Trigger 组的信息'
/

/*==============================================================*/
/* Table: QRTZ_SCHEDULER_STATE                                  */
/*==============================================================*/
create table QRTZ_SCHEDULER_STATE 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   INSTANCE_NAME        VARCHAR2(60)         not null,
   LAST_CHECKIN_TIME    INTEGER              not null,
   CHECKIN_INTERVAL     INTEGER              not null,
   constraint PK_QRTZ_SCHEDULER_STATE primary key (SCHED_NAME, INSTANCE_NAME)
)
/

comment on table QRTZ_SCHEDULER_STATE is
'存储少量的有关 Scheduler 的状态信息，和别的 Scheduler 实例(假如是用于一个集群中)'
/

/*==============================================================*/
/* Table: QRTZ_SIMPLE_TRIGGERS                                  */
/*==============================================================*/
create table QRTZ_SIMPLE_TRIGGERS 
(
   SCHED_NAME           VARCHAR2(20)         not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   TRIGGER_GROUP        VARCHAR2(60)         not null,
   REPEAT_COUNT         INTEGER              not null,
   REPEAT_INTERVAL      INTEGER              not null,
   TIMES_TRIGGERED      INTEGER              not null,
   constraint PK_QRTZ_SIMPLE_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
/

comment on table QRTZ_SIMPLE_TRIGGERS is
' 存储简单的 Trigger，包括重复次数，间隔，以及已触的次数'
/

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
   INT_PROP_1           INTEGER,
   INT_PROP_2           INTEGER,
   LONG_PROP_1          INTEGER,
   LONG_PROP_2          INTEGER,
   DEC_PROP_1           NUMBER(13,4),
   DEC_PROP_2           NUMBER(13,4),
   BOOL_PROP_1          VARCHAR2(1),
   BOOL_PROP_2          VARCHAR2(1),
   constraint PK_QRTZ_SIMPROP_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
/

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
   NEXT_FIRE_TIME       INTEGER,
   PREV_FIRE_TIME       INTEGER,
   PRIORITY             INTEGER,
   TRIGGER_STATE        VARCHAR2(16)         not null,
   TRIGGER_TYPE         VARCHAR2(8)          not null,
   START_TIME           INTEGER              not null,
   END_TIME             INTEGER,
   CALENDAR_NAME        VARCHAR2(200),
   MISFIRE_INSTR        SMALLINT,
   JOB_DATA             BLOB,
   constraint PK_QRTZ_TRIGGERS primary key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
)
/

comment on table QRTZ_TRIGGERS is
'存储已配置的 Trigger 的信息'
/

/*==============================================================*/
/* Table: RESOURCE_TYPE                                         */
/*==============================================================*/
create table RESOURCE_TYPE 
(
   RESOURCE_TYPE        INTEGER              not null,
   RESOURCE_TYPE_NAME   VARCHAR2(20)         not null,
   REMARK               VARCHAR2(60),
   constraint PK_RESOURCE_TYPE primary key (RESOURCE_TYPE)
)
/

/*==============================================================*/
/* Table: ROLE                                                  */
/*==============================================================*/
create table ROLE 
(
   ROLE_ID              NUMBER(6)            not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   ROLE_NAME            VARCHAR2(60)         not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   constraint PK_ROLE primary key (ROLE_ID)
)
/

/*==============================================================*/
/* Table: ROLE_HISTORY                                          */
/*==============================================================*/
create table ROLE_HISTORY 
(
   ROLE_ID              INTEGER              not null,
   SEQ                  INTEGER              not null,
   ROLE_NAME            VARCHAR2(60)         not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_ROLE_HISTORY primary key (ROLE_ID, SEQ)
)
/

/*==============================================================*/
/* Table: ROLE_RESOURCE                                         */
/*==============================================================*/
create table ROLE_RESOURCE 
(
   ROLE_ID              INTEGER              not null,
   RESOURCE_ID          INTEGER              not null,
   RESOURCE_TYPE        INTEGER              not null,
   constraint PK_ROLE_RESOURCE primary key (ROLE_ID, RESOURCE_ID, RESOURCE_TYPE)
)
/

/*==============================================================*/
/* Table: SEND_RECORD                                           */
/*==============================================================*/
create table SEND_RECORD 
(
   SEND_RECORD_ID       NUMBER(6)            not null,
   MESSAGE_ID           INTEGER              not null,
   CONTACT_CHANNEL_ID   INTEGER              not null,
   SEND_TIME            DATE                 not null,
   RESULT               VARCHAR2(255)        not null,
   constraint PK_SEND_RECORD primary key (SEND_RECORD_ID)
)
/

/*==============================================================*/
/* Table: SIMPLE_TRIGGER                                        */
/*==============================================================*/
create table SIMPLE_TRIGGER 
(
   TRIGGER_ID           NUMBER(6)            not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE,
   TIMES                INTEGER,
   EXECUTE_INTERVAL     INTEGER              not null,
   INTERVAL_UNIT        CHAR(1)              not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   constraint PK_SIMPLE_TRIGGER primary key (TRIGGER_ID)
)
/

/*==============================================================*/
/* Table: SIMPLE_TRIGGER_HISTORY                                */
/*==============================================================*/
create table SIMPLE_TRIGGER_HISTORY 
(
   TRIGGER_ID           INTEGER              not null,
   SEQ                  INTEGER              not null,
   TRIGGER_NAME         VARCHAR2(60)         not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE,
   TIMES                INTEGER,
   EXECUTE_INTERVAL     INTEGER              not null,
   INTERVAL_UNIT        CHAR(1)              not null,
   CREATE_TIME          DATE                 not null,
   OPERATOR_ID          INTEGER,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_SIMPLE_TRIGGER_HISTORY primary key (TRIGGER_ID, SEQ)
)
/

/*==============================================================*/
/* Table: TASK                                                  */
/*==============================================================*/
create table TASK 
(
   TASK_ID              NUMBER(6)            not null,
   TASK_NAME            VARCHAR2(60)         not null,
   CLASS_NAME           VARCHAR2(60)         not null,
   METHOD               VARCHAR2(20)         not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   PRIORITY             INTEGER              not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1)              not null,
   LAST_EXECUTE_TIME    DATE,
   NEXT_EXCUTE_DATE     DATE,
   OPERATOR_ID          INTEGER,
   CREATE_TIME          DATE                 not null,
   constraint PK_TASK primary key (TASK_ID),
   constraint AK_KEY_2_TASK unique (TASK_NAME)
)
/

/*==============================================================*/
/* Table: TASK_HISTORY                                          */
/*==============================================================*/
create table TASK_HISTORY 
(
   TASK_ID              INTEGER              not null,
   SEQ                  INTEGER              not null,
   TASK_NAME            VARCHAR2(60)         not null,
   CLASS_NAME           VARCHAR2(60)         not null,
   METHOD               VARCHAR2(20)         not null,
   MODULE_CODE          VARCHAR2(10)         not null,
   PRIORITY             INTEGER              not null,
   IS_CONCURRENT        CHAR(1),
   TASK_STATE           CHAR(1)              not null,
   LAST_EXECUTE_TIME    DATE,
   NEXT_EXCUTE_DATE     DATE,
   OPERATOR_ID          INTEGER,
   CREATE_TIME          DATE                 not null,
   UPDATE_TIME          DATE                 not null,
   UPDATE_OPERATOR_ID   INTEGER,
   constraint PK_TASK_HISTORY primary key (TASK_ID, SEQ)
)
/

/*==============================================================*/
/* Table: TASK_STATE                                            */
/*==============================================================*/
create table TASK_STATE 
(
   TASK_STATE           CHAR(1)              not null,
   TASK_STATE_NAME      VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_TASK_STATE primary key (TASK_STATE)
)
/

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
   TASK_ID              INTEGER              not null,
   TRIGGER_TYPE         INTEGER              not null,
   TRIGGER_ID           INTEGER              not null,
   constraint PK_TASK_TRIGGER primary key (TASK_ID, TRIGGER_TYPE, TRIGGER_ID)
)
/

/*==============================================================*/
/* Table: TRANS_LOG                                             */
/*==============================================================*/
create table TRANS_LOG 
(
   TRANS_ID             VARCHAR2(36)         not null,
   MODULE_CODE          VARCHAR2(10),
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE                 not null,
   CONSUME_TIME         INTEGER              not null,
   INPUT_PARAM          CLOB,
   OUTPUT_PARAM         CLOB,
   SQL_LOG              CLOB,
   EXCEPTION_LOG        CLOB,
   CONTACT_CHANNEL_ID   INTEGER,
   constraint PK_TRANS_LOG primary key (TRANS_ID)
)
/

/*==============================================================*/
/* Table: TRANS_LOG_STACK                                       */
/*==============================================================*/
create table TRANS_LOG_STACK 
(
   STACK_ID             VARCHAR2(36)         not null,
   SEQ                  INTEGER              not null,
   TRANS_ID             VARCHAR2(36)         not null,
   PARENT_STACK_ID      VARCHAR2(36),
   METHOD               CLOB                 not null,
   BEGIN_TIME           DATE                 not null,
   END_TIME             DATE                 not null,
   CONSUME_TIME         INTEGER              not null,
   INPUT_PARAM          CLOB,
   OUTPUT_PARAM         CLOB,
   IS_SUCCESS           CHAR(1)              not null,
   constraint PK_TRANS_LOG_STACK primary key (STACK_ID)
)
/

/*==============================================================*/
/* Table: TRIGGER_TYPE                                          */
/*==============================================================*/
create table TRIGGER_TYPE 
(
   TRIGGER_TYPE         INTEGER              not null,
   TRIGGER_TYPE_NAME    VARCHAR2(20)         not null,
   REMARK               VARCHAR2(120),
   constraint PK_TRIGGER_TYPE primary key (TRIGGER_TYPE)
)
/

INSERT INTO TRIGGER_TYPE(TRIGGER_TYPE,TRIGGER_TYPE_NAME,REMARK) VALUES(1,'简单触发器类型','简单触发器可以设置开始时间、结束时间、执行间隔、执行次数等');
INSERT INTO TRIGGER_TYPE(TRIGGER_TYPE,TRIGGER_TYPE_NAME,REMARK) VALUES(2,'Cron表达式触发器类型','Cron表达式触发器采用Cron表达式来设置触发器的');

/*==============================================================*/
/* Table: URL_RESOURCE                                          */
/*==============================================================*/
create table URL_RESOURCE 
(
   RESOURCE_ID          NUMBER(6)            not null,
   DIRECTORY_CODE       VARCHAR2(20)         not null,
   RESOURCE_NAME        VARCHAR2(60)         not null,
   URL                  VARCHAR2(120)        not null,
   EXECUTE_CLASS        VARCHAR2(120),
   EXECUTE_METHOD       VARCHAR2(60),
   MODULE_CODE          VARCHAR2(10)         not null,
   REMARK               VARCHAR2(255),
   constraint PK_URL_RESOURCE primary key (RESOURCE_ID)
)
/

alter table ACCOUNT
   add constraint FK_ACCOUNT_ACCOUNT_T_ACCOUNT_ foreign key (ACCOUNT_TYPE)
      references ACCOUNT_TYPE (ACCOUNT_TYPE)
/

alter table ACCOUNT
   add constraint FK_ACCOUNT_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID)
/

alter table ADMIN
   add constraint FK_ADMIN_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID)
/

alter table ADMIN_ATTR
   add constraint FK_ADMIN_AT_ADMIN_ID__ADMIN foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID)
/

alter table ADMIN_ATTR
   add constraint FK_ADMIN_AT_ATTR_ID_A_ATTR foreign key (ATTR_ID)
      references ATTR (ATTR_ID)
/

alter table ADMIN_ROLE
   add constraint FK_ADMIN_RO_ADMIN_ID__ADMIN foreign key (ADMIN_ID)
      references ADMIN (ADMIN_ID)
/

alter table ADMIN_ROLE
   add constraint FK_ADMIN_RO_ROLE_ID_A_ROLE foreign key (ROLE_ID)
      references ROLE (ROLE_ID)
/

alter table AREA
   add constraint FK_AREA_AREA_ID_A_AREA foreign key (PARENT_AREA_ID)
      references AREA (AREA_ID)
/

alter table AREA
   add constraint FK_AREA_AREA_TYPE_AREA_TYP foreign key (AREA_TYPE)
      references AREA_TYPE (AREA_TYPE)
/

alter table AREA_RANGE
   add constraint FK_AREA_RAN_AREA_AREA_AREA foreign key (AREA_ID)
      references AREA (AREA_ID)
/

alter table ATTR
   add constraint FK_ATTR_ATTR_TYPE_ATTR_TYP foreign key (ATTR_TYPE)
      references ATTR_TYPE (ATTR_TYPE)
/

alter table ATTR
   add constraint FK_ATTR_DATA_TYPE_DATA_TYP foreign key (DATA_TYPE)
      references DATA_TYPE (DATA_TYPE)
/

alter table ATTR
   add constraint FK_ATTR_INPUT_TYP_INPUT_TY foreign key (INPUT_TYPE)
      references INPUT_TYPE (INPUT_TYPE)
/

alter table ATTR_VALUE
   add constraint FK_ATTR_VAL_ATTR_ID_A_ATTR foreign key (ATTR_ID)
      references ATTR (ATTR_ID)
/

alter table ATTR_VALUE
   add constraint FK_ATTR_VAL_ATTR_ID_A_ATTR foreign key (LINK_ATTR_ID)
      references ATTR (ATTR_ID)
/

alter table CONFIG_ITEM
   add constraint FK_CONFIG_I_DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE)
/

alter table CONFIG_ITEM
   add constraint FK_CONFIG_I_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table CONFIG_ITEM_PARAM
   add constraint FK_CONFIG_I_CONFIG_IT_CONFIG_I foreign key (CONFIG_ITEM_ID)
      references CONFIG_ITEM (CONFIG_ITEM_ID)
/

alter table CONFIG_ITEM_PARAM
   add constraint FK_CONFIG_I_DATA_TYPE_DATA_TYP foreign key (DATA_TYPE)
      references DATA_TYPE (DATA_TYPE)
/

alter table CONFIG_ITEM_PARAM
   add constraint FK_CONFIG_I_INPUT_TYP_INPUT_TY foreign key (INPUT_TYPE)
      references INPUT_TYPE (INPUT_TYPE)
/

alter table CONFIG_ITEM_PARAM_VALUE
   add constraint FK_CONFIG_I_CONFIG_IT_CONFIG_I foreign key (CONFIG_ITEM_ID, PARAM_CODE)
      references CONFIG_ITEM_PARAM (CONFIG_ITEM_ID, PARAM_CODE)
/

alter table CONTACT_CHANNEL
   add constraint FK_CONTACT__CHANNEL_T_CHANNEL_ foreign key (CHANNEL_TYPE)
      references CHANNEL_TYPE (CHANNEL_TYPE)
/

alter table DIRECTORY
   add constraint FK_DIRECTOR_DIRECTORY_DIRECTOR foreign key (PARENT_DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE)
/

alter table MENU
   add constraint FK_MENU_MENU_MENU_MENU foreign key (PARENT_ID)
      references MENU (MENU_ID)
/

alter table MENU
   add constraint FK_MENU_MODULE_ME_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table MENU
   add constraint FK_MENU_RESOURCE__URL_RESO foreign key (RESOURCE_ID)
      references URL_RESOURCE (RESOURCE_ID)
/

alter table MESSAGE_ATTACHMENTS
   add constraint FK_MESSAGE__ATTACHMEN_ATTACHME foreign key (ATTACHMENTS_ID)
      references ATTACHMENTS (ATTACHMENTS_ID)
/

alter table MESSAGE_BOX
   add constraint FK_MESSAGE__MESSAGE_T_MESSAGE_ foreign key (MESSAGE_TEMPLATE_ID)
      references MESSAGE_TEMPLATE (MESSAGE_TEMPLATE_ID)
/

alter table MESSAGE_BOX
   add constraint FK_MESSAGE__MESSAGE_T_MESSAGE_ foreign key (MESSAGE_TYPE)
      references MESSAGE_TYPE (MESSAGE_TYPE)
/

alter table MESSAGE_TEMPLATE
   add constraint FK_MESSAGE__DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE)
/

alter table MODULE
   add constraint FK_MODULE_MODULE_CO_MODULE foreign key (PARENT_MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table OPERATOR
   add constraint FK_OPERATOR_OPERATOR__OPERATOR foreign key (OPERATOR_TYPE)
      references OPERATOR_TYPE (OPERATOR_TYPE)
/

alter table OPERATOR_RESOURCE
   add constraint FK_OPERATOR_OPERATOR__OPERATOR foreign key (OPERATOR_ID)
      references OPERATOR (OPERATOR_ID)
/

alter table OPERATOR_RESOURCE
   add constraint FK_OPERATOR_RESOURCE__RESOURCE foreign key (RESOURCE_TYPE)
      references RESOURCE_TYPE (RESOURCE_TYPE)
/

alter table QRTZ_BLOB_TRIGGERS
   add constraint FK_QRTZ_BLO_REFERENCE_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
/

alter table QRTZ_CRON_TRIGGERS
   add constraint FK_QRTZ_CRO_REFERENCE_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
/

alter table QRTZ_SIMPLE_TRIGGERS
   add constraint FK_QRTZ_SIM_REFERENCE_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
/

alter table QRTZ_SIMPROP_TRIGGERS
   add constraint FK_QRTZ_SIM_REFERENCE_QRTZ_TRI foreign key (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
      references QRTZ_TRIGGERS (SCHED_NAME, TRIGGER_NAME, TRIGGER_GROUP)
/

alter table QRTZ_TRIGGERS
   add constraint FK_QRTZ_TRI_REFERENCE_QRTZ_JOB foreign key (SCHED_NAME, JOB_NAME, JOB_GROUP)
      references QRTZ_JOB_DETAILS (SCHED_NAME, JOB_NAME, JOB_GROUP)
/

alter table ROLE
   add constraint FK_ROLE_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table ROLE_RESOURCE
   add constraint FK_ROLE_RES_RESOURCE__RESOURCE foreign key (RESOURCE_TYPE)
      references RESOURCE_TYPE (RESOURCE_TYPE)
/

alter table ROLE_RESOURCE
   add constraint FK_ROLE_RES_ROLE_ID_R_ROLE foreign key (ROLE_ID)
      references ROLE (ROLE_ID)
/

alter table SEND_RECORD
   add constraint FK_SEND_REC_CONTACT_C_CONTACT_ foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID)
/

alter table TASK
   add constraint FK_TASK_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table TASK
   add constraint FK_TASK_TASK_STAT_TASK_STA foreign key (TASK_STATE)
      references TASK_STATE (TASK_STATE)
/

alter table TASK_TRIGGER
   add constraint FK_TASK_TRI_TASK_ID_T_TASK foreign key (TASK_ID)
      references TASK (TASK_ID)
/

alter table TASK_TRIGGER
   add constraint FK_TASK_TRI_TRIGGER_T_TRIGGER_ foreign key (TRIGGER_TYPE)
      references TRIGGER_TYPE (TRIGGER_TYPE)
/

alter table TRANS_LOG
   add constraint FK_TRANS_LO_CONTACT_C_CONTACT_ foreign key (CONTACT_CHANNEL_ID)
      references CONTACT_CHANNEL (CONTACT_CHANNEL_ID)
/

alter table TRANS_LOG
   add constraint FK_TRANS_LO_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/

alter table URL_RESOURCE
   add constraint FK_URL_RESO_DIRECTORY_DIRECTOR foreign key (DIRECTORY_CODE)
      references DIRECTORY (DIRECTORY_CODE)
/

alter table URL_RESOURCE
   add constraint FK_URL_RESO_MODULE_CO_MODULE foreign key (MODULE_CODE)
      references MODULE (MODULE_CODE)
/


create or replace trigger "CompoundDeleteTrigger_account"
for delete on ACCOUNT compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_account"
for insert on ACCOUNT compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_account"
for update on ACCOUNT compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_account" before insert
on ACCOUNT for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ACCOUNT_ID" uses sequence S_ACCOUNT
    select S_ACCOUNT.NEXTVAL INTO :new.ACCOUNT_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_admin"
for delete on ADMIN compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_admin"
for insert on ADMIN compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_admin"
for update on ADMIN compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_admin" before insert
on ADMIN for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ADMIN_ID" uses sequence S_ADMIN
    select S_ADMIN.NEXTVAL INTO :new.ADMIN_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_area"
for delete on AREA compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_area"
for insert on AREA compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_area"
for update on AREA compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_area" before insert
on AREA for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "AREA_ID" uses sequence S_AREA
    select S_AREA.NEXTVAL INTO :new.AREA_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_attachme"
for delete on ATTACHMENTS compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_attachme"
for insert on ATTACHMENTS compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_attachme"
for update on ATTACHMENTS compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_attachments" before insert
on ATTACHMENTS for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ATTACHMENTS_ID" uses sequence S_ATTACHMENTS
    select S_ATTACHMENTS.NEXTVAL INTO :new.ATTACHMENTS_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_attr"
for delete on ATTR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_attr"
for insert on ATTR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_attr"
for update on ATTR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_attr" before insert
on ATTR for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ATTR_ID" uses sequence S_ATTR
    select S_ATTR.NEXTVAL INTO :new.ATTR_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_attr_val"
for delete on ATTR_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_attr_val"
for insert on ATTR_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_attr_val"
for update on ATTR_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_attr_value" before insert
on ATTR_VALUE for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ATTR_ID" uses sequence S_ATTR_VALUE
    select S_ATTR_VALUE.NEXTVAL INTO :new.ATTR_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_config_i"
for delete on CONFIG_ITEM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_config_i"
for insert on CONFIG_ITEM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_config_i"
for update on CONFIG_ITEM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_config_item" before insert
on CONFIG_ITEM for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "CONFIG_ITEM_ID" uses sequence S_CONFIG_ITEM
    select S_CONFIG_ITEM.NEXTVAL INTO :new.CONFIG_ITEM_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_config_i"
for delete on CONFIG_ITEM_PARAM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_config_i"
for insert on CONFIG_ITEM_PARAM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_config_i"
for update on CONFIG_ITEM_PARAM compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_config_item_param" before insert
on CONFIG_ITEM_PARAM for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "CONFIG_ITEM_ID" uses sequence S_CONFIG_ITEM_PARAM
    select S_CONFIG_ITEM_PARAM.NEXTVAL INTO :new.CONFIG_ITEM_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_config_i"
for delete on CONFIG_ITEM_PARAM_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_config_i"
for insert on CONFIG_ITEM_PARAM_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_config_i"
for update on CONFIG_ITEM_PARAM_VALUE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_config_item_param_value" before insert
on CONFIG_ITEM_PARAM_VALUE for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "CONFIG_ITEM_ID" uses sequence S_CONFIG_ITEM_PARAM_VALUE
    select S_CONFIG_ITEM_PARAM_VALUE.NEXTVAL INTO :new.CONFIG_ITEM_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_cron_tri"
for delete on CRON_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_cron_tri"
for insert on CRON_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_cron_tri"
for update on CRON_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_cron_trigger" before insert
on CRON_TRIGGER for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "TRIGGER_ID" uses sequence S_CRON_TRIGGER
    select S_CRON_TRIGGER.NEXTVAL INTO :new.TRIGGER_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_menu"
for delete on MENU compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_menu"
for insert on MENU compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_menu"
for update on MENU compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_menu" before insert
on MENU for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "MENU_ID" uses sequence S_MENU
    select S_MENU.NEXTVAL INTO :new.MENU_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_message_"
for delete on MESSAGE_BOX compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_message_"
for insert on MESSAGE_BOX compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_message_"
for update on MESSAGE_BOX compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_message_box" before insert
on MESSAGE_BOX for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "MESSAGE_ID" uses sequence S_MESSAGE_BOX
    select S_MESSAGE_BOX.NEXTVAL INTO :new.MESSAGE_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_message_"
for delete on MESSAGE_HISTORY compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_message_"
for insert on MESSAGE_HISTORY compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_message_"
for update on MESSAGE_HISTORY compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_message_history" before insert
on MESSAGE_HISTORY for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "MESSAGE_ID" uses sequence S_MESSAGE_HISTORY
    select S_MESSAGE_HISTORY.NEXTVAL INTO :new.MESSAGE_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_message_"
for delete on MESSAGE_TEMPLATE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_message_"
for insert on MESSAGE_TEMPLATE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_message_"
for update on MESSAGE_TEMPLATE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_message_template" before insert
on MESSAGE_TEMPLATE for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "MESSAGE_TEMPLATE_ID" uses sequence S_MESSAGE_TEMPLATE
    select S_MESSAGE_TEMPLATE.NEXTVAL INTO :new.MESSAGE_TEMPLATE_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_operator"
for delete on OPERATOR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_operator"
for insert on OPERATOR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_operator"
for update on OPERATOR compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_operator" before insert
on OPERATOR for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "OPERATOR_ID" uses sequence S_OPERATOR
    select S_OPERATOR.NEXTVAL INTO :new.OPERATOR_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_role"
for delete on ROLE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_role"
for insert on ROLE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_role"
for update on ROLE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_role" before insert
on ROLE for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "ROLE_ID" uses sequence S_ROLE
    select S_ROLE.NEXTVAL INTO :new.ROLE_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_send_rec"
for delete on SEND_RECORD compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_send_rec"
for insert on SEND_RECORD compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_send_rec"
for update on SEND_RECORD compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_send_record" before insert
on SEND_RECORD for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "SEND_RECORD_ID" uses sequence S_SEND_RECORD
    select S_SEND_RECORD.NEXTVAL INTO :new.SEND_RECORD_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_simple_t"
for delete on SIMPLE_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_simple_t"
for insert on SIMPLE_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_simple_t"
for update on SIMPLE_TRIGGER compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_simple_trigger" before insert
on SIMPLE_TRIGGER for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "TRIGGER_ID" uses sequence S_SIMPLE_TRIGGER
    select S_SIMPLE_TRIGGER.NEXTVAL INTO :new.TRIGGER_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_task"
for delete on TASK compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_task"
for insert on TASK compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_task"
for update on TASK compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_task" before insert
on TASK for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "TASK_ID" uses sequence S_TASK
    select S_TASK.NEXTVAL INTO :new.TASK_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/


create or replace trigger "CompoundDeleteTrigger_url_reso"
for delete on URL_RESOURCE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundInsertTrigger_url_reso"
for insert on URL_RESOURCE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create or replace trigger "CompoundUpdateTrigger_url_reso"
for update on URL_RESOURCE compound trigger
// Declaration
// Body
  before statement is
  begin
     NULL;
  end before statement;

  before each row is
  begin
     NULL;
  end before each row;

  after each row is
  begin
     NULL;
  end after each row;

  after statement is
  begin
     NULL;
  end after statement;

END
/


create trigger "tib_url_resource" before insert
on URL_RESOURCE for each row
declare
    integrity_error  exception;
    errno            integer;
    errmsg           char(200);
    dummy            integer;
    found            boolean;

begin
    --  Column "RESOURCE_ID" uses sequence S_URL_RESOURCE
    select S_URL_RESOURCE.NEXTVAL INTO :new.RESOURCE_ID from dual;

--  Errors handling
exception
    when integrity_error then
       raise_application_error(errno, errmsg);
end;
/

