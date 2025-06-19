package com.antgroup.openspgapp.biz.schema.model;

import com.antgroup.openspg.core.schema.model.OntologyId;
import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import java.util.List;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/model/EdgeTypeModel.class */
public class EdgeTypeModel {
  private OntologyId ontologyId;
  private String name;
  private String nameZh;
  private String desc;
  private Boolean inherited;
  private Boolean semanticRelation;
  private String sourceType;
  private String targetType;
  private LogicalRule rule;
  private List<PropertyModel> properties;

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

  public void setInherited(final Boolean inherited) {
    this.inherited = inherited;
  }

  public void setSemanticRelation(final Boolean semanticRelation) {
    this.semanticRelation = semanticRelation;
  }

  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }

  public void setTargetType(final String targetType) {
    this.targetType = targetType;
  }

  public void setRule(final LogicalRule rule) {
    this.rule = rule;
  }

  public void setProperties(final List<PropertyModel> properties) {
    this.properties = properties;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof EdgeTypeModel)) {
      return false;
    }
    EdgeTypeModel other = (EdgeTypeModel) o;
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
    Object this$semanticRelation = getSemanticRelation();
    Object other$semanticRelation = other.getSemanticRelation();
    if (this$semanticRelation == null) {
      if (other$semanticRelation != null) {
        return false;
      }
    } else if (!this$semanticRelation.equals(other$semanticRelation)) {
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
    Object this$sourceType = getSourceType();
    Object other$sourceType = other.getSourceType();
    if (this$sourceType == null) {
      if (other$sourceType != null) {
        return false;
      }
    } else if (!this$sourceType.equals(other$sourceType)) {
      return false;
    }
    Object this$targetType = getTargetType();
    Object other$targetType = other.getTargetType();
    if (this$targetType == null) {
      if (other$targetType != null) {
        return false;
      }
    } else if (!this$targetType.equals(other$targetType)) {
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
    Object this$properties = getProperties();
    Object other$properties = other.getProperties();
    return this$properties == null
        ? other$properties == null
        : this$properties.equals(other$properties);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof EdgeTypeModel;
  }

  public int hashCode() {
    Object $inherited = getInherited();
    int result = (1 * 59) + ($inherited == null ? 43 : $inherited.hashCode());
    Object $semanticRelation = getSemanticRelation();
    int result2 = (result * 59) + ($semanticRelation == null ? 43 : $semanticRelation.hashCode());
    Object $ontologyId = getOntologyId();
    int result3 = (result2 * 59) + ($ontologyId == null ? 43 : $ontologyId.hashCode());
    Object $name = getName();
    int result4 = (result3 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result5 = (result4 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $desc = getDesc();
    int result6 = (result5 * 59) + ($desc == null ? 43 : $desc.hashCode());
    Object $sourceType = getSourceType();
    int result7 = (result6 * 59) + ($sourceType == null ? 43 : $sourceType.hashCode());
    Object $targetType = getTargetType();
    int result8 = (result7 * 59) + ($targetType == null ? 43 : $targetType.hashCode());
    Object $rule = getRule();
    int result9 = (result8 * 59) + ($rule == null ? 43 : $rule.hashCode());
    Object $properties = getProperties();
    return (result9 * 59) + ($properties == null ? 43 : $properties.hashCode());
  }

  public String toString() {
    return "EdgeTypeModel(ontologyId="
        + getOntologyId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", desc="
        + getDesc()
        + ", inherited="
        + getInherited()
        + ", semanticRelation="
        + getSemanticRelation()
        + ", sourceType="
        + getSourceType()
        + ", targetType="
        + getTargetType()
        + ", rule="
        + getRule()
        + ", properties="
        + getProperties()
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

  public Boolean getInherited() {
    return this.inherited;
  }

  public Boolean getSemanticRelation() {
    return this.semanticRelation;
  }

  public String getSourceType() {
    return this.sourceType;
  }

  public String getTargetType() {
    return this.targetType;
  }

  public LogicalRule getRule() {
    return this.rule;
  }

  public List<PropertyModel> getProperties() {
    return this.properties;
  }
}
