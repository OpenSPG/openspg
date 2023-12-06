package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Scheduler Unit
 *
 * @author yangjin
 * @Title: SchedulerUnit.java
 * @Description:
 */
public enum SchedulerUnit {
    /**
     * hour
     */
    HOUR(11),
    /**
     * day
     */
    DAY(5);

    private Integer calendarField;

    SchedulerUnit(Integer calendarField) {
        this.calendarField = calendarField;
    }

    public Integer getCalendarField() {
        return calendarField;
    }

    public void setCalendarField(Integer calendarField) {
        this.calendarField = calendarField;
    }

    /**
     * get by name
     *
     * @param name
     * @return
     */
    public static SchedulerUnit getByName(String name, SchedulerUnit defaultValue) {
        for (SchedulerUnit value : SchedulerUnit.values()) {
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
    public static SchedulerUnit getByName(String name) {
        return getByName(name, null);
    }
}
