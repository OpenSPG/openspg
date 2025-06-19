package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.core.schema.model.OntologyId;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/NodeTypeModel.class */
public class NodeTypeModel {
  private OntologyId ontologyId;
  private String name;
  private String nameZh;
  private String desc;
  private String type;
  private String parentName;
  private String hypernymPredicate;
  private List<PropertyModel> properties;
  private List<EdgeTypeModel> relations;

  public void setOntologyId(final OntologyId ontologyId) {
    this.ontologyId = ontologyId;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setDesc(final String desc) {
    this.desc = desc;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setParentName(final String parentName) {
    this.parentName = parentName;
  }

  public void setHypernymPredicate(final String hypernymPredicate) {
    this.hypernymPredicate = hypernymPredicate;
  }

  public void setProperties(final List<PropertyModel> properties) {
    this.properties = properties;
  }

  public void setRelations(final List<EdgeTypeModel> relations) {
    this.relations = relations;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof NodeTypeModel)) {
      return false;
    }
    NodeTypeModel other = (NodeTypeModel) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$ontologyId = getOntologyId();
    Object other$ontologyId = other.getOntologyId();
    if (this$ontologyId == null) {
      if (other$ontologyId != null) {
        return false;
      }
    } else if (!this$ontologyId.equals(other$ontologyId)) {
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
    Object this$desc = getDesc();
    Object other$desc = other.getDesc();
    if (this$desc == null) {
      if (other$desc != null) {
        return false;
      }
    } else if (!this$desc.equals(other$desc)) {
      return false;
    }
    Object this$type = getType();
    Object other$type = other.getType();
    if (this$type == null) {
      if (other$type != null) {
        return false;
      }
    } else if (!this$type.equals(other$type)) {
      return false;
    }
    Object this$parentName = getParentName();
    Object other$parentName = other.getParentName();
    if (this$parentName == null) {
      if (other$parentName != null) {
        return false;
      }
    } else if (!this$parentName.equals(other$parentName)) {
      return false;
    }
    Object this$hypernymPredicate = getHypernymPredicate();
    Object other$hypernymPredicate = other.getHypernymPredicate();
    if (this$hypernymPredicate == null) {
      if (other$hypernymPredicate != null) {
        return false;
      }
    } else if (!this$hypernymPredicate.equals(other$hypernymPredicate)) {
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
    Object this$relations = getRelations();
    Object other$relations = other.getRelations();
    return this$relations == null
        ? other$relations == null
        : this$relations.equals(other$relations);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof NodeTypeModel;
  }

  public int hashCode() {
    Object $ontologyId = getOntologyId();
    int result = (1 * 59) + ($ontologyId == null ? 43 : $ontologyId.hashCode());
    Object $name = getName();
    int result2 = (result * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result3 = (result2 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $desc = getDesc();
    int result4 = (result3 * 59) + ($desc == null ? 43 : $desc.hashCode());
    Object $type = getType();
    int result5 = (result4 * 59) + ($type == null ? 43 : $type.hashCode());
    Object $parentName = getParentName();
    int result6 = (result5 * 59) + ($parentName == null ? 43 : $parentName.hashCode());
    Object $hypernymPredicate = getHypernymPredicate();
    int result7 =
        (result6 * 59) + ($hypernymPredicate == null ? 43 : $hypernymPredicate.hashCode());
    Object $properties = getProperties();
    int result8 = (result7 * 59) + ($properties == null ? 43 : $properties.hashCode());
    Object $relations = getRelations();
    return (result8 * 59) + ($relations == null ? 43 : $relations.hashCode());
  }

  public String toString() {
    return "NodeTypeModel(ontologyId="
        + getOntologyId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", desc="
        + getDesc()
        + ", type="
        + getType()
        + ", parentName="
        + getParentName()
        + ", hypernymPredicate="
        + getHypernymPredicate()
        + ", properties="
        + getProperties()
        + ", relations="
        + getRelations()
        + ")";
  }

  public OntologyId getOntologyId() {
    return this.ontologyId;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }

  public String getDesc() {
    return this.desc;
  }

  public String getType() {
    return this.type;
  }

  public String getParentName() {
    return this.parentName;
  }

  public String getHypernymPredicate() {
    return this.hypernymPredicate;
  }

  public List<PropertyModel> getProperties() {
    return this.properties;
  }

  public List<EdgeTypeModel> getRelations() {
    return this.relations;
  }
}
