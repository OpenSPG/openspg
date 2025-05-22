package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/SofaMQ.class */
public enum SofaMQ {
  endPoint("{\"cn\":\"endPoint\",\"required\":true}"),
  topic("{\"cn\":\"topic\",\"required\":true}"),
  group("{\"cn\":\"group\",\"required\":true}"),
  schemaRegistryUrl("{\"cn\":\"schemaRegistryUrl\",\"required\":true}");

  String text;

  SofaMQ(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
