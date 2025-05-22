package com.antgroup.openspgapp.server.api.facade.dto.builder;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspg.server.common.model.bulider.BuilderJob;
import com.antgroup.openspgapp.core.reasoner.model.SubGraph;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/builder/BuilderJobSubGraphRequest.class */
public class BuilderJobSubGraphRequest extends BaseRequest {
  private static final long serialVersionUID = 2611087767712034352L;
  private SubGraph subGraph;
  private BuilderJob job;

  public SubGraph getSubGraph() {
    return this.subGraph;
  }

  public void setSubGraph(SubGraph subGraph) {
    this.subGraph = subGraph;
  }

  public BuilderJob getJob() {
    return this.job;
  }

  public void setJob(BuilderJob job) {
    this.job = job;
  }
}
