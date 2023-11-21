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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config;

public class Constants {

  /** schema版本的分隔符 */
  public static final String SCHEMA_VERSION_SPLIT_KEY = "$V";

  /** schema命名空间与名称的分隔符 */
  public static final String SCHEMA_NAMESPACE_NAME_SPLIT_KEY = ".";

  /** 继承路径分隔符 */
  public static final String INHERIT_PATH_SEP = ",";
}
