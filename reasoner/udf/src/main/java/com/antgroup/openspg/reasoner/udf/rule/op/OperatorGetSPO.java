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

package com.antgroup.openspg.reasoner.udf.rule.op;

import com.ql.util.express.ArraySwap;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import org.apache.commons.lang3.StringUtils;

public class OperatorGetSPO extends OperatorBase {
  @Override
  public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
    Object[] parameterNames = new Object[list.length];

    for (int i = 0; i < list.length; ++i) {
      parameterNames[i] = ((OperateDataAttr) list.get(i)).getName();
    }
    String key = StringUtils.join(parameterNames, "_");
    Object result = parent.getParent().get(key);
    return OperateDataCacheManager.fetchOperateData(result, Object.class);
  }
}
