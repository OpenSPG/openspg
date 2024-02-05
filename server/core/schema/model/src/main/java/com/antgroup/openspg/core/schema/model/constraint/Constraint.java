/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.core.schema.model.constraint;

import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Constraint information, generally acts on {@link Property}, and requires that the value range of
 * the property must meet the constraint conditions. Constraints include non-null, unique,
 * multi-value, enumeration, regular pattern, numerical interval constraints, etc. When the
 * attribute is configured with constraints, it will check whether the property value meets the
 * constraint conditions during knowledge importing, and only the property value that meet the
 * constraint conditions will be written to the storage.
 */
public class Constraint extends BaseModel {

  private static final long serialVersionUID = -8877939106327053823L;

  /** Unique id */
  private Long id;

  /** The constraint items */
  private List<BaseConstraintItem> constraintItems = new ArrayList<>();

  public Constraint() {}

  public Constraint(Long id, List<BaseConstraintItem> constraintItems) {
    this.id = id;
    this.constraintItems = constraintItems.stream().sorted().collect(Collectors.toList());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<BaseConstraintItem> getConstraintItems() {
    return constraintItems;
  }

  public void setConstraintItems(List<BaseConstraintItem> constraintItems) {
    this.constraintItems = constraintItems;
  }

  public boolean contains(ConstraintTypeEnum constraintTypeEnum) {
    if (CollectionUtils.isEmpty(constraintItems)) {
      return false;
    }

    for (BaseConstraintItem item : constraintItems) {
      if (constraintTypeEnum.equals(item.getConstraintTypeEnum())) {
        return true;
      }
    }
    return false;
  }
}
