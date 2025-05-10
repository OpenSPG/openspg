package com.antgroup.openspgapp.core.reasoner.model.task;

import com.antgroup.openspg.server.common.model.base.BaseModel;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Edge;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Node;
import com.antgroup.openspgapp.core.reasoner.model.task.result.Path;
import com.antgroup.openspgapp.core.reasoner.model.task.result.TableResult;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* loaded from: core-reasoner-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/model/task/Task.class */
public class Task extends BaseModel {
  private static final long serialVersionUID = -3639193821121146580L;
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
  private TableResult resultTableResult;
  private List<Node> resultNodes;
  private List<Edge> resultEdges;
  private List<Path> resultPaths;
  private Object extend;
  private Date gmtModified;

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
    return this.resultTableResult;
  }

  public void setResultTable(TableResult resultTableResult) {
    this.resultTableResult = resultTableResult;
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

  public void setResultEdges(List<Edge> resultEdge) {
    this.resultEdges = resultEdge;
  }

  public List<Path> getResultPaths() {
    return this.resultPaths;
  }

  public void setResultPaths(List<Path> resultPaths) {
    this.resultPaths = resultPaths;
  }

  public Object getExtend() {
    return this.extend;
  }

  public void setExtend(Object extend) {
    this.extend = extend;
  }

  public Date getGmtModified() {
    return this.gmtModified;
  }

  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }
}
