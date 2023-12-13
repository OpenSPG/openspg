/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.progress;

import java.io.Serializable;

/**
 * @author youdonghai
 * @version TimeConsumeType.java, v 0.1 2022年04月28日 11:30 上午 youdonghai
 */
public enum TimeConsumeType implements Serializable {
    WAIT_RESOURCE,
    LOAD_GRAPH,
    COMPUTE,
}