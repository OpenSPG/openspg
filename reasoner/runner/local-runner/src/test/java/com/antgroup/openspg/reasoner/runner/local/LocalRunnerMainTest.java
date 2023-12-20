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

package com.antgroup.openspg.reasoner.runner.local;

import org.junit.Assert;
import org.junit.Test;

public class LocalRunnerMainTest {

  @Test
  public void testNoParams() {
    String[] args = new String[] {};
    try {
      LocalRunnerMain.main(args);
    } catch (Throwable e) {
      Assert.assertTrue(true);
      return;
    }
    Assert.assertTrue(false);
  }

  @Test
  public void testWithParams() {
    String[] args =
        new String[] {
          "-p",
          "1",
          "-start",
          "[[\"id1\",\"type1\"],[\"id2\",\"type2\"]]",
          "-q",
          "dsl1",
          "-s",
          "schema_uri",
          "-st",
          "schema_uri_token",
          "-g",
          "graph_state_class",
          "-gs",
          "graph_state_url",
          "-o",
          "/tmp/o"
        };
    try {
      LocalRunnerMain.main(args);
    } catch (RuntimeException e) {
      Assert.assertTrue(false);
      return;
    }
    Assert.assertTrue(true);
  }
}
