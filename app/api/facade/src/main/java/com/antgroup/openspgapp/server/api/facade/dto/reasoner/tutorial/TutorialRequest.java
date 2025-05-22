package com.antgroup.openspgapp.server.api.facade.dto.reasoner.tutorial;

import com.antgroup.openspg.server.common.model.base.BaseRequest;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-api-facade-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/server/api/facade/dto/reasoner/tutorial/TutorialRequest.class */
public class TutorialRequest extends BaseRequest {
  private static final long serialVersionUID = -6557414741948672527L;
  private Long id;
  private Long projectId;
  private Boolean enable;
  private String name;
  private String dsl;
  private String nl;
  private Map<String, String> params;
  private String description;

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

  public Boolean getEnable() {
    return this.enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
