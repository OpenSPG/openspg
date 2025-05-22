package com.antgroup.openspgapp.biz.schema.model;

import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/SchemaModel.class */
public class SchemaModel {
  private String namespace;
  private List<NodeTypeModel> nodeTypeModels;

  public void setNamespace(final String namespace) {
    this.namespace = namespace;
  }

  public void setNodeTypeModels(final List<NodeTypeModel> nodeTypeModels) {
    this.nodeTypeModels = nodeTypeModels;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SchemaModel)) {
      return false;
    }
    SchemaModel other = (SchemaModel) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$namespace = getNamespace();
    Object other$namespace = other.getNamespace();
    if (this$namespace == null) {
      if (other$namespace != null) {
        return false;
      }
    } else if (!this$namespace.equals(other$namespace)) {
      return false;
    }
    Object this$nodeTypeModels = getNodeTypeModels();
    Object other$nodeTypeModels = other.getNodeTypeModels();
    return this$nodeTypeModels == null
        ? other$nodeTypeModels == null
        : this$nodeTypeModels.equals(other$nodeTypeModels);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof SchemaModel;
  }

  public int hashCode() {
    Object $namespace = getNamespace();
    int result = (1 * 59) + ($namespace == null ? 43 : $namespace.hashCode());
    Object $nodeTypeModels = getNodeTypeModels();
    return (result * 59) + ($nodeTypeModels == null ? 43 : $nodeTypeModels.hashCode());
  }

  public String toString() {
    return "SchemaModel(namespace="
        + getNamespace()
        + ", nodeTypeModels="
        + getNodeTypeModels()
        + ")";
  }

  public String getNamespace() {
    return this.namespace;
  }

  public List<NodeTypeModel> getNodeTypeModels() {
    return this.nodeTypeModels;
  }
}
