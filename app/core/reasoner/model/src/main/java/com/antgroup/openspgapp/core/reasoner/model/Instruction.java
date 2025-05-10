package com.antgroup.openspgapp.core.reasoner.model;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/Instruction.class */
public class Instruction {
  private Long sessionId;
  private Long userId;
  private Long projectId;
  private String document;
  private String instruction;

  public void setSessionId(final Long sessionId) {
    this.sessionId = sessionId;
  }

  public void setUserId(final Long userId) {
    this.userId = userId;
  }

  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

  public void setDocument(final String document) {
    this.document = document;
  }

  public void setInstruction(final String instruction) {
    this.instruction = instruction;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Instruction)) {
      return false;
    }
    Instruction other = (Instruction) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$sessionId = getSessionId();
    Object other$sessionId = other.getSessionId();
    if (this$sessionId == null) {
      if (other$sessionId != null) {
        return false;
      }
    } else if (!this$sessionId.equals(other$sessionId)) {
      return false;
    }
    Object this$userId = getUserId();
    Object other$userId = other.getUserId();
    if (this$userId == null) {
      if (other$userId != null) {
        return false;
      }
    } else if (!this$userId.equals(other$userId)) {
      return false;
    }
    Object this$projectId = getProjectId();
    Object other$projectId = other.getProjectId();
    if (this$projectId == null) {
      if (other$projectId != null) {
        return false;
      }
    } else if (!this$projectId.equals(other$projectId)) {
      return false;
    }
    Object this$document = getDocument();
    Object other$document = other.getDocument();
    if (this$document == null) {
      if (other$document != null) {
        return false;
      }
    } else if (!this$document.equals(other$document)) {
      return false;
    }
    Object this$instruction = getInstruction();
    Object other$instruction = other.getInstruction();
    return this$instruction == null
        ? other$instruction == null
        : this$instruction.equals(other$instruction);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Instruction;
  }

  public int hashCode() {
    Object $sessionId = getSessionId();
    int result = (1 * 59) + ($sessionId == null ? 43 : $sessionId.hashCode());
    Object $userId = getUserId();
    int result2 = (result * 59) + ($userId == null ? 43 : $userId.hashCode());
    Object $projectId = getProjectId();
    int result3 = (result2 * 59) + ($projectId == null ? 43 : $projectId.hashCode());
    Object $document = getDocument();
    int result4 = (result3 * 59) + ($document == null ? 43 : $document.hashCode());
    Object $instruction = getInstruction();
    return (result4 * 59) + ($instruction == null ? 43 : $instruction.hashCode());
  }

  public String toString() {
    return "Instruction(sessionId="
        + getSessionId()
        + ", userId="
        + getUserId()
        + ", projectId="
        + getProjectId()
        + ", document="
        + getDocument()
        + ", instruction="
        + getInstruction()
        + ")";
  }

  public Long getSessionId() {
    return this.sessionId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public String getDocument() {
    return this.document;
  }

  public String getInstruction() {
    return this.instruction;
  }
}
