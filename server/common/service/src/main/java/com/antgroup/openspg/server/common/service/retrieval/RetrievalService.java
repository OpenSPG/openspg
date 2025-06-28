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
package com.antgroup.openspg.server.common.service.retrieval;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.retrieval.Retrieval;
import com.antgroup.openspg.server.common.model.retrieval.RetrievalQuery;
import java.util.List;

public interface RetrievalService {

  /** insert Retrieval */
  Long insert(Retrieval record);

  /** delete By Id */
  int deleteById(Long id);

  /** update Retrieval */
  Long update(Retrieval record);

  /** get By id */
  Retrieval getById(Long id);

  /** get By name */
  Retrieval getByName(String name);

  /** query By Condition */
  Paged<Retrieval> query(RetrievalQuery record);

  /** get project retrieval */
  List<Retrieval> getRetrievalByProjectId(Long projectId);
}
