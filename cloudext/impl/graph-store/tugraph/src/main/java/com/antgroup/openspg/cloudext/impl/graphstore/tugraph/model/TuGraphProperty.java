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

package com.antgroup.openspg.cloudext.impl.graphstore.tugraph.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/** Property of TuGraph. */
@Data
public class TuGraphProperty {

  /** Name of property */
  @JSONField(name = "name")
  private String name;

  /** Type of property value */
  @JSONField(name = "type")
  private DataTypeEnum type;

  /** Is index */
  @JSONField(name = "index")
  private Boolean index;

  /** Is optional */
  @JSONField(name = "optional")
  private Boolean optional;

  /** Is unique */
  @JSONField(name = "unique")
  private Boolean unique;
}
