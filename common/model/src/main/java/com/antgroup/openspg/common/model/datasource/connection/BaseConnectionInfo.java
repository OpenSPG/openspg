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

package com.antgroup.openspg.common.model.datasource.connection;

import com.antgroup.openspg.common.model.base.BaseValObj;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class BaseConnectionInfo extends BaseValObj {

    private String scheme;

    private Map<String, Object> params;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public Object getNotNullParam(String key) {
        Object value = params.get(key);
        if (value == null) {
            throw new IllegalStateException(
                String.format("key=%s in params=%s is null", key, params)
            );
        }
        return value;
    }

    public Object getParamOrDefault(String key, Object defaultValue) {
        return params.getOrDefault(key, defaultValue);
    }

    public <T extends BaseConnectionInfo> T setScheme(String scheme) {
        this.scheme = scheme;
        return (T) this;
    }

    public <T extends BaseConnectionInfo> T setParams(Map<String, Object> params) {
        this.params = params;
        return (T) this;
    }

    public <T extends BaseConnectionInfo> T addParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return (T) this;
    }

    public String getScheme() {
        return scheme;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseConnectionInfo)) {
            return false;
        }
        BaseConnectionInfo that = (BaseConnectionInfo) o;
        return Objects.equals(getScheme(), that.getScheme())
            && Objects.equals(getParams(), that.getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScheme(), getParams());
    }
}
