/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.progress;

import java.io.Serializable;


public enum TimeConsumeType implements Serializable {
    WAIT_RESOURCE,
    LOAD_GRAPH,
    COMPUTE,
}