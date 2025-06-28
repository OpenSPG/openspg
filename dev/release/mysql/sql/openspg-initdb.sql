-- Copyright 2023 OpenSPG Authors
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
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) NOT NULL COMMENT '项目名称',
  `description` varchar(1024) DEFAULT NULL COMMENT '项目描述信息',
  `status` varchar(20) NOT NULL DEFAULT 'INVALID' COMMENT 'DELETE:删除 VALID:有效 INVALID：无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `namespace` varchar(64) NOT NULL DEFAULT '' COMMENT '命名空间',
  `biz_domain_id` bigint(20) DEFAULT NULL COMMENT '业务域主键',
  `config` text DEFAULT NULL COMMENT '项目配置信息',
  `visibility` varchar(64) DEFAULT 'PRIVATE' COMMENT '可见性：PRIVATE、PUBLIC_READ',
  `tag` varchar(64) NOT NULL DEFAULT 'LOCAL' COMMENT '知识库标签：本地知识库（LOCAL）、公网知识库（PUBLIC-NET）',
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

CREATE TABLE `kg_sys_lock` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`method_name` varchar(128) DEFAULT NULL comment '方法名',
`method_value` varchar(128) DEFAULT NULL comment '方法值',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_mname`(`method_name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '系统内置表，用于分布式锁实现';

