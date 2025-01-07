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
package com.antgroup.openspg.server.common.service.datasource;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.datasource.Column;
import com.antgroup.openspg.server.common.model.datasource.DataSource;
import com.antgroup.openspg.server.common.model.datasource.DataSourceQuery;
import java.util.List;

public interface DataSourceService {

  /** insert Job */
  Long insert(DataSource record);

  /** delete By Id */
  int deleteById(Long id);

  /** update Job */
  Long update(DataSource record);

  /** get By id */
  DataSource getById(Long id);

  /** query By Condition */
  Paged<DataSource> query(DataSourceQuery record);

  /** get DataSource Group By Type */
  List<DataSource> getGroupByType(DataSourceQuery record);

  List<String> getAllDatabase(Long id);

  List<String> getAllTable(Long id, String database, String keyword);

  List<Column> getTableDetail(Long id, String database, String table);

  Boolean testConnect(DataSource record);

  List<Column> getDataSourceType(String category);
}
