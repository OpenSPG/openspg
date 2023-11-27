/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.schema.core.model.type;

import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import java.util.ArrayList;

/**
 * Class definition of literal constant. <br>
 *
 * <p>In the real world, the property values of some entities may be string constant、numbers or
 * other literals without specific semantics, For example, the name of a person is a text string
 * consisting of characters, and the thickness of a mobile phone is a numerical value that does not
 * carry any specific meaning by itself. <br>
 * In the SPG framework we define {@link BasicType} to represent literal constants. The difference
 * from other SPG types is that the {@link BasicType} is only used for the value type of entity
 * properties and cannot be used as a subject type to define SPO triples. The {@link BasicType} are
 * categorized into three situations:
 *
 * <ul>
 *   <li>Integer: used to describe integer values such as 1, 200, 9999 etc.
 *   <li>Float: Used to describe decimals values such as 0.021, -111.11 etc.
 *   <li>Text: Used to describe string or textual values such as "abc" and "alibaba" etc.
 * </ul>
 */
public class BasicType extends BaseSPGType {

  private static final long serialVersionUID = 863222305381414709L;

  /** Enumeration values for basic type. */
  private final BasicTypeEnum basicType;

  private BasicType(BasicTypeEnum basicType) {
    super(
        new BasicInfo<>(new SPGTypeIdentifier(null, basicType.getFlag())),
        null,
        SPGTypeEnum.BASIC_TYPE,
        new ArrayList<>(),
        new ArrayList<>(),
        new SPGTypeAdvancedConfig(VisibleScopeEnum.PUBLIC));
    this.basicType = basicType;
  }

  public BasicTypeEnum getBasicType() {
    return basicType;
  }

  public static class TextBasicType extends BasicType {

    public TextBasicType() {
      super(BasicTypeEnum.TEXT);
    }
  }

  public static class LongBasicType extends BasicType {

    public LongBasicType() {
      super(BasicTypeEnum.LONG);
    }
  }

  public static class DoubleBasicType extends BasicType {

    public DoubleBasicType() {
      super(BasicTypeEnum.DOUBLE);
    }
  }

  public static BasicType from(String basicType) {
    switch (BasicTypeEnum.from(basicType)) {
      case TEXT:
        return new TextBasicType();
      case LONG:
        return new LongBasicType();
      case DOUBLE:
        return new DoubleBasicType();
      default:
        throw new IllegalArgumentException("illegal basicType=" + basicType);
    }
  }
}
