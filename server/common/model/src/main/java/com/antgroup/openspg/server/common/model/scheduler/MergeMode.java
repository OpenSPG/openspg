package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Merge Mode
 *
 * @author yangjin
 * @Title: MergeMode.java
 * @Description:
 */
public enum MergeMode {
    /**
     * merge
     */
    MERGE,
    /**
     * snapshot
     */
    SNAPSHOT;

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static MergeMode getByName(String name, MergeMode defaultValue) {
        for (MergeMode value : MergeMode.values()) {
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
    public static MergeMode getByName(String name) {
        return getByName(name, null);
    }
}
