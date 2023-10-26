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

package com.antgroup.openspg.core.spgbuilder.model.record;

import com.antgroup.openspg.common.model.base.BaseValObj;

import java.util.Comparator;


public abstract class BaseRecord extends BaseValObj {

    public static final Comparator<BaseRecord>
        ADVANCED_RECORD_FIRST_COMPARATOR = (o1, o2) -> {
        if (o1 instanceof BaseAdvancedRecord) {
            if (o2 instanceof BaseAdvancedRecord) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (o2 instanceof BaseAdvancedRecord) {
                return 1;
            } else {
                return 0;
            }
        }
    };
}
