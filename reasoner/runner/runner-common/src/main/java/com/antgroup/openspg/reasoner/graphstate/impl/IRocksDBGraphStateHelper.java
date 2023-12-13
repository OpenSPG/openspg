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
package com.antgroup.openspg.reasoner.graphstate.impl;

import scala.Tuple2;


public interface IRocksDBGraphStateHelper {
    Tuple2<Long, Long> mapVersion2WindowRange(Long startVersion, Long endVersion);

    Tuple2<Long, Long> mapVersion2WindowRange(Long version);

    long getWriteWindow(long version);

    Object byte2Object(byte[] bytes);

    byte[] object2Byte(Object obj);
}