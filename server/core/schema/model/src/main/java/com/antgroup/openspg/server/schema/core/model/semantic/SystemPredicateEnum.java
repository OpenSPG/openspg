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

package com.antgroup.openspg.server.schema.core.model.semantic;

/**
 * The system's built-in predicate nouns. Generally have universal semantic expressions and fixed
 * description objects, such as inverseOf, which describes the reciprocal semantics between
 * relations, and isA which describes the hypernmy semantics between concepts.
 */
public enum SystemPredicateEnum {
  IS_A("isA", "是", PredicateFunctionEnum.HYPERNYM, PredicateScopeEnum.CONCEPT),

  LOCATE_AT("locateAt", "位于", PredicateFunctionEnum.HYPERNYM, PredicateScopeEnum.CONCEPT),

  ORIGINAL_PROPERTY(
      "originalPropertyOf", "原始属性", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.PROPERTY),

  NORMALIZED_PROPERTY(
      "normalizedPropertyOf", "标准属性", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.PROPERTY),

  BELONG_TO("belongTo", "属于", PredicateFunctionEnum.TAXONOMIC, PredicateScopeEnum.CONCEPT),

  LEAD_TO("leadTo", "触发", PredicateFunctionEnum.REASONER, PredicateScopeEnum.CONCEPT),

  INVERSE_OF("inverseOf", "互反", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.PROPERTY),

  MUTEX_OF("mutexOf", "互斥", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.PROPERTY),

  TRANSITIVE("transitive", "传递性", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.RELATION),

  SYMMETRIC("symmetric", "对称性", PredicateFunctionEnum.SEMANTIC, PredicateScopeEnum.RELATION),
  ;

  private final String name;

  private final String nameZh;

  private final PredicateFunctionEnum function;

  private final PredicateScopeEnum scope;

  SystemPredicateEnum(
      String name, String nameZh, PredicateFunctionEnum function, PredicateScopeEnum scope) {
    this.name = name;
    this.nameZh = nameZh;
    this.function = function;
    this.scope = scope;
  }

  public String getName() {
    return name;
  }

  public String getNameZh() {
    return nameZh;
  }

  public PredicateFunctionEnum getFunction() {
    return function;
  }

  public PredicateScopeEnum getScope() {
    return scope;
  }

  public static SystemPredicateEnum toEnum(String value) {
    for (SystemPredicateEnum predicateEnum : SystemPredicateEnum.values()) {
      if (predicateEnum.getName().equalsIgnoreCase(value)) {
        return predicateEnum;
      }
    }

    throw new IllegalArgumentException("unknown type: " + value);
  }
}
