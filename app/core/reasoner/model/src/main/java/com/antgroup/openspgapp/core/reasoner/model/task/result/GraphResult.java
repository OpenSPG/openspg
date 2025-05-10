package com.antgroup.openspgapp.core.reasoner.model.task.result;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.google.common.collect.Lists;
import java.util.List;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/task/result/GraphResult.class */
public class GraphResult extends BaseModel {
  private static final long serialVersionUID = 6567121968824686072L;
  private List<Node> nodeList = Lists.newArrayList();
  private List<Edge> edgeList = Lists.newArrayList();
  private List<Path> pathList;

  public List<Node> getNodeList() {
    return this.nodeList;
  }

  public void setNodeList(List<Node> nodeList) {
    this.nodeList = nodeList;
  }

  public List<Edge> getEdgeList() {
    return this.edgeList;
  }

  public void setEdgeList(List<Edge> edgeList) {
    this.edgeList = edgeList;
  }

  public List<Path> getPathList() {
    return this.pathList;
  }

  public void setPathList(List<Path> pathList) {
    this.pathList = pathList;
  }
}
