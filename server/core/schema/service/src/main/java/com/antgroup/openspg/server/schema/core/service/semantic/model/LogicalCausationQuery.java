/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.server.schema.core.service.semantic.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/** Query object of concept semantic. */
@Getter
@Setter
@Accessors(chain = true)
public class LogicalCausationQuery {

  /** The list of subject type name */
  private List<String> subjectTypeNames;

  /** The subject name. */
  private String subjectName;

  /** The list of object type name. */
  private List<String> objectTypeNames;

  /** The object name. */
  private String objectName;

  /** The predicate name. */
  private String predicateName;
}
