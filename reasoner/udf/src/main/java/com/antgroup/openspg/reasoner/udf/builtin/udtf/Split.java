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

package com.antgroup.openspg.reasoner.udf.builtin.udtf;

import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;

@UdfDefine(name = "split")
public class Split extends BaseUdtf {
  private String separator = ",";

  @Override
  public List<KgType> getInputRowTypes() {
    return Lists.newArrayList(KTString$.MODULE$);
  }

  @Override
  public List<KgType> getResultTypes() {
    return Lists.newArrayList(KTString$.MODULE$);
  }

  @Override
  public void initialize(Object... parameters) {
    if (parameters.length > 0) {
      separator = String.valueOf(parameters[0]);
    }
  }

  @Override
  public void process(List<Object> args) {
    String arg = String.valueOf(args.get(0));
    for (String str : Splitter.on(separator).split(arg)) {
      forward(Lists.newArrayList(str));
    }
  }
}
