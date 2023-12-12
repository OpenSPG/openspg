/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.warehouse.common.partition;

import java.util.function.Function;


public class PartitionerFactory {
    private static volatile BasePartitioner partitioner = null;

    private static final Function<Integer, BasePartitioner> DEFAULT_PARTITION_CREATOR =
            new Function<Integer, BasePartitioner>() {
                @Override
                public BasePartitioner apply(Integer parallel) {
                    return new BasePartitioner(parallel);
                }
            };

    private static volatile Function<Integer, BasePartitioner> PARTITION_CREATOR = DEFAULT_PARTITION_CREATOR;

    public static BasePartitioner getPartition(int parallel) {
        if (null == partitioner) {
            synchronized (PartitionerFactory.class) {
                if (null == partitioner) {
                    partitioner = PARTITION_CREATOR.apply(parallel);
                }
            }
        }
        return partitioner;
    }

    public static void setPartitionCreator(Function<Integer, BasePartitioner> partitionCreator) {
        PARTITION_CREATOR = partitionCreator;
        partitioner = null;
    }

    public static void reset() {
        PARTITION_CREATOR = DEFAULT_PARTITION_CREATOR;
        partitioner = null;
    }
}