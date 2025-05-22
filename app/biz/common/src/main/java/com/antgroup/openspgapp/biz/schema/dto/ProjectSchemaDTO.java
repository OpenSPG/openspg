package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/ProjectSchemaDTO.class */
public class ProjectSchemaDTO extends BaseDTO implements Serializable {
  List<EntityTypeDTO> entityTypeDTOList;
  List<RelationTypeDTO> relationTypeDTOList;

  public void setEntityTypeDTOList(final List<EntityTypeDTO> entityTypeDTOList) {
    this.entityTypeDTOList = entityTypeDTOList;
  }

  public void setRelationTypeDTOList(final List<RelationTypeDTO> relationTypeDTOList) {
    this.relationTypeDTOList = relationTypeDTOList;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ProjectSchemaDTO)) {
      return false;
    }
    ProjectSchemaDTO other = (ProjectSchemaDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$entityTypeDTOList = getEntityTypeDTOList();
    Object other$entityTypeDTOList = other.getEntityTypeDTOList();
    if (this$entityTypeDTOList == null) {
      if (other$entityTypeDTOList != null) {
        return false;
      }
    } else if (!this$entityTypeDTOList.equals(other$entityTypeDTOList)) {
      return false;
    }
    Object this$relationTypeDTOList = getRelationTypeDTOList();
    Object other$relationTypeDTOList = other.getRelationTypeDTOList();
    return this$relationTypeDTOList == null
        ? other$relationTypeDTOList == null
        : this$relationTypeDTOList.equals(other$relationTypeDTOList);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ProjectSchemaDTO;
  }

  public int hashCode() {
    Object $entityTypeDTOList = getEntityTypeDTOList();
    int result = (1 * 59) + ($entityTypeDTOList == null ? 43 : $entityTypeDTOList.hashCode());
    Object $relationTypeDTOList = getRelationTypeDTOList();
    return (result * 59) + ($relationTypeDTOList == null ? 43 : $relationTypeDTOList.hashCode());
  }

  public String toString() {
    return "ProjectSchemaDTO(entityTypeDTOList="
        + getEntityTypeDTOList()
        + ", relationTypeDTOList="
        + getRelationTypeDTOList()
        + ")";
  }

  public List<EntityTypeDTO> getEntityTypeDTOList() {
    return this.entityTypeDTOList;
  }

  public List<RelationTypeDTO> getRelationTypeDTOList() {
    return this.relationTypeDTOList;
  }
}
