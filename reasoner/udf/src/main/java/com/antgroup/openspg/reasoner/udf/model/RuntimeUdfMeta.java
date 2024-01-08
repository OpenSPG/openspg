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

package com.antgroup.openspg.reasoner.udf.model;

import com.antgroup.openspg.reasoner.common.Utils;
import com.antgroup.openspg.reasoner.common.exception.UnsupportedOperationException;
import com.antgroup.openspg.reasoner.common.types.KTObject$;
import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.utils.UdfUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The runtime UDF means that the number and type of parameters cannot be clearly obtained at the
 * definition stage, and the number and type of parameters can only be determined at the runtime
 */
public class RuntimeUdfMeta {
  private final UdfOperatorTypeEnum udfType;
  private final String name;

  private final Map<String, IUdfMeta> udfMetaMap;

  /** A mapping structure for efficient query of udfMeta. */
  private final Map<Integer, List<List<KgType>>> udfParamQueryMap = new HashMap<>();

  /**
   * runtime udf
   *
   * @param name
   * @param udfMetaMap
   */
  public RuntimeUdfMeta(String name, Map<String, IUdfMeta> udfMetaMap) {
    this.name = name;
    this.udfMetaMap = udfMetaMap;
    UdfOperatorTypeEnum funcUdfType = null;
    for (Map.Entry<String, IUdfMeta> entry : this.udfMetaMap.entrySet()) {
      IUdfMeta udfMeta = entry.getValue();
      if (funcUdfType == null) {
        funcUdfType = udfMeta.getUdfType();
      }
      if (!udfMeta.getUdfType().equals(funcUdfType)) {
        throw new RuntimeException(name + " must has one udf type, FUNCTION or OPERATOR");
      }
      List<List<KgType>> groupByParamNumList =
          this.udfParamQueryMap.computeIfAbsent(
              udfMeta.getParamTypeList().size(), k -> new ArrayList<>());
      groupByParamNumList.add(udfMeta.getParamTypeList());
    }
    for (List<List<KgType>> groupByParamNumList : this.udfParamQueryMap.values()) {
      groupByParamNumList.sort((o1, o2) -> UdfUtils.compareParamList(o2, o1));
    }
    udfType = funcUdfType;
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
   * Getter method for property <tt>udfType</tt>.
   *
   * @return property value of name
   */
  public UdfOperatorTypeEnum getUdfType() {
    return udfType;
  }

  /**
   * invoke udf method
   *
   * @param args
   * @return
   */
  public Object invoke(Object... args) {
    IUdfMeta udfMeta = null;
    List<KgType> inputParamTypeList = getParamTypeList(args);
    List<List<KgType>> groupByParamNumList = this.udfParamQueryMap.get(inputParamTypeList.size());
    if (null != groupByParamNumList) {
      for (List<KgType> udfParamTypeList : groupByParamNumList) {
        if (UdfUtils.isParameterCompatible(udfParamTypeList, inputParamTypeList)) {
          udfMeta = this.udfMetaMap.get(UdfUtils.getTypeKeyString(udfParamTypeList, "(", ")"));
          break;
        }
      }
    }
    if (null == udfMeta) {
      throw new UnsupportedOperationException(
          "unsupported udf " + this.name + ", args=" + Arrays.toString(args), null);
    }
    return udfMeta.invoke(args);
  }

  private List<KgType> getParamTypeList(Object... args) {
    List<KgType> kgTypeList = new ArrayList<>(args.length);
    for (Object arg : args) {
      if (null == arg) {
        kgTypeList.add(KTObject$.MODULE$);
        continue;
      }
      String className = arg.getClass().getName();
      if ("java.util.ArrayList".equals(className)) {
        ArrayList list = (ArrayList) arg;
        String memberType;
        if ((list.isEmpty() || list.get(0) == null)) {
          memberType = null;
        } else {
          memberType = list.get(0).getClass().getName();
          if (!memberType.startsWith("java.lang.")) {
            memberType = "java.lang.Object";
          }
        }
        className = "java.util.List<" + memberType + ">";
      }
      kgTypeList.add(Utils.javaType2KgType(className));
    }
    return kgTypeList;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RuntimeUdfMeta{");
    for (IUdfMeta udfMeta : udfMetaMap.values()) {
      sb.append(udfMeta.toString()).append(";");
    }
    sb.append("}");
    return sb.toString();
  }
}
