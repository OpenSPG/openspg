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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author peilong.zpl
 * @version $Id: SubAction.java, v 0.1 2024-04-08 15:32 peilong.zpl Exp $$
 */
public class SubAction extends AbstractAction {
  private final String describe;
  private final List<AbstractAction> subActionList = new ArrayList<>();

  public SubAction(String describe) {
    super(System.currentTimeMillis());
    this.describe = describe;
  }

  public Map<IVertexId, DebugInfoWithStartId> getRuleRuntimeInfo() {
    Map<IVertexId, DebugInfoWithStartId> vertexIdMap = new HashMap<>();
    for (AbstractAction action : subActionList) {
      Map<IVertexId, DebugInfoWithStartId> startIds = action.getRuleRuntimeInfo();
      for (IVertexId d : startIds.keySet()) {
        if (vertexIdMap.containsKey(d)) {
          vertexIdMap.get(d).mergeDebugInfo(startIds.get(d));
        } else {
          vertexIdMap.put(d, startIds.get(d));
        }
      }
    }
    return vertexIdMap;
  }

  public String getDescribe() {
    return describe;
  }

  public List<AbstractAction> getSubActionList() {
    return subActionList;
  }

  @Override
  public String toString() {
    List<String> printLineList = new ArrayList<>();
    getPrettyLines(printLineList, "", this, true);

    StringBuilder sb = new StringBuilder();
    for (String line : printLineList) {
      sb.append(line).append("\n");
    }
    return sb.toString();
  }

  private void getPrettyLines(
      List<String> printLines, String prefix, AbstractAction action, boolean last) {
    if (action instanceof SampleAction) {
      if (last) {
        printLines.add(prefix + "└─" + action);
      } else {
        printLines.add(prefix + "├─" + action);
      }
    } else {
      SubAction subAction = (SubAction) action;
      String finishDescribe = "";
      if (!subAction.subActionList.isEmpty()) {
        AbstractAction finish = subAction.subActionList.get(subAction.subActionList.size() - 1);
        if (finish instanceof SampleAction) {
          SampleAction finishSampleAction = (SampleAction) finish;
          finishDescribe = finishSampleAction.getFinishDescribe();
        }
      }
      getPrettyLines(
          printLines, prefix, new SampleAction(finishDescribe, null, subAction.time), last);

      String newPrefix;
      if (last) {
        newPrefix = prefix + "    ";
      } else {
        newPrefix = prefix + "│   ";
      }
      for (int i = 0; i < subAction.subActionList.size(); ++i) {
        boolean isLast = i + 1 == subAction.subActionList.size();
        AbstractAction aa = subAction.subActionList.get(i);
        getPrettyLines(printLines, newPrefix, aa, isLast);
      }
    }
  }
}
