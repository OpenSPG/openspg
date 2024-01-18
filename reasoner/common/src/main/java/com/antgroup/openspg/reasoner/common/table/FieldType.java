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

package com.antgroup.openspg.reasoner.common.table;

import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import java.io.Serializable;

public enum FieldType implements Serializable {
  STRING(KTString$.MODULE$),
  INT(KTInteger$.MODULE$),
  LONG(KTLong$.MODULE$),
  DOUBLE(KTDouble$.MODULE$),
  BOOLEAN(KTBoolean$.MODULE$),
  UNKNOWN(KTString$.MODULE$),
  OBJECT(KTObject$.MODULE$);

  private final KgType kgType;

  FieldType(KgType kgType) {
    this.kgType = kgType;
  }

  /**
   * Getter method for property <tt>kgType</tt>.
   *
   * @return property value of kgType
   */
  public KgType getKgType() {
    return kgType;
  }

  /** init */
  public static FieldType fromKgType(KgType kgType) {
    if (null == kgType) {
      return UNKNOWN;
    }
    for (FieldType fieldType : FieldType.values()) {
      if (fieldType == UNKNOWN) {
        continue;
      }
      if (fieldType.getKgType().equals(kgType)) {
        return fieldType;
      }
    }
    return STRING;
  }
}
