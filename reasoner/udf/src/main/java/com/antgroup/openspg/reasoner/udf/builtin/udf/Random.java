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

package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class Random {

  private final java.util.Random random = new java.util.Random();

  @UdfDefine(name = "random", compatibleName = "randomInt")
  public int randomInt() {
    return random.nextInt();
  }

  @UdfDefine(name = "random", compatibleName = "randomInt")
  public int randomInt(int bound) {
    return random.nextInt(bound);
  }

  @UdfDefine(name = "randomLong")
  public long randomLong() {
    return random.nextLong();
  }
}
