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

package com.antgroup.openspg.core.spgschema.model.type;

import com.antgroup.openspg.common.model.base.BaseValObj;

/**
 * The configuration of data storing multi version<br>
 * <p>the properties or concept can be stored ont only one version data in knowledge warehouse, the configuration about
 * multi version usually include the following elements:：
 * <ul>
 *     <li>{@link MultiVersionConfig#pattern}: the pattern of data snapshot, such as yyyymmdd</li>
 *     <li>{@link MultiVersionConfig#maxVersion}: the maximum version that stored</li>
 *     <li>{@link MultiVersionConfig#ttl}: how many days that reserved</li>
 * </ul>
 * </p>
 */
public class MultiVersionConfig extends BaseValObj {

    private static final long serialVersionUID = -4546449509123335007L;

    /**
     * The pattern of data snapshot.
     */
    private final String pattern;

    /**
     * The maximum of version kept in storage.
     */
    private final Integer maxVersion;

    /**
     * The days that data kept in storage, ttl=-1 means kept forever.
     */
    private final Integer ttl;

    public MultiVersionConfig(String pattern, Integer maxVersion, Integer ttl) {
        this.pattern = pattern;
        this.maxVersion = maxVersion;
        this.ttl = ttl;
    }

    public String getPattern() {
        return pattern;
    }

    public Integer getMaxVersion() {
        return maxVersion;
    }

    public Integer getTtl() {
        return ttl;
    }
}
