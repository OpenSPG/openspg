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

/**
 * @author donghai.ydh
 * @version com.antgroup.openspg.reasoner.context.ContextManagementTest.java, v 0.1 2023年07月12日 20:34 donghai.ydh
 */
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