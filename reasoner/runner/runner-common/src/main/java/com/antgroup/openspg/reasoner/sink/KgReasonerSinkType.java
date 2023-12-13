/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.sink;

import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version KgReasonerSinkType.java, v 0.1 2023年04月12日 13:51 donghai.ydh
 */
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