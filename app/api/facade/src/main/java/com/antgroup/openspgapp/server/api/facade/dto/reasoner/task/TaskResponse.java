package com.antgroup.openspgapp.server.api.facade.dto.reasoner.task;

import com.antgroup.openspg.server.common.model.base.BaseResponse;
import com.antgroup.openspgapp.core.reasoner.model.task.MarkEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.StatusEnum;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Path;
import com.antgroup.openspgapp.core.reasoner.model.task.result.TableResult;
import java.util.List;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/task/TaskResponse.class */
public class TaskResponse extends BaseResponse {
  private static final long serialVersionUID = -926986438586998539L;
  private Long id;
  private Long projectId;
  private Long userId;
  private Long sessionId;
  private String dsl;
  private String nl;
  private Map<String, String> params;
  private MarkEnum mark;
  private StatusEnum status;
  private String resultMessage;
  private TableResult resultTable;
  private List<Node> resultNodes;
  private List<Edge> resultEdges;
  private List<Path> resultPaths;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public String getDsl() {
    return this.dsl;
  }

  public void setDsl(String dsl) {
    this.dsl = dsl;
  }

  public String getNl() {
    return this.nl;
  }

  public void setNl(String nl) {
    this.nl = nl;
  }

  public Map<String, String> getParams() {
    return this.params;
  }

  public void setParams(Map<String, String> params) {
    this.params = params;
  }

  public MarkEnum getMark() {
    return this.mark;
  }

  public void setMark(MarkEnum mark) {
    this.mark = mark;
  }

  public StatusEnum getStatus() {
    return this.status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public String getResultMessage() {
    return this.resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  public TableResult getResultTable() {
    return this.resultTable;
  }

  public void setResultTable(TableResult resultTable) {
    this.resultTable = resultTable;
  }

  public List<Node> getResultNodes() {
    return this.resultNodes;
  }

  public void setResultNodes(List<Node> resultNodes) {
    this.resultNodes = resultNodes;
  }

  public List<Edge> getResultEdges() {
    return this.resultEdges;
  }

  public void setResultEdges(List<Edge> resultEdges) {
    this.resultEdges = resultEdges;
  }

  public List<Path> getResultPaths() {
    return this.resultPaths;
  }

  public void setResultPaths(List<Path> resultPaths) {
    this.resultPaths = resultPaths;
  }
}
