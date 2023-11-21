-- Copyright 2023 Ant Group CO., Ltd.
--
-- Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
-- in compliance with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software distributed under the License
-- is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
-- or implied.

use openspg;

CREATE TABLE `kg_project_info` (
`id` bigint(20) NOT NULL AUTO_INCREMENT comment '主键',
`name` varchar(255) NOT NULL comment '项目名称',
`description` varchar(1024) DEFAULT NULL comment '项目描述信息',
`status` varchar(20) NOT NULL DEFAULT 'INVALID' comment 'DELETE:删除 VALID:有效 INVALID：无效',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`namespace` varchar(64) NOT NULL DEFAULT '' comment '命名空间',
`biz_domain_id` bigint(20) DEFAULT NULL comment '业务域主键',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_name`(`name`),
KEY `idx_biz_domain_id`(`biz_domain_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '图谱项目信息表';


CREATE TABLE `kg_biz_domain` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`name` varchar(100) DEFAULT NULL comment '名称',
`status` varchar(20) DEFAULT NULL comment '状态。VALID - 有效 DELETE - 逻辑删除',
`description` varchar(1024) DEFAULT NULL comment '描述',
`global_config` varchar(10240) DEFAULT NULL comment '全局配置',
PRIMARY KEY(`id`),
KEY `idx_status`(`status`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '业务域表';

CREATE TABLE `kg_data_source` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`unique_name` varchar(64) NOT NULL comment '唯一名称',
`type` varchar(32) NOT NULL comment '类型 ',
`conn_info` text NOT NULL comment '链接信息',
`physical_info` text DEFAULT NULL comment '物理信息',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_unique_name`(`unique_name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '数据源信息';

CREATE TABLE `kg_data_source_usage` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`data_source_name` varchar(64) NOT NULL comment '数据源名称',
`usage_type` varchar(32) NOT NULL comment '数据源使用场景',
`is_default` tinyint(3) unsigned NOT NULL comment '是否默认资源',
`mount_object_id` varchar(32) NOT NULL comment '挂载对象Id',
`mount_object_type` varchar(16) NOT NULL comment '挂载对象类型',
PRIMARY KEY(`id`),
KEY `idx_object_id`(`mount_object_id`, `usage_type`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '资源与服务视图关系表';

CREATE TABLE `kg_sys_lock` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`method_name` varchar(128) DEFAULT NULL comment '方法名',
`method_value` varchar(128) DEFAULT NULL comment '方法值',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_mname`(`method_name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '系统内置表，用于分布式锁实现';

CREATE TABLE `kg_operator_overview` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`name` varchar(256) NOT NULL comment '算子名称',
`type` varchar(64) NOT NULL comment '算子类型',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`description` varchar(2048) NOT NULL comment '算子描述',
`lang` varchar(64) DEFAULT NULL comment '算子开发语言',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_name`(`name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '算子总览表';

CREATE TABLE `kg_operator_version` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`overview_id` bigint(20) unsigned NOT NULL comment '算子总览id',
`main_class` varchar(256) DEFAULT NULL comment '主类',
`jar_address` varchar(1024) DEFAULT NULL comment 'jar包地址',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`version` int(11) unsigned NOT NULL comment '版本',
PRIMARY KEY(`id`),
KEY `idx_overview_id`(`overview_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '算子具体版本信息';

CREATE TABLE `kg_ontology_entity` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`original_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '类型的原始ID',
`name` varchar(255) NOT NULL comment '类型具体名称，比如‘Car’',
`name_zh` varchar(255) NOT NULL comment '类型具体中文名称',
`entity_category` varchar(20) NOT NULL comment '\'BASIC\':该类型为基本类型，\'ADVANCED\':该类型为实体类型',
`layer` varchar(20) DEFAULT NULL comment '类型所属层次，“CORE”：核心层，“EXTENSION”:扩展层',
`description` varchar(1024) DEFAULT NULL comment '当前类型的说明/描述信息',
`description_zh` varchar(1024) DEFAULT NULL comment '当前类型的中文说明/描述信息即jsonLd中的\"@id\"',
`status` char(1) NOT NULL DEFAULT '0' comment '9：删除  1：有效 0:无效 默认',
`with_index` varchar(20) NOT NULL DEFAULT 'TRUE' comment 'TRUE\':该类型被索引，\'FALSE\':该类型不走索引',
`scope` varchar(20) DEFAULT NULL comment '公有私有标识:PUBLIC,PRIVATE',
`version` int(11) NOT NULL DEFAULT '0' comment '版本',
`version_status` varchar(50) NOT NULL DEFAULT 'ONLINE' comment '迭代版本状态:ONLINE:线上版本、LATEST:最新版本、EFFICIENT:生效版本、HISTORY:历史版本、DISCARDED:废弃版本',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`transformer_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '算子ID',
`operator_config` text DEFAULT NULL comment '算子配置,json格式文本',
`config` mediumtext DEFAULT NULL comment '实体类型配置',
`unique_name` varchar(255) DEFAULT NULL comment '唯一名称',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_name`(`name`),
UNIQUE KEY `uk_origianl_id_version`(`original_id`, `version`),
KEY `idx_version_status`(`version_status`),
KEY `idx_originalid_versionstatus`(`original_id`, `version_status`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体类型';

CREATE TABLE `kg_ontology_entity_parent` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`entity_id` bigint(20) NOT NULL comment '类型唯一标识',
`parent_id` bigint(20) NOT NULL comment '父类型唯一标识,根节点“-1”',
`status` char(1) NOT NULL DEFAULT '0' comment '9：删除  1：有效 0:无效 默认',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`path` varchar(4096) DEFAULT NULL comment '继承路径',
`deep_inherit` char(1) DEFAULT NULL comment '是否是深度继承,取值：Y，N',
`history_path` varchar(4096) DEFAULT NULL comment '历史继承关系',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_type_parent_id`(`entity_id`, `parent_id`),
KEY `idx_parent_id`(`parent_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体继承关系表';

CREATE TABLE `kg_ontology_entity_property_range` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`domain_id` bigint(20) unsigned NOT NULL comment '类型唯一标识或边属性边唯一标识',
`property_name` varchar(255) NOT NULL comment '数据或者对象属性英文名',
`range_id` bigint(20) unsigned NOT NULL comment '属性值域唯一标识或边属性属性值域唯一标识',
`property_name_zh` varchar(255) NOT NULL comment '数据或者对象属性中文名',
`constraint_id` bigint(20) unsigned NOT NULL comment '数据属性约束ID',
`property_category` varchar(20) NOT NULL comment 'BASIC\':该属性为基本类型（实体），\'ADVANCED\':该属性为高级类型（边关系）',
`map_type` varchar(20) NOT NULL DEFAULT 'TYPE' comment '标识映射是类型-》属性-》值域还是边的属性-》边属性的属性-》边属性的属性的值域，\"TYPE\":类型映射 \"EDGE\":边属性映射',
`version` int(11) NOT NULL DEFAULT '0' comment '版本',
`status` char(1) NOT NULL comment '9：删除 1：有效 0:无效 默认 和其他schema表对齐',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`original_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment 'spo多版本的原始ID',
`store_property_name` varchar(255) DEFAULT NULL comment '数据属性对应的存储属性名',
`transformer_id` bigint(20) NOT NULL DEFAULT '0' comment '算子ID',
`property_desc` varchar(1024) DEFAULT NULL comment '属性描述',
`property_desc_zh` varchar(1024) DEFAULT NULL comment '属性中文描述',
`project_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '项目ID',
`original_domain_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '类型或边的唯一原始标识',
`original_range_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '类型的唯一原始标识',
`version_status` varchar(50) DEFAULT NULL comment '迭代版本状态:ONLINE:线上版本、LATEST:最新版本、EFFICIENT:生效版本、HISTORY:历史版本、DISCARDED:废弃版本',
`relation_source` varchar(2550) DEFAULT NULL comment '记录关系对应的属性(用于属性转关系)',
`direction` varchar(10) DEFAULT NULL comment 'BOTH:表示双向边',
`mask_type` varchar(20) DEFAULT NULL comment '数据加密规则。',
`multiver_config` varchar(1024) DEFAULT NULL comment '多版本配置,json格式文本',
`property_source` bigint(20) DEFAULT NULL comment '属性的来源，对应全局属性的id',
`property_config` text DEFAULT NULL comment '针对属性的配置信息，如运营配置',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_spo`(`domain_id`, `property_name`, `range_id`, `map_type`, `version`),
KEY `idx_original_id`(`original_id`),
KEY `idx_version_status`(`version_status`),
KEY `idx_relation`(`domain_id`, `property_category`, `map_type`, `version_status`),
KEY `idx_property_name`(`property_name`),
KEY `uk_spo_v2`(`original_domain_id`, `property_name`, `original_range_id`, `map_type`, `version`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体三元组表';

CREATE TABLE `kg_project_entity` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`project_id` bigint(20) unsigned NOT NULL comment '项目id',
`entity_id` bigint(20) unsigned NOT NULL comment '本体类型id',
`version` int(11) NOT NULL DEFAULT '0' comment '版本',
`version_status` varchar(50) NOT NULL DEFAULT 'ONLINE' comment '迭代版本状态:ONLINE:线上版本、EFFICTIVE:生效版本、RELEASED:已发布版本、DISCARD:废弃版本',
`referenced` char(1) NOT NULL comment '标志是否是引用的类型。Y:是，N:不是',
`type` varchar(64) DEFAULT 'ENTITY_TYPE' comment '引入的资源类型，关系（RELATION_TYPE）和实体类型（ENTITY_TYPE），默认ENTITY_TYPE',
`ref_source` varchar(64) DEFAULT NULL comment '引用来源，corekg:COREKG, 项目:PROJECT',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_project_id_entity_id`(`project_id`, `entity_id`, `version`),
KEY `idx_version_status`(`version_status`),
KEY `idx_projectid_versionstatus`(`project_id`, `version_status`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '项目和本体类型关联表';

CREATE TABLE `kg_ontology_semantic` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`resource_id` varchar(128) NOT NULL comment '关联资源id',
`semantic_type` varchar(64) NOT NULL comment '谓词',
`original_resource_id` varchar(64) NOT NULL comment '被关联资源id',
`resource_type` varchar(64) DEFAULT NULL comment '资源类型：entity_type、relation_type、property，可为空，也可有其他类型',
`status` int(11) NOT NULL comment '状态，0:删除 1：有效',
`config` text DEFAULT NULL comment '预留，谓词额外信息',
`rule_id` varchar(128) DEFAULT NULL comment '关联规则ID',
`subject_meta_type` varchar(128) DEFAULT NULL comment '主体元概念名',
`object_meta_type` varchar(128) DEFAULT NULL comment '客体元概念名',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_spo`(`resource_id`, `semantic_type`, `original_resource_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '语义关联维护表';

CREATE TABLE `kg_semantic_rule` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`name` varchar(255) DEFAULT NULL comment '名称',
`expression` mediumtext NOT NULL comment '内容',
`version_id` int(11) NOT NULL comment '版本号',
`status` varchar(60) NOT NULL comment '状态',
`user_no` varchar(255) NOT NULL comment '用户ID',
`is_master` tinyint(4) DEFAULT NULL comment '是否主版本',
`rule_id` varchar(512) DEFAULT NULL comment '规则ID',
`effect_scope` varchar(60) DEFAULT NULL comment '生效范围',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_id_version`(`rule_id`, `version_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '语义规则表';

CREATE TABLE `kg_ontology_property_constraint` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`name` varchar(255) NOT NULL comment '该约束的名称，英文',
`name_zh` varchar(255) NOT NULL comment '该约束的中文名称',
`is_require` char(1) NOT NULL DEFAULT 'N' comment '空约束，属性值域是否可以为空，\"N\":可为空 \"Y\":不可为空',
`up_down_boundary` char(1) NOT NULL DEFAULT '0' comment '\">\":1;\">=\":2;\"<\":3;\"<=\":4;1\">\"\"<\":5 \">\"\"<=\":6 \">=\"\"<\":7 \">=\"\"<=\":8，,默认0：无校验',
`max_value` varchar(255) DEFAULT NULL comment '该属性在该类别下的最大值，仅当数值类型Number及其子类时有效',
`min_value` varchar(255) DEFAULT NULL comment '该属性在该类别下的最小值，仅当值类型是Number及其子类时有效',
`value_pattern` varchar(1024) DEFAULT NULL comment '正则表达的值规范，多用于文本类型Text',
`description` varchar(1024) NOT NULL comment '当前约束的说明/描述信息',
`description_zh` varchar(1024) NOT NULL comment '当前约束的中文说明/描述信息',
`is_unique` char(1) DEFAULT 'N' comment 'Y:属性唯一约束, N:无唯一约束',
`is_enum` char(1) DEFAULT 'N' comment 'Y 是枚举类型,N 不是',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`enum_value` text comment '枚举值',
`is_multi_value` char(1) DEFAULT NULL comment '是否多值，Y：多值',
PRIMARY KEY(`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体属性约束表';

CREATE TABLE `kg_ontology_release` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`project_id` bigint(20) NOT NULL comment '项目ID',
`version` int(11) NOT NULL comment '发布版本',
`schema_view` longtext DEFAULT NULL comment '当前版本schema视图',
`user_id` varchar(20) NOT NULL comment '发布人',
`description` text NOT NULL comment '发布描述',
`status` varchar(20) NOT NULL comment '状态',
`change_procedure_id` text DEFAULT NULL comment '变更流程id',
`operation_detail` text DEFAULT NULL comment '（废弃）本次发布的操作详情',
`error_detail` text DEFAULT NULL comment '失败详情',
`operation_info` mediumtext DEFAULT NULL comment '本次发布的操作详情',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_project_version`(`project_id`, `version`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体建模发布版本';

CREATE TABLE `kg_spg_job_info` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`name` varchar(64) NOT NULL comment '任务名称',
`type` varchar(20) NOT NULL comment '任务类型',
`content` text NOT NULL comment '任务内容',
`project_id` bigint(20) NOT NULL comment '项目ID',
`cron` varchar(20) DEFAULT NULL comment 'cron表达式，null时则为单次调度',
`status` varchar(20) NOT NULL comment '状态',
`ext_info` varchar(1024) DEFAULT NULL comment '扩展字段',
`external_job_info_id` varchar(64) DEFAULT NULL comment '外部调度任务Id',
PRIMARY KEY(`id`),
KEY `idx_project_id`(`project_id`),
UNIQUE KEY `idx_external_job_info_id`(`external_job_info_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '图谱任务定义';

CREATE TABLE `kg_spg_job_inst` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`job_id` bigint(20) NOT NULL comment '任务id',
`type` varchar(20) NOT NULL comment '任务类型',
`project_id` bigint(20) NOT NULL comment '项目ID',
`status` varchar(20) NOT NULL comment '状态',
`start_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '开始时间',
`end_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '结束时间',
`result` text DEFAULT NULL comment '调度实例运行日志',
`progress` text DEFAULT NULL comment '运行进度',
`log_info` text DEFAULT NULL comment '调度实例运行日志',
`external_job_inst_id` varchar(64) DEFAULT NULL comment '外部调度实例Id',
PRIMARY KEY(`id`),
KEY `idx_project_id`(`project_id`),
KEY `idx_job_id`(`job_id`),
UNIQUE KEY `idx_external_job_inst_id`(`external_job_inst_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '图谱任务定义';

CREATE TABLE `kg_schedule_job_info` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`name` varchar(64) NOT NULL comment '任务名称',
`type` varchar(20) NOT NULL comment '任务类型',
`cron` varchar(20) DEFAULT NULL comment 'cron表达式，null时则为单次调度',
`status` varchar(20) NOT NULL comment '状态',
`idempotent_id` varchar(64) DEFAULT NULL comment '幂等键，即业务jobId',
PRIMARY KEY(`id`),
UNIQUE KEY `idx_idempotent_id`(`idempotent_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '调度任务定义';

CREATE TABLE `kg_schedule_job_inst` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`job_id` bigint(20) NOT NULL comment '调度任务id',
`type` varchar(20) NOT NULL comment '任务类型',
`status` varchar(20) NOT NULL comment '状态',
`result` text DEFAULT NULL comment '运行结果',
`host` varchar(32) DEFAULT NULL comment '运行机器',
`trace_id` varchar(32) DEFAULT NULL comment '任务最近一次运行的trace_id',
`idempotent_id` varchar(64) DEFAULT NULL comment '幂等键，即业务jobInstId',
PRIMARY KEY(`id`),
KEY `idx_job_id`(`job_id`),
UNIQUE KEY `idx_idempotent_id`(`idempotent_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '调度任务实例';

INSERT INTO kg_biz_domain (`id`,`gmt_create`,`gmt_modified`,`name`,`status`,`description`,`global_config`) VALUES(1,'2023-09-01 00:00:00','2023-09-01 00:00:00','defaultTenant','VALID','',null);
INSERT INTO kg_project_info (`id`,`name`,`description`,`status`,`gmt_create`,`gmt_modified`,`namespace`,`biz_domain_id`) VALUES(1,'defaultProject','defaultProject','VALID','2023-09-01 00:00:00','2023-09-01 00:00:00','DEFAULT',1);

INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(1,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_tugraph','GRAPH_STORE','{"scheme":"tugraph","params":{"graphName":"default","timeout":5000,"host":"tugraph:9090","accessId":"admin","accessKey":"73@TuGraph"}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(2,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_operator','OBJECT_STORE','{"scheme":"local","params":{"localDir":"/objectStore/operator"}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(3,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_tmpfile','OBJECT_STORE','{"scheme":"local","params":{"localDir":"/objectStore/tmpfile"}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(4,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_elasticsearch','SEARCH_ENGINE','{"scheme":"elasticsearch","params":{"host":"elasticsearch","scheme":"http","port":"9200"}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(5,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_scheduler','JOB_SCHEDULER','{"scheme":"local",params:{}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(6,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_computing','COMPUTING','{"scheme":"local",params:{}}',null);
INSERT INTO kg_data_source (`id`,`gmt_create`,`gmt_modified`,`unique_name`,`type`,`conn_info`,`physical_info`) VALUES(7,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_table_store','TABLE_STORE','{"scheme":"local",params:{"localDir":"/tableStore"}}',null);
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(1,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_tugraph','KG_STORE',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(2,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_operator','OPERATOR_STORE',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(3,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_tmpfile','FILE_STORE',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(4,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_elasticsearch','SEARCH',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(5,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_scheduler','JOB_SCHEDULER',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(6,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_computing','COMPUTING',1,'-1','PROJECT');
INSERT INTO kg_data_source_usage (`id`,`gmt_create`,`gmt_modified`,`data_source_name`,`usage_type`,`is_default`,`mount_object_id`,`mount_object_type`) VALUES(7,'2023-09-01 00:00:00','2023-09-01 00:00:00','local_table_store','TABLE_STORE',1,'-1','PROJECT');

INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(1,1,'Thing','事物','ADVANCED','EXTENSION','Base class for all schema types, all of which inherit the type either directly or indirectly','所有schema类型的基类，它们都直接或者间接继承该类型','1','TRUE','PUBLIC',44,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,null,'Thing');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(2,2,'Text','文本','BASIC','CORE','Data type: Text','基本数据类型-文本','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"UNIQUE","name":"Unique","nameZh":"值唯一","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":null}]}','Text');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(4,4,'Integer','整型','BASIC','CORE','Data type: Integer','基本数据类型-整型','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MINIMUM_GT","name":"Greater than","nameZh":"大于","value":null},{"id":"MINIMUM_GT_OE","name":"Greater than or equal","nameZh":"大于等于","value":null},{"id":"MAXIMUM_LT","name":"Less than","nameZh":"小于","value":null},{"id":"MAXIMUM_LT_OE","name":"Less than or equal","nameZh":"小于等于","value":null}]}','Integer');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(5,5,'Float','浮点数','BASIC','CORE','Data type: Float','基本数据类型-浮点数','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MINIMUM_GT","name":"Greater than","nameZh":"大于","value":null},{"id":"MINIMUM_GT_OE","name":"Greater than or equal","nameZh":"大于等于","value":null},{"id":"MAXIMUM_LT","name":"Less than","nameZh":"小于","value":null},{"id":"MAXIMUM_LT_OE","name":"Less than or equal","nameZh":"小于等于","value":null}]}','Float');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(10,10,'STD.ChinaMobile','国内手机号','STANDARD','CORE','Data type:ChinaMobile','标准类型-国内手机号','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$"}],"spreadable":true}','STD.ChinaMobile');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(11,11,'STD.Email','邮箱','STANDARD','CORE','Data type:Email','标准类型-邮箱','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[.][A-Za-z]{2,3}([.][A-Za-z]{2})?$"}],"spreadable":true}','STD.Email');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(12,12,'STD.Url','网址','STANDARD','CORE','Data type:Url','标准类型-网址','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.Url');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(13,13,'STD.IdCardNo','身份证','STANDARD','CORE','Data type:IdCardNo','标准类型-身份证','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^[1-9]{1}[0-9]{5}(19|20)[0-9]{2}((0[1-9]{1})|(1[0-2]{1}))((0[1-9]{1})|([1-2]{1}[0-9]{1}|(3[0-1]{1})))[0-9]{3}[0-9xX]{1}$"}],"spreadable":true}','STD.IdCardNo');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(14,14,'STD.MacAddress','MAC地址','STANDARD','CORE','Data type:MacAddress','标准类型-MAC地址','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}"}],"spreadable":true}','STD.MacAddress');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(17,17,'STD.Gender','性别','STANDARD','CORE','Data type:Gender','标准类型-性别','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"ENUM","name":"Enum","nameZh":"u679Au4E3E","value":{"男":"男","女":"女"}}]}','STD.Gender');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(18,18,'STD.Boolean','布尔','STANDARD','CORE','Data type:Boolean','标准类型-布尔','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"ENUM","name":"Enum","nameZh":"u679Au4E3E","value":{"true":"true","false":"false"}}]}','STD.Boolean');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(19,19,'STD.Date','日期','STANDARD','CORE','Data type:Date','标准类型-日期','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"[1,2][0-9][0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1])"}]}','STD.Date');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(20,20,'STD.Polygon','多边形','STANDARD','CORE','Data type:Polygon','标准类型-多边形','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.Polygon');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(21,21,'STD.Location','点','STANDARD','CORE','Data type:Location','标准类型-点','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^((-?(([1-9]?[0-9])|(1[0-7][0-9]))([.])?[0-9]{0,6})|(180))[,]((-?(([1-8]?[0-9])|)([.])?[0-9]{0,6})|(90))$"}]}','STD.Location');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(22,22,'STD.Line','线','STANDARD','CORE','Data type:Line','标准类型-线','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^((-?(([1-9]?[0-9])|(1[0-7][0-9]))([.])?[0-9]{0,6})|(180))[ ]((-?(([1-8]?[0-9])|)([.])?[0-9]{0,6})|(90))[;]((-?(([1-9]?[0-9])|(1[0-7][0-9]))([.])?[0-9]{0,6})|(180))[ ]((-?(([1-8]?[0-9])|)([.])?[0-9]{0,6})|(90))$"}]}','STD.Line');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(27,27,'STD.ChinaTelCode','国内通讯号','STANDARD','CORE','Data type:ChinaTelCode','标准类型-国内通讯号','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^(400[0-9]{7})|(800[0-9]{7})|(0[0-9]{2,3}-[0-9]{7,8})|((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$"}],"spreadable":true}','STD.ChinaTelCode');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(28,28,'STD.MCCCode','MCC编码','STANDARD','CORE','Data type:MCCCode','标准类型-MCC编码','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^[A-Z]{1}[0-9]{4}$"}],"spreadable":true}','STD.MCCCode');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(29,29,'STD.Timestamp','时间戳','STANDARD','CORE','Data type:Timestamp','标准类型-时间戳','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MINIMUM_GT", "name":"Greater than", "nameZh":"大于", "value":"31507200000"}]}','STD.Timestamp');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(34,34,'STD.Point','点','STANDARD','CORE','Data type:Point','标准类型-点','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.Point');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(35,35,'STD.LineString','线','STANDARD','CORE','Data type:LineString','标准类型-线','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.LineString');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(36,36,'STD.MultiPoint','点集合','STANDARD','CORE','Data type:MultiPoint','标准类型-点集合','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.MultiPoint');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(37,37,'STD.MultiPolygon','多边形集合','STANDARD','CORE','Data type:MultiPolygon','标准类型-多边形集合','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.MultiPolygon');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(38,38,'STD.Trajectory','轨迹','STANDARD','CORE','Data type:Trajectory','标准类型-轨迹','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"spreadable":true}','STD.Trajectory');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(39,39,'STD.S2CellId','S2CellId','STANDARD','CORE','Data type:S2CellId','标准类型-S2CellId','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null}],"spreadable":true}','STD.S2CellId');

INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(1,10,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,10','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(2,11,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,11','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(3,12,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,12','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(4,13,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,13','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(5,14,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,14','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(6,17,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,17','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(7,18,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,18','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(8,19,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,19','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(9,20,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,20','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(10,21,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,21','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(11,22,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,22','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(12,27,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,27','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(13,28,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,28','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(14,29,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,29','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(15,34,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,34','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(16,35,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,35','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(17,36,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,36','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(18,37,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,37','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(19,38,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,38','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(20,39,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,39','N', null);

INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(1,1,'description',2,'描述',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',1,'description',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);
INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(2,1,'id',2,'实体主键',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',2,'id',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);
INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(3,1,'name',2,'名称',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',3,'name',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);
