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

package com.antgroup.openspg.reasoner.udf.builtin.udaf;

import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdaf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

@UdfDefine(name = "concat_agg", compatibleName = "concat_agg")
public class ConcatAgg implements BaseUdaf {
  private StringBuilder stringBuilder;
  private static final String SPLITTER = ",";

  @Override
  public KgType getInputRowType() {
    return KTObject$.MODULE$;
  }

  @Override
  public KgType getResultType() {
    return KTString$.MODULE$;
  }

  @Override
  public void initialize(Object... params) {
    this.stringBuilder = new StringBuilder();
  }

  @Override
  public void update(Object row) {
    if (stringBuilder.length() > 0) {
      stringBuilder.append(SPLITTER);
    }
    stringBuilder.append(row);
  }

  @Override
  public void merge(BaseUdaf function) {
    ConcatAgg other = (ConcatAgg) function;
    if (stringBuilder.length() > 0) {
      stringBuilder.append(SPLITTER);
    }
    this.stringBuilder.append(other.stringBuilder.toString());
  }

  @Override
  public Object evaluate() {
    return this.stringBuilder.toString();
  }
}
