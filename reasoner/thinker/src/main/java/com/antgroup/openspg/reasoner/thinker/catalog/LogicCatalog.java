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

package com.antgroup.openspg.reasoner.thinker.catalog;

import com.antgroup.openspg.reasoner.lube.catalog.Catalog;
import com.antgroup.openspg.reasoner.thinker.logic.LogicNetwork;

public abstract class LogicCatalog extends Catalog {
  private LogicNetwork logicNetwork;

  public void init() {
    super.init();
    logicNetwork = loadLogicNetwork();
  }

  public abstract LogicNetwork loadLogicNetwork();

  public LogicNetwork getLogicNetwork() {
    return logicNetwork;
  }
}
