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

package com.antgroup.openspg.reasoner.udf;

import com.antgroup.openspg.reasoner.common.types.KgType;
import com.antgroup.openspg.reasoner.udf.model.IUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.RuntimeUdfMeta;
import com.antgroup.openspg.reasoner.udf.model.UdafMeta;
import com.antgroup.openspg.reasoner.udf.model.UdfParameterTypeHint;
import com.antgroup.openspg.reasoner.udf.model.UdtfMeta;
import java.util.List;
import java.util.Map;

public interface UdfMng {
  /**
   * Query UDF meta information, which can be used to determine whether UDF exists KgType is
   * compatible with java Object type
   *
   * <p>Query is inefficient, please cache for use
   *
   * @param name
   * @param paramTypeList
   * @return
   */
  IUdfMeta getUdfMeta(String name, List<KgType> paramTypeList);

  /**
   * use for querying the parameter list of the UDF can only be obtained on runtime
   *
   * @param name
   * @return
   */
  RuntimeUdfMeta getRuntimeUdfMeta(String name);

  /**
   * Query UDAF meta information
   *
   * <p>Query is inefficient, please cache for use
   *
   * @param name
   * @param rowDataType
   * @return
   */
  UdafMeta getUdafMeta(String name, KgType rowDataType);

  /**
   * Query UDTF meta information
   *
   * @param name
   * @param rowDataTypes
   * @return
   */
  UdtfMeta getUdtfMeta(String name, List<KgType> rowDataTypes);

  /**
   * get all udf meta information, for udf registration
   *
   * @return
   */
  List<IUdfMeta> getAllUdfMeta();

  /**
   * get all runtime udf meta, for udf registration on QLExpress
   *
   * @return
   */
  List<RuntimeUdfMeta> getAllRuntimeUdfMeta();

  /**
   * get all udaf for registration
   *
   * @return
   */
  List<UdafMeta> getAllUdafMeta();

  /**
   * get all udtf meta for registration
   *
   * @return
   */
  List<UdtfMeta> getAllUdtfMeta();

  /**
   * get udf type list hint
   *
   * <p>KTObject means support all kind types
   */
  Map<String, Map<String, UdfParameterTypeHint>> getUdfTypeHint();
}
