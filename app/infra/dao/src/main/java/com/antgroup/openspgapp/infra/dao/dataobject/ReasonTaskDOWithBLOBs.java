package com.antgroup.openspgapp.infra.dao.dataobject;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTaskDOWithBLOBs.class */
public class ReasonTaskDOWithBLOBs extends ReasonTaskDO {
  private String dsl;
  private String nl;
  private String params;
  private String resultMessage;
  private String resultTable;
  private String resultNodes;
  private String resultEdges;
  private String resultPaths;

  public String getDsl() {
    return this.dsl;
  }

  public void setDsl(String dsl) {
    this.dsl = dsl == null ? null : dsl.trim();
  }

  public String getNl() {
    return this.nl;
  }

  public void setNl(String nl) {
    this.nl = nl == null ? null : nl.trim();
  }

  public String getParams() {
    return this.params;
  }

  public void setParams(String params) {
    this.params = params == null ? null : params.trim();
  }

  public String getResultMessage() {
    return this.resultMessage;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage == null ? null : resultMessage.trim();
  }

  public String getResultTable() {
    return this.resultTable;
  }

  public void setResultTable(String resultTable) {
    this.resultTable = resultTable == null ? null : resultTable.trim();
  }

  public String getResultNodes() {
    return this.resultNodes;
  }

  public void setResultNodes(String resultNodes) {
    this.resultNodes = resultNodes == null ? null : resultNodes.trim();
  }

  public String getResultEdges() {
    return this.resultEdges;
  }

  public void setResultEdges(String resultEdges) {
    this.resultEdges = resultEdges == null ? null : resultEdges.trim();
  }

  public String getResultPaths() {
    return this.resultPaths;
  }

  public void setResultPaths(String resultPaths) {
    this.resultPaths = resultPaths == null ? null : resultPaths.trim();
  }
}
