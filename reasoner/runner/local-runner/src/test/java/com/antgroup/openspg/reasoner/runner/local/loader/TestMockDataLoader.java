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

package com.antgroup.openspg.reasoner.runner.local.loader;

import com.antgroup.openspg.reasoner.graphstate.impl.MemGraphState;
import org.junit.Assert;
import org.junit.Test;

public class TestMockDataLoader {
  @Test
  public void test() {
    String demoGraph =
        "Graph {\n"
            + "  C_37091 [CRO.Company, regNo='C_37091']\n"
            + "  C_7661 [CRO.Company, regNo='C_7661']\n"
            + "  C_8125 [CRO.Company, regNo='C_8125'] \n"
            + "  P1 [CRO.Person]\n"
            + "  P2 [CRO.Person]\n"
            + "  P3 [CRO.Person]\n"
            + "\n"
            + "\n"
            + "  P1->C_37091[corporate]\n"
            + "  P1->C_7661 [control]\n"
            + "  P1->C_7661 [superviseDirctor]\n"
            + "\n"
            + "  P2->C_7661[corporate]\n"
            + "  P2->C_7661 [control]\n"
            + "  P2->C_7661 [superviseDirctor]\n"
            + "\n"
            + "  P3->C_8125[corporate]\n"
            + "  P3->C_7661[superviseDirctor]\n"
            + "\n"
            + "}";
    MockLocalGraphLoader mockLocalGraphLoader = new MockLocalGraphLoader(demoGraph);
    mockLocalGraphLoader.setGraphState(new MemGraphState());
    mockLocalGraphLoader.load();
    Assert.assertEquals(0, mockLocalGraphLoader.genVertexList().size());
    Assert.assertEquals(0, mockLocalGraphLoader.genEdgeList().size());
  }
}
