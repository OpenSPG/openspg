/*
 * Copyright 2023 OpenSPG Authors
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

package com.antgroup.openspg.reasoner.context;

import org.junit.Assert;

public class ConfigCheckInitializer extends BaseContextInitializer<String> {

  @Override
  public String initOnDriver() {
    Assert.assertEquals(this.taskRecord.getParams().get("config1"), "1");
    Assert.assertEquals(this.taskRecord.getParams().get("config2"), "2");
    return "";
  }

  @Override
  public void dispatchToWorker(String obj) {
    Assert.assertEquals(this.taskRecord.getParams().get("config1"), "1");
    Assert.assertEquals(this.taskRecord.getParams().get("config2"), "2");
  }
}
