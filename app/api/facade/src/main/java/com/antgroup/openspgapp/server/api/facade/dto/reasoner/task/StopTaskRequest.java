package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/StopTaskRequest.class */
public class StopTaskRequest extends BaseRequest {
  private static final long serialVersionUID = -6171165802144423969L;
  private Long id;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
