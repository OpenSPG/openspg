package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/BuilderJobType.class */
public enum BuilderJobType {
  FILE_EXTRACT("文件抽取"),
  YUQUE_EXTRACT("语雀抽取");

  String text;

  BuilderJobType(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
