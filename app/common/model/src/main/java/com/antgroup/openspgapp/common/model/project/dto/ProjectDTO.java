package com.antgroup.openspgapp.common.model.project.dto;

/* loaded from: com.antgroup.openspgapp-common-model-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/common/model/project/dto/ProjectDTO.class */
public class ProjectDTO {
  private Long id;
  private String name;
  private String description;
  private String namespace;
  private Long tenantId;
  private String config;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNamespace() {
    return this.namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public Long getTenantId() {
    return this.tenantId;
  }

  public void setTenantId(Long tenantId) {
    this.tenantId = tenantId;
  }

  public String getConfig() {
    return this.config;
  }

  public void setConfig(String config) {
    this.config = config;
  }
}
