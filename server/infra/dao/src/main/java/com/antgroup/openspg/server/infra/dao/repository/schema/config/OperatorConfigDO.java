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

package com.antgroup.openspg.server.infra.dao.repository.schema.config;

import lombok.Data;

@Data
public class OperatorConfigDO {

  private static final long serialVersionUID = -8961467524485610108L;

  /** The operator id. */
  private Long id;

  /** The operator name */
  private String name;

  /** The operator version. */
  private Integer version;

  /** The operator url. */
  private String operatorUrl;

  /** The operator description. */
  private String description;

  /** If the operator is very good. */
  private Integer iGood;

  /** The basic id of operator, multi version operators have same basic id. */
  private Long overviewId;

  /** Operator type */
  private String operatorType;

  /** The parameters of operator */
  private String params;

  /** The address of operator. */
  private String jarAddress;

  /** The script of operator, only is valid for python operator. */
  private String script;

  /** The language of develop operator by. */
  private String lang;

  /** The main class of operator jar to execute. */
  private String mainClass;
}
