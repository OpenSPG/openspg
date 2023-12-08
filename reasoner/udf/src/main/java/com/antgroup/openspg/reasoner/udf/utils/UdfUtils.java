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

package com.antgroup.openspg.reasoner.udf.utils;

import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException;
import com.antgroup.openspg.reasoner.common.types.KTArray;
import com.antgroup.openspg.reasoner.common.types.KTList;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UdfUtils {
  /**
   * get udf meta key
   *
   * @param name
   * @param paramTypeList
   * @return
   */
  public static String getUdfMetaKey(
      String name, List<KgType> paramTypeList, List<KgType> resultTypeList) {
    String key = name + getTypeKeyString(paramTypeList, "(", ")");
    if (null != resultTypeList) {
      key += "->" + getTypeKeyString(resultTypeList, "", "");
    }
    return key;
  }

  /**
   * type list converted to string
   *
   * @param paramTypeList
   * @param prefix
   * @param suffix
   * @return
   */
  public static String getTypeKeyString(List<KgType> paramTypeList, String prefix, String suffix) {
    if (null == paramTypeList || paramTypeList.isEmpty()) {
      return prefix + suffix;
    }
    StringBuilder key = new StringBuilder(prefix);
    boolean first = true;
    for (KgType type : paramTypeList) {
      if (first) {
        first = false;
      } else {
        key.append(",");
      }
      key.append(type);
    }
    return key + suffix;
  }

  /**
   * If the user input parameter type is a specific type, we need to support matching to the
   * function with parameters type of Object type through overload. For example: if the user calls
   * UDF concat("str1", "str2"), we need to first match UDF function concat(String, String). If
   * there is no implementation of concat with two String types in the system, we need call
   * concat(Object, Object) function.
   *
   * @param paramTypeList
   * @return
   */
  public static Iterator<List<KgType>> getAllCompatibleParamTypeList(List<KgType> paramTypeList) {
    int notObjTypeNum =
        (int) paramTypeList.stream().filter(kgType -> !KTObject$.MODULE$.equals(kgType)).count();
    int maxIndex = (int) Math.pow(2, notObjTypeNum);
    return new Iterator<List<KgType>>() {
      private int i = 0;

      @Override
      public boolean hasNext() {
        return this.i < maxIndex;
      }

      @Override
      public List<KgType> next() {
        int flag = this.i++;
        if (0 == flag) {
          return paramTypeList;
        }
        List<KgType> compatibleParamTypeList = new ArrayList<>(paramTypeList.size());
        for (KgType kgType : paramTypeList) {
          if (KTObject$.MODULE$.equals(kgType)) {
            compatibleParamTypeList.add(kgType);
            continue;
          }
          if (flag % 2 != 0) {
            compatibleParamTypeList.add(KTObject$.MODULE$);
          } else {
            compatibleParamTypeList.add(kgType);
          }
          flag = flag / 2;
        }
        return compatibleParamTypeList;
      }
    };
  }

  /** check two param type list is compatible */
  public static boolean isParameterCompatible(
      List<KgType> udfParamList, List<KgType> inputParamList) {
    if (udfParamList.size() != inputParamList.size()) {
      throw new UnsupportedOperationException(
          "unsupported udf parameter list check, length not match.", null);
    }
    for (int i = 0; i < inputParamList.size(); ++i) {
      KgType udfParamType = udfParamList.get(i);
      KgType inputParamType = inputParamList.get(i);
      if (KTObject$.MODULE$.equals(udfParamType) || KTObject$.MODULE$.equals(inputParamType)) {
        continue;
      }
      if (udfParamType.equals(inputParamType)) {
        continue;
      } else if (inputParamType instanceof KTList && udfParamType instanceof KTList) {
        KTList inputParamTypeList = (KTList) inputParamType;
        if (null == inputParamTypeList.elementType()) {
          continue;
        }
        KTList udfParamTypeList = (KTList) udfParamType;
        if (KTObject$.MODULE$.equals(udfParamTypeList.elementType())) {
          continue;
        }
      } else if (inputParamType instanceof KTArray && udfParamType instanceof KTArray) {
        KTArray inputParamTypeArray = (KTArray) inputParamType;
        if (null == inputParamTypeArray.elementType()) {
          continue;
        }
        KTArray udfParamTypeArray = (KTArray) udfParamType;
        if (KTObject$.MODULE$.equals(udfParamTypeArray.elementType())) {
          continue;
        }
      }
      return false;
    }
    return true;
  }

  public static int compareParamList(List<KgType> o1, List<KgType> o2) {
    for (int i = 0; i < o1.size(); ++i) {
      KgType type1 = o1.get(i);
      KgType type2 = o2.get(i);
      if (type1.equals(type2)) {
        continue;
      }
      if (KTObject$.MODULE$.equals(type1)) {
        return -1;
      }
      if (KTObject$.MODULE$.equals(type2)) {
        return 1;
      }
    }
    return 0;
  }
}
