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

package com.antgroup.openspg.server.core.schema.service.type.model;

import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default property enums of spg type, currently only ConceptType has default property, the default
 * property is created by system when the spg type is created, and deleted by system when the spg
 * type is deleted.
 */
public enum BuiltInPropertyEnum {

  /** Standard id of concept, such as area code of county. */
  STD_ID("CONCEPT_TYPE", "stdId", "标准ID", "标准ID", false, "Text", "BASIC_TYPE", null),

  /** Alias of concept, such as capital of BeiJing city. */
  ALIAS("CONCEPT_TYPE", "alias", "别名", "别名", true, "Text", "BASIC_TYPE", null),

  /** Time that event occurs, the format of value must be unix timestamp. */
  EVENT_TIME(
      "EVENT_TYPE", "eventTime", "发生时间", "发生时间", false, "STD.Timestamp", "STANDARD_TYPE", "TIME"),
  ;

  /**
   * Spg type enum
   *
   * @see SPGTypeEnum
   */
  private final String spgTypeEnum;

  /** Name of the default property. */
  private final String name;

  /** Chinese name of the default property. */
  private final String nameZh;

  /** Description of the default property. */
  private final String desc;

  /** If the property value is multi. */
  private final boolean multiValue;

  /** Spg type of the default property's value. */
  private final String valueType;

  /**
   * The enum of value type
   *
   * @see SPGTypeEnum
   */
  private final String valueTypeEnum;

  /** */
  private final String propertyGroup;

  BuiltInPropertyEnum(
      String spgTypeEnum,
      String name,
      String nameZh,
      String desc,
      boolean multiValue,
      String valueType,
      String valueTypeEnum,
      String propertyGroup) {
    this.spgTypeEnum = spgTypeEnum;
    this.name = name;
    this.nameZh = nameZh;
    this.desc = desc;
    this.multiValue = multiValue;
    this.valueType = valueType;
    this.valueTypeEnum = valueTypeEnum;
    this.propertyGroup = propertyGroup;
  }

  public String getName() {
    return name;
  }

  public String getNameZh() {
    return nameZh;
  }

  public String getDesc() {
    return desc;
  }

  public boolean isMultiValue() {
    return multiValue;
  }

  public String getValueType() {
    return valueType;
  }

  public String getSpgTypeEnum() {
    return spgTypeEnum;
  }

  public String getValueTypeEnum() {
    return valueTypeEnum;
  }

  public String getPropertyGroup() {
    return propertyGroup;
  }

  /**
   * Get default properties of one kind of spg type.
   *
   * @param spgTypeEnum spg type enum
   * @return list of default property
   */
  public static List<BuiltInPropertyEnum> getBuiltInProperty(SPGTypeEnum spgTypeEnum) {
    List<BuiltInPropertyEnum> defaultPropertyEnums = Lists.newArrayList();
    for (BuiltInPropertyEnum defaultPropertyEnum : BuiltInPropertyEnum.values()) {
      if (spgTypeEnum.name().equals(defaultPropertyEnum.getSpgTypeEnum())) {
        defaultPropertyEnums.add(defaultPropertyEnum);
      }
    }
    return defaultPropertyEnums;
  }

  public static Set<String> getBuiltInPropertyName(SPGTypeEnum spgTypeEnum) {
    return Arrays.stream(BuiltInPropertyEnum.values())
        .filter(typeEnum -> spgTypeEnum.name().equals(typeEnum.getSpgTypeEnum()))
        .map(BuiltInPropertyEnum::getName)
        .collect(Collectors.toSet());
  }
}
