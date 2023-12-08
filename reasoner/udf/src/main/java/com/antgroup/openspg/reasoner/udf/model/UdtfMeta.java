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

/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import com.google.common.base.Splitter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class UdtfMeta implements IMetaBase {
  /** name of udaf */
  private final String name;

  /** compatible with old udf name set */
  private final Set<String> compatibleNameSet;

  /** description for udf */
  private final String description;

  /** type list of a row of data */
  private final List<KgType> rowDataTypes;

  /** result types */
  private final List<KgType> resultTypes;

  /** aggregation implement class */
  private final Class<? extends BaseUdtf> tableFunctionClass;

  public UdtfMeta(
      String name,
      String compatibleName,
      String description,
      Class<? extends BaseUdtf> tableFunctionClass) {
    this.name = name;
    this.description = description;
    this.tableFunctionClass = tableFunctionClass;
    BaseUdtf baseUdtf = this.createTableFunction();
    this.rowDataTypes = baseUdtf.getInputRowTypes();
    this.resultTypes = baseUdtf.getResultTypes();
    this.compatibleNameSet =
        StreamSupport.stream(Splitter.on(",").split(compatibleName).spliterator(), false)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toSet());
  }

  /**
   * create table function object
   *
   * @return
   */
  public BaseUdtf createTableFunction() {
    BaseUdtf tableFunction;
    try {
      tableFunction = this.tableFunctionClass.getConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new UnsupportedOperationException(
          "create udtf meta error, className " + this.tableFunctionClass.getName(), e);
    }
    return tableFunction;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return property value of name
   */
  public String getName() {
    return name;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return
   */
  @Override
  public Set<String> getCompatibleNames() {
    return compatibleNameSet;
  }

  @Override
  public List<KgType> getParamTypeList() {
    return getRowDataTypes();
  }

  /**
   * Getter method for property <tt>description</tt>.
   *
   * @return property value of description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Getter method for property <tt>rowDataTypes</tt>.
   *
   * @return property value of rowDataTypes
   */
  public List<KgType> getRowDataTypes() {
    return rowDataTypes;
  }

  /**
   * Getter method for property <tt>resultTypes</tt>.
   *
   * @return property value of resultTypes
   */
  public List<KgType> getResultTypes() {
    return resultTypes;
  }

  @Override
  public String toString() {
    return UdfUtils.getUdfMetaKey(this.name, this.rowDataTypes, this.resultTypes);
  }
}
