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

package com.antgroup.openspg.server.schema.core.model.predicate;

import com.antgroup.openspg.core.spgschema.model.constraint.Constraint;
import com.antgroup.openspg.core.spgschema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.spgschema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.spgschema.model.type.WithBasicInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

public interface WithPropertyAdvancedConfig {

  /**
   * Get advanced config of property.
   *
   * @return config object
   */
  PropertyAdvancedConfig getAdvancedConfig();

  /**
   * Get multiple version config of property.
   *
   * @return config
   */
  default MultiVersionConfig getMultiVersionConfig() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getMultiVersionConfig() : null;
  }

  /**
   * Get mounted concept config of property.
   *
   * @return config
   */
  default MountedConceptConfig getMountedConceptConfig() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getMountedConceptConfig() : null;
  }

  /**
   * Get encrypt type of property.
   *
   * @return encrypt type
   */
  default EncryptTypeEnum getEncryptTypeEnum() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getEncryptTypeEnum() : null;
  }

  /**
   * Get property group
   *
   * @return group enum
   */
  default PropertyGroupEnum getPropertyGroup() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getPropertyGroup() : null;
  }

  /**
   * Get constraint of property.
   *
   * @return constraint
   */
  default Constraint getConstraint() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getConstraint() : null;
  }

  /**
   * If property has sub properties.
   *
   * @return true/false
   */
  default boolean hasSubProperty() {
    return CollectionUtils.isNotEmpty(getSubProperties());
  }

  /**
   * Get sub properties of property
   *
   * @return list
   */
  default List<SubProperty> getSubProperties() {
    return getAdvancedConfig() != null
        ? getAdvancedConfig().getSubProperties()
        : Collections.emptyList();
  }

  /**
   * Get a map contains sub properties of property.
   *
   * @return map
   */
  default Map<String, SubProperty> getSubPropertyMap() {
    List<SubProperty> subProperties = getSubProperties();
    if (CollectionUtils.isEmpty(subProperties)) {
      return Collections.emptyMap();
    }
    return subProperties.stream()
        .collect(Collectors.toMap(WithBasicInfo::getName, Function.identity(), (x1, x2) -> x1));
  }

  /**
   * Get a list of predicate semantic of property
   *
   * @return list
   */
  default List<PredicateSemantic> getSemantics() {
    return getAdvancedConfig() != null
        ? getAdvancedConfig().getSemantics()
        : Collections.emptyList();
  }

  /**
   * Get logical rule of property
   *
   * @return rule
   */
  default LogicalRule getLogicalRule() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getLogicalRule() : null;
  }

  /**
   * If the property belongs to subject group.
   *
   * @return true/false
   */
  default boolean isSubjectProperty() {
    return getAdvancedConfig() != null
        && getAdvancedConfig().getPropertyGroup() != null
        && PropertyGroupEnum.SUBJECT.equals(getAdvancedConfig().getPropertyGroup());
  }

  /**
   * If the property belongs to object group.
   *
   * @return true/false
   */
  default boolean isObjectProperty() {
    return getAdvancedConfig() != null
        && getAdvancedConfig().getPropertyGroup() != null
        && PropertyGroupEnum.OBJECT.equals(getAdvancedConfig().getPropertyGroup());
  }
}
