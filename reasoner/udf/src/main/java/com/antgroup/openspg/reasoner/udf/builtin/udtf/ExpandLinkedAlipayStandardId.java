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
package com.antgroup.openspg.reasoner.udf.builtin.udtf;

import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.BaseUdtf;
import com.antgroup.openspg.reasoner.udf.model.LinkedUdtfResult;
import com.antgroup.openspg.reasoner.udf.model.UdfDefine;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j(topic = "userlogger")
@UdfDefine(name = "expand_linked_alipay_id")
public class ExpandLinkedAlipayStandardId extends BaseUdtf {

  /**
   * udtf input row data type list
   *
   * @return
   */
  @Override
  public List<KgType> getInputRowTypes() {
    return Lists.newArrayList(KTString$.MODULE$);
  }

  /**
   * udtf result type list
   *
   * @return
   */
  @Override
  public List<KgType> getResultTypes() {
    return Lists.newArrayList(KTObject$.MODULE$);
  }

  @Override
  public void process(List<Object> args) {
    if (null == args || args.size() != 1) {
      throw new RuntimeException(
          "expand_linked_alipay_id should have 1 parameters with standard alipay id");
    }
    if (null == args.get(0) || StringUtils.isBlank(args.get(0).toString())) {
      log.warn("expand_linked_alipay_id id is empty");
      return;
    }

    LinkedUdtfResult result = new LinkedUdtfResult();
    String standardAlipayId = args.get(0).toString();
    result.getTargetVertexIdList().add(standardAlipayId);
    forward(Lists.newArrayList(result));
  }
}
