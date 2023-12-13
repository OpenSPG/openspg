/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.recorder;

/**
 * @author donghai.ydh
 * @version IExecutionRecorder.java, v 0.1 2023年11月07日 15:00 donghai.ydh
 */
public interface IExecutionRecorder {
    /**
     * get readable string
     */
    String toReadableString();

    /**
     * call when entry a new rdg
     */
    void entryRDG(String rdg);

    /**
     * call when leave a rdg
     */
    void leaveRDG();

    /**
     * record result num, like filer, expendInto
     */
    void stageResult(String stage, long result);

    /**
     * finish
     */
    void stageResultWithDesc(String stage, long result, String finishDescribe);
}