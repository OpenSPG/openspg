package com.antgroup.openspgapp.common.model.data.dto;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-common-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/model/data/dto/ChatStreamData.class */
public class ChatStreamData {
  private Long id;
  private String answer;
  private String think;
  private String thinkCost;
  private String reasoner;
  private List<JSONObject> subgraph = new ArrayList();
  private List<JSONObject> reference = new ArrayList();

  public void setId(final Long id) {
    this.id = id;
  }

  public void setAnswer(final String answer) {
    this.answer = answer;
  }

  public void setThink(final String think) {
    this.think = think;
  }

  public void setThinkCost(final String thinkCost) {
    this.thinkCost = thinkCost;
  }

  public void setReasoner(final String reasoner) {
    this.reasoner = reasoner;
  }

  public void setSubgraph(final List<JSONObject> subgraph) {
    this.subgraph = subgraph;
  }

  public void setReference(final List<JSONObject> reference) {
    this.reference = reference;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ChatStreamData)) {
      return false;
    }
    ChatStreamData other = (ChatStreamData) o;
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
    Object this$answer = getAnswer();
    Object other$answer = other.getAnswer();
    if (this$answer == null) {
      if (other$answer != null) {
        return false;
      }
    } else if (!this$answer.equals(other$answer)) {
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
    Object this$thinkCost = getThinkCost();
    Object other$thinkCost = other.getThinkCost();
    if (this$thinkCost == null) {
      if (other$thinkCost != null) {
        return false;
      }
    } else if (!this$thinkCost.equals(other$thinkCost)) {
      return false;
    }
    Object this$reasoner = getReasoner();
    Object other$reasoner = other.getReasoner();
    if (this$reasoner == null) {
      if (other$reasoner != null) {
        return false;
      }
    } else if (!this$reasoner.equals(other$reasoner)) {
      return false;
    }
    Object this$subgraph = getSubgraph();
    Object other$subgraph = other.getSubgraph();
    if (this$subgraph == null) {
      if (other$subgraph != null) {
        return false;
      }
    } else if (!this$subgraph.equals(other$subgraph)) {
      return false;
    }
    Object this$reference = getReference();
    Object other$reference = other.getReference();
    return this$reference == null
        ? other$reference == null
        : this$reference.equals(other$reference);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ChatStreamData;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $answer = getAnswer();
    int result2 = (result * 59) + ($answer == null ? 43 : $answer.hashCode());
    Object $think = getThink();
    int result3 = (result2 * 59) + ($think == null ? 43 : $think.hashCode());
    Object $thinkCost = getThinkCost();
    int result4 = (result3 * 59) + ($thinkCost == null ? 43 : $thinkCost.hashCode());
    Object $reasoner = getReasoner();
    int result5 = (result4 * 59) + ($reasoner == null ? 43 : $reasoner.hashCode());
    Object $subgraph = getSubgraph();
    int result6 = (result5 * 59) + ($subgraph == null ? 43 : $subgraph.hashCode());
    Object $reference = getReference();
    return (result6 * 59) + ($reference == null ? 43 : $reference.hashCode());
  }

  public String toString() {
    return "ChatStreamData(id="
        + getId()
        + ", answer="
        + getAnswer()
        + ", think="
        + getThink()
        + ", thinkCost="
        + getThinkCost()
        + ", reasoner="
        + getReasoner()
        + ", subgraph="
        + getSubgraph()
        + ", reference="
        + getReference()
        + ")";
  }

  public Long getId() {
    return this.id;
  }

  public String getAnswer() {
    return this.answer;
  }

  public String getThink() {
    return this.think;
  }

  public String getThinkCost() {
    return this.thinkCost;
  }

  public String getReasoner() {
    return this.reasoner;
  }

  public List<JSONObject> getSubgraph() {
    return this.subgraph;
  }

  public List<JSONObject> getReference() {
    return this.reference;
  }

  public ChatStreamData() {}

  public ChatStreamData(Long id) {
    this.id = id;
  }
}
