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
public class SchedulerTaskDO {

  /** primary key */
  private Long id;

  /** Create time */
  private Date gmtCreate;

  /** Modified time */
  private Date gmtModified;

  /** type */
  private String type;

  /** title */
  private String title;

  /** status */
  private String status;

  /** project id */
  private Long projectId;

  /** SchedulerJobDO Id */
  private Long jobId;

  /** instance id */
  private Long instanceId;

  /** execute Num */
  private Integer executeNum;

  /** execute begin Time */
  private Date beginTime;

  /** execute finish Time */
  private Date finishTime;

  /** estimate Finish Time */
  private Date estimateFinishTime;

  /** traceLog */
  private String traceLog;

  /** lock Time */
  private Date lockTime;

  /** resource */
  private String resource;

  /** input */
  private String input;

  /** output */
  private String output;

  /** node id */
  private String nodeId;

  /** extension，JSON */
  private String extension;
}
