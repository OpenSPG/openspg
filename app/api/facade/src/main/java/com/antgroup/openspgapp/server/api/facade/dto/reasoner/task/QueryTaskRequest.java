package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/QueryTaskRequest.class */
public class QueryTaskRequest extends BaseRequest {
  private static final long serialVersionUID = -2641851823210333899L;
  private Long id;
  private Long jobId;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getJobId() {
    return this.jobId;
  }

  public void setJobId(Long jobId) {
    this.jobId = jobId;
  }
}
