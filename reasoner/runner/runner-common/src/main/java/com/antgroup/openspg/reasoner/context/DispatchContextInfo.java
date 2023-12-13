/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import com.antgroup.openspg.reasoner.task.TaskRecord;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author donghai.ydh
 * @version DispatchContextInfo.java, v 0.1 2023年07月13日 09:58 donghai.ydh
 */
public class DispatchContextInfo implements Serializable {
    private final TaskRecord taskRecord;

    private final Map<Class<? extends BaseContextInitializer>, Object> contextObjectMap = new HashMap<>();

    public DispatchContextInfo(TaskRecord taskRecord, Map<Class<? extends BaseContextInitializer>, Object> contextObjectMap) {
        this.taskRecord = taskRecord;
        this.contextObjectMap.putAll(contextObjectMap);
    }

    public Map<Class<? extends BaseContextInitializer>, Object> getContextObjectMap() {
        return contextObjectMap;
    }

    public TaskRecord getTaskRecord() {
        return taskRecord;
    }

    public boolean isEmpty() {
        return contextObjectMap.isEmpty();
    }
}