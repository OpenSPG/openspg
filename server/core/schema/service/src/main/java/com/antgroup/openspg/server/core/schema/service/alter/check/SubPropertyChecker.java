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

package com.antgroup.openspg.server.core.schema.service.alter.check;

import com.antgroup.openspg.core.schema.model.SchemaConstants;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.type.BasicType;
import com.antgroup.openspg.core.schema.model.type.BasicType.TextBasicType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;

public class SubPropertyChecker {

  private static final String PATTERN = "^[a-z][0-9a-zA-Z]*";
  private static final Pattern NAME_PATTERN = Pattern.compile(PATTERN);

  public void check(Property property) {
    if (CollectionUtils.isEmpty(property.getSubProperties())) {
      return;
    }

    for (SubProperty subProperty : property.getSubProperties()) {
      if (subProperty.isDelete()) {
        continue;
      }

      this.checkBasicInfo(subProperty);
      this.checkConstraint(subProperty);
    }
    this.checkNameDuplicated(property.getSubProperties());
  }

  private void checkBasicInfo(SubProperty subProperty) {
    if (null == subProperty.getBasicInfo()) {
      throw new IllegalArgumentException("sub property's basic info can not be null");
    }
    if (null == subProperty.getName()) {
      throw new IllegalArgumentException("sub property's name can not be null");
    }
    if (null == subProperty.getBasicInfo().getNameZh()) {
      throw new IllegalArgumentException(
          String.format("nameZh of sub property: %s can not be null", subProperty.getName()));
    }
    if (!NAME_PATTERN.matcher(subProperty.getName()).matches()) {
      throw new IllegalArgumentException(
          String.format(
              "name of sub property: %s not match pattern: %s", subProperty.getName(), PATTERN));
    }
    if (subProperty.getName().length() > SchemaConstants.SCHEMA_PROPERTY_MAX_NAME) {
      throw new IllegalArgumentException(
          String.format(
              "the length of sub property's name: %s can not be larger than: %s",
              subProperty.getName(), SchemaConstants.SCHEMA_PROPERTY_MAX_NAME));
    }
    if (subProperty.getBasicInfo().getNameZh().length()
        > SchemaConstants.SCHEMA_PROPERTY_MAX_NAME_ZH) {
      throw new IllegalArgumentException(
          String.format(
              "the length of sub property's nameZh: %s can not be larger than: %s",
              subProperty.getBasicInfo().getNameZh(), SchemaConstants.SCHEMA_PROPERTY_MAX_NAME_ZH));
    }
    if (null == subProperty.getObjectTypeRef()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef of sub property: %s can not be null", subProperty.getName()));
    }
    if (null == subProperty.getObjectTypeRef().getBasicInfo()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef.basicInfo of sub property: %s can not be null",
              subProperty.getName()));
    }
    if (!subProperty.getObjectTypeRef().isBasicType()) {
      throw new IllegalArgumentException(
          String.format(
              "objectTypeRef of sub property: %s must be basic type", subProperty.getName()));
    }
  }

  private void checkConstraint(SubProperty subProperty) {
    if (!(BasicType.from(subProperty.getObjectTypeRef().getName()) instanceof TextBasicType)) {
      CommonChecker.containForbiddenConstraintType(
          subProperty.getConstraint(), ConstraintTypeEnum.MULTI_VALUE);
    }
  }

  private void checkNameDuplicated(List<SubProperty> subProperties) {
    Set<String> names = new HashSet<>();
    for (SubProperty subProperty : subProperties) {
      if (subProperty.isDelete()) {
        continue;
      }

      String name = subProperty.getName();
      if (names.contains(name)) {
        throw new IllegalArgumentException(
            String.format("sub property name: %s is duplicated", name));
      }
      names.add(name);
    }
  }
}
