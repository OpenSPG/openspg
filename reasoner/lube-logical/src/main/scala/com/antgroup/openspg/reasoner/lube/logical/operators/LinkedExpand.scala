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

package com.antgroup.openspg.reasoner.lube.logical.operators

import com.antgroup.openspg.reasoner.lube.common.pattern.{EdgePattern, LinkedPatternConnection}
import com.antgroup.openspg.reasoner.lube.logical.{EdgeVar, SolvedModel, Var}

final case class LinkedExpand(
    in: LogicalOperator,
    edgePattern: EdgePattern[LinkedPatternConnection])
    extends StackingLogicalOperator {

  /**
   * the nodes, edges, attributes has been solved in currently
   *
   * @return
   */
  override def solved: SolvedModel = in.solved

  /**
   * the reference fields in current operator
   *
   * @return
   */
  override def refFields: List[Var] = List.apply(solved.getVar(edgePattern.edge.alias))

  /**
   * the output fields of current operator
   *
   * @return
   */
  override def fields: List[Var] = in.fields ++ refFields
}
