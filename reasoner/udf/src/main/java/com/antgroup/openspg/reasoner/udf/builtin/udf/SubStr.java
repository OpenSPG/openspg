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
import org.apache.commons.lang3.StringUtils;

public class SubStr {
  @UdfDefine(name = "sub_str", compatibleName = "SubStr")
  public Object subStrWithOutEndIndex(String str, int start) throws Exception {
    return StringUtils.substring(str, start - 1);
  }

  @UdfDefine(name = "sub_str", compatibleName = "SubStr")
  public Object subStrWithEndIndex(String str, int start, int end) throws Exception {
    int startIndex = start - 1;
    int endIndex = startIndex + end;
    return StringUtils.substring(str, startIndex, endIndex);
  }
}
