package com.antgroup.openspgapp.biz.schema.dto;

import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/ConceptNodeDTO.class */
public class ConceptNodeDTO {
  private String id;
  private String label;
  private String name;
  private String nameZh;
  private String primaryKey;
  private String description;
  private String descriptionZh;
  private Boolean metaConcept;
  private Map<String, Object> properties;
  private Map<String, Object> parentProps;

  public void setId(final String id) {
    this.id = id;
  }

  public void setLabel(final String label) {
    this.label = label;
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

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setDescriptionZh(final String descriptionZh) {
    this.descriptionZh = descriptionZh;
  }

  public void setMetaConcept(final Boolean metaConcept) {
    this.metaConcept = metaConcept;
  }

  public void setProperties(final Map<String, Object> properties) {
    this.properties = properties;
  }

  public void setParentProps(final Map<String, Object> parentProps) {
    this.parentProps = parentProps;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof ConceptNodeDTO)) {
      return false;
    }
    ConceptNodeDTO other = (ConceptNodeDTO) o;
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
    Object this$id = getId();
    Object other$id = other.getId();
    if (this$id == null) {
      if (other$id != null) {
        return false;
      }
    } else if (!this$id.equals(other$id)) {
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
    Object this$description = getDescription();
    Object other$description = other.getDescription();
    if (this$description == null) {
      if (other$description != null) {
        return false;
      }
    } else if (!this$description.equals(other$description)) {
      return false;
    }
    Object this$descriptionZh = getDescriptionZh();
    Object other$descriptionZh = other.getDescriptionZh();
    if (this$descriptionZh == null) {
      if (other$descriptionZh != null) {
        return false;
      }
    } else if (!this$descriptionZh.equals(other$descriptionZh)) {
      return false;
    }
    Object this$properties = getProperties();
    Object other$properties = other.getProperties();
    if (this$properties == null) {
      if (other$properties != null) {
        return false;
      }
    } else if (!this$properties.equals(other$properties)) {
      return false;
    }
    Object this$parentProps = getParentProps();
    Object other$parentProps = other.getParentProps();
    return this$parentProps == null
        ? other$parentProps == null
        : this$parentProps.equals(other$parentProps);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof ConceptNodeDTO;
  }

  public int hashCode() {
    Object $metaConcept = getMetaConcept();
    int result = (1 * 59) + ($metaConcept == null ? 43 : $metaConcept.hashCode());
    Object $id = getId();
    int result2 = (result * 59) + ($id == null ? 43 : $id.hashCode());
    Object $label = getLabel();
    int result3 = (result2 * 59) + ($label == null ? 43 : $label.hashCode());
    Object $name = getName();
    int result4 = (result3 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result5 = (result4 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $primaryKey = getPrimaryKey();
    int result6 = (result5 * 59) + ($primaryKey == null ? 43 : $primaryKey.hashCode());
    Object $description = getDescription();
    int result7 = (result6 * 59) + ($description == null ? 43 : $description.hashCode());
    Object $descriptionZh = getDescriptionZh();
    int result8 = (result7 * 59) + ($descriptionZh == null ? 43 : $descriptionZh.hashCode());
    Object $properties = getProperties();
    int result9 = (result8 * 59) + ($properties == null ? 43 : $properties.hashCode());
    Object $parentProps = getParentProps();
    return (result9 * 59) + ($parentProps == null ? 43 : $parentProps.hashCode());
  }

  public String toString() {
    return "ConceptNodeDTO(id="
        + getId()
        + ", label="
        + getLabel()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", primaryKey="
        + getPrimaryKey()
        + ", description="
        + getDescription()
        + ", descriptionZh="
        + getDescriptionZh()
        + ", metaConcept="
        + getMetaConcept()
        + ", properties="
        + getProperties()
        + ", parentProps="
        + getParentProps()
        + ")";
  }

  public String getId() {
    return this.id;
  }

  public String getLabel() {
    return this.label;
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

  public String getDescription() {
    return this.description;
  }

  public String getDescriptionZh() {
    return this.descriptionZh;
  }

  public Boolean getMetaConcept() {
    return this.metaConcept;
  }

  public Map<String, Object> getProperties() {
    return this.properties;
  }

  public Map<String, Object> getParentProps() {
    return this.parentProps;
  }
}
