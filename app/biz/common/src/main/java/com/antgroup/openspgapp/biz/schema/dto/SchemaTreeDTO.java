package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/SchemaTreeDTO.class */
public class SchemaTreeDTO extends BaseDTO implements Serializable {
  private EntityTypeDTO entityTypeDTO;
  private List<SchemaTreeDTO> children = Lists.newArrayList();

  public void setEntityTypeDTO(final EntityTypeDTO entityTypeDTO) {
    this.entityTypeDTO = entityTypeDTO;
  }

  public void setChildren(final List<SchemaTreeDTO> children) {
    this.children = children;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof SchemaTreeDTO)) {
      return false;
    }
    SchemaTreeDTO other = (SchemaTreeDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$entityTypeDTO = getEntityTypeDTO();
    Object other$entityTypeDTO = other.getEntityTypeDTO();
    if (this$entityTypeDTO == null) {
      if (other$entityTypeDTO != null) {
        return false;
      }
    } else if (!this$entityTypeDTO.equals(other$entityTypeDTO)) {
      return false;
    }
    Object this$children = getChildren();
    Object other$children = other.getChildren();
    return this$children == null ? other$children == null : this$children.equals(other$children);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof SchemaTreeDTO;
  }

  public int hashCode() {
    Object $entityTypeDTO = getEntityTypeDTO();
    int result = (1 * 59) + ($entityTypeDTO == null ? 43 : $entityTypeDTO.hashCode());
    Object $children = getChildren();
    return (result * 59) + ($children == null ? 43 : $children.hashCode());
  }

  public String toString() {
    return "SchemaTreeDTO(entityTypeDTO="
        + getEntityTypeDTO()
        + ", children="
        + getChildren()
        + ")";
  }

  public EntityTypeDTO getEntityTypeDTO() {
    return this.entityTypeDTO;
  }

  public List<SchemaTreeDTO> getChildren() {
    return this.children;
  }
}
