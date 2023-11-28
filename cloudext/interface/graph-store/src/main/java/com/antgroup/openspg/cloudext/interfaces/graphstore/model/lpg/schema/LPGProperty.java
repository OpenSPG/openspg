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

package com.antgroup.openspg.cloudext.interfaces.graphstore.model.lpg.schema;

import com.antgroup.openspg.core.schema.model.type.BasicTypeEnum;
import com.antgroup.openspg.server.common.model.base.BaseValObj;
import lombok.Getter;

/**
 * {@link LPGProperty LPGProperty} represents definition of property in <tt>LPG</tt>, and contains
 * four fields are as the following:
 * <li><code>{@link LPGProperty#name name}:</code> name of property.
 * <li><code>{@link LPGProperty#type type}:</code> type of property value, such as TEXT, LONG,
 *     DOUBLE.
 * <li><code>{@link LPGProperty#optional optional}:</code> <strong>TRUE</strong> if property value
 *     can be <strong>NULL</strong>, otherwise false.
 * <li><code>{@link LPGProperty#isPrimaryKey isPrimaryKey}:</code> <strong>TRUE</strong>if it is
 *     primary key of <tt>LPGOntology</tt>. <strong>NOTE 1: </strong> when property is a primary
 *     key, it must be <strong>NOT</strong> optional. <strong>NOTE 2: </strong> default value of
 *     <code>{@link LPGProperty#isPrimaryKey isPrimaryKey}</code> is <strong>FALSE</strong>.
 */
@Getter
public class LPGProperty extends BaseValObj {

  private final String name;

  private final BasicTypeEnum type;

  private boolean optional;

  private boolean isPrimaryKey = false;

  public LPGProperty(String name, BasicTypeEnum type) {
    this(name, type, true);
  }

  public LPGProperty(String name, BasicTypeEnum type, boolean optional) {
    this.name = name;
    this.type = type;
    this.optional = optional;
  }

  public void setOptional(boolean optional) {
    this.optional = optional;
  }

  public void setPrimaryKey(boolean primaryKey) {
    isPrimaryKey = primaryKey;
    setOptional(false);
  }
}
