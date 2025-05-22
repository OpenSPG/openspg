package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/ExtractDomainType.class */
public enum ExtractDomainType {
  ENCYC("百科"),
  GOV("政务"),
  EVENT("事件");

  String text;

  ExtractDomainType(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
