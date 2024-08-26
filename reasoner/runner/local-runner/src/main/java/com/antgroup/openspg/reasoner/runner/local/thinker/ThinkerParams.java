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

package com.antgroup.openspg.reasoner.runner.local.thinker;

import com.antgroup.openspg.reasoner.catalog.impl.KgSchemaConnectionInfo;
import com.antgroup.openspg.reasoner.thinker.logic.graph.Triple;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class ThinkerParams implements Serializable {
  private KgSchemaConnectionInfo connInfo = null;
  private Long projectId;
  private Triple triple;
  private Map<String, Object> params;

  /** Choose between graphLoadClass and graphState, or specify a class name */
  private String graphLoadClass = null;

  /** User specified the name of graphstate */
  private String graphStateClassName = null;

  private String graphStateInitString = null;

  private String mode = "spo";
}
