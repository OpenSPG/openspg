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

CREATE TABLE `kg_reason_session` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `name` varchar(1024) NOT NULL COMMENT '会话名称',
  `description` longtext DEFAULT NULL COMMENT '会话描述信息',
  `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`,`id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='图谱推理任务会话表';

CREATE TABLE `kg_reason_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `session_id` bigint(20) unsigned NOT NULL COMMENT '会话ID',
  `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
  `mark` varchar(16) DEFAULT 'NULL' COMMENT '收藏状态',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `dsl` longtext DEFAULT NULL COMMENT 'DSL执行语句',
  `nl` longtext DEFAULT NULL COMMENT '自然语言查询语句',
  `params` longtext DEFAULT NULL COMMENT '参数',
  `result_message` longtext DEFAULT NULL COMMENT '执行结果，错误信息',
  `result_table` longtext DEFAULT NULL COMMENT '执行结果，表格数据',
  `result_nodes` longtext DEFAULT NULL COMMENT '执行结果，点数据',
  `result_edges` longtext DEFAULT NULL COMMENT '执行结果，边数据',
  `result_paths` longtext DEFAULT NULL COMMENT '执行结果，路径数据',
  PRIMARY KEY (`id`),
  KEY `idx_session_id_id` (`session_id`,`id`),
  KEY `idx_project_user_mark` (`project_id`,`user_id`,`mark`),
  KEY `idx_user_mark_id` (`user_id`,`mark`,`id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='图谱推理任务表';

CREATE TABLE `kg_reason_tutorial` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
  `enable` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态',
  `name` varchar(1024) DEFAULT NULL COMMENT '名称',
  `dsl` longtext DEFAULT NULL COMMENT 'DSL执行语句',
  `nl` longtext DEFAULT NULL COMMENT '自然语言查询语句',
  `params` longtext DEFAULT NULL COMMENT '参数',
  `description` longtext DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`),
  KEY `idx_project_status` (`project_id`,`enable`)
) DEFAULT CHARSET=utf8mb4 COMMENT='图谱推理教程信息表';

CREATE TABLE `kg_builder_job` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
  `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
  `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
  `create_user` varchar(32) DEFAULT 'NULL' COMMENT '创建人',
  `modify_user` varchar(32) DEFAULT 'NULL' COMMENT '修改人',
  `task_id` bigint(20) unsigned DEFAULT NULL COMMENT '实例任务ID',
  `job_name` varchar(64) NOT NULL COMMENT '名称',
  `chunk_num` bigint(20) unsigned DEFAULT 0 COMMENT '分段数',
  `file_url` varchar(256) NOT NULL COMMENT '文件地址',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `extension` longtext DEFAULT NULL COMMENT '扩展信息',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_task_id` (`task_id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='图谱构建任务表';


