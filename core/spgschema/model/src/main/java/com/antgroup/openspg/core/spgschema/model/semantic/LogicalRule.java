/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.core.spgschema.model.semantic;

import com.antgroup.openspg.common.model.UserInfo;
import com.antgroup.openspg.common.model.base.BaseModel;

/**
 * The logic rule that defined on the property or relation<br>
 * <p>the logic rule usually contains the following core attributes：
 * <ul>
 *     <li>{@link LogicalRule#code}: unique id of logic rule</li>
 *     <li>{@link LogicalRule#version}: one rule can have multi versions</li>
 *     <li>{@link LogicalRule#status}: the status of the rule</li>
 *     <li>{@link LogicalRule#content}: the content of KGDSL</li>
 * </ul>
 * </p>
 */
public class LogicalRule extends BaseModel {

    private static final long serialVersionUID = -1513161593222131407L;

    /**
     * The unique id of the rule.
     */
    private RuleCode code;

    /**
     * The name of the rule.
     */
    private String name;

    /**
     * the version of the rule
     */
    private Integer version;

    /**
     * If the version of rule is master.
     */
    private Boolean isMaster;

    /**
     * The status of rule.
     */
    private RuleStatusEnum status;

    /**
     * The content of rule, usually is used by rule engine.
     */
    private String content;

    /**
     * The userId
     */
    private UserInfo creator;

    public LogicalRule() {

    }

    public LogicalRule(
        RuleCode ruleCode,
        String ruleName,
        String ruleContent) {
        this(ruleCode, null, ruleName, null, null, ruleContent, null);
    }

    public LogicalRule(
        RuleCode ruleCode,
        Integer version,
        String ruleName,
        Boolean isMaster,
        RuleStatusEnum ruleStatus,
        String ruleContent,
        UserInfo creator) {
        this.code = ruleCode;
        this.version = version;
        this.name = ruleName;
        this.isMaster = isMaster;
        this.status = ruleStatus;
        this.content = ruleContent;
        this.creator = creator;
    }

    public RuleCode getCode() {
        return code;
    }

    public Integer getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public Boolean getMaster() {
        return isMaster;
    }

    public RuleStatusEnum getStatus() {
        return status;
    }

    public String getContent() {
        return content;
    }

    public UserInfo getCreator() {
        return creator;
    }

    public void setCode(RuleCode code) {
        this.code = code;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
