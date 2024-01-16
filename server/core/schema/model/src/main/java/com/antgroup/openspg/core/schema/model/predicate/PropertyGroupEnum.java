/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.core.schema.model.predicate;

/**
 * Property group enums of event type, since event type must have properties about
 * time/location/subject/obejct etc.
 */
public enum PropertyGroupEnum {

  /** Properties in this group are generally of date and time type. */
  TIME("time"),

  /** Properties in this group are generally subject of an EventType. */
  SUBJECT("subject"),

  /** Properties in this group are generally object of an EventType. */
  OBJECT("object"),

  /** Properties in this group are generally represent geographic location. */
  LOC("loc");

  /** Name of group. */
  private final String nameEn;

  PropertyGroupEnum(String nameEn) {
    this.nameEn = nameEn;
  }

  public static PropertyGroupEnum toEnum(String name) {
    for (PropertyGroupEnum propertyGroupEnum : PropertyGroupEnum.values()) {
      if (propertyGroupEnum.name().equals(name)) {
        return propertyGroupEnum;
      }
    }
    return null;
  }

  public String getNameEn() {
    return nameEn;
  }
}
