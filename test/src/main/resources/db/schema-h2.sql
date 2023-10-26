
CREATE TABLE `kg_project_info` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT comment '主键',
                                   `name` varchar(255) NOT NULL comment '项目名称',
                                   `description` varchar(1024) DEFAULT NULL comment '项目描述信息',
                                   `status` varchar(20) NOT NULL DEFAULT 'INVALID' comment 'DELETE:删除 VALID:有效 INVALID：无效',
                                   `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                   `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                   `namespace` varchar(64) NOT NULL DEFAULT '' comment '命名空间',
                                   `biz_domain_id` BIGINT DEFAULT NULL comment '业务域主键'
);


CREATE TABLE `kg_biz_domain` (
                                 `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                 `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                 `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                 `name` varchar(100) DEFAULT NULL comment '名称',
                                 `status` varchar(20) DEFAULT NULL comment '状态。VALID - 有效 DELETE - 逻辑删除',
                                 `description` varchar(1024) DEFAULT NULL comment '描述',
                                 `global_config` varchar(10240) DEFAULT NULL comment '全局配置'
);

CREATE TABLE `kg_data_source` (
                                  `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                  `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                  `unique_name` varchar(64) NOT NULL comment '唯一名称',
                                  `type` varchar(32) NOT NULL comment '类型 ',
                                  `conn_info` text NOT NULL comment '链接信息',
                                  `physical_info` text DEFAULT NULL comment '物理信息'
) ;

CREATE TABLE `kg_data_source_usage` (
                                        `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                        `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `data_source_name` varchar(64) NOT NULL comment '数据源名称',
                                        `usage_type` varchar(32) NOT NULL comment '数据源使用场景',
                                        `is_default` tinyint  NOT NULL comment '是否默认资源',
                                        `mount_object_id` varchar(32) NOT NULL comment '挂载对象Id',
                                        `mount_object_type` varchar(16) NOT NULL comment '挂载对象类型'
) ;

CREATE TABLE `kg_sys_lock` (
                               `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                               `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                               `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                               `method_name` varchar(128) DEFAULT NULL comment '方法名',
                               `method_value` varchar(128) DEFAULT NULL comment '方法值'
) ;

CREATE TABLE `kg_operator_overview` (
                                        `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                        `name` varchar(256) NOT NULL comment '算子名称',
                                        `type` varchar(64) NOT NULL comment '算子类型',
                                        `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `description` varchar(2048) NOT NULL comment '算子描述',
                                        `lang` varchar(64) DEFAULT NULL comment '算子开发语言'
) ;

