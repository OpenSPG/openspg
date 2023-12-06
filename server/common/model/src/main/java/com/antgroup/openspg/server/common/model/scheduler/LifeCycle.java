package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Life Cycle
 *
 * @author yangjin
 * @Title: LifeCycle.java
 * @Description:
 */
public enum LifeCycle {

    /**
     * period
     */
    PERIOD,

    /**
     * once
     */
    ONCE,

    /**
     * realtime
     */
    REAL_TIME;

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static LifeCycle getByName(String name, LifeCycle defaultValue) {
        for (LifeCycle value : LifeCycle.values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return defaultValue;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static LifeCycle getByName(String name) {
        return getByName(name, null);
    }
}
