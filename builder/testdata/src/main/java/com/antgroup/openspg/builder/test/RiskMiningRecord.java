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

package com.antgroup.openspg.builder.test;

import com.antgroup.openspg.builder.model.record.EntityRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyRecord;
import com.antgroup.openspg.builder.model.record.property.SPGPropertyValue;
import com.antgroup.openspg.server.testdata.RiskMiningSchema;
import com.google.common.collect.Lists;

public class RiskMiningRecord {

  public static final EntityRecord PERSON_RECORD1 =
      new EntityRecord(
          RiskMiningSchema.PERSON,
          "0",
          Lists.newArrayList(
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("name"),
                  new SPGPropertyValue("裘**")),
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("age"), new SPGPropertyValue("58")),
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("hasPhone"),
                  new SPGPropertyValue("154****7458"))));

  public static final EntityRecord PERSON_RECORD1_NORMALIZED =
      new EntityRecord(
          RiskMiningSchema.PERSON,
          "0",
          Lists.newArrayList(
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("name"),
                  new SPGPropertyValue("裘**").setStds(Lists.newArrayList("裘**"))),
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("age"),
                  new SPGPropertyValue("58").setStds(Lists.newArrayList(58L))),
              new SPGPropertyRecord(
                  RiskMiningSchema.PERSON.getPropertyMap().get("hasPhone"),
                  new SPGPropertyValue("154****7458").setStds(Lists.newArrayList("154****7458")))));
}
