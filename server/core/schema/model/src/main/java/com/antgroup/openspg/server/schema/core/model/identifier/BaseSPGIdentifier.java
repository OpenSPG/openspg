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

package com.antgroup.openspg.server.schema.core.model.identifier;

import com.antgroup.openspg.common.model.base.BaseValObj;

/**
 * This base class of SPG identifier. Currently, there are two methods for SPG naming. The first is
 * the node naming method, which consists of a namespace and a real name.
 *
 * <p>The second is the edge naming method, which consists of the starting node and the ending node
 * plus the real name of the edge.
 */
public abstract class BaseSPGIdentifier extends BaseValObj {

  private static final long serialVersionUID = -1365635665645757525L;

  /** SPG naming type */
  private final SPGIdentifierTypeEnum identityType;

  protected BaseSPGIdentifier(SPGIdentifierTypeEnum nameType) {
    this.identityType = nameType;
  }

  public SPGIdentifierTypeEnum getIdentifierType() {
    return identityType;
  }

  @Override
  public abstract String toString();
}
