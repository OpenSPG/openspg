package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.alibaba.fastjson.JSONObject;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/CompletionContent.class */
public class CompletionContent {
  private String answer;
  private List<JSONObject> reference;
  private String think;
  private JSONObject metrics;
  private List<JSONObject> subgraph;

  public void setAnswer(final String answer) {
    this.answer = answer;
  }

  public void setReference(final List<JSONObject> reference) {
    this.reference = reference;
  }

  public void setThink(final String think) {
    this.think = think;
  }

  public void setMetrics(final JSONObject metrics) {
    this.metrics = metrics;
  }

  public void setSubgraph(final List<JSONObject> subgraph) {
    this.subgraph = subgraph;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof CompletionContent)) {
      return false;
    }
    CompletionContent other = (CompletionContent) o;
    if (!other.canEqual(this)) {
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
    Object this$reference = getReference();
    Object other$reference = other.getReference();
    if (this$reference == null) {
      if (other$reference != null) {
        return false;
      }
    } else if (!this$reference.equals(other$reference)) {
      return false;
    }
    Object this$think = getThink();
    Object other$think = other.getThink();
    if (this$think == null) {
      if (other$think != null) {
        return false;
      }
    } else if (!this$think.equals(other$think)) {
      return false;
    }
    Object this$metrics = getMetrics();
    Object other$metrics = other.getMetrics();
    if (this$metrics == null) {
      if (other$metrics != null) {
        return false;
      }
    } else if (!this$metrics.equals(other$metrics)) {
      return false;
    }
    Object this$subgraph = getSubgraph();
    Object other$subgraph = other.getSubgraph();
    return this$subgraph == null ? other$subgraph == null : this$subgraph.equals(other$subgraph);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof CompletionContent;
  }

  public int hashCode() {
    Object $answer = getAnswer();
    int result = (1 * 59) + ($answer == null ? 43 : $answer.hashCode());
    Object $reference = getReference();
    int result2 = (result * 59) + ($reference == null ? 43 : $reference.hashCode());
    Object $think = getThink();
    int result3 = (result2 * 59) + ($think == null ? 43 : $think.hashCode());
    Object $metrics = getMetrics();
    int result4 = (result3 * 59) + ($metrics == null ? 43 : $metrics.hashCode());
    Object $subgraph = getSubgraph();
    return (result4 * 59) + ($subgraph == null ? 43 : $subgraph.hashCode());
  }

  public String toString() {
    return "CompletionContent(answer="
        + getAnswer()
        + ", reference="
        + getReference()
        + ", think="
        + getThink()
        + ", metrics="
        + getMetrics()
        + ", subgraph="
        + getSubgraph()
        + ")";
  }

  public String getAnswer() {
    return this.answer;
  }

  public List<JSONObject> getReference() {
    return this.reference;
  }

  public String getThink() {
    return this.think;
  }

  public JSONObject getMetrics() {
    return this.metrics;
  }

  public List<JSONObject> getSubgraph() {
    return this.subgraph;
  }
}
