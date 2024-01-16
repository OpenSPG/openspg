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

package com.antgroup.openspg.reasoner.lube.logical.planning

import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.parser.ParserInterface

/**
 * The context during the execution of physical planner
 *
 * @param catalog the catalog [[Catalog]]
 * @param parser parser [[ParserInterface]]
 * @param params query parameters for KGReasoner execution.
 */
case class LogicalPlannerContext(
    catalog: Catalog,
    parser: ParserInterface,
    params: Map[String, Object]) {
  def addParam(key: String, value: Object): LogicalPlannerContext = {
    val newParams = params + ((key, value))
    this.copy(params = newParams)
  }
}
