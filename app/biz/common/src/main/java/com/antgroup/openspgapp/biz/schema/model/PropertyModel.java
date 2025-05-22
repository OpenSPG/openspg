package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.constraint.Constraint;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/PropertyModel.class */
public class PropertyModel {
  private OntologyId ontologyId;
  private String name;
  private String nameZh;
  private String type;
  private String desc;
  private Boolean inherited;
  private String index;
  private LogicalRule rule;
  private Constraint constraint;
  private List<PropertyModel> subProperties;

  public void setOntologyId(final OntologyId ontologyId) {
    this.ontologyId = ontologyId;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setType(final String type) {
    this.type = type;
  }

  public void setDesc(final String desc) {
    this.desc = desc;
  }

  public void setInherited(final Boolean inherited) {
    this.inherited = inherited;
  }

  public void setIndex(final String index) {
    this.index = index;
  }

  public void setRule(final LogicalRule rule) {
    this.rule = rule;
  }

  public void setConstraint(final Constraint constraint) {
    this.constraint = constraint;
  }

  public void setSubProperties(final List<PropertyModel> subProperties) {
    this.subProperties = subProperties;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof PropertyModel)) {
      return false;
    }
    PropertyModel other = (PropertyModel) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$inherited = getInherited();
    Object other$inherited = other.getInherited();
    if (this$inherited == null) {
      if (other$inherited != null) {
        return false;
      }
    } else if (!this$inherited.equals(other$inherited)) {
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
    Object this$type = getType();
    Object other$type = other.getType();
    if (this$type == null) {
      if (other$type != null) {
        return false;
      }
    } else if (!this$type.equals(other$type)) {
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
    Object this$index = getIndex();
    Object other$index = other.getIndex();
    if (this$index == null) {
      if (other$index != null) {
        return false;
      }
    } else if (!this$index.equals(other$index)) {
      return false;
    }
    Object this$rule = getRule();
    Object other$rule = other.getRule();
    if (this$rule == null) {
      if (other$rule != null) {
        return false;
      }
    } else if (!this$rule.equals(other$rule)) {
      return false;
    }
    Object this$constraint = getConstraint();
    Object other$constraint = other.getConstraint();
    if (this$constraint == null) {
      if (other$constraint != null) {
        return false;
      }
    } else if (!this$constraint.equals(other$constraint)) {
      return false;
    }
    Object this$subProperties = getSubProperties();
    Object other$subProperties = other.getSubProperties();
    return this$subProperties == null
        ? other$subProperties == null
        : this$subProperties.equals(other$subProperties);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof PropertyModel;
  }

  public int hashCode() {
    Object $inherited = getInherited();
    int result = (1 * 59) + ($inherited == null ? 43 : $inherited.hashCode());
    Object $ontologyId = getOntologyId();
    int result2 = (result * 59) + ($ontologyId == null ? 43 : $ontologyId.hashCode());
    Object $name = getName();
    int result3 = (result2 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result4 = (result3 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $type = getType();
    int result5 = (result4 * 59) + ($type == null ? 43 : $type.hashCode());
    Object $desc = getDesc();
    int result6 = (result5 * 59) + ($desc == null ? 43 : $desc.hashCode());
    Object $index = getIndex();
    int result7 = (result6 * 59) + ($index == null ? 43 : $index.hashCode());
    Object $rule = getRule();
    int result8 = (result7 * 59) + ($rule == null ? 43 : $rule.hashCode());
    Object $constraint = getConstraint();
    int result9 = (result8 * 59) + ($constraint == null ? 43 : $constraint.hashCode());
    Object $subProperties = getSubProperties();
    return (result9 * 59) + ($subProperties == null ? 43 : $subProperties.hashCode());
  }

  public String toString() {
    return "PropertyModel(ontologyId="
        + getOntologyId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", type="
        + getType()
        + ", desc="
        + getDesc()
        + ", inherited="
        + getInherited()
        + ", index="
        + getIndex()
        + ", rule="
        + getRule()
        + ", constraint="
        + getConstraint()
        + ", subProperties="
        + getSubProperties()
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

  public String getType() {
    return this.type;
  }

  public String getDesc() {
    return this.desc;
  }

  public Boolean getInherited() {
    return this.inherited;
  }

  public String getIndex() {
    return this.index;
  }

  public LogicalRule getRule() {
    return this.rule;
  }

  public Constraint getConstraint() {
    return this.constraint;
  }

  public List<PropertyModel> getSubProperties() {
    return this.subProperties;
  }
}
