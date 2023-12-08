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

import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class UdfMeta implements IUdfMeta {
  /** name of udf */
  private final String name;

  /** compatible with old udf name set */
  private final Set<String> compatibleNameSet;

  /** description for udf */
  private final String description;

  /** parameter type list */
  private final List<KgType> paramTypeList;
  /** udf result type */
  private final KgType resultType;

  /** java object of this udf function */
  private final Object obj;

  /** method of udf */
  private final Method method;

  /** udf type */
  private final UdfOperatorTypeEnum udfOperatorTypeEnum;

  /**
   * udf元信息
   *
   * @param name
   * @param compatibleName
   * @param description
   * @param udfOperatorTypeEnum
   * @param paramTypeList
   * @param resultType
   * @param obj
   * @param method
   */
  public UdfMeta(
      String name,
      String compatibleName,
      String description,
      UdfOperatorTypeEnum udfOperatorTypeEnum,
      List<KgType> paramTypeList,
      KgType resultType,
      Object obj,
      Method method) {
    this.name = name;
    this.compatibleNameSet =
        StreamSupport.stream(Splitter.on(",").split(compatibleName).spliterator(), false)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toSet());
    this.udfOperatorTypeEnum = udfOperatorTypeEnum;
    this.description = description;
    this.paramTypeList = paramTypeList;
    this.resultType = resultType;
    this.method = method;
    this.obj = obj;
  }

  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * Getter method for property <tt>compatibleName</tt>.
   *
   * @return
   */
  public Set<String> getCompatibleNames() {
    return compatibleNameSet;
  }

  /**
   * Getter method for property <tt>paramTypeList</tt>.
   *
   * @return
   */
  public List<KgType> getParamTypeList() {
    return paramTypeList;
  }

  /**
   * Getter method for property <tt>resultType</tt>.
   *
   * @return
   */
  public KgType getResultType() {
    return resultType;
  }

  /**
   * Getter method for property <tt>udfOperatorTypeEnum</tt>.
   *
   * @return
   */
  @Override
  public UdfOperatorTypeEnum getUdfType() {
    return udfOperatorTypeEnum;
  }
  /**
   * invoke udf method
   *
   * @param args
   * @return
   */
  public Object invoke(Object... args) {
    try {
      return method.invoke(obj, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalArgumentException(
          paramTypeList.toString(),
          Arrays.toString(args),
          "invoke udf error, class="
              + this.obj.getClass().getName()
              + ",method="
              + this.method.getName(),
          e);
    }
  }

  @Override
  public String toString() {
    return UdfUtils.getUdfMetaKey(
        this.name, this.paramTypeList, Lists.newArrayList(this.resultType));
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
