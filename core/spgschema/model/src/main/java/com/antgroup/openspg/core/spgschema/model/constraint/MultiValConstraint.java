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

package com.antgroup.openspg.core.spgschema.model.constraint;

/**
 * A multi-value constraint means that the value of the attribute is an array with multiple values, such as the user's
 * hobbies attribute, which may have multiple values, making it suitable for configuring multi-value constraints.
 */
public class MultiValConstraint extends BaseConstraintItem {

    private static final long serialVersionUID = 141121651435846593L;

    @Override
    public ConstraintTypeEnum getConstraintTypeEnum() {
        return ConstraintTypeEnum.MULTI_VALUE;
    }

    @Override
    public boolean checkIsLegal(Object value) {
        return true;
    }
}
