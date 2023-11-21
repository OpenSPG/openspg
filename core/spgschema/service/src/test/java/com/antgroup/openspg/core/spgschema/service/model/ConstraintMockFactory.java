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

package com.antgroup.openspg.core.spgschema.service.model;

import com.antgroup.openspg.core.spgschema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.RegularConstraint;
import com.google.common.collect.Lists;
import java.util.List;

public class ConstraintMockFactory {

  public static Constraint mockGenderEnumConstraint() {
    Constraint constraint = new Constraint();
    EnumConstraint gender = new EnumConstraint(Lists.newArrayList("male", "female"));
    constraint.getConstraintItems().add(gender);
    return constraint;
  }

  public static List<BaseConstraintItem> mockMobileConstraintItem() {
    return Lists.newArrayList(
        new MultiValConstraint(),
        new RegularConstraint(
            "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$"));
  }
}
