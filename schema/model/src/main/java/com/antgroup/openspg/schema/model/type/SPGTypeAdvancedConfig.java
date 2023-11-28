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

package com.antgroup.openspg.schema.model.type;

import com.antgroup.openspg.common.model.base.BaseValObj;

/**
 * Advanced configurations for SPG types, including configurations that are unique to different
 * types, such as extraction operator for event types and normalization operator for concept types.
 */
public class SPGTypeAdvancedConfig extends BaseValObj {

  private static final long serialVersionUID = 5985750316789734122L;

  /** The visible scope of the schema type. */
  private VisibleScopeEnum visibleScope;

  /** The linking operator. */
  private OperatorKey linkOperator;

  /** The fusion operator. */
  private OperatorKey fuseOperator;

  /** The extraction operator. */
  private OperatorKey extractOperator;

  /** Execute in knowledge process, to nomalize property value that matches constraint. */
  private OperatorKey normalizedOperator;

  public SPGTypeAdvancedConfig() {
    this(VisibleScopeEnum.DOMAIN);
  }

  public SPGTypeAdvancedConfig(VisibleScopeEnum visibleScope) {
    this.visibleScope = visibleScope;
  }

  public VisibleScopeEnum getVisibleScope() {
    return visibleScope;
  }

  public SPGTypeAdvancedConfig setVisibleScope(VisibleScopeEnum visibleScope) {
    this.visibleScope = visibleScope;
    return this;
  }

  public OperatorKey getLinkOperator() {
    return linkOperator;
  }

  public void setLinkOperator(OperatorKey linkOperator) {
    this.linkOperator = linkOperator;
  }

  public OperatorKey getFuseOperator() {
    return fuseOperator;
  }

  public void setFuseOperator(OperatorKey fuseOperator) {
    this.fuseOperator = fuseOperator;
  }

  public OperatorKey getExtractOperator() {
    return extractOperator;
  }

  public void setExtractOperator(OperatorKey extractOperator) {
    this.extractOperator = extractOperator;
  }

  public OperatorKey getNormalizedOperator() {
    return normalizedOperator;
  }

  public void setNormalizedOperator(OperatorKey normalizedOperator) {
    this.normalizedOperator = normalizedOperator;
  }
}
