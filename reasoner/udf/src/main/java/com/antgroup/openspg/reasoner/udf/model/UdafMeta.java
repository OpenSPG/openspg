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

package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class UdafMeta implements IMetaBase {

  /** name of udaf */
  protected final String name;

  /** compatible with old udf name */
  private final Set<String> compatibleNameSet;

  /** description for udf */
  protected final String description;

  /** type list of a row of data */
  protected final KgType rowDataType;

  /** result type */
  protected final KgType resultType;

  /** type for udf */
  protected final UdfOperatorTypeEnum udfType;

  /** aggregation implement class */
  protected final Class<? extends BaseUdaf> aggregateClass;

  /**
   * udaf meta information
   *
   * @param name
   * @param udfType
   * @param aggregateClass
   */
  public UdafMeta(
      String name,
      String compatibleName,
      String description,
      UdfOperatorTypeEnum udfType,
      Class<? extends BaseUdaf> aggregateClass) {
    this.name = name;
    this.description = description;
    this.udfType = udfType;
    this.aggregateClass = aggregateClass;
    BaseUdaf udfObj = this.createAggregateFunction();
    this.rowDataType = udfObj.getInputRowType();
    this.resultType = udfObj.getResultType();
    this.compatibleNameSet =
        StreamSupport.stream(Splitter.on(",").split(compatibleName).spliterator(), false)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toSet());
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  @Override
  public Set<String> getCompatibleNames() {
    return compatibleNameSet;
  }

  @Override
  public List<KgType> getParamTypeList() {
    return Lists.newArrayList(getRowDataType());
  }

  /**
   * create aggregate function
   *
   * @return
   */
  public BaseUdaf createAggregateFunction() {
    BaseUdaf aggregateFunction;
    try {
      aggregateFunction = aggregateClass.getConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new UnsupportedOperationException(
          "create udaf meta error, className " + this.aggregateClass.getName(), e);
    }
    return aggregateFunction;
  }

  /**
   * Getter method for property <tt>inputTypes</tt>.
   *
   * @return property value of inputTypes
   */
  public KgType getRowDataType() {
    return rowDataType;
  }

  /**
   * Getter method for property <tt>outputType</tt>.
   *
   * @return property value of outputType
   */
  public KgType getResultType() {
    return resultType;
  }

  @Override
  public String toString() {
    return UdfUtils.getUdfMetaKey(
        this.name, Lists.newArrayList(this.rowDataType), Lists.newArrayList(this.resultType));
  }

  /**
   * Getter method for property <tt>description</tt>.
   *
   * @return property value of description
   */
  public String getDescription() {
    return description;
  }
}
