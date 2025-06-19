package com.antgroup.openspgapp.infra.dao.dataobject;

/* loaded from: com.antgroup.openspgapp-infra-dao-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/infra/dao/dataobject/ReasonTutorialDOWithBLOBs.class */
public class ReasonTutorialDOWithBLOBs extends ReasonTutorialDO {
  private String dsl;
  private String nl;
  private String params;
  private String description;

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

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description == null ? null : description.trim();
  }
}
