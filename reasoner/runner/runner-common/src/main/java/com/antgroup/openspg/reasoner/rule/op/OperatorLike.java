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
package com.antgroup.openspg.reasoner.rule.op;

/**
 * like op without exception
 *
 * @author chengqiang.cq
 * @version $Id: OperatorLike.java, v 0.1 2022-11-20 10:56 chengqiang.cq Exp $$
 */
public class OperatorLike extends com.ql.util.express.instruction.op.OperatorLike {
  public OperatorLike(String name) {
    super(name);
  }

  @Override
  public Object executeInner(Object[] list) throws Exception {
    if (list[0] == null || list[1] == null) {
      return false;
    } else {
      return executeInner(list[0], list[1]);
    }
  }
}
