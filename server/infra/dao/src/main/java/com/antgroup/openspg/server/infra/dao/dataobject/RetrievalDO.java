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

package com.antgroup.openspg.server.infra.dao.dataobject;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetrievalDO {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String updateUser;
  private String createUser;
  private String type;
  private String status;
  private String isDefault;
  private String name;
  private String chineseName;
  private String schemaDesc;
  private String scenariosDesc;
  private String costDesc;
  private String methodDesc;
  private String extractorDesc;
  private String retrieverDesc;
  private String modulePath;
  private String className;
  private String method;
  private String extension;
  private String config;
}
