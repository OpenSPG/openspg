/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.sink;

import java.io.Serializable;


public enum KgReasonerSinkType implements Serializable {
    /**
     * log
     */
    LOG,
    /**
     * loca csv file
     */
    FILE,
    /**
     * odps table
     */
    ODPS,
    /**
     * hive table
     */
    HIVE
}