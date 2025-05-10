package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/MarkTaskRequest.class */
public class MarkTaskRequest extends BaseRequest {
  private static final long serialVersionUID = -8552512405819913632L;
  private Long id;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
