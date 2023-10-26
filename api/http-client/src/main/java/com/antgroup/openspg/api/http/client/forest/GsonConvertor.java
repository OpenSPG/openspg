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

package com.antgroup.openspg.api.http.client.forest;

import com.antgroup.openspg.api.facade.JSON;

import com.dtflys.forest.converter.ConvertOptions;
import com.dtflys.forest.converter.json.ForestJsonConverter;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.Lazy;
import com.dtflys.forest.utils.ForestDataType;
import com.dtflys.forest.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("unchecked")
public class GsonConvertor implements ForestJsonConverter {

    @Override
    public String encodeToString(Object obj) {
        return JSON.serialize(obj);
    }

    @Override
    public Map<String, Object> convertObjectToMap(Object obj, ForestRequest request, ConvertOptions options) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            final Map<?, ?> objMap = (Map<?, ?>) obj;
            final Map<String, Object> newMap = new HashMap<>(objMap.size());
            for (Object key : objMap.keySet()) {
                final String name = String.valueOf(key);
                if (options != null && options.shouldExclude(name)) {
                    continue;
                }
                Object val = objMap.get(key);
                if (Lazy.isEvaluatingLazyValue(val, request)) {
                    continue;
                }
                if (options != null) {
                    val = options.getValue(val, request);
                    if (options.shouldIgnore(val)) {
                        continue;
                    }
                }
                if (val != null) {
                    newMap.put(name, val);
                }
            }
            return newMap;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        final Gson gson = JSON.gson;
        final JsonElement jsonElement = gson.toJsonTree(obj);
        return toMap(jsonElement.getAsJsonObject(), true);
    }

    @Override
    public void setDateFormat(String format) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDateFormat() {
        return JSON.DATA_FORMAT;
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        return JSON.deserialize(source, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Class<T> targetType, Charset charset) {
        final String str = StringUtils.fromBytes(source, charset);
        return convertToJavaObject(str, targetType);
    }

    @Override
    public <T> T convertToJavaObject(byte[] source, Type targetType, Charset charset) {
        final String str = StringUtils.fromBytes(source, charset);
        return convertToJavaObject(str, targetType);
    }

    @Override
    public ForestDataType getDataType() {
        return ForestDataType.JSON;
    }

    private static Map<String, Object> toMap(JsonObject json, boolean singleLevel) {
        final Map<String, Object> map = new HashMap<>();
        final Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (final Map.Entry<String, JsonElement> entry : entrySet) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (singleLevel) {
                if (value instanceof JsonArray) {
                    map.put(key, toList((JsonArray) value));
                } else if (value instanceof JsonPrimitive) {
                    map.put(key, toObject((JsonPrimitive) value));
                } else {
                    map.put(key, value);
                }
                continue;
            }
            if (value instanceof JsonArray) {
                map.put(key, toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                map.put(key, toMap((JsonObject) value, singleLevel));
            } else if (value instanceof JsonPrimitive) {
                map.put(key, toObject((JsonPrimitive) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object toObject(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }
        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }
        if (jsonPrimitive.isNumber()) {
            final BigDecimal num = jsonPrimitive.getAsBigDecimal();
            final int index = num.toString().indexOf('.');
            if (index == -1) {
                if (num.compareTo(new BigDecimal(Long.MAX_VALUE)) > 0) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Long.MIN_VALUE)) < 0) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0
                    || num.compareTo(new BigDecimal(Integer.MIN_VALUE)) < 0) {
                    return jsonPrimitive.getAsLong();
                }
                return jsonPrimitive.getAsInt();
            }
            return jsonPrimitive.getAsDouble();
        }
        if (jsonPrimitive.isJsonArray()) {
            return toList(jsonPrimitive.getAsJsonArray());
        }
        if (jsonPrimitive.isJsonObject()) {
            return toMap(jsonPrimitive.getAsJsonObject(), false);
        }
        return null;
    }

    private static List<Object> toList(JsonArray json) {
        final List<Object> list = new ArrayList<>();
        for (int i = 0; i < json.size(); i++) {
            final Object value = json.get(i);
            if (value instanceof JsonArray) {
                list.add(toList((JsonArray) value));
            } else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value, false));
            } else if (value instanceof JsonPrimitive) {
                list.add(toObject((JsonPrimitive) value));
            } else {
                list.add(value);
            }
        }
        return list;
    }
}
