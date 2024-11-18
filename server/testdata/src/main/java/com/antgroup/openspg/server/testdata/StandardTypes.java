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

package com.antgroup.openspg.server.testdata;

import static com.antgroup.openspg.server.testdata.TestCommons.THING;

import com.antgroup.openspg.core.schema.model.BasicInfo;
import com.antgroup.openspg.core.schema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.type.SPGTypeAdvancedConfig;
import com.antgroup.openspg.core.schema.model.type.StandardType;
import com.google.common.collect.Lists;

public class StandardTypes {

  public static final StandardType CHINA_MOBILE =
      new StandardType(
          new BasicInfo<>(newStandardIdentifier("ChinaMobile")),
          THING,
          Lists.newArrayList(),
          Lists.newArrayList(),
          new SPGTypeAdvancedConfig(),
          Boolean.TRUE,
          Lists.newArrayList(
              new RegularConstraint(
                  "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1,5,8,9]))[0-9]{8}$")));

  private static SPGTypeIdentifier newStandardIdentifier(String identifier) {
    return SPGTypeIdentifier.parse("STD" + "_" + identifier);
  }
}
