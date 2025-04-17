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
  `file_url` varchar(2560) NOT NULL COMMENT '文件地址',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `data_source_type` varchar(32) DEFAULT NULL COMMENT '数据源类型',
  `extension` longtext DEFAULT NULL COMMENT '扩展信息',
  `version` varchar(64) DEFAULT NULL COMMENT '版本号',
  `life_cycle` varchar(64) DEFAULT NULL COMMENT '执行周期类型',
  `action` varchar(64) DEFAULT NULL COMMENT '数据操作类型',
  `computing_conf` longtext DEFAULT NULL COMMENT '计算引擎配置',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_task_id` (`task_id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='图谱构建任务表';

CREATE TABLE `kg_scheduler_job` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
    `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
    `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
    `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
    `modify_user` varchar(32) DEFAULT NULL COMMENT '修改人',
    `name` varchar(64) NOT NULL COMMENT '任务名称',
    `life_cycle` varchar(64) NOT NULL COMMENT '调度周期类型',
    `translate_type` varchar(64) NOT NULL COMMENT '任务转换类型',
    `status` varchar(64) NOT NULL COMMENT '状态',
    `dependence` varchar(64) NOT NULL COMMENT '前置依赖',
    `scheduler_cron` varchar(128) DEFAULT NULL COMMENT '调度周期cron表达式',
    `last_execute_time` timestamp NULL COMMENT '最后一次执行时间',
    `invoker_id` bigint(20) unsigned DEFAULT NULL COMMENT '调用者id',
    `extension` longtext DEFAULT NULL COMMENT '扩展信息',
    `version` varchar(64) DEFAULT NULL COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_project_id` (`project_id`),
    KEY `idx_projcet_create_user_name` (`project_id`,`create_user`)
) DEFAULT CHARSET=utf8mb4 COMMENT='调度任务表';

CREATE TABLE `kg_scheduler_instance` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
 `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
 `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
 `create_user` varchar(32) DEFAULT NULL COMMENT '创建人',
 `modify_user` varchar(32) DEFAULT NULL COMMENT '修改人',
 `unique_id` varchar(128) NOT NULL COMMENT '调度实例唯一id',
 `job_id` bigint(20) unsigned NOT NULL COMMENT '调度任务ID',
 `type` varchar(64) NOT NULL COMMENT '实例类型',
 `status` varchar(64) NOT NULL COMMENT '实例状态',
 `progress` bigint(20) unsigned DEFAULT 0 COMMENT '进度',
 `begin_running_time` timestamp NULL COMMENT '实例开始时间',
 `finish_time` timestamp NULL COMMENT '实例完成时间',
 `life_cycle` varchar(64) NOT NULL COMMENT '调度周期类型',
 `dependence` varchar(64) NOT NULL COMMENT '前置依赖',
 `scheduler_date` timestamp NULL COMMENT '调度执行时间',
 `version` varchar(64) DEFAULT NULL COMMENT '版本号',
 `extension` longtext DEFAULT NULL COMMENT '扩展信息',
 `task_dag` longtext DEFAULT NULL COMMENT '示例调度DAG',
 PRIMARY KEY (`id`),
 KEY `idx_project_id` (`project_id`),
 KEY `idx_job_id` (`job_id`),
 UNIQUE KEY `uk_unique_id` (`unique_id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='调度实例表';


CREATE TABLE `kg_scheduler_task` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `project_id` bigint(20) unsigned NOT NULL COMMENT '项目ID',
 `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
 `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
 `job_id` bigint(20) unsigned NOT NULL COMMENT '调度任务ID',
 `instance_id` bigint(20) unsigned NOT NULL COMMENT '调度示例ID',
 `type` varchar(64) NOT NULL COMMENT '类型',
 `status` varchar(64) NOT NULL COMMENT '实例状态',
 `title` varchar(128) NOT NULL COMMENT '节点标题',
 `execute_num` bigint(20) unsigned DEFAULT 0 COMMENT '执行次数',
 `begin_time` timestamp NULL COMMENT '开始执行时间',
 `finish_time` timestamp NULL COMMENT '执行完成时间',
 `estimate_finish_time` timestamp NULL COMMENT '预估完成时间',
 `trace_log` longtext DEFAULT NULL COMMENT '执行日志',
 `lock_time` timestamp NULL COMMENT '抢锁时间',
 `resource` varchar(10240) DEFAULT NULL COMMENT '资源标记',
 `input` longtext DEFAULT NULL COMMENT '输入信息',
 `output` longtext DEFAULT NULL COMMENT '输出信息',
 `node_id` varchar(64) NOT NULL COMMENT '节点id',
 `extension` longtext DEFAULT NULL COMMENT '扩展信息',
 PRIMARY KEY (`id`),
 KEY `idx_project_id` (`project_id`),
 KEY `idx_job_id` (`job_id`),
 KEY `idx_instance_id` (`instance_id`),
 KEY `idx_type_status` (`type`,`status`),
 UNIQUE KEY `uk_instance_node_id` (`instance_id`,`node_id`)
) DEFAULT CHARSET=utf8mb4 COMMENT='调度作业节点表';


CREATE TABLE `kg_scheduler_info` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `gmt_create` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
 `gmt_modified` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '修改时间',
 `name` varchar(64) NOT NULL COMMENT '调度任务名称',
 `status` varchar(32) DEFAULT NULL COMMENT '状态',
 `period` bigint(20) DEFAULT 300 COMMENT '调度间隔，单位秒',
 `count` bigint(20) unsigned DEFAULT 0 COMMENT '失败次数',
 `log` longtext DEFAULT NULL COMMENT '日志内容',
 `config` longtext DEFAULT NULL COMMENT '配置信息',
 `lock_time` timestamp NULL COMMENT '抢锁时间',
 PRIMARY KEY (`id`),
 UNIQUE KEY `uk_name` (`name`)
) DEFAULT CHARSET=utf8mb4 COMMENT='调度任务记录表';

CREATE TABLE `kg_data_source`(
 `id`              bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
 `gmt_create`      timestamp     NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
 `gmt_modified`    timestamp     NOT NULL DEFAULT current_timestamp() on update current_timestamp () COMMENT '修改时间',
 `create_user`     varchar(64)   NOT NULL DEFAULT 'system' COMMENT '创建用户',
 `update_user`     varchar(64)   NOT NULL DEFAULT 'system' COMMENT '修改用户',
 `status`          varchar(64)   NOT NULL DEFAULT 'ENABLE' COMMENT '状态',
 `remark`          varchar(1024)          DEFAULT NULL COMMENT '描述',
 `type`            varchar(64)   NOT NULL DEFAULT 'MYSQL' COMMENT '数据源类型',
 `db_name`         varchar(256)  NOT NULL COMMENT '数据源名称',
 `db_url`          varchar(1024) NOT NULL COMMENT '数据库url',
 `db_user`         varchar(128)           DEFAULT NULL COMMENT '数据源用户名',
 `db_password`     varchar(128)           DEFAULT NULL COMMENT '数据源密码',
 `db_driver_name`  varchar(128)           DEFAULT NULL COMMENT '数据源驱动',
 `category`        varchar(64)            DEFAULT NULL COMMENT '数据源类别',
 `connection_info` longtext               DEFAULT NULL COMMENT '连接配置信息',
PRIMARY KEY (`id`),
UNIQUE KEY `uk_db_name` (`db_name`)
) DEFAULT CHARSET=utf8mb4 COMMENT='数据源管理表';


