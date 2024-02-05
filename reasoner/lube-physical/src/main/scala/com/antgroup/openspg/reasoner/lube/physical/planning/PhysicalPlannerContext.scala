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

package com.antgroup.openspg.reasoner.lube.physical.planning

import com.antgroup.openspg.reasoner.lube.catalog.Catalog
import com.antgroup.openspg.reasoner.lube.physical.GraphSession
import com.antgroup.openspg.reasoner.lube.physical.rdg.RDG

/**
 * The context during the execution of physical planner
 *
 * @param catalog the catalog [[Catalog]]
 * @param workingRDG the working rdg [[RDG]]
 * @param params query parameters for KGReasoner execution.
 * @tparam T
 */
case class PhysicalPlannerContext[T <: RDG[T]](
    catalog: Catalog,
    graphSession: GraphSession[T],
    params: Map[String, Object])
