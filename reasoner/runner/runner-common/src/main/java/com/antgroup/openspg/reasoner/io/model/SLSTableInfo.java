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
package com.antgroup.openspg.reasoner.io.model;

import lombok.Data;

/**
 * @author kejian
 * @version SLSTableInfo.java, v 0.1 2024年03月05日 11:49 AM kejian
 */
@Data
public class SLSTableInfo extends AbstractTableInfo {
  /** sls project */
  private String project;

  /** sls endpoint */
  private String endpoint;

  /** sls logStore */
  private String logStore;

  /** sls accessId */
  private String accessId;

  /** sls accessKey */
  private String accessKey;

  /** akg Task dev id */
  private String taskId;
}
