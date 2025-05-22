package com.antgroup.openspgapp.core.builder.model;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspg.server.common.model.job.SubGraph;
import com.google.common.collect.Lists;
import java.util.List;

/* loaded from: core-builder-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/builder/model/CaPipeline.class */
public class CaPipeline extends BaseModel {
  private static final long serialVersionUID = 6779235839815592435L;
  private List<Node> nodes = Lists.newArrayList();
  private List<Edge> edges = Lists.newArrayList();
  private String type;

  public void setNodes(final List<Node> nodes) {
    this.nodes = nodes;
  }

  public void setEdges(final List<Edge> edges) {
    this.edges = edges;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CaPipeline)) {
      return false;
    }
    CaPipeline other = (CaPipeline) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$nodes = getNodes();
    Object other$nodes = other.getNodes();
    if (this$nodes == null) {
      if (other$nodes != null) {
        return false;
      }
    } else if (!this$nodes.equals(other$nodes)) {
      return false;
    }
    Object this$edges = getEdges();
    Object other$edges = other.getEdges();
    if (this$edges == null) {
      if (other$edges != null) {
        return false;
      }
    } else if (!this$edges.equals(other$edges)) {
      return false;
    }
    Object this$type = getType();
    Object other$type = other.getType();
    return this$type == null ? other$type == null : this$type.equals(other$type);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof CaPipeline;
  }

  public int hashCode() {
    Object $nodes = getNodes();
    int result = (1 * 59) + ($nodes == null ? 43 : $nodes.hashCode());
    Object $edges = getEdges();
    int result2 = (result * 59) + ($edges == null ? 43 : $edges.hashCode());
    Object $type = getType();
    return (result2 * 59) + ($type == null ? 43 : $type.hashCode());
  }

  public String toString() {
    return "CaPipeline(nodes=" + getNodes() + ", edges=" + getEdges() + ", type=" + getType() + ")";
  }

  public List<Node> getNodes() {
    return this.nodes;
  }

  public List<Edge> getEdges() {
    return this.edges;
  }

  public String getType() {
    return this.type;
  }

  /* loaded from: core-builder-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/builder/model/CaPipeline$Edge.class */
  public static class Edge extends BaseModel {
    private String from;
    private String to;

    public void setFrom(final String from) {
      this.from = from;
    }

    public void setTo(final String to) {
      this.to = to;
    }

    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof Edge)) {
        return false;
      }
      Edge other = (Edge) o;
      if (!other.canEqual(this)) {
        return false;
      }
      Object this$from = getFrom();
      Object other$from = other.getFrom();
      if (this$from == null) {
        if (other$from != null) {
          return false;
        }
      } else if (!this$from.equals(other$from)) {
        return false;
      }
      Object this$to = getTo();
      Object other$to = other.getTo();
      return this$to == null ? other$to == null : this$to.equals(other$to);
    }

    protected boolean canEqual(final Object other) {
      return other instanceof Edge;
    }

    public int hashCode() {
      Object $from = getFrom();
      int result = (1 * 59) + ($from == null ? 43 : $from.hashCode());
      Object $to = getTo();
      return (result * 59) + ($to == null ? 43 : $to.hashCode());
    }

    public String toString() {
      return "CaPipeline.Edge(from=" + getFrom() + ", to=" + getTo() + ")";
    }

    public String getFrom() {
      return this.from;
    }

    public String getTo() {
      return this.to;
    }
  }

  /* loaded from: core-builder-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/builder/model/CaPipeline$Node.class */
  public static class Node extends BaseModel {
    private String id;
    private String state;
    private String question;
    private String answer;
    private String logs;
    private String title;
    private List<SubGraph> subgraph;

    public void setId(final String id) {
      this.id = id;
    }

    public void setState(final String state) {
      this.state = state;
    }

    public void setQuestion(final String question) {
      this.question = question;
    }

    public void setAnswer(final String answer) {
      this.answer = answer;
    }

    public void setLogs(final String logs) {
      this.logs = logs;
    }

    public void setTitle(final String title) {
      this.title = title;
    }

    public void setSubgraph(final List<SubGraph> subgraph) {
      this.subgraph = subgraph;
    }

    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof Node)) {
        return false;
      }
      Node other = (Node) o;
      if (!other.canEqual(this)) {
        return false;
      }
      Object this$id = getId();
      Object other$id = other.getId();
      if (this$id == null) {
        if (other$id != null) {
          return false;
        }
      } else if (!this$id.equals(other$id)) {
        return false;
      }
      Object this$state = getState();
      Object other$state = other.getState();
      if (this$state == null) {
        if (other$state != null) {
          return false;
        }
      } else if (!this$state.equals(other$state)) {
        return false;
      }
      Object this$question = getQuestion();
      Object other$question = other.getQuestion();
      if (this$question == null) {
        if (other$question != null) {
          return false;
        }
      } else if (!this$question.equals(other$question)) {
        return false;
      }
      Object this$answer = getAnswer();
      Object other$answer = other.getAnswer();
      if (this$answer == null) {
        if (other$answer != null) {
          return false;
        }
      } else if (!this$answer.equals(other$answer)) {
        return false;
      }
      Object this$logs = getLogs();
      Object other$logs = other.getLogs();
      if (this$logs == null) {
        if (other$logs != null) {
          return false;
        }
      } else if (!this$logs.equals(other$logs)) {
        return false;
      }
      Object this$title = getTitle();
      Object other$title = other.getTitle();
      if (this$title == null) {
        if (other$title != null) {
          return false;
        }
      } else if (!this$title.equals(other$title)) {
        return false;
      }
      Object this$subgraph = getSubgraph();
      Object other$subgraph = other.getSubgraph();
      return this$subgraph == null ? other$subgraph == null : this$subgraph.equals(other$subgraph);
    }

    protected boolean canEqual(final Object other) {
      return other instanceof Node;
    }

    public int hashCode() {
      Object $id = getId();
      int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
      Object $state = getState();
      int result2 = (result * 59) + ($state == null ? 43 : $state.hashCode());
      Object $question = getQuestion();
      int result3 = (result2 * 59) + ($question == null ? 43 : $question.hashCode());
      Object $answer = getAnswer();
      int result4 = (result3 * 59) + ($answer == null ? 43 : $answer.hashCode());
      Object $logs = getLogs();
      int result5 = (result4 * 59) + ($logs == null ? 43 : $logs.hashCode());
      Object $title = getTitle();
      int result6 = (result5 * 59) + ($title == null ? 43 : $title.hashCode());
      Object $subgraph = getSubgraph();
      return (result6 * 59) + ($subgraph == null ? 43 : $subgraph.hashCode());
    }

    public String toString() {
      return "CaPipeline.Node(id="
          + getId()
          + ", state="
          + getState()
          + ", question="
          + getQuestion()
          + ", answer="
          + getAnswer()
          + ", logs="
          + getLogs()
          + ", title="
          + getTitle()
          + ", subgraph="
          + getSubgraph()
          + ")";
    }

    public String getId() {
      return this.id;
    }

    public String getState() {
      return this.state;
    }

    public String getQuestion() {
      return this.question;
    }

    public String getAnswer() {
      return this.answer;
    }

    public String getLogs() {
      return this.logs;
    }

    public String getTitle() {
      return this.title;
    }

    public List<SubGraph> getSubgraph() {
      return this.subgraph;
    }
  }

  public Node getNode(String nodeId) {
    for (Node node : this.nodes) {
      if (node.getId().equals(nodeId)) {
        return node;
      }
    }
    return null;
  }
}