CREATE TABLE `kg_operator_version` (
                                       `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                       `overview_id` bigint NOT NULL comment '算子总览id',
                                       `main_class` varchar(256) DEFAULT NULL comment '主类',
                                       `jar_address` varchar(1024) DEFAULT NULL comment 'jar包地址',
                                       `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                       `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                       `version` bigint NOT NULL comment '版本'
) ;

CREATE TABLE `kg_ontology_entity` (
                                      `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                      `original_id` bigint NOT NULL DEFAULT '0' comment '类型的原始ID',
                                      `name` varchar(255) NOT NULL comment '类型具体名称，比如‘Car’',
                                      `name_zh` varchar(255) NOT NULL comment '类型具体中文名称',
                                      `entity_category` varchar(20) NOT NULL comment '',
                                      `layer` varchar(20) DEFAULT NULL comment '类型所属层次，“CORE”：核心层，“EXTENSION”:扩展层',
                                      `description` varchar(1024) DEFAULT NULL comment '当前类型的说明/描述信息',
                                      `description_zh` varchar(1024) DEFAULT NULL comment '当前类型的中文说明/描述信息即jsonLd中的\"@id\"',
                                      `status` char(1) NOT NULL DEFAULT '0' comment '9：删除  1：有效 0:无效 默认',
                                      `with_index` varchar(20) NOT NULL DEFAULT 'TRUE' comment '',
                                        `scope` varchar(20) DEFAULT NULL comment '公有私有标识:PUBLIC,PRIVATE',
                                        `version` int NOT NULL DEFAULT '0' comment '版本',
                                        `version_status` varchar(50) NOT NULL DEFAULT 'ONLINE' comment '迭代版本状态:ONLINE:线上版本、LATEST:最新版本、EFFICIENT:生效版本、HISTORY:历史版本、DISCARDED:废弃版本',
                                        `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `transformer_id` bigint NOT NULL DEFAULT '0' comment '算子ID',
                                        `operator_config` text DEFAULT NULL comment '算子配置,json格式文本',
                                        `config` text DEFAULT NULL comment '实体类型配置',
                                        `unique_name` varchar(255) DEFAULT NULL comment '唯一名称'
) ;

CREATE TABLE `kg_ontology_entity_parent` (
`id` bigint NOT NULL AUTO_INCREMENT comment '主键',
`entity_id` bigint NOT NULL comment '类型唯一标识',
`parent_id` bigint NOT NULL comment '父类型唯一标识,根节点“-1”',
`status` char(1) NOT NULL DEFAULT '0' comment '9：删除  1：有效 0:无效 默认',
`gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
`gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
`path` varchar(4096) DEFAULT NULL comment '继承路径',
`deep_inherit` char(1) DEFAULT NULL comment '是否是深度继承,取值：Y，N',
`history_path` varchar(4096) DEFAULT NULL comment '历史继承关系'
) ;

CREATE TABLE `kg_ontology_entity_property_range` (
`id` bigint NOT NULL AUTO_INCREMENT comment '主键',
`domain_id` bigint NOT NULL comment '类型唯一标识或边属性边唯一标识',
`property_name` varchar(255) NOT NULL comment '数据或者对象属性英文名',
`range_id` bigint NOT NULL comment '属性值域唯一标识或边属性属性值域唯一标识',
`property_name_zh` varchar(255) NOT NULL comment '数据或者对象属性中文名',
`constraint_id` bigint NOT NULL comment '数据属性约束ID',
`property_category` varchar(20) NOT NULL comment '',
                                      `map_type` varchar(20) NOT NULL DEFAULT 'TYPE' comment '标识映射是类型-》属性-》值域还是边的属性-》边属性的属性-》边属性的属性的值域，\"TYPE\":类型映射 \"EDGE\":边属性映射',
                                      `version` int NOT NULL DEFAULT '0' comment '版本',
                                      `status` char(1) NOT NULL comment '9：删除 1：有效 0:无效 默认 和其他schema表对齐',
                                      `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                      `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                      `original_id` bigint NOT NULL DEFAULT '0' comment 'spo多版本的原始ID',
                                      `store_property_name` varchar(255) DEFAULT NULL comment '数据属性对应的存储属性名',
                                      `transformer_id` bigint NOT NULL DEFAULT '0' comment '算子ID',
                                      `property_desc` varchar(1024) DEFAULT NULL comment '属性描述',
                                      `property_desc_zh` varchar(1024) DEFAULT NULL comment '属性中文描述',
                                      `project_id` bigint NOT NULL DEFAULT '0' comment '项目ID',
                                      `original_domain_id` bigint NOT NULL DEFAULT '0' comment '类型或边的唯一原始标识',
                                      `original_range_id` bigint NOT NULL DEFAULT '0' comment '类型的唯一原始标识',
                                      `version_status` varchar(50) DEFAULT NULL comment '迭代版本状态:ONLINE:线上版本、LATEST:最新版本、EFFICIENT:生效版本、HISTORY:历史版本、DISCARDED:废弃版本',
                                      `relation_source` varchar(2550) DEFAULT NULL comment '记录关系对应的属性(用于属性转关系)',
                                      `direction` varchar(10) DEFAULT NULL comment 'BOTH:表示双向边',
                                      `mask_type` varchar(20) DEFAULT NULL comment '数据加密规则。',
                                      `multiver_config` varchar(1024) DEFAULT NULL comment '多版本配置,json格式文本',
                                      `property_source` bigint DEFAULT NULL comment '属性的来源，对应全局属性的id',
                                      `property_config` text DEFAULT NULL comment '针对属性的配置信息，如运营配置'
) ;

CREATE TABLE `kg_project_entity` (
                                     `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                     `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                     `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                     `project_id` bigint NOT NULL comment '项目id',
                                     `entity_id` bigint NOT NULL comment '本体类型id',
                                     `version` int NOT NULL DEFAULT '0' comment '版本',
                                     `version_status` varchar(50) NOT NULL DEFAULT 'ONLINE' comment '迭代版本状态:ONLINE:线上版本、EFFICTIVE:生效版本、RELEASED:已发布版本、DISCARD:废弃版本',
                                     `referenced` char(1) NOT NULL comment '标志是否是引用的类型。Y:是，N:不是',
                                     `type` varchar(64) DEFAULT 'ENTITY_TYPE' comment '引入的资源类型，关系（RELATION_TYPE）和实体类型（ENTITY_TYPE），默认ENTITY_TYPE',
                                     `ref_source` varchar(64) DEFAULT NULL comment '引用来源，corekg:COREKG, 项目:PROJECT'

) ;

