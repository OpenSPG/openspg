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

/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package com.antgroup.openspg.reasoner.udf.builtin.udf;

import com.antgroup.openspg.reasoner.udf.model.UdfDefine;

public class InStr {

  @UdfDefine(name = "in_str", compatibleName = "InStr")
  public int inStr(String str, Object target) {
    return str.indexOf(String.valueOf(target), -1) + 1;
  }

  @UdfDefine(name = "in_str", compatibleName = "InStr")
  public int inStr(String str, Object target, int index) {
    return str.indexOf(String.valueOf(target), index - 1) + 1;
  }
}
