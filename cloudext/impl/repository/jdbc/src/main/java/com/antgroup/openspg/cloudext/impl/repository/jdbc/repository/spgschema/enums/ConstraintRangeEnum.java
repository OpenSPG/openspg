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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums;

import com.antgroup.openspg.core.spgschema.model.constraint.RangeConstraint;

import org.apache.commons.lang3.StringUtils;


public enum ConstraintRangeEnum {

    /**
     * None constraint.
     */
    NONE("0"),
    /**
     * Larger than
     */
    GT("1"),
    /**
     * Larger or equal than
     */
    GT_OE("2"),
    /**
     * Less than
     */
    LT("3"),
    /**
     * Less or equal than
     */
    LT_OE("4"),
    /**
     * If the range is open in left and right
     */
    OPEN("5"),
    /**
     * If the range is open in left and closed in right.
     */
    OAOB("6"),
    /**
     * If the range is closed in left and open in right.
     */
    BCAO("7"),
    /**
     * If the range is closed in left and right
     */
    CLOSED("8"),
    ;

    private final String value;

    ConstraintRangeEnum(String value) {
        this.value = value;
    }

    public static ConstraintRangeEnum getRange(String value) {
        for (ConstraintRangeEnum rangeType : ConstraintRangeEnum.values()) {
            if (rangeType.value.equals(value)) {
                return rangeType;
            }
        }

        throw new IllegalArgumentException("Unsupported range type:" + value);
    }

    public String getValue() {
        return value;
    }

    public static ConstraintRangeEnum parse(RangeConstraint rangeConstraint) {
        if (StringUtils.isNotBlank(rangeConstraint.getMinimumValue()) && StringUtils.isNotBlank(
            rangeConstraint.getMaximumValue())) {
            if (rangeConstraint.getLeftOpen() && rangeConstraint.getRightOpen()) {
                return ConstraintRangeEnum.OPEN;
            }
            if (rangeConstraint.getLeftOpen() && !rangeConstraint.getRightOpen()) {
                return ConstraintRangeEnum.OAOB;
            }
            if (!rangeConstraint.getLeftOpen() && rangeConstraint.getRightOpen()) {
                return ConstraintRangeEnum.BCAO;
            }
            return ConstraintRangeEnum.CLOSED;
        } else {
            if (StringUtils.isNotBlank(rangeConstraint.getMinimumValue())) {
                if (rangeConstraint.getLeftOpen()) {
                    return ConstraintRangeEnum.GT;
                } else {
                    return ConstraintRangeEnum.GT_OE;
                }
            } else {
                if (rangeConstraint.getRightOpen()) {
                    return ConstraintRangeEnum.LT;
                } else {
                    return ConstraintRangeEnum.LT_OE;
                }
            }
        }
    }
}
