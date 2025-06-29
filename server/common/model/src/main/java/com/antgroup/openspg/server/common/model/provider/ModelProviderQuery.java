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
package com.antgroup.openspg.server.common.model.provider;

import java.util.Date;
import lombok.Data;

@Data
public class ModelProviderQuery {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String updateUser;
  private String createUser;
  private String name;
  private String provider;
  private String status;
  private String pageMode;
  private String modelType;

  private String sort;

  private String order;
}
