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

package com.antgroup.openspg.reasoner.lube.logical.validate.semantic

import com.antgroup.openspg.reasoner.lube.block.Block
import com.antgroup.openspg.reasoner.lube.logical.planning.LogicalPlannerContext

trait Explain {
  /**
   * Rewrite [[Block]] tree TopDown
   * @param context
   * @return
   */
  def explain(implicit context: LogicalPlannerContext): PartialFunction[Block, Block]
}
