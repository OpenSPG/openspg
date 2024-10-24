package com.antgroup.openspg.server.api.facade.dto.service.request;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspg.server.common.model.job.SubGraph;

public class WriterGraphRequest extends BaseRequest {

  private static final long serialVersionUID = -5051318132737772511L;

  private SubGraph subGraph;

  /** UPSERT OR DELETE * */
  private String operation;

  Long projectId;

  Boolean enableLeadTo;

  public WriterGraphRequest() {}

  public WriterGraphRequest(
      SubGraph subGraph, String operation, Long projectId, Boolean enableLeadTo) {
    this.subGraph = subGraph;
    this.operation = operation;
    this.projectId = projectId;
    this.enableLeadTo = enableLeadTo;
  }

  public SubGraph getSubGraph() {
    return subGraph;
  }

  public void setSubGraph(SubGraph subGraph) {
    this.subGraph = subGraph;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Boolean getEnableLeadTo() {
    return enableLeadTo;
  }

  public void setEnableLeadTo(Boolean enableLeadTo) {
    this.enableLeadTo = enableLeadTo;
  }
}
