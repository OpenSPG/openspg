/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import com.antgroup.openspg.reasoner.task.TaskRecord;


public abstract class BaseContextInitializer<T> {

    protected TaskRecord taskRecord;

    public void setTaskRecord(TaskRecord taskRecord) {
        this.taskRecord = taskRecord;
    }

    public abstract T initOnDriver();

    public abstract void dispatchToWorker(T obj);
}