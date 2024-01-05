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

package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

@UdfDefine(name = "first", compatibleName = "First")
public class First implements BaseUdaf {
  private Object first = null;

  @Override
  public KgType getInputRowType() {
    return KTObject$.MODULE$;
  }

  @Override
  public KgType getResultType() {
    return KTObject$.MODULE$;
  }

  @Override
  public void initialize(Object... params) {}

  @Override
  public void update(Object row) {
    if (null == this.first) {
      this.first = row;
    }
  }

  @Override
  public void merge(BaseUdaf function) {}

  @Override
  public Object evaluate() {
    return this.first;
  }
}
