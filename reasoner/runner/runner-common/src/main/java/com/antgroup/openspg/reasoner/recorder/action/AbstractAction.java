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
package com.antgroup.openspg.reasoner.recorder.action;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import java.util.Map;

/**
 * @author peilong.zpl
 * @version $Id: AbstractAction.java, v 0.1 2024-04-08 15:31 peilong.zpl Exp $$
 */
public abstract class AbstractAction {
  protected final long time;

  protected AbstractAction(long time) {
    this.time = time;
  }

  public abstract Map<IVertexId, DebugInfoWithStartId> getRuleRuntimeInfo();
}
