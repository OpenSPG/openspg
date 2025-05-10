package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseRequest;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/QueryTasksRequest.class */
public class QueryTasksRequest extends BaseRequest {
  private static final long serialVersionUID = -3199400676929548621L;
  private Long projectId;
  private Long userId;
  private Long sessionId;
  private String mark;
  private Long start;
  private Integer limit;
  private String keyword;

  public Long getProjectId() {
    return this.projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public Long getUserId() {
    return this.userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public String getMark() {
    return this.mark;
  }

  public void setMark(String mark) {
    this.mark = mark;
  }

  public Long getStart() {
    return this.start;
  }

  public void setStart(Long start) {
    this.start = start;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public String getKeyword() {
    return this.keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }
}
