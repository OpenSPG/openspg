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

package com.antgroup.openspg.server.common.service.project;

import com.antgroup.openspg.server.common.model.project.Project;

/**
 * The domain interface of the project model provides management methods for adding, modifying,
 * querying, and deleting projects.
 */
public interface ProjectService {

  /**
   * Query project information based on project ID.
   *
   * @param projectId the unique id of project
   * @return project information
   */
  Project queryById(Long projectId);
}
