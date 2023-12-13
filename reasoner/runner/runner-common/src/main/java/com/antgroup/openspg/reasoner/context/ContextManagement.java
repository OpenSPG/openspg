/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.context;

import com.antgroup.openspg.reasoner.task.TaskRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author donghai.ydh
 * @version ContextManagement.java, v 0.1 2023年07月12日 20:13 donghai.ydh
 */
@Slf4j(topic = "userlogger")
public class ContextManagement {

    private TaskRecord taskRecord = null;

    private final Map<Class<? extends BaseContextInitializer>, Object> contextObjectMap = new ConcurrentHashMap<>();

    private ContextManagement() {
    }

    /**
     * get dispatch info
     *
     * @return
     */
    public DispatchContextInfo getDispatchContextInfo() {
        return new DispatchContextInfo(this.taskRecord, this.contextObjectMap);
    }

    /**
     * get context
     */
    public <T> T getContext(Class<? extends BaseContextInitializer<T>> contextClass) {
        Object contextObj = contextObjectMap.get(contextClass);
        if (null != contextObj) {
            return (T) contextObj;
        }
        synchronized (ContextManagement.class) {
            contextObj = contextObjectMap.get(contextClass);
            if (null != contextObj) {
                return (T) contextObj;
            }
            contextObj = createContextObject(contextClass);
            contextObjectMap.put(contextClass, contextObj);
            return (T) contextObj;
        }
    }

    /**
     * init context
     */
    public void initContextOnDriver(TaskRecord taskRecord) {
        if (null != this.taskRecord) {
            return;
        }
        synchronized (ContextManagement.class) {
            if (null != this.taskRecord) {
                return;
            }
            this.taskRecord = taskRecord;
            if (CollectionUtils.isEmpty(this.taskRecord.getInitializerClassList())) {
                return;
            }
            for (String className : this.taskRecord.getInitializerClassList()) {
                getContext(getInitializerClass(className));
            }
        }
    }

    /**
     * dispatch context to worker
     */
    public void dispatchContextToWorker(DispatchContextInfo dispatchContextInfo) {
        if (null != this.taskRecord) {
            return;
        }
        synchronized (ContextManagement.class) {
            if (null != this.taskRecord) {
                return;
            }
            this.taskRecord = dispatchContextInfo.getTaskRecord();
            for (Map.Entry<Class<? extends BaseContextInitializer>, Object> entry :
                    dispatchContextInfo.getContextObjectMap().entrySet()) {
                Object obj = this.contextObjectMap.get(entry.getKey());
                if (null != obj) {
                    continue;
                }
                this.contextObjectMap.put(entry.getKey(), entry.getValue());
                callDispatchFunctionOnWorker(entry.getKey(), entry.getValue(), this.taskRecord);
            }
        }
    }

    protected <T> T createContextObject(Class<? extends BaseContextInitializer<T>> contextClass) {
        try {
            BaseContextInitializer<T> initializer = contextClass.getConstructor().newInstance();
            initializer.setTaskRecord(this.taskRecord);
            T t = initializer.initOnDriver();
            log.info("ContextManagement,create context, name=" + contextClass.getName());
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected void callDispatchFunctionOnWorker(Class<? extends BaseContextInitializer> contextClass, Object obj,
                                                TaskRecord taskRecord) {
        try {
            BaseContextInitializer initializer = contextClass.getConstructor().newInstance();
            initializer.setTaskRecord(this.taskRecord);
            initializer.dispatchToWorker(obj);
            log.info("ContextManagement,call dispatch context, name=" + contextClass.getName());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> Class<? extends BaseContextInitializer<T>> getInitializerClass(String initializerClassName) {
        Class<? extends BaseContextInitializer<T>> initializerClass;
        try {
            initializerClass = (Class<? extends BaseContextInitializer<T>>) Class.forName(initializerClassName);
        } catch (Exception e) {
            throw new RuntimeException("can not create initializer from name " + initializerClassName, e);
        }
        return initializerClass;
    }

    /**
     * instance
     */
    private static volatile ContextManagement instance = null;

    /**
     * Getter method for property <tt>instance</tt>.
     *
     * @return property value of instance
     */
    public static ContextManagement getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (ContextManagement.class) {
            if (null != instance) {
                return instance;
            }
            instance = new ContextManagement();
        }
        return instance;
    }

    /**
     * clear
     */
    public static void clear() {
        instance = null;
    }
}