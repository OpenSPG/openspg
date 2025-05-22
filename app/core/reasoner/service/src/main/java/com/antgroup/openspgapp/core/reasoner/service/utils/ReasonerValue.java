package com.antgroup.openspgapp.core.reasoner.service.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-core-reasoner-service-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/core/reasoner/service/utils/ReasonerValue.class */
public class ReasonerValue {

  @Value("${cloudext.graphstore.url:}")
  private String graphStoreUrl;

  @Value("${cloudext.searchengine.url:}")
  private String searchEngineUrl;

  @Value("${schema.uri:}")
  private String schemaUrlHost;

  @Value("${builder.model.execute.num:5}")
  private Integer modelExecuteNum;

  @Value("${python.exec:}")
  private String pythonExec;

  @Value("${python.paths:}")
  private String pythonPaths;

  @Value("${python.env:}")
  private String pythonEnv;

  public String toString() {
    return "ReasonerValue(graphStoreUrl="
        + getGraphStoreUrl()
        + ", searchEngineUrl="
        + getSearchEngineUrl()
        + ", schemaUrlHost="
        + getSchemaUrlHost()
        + ", modelExecuteNum="
        + getModelExecuteNum()
        + ", pythonExec="
        + getPythonExec()
        + ", pythonPaths="
        + getPythonPaths()
        + ", pythonEnv="
        + getPythonEnv()
        + ")";
  }

  public String getGraphStoreUrl() {
    return this.graphStoreUrl;
  }

  public String getSearchEngineUrl() {
    return this.searchEngineUrl;
  }

  public String getSchemaUrlHost() {
    return this.schemaUrlHost;
  }

  public Integer getModelExecuteNum() {
    return this.modelExecuteNum;
  }

  public String getPythonExec() {
    return this.pythonExec;
  }

  public String getPythonPaths() {
    return this.pythonPaths;
  }

  public String getPythonEnv() {
    return this.pythonEnv;
  }
}
