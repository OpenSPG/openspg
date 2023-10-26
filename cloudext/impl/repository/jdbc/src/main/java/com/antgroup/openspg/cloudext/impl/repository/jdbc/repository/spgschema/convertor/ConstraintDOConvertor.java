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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.convertor;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ConstraintDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.ConstraintRangeEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.YesOrNoEnum;
import com.antgroup.openspg.core.spgschema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.spgschema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.MultiValConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.NotNullConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.RangeConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.spgschema.model.constraint.UniqueConstraint;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 约束Model和DO转换器
 */
public class ConstraintDOConvertor {

    public static ConstraintDO toConstraintDO(Constraint constraint) {
        if (null == constraint) {
            return null;
        }

        ConstraintDO constraintDO = new ConstraintDO();
        constraintDO.setId(constraint.getId());
        constraintDO.setName("name");
        constraintDO.setNameZh("nameZh");
        constraintDO.setDescription("desc");
        constraintDO.setDescriptionZh("descZh");

        //初始化非数值型约束
        constraintDO.setIsRequire(YesOrNoEnum.N.name());
        constraintDO.setIsUnique(YesOrNoEnum.N.name());
        constraintDO.setIsEnum(YesOrNoEnum.N.name());
        constraintDO.setUpDownBoundary(ConstraintRangeEnum.NONE.getValue());
        constraintDO.setIsMultiValue(YesOrNoEnum.N.name());

        //解析约束条件项
        constraint.getConstraintItems().forEach(v -> {
            ConstraintTypeEnum constraintItemEnum = v.getConstraintTypeEnum();
            switch (constraintItemEnum) {
                case NOT_NULL:
                    constraintDO.setIsRequire(YesOrNoEnum.Y.name());
                    break;
                case UNIQUE:
                    constraintDO.setIsUnique(YesOrNoEnum.Y.name());
                    break;
                case ENUM:
                    constraintDO.setIsEnum(YesOrNoEnum.Y.name());
                    EnumConstraint enumConstraint = (EnumConstraint) v;
                    if (enumConstraint.getEnumValues() != null) {
                        constraintDO.setEnumValue(JSON.toJSONString(enumConstraint.getEnumValues()));
                    }
                    break;
                case MULTI_VALUE:
                    constraintDO.setIsMultiValue(YesOrNoEnum.Y.name());
                    break;
                case REGULAR:
                    RegularConstraint regularConstraint = (RegularConstraint) v;
                    constraintDO.setValuePattern(regularConstraint.getRegularPattern());
                    break;
                case RANGE:
                    RangeConstraint rangeConstraint = (RangeConstraint) v;
                    constraintDO.setMinValue(rangeConstraint.getMinimumValue());
                    constraintDO.setMaxValue(rangeConstraint.getMaximumValue());
                    constraintDO.setUpDownBoundary(ConstraintRangeEnum
                        .parse(rangeConstraint)
                        .getValue());
                    break;
                default:
                    throw new IllegalArgumentException("illegal type=" + constraintItemEnum.name());
            }
        });
        return constraintDO;
    }

    public static Constraint toConstraint(ConstraintDO constraintDO) {
        if (null == constraintDO) {
            return null;
        }

        //解析非数值型约束条件
        List<BaseConstraintItem> constraintItems = new ArrayList<>();
        if (YesOrNoEnum.isYes(constraintDO.getIsRequire())) {
            constraintItems.add(new NotNullConstraint());
        }
        if (YesOrNoEnum.isYes(constraintDO.getIsUnique())) {
            constraintItems.add(new UniqueConstraint());
        }
        if (YesOrNoEnum.isYes(constraintDO.getIsEnum())) {
            constraintItems.add(new EnumConstraint(JSON.parseArray(
                constraintDO.getEnumValue(), String.class)));
        }
        if (YesOrNoEnum.isYes(constraintDO.getIsMultiValue())) {
            constraintItems.add(new MultiValConstraint());
        }
        if (StringUtils.isNotBlank(constraintDO.getValuePattern())) {
            constraintItems.add(new RegularConstraint(constraintDO.getValuePattern()));
        }

        // 解析数值型约束条件
        ConstraintRangeEnum constraintRangeEnum = ConstraintRangeEnum.getRange(constraintDO.getUpDownBoundary());
        switch (constraintRangeEnum) {
            case GT:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    null,
                    true,
                    null));
                break;
            case GT_OE:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    null,
                    false,
                    null));
                break;
            case LT:
                constraintItems.add(new RangeConstraint(
                    null,
                    constraintDO.getMaxValue(),
                    null,
                    true));
                break;
            case LT_OE:
                constraintItems.add(new RangeConstraint(
                    null,
                    constraintDO.getMaxValue(),
                    null,
                    false));
                break;
            case OPEN:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    constraintDO.getMaxValue(),
                    true,
                    true));
                break;
            case OAOB:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    constraintDO.getMaxValue(),
                    true,
                    false));
                break;
            case BCAO:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    constraintDO.getMaxValue(),
                    false,
                    true));
                break;
            case CLOSED:
                constraintItems.add(new RangeConstraint(
                    constraintDO.getMinValue(),
                    constraintDO.getMaxValue(),
                    false,
                    false));
                break;
            default:
                break;
        }
        return new Constraint(constraintDO.getId(), constraintItems);
    }
}
