package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/LLMType.class */
public enum LLMType {
  DEFAULT("Default");

  String text;

  LLMType(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
