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

package com.antgroup.openspg.server.infra.dao.repository.schema.convertor;

import com.antgroup.openspg.core.schema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.schema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.schema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.schema.model.constraint.NotNullConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RangeConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.schema.model.constraint.UniqueConstraint;
import com.antgroup.openspg.server.infra.dao.repository.schema.config.ConstraintItemConfigDO;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ConstraintEnum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class ConstraintItemConfigDOConvertor {

  /**
   * convert constraint config to AbstractConstraintItem object.
   *
   * @param configDOS
   * @return
   */
  public static List<BaseConstraintItem> toConstraintItem(List<ConstraintItemConfigDO> configDOS) {
    if (CollectionUtils.isEmpty(configDOS)) {
      return Collections.emptyList();
    }

    List<BaseConstraintItem> constraintItems = Lists.newArrayList();

    List<ConstraintItemConfigDO> rangConfigDOS = Lists.newArrayList();
    for (ConstraintItemConfigDO config : configDOS) {
      ConstraintEnum itemEnum = ConstraintEnum.getConstraint(config.getId());
      switch (itemEnum) {
        case REQUIRE:
          constraintItems.add(new NotNullConstraint());
          break;
        case UNIQUE:
          constraintItems.add(new UniqueConstraint());
          break;
        case ENUM:
          List<String> enumValues = new ArrayList<>();
          if (config.getValue() instanceof List) {
            enumValues = (List) config.getValue();
          }
          constraintItems.add(new EnumConstraint(enumValues));
          break;
        case REGULAR:
          constraintItems.add(new RegularConstraint(String.valueOf(config.getValue())));
          break;
        case MULTIVALUE:
          constraintItems.add(new MultiValConstraint());
          break;
        case RANGE:
          constraintItems.add((RangeConstraint) config.getValue());
          break;
        default:
          rangConfigDOS.add(config);
          break;
      }
    }

    // 兼容老的数值型约束
    if (CollectionUtils.isNotEmpty(rangConfigDOS)) {
      RangeConstraint rangeConstraint = new RangeConstraint();
      for (ConstraintItemConfigDO configDO : rangConfigDOS) {
        ConstraintEnum itemEnum = ConstraintEnum.getConstraint(configDO.getId());
        switch (itemEnum) {
          case MAXIMUM_LT:
            rangeConstraint.setRightOpen(true);
            rangeConstraint.setMaximumValue(configDO.getValue().toString());
            break;
          case MAXIMUM_LT_OE:
            rangeConstraint.setRightOpen(false);
            rangeConstraint.setMaximumValue(configDO.getValue().toString());
            break;
          case MINIMUM_GT:
            rangeConstraint.setLeftOpen(true);
            rangeConstraint.setMinimumValue(configDO.getValue().toString());
            break;
          case MINIMUM_GT_OE:
            rangeConstraint.setLeftOpen(false);
            rangeConstraint.setMinimumValue(configDO.getValue().toString());
            break;
          default:
            throw new IllegalArgumentException("invalid ConstraintEnum:" + itemEnum.name());
        }
      }
      constraintItems.add(rangeConstraint);
    }
    return constraintItems;
  }

  public static ConstraintItemConfigDO toConfigDO(BaseConstraintItem item) {
    ConstraintItemConfigDO configDO = new ConstraintItemConfigDO();
    configDO.setId(item.getConstraintTypeEnum().name());

    switch (item.getConstraintTypeEnum()) {
      case ENUM:
        EnumConstraint enumConstraint = (EnumConstraint) item;
        configDO.setValue(enumConstraint.getEnumValues());
        break;
      case REGULAR:
        RegularConstraint regularConstraint = (RegularConstraint) item;
        configDO.setValue(regularConstraint.getRegularPattern());
        break;
      case RANGE:
        RangeConstraint rangeConstraint = (RangeConstraint) item;
        configDO.setValue(rangeConstraint);
        break;
      default:
        configDO.setValue(null);
        break;
    }
    return configDO;
  }
}
