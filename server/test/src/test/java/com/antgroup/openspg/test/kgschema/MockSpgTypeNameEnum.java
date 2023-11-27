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

package com.antgroup.openspg.test.kgschema;

import com.antgroup.openspg.server.core.schema.model.type.SPGTypeEnum;
import java.util.ArrayList;
import java.util.List;

public enum MockSpgTypeNameEnum {
  TEXT("Text", SPGTypeEnum.BASIC_TYPE, true),

  FLOAT("Float", SPGTypeEnum.BASIC_TYPE, true),

  INTEGER("Integer", SPGTypeEnum.BASIC_TYPE, true),

  STD_MOBILE("STD.ChinaMobile", SPGTypeEnum.STANDARD_TYPE, true),
  STD_EMAIL("STD.Email", SPGTypeEnum.STANDARD_TYPE, true),
  STD_GENDER("STD.Gender", SPGTypeEnum.STANDARD_TYPE, true),
  STD_BOOLEAN("STD.Boolean", SPGTypeEnum.STANDARD_TYPE, true),
  STD_TIME_STAMP("STD.Timestamp", SPGTypeEnum.STANDARD_TYPE, true),

  STD_ALIPAY_ID("STD.AlipayId", SPGTypeEnum.STANDARD_TYPE, false),

  DEFAULT_COMPANY("DEFAULT.Company", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_DEVICE("DEFAULT.Device", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_APP("DEFAULT.App", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_GOODS("DEFAULT.Goods", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_ADMINISTRATION("DEFAULT.Administration", SPGTypeEnum.CONCEPT_TYPE, false),

  DEFAULT_MEMBER_DEGREE("DEFAULT.MemberDegree", SPGTypeEnum.CONCEPT_TYPE, false),

  DEFAULT_TAXOMOMY_OF_PERSON("DEFAULT.TaxonomyOfPerson", SPGTypeEnum.CONCEPT_TYPE, false),

  DEFAULT_PERSON("DEFAULT.Person", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_ALIPAY_USER("DEFAULT.AlipayUser", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_ALIPAY_MEMBER("DEFAULT.AlipayMember", SPGTypeEnum.ENTITY_TYPE, false),

  DEFAULT_EXCHANGE_GOODS("DEFAULT.ExchangeGoods", SPGTypeEnum.EVENT_TYPE, false),
  ;

  private final String name;

  private final SPGTypeEnum spgTypeEnum;

  private final boolean init;

  MockSpgTypeNameEnum(String name, SPGTypeEnum spgTypeEnum, boolean init) {
    this.name = name;
    this.spgTypeEnum = spgTypeEnum;
    this.init = init;
  }

  public String getName() {
    return name;
  }

  public SPGTypeEnum getSpgTypeEnum() {
    return spgTypeEnum;
  }

  public boolean isInit() {
    return init;
  }

  public static List<MockSpgTypeNameEnum> getBasicType() {
    List<MockSpgTypeNameEnum> basicTypes = new ArrayList<>();
    for (MockSpgTypeNameEnum name : MockSpgTypeNameEnum.values()) {
      if (SPGTypeEnum.BASIC_TYPE.equals(name.getSpgTypeEnum())) {
        basicTypes.add(name);
      }
    }
    return basicTypes;
  }

  public static List<MockSpgTypeNameEnum> getInitStandardType() {
    List<MockSpgTypeNameEnum> basicTypes = new ArrayList<>();
    for (MockSpgTypeNameEnum name : MockSpgTypeNameEnum.values()) {
      if (SPGTypeEnum.STANDARD_TYPE.equals(name.getSpgTypeEnum()) && name.init) {
        basicTypes.add(name);
      }
    }
    return basicTypes;
  }

  public static List<MockSpgTypeNameEnum> getCustomizedType() {
    List<MockSpgTypeNameEnum> basicTypes = new ArrayList<>();
    for (MockSpgTypeNameEnum name : MockSpgTypeNameEnum.values()) {
      if (!name.init) {
        basicTypes.add(name);
      }
    }
    return basicTypes;
  }
}
