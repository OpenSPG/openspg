/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.graphstate.impl;

import scala.Tuple2;


public interface IRocksDBGraphStateHelper {
    Tuple2<Long, Long> mapVersion2WindowRange(Long startVersion, Long endVersion);

    Tuple2<Long, Long> mapVersion2WindowRange(Long version);

    long getWriteWindow(long version);

    Object byte2Object(byte[] bytes);

    byte[] object2Byte(Object obj);
}