/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.recorder;

/**
 * This is an empty recorder
 *
 * @author donghai.ydh
 * @version EmptyRecorder.java, v 0.1 2023年11月07日 15:09 donghai.ydh
 */
public class EmptyRecorder implements IExecutionRecorder {
    @Override
    public String toReadableString() {
        return "";
    }

    @Override
    public void entryRDG(String rdg) {
    }

    @Override
    public void leaveRDG() {
    }

    @Override
    public void stageResult(String stage, long result) {
    }

    @Override
    public void stageResultWithDesc(String stage, long result, String finishDescribe) {

    }
}