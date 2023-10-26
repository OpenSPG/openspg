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

import java.util.regex.Pattern;

/**
 * Regular expression constraints are applicable to text-type property with a certain format, such as mobile phone
 * number, email address, etc. Users can define regular expressions, and the property value must match the regular
 * expression to be considered valid.
 */
public class RegularConstraint extends BaseConstraintItem {

    private static final long serialVersionUID = -2333649026241875412L;

    /**
     * regular pattern
     */
    private final String regularPattern;
    private final Pattern pattern;

    public RegularConstraint(String regularPattern) {
        this.regularPattern = regularPattern;
        this.pattern = Pattern.compile(regularPattern);
    }

    public String getRegularPattern() {
        return regularPattern;
    }

    @Override
    public ConstraintTypeEnum getConstraintTypeEnum() {
        return ConstraintTypeEnum.REGULAR;
    }

    @Override
    public boolean checkIsLegal(Object value) {
        if (value == null) {
            return true;
        }
        return pattern.matcher(value.toString()).matches();
    }
}
