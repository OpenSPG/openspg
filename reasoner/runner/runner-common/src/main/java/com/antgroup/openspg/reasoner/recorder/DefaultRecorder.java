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
import com.antgroup.openspg.reasoner.recorder.action.SampleAction;
import com.antgroup.openspg.reasoner.recorder.action.SubAction;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DefaultRecorder implements IExecutionRecorder {
  private final Stack<SubAction> actionStack = new Stack<>();

  public DefaultRecorder() {}

  @Override
  public String toReadableString() {
    return actionStack.get(0).toString();
  }

  @Override
  public void entryRDG(String rdg) {
    SubAction nowAction = null;
    if (!actionStack.isEmpty()) {
      nowAction = actionStack.peek();
    }
    SubAction newAction = new SubAction(rdg);
    if (null != nowAction) {
      nowAction.getSubActionList().add(newAction);
    }
    actionStack.push(newAction);
  }

  @Override
  public void leaveRDG() {
    actionStack.pop();
  }

  @Override
  public void stageResult(String stage, long result) {
    SubAction nowAction = actionStack.peek();
    nowAction.getSubActionList().add(new SampleAction(stage, result));
  }

  @Override
  public Map<IVertexId, DebugInfoWithStartId> getCurStageDebugInfo() {
    SubAction nowAction = actionStack.peek();
    return nowAction.getRuleRuntimeInfo();
  }

  @Override
  public void stageResultWithDesc(String stage, long result, String finishDescribe) {
    SubAction nowAction = actionStack.peek();
    nowAction.getSubActionList().add(new SampleAction(stage, result, finishDescribe));
  }

  @Override
  public void stageResultWithDetail(
      String stage,
      long result,
      Map<String, List<IVertexId>> runtimeDetail,
      List<Rule> relateRules) {
    SubAction nowAction = actionStack.peek();
    nowAction.getSubActionList().add(new SampleAction(stage, result, runtimeDetail, relateRules));
  }

  @Override
  public void stageResultWithDescAndDetail(
      String stage,
      long result,
      String finishDescribe,
      Map<String, List<IVertexId>> runtimeDetail,
      List<Rule> relateRules) {
    SubAction nowAction = actionStack.peek();
    nowAction
        .getSubActionList()
        .add(new SampleAction(stage, result, finishDescribe, runtimeDetail, relateRules));
  }
}
