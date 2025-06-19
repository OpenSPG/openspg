package com.antgroup.openspgapp.common.util.enums;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/BasicTypeEnum.class */
public enum BasicTypeEnum {
  TEXT("Text", "文本"),
  INTEGER("Integer", "整型"),
  FLOAT("Float", "浮点数");

  private String code;
  private String desc;

  public String getCode() {
    return this.code;
  }

  public String getDesc() {
    return this.desc;
  }

  BasicTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static BasicTypeEnum getByCode(String code) {
    for (BasicTypeEnum typeEnum : values()) {
      if (typeEnum.getCode().equalsIgnoreCase(code)) {
        return typeEnum;
      }
    }
    throw new IllegalArgumentException("code is illegal");
  }

  public static boolean isBasicType(String code) {
    for (BasicTypeEnum typeEnum : values()) {
      if (typeEnum.getCode().equalsIgnoreCase(code)) {
        return true;
      }
    }
    return false;
  }
}
