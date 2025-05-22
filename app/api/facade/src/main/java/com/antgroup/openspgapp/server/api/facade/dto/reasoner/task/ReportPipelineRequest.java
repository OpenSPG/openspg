package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspgapp.core.builder.model.CaPipeline;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/ReportPipelineRequest.class */
public class ReportPipelineRequest extends BaseRequest {
  private static final long serialVersionUID = -3546610993385943286L;
  private Long taskId;
  private CaPipeline pipeline;
  private CaPipeline.Node node;

  public void setTaskId(final Long taskId) {
    this.taskId = taskId;
  }

  public void setPipeline(final CaPipeline pipeline) {
    this.pipeline = pipeline;
  }

  public void setNode(final CaPipeline.Node node) {
    this.node = node;
  }

  public Long getTaskId() {
    return this.taskId;
  }

  public CaPipeline getPipeline() {
    return this.pipeline;
  }

  public CaPipeline.Node getNode() {
    return this.node;
  }
}
