/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.common.table;

import com.antgroup.openspg.reasoner.common.types.KTBoolean$;
import com.antgroup.openspg.reasoner.common.types.KTDouble$;
import com.antgroup.openspg.reasoner.common.types.KTInteger$;
import com.antgroup.openspg.reasoner.common.types.KTLong$;
import com.antgroup.openspg.reasoner.common.types.KTString$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import java.io.Serializable;

/**
 * @author donghai.ydh
 * @version FieldType.java, v 0.1 2023年10月20日 14:49 donghai.ydh
 */
public enum FieldType implements Serializable {
  STRING(KTString$.MODULE$),
  INT(KTInteger$.MODULE$),
  LONG(KTLong$.MODULE$),
  DOUBLE(KTDouble$.MODULE$),
  BOOLEAN(KTBoolean$.MODULE$),
  UNKNOWN(KTString$.MODULE$),
  ;

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
