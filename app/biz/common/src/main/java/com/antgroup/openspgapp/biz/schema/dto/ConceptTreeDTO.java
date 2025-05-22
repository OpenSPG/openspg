package com.antgroup.openspgapp.biz.schema.dto;

import java.util.ArrayList;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/ConceptTreeDTO.class */
public class ConceptTreeDTO {
  private ConceptDTO conceptDTO;
  private List<ConceptTreeDTO> children = new ArrayList();

  public void setConceptDTO(final ConceptDTO conceptDTO) {
    this.conceptDTO = conceptDTO;
  }

  public void setChildren(final List<ConceptTreeDTO> children) {
    this.children = children;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ConceptTreeDTO)) {
      return false;
    }
    ConceptTreeDTO other = (ConceptTreeDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$conceptDTO = getConceptDTO();
    Object other$conceptDTO = other.getConceptDTO();
    if (this$conceptDTO == null) {
      if (other$conceptDTO != null) {
        return false;
      }
    } else if (!this$conceptDTO.equals(other$conceptDTO)) {
      return false;
    }
    Object this$children = getChildren();
    Object other$children = other.getChildren();
    return this$children == null ? other$children == null : this$children.equals(other$children);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ConceptTreeDTO;
  }

  public int hashCode() {
    Object $conceptDTO = getConceptDTO();
    int result = (1 * 59) + ($conceptDTO == null ? 43 : $conceptDTO.hashCode());
    Object $children = getChildren();
    return (result * 59) + ($children == null ? 43 : $children.hashCode());
  }

  public String toString() {
    return "ConceptTreeDTO(conceptDTO=" + getConceptDTO() + ", children=" + getChildren() + ")";
  }

  public ConceptDTO getConceptDTO() {
    return this.conceptDTO;
  }

  public List<ConceptTreeDTO> getChildren() {
    return this.children;
  }
}
