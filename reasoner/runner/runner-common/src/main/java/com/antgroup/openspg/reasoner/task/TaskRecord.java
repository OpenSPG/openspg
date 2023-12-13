/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.task;

import com.antgroup.openspg.reasoner.graphstate.GraphStateTypeEnum;
import lombok.Builder;
import lombok.Data;
import scala.Tuple2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@Builder
public class TaskRecord implements Serializable {
    /**
     * task id
     */
    private String taskId;

    /**
     * task parallel
     */
    private int parallel;

    /**
     * graph state type
     */
    private GraphStateTypeEnum graphStateType;

    /**
     * class name of graph loader
     */
    private String graphLoaderJobClassName;

    /**
     * start id from input
     */
    private List<Tuple2<String, String>> startIdList;

    /**
     * expect batch number, batching control
     */
    private int expectBatchNum;

    /**
     * dsl
     */
    private String dsl;

    /**
     * initializer class list
     */
    private List<String> initializerClassList;

    /**
     * task params
     */
    private Map<String, Object> params;
}