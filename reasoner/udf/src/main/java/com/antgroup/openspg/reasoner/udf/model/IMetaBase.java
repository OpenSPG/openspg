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
import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface IMetaBase extends Serializable {
  /**
   * Getter method for property <tt>name</tt>.
   *
   * @return
   */
  String getName();

  /**
   * Getter method for property <tt>compatibleName</tt>
   *
   * @return
   */
  Set<String> getCompatibleNames();

  /**
   * Getter method for property <tt>paramTypeList</tt>.
   *
   * @return
   */
  List<KgType> getParamTypeList();
}
