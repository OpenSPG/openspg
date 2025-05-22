package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/SLS.class */
public enum SLS {
  project("{\"cn\":\"project\",\"required\":true}"),
  endPoint("{\"cn\":\"endPoint\",\"required\":true}"),
  logStore("{\"cn\":\"logStore\",\"required\":true}"),
  accessId("{\"cn\":\"accessId\",\"required\":true}"),
  accessKey("{\"cn\":\"accessKey\",\"required\":true}"),
  layout("{\"cn\":\"layout\",\"required\":true}");

  String text;

  SLS(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
