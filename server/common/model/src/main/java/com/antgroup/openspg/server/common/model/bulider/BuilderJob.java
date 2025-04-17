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
package com.antgroup.openspg.server.common.model.bulider;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuilderJob extends BaseModel {

  private static final long serialVersionUID = 8873542124566103571L;

  private Long id;
  private Long projectId;
  private Date gmtCreate;
  private Date gmtModified;
  private String modifyUser;
  private String createUser;
  private Long taskId;
  private String jobName;
  private Long chunkNum;
  private String fileUrl;
  private String status;
  private String dataSourceType;
  private String type;
  private String extension;
  private String version;
  private String cron;
  private String pipeline;
  private String computingConf;
  private String lifeCycle;
  private String action;
  private String dependence;
}
