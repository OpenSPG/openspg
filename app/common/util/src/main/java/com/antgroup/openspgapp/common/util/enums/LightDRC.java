package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/LightDRC.class */
public enum LightDRC {
  drcGuid("{\"cn\":\"drcGuid\",\"required\":false}"),
  drcSite("{\"cn\":\"drcSite\",\"required\":false}");

  String text;

  LightDRC(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
