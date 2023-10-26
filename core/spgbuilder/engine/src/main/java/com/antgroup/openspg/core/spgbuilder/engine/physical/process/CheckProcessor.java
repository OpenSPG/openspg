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

package com.antgroup.openspg.core.spgbuilder.engine.physical.process;

import com.antgroup.openspg.core.spgbuilder.engine.runtime.BuilderRecordException;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.CheckNodeConfig;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseAdvancedRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.BaseSPGRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.RelationRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGPropertyRecord;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGRecordTypeEnum;
import com.antgroup.openspg.core.spgbuilder.model.record.SPGSubPropertyRecord;
import com.antgroup.openspg.core.spgschema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.spgschema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.spgschema.model.predicate.Property;
import com.antgroup.openspg.core.spgschema.model.predicate.SubProperty;

import java.util.List;


public class CheckProcessor extends BaseProcessor<CheckNodeConfig> {

    private final static String PROCESSOR_NAME = "CHECK";

    public CheckProcessor() {
        super(PROCESSOR_NAME, PROCESSOR_NAME, null);
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public List<BaseRecord> process(List<BaseRecord> records) {
        for (BaseRecord record : records) {
            BaseSPGRecord spgRecord = (BaseSPGRecord) record;
            if (SPGRecordTypeEnum.RELATION.equals(spgRecord.getRecordType())) {
                checkSubProperty(((RelationRecord) spgRecord));
            } else {
                checkProperty(((BaseAdvancedRecord) spgRecord));
            }
        }
        return records;
    }

    private void checkProperty(BaseAdvancedRecord advancedRecord) {
        for (Property property : advancedRecord.getSpgType().getProperties()) {
            if (property.getObjectTypeRef().isBasicType()) {
                if (property.getConstraint() == null) {
                    continue;
                }
                SPGPropertyRecord propertyRecord = advancedRecord.getPropertyRecord(property);
                Object stdValue = (propertyRecord == null ? null : propertyRecord.getValue().getStd());
                doCheckBasicProperty(property.getName(), stdValue, property.getConstraint().getConstraintItems());
            }
        }
    }

    private void checkSubProperty(RelationRecord relationRecord) {
        for (SubProperty subProperty : relationRecord.getRelationType().getSubProperties()) {
            if (subProperty.getObjectTypeRef().isBasicType()) {
                if (subProperty.getConstraint() == null) {
                    continue;
                }
                SPGSubPropertyRecord subPropertyRecord = relationRecord.getSubPropertyRecord(subProperty);
                Object stdValue = (subPropertyRecord == null ? null : subPropertyRecord.getValue().getStd());
                doCheckBasicProperty(subProperty.getName(), stdValue, subProperty.getConstraint().getConstraintItems());
            }
        }
    }

    private void doCheckBasicProperty(String propertyName, Object stdValue, List<BaseConstraintItem> constraintItems) {
        Object[] values = new Object[]{stdValue};
        for (BaseConstraintItem constraintItem : constraintItems) {
            for (Object value : values) {
                if (!constraintItem.checkIsLegal(value)) {
                    throw new BuilderRecordException(this,
                        "the property {}={} violates constraints:{}", propertyName, stdValue,
                        constraintItem.getConstraintTypeEnum().name()
                    );
                }
            }
            if (constraintItem instanceof MultiValConstraint) {
                if (stdValue != null) {
                    values = stdValue.toString().split(",");
                }
            }
        }
    }
}
