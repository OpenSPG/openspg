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
     * minute
     */
    MINUTE(12),
    /**
     * hour
     */
    HOUR(11),
    /**
     * day
     */
    DAY(5),
    /**
     * week
     */
    WEEK(3),
    /**
     * month
     */
    MONTH(2);

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
}
