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
package com.antgroup.openspg.server.common.service.builder;

import com.antgroup.openspg.server.api.facade.Paged;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspg.server.common.model.bulider.BuilderJobQuery;

public interface BuilderJobService {

  /** insert Job */
  Long insert(BuilderJob record);

  /** delete By Id */
  int deleteById(Long id);

  /** update Job */
  Long update(BuilderJob record);

  /** get By id */
  BuilderJob getById(Long id);

  /** query By Condition */
  Paged<BuilderJob> query(BuilderJobQuery record);
}
