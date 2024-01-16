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

package com.antgroup.openspg.builder.core.physical.process;

import com.antgroup.openspg.builder.model.pipeline.config.BaseNodeConfig;
import com.antgroup.openspg.builder.model.pipeline.enums.NodeTypeEnum;
import com.antgroup.openspg.builder.model.record.BaseRecord;
import com.antgroup.openspg.builder.model.record.BaseSPGRecord;
import com.antgroup.openspg.builder.model.record.SPGRecordTypeEnum;
import java.util.List;

public class CheckProcessor extends BaseProcessor<CheckProcessor.CheckNodeConfig> {

  public static class CheckNodeConfig extends BaseNodeConfig {
    public CheckNodeConfig() {
      super(NodeTypeEnum.CHECK);
    }
  }

  private static final String PROCESSOR_NAME = "CHECK";

  public CheckProcessor() {
    super(PROCESSOR_NAME, PROCESSOR_NAME, null);
  }

  @Override
  public void close() throws Exception {}

  @Override
  public List<BaseRecord> process(List<BaseRecord> inputs) {
    for (BaseRecord record : inputs) {
      BaseSPGRecord spgRecord = (BaseSPGRecord) record;
      if (SPGRecordTypeEnum.RELATION.equals(spgRecord.getRecordType())) {
        //        checkSubProperty(((RelationRecord) spgRecord));
      } else {
        //        checkProperty(((BaseAdvancedRecord) spgRecord));
      }
    }
    return inputs;
  }

  //  private void checkProperty(BaseAdvancedRecord advancedRecord) {
  //    for (Property property : advancedRecord.getSpgType().getProperties()) {
  //      if (property.getObjectTypeRef().isBasicType()) {
  //        if (property.getConstraint() == null) {
  //          continue;
  //        }
  //        SPGPropertyRecord propertyRecord = advancedRecord.getPropertyRecord(property);
  //        Object stdValue = (propertyRecord == null ? null : propertyRecord.getValue().getStd());
  //        doCheckBasicProperty(
  //            property.getName(), stdValue, property.getConstraint().getConstraintItems());
  //      }
  //    }
  //  }
  //
  //  private void checkSubProperty(RelationRecord relationRecord) {
  //    for (SubProperty subProperty : relationRecord.getRelationType().getSubProperties()) {
  //      if (subProperty.getObjectTypeRef().isBasicType()) {
  //        if (subProperty.getConstraint() == null) {
  //          continue;
  //        }
  //        SPGSubPropertyRecord subPropertyRecord =
  // relationRecord.getSubPropertyRecord(subProperty);
  //        Object stdValue =
  //            (subPropertyRecord == null ? null : subPropertyRecord.getValue().getStd());
  //        doCheckBasicProperty(
  //            subProperty.getName(), stdValue, subProperty.getConstraint().getConstraintItems());
  //      }
  //    }
  //  }
  //
  //  private void doCheckBasicProperty(
  //      String propertyName, Object stdValue, List<BaseConstraintItem> constraintItems) {
  //    Object[] values = new Object[] {stdValue};
  //    for (BaseConstraintItem constraintItem : constraintItems) {
  //      for (Object value : values) {
  //        if (!constraintItem.checkIsLegal(value)) {
  //          throw new BuilderRecordException(
  //              this,
  //              "the property {}={} violates constraints:{}",
  //              propertyName,
  //              stdValue,
  //              constraintItem.getConstraintTypeEnum().name());
  //        }
  //      }
  //      if (constraintItem instanceof MultiValConstraint) {
  //        if (stdValue != null) {
  //          values = stdValue.toString().split(",");
  //        }
  //      }
  //    }
  //  }
}
