package com.antgroup.openspgapp.core.reasoner.model;

import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.google.common.collect.Lists;
import java.util.List;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/SubGraph.class */
public class SubGraph {
  private List<Node> resultNodes = Lists.newArrayList();
  private List<Edge> resultEdges = Lists.newArrayList();

  public void setResultNodes(final List<Node> resultNodes) {
    this.resultNodes = resultNodes;
  }

  public void setResultEdges(final List<Edge> resultEdges) {
    this.resultEdges = resultEdges;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SubGraph)) {
      return false;
    }
    SubGraph other = (SubGraph) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$resultNodes = getResultNodes();
    Object other$resultNodes = other.getResultNodes();
    if (this$resultNodes == null) {
      if (other$resultNodes != null) {
        return false;
      }
    } else if (!this$resultNodes.equals(other$resultNodes)) {
      return false;
    }
    Object this$resultEdges = getResultEdges();
    Object other$resultEdges = other.getResultEdges();
    return this$resultEdges == null
        ? other$resultEdges == null
        : this$resultEdges.equals(other$resultEdges);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof SubGraph;
  }

  public int hashCode() {
    Object $resultNodes = getResultNodes();
    int result = (1 * 59) + ($resultNodes == null ? 43 : $resultNodes.hashCode());
    Object $resultEdges = getResultEdges();
    return (result * 59) + ($resultEdges == null ? 43 : $resultEdges.hashCode());
  }

  public String toString() {
    return "SubGraph(resultNodes=" + getResultNodes() + ", resultEdges=" + getResultEdges() + ")";
  }

  public List<Node> getResultNodes() {
    return this.resultNodes;
  }

  public List<Edge> getResultEdges() {
    return this.resultEdges;
  }
}
