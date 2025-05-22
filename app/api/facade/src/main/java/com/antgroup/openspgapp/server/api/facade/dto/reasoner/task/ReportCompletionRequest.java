package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/ReportCompletionRequest.class */
public class ReportCompletionRequest extends BaseRequest {
  private static final long serialVersionUID = -2641851826210334233L;
  private Long taskId;
  private StatusEnum statusEnum;
  private CompletionContent content;

  public void setTaskId(final Long taskId) {
    this.taskId = taskId;
  }

  public void setStatusEnum(final StatusEnum statusEnum) {
    this.statusEnum = statusEnum;
  }

  public void setContent(final CompletionContent content) {
    this.content = content;
  }

  public Long getTaskId() {
    return this.taskId;
  }

  public StatusEnum getStatusEnum() {
    return this.statusEnum;
  }

  public CompletionContent getContent() {
    return this.content;
  }
}
