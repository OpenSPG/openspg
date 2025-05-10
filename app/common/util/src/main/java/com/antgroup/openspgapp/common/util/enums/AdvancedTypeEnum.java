package com.antgroup.openspgapp.common.util.enums;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/AdvancedTypeEnum.class */
public enum AdvancedTypeEnum {
  ENTITY_TYPE("EntityType", "实体类型"),
  CONCEPT_TYPE("ConceptType", "概念类型"),
  EVENT_TYPE("EventType", "事件类型"),
  STANDARD_TYPE("StandardType", "标准类型");

  private String code;
  private String desc;

  AdvancedTypeEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static AdvancedTypeEnum toEnumByCode(String code) {
    for (AdvancedTypeEnum advancedTypeEnum : values()) {
      if (advancedTypeEnum.getCode().equalsIgnoreCase(code)) {
        return advancedTypeEnum;
      }
    }
    throw new IllegalArgumentException("code is illegal");
  }

  public static AdvancedTypeEnum toEnum(String typeName) {
    for (AdvancedTypeEnum advancedTypeEnum : values()) {
      if (advancedTypeEnum.name().equalsIgnoreCase(typeName)) {
        return advancedTypeEnum;
      }
    }
    throw new IllegalArgumentException("typeName is illegal");
  }

  public String getCode() {
    return this.code;
  }

  public String getDesc() {
    return this.desc;
  }
}
