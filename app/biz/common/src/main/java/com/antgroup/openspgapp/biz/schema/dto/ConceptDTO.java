package com.antgroup.openspgapp.biz.schema.dto;

import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/ConceptDTO.class */
public class ConceptDTO {
  private String id;
  private String name;
  private String nameZh;
  private String primaryKey;
  private Boolean metaConcept = false;
  private String conceptParentType;
  private String conceptDescription;
  private String conceptDescriptionZh;
  private String label;
  private Integer subTypeCount;
  private Map<String, Object> properties;

  public void setId(final String id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setPrimaryKey(final String primaryKey) {
    this.primaryKey = primaryKey;
  }

  public void setMetaConcept(final Boolean metaConcept) {
    this.metaConcept = metaConcept;
  }

  public void setConceptParentType(final String conceptParentType) {
    this.conceptParentType = conceptParentType;
  }

  public void setConceptDescription(final String conceptDescription) {
    this.conceptDescription = conceptDescription;
  }

  public void setConceptDescriptionZh(final String conceptDescriptionZh) {
    this.conceptDescriptionZh = conceptDescriptionZh;
  }

  public void setLabel(final String label) {
    this.label = label;
  }

  public void setSubTypeCount(final Integer subTypeCount) {
    this.subTypeCount = subTypeCount;
  }

  public void setProperties(final Map<String, Object> properties) {
    this.properties = properties;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ConceptDTO)) {
      return false;
    }
    ConceptDTO other = (ConceptDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$metaConcept = getMetaConcept();
    Object other$metaConcept = other.getMetaConcept();
    if (this$metaConcept == null) {
      if (other$metaConcept != null) {
        return false;
      }
    } else if (!this$metaConcept.equals(other$metaConcept)) {
      return false;
    }
    Object this$subTypeCount = getSubTypeCount();
    Object other$subTypeCount = other.getSubTypeCount();
    if (this$subTypeCount == null) {
      if (other$subTypeCount != null) {
        return false;
      }
    } else if (!this$subTypeCount.equals(other$subTypeCount)) {
      return false;
    }
    Object this$id = getId();
    Object other$id = other.getId();
    if (this$id == null) {
      if (other$id != null) {
        return false;
      }
    } else if (!this$id.equals(other$id)) {
      return false;
    }
    Object this$name = getName();
    Object other$name = other.getName();
    if (this$name == null) {
      if (other$name != null) {
        return false;
      }
    } else if (!this$name.equals(other$name)) {
      return false;
    }
    Object this$nameZh = getNameZh();
    Object other$nameZh = other.getNameZh();
    if (this$nameZh == null) {
      if (other$nameZh != null) {
        return false;
      }
    } else if (!this$nameZh.equals(other$nameZh)) {
      return false;
    }
    Object this$primaryKey = getPrimaryKey();
    Object other$primaryKey = other.getPrimaryKey();
    if (this$primaryKey == null) {
      if (other$primaryKey != null) {
        return false;
      }
    } else if (!this$primaryKey.equals(other$primaryKey)) {
      return false;
    }
    Object this$conceptParentType = getConceptParentType();
    Object other$conceptParentType = other.getConceptParentType();
    if (this$conceptParentType == null) {
      if (other$conceptParentType != null) {
        return false;
      }
    } else if (!this$conceptParentType.equals(other$conceptParentType)) {
      return false;
    }
    Object this$conceptDescription = getConceptDescription();
    Object other$conceptDescription = other.getConceptDescription();
    if (this$conceptDescription == null) {
      if (other$conceptDescription != null) {
        return false;
      }
    } else if (!this$conceptDescription.equals(other$conceptDescription)) {
      return false;
    }
    Object this$conceptDescriptionZh = getConceptDescriptionZh();
    Object other$conceptDescriptionZh = other.getConceptDescriptionZh();
    if (this$conceptDescriptionZh == null) {
      if (other$conceptDescriptionZh != null) {
        return false;
      }
    } else if (!this$conceptDescriptionZh.equals(other$conceptDescriptionZh)) {
      return false;
    }
    Object this$label = getLabel();
    Object other$label = other.getLabel();
    if (this$label == null) {
      if (other$label != null) {
        return false;
      }
    } else if (!this$label.equals(other$label)) {
      return false;
    }
    Object this$properties = getProperties();
    Object other$properties = other.getProperties();
    return this$properties == null
        ? other$properties == null
        : this$properties.equals(other$properties);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ConceptDTO;
  }

  public int hashCode() {
    Object $metaConcept = getMetaConcept();
    int result = (1 * 59) + ($metaConcept == null ? 43 : $metaConcept.hashCode());
    Object $subTypeCount = getSubTypeCount();
    int result2 = (result * 59) + ($subTypeCount == null ? 43 : $subTypeCount.hashCode());
    Object $id = getId();
    int result3 = (result2 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $name = getName();
    int result4 = (result3 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result5 = (result4 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $primaryKey = getPrimaryKey();
    int result6 = (result5 * 59) + ($primaryKey == null ? 43 : $primaryKey.hashCode());
    Object $conceptParentType = getConceptParentType();
    int result7 =
        (result6 * 59) + ($conceptParentType == null ? 43 : $conceptParentType.hashCode());
    Object $conceptDescription = getConceptDescription();
    int result8 =
        (result7 * 59) + ($conceptDescription == null ? 43 : $conceptDescription.hashCode());
    Object $conceptDescriptionZh = getConceptDescriptionZh();
    int result9 =
        (result8 * 59) + ($conceptDescriptionZh == null ? 43 : $conceptDescriptionZh.hashCode());
    Object $label = getLabel();
    int result10 = (result9 * 59) + ($label == null ? 43 : $label.hashCode());
    Object $properties = getProperties();
    return (result10 * 59) + ($properties == null ? 43 : $properties.hashCode());
  }

  public String toString() {
    return "ConceptDTO(id="
        + getId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", primaryKey="
        + getPrimaryKey()
        + ", metaConcept="
        + getMetaConcept()
        + ", conceptParentType="
        + getConceptParentType()
        + ", conceptDescription="
        + getConceptDescription()
        + ", conceptDescriptionZh="
        + getConceptDescriptionZh()
        + ", label="
        + getLabel()
        + ", subTypeCount="
        + getSubTypeCount()
        + ", properties="
        + getProperties()
        + ")";
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }

  public String getPrimaryKey() {
    return this.primaryKey;
  }

  public Boolean getMetaConcept() {
    return this.metaConcept;
  }

  public String getConceptParentType() {
    return this.conceptParentType;
  }

  public String getConceptDescription() {
    return this.conceptDescription;
  }

  public String getConceptDescriptionZh() {
    return this.conceptDescriptionZh;
  }

  public String getLabel() {
    return this.label;
  }

  public Integer getSubTypeCount() {
    return this.subTypeCount;
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }
}
