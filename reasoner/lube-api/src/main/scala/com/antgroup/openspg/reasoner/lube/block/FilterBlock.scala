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

package com.antgroup.openspg.reasoner.lube.block

import com.antgroup.openspg.reasoner.lube.common.rule.Rule

/**
 * a filter block，to filter data that meets the rules
 *
 * @param dependencies
 * @param rules
 * @param graph
 */
final case class FilterBlock(dependencies: List[Block], rules: Rule)
    extends BasicBlock[Binds](BlockType("filter")) {

  override def binds: Binds = {
    dependencies.head.binds
  }

}
