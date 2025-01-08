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
public class DataSourceDO {

  private Long id;
  private Date gmtCreate;
  private Date gmtModified;
  private String createUser;
  private String updateUser;
  private String status;
  private String remark;
  private String type;
  private String dbName;
  private String dbUrl;
  private String dbUser;
  private String dbPassword;
  private String encrypt;
  private String dbDriverName;
  private String category;
  private String connectionInfo;
}
