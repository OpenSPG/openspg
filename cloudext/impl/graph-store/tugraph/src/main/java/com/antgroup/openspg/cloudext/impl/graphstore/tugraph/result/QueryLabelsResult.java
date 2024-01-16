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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.result;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;

/** Result of querying labels. */
public class QueryLabelsResult implements Serializable {

  /** Name of label. */
  @JSONField(name = "labelName")
  private String labelName;

  /**
   * Getter method for property <tt>labelName</tt>.
   *
   * @return property value of labelName
   */
  public String getLabelName() {
    return labelName;
  }

  /**
   * Setter method for property <tt>labelName</tt>.
   *
   * @param labelName value to be assigned to property labelName
   */
  public void setLabelName(String labelName) {
    this.labelName = labelName;
  }
}
