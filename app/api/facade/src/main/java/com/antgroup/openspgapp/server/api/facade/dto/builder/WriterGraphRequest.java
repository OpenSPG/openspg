package com.antgroup.openspgapp.server.api.facade.dto.builder;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspgapp.core.reasoner.model.SubGraph;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/builder/WriterGraphRequest.class */
public class WriterGraphRequest extends BaseRequest {
  private static final long serialVersionUID = -5051318132737772511L;
  private SubGraph subGraph;
  private String operation;
  Long projectId;

  public WriterGraphRequest() {}

  public WriterGraphRequest(SubGraph subGraph, String operation, Long projectId) {
    this.subGraph = subGraph;
    this.operation = operation;
    this.projectId = projectId;
  }

  public SubGraph getSubGraph() {
    return this.subGraph;
  }

  public void setSubGraph(SubGraph subGraph) {
    this.subGraph = subGraph;
  }

  public String getOperation() {
    return this.operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Long getProjectId() {
    return this.projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }
}
