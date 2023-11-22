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

import com.antgroup.openspg.server.schema.core.model.BasicInfo;
import com.antgroup.openspg.server.schema.core.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.server.schema.core.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.server.schema.core.model.predicate.Property;
import com.antgroup.openspg.server.schema.core.model.predicate.Relation;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;

/**
 * Class definition of literal with standard format constraint and semantics. <br>
 *
 * <p>In the real world, some entities have standard format constraints and carry certain semantics.
 * Usually users can understand the meaning of the entity and what format the physical data needs to
 * meet according to entity name. Such as email-address, phone-number, mac-address and so on. <br>
 * Like {@link BasicType}, {@link StandardType} are usually only used as value types of property and
 * cannot be used as the subject of SPO triples.<br>
 * There are also differences between standard types. Some standard types are spreadable, that is,
 * based on the entity of the standard type, you can reversely find which entities point to it, such
 * as mobile phone numbers, email addresses etc., while some standard types only have format
 * constraints and are not semantically communicable, such as timestamp and constellation.
 */
public class StandardType extends BaseAdvancedType {

  private static final long serialVersionUID = 7362461635734268212L;

  /** The namespace of standard types, the unique name of standard type is start with {@code STD} */
  public static final String STD_NAMESPACE = "STD";

  /** If the standardType is spreadable, we can find source node by the standard type node */
  private final Boolean spreadable;

  /**
   * The constraint is used to normalize the property value that is attached to the StandardType.
   */
  private final List<BaseConstraintItem> constraintItems;

  public StandardType(
      BasicInfo<SPGTypeIdentifier> basicInfo,
      ParentTypeInfo parentTypeInfo,
      List<Property> properties,
      List<Relation> relations,
      SPGTypeAdvancedConfig advancedConfig,
      Boolean spreadable,
      List<BaseConstraintItem> constraintItems) {
    super(
        basicInfo,
        parentTypeInfo,
        SPGTypeEnum.STANDARD_TYPE,
        properties,
        relations,
        advancedConfig);

    this.spreadable = spreadable != null && spreadable;
    this.constraintItems = constraintItems;
    this.getAdvancedConfig().setVisibleScope(VisibleScopeEnum.PUBLIC);
  }

  public Boolean getSpreadable() {
    return BooleanUtils.isTrue(spreadable);
  }

  public List<BaseConstraintItem> getConstraintItems() {
    return constraintItems;
  }
}
