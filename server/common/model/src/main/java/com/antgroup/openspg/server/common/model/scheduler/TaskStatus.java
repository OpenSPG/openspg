package com.antgroup.openspg.server.common.model.scheduler;

/**
 * Task Status
 *
 * @author yangjin
 * @Title: TaskStatus.java
 * @Description:
 */
public enum TaskStatus {
    /**
     * wait
     */
    WAIT,
    /**
     * running
     */
    RUNNING,
    /**
     * finish
     */
    FINISH,
    /**
     * error
     */
    ERROR,
    /**
     * skip
     */
    SKIP,
    /**
     * terminate
     */
    TERMINATE,
    /**
     * set finish
     */
    SET_FINISH;

    /**
     * get TaskStatus by name
     *
     * @param name
     * @return
     */
    public static TaskStatus getByName(String name, TaskStatus defaultValue) {
        for (TaskStatus workflowStatus : TaskStatus.values()) {
            if (workflowStatus.name().equalsIgnoreCase(name)) {
                return workflowStatus;
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
    public static TaskStatus getByName(String name) {
        return getByName(name, null);
    }

    /**
     * status is Finish
     *
     * @param status
     * @return
     */
    public static boolean isFinish(TaskStatus status) {
        return TaskStatus.FINISH.equals(status) || TaskStatus.SKIP.equals(status) || TaskStatus.TERMINATE.equals(status)
                || TaskStatus.SET_FINISH.equals(status);
    }

    public static boolean isFinish(String status) {
        return isFinish(getByName(status));
    }

    /**
     * status is Running
     *
     * @param status
     * @return
     */
    public static boolean isRunning(TaskStatus status) {
        return TaskStatus.RUNNING.equals(status) || TaskStatus.ERROR.equals(status);
    }

    public static boolean isRunning(String status) {
        return isRunning(getByName(status));
    }
}
