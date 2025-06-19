package com.antgroup.openspgapp.common.util.enums;

import com.antgroup.openspgapp.common.util.utils.EnumSelectAnnotation;

@EnumSelectAnnotation
/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/BuilderJobStatus.class */
public enum BuilderJobStatus {
  WAIT("等待中"),
  RUNNING("执行中"),
  ERROR("异常"),
  TERMINATE("终止"),
  PENDING("待导入"),
  FINISH("完成");

  String text;

  BuilderJobStatus(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }
}