CREATE TABLE `kg_ontology_semantic` (
                                        `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                        `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `resource_id` varchar(128) NOT NULL comment '关联资源id',
                                        `semantic_type` varchar(64) NOT NULL comment '谓词',
                                        `original_resource_id` varchar(64) NOT NULL comment '被关联资源id',
                                        `resource_type` varchar(64) DEFAULT NULL comment '资源类型：entity_type、relation_type、property，可为空，也可有其他类型',
                                        `status` int NOT NULL comment '状态，0:删除 1：有效',
                                        `config` text DEFAULT NULL comment '预留，谓词额外信息',
                                        `rule_id` varchar(128) DEFAULT NULL comment '关联规则ID',
                                        `subject_meta_type` varchar(128) DEFAULT NULL comment '主体元概念名',
                                        `object_meta_type` varchar(128) DEFAULT NULL comment '客体元概念名'
) ;

CREATE TABLE `kg_semantic_rule` (
                                    `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                    `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                    `gmt_modified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                    `name` varchar(255) DEFAULT NULL comment '名称',
                                    `expression` text NOT NULL comment '内容',
                                    `version_id` int NOT NULL comment '版本号',
                                    `status` varchar(60) NOT NULL comment '状态',
                                    `user_no` varchar(255) NOT NULL comment '用户ID',
                                    `is_master` tinyint DEFAULT NULL comment '是否主版本',
                                    `rule_id` varchar(512) DEFAULT NULL comment '规则ID',
                                    `effect_scope` varchar(60) DEFAULT NULL comment '生效范围'
) ;

CREATE TABLE `kg_ontology_property_constraint` (
                                                   `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
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
                                                   `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                                   `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                                   `enum_value` text comment '枚举值',
                                                   `is_multi_value` char(1) DEFAULT NULL comment '是否多值，Y：多值'
) ;

CREATE TABLE `kg_ontology_release` (
                                       `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                       `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                       `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                       `project_id` bigint NOT NULL comment '项目ID',
                                       `version` int NOT NULL comment '发布版本',
                                       `schema_view` longtext DEFAULT NULL comment '当前版本schema视图',
                                       `user_id` varchar(20) NOT NULL comment '发布人',
                                       `description` text NOT NULL comment '发布描述',
                                       `status` varchar(20) NOT NULL comment '状态',
                                       `change_procedure_id` text DEFAULT NULL comment '变更流程id',
                                       `operation_detail` text DEFAULT NULL comment '（废弃）本次发布的操作详情',
                                       `error_detail` text DEFAULT NULL comment '失败详情',
                                       `operation_info` text DEFAULT NULL comment '本次发布的操作详情'
) ;

CREATE TABLE `kg_spg_job_info` (
                                   `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                   `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                   `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                   `name` varchar(64) NOT NULL comment '任务名称',
                                   `type` varchar(20) NOT NULL comment '任务类型',
                                   `project_id` bigint NOT NULL comment '项目ID',
                                   `cron` varchar(20) NOT NULL comment 'cron表达式，null时则为单次调度',
                                   `status` varchar(20) NOT NULL comment '状态',
                                   `ext_info` varchar(1024) DEFAULT NULL comment '扩展字段'
) ;

CREATE TABLE `kg_schedule_job_info` (
                                        `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                        `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `name` varchar(64) NOT NULL comment '任务名称',
                                        `type` varchar(20) NOT NULL comment '任务类型',
                                        `biz_id` varchar(20) NOT NULL comment '业务标识id',
                                        `cron` varchar(20) NOT NULL comment 'cron表达式，null时则为单次调度',
                                        `status` varchar(20) NOT NULL comment '状态'
) ;

CREATE TABLE `kg_schedule_job_inst` (
                                        `id` bigint NOT NULL AUTO_INCREMENT comment '主键',
                                        `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
                                        `gmt_modified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
                                        `job_id` bigint NOT NULL comment '调度任务id',
                                        `type` varchar(20) NOT NULL comment '任务类型',
                                        `status` varchar(20) NOT NULL comment '状态',
                                        `result` text DEFAULT NULL comment '运行结果',
                                        `host` varchar(32) DEFAULT NULL comment '运行机器',
                                        `trace_id` varchar(32) DEFAULT NULL comment '任务最近一次运行的trace_id',
                                        `idempotent_id` varchar(64) DEFAULT NULL comment '幂等键，即业务jobInstId'
) ;
