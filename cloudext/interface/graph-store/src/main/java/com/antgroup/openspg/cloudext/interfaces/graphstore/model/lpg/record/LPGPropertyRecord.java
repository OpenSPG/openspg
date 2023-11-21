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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.record;

import com.antgroup.openspg.common.model.base.BaseValObj;
import com.antgroup.openspg.core.spgschema.model.type.BasicTypeEnum;
import lombok.Getter;

/**
 * Represents a property record in the labeled property graph<tt>(LPG)</tt>. {@link
 * LPGPropertyRecord LPGPropertyRecord} is the atomic carrier of knowledge in a <tt>LPG</tt>. {@link
 * LPGPropertyRecord LPGPropertyRecord} is added to {@link VertexRecord VertexRecord} or {@link
 * EdgeRecord EdgeRecord}, and provides additional information to help us better understand the
 * content in the graph. It is composed of an property name and an property value. The property name
 * is used to describe the characteristics or types of the property, while the property value
 * represents the specific property value. Valid property value types are all {@link BasicTypeEnum
 * BaiscType}, including:
 *
 * <ul>
 *   <li><code>TEXT</code>
 *   <li><code>LONG</code>
 *   <li><code>FLOAT</code>
 * </ul>
 */
@Getter
public class LPGPropertyRecord extends BaseValObj {

  /** The property name */
  private final String name;

  /** The property value */
  private final Object value;

  public LPGPropertyRecord(String name, Object value) {
    this.name = name;
    this.value = value;
  }
}
