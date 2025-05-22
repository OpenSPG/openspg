package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import com.antgroup.openspg.server.common.model.job.SubGraph;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/ReportLogRequest.class */
public class ReportLogRequest extends BaseRequest {
  private static final long serialVersionUID = -2641851823210334233L;
  private Long taskId;
  private String content;
  private List<SubGraph> subgraph;
  private String executeStatus;
  private String gmtCreate;

  public void setTaskId(final Long taskId) {
    this.taskId = taskId;
  }

  public void setContent(final String content) {
    this.content = content;
  }

  public void setSubgraph(final List<SubGraph> subgraph) {
    this.subgraph = subgraph;
  }

  public void setExecuteStatus(final String executeStatus) {
    this.executeStatus = executeStatus;
  }

  public void setGmtCreate(final String gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  public Long getTaskId() {
    return this.taskId;
  }

  public String getContent() {
    return this.content;
  }

  public List<SubGraph> getSubgraph() {
    return this.subgraph;
  }

  public String getExecuteStatus() {
    return this.executeStatus;
  }

  public String getGmtCreate() {
    return this.gmtCreate;
  }
}
