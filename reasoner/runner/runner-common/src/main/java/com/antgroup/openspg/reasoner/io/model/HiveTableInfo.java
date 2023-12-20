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

package com.antgroup.openspg.reasoner.io.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HiveTableInfo extends AbstractTableInfo {
  private String hadoopUserName = null;

  // jdbc info
  private String jdbcUrl;
  private String jdbcUser;
  private String jdbcPasswd;

  // output path info
  private String outputParquetPath; // can be null
  private String coreSiteXml;
  private String hdfsSiteXml;

  @Override
  public int hashCode() {
    Object[] item = new Object[] {table, project, jdbcUrl, getPartitionString()};
    return Arrays.hashCode(item);
  }
}
