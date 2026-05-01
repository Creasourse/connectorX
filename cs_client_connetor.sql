-- create database if not exists
CREATE DATABASE "cs_client_conn";


-- ----------------------------
-- Sequence structure for conn_docker_service_enum_conn_docker_service_enum_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."conn_docker_service_enum_conn_docker_service_enum_id_seq";
CREATE SEQUENCE "public"."conn_docker_service_enum_conn_docker_service_enum_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for local_connector_local_connector_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."local_connector_local_connector_id_seq";
CREATE SEQUENCE "public"."local_connector_local_connector_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for meta_table_column_table_column_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."meta_table_column_table_column_id_seq";
CREATE SEQUENCE "public"."meta_table_column_table_column_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for meta_table_table_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."meta_table_table_id_seq";
CREATE SEQUENCE "public"."meta_table_table_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for sys_user_user_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."sys_user_user_id_seq";
CREATE SEQUENCE "public"."sys_user_user_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_callback_config_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_callback_config_id_seq";
CREATE SEQUENCE "public"."wecom_callback_config_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_callback_log_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_callback_log_id_seq";
CREATE SEQUENCE "public"."wecom_callback_log_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_cdp_tag_mapping_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_cdp_tag_mapping_id_seq";
CREATE SEQUENCE "public"."wecom_cdp_tag_mapping_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_contact_rules_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_contact_rules_id_seq";
CREATE SEQUENCE "public"."wecom_contact_rules_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_corp_agent_wecom_corp_agent_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_corp_agent_wecom_corp_agent_id_seq";
CREATE SEQUENCE "public"."wecom_corp_agent_wecom_corp_agent_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_corp_tags_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_corp_tags_id_seq";
CREATE SEQUENCE "public"."wecom_corp_tags_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_corp_wecome_corp_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_corp_wecome_corp_id_seq";
CREATE SEQUENCE "public"."wecom_corp_wecome_corp_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_customer_groups_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_customer_groups_id_seq";
CREATE SEQUENCE "public"."wecom_customer_groups_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_departments_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_departments_id_seq";
CREATE SEQUENCE "public"."wecom_departments_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_employee_details_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_employee_details_id_seq";
CREATE SEQUENCE "public"."wecom_employee_details_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_employees_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_employees_id_seq";
CREATE SEQUENCE "public"."wecom_employees_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_external_contacts_details_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_external_contacts_details_id_seq";
CREATE SEQUENCE "public"."wecom_external_contacts_details_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_external_contacts_mapping_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_external_contacts_mapping_id_seq";
CREATE SEQUENCE "public"."wecom_external_contacts_mapping_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_external_tags_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_external_tags_id_seq";
CREATE SEQUENCE "public"."wecom_external_tags_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_group_members_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_group_members_id_seq";
CREATE SEQUENCE "public"."wecom_group_members_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_sync_logs_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_sync_logs_id_seq";
CREATE SEQUENCE "public"."wecom_sync_logs_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Sequence structure for wecom_tag_groups_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."wecom_tag_groups_id_seq";
CREATE SEQUENCE "public"."wecom_tag_groups_id_seq"
INCREMENT 1
MINVALUE  1
MAXVALUE 9223372036854775807
START 1
CACHE 1;

-- ----------------------------
-- Table structure for conn_docker_service_enum
-- ----------------------------
DROP TABLE IF EXISTS "public"."conn_docker_service_enum";
CREATE TABLE "public"."conn_docker_service_enum" (
  "conn_docker_service_enum_id" int8 NOT NULL DEFAULT nextval('conn_docker_service_enum_conn_docker_service_enum_id_seq'::regclass),
  "connector_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "service_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL
)
;
COMMENT ON COLUMN "public"."conn_docker_service_enum"."conn_docker_service_enum_id" IS '主键';
COMMENT ON COLUMN "public"."conn_docker_service_enum"."connector_name" IS '连接器名称';
COMMENT ON COLUMN "public"."conn_docker_service_enum"."service_name" IS '插件在docker compose中的服务名称';
COMMENT ON TABLE "public"."conn_docker_service_enum" IS '插件在docker中的服务名称';

-- ----------------------------
-- Table structure for local_connector
-- ----------------------------
DROP TABLE IF EXISTS "public"."local_connector";
CREATE TABLE "public"."local_connector" (
  "local_connector_id" int8 NOT NULL DEFAULT nextval('local_connector_local_connector_id_seq'::regclass),
  "connector_id" int8 NOT NULL,
  "connector_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "titile" varchar(50) COLLATE "pg_catalog"."default",
  "remark" text COLLATE "pg_catalog"."default",
  "icon" varchar(255) COLLATE "pg_catalog"."default",
  "staus" int2,
  "user_id" int4 NOT NULL,
  "user_name" varchar(100) COLLATE "pg_catalog"."default",
  "version" varchar(50) COLLATE "pg_catalog"."default",
  "create_time" timestamp(0),
  "update_time" timestamp(0),
  "location_url" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."local_connector"."local_connector_id" IS '我的连接器ID';
COMMENT ON COLUMN "public"."local_connector"."connector_id" IS '连接器主键';
COMMENT ON COLUMN "public"."local_connector"."connector_name" IS '连接器名称';
COMMENT ON COLUMN "public"."local_connector"."titile" IS '标题';
COMMENT ON COLUMN "public"."local_connector"."remark" IS '简介';
COMMENT ON COLUMN "public"."local_connector"."icon" IS '图标';
COMMENT ON COLUMN "public"."local_connector"."staus" IS '1:上线 2:下线 3:使用中';
COMMENT ON COLUMN "public"."local_connector"."user_id" IS '用户ID';
COMMENT ON COLUMN "public"."local_connector"."user_name" IS '用户名称';
COMMENT ON COLUMN "public"."local_connector"."version" IS '版本号';
COMMENT ON COLUMN "public"."local_connector"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."local_connector"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."local_connector"."location_url" IS '存放路径';
COMMENT ON TABLE "public"."local_connector" IS '我的连接器';

-- ----------------------------
-- Table structure for meta_table
-- ----------------------------
DROP TABLE IF EXISTS "public"."meta_table";
CREATE TABLE "public"."meta_table" (
  "table_id" int8 NOT NULL DEFAULT nextval('meta_table_table_id_seq'::regclass),
  "table_name" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "table_alias" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "comment" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6),
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "type" int2
)
;
COMMENT ON COLUMN "public"."meta_table"."table_id" IS '主键';
COMMENT ON COLUMN "public"."meta_table"."table_name" IS '表名称';
COMMENT ON COLUMN "public"."meta_table"."table_alias" IS '表别名';
COMMENT ON COLUMN "public"."meta_table"."comment" IS '表详情';
COMMENT ON COLUMN "public"."meta_table"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."meta_table"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."meta_table"."type" IS '1.企业微信';
COMMENT ON TABLE "public"."meta_table" IS 'cdp表';

