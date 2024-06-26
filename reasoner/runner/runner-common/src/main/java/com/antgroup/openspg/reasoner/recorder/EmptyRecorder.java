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

package com.antgroup.openspg.reasoner.recorder;

import com.antgroup.openspg.reasoner.common.graph.vertex.IVertexId;
import com.antgroup.openspg.reasoner.lube.common.rule.Rule;
import com.antgroup.openspg.reasoner.recorder.action.DebugInfoWithStartId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmptyRecorder implements IExecutionRecorder {
  @Override
  public String toReadableString() {
    return "";
  }

  @Override
  public void entryRDG(String rdg) {}

  @Override
  public void leaveRDG() {}

  @Override
  public void stageResult(String stage, long result) {}

  @Override
  public Map<IVertexId, DebugInfoWithStartId> getCurStageDebugInfo() {
    return new HashMap<>();
  }

  @Override
  public void stageResultWithDesc(String stage, long result, String finishDescribe) {}

  @Override
  public void stageResultWithDetail(
      String stage, long result, Map<String, Object> runtimeDetail, List<Rule> relateRules) {}

  @Override
  public void stageResultWithDescAndDetail(
      String stage,
      long result,
      String finishDescribe,
      Map<String, Object> runtimeDetail,
      List<Rule> relateRules) {}
}
