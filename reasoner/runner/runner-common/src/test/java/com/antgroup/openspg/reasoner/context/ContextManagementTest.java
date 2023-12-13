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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */

package com.antgroup.openspg.reasoner.context;

import com.antgroup.openspg.reasoner.task.TaskRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContextManagementTest {

    @Test
    public void testOnlyCallInitOnce() {
        // only init once
        Assert.assertEquals("0", ContextManagement.getInstance().getContext(StringInitializer.class));
        Assert.assertEquals("0", ContextManagement.getInstance().getContext(StringInitializer.class));
        Assert.assertEquals("0", ContextManagement.getInstance().getContext(StringInitializer.class));
        Assert.assertEquals("0", ContextManagement.getInstance().getContext(StringInitializer.class));
    }

    @Test
    public void testJobConfigMap() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("config1", "1");
        paramsMap.put("config2", "2");

        List<String> initializerClassList = new ArrayList<>();
        initializerClassList.add("com.antgroup.openspg.reasoner.context.ConfigCheckInitializer");
        initializerClassList.add("com.antgroup.openspg.reasoner.context.StringInitializer");
        TaskRecord taskRecord = TaskRecord.builder()
                .params(paramsMap)
                .initializerClassList(initializerClassList)
                .build();

        // init
        ContextManagement.getInstance().initContextOnDriver(taskRecord);
        // init again, no error
        ContextManagement.getInstance().initContextOnDriver(taskRecord);

        Assert.assertEquals("0", ContextManagement.getInstance().getContext(StringInitializer.class));
        ContextManagement.getInstance().getContext(ConfigCheckInitializer.class);

        DispatchContextInfo dispatchContextInfo = ContextManagement.getInstance().getDispatchContextInfo();

        // create new management
        ContextManagement.clear();

        // dispatch
        ContextManagement.getInstance().dispatchContextToWorker(dispatchContextInfo);

        // dispatch two times, no error
        ContextManagement.getInstance().dispatchContextToWorker(dispatchContextInfo);
    }

}