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

package com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.operator.impl;

import com.antgroup.openspg.common.model.LangTypeEnum;
import com.antgroup.openspg.core.spgbuilder.engine.physical.invoker.operator.OperatorFactory;
import com.antgroup.openspg.core.spgbuilder.engine.runtime.RuntimeContext;
import com.antgroup.openspg.core.spgbuilder.model.pipeline.config.OperatorConfig;
import com.antgroup.openspg.core.spgschema.model.type.OperatorKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class OperatorFactoryImpl implements OperatorFactory {

    private final OperatorFactory pythonOperatorFactory = new PythonOperatorFactory();

    private final Map<OperatorKey, OperatorFactory> operators = new ConcurrentHashMap<>();

    @Override
    public void init(RuntimeContext context) {
        pythonOperatorFactory.init(context);
    }

    @Override
    public void register(OperatorConfig config) {
        OperatorKey operatorKey = config.toKey();
        if (LangTypeEnum.PYTHON.equals(config.getLangType())) {
            pythonOperatorFactory.register(config);
            operators.put(operatorKey, pythonOperatorFactory);
            return;
        }
        throw new IllegalArgumentException("illegal langType=" + config.getLangType());
    }

    @Override
    public Object invoke(OperatorKey key, Object... input) {
        OperatorFactory operatorFactory = operators.get(key);
        if (operatorFactory == null) {
            throw new IllegalArgumentException("illegal key=" + key);
        }
        return operatorFactory.invoke(key, input);
    }
}
