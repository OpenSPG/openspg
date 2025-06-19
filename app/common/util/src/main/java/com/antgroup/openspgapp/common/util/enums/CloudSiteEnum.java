package com.antgroup.openspgapp.common.util.enums;

/* loaded from: com.antgroup.openspgapp-common-util-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/util/enums/CloudSiteEnum.class */
public enum CloudSiteEnum {
  ANT("ant"),
  PUBLIC("public");

  private String value;

  CloudSiteEnum(String value) {
    this.value = value;
  }

  public static CloudSiteEnum getByValue(String value) {
    for (CloudSiteEnum kgCloudSiteEnum : values()) {
      if (kgCloudSiteEnum.getValue().equals(value)) {
        return kgCloudSiteEnum;
      }
    }
    throw new RuntimeException("invalid KgCloudSiteEnum value:" + value);
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
