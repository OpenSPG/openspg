/*
 * Copyright 2023 Ant Group CO., Ltd.
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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io;

import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.model.ReadRange;
import com.antgroup.openspg.reasoner.io.odps.OdpsUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ReadRangeTest {

    private static final Random RANDOM = new Random();

    /**
     * parallel=1, allRound=1, should read all data
     */
    @Test
    public void testReadRange1() {
        OdpsTableInfo odpsTableInfo1 = new OdpsTableInfo();
        odpsTableInfo1.setProject("test");
        odpsTableInfo1.setTable("table1");
        OdpsTableInfo odpsTableInfo2 = new OdpsTableInfo();
        odpsTableInfo2.setProject("test");
        odpsTableInfo2.setTable("table2");

        Map<OdpsTableInfo, Long> partitionCountMap = new HashMap<>();
        partitionCountMap.put(odpsTableInfo1, 154L);
        partitionCountMap.put(odpsTableInfo2, 319L);
        Map<OdpsTableInfo, ReadRange> rangeMap = OdpsUtils.getReadRange(1, 0, 1, 0, partitionCountMap);
        for (OdpsTableInfo partition : partitionCountMap.keySet()) {
            Assert.assertEquals(partitionCountMap.get(partition).longValue(), rangeMap.get(partition).getCount());
        }
    }

    /**
     * any parallel any round, should read all data
     */
    @Test
    public void testReadRange2() {
        OdpsTableInfo odpsTableInfo1 = new OdpsTableInfo();
        odpsTableInfo1.setProject("test");
        odpsTableInfo1.setTable("table1");
        Map<String, String> partition1 = new HashMap<>();
        partition1.put("dt", "20230203");
        odpsTableInfo1.setPartition(partition1);
        OdpsTableInfo odpsTableInfo2 = new OdpsTableInfo();
        odpsTableInfo2.setProject("test");
        odpsTableInfo2.setTable("table1");
        Map<String, String> partition2 = new HashMap<>();
        partition2.put("dt", "20230204");
        odpsTableInfo2.setPartition(partition2);

        Map<OdpsTableInfo, Long> partitionCountMap = new HashMap<>();
        partitionCountMap.put(odpsTableInfo1, getRandomLong(true, 100, 567934L));
        partitionCountMap.put(odpsTableInfo2, getRandomLong(true, 100, 95126123L));

        int parallel = (int) getRandomLong(true, 3L, 2000L);
        int allRound = (int) getRandomLong(true, 15L, 100L);

        Map<OdpsTableInfo, List<ReadRange>> resultRangeMap = new HashMap<>();
        for (int i = 0; i < parallel; ++i) {
            for (int j = 0; j < allRound; ++j) {
                Map<OdpsTableInfo, ReadRange> rangeMap = OdpsUtils.getReadRange(parallel, i, allRound, j, partitionCountMap);
                for (OdpsTableInfo tableInfo : rangeMap.keySet()) {
                    List<ReadRange> readRangeList = resultRangeMap.computeIfAbsent(tableInfo, k -> new ArrayList<>());
                    readRangeList.add(rangeMap.get(tableInfo));
                }
            }
        }

        for (List<ReadRange> readRangeList : resultRangeMap.values()) {
            readRangeList.sort(ReadRange::compareTo);
        }

        for (OdpsTableInfo tableInfo : partitionCountMap.keySet()) {
            List<ReadRange> readRangeList = resultRangeMap.get(tableInfo);
            long allCount = readRangeList.stream().map(ReadRange::getCount).reduce(Long::sum).get();
            Assert.assertEquals(allCount, partitionCountMap.get(tableInfo).longValue());

            for (int i = 0; i < readRangeList.size(); ++i) {
                ReadRange readRange = readRangeList.get(i);
                if (0 == i) {
                    Assert.assertEquals(readRange.getStart(), 0L);
                } else {
                    ReadRange lastReadRange = readRangeList.get(i - 1);
                    Assert.assertEquals(readRange.getStart(), lastReadRange.getEnd());
                }
            }

            /*
            if (readRangeList.size() > 2) {
                double[] rangeArray = new double[readRangeList.size() - 2];
                for (int i = 0; i < rangeArray.length; ++i) {
                    rangeArray[i] = (double) readRangeList.get(i + 1).getCount();
                }
                double stdDev = Variance.sampleStdDev(rangeArray);
                Assert.assertTrue(stdDev < 10);
            }
             */
        }

    }

    private long getRandomLong(boolean recurrence, long min, long max) {
        long size = Math.abs(RANDOM.nextLong()) % max;
        while (recurrence && size < min) {
            size = getRandomLong(false, min, max);
        }
        return size;
    }
}