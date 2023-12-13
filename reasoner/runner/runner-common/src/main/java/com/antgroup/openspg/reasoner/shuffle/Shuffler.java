/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.shuffle;

import com.antgroup.openspg.reasoner.warehouse.common.partition.PartitionerFactory;

/**
 * @author donghai.ydh
 * @version Shuffler.java, v 0.1 2023年02月24日 10:03 donghai.ydh
 */
public class Shuffler {

    public static int getPartitionIndex(Object obj, int parallel) {
        return PartitionerFactory.getPartition(parallel).partition(obj);
    }
}