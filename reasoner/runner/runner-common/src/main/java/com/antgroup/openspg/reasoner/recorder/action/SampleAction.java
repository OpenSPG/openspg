/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */
package com.antgroup.openspg.reasoner.recorder.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;

/**
 * @author peilong.zpl
 * @version $Id: SampleAction.java, v 0.1 2024-04-08 15:31 peilong.zpl Exp $$
 */
public class SampleAction extends AbstractAction {

    private final String describe;
    private final Long num;

    private final String finishDescribe;

    private final Map<String, List<IVertexId>> runtimeDetail;

    public SampleAction(String describe, long num) {
        super(System.currentTimeMillis());
        this.describe = describe;
        this.num = num;
        this.finishDescribe = null;
        this.runtimeDetail = null;
    }

    public SampleAction(String describe, long num, Map<String, List<IVertexId>> runtimeDetail) {
        super(System.currentTimeMillis());
        this.describe = describe;
        this.num = num;
        this.finishDescribe = null;
        this.runtimeDetail = runtimeDetail;
    }

    public SampleAction(String describe, long num, String finishDescribe, Map<String, List<IVertexId>> runtimeDetail) {
        super(System.currentTimeMillis());
        this.describe = describe;
        this.num = num;
        this.finishDescribe = finishDescribe;
        this.runtimeDetail = runtimeDetail;
    }


    public SampleAction(String describe, long num, String finishDescribe) {
        super(System.currentTimeMillis());
        this.describe = describe;
        this.num = num;
        this.finishDescribe = finishDescribe;
        this.runtimeDetail = null;
    }

    public SampleAction(String describe, Long num, long time) {
        super(time);
        this.describe = describe;
        this.num = num;
        this.finishDescribe = null;
        this.runtimeDetail = null;
    }

    public String getDescribe() {
        return describe;
    }

    public Long getNum() {
        return num;
    }

    public String getFinishDescribe() {
        return finishDescribe;
    }

    public Map<String, List<IVertexId>> getRuntimeDetail() {
        return runtimeDetail;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        String prefix = null == num ? "SUBQUERY" : "NUM=" + num;
        StringBuilder passIds = new StringBuilder();
        StringBuilder failedIds = new StringBuilder();
        if (runtimeDetail != null) {
            passIds.append(", [pass]{");
            for (IVertexId vertexId : this.runtimeDetail.computeIfAbsent("pass", k-> new ArrayList<>())) {
                passIds.append(" ").append(vertexId.toString());
            }
            passIds.append("}");
            failedIds.append(", [failed]{");
            for (IVertexId vertexId : this.runtimeDetail.computeIfAbsent("failed", k-> new ArrayList<>())) {
                failedIds.append(" ").append(vertexId.toString());
            }
            failedIds.append("}");
        }
        return prefix + ", " + describe + passIds + failedIds +" @" + formatter.format(new Date(this.time));
    }
}