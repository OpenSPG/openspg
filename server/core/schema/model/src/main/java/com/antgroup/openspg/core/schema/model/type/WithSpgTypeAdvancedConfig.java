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

package com.antgroup.openspg.core.schema.model.type;

/** An interface provides some methods to check and get advanced config of type. */
public interface WithSpgTypeAdvancedConfig {

  /**
   * Get advanced config of spg type.
   *
   * @return config
   */
  SPGTypeAdvancedConfig getAdvancedConfig();

  /**
   * If type contains linking operator.
   *
   * @return true/false
   */
  default boolean hasLinkOperator() {
    return getLinkOperator() != null;
  }

  /**
   * Get linking operator of type.
   *
   * @return operator key
   */
  default OperatorKey getLinkOperator() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getLinkOperator() : null;
  }

  /**
   * If type contains fusing operator.
   *
   * @return true/false
   */
  default boolean hasFuseOperator() {
    return getFuseOperator() != null;
  }

  /**
   * Get linking operator of type.
   *
   * @return operator key
   */
  default OperatorKey getFuseOperator() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getFuseOperator() : null;
  }

  /**
   * If type contains extracting operator.
   *
   * @return true/false
   */
  default boolean hasExtractOperator() {
    return getExtractOperator() != null;
  }

  /**
   * Get extracting operator of type.
   *
   * @return operator key
   */
  default OperatorKey getExtractOperator() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getExtractOperator() : null;
  }

  /**
   * If type contains normalizing operator.
   *
   * @return true/false
   */
  default boolean hasNormalizedOperator() {
    return getNormalizedOperator() != null;
  }

  /**
   * Get normalizing operator of type.
   *
   * @return operator key
   */
  default OperatorKey getNormalizedOperator() {
    return getAdvancedConfig() != null ? getAdvancedConfig().getNormalizedOperator() : null;
  }
}