-- ----------------------------
-- Table structure for meta_table_column
-- ----------------------------
DROP TABLE IF EXISTS "public"."meta_table_column";
CREATE TABLE "public"."meta_table_column" (
  "table_column_id" int8 NOT NULL DEFAULT nextval('meta_table_column_table_column_id_seq'::regclass),
  "table_id" int8,
  "table_column_name" varchar(255) COLLATE "pg_catalog"."default",
  "table_column_alias" varchar(255) COLLATE "pg_catalog"."default",
  "comment" text COLLATE "pg_catalog"."default",
  "is_pk" int4 DEFAULT 0,
  "data_type" int4,
  "short_data_type" int4,
  "data_type_name" varchar(50) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."meta_table_column"."table_id" IS '关联表ID';
COMMENT ON COLUMN "public"."meta_table_column"."table_column_name" IS '列名称';
COMMENT ON COLUMN "public"."meta_table_column"."table_column_alias" IS '列别名';
COMMENT ON COLUMN "public"."meta_table_column"."comment" IS '列注释';
COMMENT ON COLUMN "public"."meta_table_column"."is_pk" IS '是否主键 1:是';
COMMENT ON COLUMN "public"."meta_table_column"."data_type" IS '数据类型';
COMMENT ON COLUMN "public"."meta_table_column"."short_data_type" IS '短数据类型 ';
COMMENT ON COLUMN "public"."meta_table_column"."data_type_name" IS '数据类型名称';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_user";
CREATE TABLE "public"."sys_user" (
  "user_id" int8 NOT NULL DEFAULT nextval('sys_user_user_id_seq'::regclass),
  "user_name" varchar(100) COLLATE "pg_catalog"."default",
  "nick_name" varchar(100) COLLATE "pg_catalog"."default",
  "password" varchar(100) COLLATE "pg_catalog"."default",
  "salt" varchar(50) COLLATE "pg_catalog"."default",
  "type" int2,
  "email" varchar(100) COLLATE "pg_catalog"."default",
  "phone" varchar(20) COLLATE "pg_catalog"."default",
  "create_time" timestamp(0),
  "update_time" timestamp(0),
  "avatar" varchar(200) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."sys_user"."user_id" IS '用户ID主键';
COMMENT ON COLUMN "public"."sys_user"."user_name" IS '用户名称';
COMMENT ON COLUMN "public"."sys_user"."nick_name" IS '昵称';
COMMENT ON COLUMN "public"."sys_user"."password" IS '密码';
COMMENT ON COLUMN "public"."sys_user"."salt" IS '密码盐值';
COMMENT ON COLUMN "public"."sys_user"."type" IS '1:客户 2:开发者 3:管理者';
COMMENT ON COLUMN "public"."sys_user"."email" IS '邮箱';
COMMENT ON COLUMN "public"."sys_user"."phone" IS '手机号';
COMMENT ON COLUMN "public"."sys_user"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."sys_user"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."sys_user"."avatar" IS '头像地址';
COMMENT ON TABLE "public"."sys_user" IS '用户表';

-- ----------------------------
-- Table structure for wecom_callback_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_callback_config";
CREATE TABLE "public"."wecom_callback_config" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_callback_config_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "event_code" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "event_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "event_description" varchar(500) COLLATE "pg_catalog"."default",
  "enabled" int2 DEFAULT 1,
  "callback_url" varchar(500) COLLATE "pg_catalog"."default",
  "remark" varchar(500) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."wecom_callback_config"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_callback_config"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_callback_config"."event_code" IS '事件编码';
COMMENT ON COLUMN "public"."wecom_callback_config"."event_name" IS '事件名称';
COMMENT ON COLUMN "public"."wecom_callback_config"."event_description" IS '事件说明';
COMMENT ON COLUMN "public"."wecom_callback_config"."enabled" IS '是否启用 0-禁用 1-启用';
COMMENT ON COLUMN "public"."wecom_callback_config"."callback_url" IS '回调URL';
COMMENT ON COLUMN "public"."wecom_callback_config"."remark" IS '备注';
COMMENT ON COLUMN "public"."wecom_callback_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_callback_config"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."wecom_callback_config" IS '企业微信回调配置表';

-- ----------------------------
-- Table structure for wecom_callback_log
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_callback_log";
CREATE TABLE "public"."wecom_callback_log" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_callback_log_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "event_code" varchar(100) COLLATE "pg_catalog"."default",
  "event_name" varchar(200) COLLATE "pg_catalog"."default",
  "request_body" text COLLATE "pg_catalog"."default",
  "process_status" int4 DEFAULT 0,
  "error_message" text COLLATE "pg_catalog"."default",
  "response_body" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."wecom_callback_log"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_callback_log"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_callback_log"."event_code" IS '事件编码';
COMMENT ON COLUMN "public"."wecom_callback_log"."event_name" IS '事件名称';
COMMENT ON COLUMN "public"."wecom_callback_log"."request_body" IS '接收到的消息内容';
COMMENT ON COLUMN "public"."wecom_callback_log"."process_status" IS '处理结果 0-待处理 1-成功 2-失败';
COMMENT ON COLUMN "public"."wecom_callback_log"."error_message" IS '错误信息';
COMMENT ON COLUMN "public"."wecom_callback_log"."response_body" IS '响应内容';
COMMENT ON COLUMN "public"."wecom_callback_log"."create_time" IS '创建时间';
COMMENT ON TABLE "public"."wecom_callback_log" IS '企业微信回调日志表';

-- ----------------------------
-- Table structure for wecom_cdp_tag_mapping
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_cdp_tag_mapping";
CREATE TABLE "public"."wecom_cdp_tag_mapping" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_cdp_tag_mapping_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "cdp_tag_name" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "cdp_tag_value" varchar(500) COLLATE "pg_catalog"."default",
  "wecom_tag_group_id" varchar(64) COLLATE "pg_catalog"."default",
  "wecom_tag_group_name" varchar(100) COLLATE "pg_catalog"."default",
  "wecom_tag_id" varchar(64) COLLATE "pg_catalog"."default",
  "wecom_tag_name" varchar(100) COLLATE "pg_catalog"."default",
  "sync_time" timestamp(6),
  "sync_status" int4 DEFAULT 0,
  "sync_error" varchar(500) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."cdp_tag_name" IS 'CDP标签名称';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."cdp_tag_value" IS 'CDP标签值';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."wecom_tag_group_id" IS '企业微信标签组ID';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."wecom_tag_group_name" IS '企业微信标签组名称';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."wecom_tag_id" IS '企业微信标签ID';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."wecom_tag_name" IS '企业微信标签名称';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."sync_time" IS '同步时间';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."sync_status" IS '同步状态：0-未同步，1-已同步，2-同步失败';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."sync_error" IS '同步错误信息';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_cdp_tag_mapping"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_cdp_tag_mapping" IS 'CDP标签映射表';

-- ----------------------------
-- Table structure for wecom_contact_rules
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_contact_rules";
CREATE TABLE "public"."wecom_contact_rules" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_contact_rules_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "rule_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "rule_name" varchar(200) COLLATE "pg_catalog"."default",
  "range" text COLLATE "pg_catalog"."default",
  "add_time" int8,
  "admin_list" varchar(500) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_contact_rules"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_contact_rules"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_contact_rules"."rule_id" IS '规则id';
COMMENT ON COLUMN "public"."wecom_contact_rules"."rule_name" IS '规则名称';
COMMENT ON COLUMN "public"."wecom_contact_rules"."range" IS '规则范围，JSON格式';
COMMENT ON COLUMN "public"."wecom_contact_rules"."add_time" IS '规则添加时间';
COMMENT ON COLUMN "public"."wecom_contact_rules"."admin_list" IS '管理员userid列表';
COMMENT ON COLUMN "public"."wecom_contact_rules"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_contact_rules"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_contact_rules"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_contact_rules" IS '客户联系规则表';

-- ----------------------------
-- Table structure for wecom_corp
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_corp";
CREATE TABLE "public"."wecom_corp" (
  "wecome_corp_id" int8 NOT NULL DEFAULT nextval('wecom_corp_wecome_corp_id_seq'::regclass),
  "company_name" varchar(255) COLLATE "pg_catalog"."default",
  "corp_id" varchar(255) COLLATE "pg_catalog"."default",
  "corp_secret" varchar(255) COLLATE "pg_catalog"."default",
  "logo_url" varchar(255) COLLATE "pg_catalog"."default",
  "create_time" timestamp(0),
  "update_time" timestamp(0)
)
;
COMMENT ON COLUMN "public"."wecom_corp"."wecome_corp_id" IS '企业微信账户信息表';
COMMENT ON COLUMN "public"."wecom_corp"."company_name" IS '企业名称';
COMMENT ON COLUMN "public"."wecom_corp"."corp_id" IS '企业ID';
COMMENT ON COLUMN "public"."wecom_corp"."corp_secret" IS '通讯录密钥';
COMMENT ON COLUMN "public"."wecom_corp"."logo_url" IS '企业logo';
COMMENT ON COLUMN "public"."wecom_corp"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_corp"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."wecom_corp" IS '企业微信账户表';

-- ----------------------------
-- Table structure for wecom_corp_agent
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_corp_agent";
CREATE TABLE "public"."wecom_corp_agent" (
  "wecom_corp_agent_id" int8 NOT NULL DEFAULT nextval('wecom_corp_agent_wecom_corp_agent_id_seq'::regclass),
  "wecome_corp_id" int8,
  "agent_secret" varchar(255) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."wecom_corp_agent"."wecom_corp_agent_id" IS '企业微信应用ID';
COMMENT ON COLUMN "public"."wecom_corp_agent"."wecome_corp_id" IS '企业微信账户信息表ID';
COMMENT ON COLUMN "public"."wecom_corp_agent"."agent_secret" IS '应该密钥';
COMMENT ON TABLE "public"."wecom_corp_agent" IS '企业微信agent账户';

-- ----------------------------
-- Table structure for wecom_corp_tags
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_corp_tags";
CREATE TABLE "public"."wecom_corp_tags" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_corp_tags_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "tag_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "tag_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "tag_type" varchar(20) COLLATE "pg_catalog"."default",
  "group_id" varchar(64) COLLATE "pg_catalog"."default",
  "group_name" varchar(100) COLLATE "pg_catalog"."default",
  "tag_order" int4 DEFAULT 0,
  "create_time" int8,
  "deleted" int2 DEFAULT 0,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."wecom_corp_tags"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_corp_tags"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_corp_tags"."tag_id" IS '标签ID';
COMMENT ON COLUMN "public"."wecom_corp_tags"."tag_name" IS '标签名称';
COMMENT ON COLUMN "public"."wecom_corp_tags"."tag_type" IS '标签类型';
COMMENT ON COLUMN "public"."wecom_corp_tags"."group_id" IS '标签组id';
COMMENT ON COLUMN "public"."wecom_corp_tags"."group_name" IS '标签组名称';
COMMENT ON COLUMN "public"."wecom_corp_tags"."tag_order" IS '标签次序';
COMMENT ON COLUMN "public"."wecom_corp_tags"."create_time" IS '标签创建时间';
COMMENT ON COLUMN "public"."wecom_corp_tags"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."wecom_corp_tags"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."wecom_corp_tags" IS '企业标签表';

-- ----------------------------
-- Table structure for wecom_customer_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_customer_groups";
CREATE TABLE "public"."wecom_customer_groups" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_customer_groups_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "chat_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(200) COLLATE "pg_catalog"."default",
  "owner" varchar(64) COLLATE "pg_catalog"."default",
  "create_time" int8,
  "notice" varchar(1000) COLLATE "pg_catalog"."default",
  "member_count" int4,
  "admin_list" varchar(500) COLLATE "pg_catalog"."default",
  "chat_announcement" varchar(2000) COLLATE "pg_catalog"."default",
  "group_qrcode" varchar(500) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_customer_groups"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_customer_groups"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_customer_groups"."chat_id" IS '客户群ID';
COMMENT ON COLUMN "public"."wecom_customer_groups"."name" IS '群名称';
COMMENT ON COLUMN "public"."wecom_customer_groups"."owner" IS '群主ID';
COMMENT ON COLUMN "public"."wecom_customer_groups"."create_time" IS '群创建时间';
COMMENT ON COLUMN "public"."wecom_customer_groups"."notice" IS '群公告';
COMMENT ON COLUMN "public"."wecom_customer_groups"."member_count" IS '群成员数';
COMMENT ON COLUMN "public"."wecom_customer_groups"."admin_list" IS '群管理员ID列表';
COMMENT ON COLUMN "public"."wecom_customer_groups"."chat_announcement" IS '群公告';
COMMENT ON COLUMN "public"."wecom_customer_groups"."group_qrcode" IS '群二维码';
COMMENT ON COLUMN "public"."wecom_customer_groups"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_customer_groups"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_customer_groups" IS '客户群基本信息表';

-- ----------------------------
-- Table structure for wecom_departments
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_departments";
CREATE TABLE "public"."wecom_departments" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_departments_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "department_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "name_en" varchar(255) COLLATE "pg_catalog"."default",
  "parent_id" varchar(64) COLLATE "pg_catalog"."default",
  "order" int4,
  "department_leader" varchar(500) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_departments"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_departments"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_departments"."department_id" IS '部门ID';
COMMENT ON COLUMN "public"."wecom_departments"."name" IS '部门名称';
COMMENT ON COLUMN "public"."wecom_departments"."name_en" IS '部门英文名称';
COMMENT ON COLUMN "public"."wecom_departments"."parent_id" IS '父部门ID';
COMMENT ON COLUMN "public"."wecom_departments"."order" IS '在父部门中的次序值';
COMMENT ON COLUMN "public"."wecom_departments"."department_leader" IS '部门负责人列表';
COMMENT ON COLUMN "public"."wecom_departments"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_departments"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_departments"."deleted" IS '是否删除 0-否 1-是';
COMMENT ON TABLE "public"."wecom_departments" IS '企业微信部门信息表';

-- ----------------------------
-- Table structure for wecom_employee_details
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_employee_details";
CREATE TABLE "public"."wecom_employee_details" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_employee_details_id_seq'::regclass),
  "employee_id" int8 NOT NULL,
  "userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "english_name" varchar(100) COLLATE "pg_catalog"."default",
  "address" varchar(500) COLLATE "pg_catalog"."default",
  "direct_leader" varchar(500) COLLATE "pg_catalog"."default",
  "main_department" varchar(64) COLLATE "pg_catalog"."default",
  "hide" int2 DEFAULT 0,
  "senior" int2 DEFAULT 0,
  "join_time" int8,
  "first_party" int2 DEFAULT 0,
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_employee_details"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_employee_details"."employee_id" IS '员工基本信息表ID';
COMMENT ON COLUMN "public"."wecom_employee_details"."userid" IS '成员UserID';
COMMENT ON COLUMN "public"."wecom_employee_details"."english_name" IS '英文名';
COMMENT ON COLUMN "public"."wecom_employee_details"."address" IS '地址';
COMMENT ON COLUMN "public"."wecom_employee_details"."direct_leader" IS '直属上级UserID列表';
COMMENT ON COLUMN "public"."wecom_employee_details"."main_department" IS '主部门';
COMMENT ON COLUMN "public"."wecom_employee_details"."hide" IS '是否隐藏：0-否，1-是';
COMMENT ON COLUMN "public"."wecom_employee_details"."senior" IS '是否为高管：0-否，1-是';
COMMENT ON COLUMN "public"."wecom_employee_details"."join_time" IS '加入企业时间';
COMMENT ON COLUMN "public"."wecom_employee_details"."first_party" IS '是否为党务：0-否，1-是';
COMMENT ON COLUMN "public"."wecom_employee_details"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_employee_details"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_employee_details"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_employee_details" IS '企业微信员工详细信息表';

-- ----------------------------
-- Table structure for wecom_employees
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_employees";
CREATE TABLE "public"."wecom_employees" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_employees_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "alias" varchar(100) COLLATE "pg_catalog"."default",
  "mobile" varchar(50) COLLATE "pg_catalog"."default",
  "email" varchar(100) COLLATE "pg_catalog"."default",
  "department" varchar(1000) COLLATE "pg_catalog"."default",
  "order_in_dept" varchar(1000) COLLATE "pg_catalog"."default",
  "position" varchar(100) COLLATE "pg_catalog"."default",
  "gender" varchar(10) COLLATE "pg_catalog"."default",
  "avatar" varchar(500) COLLATE "pg_catalog"."default",
  "telephone" varchar(50) COLLATE "pg_catalog"."default",
  "enable" int2 DEFAULT 1,
  "is_leader" int2 DEFAULT 0,
  "extattr" text COLLATE "pg_catalog"."default",
  "status" int4 DEFAULT 1,
  "qr_code" varchar(500) COLLATE "pg_catalog"."default",
  "external_profile" text COLLATE "pg_catalog"."default",
  "external_position" varchar(100) COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_employees"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_employees"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_employees"."userid" IS '成员UserID';
COMMENT ON COLUMN "public"."wecom_employees"."name" IS '成员名称';
COMMENT ON COLUMN "public"."wecom_employees"."alias" IS '成员别名';
COMMENT ON COLUMN "public"."wecom_employees"."mobile" IS '手机号码';
COMMENT ON COLUMN "public"."wecom_employees"."email" IS '邮箱';
COMMENT ON COLUMN "public"."wecom_employees"."department" IS '所属部门ID列表';
COMMENT ON COLUMN "public"."wecom_employees"."order_in_dept" IS '部门内的排序值';
COMMENT ON COLUMN "public"."wecom_employees"."position" IS '职位信息';
COMMENT ON COLUMN "public"."wecom_employees"."gender" IS '性别，0表示未定义，1表示男性，2表示女性';
COMMENT ON COLUMN "public"."wecom_employees"."avatar" IS '头像url';
COMMENT ON COLUMN "public"."wecom_employees"."telephone" IS '座机';
COMMENT ON COLUMN "public"."wecom_employees"."enable" IS '激活状态: 1表示已激活，2表示已禁用，4表示未激活';
COMMENT ON COLUMN "public"."wecom_employees"."is_leader" IS '是否为部门负责人：0-否，1-是';
COMMENT ON COLUMN "public"."wecom_employees"."extattr" IS '扩展属性';
COMMENT ON COLUMN "public"."wecom_employees"."status" IS '激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业';
COMMENT ON COLUMN "public"."wecom_employees"."qr_code" IS '员工个人二维码';
COMMENT ON COLUMN "public"."wecom_employees"."external_profile" IS '成员对外属性，JSON格式';
COMMENT ON COLUMN "public"."wecom_employees"."external_position" IS '对外职务';
COMMENT ON COLUMN "public"."wecom_employees"."create_time" IS '创建时间';
COMMENT ON COLUMN "public"."wecom_employees"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_employees"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_employees" IS '企业微信员工基本信息表';

-- ----------------------------
-- Table structure for wecom_external_contacts_details
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_external_contacts_details";
CREATE TABLE "public"."wecom_external_contacts_details" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_external_contacts_details_id_seq'::regclass),
  "external_profile" text COLLATE "pg_catalog"."default",
  "external_userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "name" varchar(100) COLLATE "pg_catalog"."default",
  "position" varchar(100) COLLATE "pg_catalog"."default",
  "avatar" varchar(500) COLLATE "pg_catalog"."default",
  "type" int4,
  "gender" int4,
  "unionid" varchar(64) COLLATE "pg_catalog"."default",
  "corp_name" varchar(100) COLLATE "pg_catalog"."default",
  "corp_full_name" varchar(200) COLLATE "pg_catalog"."default",
  "follow_user" varchar(64) COLLATE "pg_catalog"."default",
  "add_time" int8,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."external_profile" IS '对外属性信息，JSON格式';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."external_userid" IS '外部联系人userid';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."name" IS '外部联系人的昵称';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."position" IS '外部联系人的职位';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."avatar" IS '外部联系人头像';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."type" IS '外部联系人的类型，1表示该企业微信用户是从微信用户中转换而来的，2表示该企业微信用户是直接通过企业微信创建';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."gender" IS '外部联系人性别，0表示未定义，1表示男性，2表示女性';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."unionid" IS '外部联系人在微信开放平台的唯一身份标识';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."corp_name" IS '外部联系人所属企业的简称';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."corp_full_name" IS '外部联系人所属企业的全称';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."follow_user" IS '添加此外部联系人的成员userid';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."add_time" IS '添加此外部联系人的时间';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_external_contacts_details"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_external_contacts_details" IS '外部联系人详情表';

-- ----------------------------
-- Table structure for wecom_external_contacts_mapping
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_external_contacts_mapping";
CREATE TABLE "public"."wecom_external_contacts_mapping" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_external_contacts_mapping_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "external_userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "remark" varchar(200) COLLATE "pg_catalog"."default",
  "description" varchar(500) COLLATE "pg_catalog"."default",
  "create_time" int8,
  "remark_corp_name" varchar(100) COLLATE "pg_catalog"."default",
  "remark_mobiles" varchar(500) COLLATE "pg_catalog"."default",
  "add_way" int4,
  "state" varchar(100) COLLATE "pg_catalog"."default",
  "unionid" varchar(64) COLLATE "pg_catalog"."default",
  "operate_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."userid" IS '添加了外部联系人的成员UserID';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."external_userid" IS '外部联系人userid';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."remark" IS '外部联系人昵称';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."description" IS '外部联系人描述';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."create_time" IS '添加外部联系人的时间';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."remark_corp_name" IS '外部联系人所属企业名称';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."remark_mobiles" IS '外部联系人手机号';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."add_way" IS '添加方式';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."state" IS '企业自定义的state参数';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."unionid" IS '外部联系人在微信开放平台的唯一身份标识';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."operate_time" IS '操作时间';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_external_contacts_mapping"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_external_contacts_mapping" IS '外部联系人映射表';

-- ----------------------------
-- Table structure for wecom_external_tags
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_external_tags";
CREATE TABLE "public"."wecom_external_tags" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_external_tags_id_seq'::regclass),
  "external_userid" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "user_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "tag_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "tag_name" varchar(100) COLLATE "pg_catalog"."default",
  "tag_type" varchar(20) COLLATE "pg_catalog"."default",
  "group_id" varchar(64) COLLATE "pg_catalog"."default",
  "group_name" varchar(100) COLLATE "pg_catalog"."default",
  "create_time" int8,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_external_tags"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_external_tags"."external_userid" IS '外部联系人userid';
COMMENT ON COLUMN "public"."wecom_external_tags"."user_id" IS '成员userid';
COMMENT ON COLUMN "public"."wecom_external_tags"."tag_id" IS '企业标签的id';
COMMENT ON COLUMN "public"."wecom_external_tags"."tag_name" IS '企业标签的名称';
COMMENT ON COLUMN "public"."wecom_external_tags"."tag_type" IS '标签类型';
COMMENT ON COLUMN "public"."wecom_external_tags"."group_id" IS '标签组id';
COMMENT ON COLUMN "public"."wecom_external_tags"."group_name" IS '标签组名称';
COMMENT ON COLUMN "public"."wecom_external_tags"."create_time" IS '标签打上/移除的时间';
COMMENT ON COLUMN "public"."wecom_external_tags"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_external_tags"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_external_tags" IS '外部联系人标签关联表';

-- ----------------------------
-- Table structure for wecom_group_members
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_group_members";
CREATE TABLE "public"."wecom_group_members" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_group_members_id_seq'::regclass),
  "chat_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "member_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "type" int4,
  "unionid" varchar(64) COLLATE "pg_catalog"."default",
  "join_time" int8,
  "join_scene" int4,
  "invitor" varchar(64) COLLATE "pg_catalog"."default",
  "group_nickname" varchar(100) COLLATE "pg_catalog"."default",
  "name" varchar(100) COLLATE "pg_catalog"."default",
  "user_id" varchar(64) COLLATE "pg_catalog"."default",
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" int2 DEFAULT 0
)
;
COMMENT ON COLUMN "public"."wecom_group_members"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_group_members"."chat_id" IS '客户群ID';
COMMENT ON COLUMN "public"."wecom_group_members"."member_id" IS '成员ID';
COMMENT ON COLUMN "public"."wecom_group_members"."type" IS '成员类型，1-企业成员，2-外部联系人';
COMMENT ON COLUMN "public"."wecom_group_members"."unionid" IS '微信unionid';
COMMENT ON COLUMN "public"."wecom_group_members"."join_time" IS '加入群聊时间';
COMMENT ON COLUMN "public"."wecom_group_members"."join_scene" IS '加入群聊的途径';
COMMENT ON COLUMN "public"."wecom_group_members"."invitor" IS '邀请者的userid';
COMMENT ON COLUMN "public"."wecom_group_members"."group_nickname" IS '在群里的昵称';
COMMENT ON COLUMN "public"."wecom_group_members"."name" IS '成员名称';
COMMENT ON COLUMN "public"."wecom_group_members"."user_id" IS '企业成员的userid';
COMMENT ON COLUMN "public"."wecom_group_members"."update_time" IS '更新时间';
COMMENT ON COLUMN "public"."wecom_group_members"."deleted" IS '是否删除';
COMMENT ON TABLE "public"."wecom_group_members" IS '客户群成员表';

-- ----------------------------
-- Table structure for wecom_master
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_master";
CREATE TABLE "public"."wecom_master" (
  "wecom_mater_id" int8 NOT NULL,
  "userId" varchar(50) COLLATE "pg_catalog"."default",
  "unionId" varchar(50) COLLATE "pg_catalog"."default",
  "external_userid" varchar(50) COLLATE "pg_catalog"."default"
)
;
COMMENT ON COLUMN "public"."wecom_master"."wecom_mater_id" IS '企业微信主表ID';
COMMENT ON COLUMN "public"."wecom_master"."userId" IS '用户ID';
COMMENT ON COLUMN "public"."wecom_master"."unionId" IS '微信唯一ID';
COMMENT ON COLUMN "public"."wecom_master"."external_userid" IS '外部联系人userid';

-- ----------------------------
-- Table structure for wecom_sync_logs
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_sync_logs";
CREATE TABLE "public"."wecom_sync_logs" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_sync_logs_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "sync_type" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "sync_status" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
  "start_time" timestamp(6) NOT NULL,
  "end_time" timestamp(6),
  "total_count" int4 DEFAULT 0,
  "success_count" int4 DEFAULT 0,
  "fail_count" int4 DEFAULT 0,
  "error_message" text COLLATE "pg_catalog"."default",
  "create_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."wecom_sync_logs"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_sync_logs"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_sync_logs"."sync_type" IS '同步类型：DEPARTMENT-部门，EMPLOYEE-员工';
COMMENT ON COLUMN "public"."wecom_sync_logs"."sync_status" IS '同步状态：SUCCESS-成功，FAILED-失败，PARTIAL-部分成功';
COMMENT ON COLUMN "public"."wecom_sync_logs"."start_time" IS '开始时间';
COMMENT ON COLUMN "public"."wecom_sync_logs"."end_time" IS '结束时间';
COMMENT ON COLUMN "public"."wecom_sync_logs"."total_count" IS '总记录数';
COMMENT ON COLUMN "public"."wecom_sync_logs"."success_count" IS '成功数量';
COMMENT ON COLUMN "public"."wecom_sync_logs"."fail_count" IS '失败数量';
COMMENT ON COLUMN "public"."wecom_sync_logs"."error_message" IS '错误信息';
COMMENT ON TABLE "public"."wecom_sync_logs" IS '企业微信数据同步日志表';

-- ----------------------------
-- Table structure for wecom_tag_groups
-- ----------------------------
DROP TABLE IF EXISTS "public"."wecom_tag_groups";
CREATE TABLE "public"."wecom_tag_groups" (
  "id" int8 NOT NULL DEFAULT nextval('wecom_tag_groups_id_seq'::regclass),
  "wecom_corp_id" int8 NOT NULL,
  "group_id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
  "group_name" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
  "group_order" int4 DEFAULT 0,
  "create_time" int8,
  "deleted" int2 DEFAULT 0,
  "update_time" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
COMMENT ON COLUMN "public"."wecom_tag_groups"."id" IS '主键ID';
COMMENT ON COLUMN "public"."wecom_tag_groups"."wecom_corp_id" IS '企业微信账户ID';
COMMENT ON COLUMN "public"."wecom_tag_groups"."group_id" IS '标签组id';
COMMENT ON COLUMN "public"."wecom_tag_groups"."group_name" IS '标签组名称';
COMMENT ON COLUMN "public"."wecom_tag_groups"."group_order" IS '标签组次序';
COMMENT ON COLUMN "public"."wecom_tag_groups"."create_time" IS '标签组创建时间';
COMMENT ON COLUMN "public"."wecom_tag_groups"."deleted" IS '是否删除';
COMMENT ON COLUMN "public"."wecom_tag_groups"."update_time" IS '更新时间';
COMMENT ON TABLE "public"."wecom_tag_groups" IS '标签组信息表';

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."conn_docker_service_enum_conn_docker_service_enum_id_seq"
OWNED BY "public"."conn_docker_service_enum"."conn_docker_service_enum_id";
SELECT setval('"public"."conn_docker_service_enum_conn_docker_service_enum_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."local_connector_local_connector_id_seq"
OWNED BY "public"."local_connector"."local_connector_id";
SELECT setval('"public"."local_connector_local_connector_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."meta_table_column_table_column_id_seq"
OWNED BY "public"."meta_table_column"."table_column_id";
SELECT setval('"public"."meta_table_column_table_column_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."meta_table_table_id_seq"
OWNED BY "public"."meta_table"."table_id";
SELECT setval('"public"."meta_table_table_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."sys_user_user_id_seq"
OWNED BY "public"."sys_user"."user_id";
SELECT setval('"public"."sys_user_user_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_callback_config_id_seq"
OWNED BY "public"."wecom_callback_config"."id";
SELECT setval('"public"."wecom_callback_config_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_callback_log_id_seq"
OWNED BY "public"."wecom_callback_log"."id";
SELECT setval('"public"."wecom_callback_log_id_seq"', 2, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_cdp_tag_mapping_id_seq"
OWNED BY "public"."wecom_cdp_tag_mapping"."id";
SELECT setval('"public"."wecom_cdp_tag_mapping_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_contact_rules_id_seq"
OWNED BY "public"."wecom_contact_rules"."id";
SELECT setval('"public"."wecom_contact_rules_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_corp_agent_wecom_corp_agent_id_seq"
OWNED BY "public"."wecom_corp_agent"."wecom_corp_agent_id";
SELECT setval('"public"."wecom_corp_agent_wecom_corp_agent_id_seq"', 1, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_corp_tags_id_seq"
OWNED BY "public"."wecom_corp_tags"."id";
SELECT setval('"public"."wecom_corp_tags_id_seq"', 3, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_corp_wecome_corp_id_seq"
OWNED BY "public"."wecom_corp"."wecome_corp_id";
SELECT setval('"public"."wecom_corp_wecome_corp_id_seq"', 8, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_customer_groups_id_seq"
OWNED BY "public"."wecom_customer_groups"."id";
SELECT setval('"public"."wecom_customer_groups_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_departments_id_seq"
OWNED BY "public"."wecom_departments"."id";
SELECT setval('"public"."wecom_departments_id_seq"', 4, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_employee_details_id_seq"
OWNED BY "public"."wecom_employee_details"."id";
SELECT setval('"public"."wecom_employee_details_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_employees_id_seq"
OWNED BY "public"."wecom_employees"."id";
SELECT setval('"public"."wecom_employees_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_external_contacts_details_id_seq"
OWNED BY "public"."wecom_external_contacts_details"."id";
SELECT setval('"public"."wecom_external_contacts_details_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_external_contacts_mapping_id_seq"
OWNED BY "public"."wecom_external_contacts_mapping"."id";
SELECT setval('"public"."wecom_external_contacts_mapping_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_external_tags_id_seq"
OWNED BY "public"."wecom_external_tags"."id";
SELECT setval('"public"."wecom_external_tags_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_group_members_id_seq"
OWNED BY "public"."wecom_group_members"."id";
SELECT setval('"public"."wecom_group_members_id_seq"', 1, false);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_sync_logs_id_seq"
OWNED BY "public"."wecom_sync_logs"."id";
SELECT setval('"public"."wecom_sync_logs_id_seq"', 24, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."wecom_tag_groups_id_seq"
OWNED BY "public"."wecom_tag_groups"."id";
SELECT setval('"public"."wecom_tag_groups_id_seq"', 1, true);

-- ----------------------------
-- Primary Key structure for table conn_docker_service_enum
-- ----------------------------
ALTER TABLE "public"."conn_docker_service_enum" ADD CONSTRAINT "conn_docker_service_enum_pkey" PRIMARY KEY ("conn_docker_service_enum_id");

-- ----------------------------
-- Primary Key structure for table local_connector
-- ----------------------------
ALTER TABLE "public"."local_connector" ADD CONSTRAINT "local_connector_pkey" PRIMARY KEY ("local_connector_id");

-- ----------------------------
-- Primary Key structure for table meta_table
-- ----------------------------
ALTER TABLE "public"."meta_table" ADD CONSTRAINT "_copy_1" PRIMARY KEY ("table_id");

-- ----------------------------
-- Indexes structure for table meta_table_column
-- ----------------------------
CREATE UNIQUE INDEX "cdp_id_name" ON "public"."meta_table_column" USING btree (
  "table_id" "pg_catalog"."int8_ops" ASC NULLS LAST,
  "table_column_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table meta_table_column
-- ----------------------------
ALTER TABLE "public"."meta_table_column" ADD CONSTRAINT "cdp_table_column_pkey" PRIMARY KEY ("table_column_id");

-- ----------------------------
-- Primary Key structure for table sys_user
-- ----------------------------
ALTER TABLE "public"."sys_user" ADD CONSTRAINT "sys_user_pkey" PRIMARY KEY ("user_id");

-- ----------------------------
-- Indexes structure for table wecom_callback_config
-- ----------------------------
CREATE INDEX "idx_callback_config_corp" ON "public"."wecom_callback_config" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_callback_config_enabled" ON "public"."wecom_callback_config" USING btree (
  "enabled" "pg_catalog"."int2_ops" ASC NULLS LAST
);
CREATE INDEX "idx_callback_config_event_code" ON "public"."wecom_callback_config" USING btree (
  "event_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_callback_config
-- ----------------------------
ALTER TABLE "public"."wecom_callback_config" ADD CONSTRAINT "wecom_callback_config_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_callback_log
-- ----------------------------
CREATE INDEX "idx_callback_log_corp" ON "public"."wecom_callback_log" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_callback_log_create_time" ON "public"."wecom_callback_log" USING btree (
  "create_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_callback_log_event_code" ON "public"."wecom_callback_log" USING btree (
  "event_code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_callback_log
-- ----------------------------
ALTER TABLE "public"."wecom_callback_log" ADD CONSTRAINT "wecom_callback_log_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_cdp_tag_mapping
-- ----------------------------
CREATE INDEX "idx_cdp_tag_mapping_cdp_name" ON "public"."wecom_cdp_tag_mapping" USING btree (
  "cdp_tag_name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_cdp_tag_mapping_corp" ON "public"."wecom_cdp_tag_mapping" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_cdp_tag_mapping_wecom_tag_id" ON "public"."wecom_cdp_tag_mapping" USING btree (
  "wecom_tag_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_cdp_tag_mapping
-- ----------------------------
ALTER TABLE "public"."wecom_cdp_tag_mapping" ADD CONSTRAINT "wecom_cdp_tag_mapping_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_contact_rules
-- ----------------------------
CREATE INDEX "idx_contact_rules_corp" ON "public"."wecom_contact_rules" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_contact_rules_rule_id" ON "public"."wecom_contact_rules" USING btree (
  "rule_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_contact_rules
-- ----------------------------
ALTER TABLE "public"."wecom_contact_rules" ADD CONSTRAINT "wecom_contact_rules_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table wecom_corp
-- ----------------------------
ALTER TABLE "public"."wecom_corp" ADD CONSTRAINT "wecom_corp_pkey" PRIMARY KEY ("wecome_corp_id");

-- ----------------------------
-- Primary Key structure for table wecom_corp_agent
-- ----------------------------
ALTER TABLE "public"."wecom_corp_agent" ADD CONSTRAINT "wecom_corp_agent_pkey" PRIMARY KEY ("wecom_corp_agent_id");

-- ----------------------------
-- Indexes structure for table wecom_corp_tags
-- ----------------------------
CREATE INDEX "idx_corp_tags_corp" ON "public"."wecom_corp_tags" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_corp_tags_group_id" ON "public"."wecom_corp_tags" USING btree (
  "group_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_corp_tags_tag_id" ON "public"."wecom_corp_tags" USING btree (
  "tag_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_corp_tags
-- ----------------------------
ALTER TABLE "public"."wecom_corp_tags" ADD CONSTRAINT "wecom_corp_tags_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_customer_groups
-- ----------------------------
CREATE INDEX "idx_customer_groups_chat_id" ON "public"."wecom_customer_groups" USING btree (
  "chat_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_customer_groups_corp" ON "public"."wecom_customer_groups" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_customer_groups_owner" ON "public"."wecom_customer_groups" USING btree (
  "owner" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_customer_groups
-- ----------------------------
ALTER TABLE "public"."wecom_customer_groups" ADD CONSTRAINT "wecom_customer_groups_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_departments
-- ----------------------------
CREATE INDEX "idx_departments_corp" ON "public"."wecom_departments" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_departments_dept_id" ON "public"."wecom_departments" USING btree (
  "department_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_departments_parent_id" ON "public"."wecom_departments" USING btree (
  "parent_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_departments
-- ----------------------------
ALTER TABLE "public"."wecom_departments" ADD CONSTRAINT "wecom_departments_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_employee_details
-- ----------------------------
CREATE INDEX "idx_employee_details_emp_id" ON "public"."wecom_employee_details" USING btree (
  "employee_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_employee_details_userid" ON "public"."wecom_employee_details" USING btree (
  "userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_employee_details
-- ----------------------------
ALTER TABLE "public"."wecom_employee_details" ADD CONSTRAINT "wecom_employee_details_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_employees
-- ----------------------------
CREATE INDEX "idx_employees_corp" ON "public"."wecom_employees" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_employees_dept" ON "public"."wecom_employees" USING btree (
  "department" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_employees_mobile" ON "public"."wecom_employees" USING btree (
  "mobile" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_employees_userid" ON "public"."wecom_employees" USING btree (
  "userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_employees
-- ----------------------------
ALTER TABLE "public"."wecom_employees" ADD CONSTRAINT "wecom_employees_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_external_contacts_details
-- ----------------------------
CREATE INDEX "idx_external_details_external_userid" ON "public"."wecom_external_contacts_details" USING btree (
  "external_userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_external_details_follow_user" ON "public"."wecom_external_contacts_details" USING btree (
  "follow_user" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_external_contacts_details
-- ----------------------------
ALTER TABLE "public"."wecom_external_contacts_details" ADD CONSTRAINT "wecom_external_contacts_details_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_external_contacts_mapping
-- ----------------------------
CREATE INDEX "idx_external_mapping_corp" ON "public"."wecom_external_contacts_mapping" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_external_mapping_external_userid" ON "public"."wecom_external_contacts_mapping" USING btree (
  "external_userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_external_mapping_userid" ON "public"."wecom_external_contacts_mapping" USING btree (
  "userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_external_contacts_mapping
-- ----------------------------
ALTER TABLE "public"."wecom_external_contacts_mapping" ADD CONSTRAINT "wecom_external_contacts_mapping_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_external_tags
-- ----------------------------
CREATE INDEX "idx_external_tags_external_userid" ON "public"."wecom_external_tags" USING btree (
  "external_userid" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_external_tags_tag_id" ON "public"."wecom_external_tags" USING btree (
  "tag_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_external_tags_user_id" ON "public"."wecom_external_tags" USING btree (
  "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_external_tags
-- ----------------------------
ALTER TABLE "public"."wecom_external_tags" ADD CONSTRAINT "wecom_external_tags_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_group_members
-- ----------------------------
CREATE INDEX "idx_group_members_chat_id" ON "public"."wecom_group_members" USING btree (
  "chat_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_group_members_member_id" ON "public"."wecom_group_members" USING btree (
  "member_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);
CREATE INDEX "idx_group_members_user_id" ON "public"."wecom_group_members" USING btree (
  "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_group_members
-- ----------------------------
ALTER TABLE "public"."wecom_group_members" ADD CONSTRAINT "wecom_group_members_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table wecom_master
-- ----------------------------
ALTER TABLE "public"."wecom_master" ADD CONSTRAINT "_copy_3" PRIMARY KEY ("wecom_mater_id");

-- ----------------------------
-- Indexes structure for table wecom_sync_logs
-- ----------------------------
CREATE INDEX "idx_sync_logs_corp" ON "public"."wecom_sync_logs" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sync_logs_time" ON "public"."wecom_sync_logs" USING btree (
  "start_time" "pg_catalog"."timestamp_ops" ASC NULLS LAST
);
CREATE INDEX "idx_sync_logs_type" ON "public"."wecom_sync_logs" USING btree (
  "sync_type" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_sync_logs
-- ----------------------------
ALTER TABLE "public"."wecom_sync_logs" ADD CONSTRAINT "wecom_sync_logs_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes structure for table wecom_tag_groups
-- ----------------------------
CREATE INDEX "idx_tag_groups_corp" ON "public"."wecom_tag_groups" USING btree (
  "wecom_corp_id" "pg_catalog"."int8_ops" ASC NULLS LAST
);
CREATE INDEX "idx_tag_groups_group_id" ON "public"."wecom_tag_groups" USING btree (
  "group_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table wecom_tag_groups
-- ----------------------------
ALTER TABLE "public"."wecom_tag_groups" ADD CONSTRAINT "wecom_tag_groups_pkey" PRIMARY KEY ("id");


-- 初始化脚本
INSERT INTO conn_docker_service_enum (connector_name, service_name) VALUES('企业微信插件', 'wecom-open');
