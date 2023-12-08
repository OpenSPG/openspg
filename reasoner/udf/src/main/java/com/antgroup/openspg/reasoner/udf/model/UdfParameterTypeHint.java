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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import scala.Tuple2;

public class UdfParameterTypeHint implements Serializable {
  /** udf name */
  private final String name;

  /**
   * map key is parameter number
   *
   * <p>The first element of Tuple2 is parameter type list The second element of Tuple2 is udf
   * result type list
   */
  private final Map<Integer, List<Tuple2<List<KgType>, List<KgType>>>> udfParameterTypeMap;

  public UdfParameterTypeHint(String name, List<Tuple2<List<KgType>, List<KgType>>> typeInfoList) {
    this.name = name;
    Map<Integer, List<Tuple2<List<KgType>, List<KgType>>>> udfParameterTypeMap = new HashMap<>();
    for (Tuple2<List<KgType>, List<KgType>> typeTuple2 : typeInfoList) {
      List<KgType> paramTypeList = typeTuple2._1();
      List<KgType> resultTypeList = typeTuple2._2();

      List<Tuple2<List<KgType>, List<KgType>>> typeList =
          udfParameterTypeMap.computeIfAbsent(paramTypeList.size(), k -> new ArrayList<>());
      typeList.add(new Tuple2<>(paramTypeList, resultTypeList));
    }
    this.udfParameterTypeMap = udfParameterTypeMap;
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
   * Getter method for property <tt>udfParameterTypeList</tt>.
   *
   * @return property value of udfParameterTypeList
   */
  public Map<Integer, List<Tuple2<List<KgType>, List<KgType>>>> getUdfParameterTypeMap() {
    return udfParameterTypeMap;
  }

  @Override
  public String toString() {
    return "name=" + name + ",typeMap=" + udfParameterTypeMap;
  }
}
