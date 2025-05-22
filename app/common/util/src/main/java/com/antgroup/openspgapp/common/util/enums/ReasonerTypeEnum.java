package com.antgroup.openspgapp.common.util.enums;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/ReasonerTypeEnum.class */
public enum ReasonerTypeEnum {
  DSL("dsl"),
  ENTITY("entity"),
  ENTITY_RELATION("entity_relation");

  String desc;

  ReasonerTypeEnum(String desc) {
    this.desc = desc;
  }

  public static ReasonerTypeEnum getByName(String name) {
    for (ReasonerTypeEnum rte : values()) {
      if (rte.name().equals(name)) {
        return rte;
      }
    }
    throw new RuntimeException("invalid ReasonerTypeEnum : " + name);
  }
}
