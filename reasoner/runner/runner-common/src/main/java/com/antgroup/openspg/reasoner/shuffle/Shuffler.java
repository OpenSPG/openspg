/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.shuffle;

import com.antgroup.openspg.reasoner.warehouse.common.partition.PartitionerFactory;


public class Shuffler {

    public static int getPartitionIndex(Object obj, int parallel) {
        return PartitionerFactory.getPartition(parallel).partition(obj);
    }
}