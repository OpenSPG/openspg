package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Status
 *
 * @author yangjin
 * @Title: Status.java
 * @Description:
 */
public enum Status {
    /**
     * online
     */
    ONLINE,
    /**
     * offline
     */
    OFFLINE;

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static Status getByName(String name, Status defaultValue) {
        for (Status value : Status.values()) {
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
    public static Status getByName(String name) {
        return getByName(name, null);
    }
}