CREATE TABLE `kg_ontology_entity` (
`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
`original_id` bigint(20) unsigned NOT NULL DEFAULT '0' comment '类型的原始ID',
`name` varchar(255) NOT NULL comment '类型具体名称，比如‘Car’',
`name_zh` varchar(255) NOT NULL comment '类型具体中文名称',
`entity_category` varchar(20) NOT NULL comment 'BASIC:该类型为基本类型，ADVANCED:该类型为实体类型',
`layer` varchar(20) DEFAULT NULL comment '类型所属层次，“CORE”：核心层，“EXTENSION”:扩展层',
`description` varchar(1024) DEFAULT NULL comment '当前类型的说明/描述信息',
`description_zh` varchar(1024) DEFAULT NULL comment '当前类型的中文说明/描述信息即jsonLd中的"@id"',
`status` char(1) NOT NULL DEFAULT '0' comment '9：删除  1：有效 0:无效 默认',
`with_index` varchar(20) NOT NULL DEFAULT 'TRUE' comment 'TRUE:该类型被索引，FALSE:该类型不走索引',
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
`property_category` varchar(20) NOT NULL comment 'BASIC:该属性为基本类型（实体），ADVANCED:该属性为高级类型（边关系）',
`map_type` varchar(20) NOT NULL DEFAULT 'TYPE' comment '标识映射是类型-》属性-》值域还是边的属性-》边属性的属性-》边属性的属性的值域，"TYPE":类型映射 "EDGE":边属性映射',
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
`index_type` varchar(1024) DEFAULT NULL comment '索引规则。',
`multiver_config` varchar(1024) DEFAULT NULL comment '多版本配置,json格式文本',
`property_source` bigint(20) DEFAULT NULL comment '属性的来源，对应全局属性的id',
`property_config` text DEFAULT NULL comment '针对属性的配置信息，如运营配置',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_spo`(`domain_id`, `property_name`, `range_id`, `map_type`, `version`),
KEY `idx_original_id`(`original_id`),
KEY `idx_version_status`(`version_status`),
KEY `idx_relation`(`domain_id`, `property_category`, `map_type`, `version_status`),
KEY `idx_property_name`(`property_name`),
KEY `idx_uk_spo_v2`(`original_domain_id`, `property_name`, `original_range_id`, `map_type`, `version`)
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
`is_require` char(1) NOT NULL DEFAULT 'N' comment '空约束，属性值域是否可以为空，"N":可为空 "Y":不可为空',
`up_down_boundary` char(1) NOT NULL DEFAULT '0' comment '">":1;">=":2;"<":3;"<=":4;1">""<":5 ">""<=":6 ">=""<":7 ">=""<=":8，,默认0：无校验',
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
`user_no` varchar(255) NOT NULL COMMENT '发布人工号',
`description` text NOT NULL comment '发布描述',
`status` varchar(20) NOT NULL comment '状态',
`change_procedure_id` text DEFAULT NULL comment '变更流程id',
`operation_detail` text DEFAULT NULL comment '（废弃）本次发布的操作详情',
`error_detail` text DEFAULT NULL comment '失败详情',
`operation_info` mediumtext DEFAULT NULL comment '本次发布的操作详情',
PRIMARY KEY(`id`),
UNIQUE KEY `uk_project_version`(`project_id`, `version`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '本体建模发布版本';

CREATE TABLE `kg_ontology_ext` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT comment '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
  `resource_id` varchar(128) NOT NULL comment '实体类型id、关系类型id、属性id',
  `resource_type` varchar(64) NOT NULL comment '操作的类型枚举：实体类型、关系类型、属性',
  `ext_type` varchar(64) NOT NULL comment '扩展类型：标签、回流、颜色',
  `field` varchar(64) NOT NULL comment '扩展属性所属域，比如区分用户',
  `config` mediumtext DEFAULT NULL comment '配置内容',
  `creator` varchar(255) NOT NULL comment '创建者',
  `modifier` varchar(255) NOT NULL comment '更新者',
  `status` int(10) unsigned NOT NULL comment '状态 1：有效 0：无效',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_id_type_field`(`resource_id`, `resource_type`, `ext_type`, `field`)
) AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = 'schema的扩展属性';

CREATE TABLE `kg_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `user_no` varchar(255) NOT NULL COMMENT '用户工号',
  `token` varchar(255) NOT NULL COMMENT 'token',
  `last_token` varchar(255) DEFAULT NULL COMMENT '修改前token',
  `salt` varchar(255) NOT NULL COMMENT '随机字符串',
  `gmt_last_token_disable` timestamp NULL DEFAULT NULL COMMENT 'token修改时间',
  `dw_access_id` varchar(32) DEFAULT NULL COMMENT '数仓用户ID',
  `dw_access_key` varchar(64) DEFAULT NULL COMMENT '数仓用户密钥',
  `real_name` varchar(50) DEFAULT NULL COMMENT '用户真名',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '用户花名',
  `email` varchar(64) DEFAULT NULL COMMENT '用户邮箱',
  `domain_account` varchar(64) DEFAULT NULL COMMENT '用户域账号',
  `mobile` varchar(64) DEFAULT NULL COMMENT '用户手机号',
  `wx_account` varchar(64) DEFAULT NULL COMMENT '用户微信账号',
  `config` text DEFAULT NULL COMMENT '配置，json',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_userNo`(`user_no`),
  UNIQUE KEY `uk_token`(`token`),
  UNIQUE KEY `uk_domain_account`(`domain_account`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '用户管理表';

CREATE TABLE `kg_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `project_id` varchar(64) NOT NULL COMMENT '项目id，可以为某个域的唯一值',
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `config_id` varchar(128) NOT NULL COMMENT '配置id',
  `version` varchar(64) NOT NULL DEFAULT '1' COMMENT '配置版本',
  `config` longtext NOT NULL COMMENT '配置，json',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态，1有效',
  `description` varchar(1024) DEFAULT NULL COMMENT '版本描述',
  `resource_id` varchar(128) DEFAULT NULL COMMENT '资源id,用于外键关联schem视图',
  `resource_type` varchar(128) DEFAULT 'CONFIG' COMMENT '资源类型',
  `user_no` varchar(64) NOT NULL COMMENT '创建者',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_configidversion`(`config_id`, `version`),
  KEY `idx_projectid`(`project_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '图谱配置表';

CREATE TABLE `kg_resource_permission` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `user_no` varchar(255) NOT NULL COMMENT '用户工号',
  `resource_id` bigint(20) NOT NULL COMMENT '资源id',
  `role_id` bigint(20) NOT NULL COMMENT '角色id',
  `resource_tag` varchar(50) NOT NULL DEFAULT 'TYPE' COMMENT '资源分类',
  `status` varchar(2) NOT NULL DEFAULT '99' COMMENT '状态。-1：驳回;99：审批中;1：有效;9：删除',
  `expire_date` date DEFAULT NULL COMMENT '过期日期',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_unique`(`user_no`, `resource_id`, `resource_tag`),
  KEY `idx_resource`(`resource_id`, `role_id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '资源权限表';

CREATE TABLE `kg_role` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键，角色id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `role_name` varchar(255) NOT NULL COMMENT '角色名',
  `permission_detail` text DEFAULT NULL COMMENT '角色权限具体信息，json格式',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_role_name`(`role_name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '平台角色表';


CREATE TABLE `kg_ref` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `name` varchar(512) NOT NULL COMMENT '关联名称',
  `ref_id` varchar(255) NOT NULL COMMENT '关联id',
  `ref_type` varchar(64) NOT NULL COMMENT '关联类型',
  `refed_id` varchar(255) NOT NULL COMMENT '被关联Id',
  `refed_type` varchar(64) NOT NULL COMMENT '被关联类型',
  `config` longtext DEFAULT NULL COMMENT '配置详情',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态：1 有效',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_id_type`(`ref_id`, `ref_type`, `refed_id`, `refed_type`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '关联表';

CREATE TABLE `kg_model_provider` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `name` varchar(32) NOT NULL COMMENT '名称',
  `provider` varchar(32) NOT NULL COMMENT '供应商英文名称',
  `status` varchar(32) NOT NULL COMMENT '状态 0：禁用，1：启用',
  `page_mode` varchar(32) NOT NULL COMMENT '添加模型模式，ALL:全部模型，SINGLE:单个模型',
  `model_type` varchar(255) NOT NULL COMMENT '支持模型类型(以逗号拼接)',
  `logo` varchar(255) NOT NULL COMMENT '图标',
  `tags` varchar(255) NOT NULL COMMENT '标签',
  `params` longtext NOT NULL COMMENT '供应商参数json',
  `creator` varchar(64) NOT NULL COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL COMMENT '修改用户',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_provider`(`provider`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '模型供应商表';

CREATE TABLE `kg_provider_param` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `provider` varchar(32) NOT NULL COMMENT '供应商英文名称',
  `model_type` varchar(32) NOT NULL COMMENT '模型类型',
  `model_name` varchar(32) DEFAULT NULL COMMENT '基础模型名称',
  `params` longtext NOT NULL COMMENT '定制参数json',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_provider_model_type`(`provider`, `model_type`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '供应商参数表';

CREATE TABLE `kg_model_detail` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `provider` varchar(32) NOT NULL COMMENT '供应商英文名称',
  `type` varchar(32) NOT NULL COMMENT '模型类型',
  `name` varchar(64) NOT NULL COMMENT '名称',
  `description` varchar(255) DEFAULT NULL COMMENT '备注',
  `params` longtext DEFAULT NULL COMMENT '模型参数json',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_provider_type_name`(`provider`, `type`, `name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '模型配置详情表';


CREATE TABLE `kg_user_model` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `instance_id` varchar(64) NOT NULL COMMENT '实例id',
  `visibility` varchar(32) NOT NULL COMMENT '可见性，PUBLIC_READ：公有、PRIVATE：私有',
  `provider` varchar(32) NOT NULL COMMENT '供应商英文名称',
  `name` varchar(64) NOT NULL COMMENT '唯一名称',
  `config` longtext DEFAULT NULL COMMENT '模型配置字段json',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_instanceid`(`instance_id`),
  UNIQUE KEY `uk_provider_name_visibility`(`provider`, `name`, `visibility`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '用户模型表';

CREATE TABLE `kg_app` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  `name` varchar(64) NOT NULL COMMENT '应用名称',
  `description` text DEFAULT NULL COMMENT '备注',
  `logo` varchar(1024) NOT NULL COMMENT '图标',
  `config` longtext DEFAULT NULL COMMENT '应用参数',
  `alias` varchar(32) DEFAULT NULL COMMENT '别名',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_name`(`name`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '应用表';

CREATE TABLE `kg_feedback` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  `module_type` varchar(65) NOT NULL COMMENT '功能模块类型，QA:问答',
  `one_category` varchar(255) NOT NULL COMMENT '一级类目',
  `two_category` varchar(255) DEFAULT NULL COMMENT '二级类目',
  `three_category` varchar(255) DEFAULT NULL COMMENT '三级类目',
  `four_category` varchar(255) DEFAULT NULL COMMENT '四级类目',
  `five_category` varchar(255) DEFAULT NULL COMMENT '五级类目',
  `reaction_type` varchar(64) NOT NULL COMMENT '反应类型：DEFAULT/UP/DOWN',
  `reason` longtext DEFAULT NULL COMMENT '点踩原因',
  PRIMARY KEY(`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '反馈记录表';

CREATE TABLE `kg_statistics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `creator` varchar(64) NOT NULL DEFAULT 'system' COMMENT '创建用户',
  `modifier` varchar(64) NOT NULL DEFAULT 'system' COMMENT '修改用户',
  `resource_tag` varchar(50) NOT NULL COMMENT '资源分类',
  `resource_id` varchar(50) NOT NULL COMMENT '资源id：应用id、平台0',
  `statistics_type` varchar(50) NOT NULL COMMENT '指标维度',
  `statistics_date` varchar(50) NOT NULL COMMENT '统计具体时间',
  `num` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '指标数量',
  PRIMARY KEY(`id`),
  UNIQUE KEY `uk_unique`(`resource_tag`, `resource_id`, `statistics_type`, `statistics_date`)
) DEFAULT CHARSET = utf8mb4 COMMENT = '统计汇总表';

INSERT INTO kg_biz_domain (`id`,`gmt_create`,`gmt_modified`,`name`,`status`,`description`,`global_config`) VALUES(1,'2023-09-01 00:00:00','2023-09-01 00:00:00','defaultTenant','VALID','',null);

INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(1,1,'Thing','事物','ADVANCED','EXTENSION','Base class for all schema types, all of which inherit the type either directly or indirectly','所有schema类型的基类，它们都直接或者间接继承该类型','1','TRUE','PUBLIC',44,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,null,'Thing');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(2,2,'Text','文本','BASIC','CORE','文本','基本数据类型-文本','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"UNIQUE","name":"Unique","nameZh":"值唯一","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":null}]}','Text');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(4,4,'Integer','整型','BASIC','CORE','整型数字','基本数据类型-整型','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MINIMUM_GT","name":"Greater than","nameZh":"大于","value":null},{"id":"MINIMUM_GT_OE","name":"Greater than or equal","nameZh":"大于等于","value":null},{"id":"MAXIMUM_LT","name":"Less than","nameZh":"小于","value":null},{"id":"MAXIMUM_LT_OE","name":"Less than or equal","nameZh":"小于等于","value":null}]}','Integer');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(5,5,'Float','浮点数','BASIC','CORE','浮点数','基本数据类型-浮点数','1','TRUE','PUBLIC',0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REQUIRE","name":"Required","nameZh":"值非空","value":null},{"id":"ENUM","name":"Enum","nameZh":"枚举","value":null},{"id":"MINIMUM_GT","name":"Greater than","nameZh":"大于","value":null},{"id":"MINIMUM_GT_OE","name":"Greater than or equal","nameZh":"大于等于","value":null},{"id":"MAXIMUM_LT","name":"Less than","nameZh":"小于","value":null},{"id":"MAXIMUM_LT_OE","name":"Less than or equal","nameZh":"小于等于","value":null}]}','Float');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(10,10,'STD.ChinaMobile','国内手机号','STANDARD','CORE','中国国内使用的手机号码由11位数字组成','中国国内使用的手机号码由11位数字组成','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$"}],"spreadable":true}','STD.ChinaMobile');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(11,11,'STD.Email','电子邮箱','STANDARD','CORE','电子邮箱地址','电子邮箱地址','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^([a-zA-Z0-9]*[-_.]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[.][A-Za-z]{2,3}([.][A-Za-z]{2})?$"}],"spreadable":true}','STD.Email');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(13,13,'STD.IdCardNo','身份证','STANDARD','CORE','中国身份证号码一般由18位数字和字母组成','中国身份证号码一般由18位数字和字母组成','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^[1-9]{1}[0-9]{5}(19|20)[0-9]{2}((0[1-9]{1})|(1[0-2]{1}))((0[1-9]{1})|([1-2]{1}[0-9]{1}|(3[0-1]{1})))[0-9]{3}[0-9xX]{1}$"}],"spreadable":true}','STD.IdCardNo');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(14,14,'STD.MacAddress','MAC地址','STANDARD','CORE','网卡MAC地址','网卡MAC地址','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"MULTIVALUE","name":"Multi value","nameZh":"多值","value":null},{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}"}],"spreadable":true}','STD.MacAddress');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(19,19,'STD.Date','日期','STANDARD','CORE','8位数字组成的日期','8位数字组成的日期','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"[1,2][0-9][0-9][0-9](0[1-9]|1[0-2])(0[1-9]|[1,2][0-9]|3[0,1])"}]}','STD.Date');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(27,27,'STD.ChinaTelCode','国内通讯号','STANDARD','CORE','国内通讯号码包含常见座机和手机号码','国内通讯号码包含常见座机和手机号码','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^(400[0-9]{7})|(800[0-9]{7})|(0[0-9]{2,3}-[0-9]{7,8})|((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$"}],"spreadable":true}','STD.ChinaTelCode');
INSERT INTO kg_ontology_entity (`id`,`original_id`,`name`,`name_zh`,`entity_category`,`layer`,`description`,`description_zh`,`status`,`with_index`,`scope`,`version`,`version_status`,`gmt_create`,`gmt_modified`,`transformer_id`,`operator_config`,`config`,`unique_name`) VALUES(29,29,'STD.Timestamp','时间戳','STANDARD','CORE','10位或者13位的时间戳','10位或者13位的时间戳','1','FALSE',null,0,'ONLINE','2023-09-01 00:00:00','2023-09-01 00:00:00',0,null,'{"constrains":[{"id":"REGULAR","name":"Regular match","nameZh":"正则匹配","value":"^([0-9]{10})|([0-9]{13})$"}]}','STD.Timestamp');

INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(1,10,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,10','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(2,11,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,11','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(4,13,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,13','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(5,14,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,14','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(8,19,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,19','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(12,27,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,27','N', null);
INSERT INTO kg_ontology_entity_parent (`id`,`entity_id`,`parent_id`,`status`,`gmt_create`,`gmt_modified`,`path`,`deep_inherit`,`history_path`) VALUES(14,29,1,1,'2023-09-01 00:00:00','2023-09-01 00:00:00','1,29','N', null);

INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(1,1,'description',2,'描述',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',1,'description',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);
INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(2,1,'id',2,'实体主键',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',2,'id',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);
INSERT INTO kg_ontology_entity_property_range (`id`,`domain_id`,`property_name`,`range_id`,`property_name_zh`,`constraint_id`,`property_category`,`map_type`,`version`,`status`,`gmt_create`,`gmt_modified`,`original_id`,`store_property_name`,`transformer_id`,`property_desc`,`property_desc_zh`,`project_id`,`original_domain_id`,`original_range_id`,`version_status`,`relation_source`,`direction`,`mask_type`,`multiver_config`,`property_source`,`property_config`) VALUES(3,1,'name',2,'名称',0,'BASIC','TYPE',44,'1','2022-03-21 19:24:54','2023-08-27 09:39:04',3,'name',0,null,null,0,1,2,'ONLINE',null,null,null,null,null,null);

INSERT INTO kg_user (`gmt_create`,`gmt_modified`,`user_no`,`token`,`last_token`,`salt`,`gmt_last_token_disable`,`dw_access_id`,`dw_access_key`,`real_name`,`nick_name`,`email`,`domain_account`,`mobile`,`wx_account`,`config`) VALUES(now(),now(),'openspg','075Df6275475a739',null,'Ktu4O',null,null,'efea9c06f9a581fe392bab2ee9a0508b2878f958c1f422f8080999e7dc024b83','openspg','openspg',null,'openspg',null,null,'{"useCurrentLanguage":"zh-CN"}');

INSERT INTO kg_role (`id`,`gmt_create`,`gmt_modified`,`role_name`,`permission_detail`) VALUES(1,now(),now(),'SUPER','');
INSERT INTO kg_role (`id`,`gmt_create`,`gmt_modified`,`role_name`,`permission_detail`) VALUES(2,now(),now(),'OWNER','');
INSERT INTO kg_role (`id`,`gmt_create`,`gmt_modified`,`role_name`,`permission_detail`) VALUES(3,now(),now(),'MEMBER','');

INSERT INTO kg_resource_permission (`gmt_create`,`gmt_modified`,`user_no`,`resource_id`,`role_id`,`resource_tag`,`status`,`expire_date`) VALUES(now(),now(),'openspg',0,1,'PLATFORM','1',null);

INSERT INTO kg_config (`id`,`gmt_create`,`gmt_modified`,`project_id`,`config_name`,`config_id`,`version`,`config`,`status`,`description`,`resource_id`,`resource_type`,`user_no`) VALUES(1,now(),now(),'0','KAG Support Model','KAG_SUPPORT_MODEL','1','[{"id":1,"vendor":"vllm","logo":"/img/logo/vllm.png","params":[{"ename":"base_url","cname":"base_url","required":true,"defaultValue":""},{"ename":"model","cname":"model","required":true,"defaultValue":""},{"ename":"desc","cname":"desc","required":true,"formProps":{"allowClear":true,"placeholder":"Please enter remarks for partitioning."}}]},{"id":2,"vendor":"maas","logo":"/img/logo/maas.png","params":[{"ename":"base_url","cname":"base_url","required":true,"defaultValue":""},{"ename":"api_key","cname":"api_key ","required":true,"defaultValue":""},{"ename":"model","cname":"model","required":true,"defaultValue":""},{"ename":"temperature","cname":"temperature","required":true,"formType":"number","defaultValue":0.7},{"ename":"stream","cname":"stream","required":true,"defaultValue":"False"},{"ename":"desc","cname":"desc","required":true,"formProps":{"allowClear":true,"placeholder":"Please enter remarks for partitioning."}}]},{"id":3,"vendor":"ollama","logo":"/img/logo/ollama.png","params":[{"ename":"base_url","cname":"base_url","required":true,"defaultValue":""},{"ename":"model","cname":"model","required":true,"defaultValue":""},{"ename":"desc","cname":"desc","required":true,"formProps":{"allowClear":true,"placeholder":"Please enter remarks for partitioning."}}]}]',1,null,null,'SYSTEM_CONFIG','');
INSERT INTO kg_config (`id`,`gmt_create`,`gmt_modified`,`project_id`,`config_name`,`config_id`,`version`,`config`,`status`,`description`,`resource_id`,`resource_type`,`user_no`) VALUES(3,now(),now(),'0','KAG Environment Configuration','KAG_ENV','1','{"showProfilePicture":false,"showUserConfig":true,"showLinks":false,"configTitle":{"graph_store":{"id":1,"title":[{"ename":"database","cname":"database","required":true,"inputType":"text","defaultValue":"kag","formProps":{"disabled":true}},{"ename":"password","cname":"password","required":true,"inputType":"text","defaultValue":"neo4j@openspg"},{"ename":"uri","cname":"uri","required":true,"inputType":"text","defaultValue":"neo4j://release-openspg-neo4j:7687"},{"ename":"user","cname":"user","required":true,"inputType":"text","defaultValue":"neo4j"}]},"prompt":{"id":3,"title":[{"ename":"language","cname":"语言","required":true,"defaultValue":"zh","inputType":"select","formProps":{"options":[{"label":"中文","value":"zh"},{"label":"英文","value":"en"}]}}]}}}',1,null,null,'SYSTEM_CONFIG','');
INSERT INTO kg_config (`id`,`gmt_create`,`gmt_modified`,`project_id`,`config_name`,`config_id`,`version`,`config`,`status`,`description`,`resource_id`,`resource_type`,`user_no`) VALUES(6,now(),now(),'0','APP_CHAT','APP_CHAT','1','[{"id":2,"cname":"推理问答","ename":"think_pipeline","logo":"/img/logo/modal_2.png","cdesc":"基于蚂蚁集团开源的专业领域知识服务框架KAG搭建的问答模板，擅长逻辑推理、数值计算等任务，可以协助解答相关问题、提供信息支持或进行数据分析","edesc":"The Q&A module, built on Ant Group\'s open-source KAG framework (a domain-specific knowledge service platform), demonstrates strong capabilities in logical reasoning and numerical computations. It effectively assists with problem-solving, delivers informational support, and performs data analysis tasks","thinking_enabled":{"defaultValue":true,"disabled":false}},{"id":4,"cname":"KAG Model问答","ename":"kag_thinker_pipeline","logo":"/img/logo/modal_2.png","thinking_enabled":{"defaultValue":true,"disabled":true},"cdesc":"基于蚂蚁集团开源的思考大模型及KAG搭建的问答模板，擅长逻辑推理、数值计算等任务，可以协助解答相关问题、提供信息支持或进行数据分析","edesc":"The Q&A template built on Ant Group\'s open-source thinking large model and KAG is excellent at tasks such as logical reasoning and numerical calculations. It can assist in answering relevant questions, providing information support, or conducting data analysis."}]', 1, NULL, NULL, 'SYSTEM_CONFIG', 'system');
INSERT INTO kg_config (`id`,`gmt_create`,`gmt_modified`,`project_id`,`config_name`,`config_id`,`version`,`config`,`status`,`description`,`resource_id`,`resource_type`,`user_no`) VALUES(8,now(),now(),'0','PROVIDER BASE INFO','PROVIDER_BASE_INFO','1','{"modelType":{"OpenAI_chat":"maas","Google_embedding":"openai","OpenAI_embedding":"openai","SILICONFLOW_chat":"maas","Ollama_chat":"ollama","DeepSeek_chat":"maas","BaiLian_embedding":"openai","ant_gpt_chat":"ant_gpt","maya_chat":"maya","Google_chat":"maas","OpenRouter_chat":"maas","ant_bailian_chat":"ant_bailian","ant_deepseek_chat":"ant_deepseek","maya_embedding":"maya","SILICONFLOW_embedding":"openai","zdfmng_stream_chat":"zdfmng_stream","BaiLian_chat":"maas","Ollama_embedding":"openai"}}',1,null,null,'SYSTEM_CONFIG','system');
INSERT INTO kg_config (`id`,`gmt_create`,`gmt_modified`,`project_id`,`config_name`,`config_id`,`version`,`config`,`status`,`description`,`resource_id`,`resource_type`,`user_no`) VALUES(11,NOW(),NOW(),'0','FEEDBACK_REASON','FEEDBACK_REASON','1','[{"label":"理解","value":"UNDERSTAND","child":[{"value":"DISOBEYING_INSTRUCTIONS","label":"不理解问题/不服从指令"},{"value":"NOT_UNDERSTANDING_THE_CONTEXT","label":"不理解上下文"}]},{"label":"答案","value":"ANSWER","child":[{"value":"HALLUCINATIONS","label":"事实错误/幻觉"},{"value":"REASONING","label":"推理/计算错误"},{"value":"INCOMPLETE","label":"答案截断/不完整"},{"value":"REPEAT_OUTPUT","label":"重复输出"},{"value":"INCONSISTENT_LANGUAGE","label":"语言不一致"},{"value":"FORMAT_ERROR","label":"格式错误"},{"value":"CODE_SYNTAX_ERROR","label":"代码语法错误"},{"value":"IMAGE_COMPREHENSION_ERROR","label":"图片理解错误"},{"value":"LOW_QUALITY_IMAGE_GENERATION","label":"图片生成质量低"},{"value":"BORING","label":"内容没有帮助/无趣"},{"value":"CONTENT_IS_TOO_SHORT","label":"内容过短"},{"value":"REDUNDANCY","label":"内容过长/冗余"}]},{"label":"安全","value":"SAFETY","child":[{"value":"HARMFUL_CONTENT","label":"内容有害（违法违规）"},{"value":"ETHICS","label":"价值观（伦理道德）"},{"value":"PRIVACY_LEAKAGE","label":"隐私泄露"},{"value":"EXCESSIVE_REFUSAL_TO_ANSWER","label":"过度拒答"}]},{"label":"产品","value":"PRODUCT","child":[{"value":"NO_RESPONSE","label":"系统BUG/无响应"},{"value":"SLOW_OPERATION","label":"运行慢/体验差"}]}]',1,null,null,'CONFIG','admin');

INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(10,now(),now(),'BaiLing','BaiLing','1','SINGLE','chat','/img/logo/bailing.png','LLM','[]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(20,now(),now(),'BaiLian','BaiLian','1','SINGLE','chat,embedding','/img/logo/bailian.png','LLM,TEXT EMBEDDING','[]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(30,now(),now(),'DeepSeek','DeepSeek','1','ALL','chat','/img/logo/deepseek.png','LLM','[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://api.siliconflow.cn/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true}]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(40,now(),now(),'Ollama','Ollama','1','SINGLE','chat,embedding','/img/logo/ollama.png','LLM,TEXT EMBEDDING','[]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(50,now(),now(),'SILICONFLOW','SILICONFLOW','1','SINGLE','chat,embedding','/img/logo/siliconflow.png','LLM,TEXT EMBEDDING','[]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(60,now(),now(),'OpenAI','OpenAI','1','SINGLE','chat,embedding','/img/logo/openai.png','LLM,TEXT EMBEDDING','[]','system','system');
INSERT INTO kg_model_provider (`id`,`gmt_create`,`gmt_modified`,`name`,`provider`,`status`,`page_mode`,`model_type`,`logo`,`tags`,`params`,`creator`,`modifier`) VALUES(70,now(),now(),'OpenRouter','OpenRouter','1','SINGLE','chat','/img/logo/openrouter.png','LLM','[]','system','system');

INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(1,now(),now(),'BaiLing','chat',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://antchat.alipay.com/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(2,now(),now(),'BaiLian','chat',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://dashscope.aliyuncs.com/compatible-mode/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(3,now(),now(),'BaiLian','embedding',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://dashscope.aliyuncs.com/compatible-mode/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(4,now(),now(),'ollama','chat',null,'[{"inputType":"select","cname":"Model","ename":"model","required":true},{"inputType":"text","cname":"base_url","ename":"base_url","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(5,now(),now(),'ollama','embedding',null,'[{"inputType":"select","cname":"Model","ename":"model","required":true},{"inputType":"text","cname":"base_url","ename":"base_url","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(6,now(),now(),'SILICONFLOW','chat',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://api.siliconflow.cn/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(7,now(),now(),'SILICONFLOW','embedding',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://api.siliconflow.cn/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(8,now(),now(),'OpenAI','chat',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://api.openai.com"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(9,now(),now(),'OpenAI','embedding',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://api.openai.com"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');
INSERT INTO kg_provider_param (`id`,`gmt_create`,`gmt_modified`,`provider`,`model_type`,`model_name`,`params`,`creator`,`modifier`) VALUES(10,now(),now(),'OpenRouter','chat',null,'[{"inputType":"text","cname":"base_url","ename":"base_url","required":true,"defaultValue":"https://openrouter.ai/api/v1"},{"inputType":"text","cname":"API-Key","ename":"api_key","required":true},{"inputType":"text","cname":"Model","ename":"model","required":true}]','system','system');

INSERT INTO kg_model_detail (`id`,`gmt_create`,`gmt_modified`,`provider`,`type`,`name`,`description`,`params`,`creator`,`modifier`) VALUES(1,now(),now(),'DeepSeek','chat','deepseek-chat','deepseek-chat',null,'system','system');
INSERT INTO kg_model_detail (`id`,`gmt_create`,`gmt_modified`,`provider`,`type`,`name`,`description`,`params`,`creator`,`modifier`) VALUES(2,now(),now(),'DeepSeek','chat','deepseek-reasoner','deepseek-reasoner',null,'system','system');
