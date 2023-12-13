/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.sink;

import com.antgroup.openspg.reasoner.common.graph.type.GraphItemType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author donghai.ydh
 * @version QueryGraphStateInfo.java, v 0.1 2023年04月12日 14:44 donghai.ydh
 */
@Data
public class QueryGraphStateInfo implements Serializable {
    private GraphItemType sourceGraphItemType;
    private List<String>  sourceTypeList;

    private GraphItemType targetGraphItemType;
    private List<String>  targetPropertyNameList;

    private List<String> targetEdgeTypeList;
    private String targetVertexIdString;
}