package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/PropertyDTO.class */
public class PropertyDTO extends BaseDTO implements Serializable {
  private Long id;
  private String name;
  private String nameZh;
  private String description;
  private Long rangeId;
  private String rangeName;
  private String rangeNameZh;
  private Long rangeEntityId;
  private String rangeEntityName;
  private String rangeEntityNameZh;
  private String propertyToRelationName;
  private String propertyToRelationNameZh;
  private String propertyCategoryEnum;
  private String propertyTagEnum;
  private String maskType;
  private List<AttrConsDTO> constraints;
  private Long propertySource;
  private String propertySourceName;
  private Boolean aggregation;
  private String propType;
  private String valueTag;
  private LogicRuleDTO logicRule;
  private Boolean logicProperty;
  private String config;
  private Boolean spreadable = false;
  private Map<String, Object> extInfo = new HashMap();
  private Boolean defaultTime = false;
  private List<PropertyDTO> subPropertyList = new ArrayList();

  public void setId(final Long id) {
    this.id = id;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public void setRangeId(final Long rangeId) {
    this.rangeId = rangeId;
  }

  public void setRangeName(final String rangeName) {
    this.rangeName = rangeName;
  }

  public void setRangeNameZh(final String rangeNameZh) {
    this.rangeNameZh = rangeNameZh;
  }

  public void setRangeEntityId(final Long rangeEntityId) {
    this.rangeEntityId = rangeEntityId;
  }

  public void setRangeEntityName(final String rangeEntityName) {
    this.rangeEntityName = rangeEntityName;
  }

  public void setRangeEntityNameZh(final String rangeEntityNameZh) {
    this.rangeEntityNameZh = rangeEntityNameZh;
  }

  public void setPropertyToRelationName(final String propertyToRelationName) {
    this.propertyToRelationName = propertyToRelationName;
  }

  public void setPropertyToRelationNameZh(final String propertyToRelationNameZh) {
    this.propertyToRelationNameZh = propertyToRelationNameZh;
  }

  public void setPropertyCategoryEnum(final String propertyCategoryEnum) {
    this.propertyCategoryEnum = propertyCategoryEnum;
  }

  public void setPropertyTagEnum(final String propertyTagEnum) {
    this.propertyTagEnum = propertyTagEnum;
  }

  public void setSpreadable(final Boolean spreadable) {
    this.spreadable = spreadable;
  }

  public void setMaskType(final String maskType) {
    this.maskType = maskType;
  }

  public void setConstraints(final List<AttrConsDTO> constraints) {
    this.constraints = constraints;
  }

  public void setPropertySource(final Long propertySource) {
    this.propertySource = propertySource;
  }

  public void setPropertySourceName(final String propertySourceName) {
    this.propertySourceName = propertySourceName;
  }

  public void setAggregation(final Boolean aggregation) {
    this.aggregation = aggregation;
  }

  public void setExtInfo(final Map<String, Object> extInfo) {
    this.extInfo = extInfo;
  }

  public void setPropType(final String propType) {
    this.propType = propType;
  }

  public void setValueTag(final String valueTag) {
    this.valueTag = valueTag;
  }

  public void setLogicRule(final LogicRuleDTO logicRule) {
    this.logicRule = logicRule;
  }

  public void setLogicProperty(final Boolean logicProperty) {
    this.logicProperty = logicProperty;
  }

  public void setDefaultTime(final Boolean defaultTime) {
    this.defaultTime = defaultTime;
  }

  public void setConfig(final String config) {
    this.config = config;
  }

  public void setSubPropertyList(final List<PropertyDTO> subPropertyList) {
    this.subPropertyList = subPropertyList;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof PropertyDTO)) {
      return false;
    }
    PropertyDTO other = (PropertyDTO) o;
    if (!other.canEqual(this)) {
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
    Object this$rangeId = getRangeId();
    Object other$rangeId = other.getRangeId();
    if (this$rangeId == null) {
      if (other$rangeId != null) {
        return false;
      }
    } else if (!this$rangeId.equals(other$rangeId)) {
      return false;
    }
    Object this$rangeEntityId = getRangeEntityId();
    Object other$rangeEntityId = other.getRangeEntityId();
    if (this$rangeEntityId == null) {
      if (other$rangeEntityId != null) {
        return false;
      }
    } else if (!this$rangeEntityId.equals(other$rangeEntityId)) {
      return false;
    }
    Object this$spreadable = getSpreadable();
    Object other$spreadable = other.getSpreadable();
    if (this$spreadable == null) {
      if (other$spreadable != null) {
        return false;
      }
    } else if (!this$spreadable.equals(other$spreadable)) {
      return false;
    }
    Object this$propertySource = getPropertySource();
    Object other$propertySource = other.getPropertySource();
    if (this$propertySource == null) {
      if (other$propertySource != null) {
        return false;
      }
    } else if (!this$propertySource.equals(other$propertySource)) {
      return false;
    }
    Object this$aggregation = getAggregation();
    Object other$aggregation = other.getAggregation();
    if (this$aggregation == null) {
      if (other$aggregation != null) {
        return false;
      }
    } else if (!this$aggregation.equals(other$aggregation)) {
      return false;
    }
    Object this$logicProperty = getLogicProperty();
    Object other$logicProperty = other.getLogicProperty();
    if (this$logicProperty == null) {
      if (other$logicProperty != null) {
        return false;
      }
    } else if (!this$logicProperty.equals(other$logicProperty)) {
      return false;
    }
    Object this$defaultTime = getDefaultTime();
    Object other$defaultTime = other.getDefaultTime();
    if (this$defaultTime == null) {
      if (other$defaultTime != null) {
        return false;
      }
    } else if (!this$defaultTime.equals(other$defaultTime)) {
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
    Object this$description = getDescription();
    Object other$description = other.getDescription();
    if (this$description == null) {
      if (other$description != null) {
        return false;
      }
    } else if (!this$description.equals(other$description)) {
      return false;
    }
    Object this$rangeName = getRangeName();
    Object other$rangeName = other.getRangeName();
    if (this$rangeName == null) {
      if (other$rangeName != null) {
        return false;
      }
    } else if (!this$rangeName.equals(other$rangeName)) {
      return false;
    }
    Object this$rangeNameZh = getRangeNameZh();
    Object other$rangeNameZh = other.getRangeNameZh();
    if (this$rangeNameZh == null) {
      if (other$rangeNameZh != null) {
        return false;
      }
    } else if (!this$rangeNameZh.equals(other$rangeNameZh)) {
      return false;
    }
    Object this$rangeEntityName = getRangeEntityName();
    Object other$rangeEntityName = other.getRangeEntityName();
    if (this$rangeEntityName == null) {
      if (other$rangeEntityName != null) {
        return false;
      }
    } else if (!this$rangeEntityName.equals(other$rangeEntityName)) {
      return false;
    }
    Object this$rangeEntityNameZh = getRangeEntityNameZh();
    Object other$rangeEntityNameZh = other.getRangeEntityNameZh();
    if (this$rangeEntityNameZh == null) {
      if (other$rangeEntityNameZh != null) {
        return false;
      }
    } else if (!this$rangeEntityNameZh.equals(other$rangeEntityNameZh)) {
      return false;
    }
    Object this$propertyToRelationName = getPropertyToRelationName();
    Object other$propertyToRelationName = other.getPropertyToRelationName();
    if (this$propertyToRelationName == null) {
      if (other$propertyToRelationName != null) {
        return false;
      }
    } else if (!this$propertyToRelationName.equals(other$propertyToRelationName)) {
      return false;
    }
    Object this$propertyToRelationNameZh = getPropertyToRelationNameZh();
    Object other$propertyToRelationNameZh = other.getPropertyToRelationNameZh();
    if (this$propertyToRelationNameZh == null) {
      if (other$propertyToRelationNameZh != null) {
        return false;
      }
    } else if (!this$propertyToRelationNameZh.equals(other$propertyToRelationNameZh)) {
      return false;
    }
    Object this$propertyCategoryEnum = getPropertyCategoryEnum();
    Object other$propertyCategoryEnum = other.getPropertyCategoryEnum();
    if (this$propertyCategoryEnum == null) {
      if (other$propertyCategoryEnum != null) {
        return false;
      }
    } else if (!this$propertyCategoryEnum.equals(other$propertyCategoryEnum)) {
      return false;
    }
    Object this$propertyTagEnum = getPropertyTagEnum();
    Object other$propertyTagEnum = other.getPropertyTagEnum();
    if (this$propertyTagEnum == null) {
      if (other$propertyTagEnum != null) {
        return false;
      }
    } else if (!this$propertyTagEnum.equals(other$propertyTagEnum)) {
      return false;
    }
    Object this$maskType = getMaskType();
    Object other$maskType = other.getMaskType();
    if (this$maskType == null) {
      if (other$maskType != null) {
        return false;
      }
    } else if (!this$maskType.equals(other$maskType)) {
      return false;
    }
    Object this$constraints = getConstraints();
    Object other$constraints = other.getConstraints();
    if (this$constraints == null) {
      if (other$constraints != null) {
        return false;
      }
    } else if (!this$constraints.equals(other$constraints)) {
      return false;
    }
    Object this$propertySourceName = getPropertySourceName();
    Object other$propertySourceName = other.getPropertySourceName();
    if (this$propertySourceName == null) {
      if (other$propertySourceName != null) {
        return false;
      }
    } else if (!this$propertySourceName.equals(other$propertySourceName)) {
      return false;
    }
    Object this$extInfo = getExtInfo();
    Object other$extInfo = other.getExtInfo();
    if (this$extInfo == null) {
      if (other$extInfo != null) {
        return false;
      }
    } else if (!this$extInfo.equals(other$extInfo)) {
      return false;
    }
    Object this$propType = getPropType();
    Object other$propType = other.getPropType();
    if (this$propType == null) {
      if (other$propType != null) {
        return false;
      }
    } else if (!this$propType.equals(other$propType)) {
      return false;
    }
    Object this$valueTag = getValueTag();
    Object other$valueTag = other.getValueTag();
    if (this$valueTag == null) {
      if (other$valueTag != null) {
        return false;
      }
    } else if (!this$valueTag.equals(other$valueTag)) {
      return false;
    }
    Object this$logicRule = getLogicRule();
    Object other$logicRule = other.getLogicRule();
    if (this$logicRule == null) {
      if (other$logicRule != null) {
        return false;
      }
    } else if (!this$logicRule.equals(other$logicRule)) {
      return false;
    }
    Object this$config = getConfig();
    Object other$config = other.getConfig();
    if (this$config == null) {
      if (other$config != null) {
        return false;
      }
    } else if (!this$config.equals(other$config)) {
      return false;
    }
    Object this$subPropertyList = getSubPropertyList();
    Object other$subPropertyList = other.getSubPropertyList();
    return this$subPropertyList == null
        ? other$subPropertyList == null
        : this$subPropertyList.equals(other$subPropertyList);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof PropertyDTO;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $rangeId = getRangeId();
    int result2 = (result * 59) + ($rangeId == null ? 43 : $rangeId.hashCode());
    Object $rangeEntityId = getRangeEntityId();
    int result3 = (result2 * 59) + ($rangeEntityId == null ? 43 : $rangeEntityId.hashCode());
    Object $spreadable = getSpreadable();
    int result4 = (result3 * 59) + ($spreadable == null ? 43 : $spreadable.hashCode());
    Object $propertySource = getPropertySource();
    int result5 = (result4 * 59) + ($propertySource == null ? 43 : $propertySource.hashCode());
    Object $aggregation = getAggregation();
    int result6 = (result5 * 59) + ($aggregation == null ? 43 : $aggregation.hashCode());
    Object $logicProperty = getLogicProperty();
    int result7 = (result6 * 59) + ($logicProperty == null ? 43 : $logicProperty.hashCode());
    Object $defaultTime = getDefaultTime();
    int result8 = (result7 * 59) + ($defaultTime == null ? 43 : $defaultTime.hashCode());
    Object $name = getName();
    int result9 = (result8 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result10 = (result9 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $description = getDescription();
    int result11 = (result10 * 59) + ($description == null ? 43 : $description.hashCode());
    Object $rangeName = getRangeName();
    int result12 = (result11 * 59) + ($rangeName == null ? 43 : $rangeName.hashCode());
    Object $rangeNameZh = getRangeNameZh();
    int result13 = (result12 * 59) + ($rangeNameZh == null ? 43 : $rangeNameZh.hashCode());
    Object $rangeEntityName = getRangeEntityName();
    int result14 = (result13 * 59) + ($rangeEntityName == null ? 43 : $rangeEntityName.hashCode());
    Object $rangeEntityNameZh = getRangeEntityNameZh();
    int result15 =
        (result14 * 59) + ($rangeEntityNameZh == null ? 43 : $rangeEntityNameZh.hashCode());
    Object $propertyToRelationName = getPropertyToRelationName();
    int result16 =
        (result15 * 59)
            + ($propertyToRelationName == null ? 43 : $propertyToRelationName.hashCode());
    Object $propertyToRelationNameZh = getPropertyToRelationNameZh();
    int result17 =
        (result16 * 59)
            + ($propertyToRelationNameZh == null ? 43 : $propertyToRelationNameZh.hashCode());
    Object $propertyCategoryEnum = getPropertyCategoryEnum();
    int result18 =
        (result17 * 59) + ($propertyCategoryEnum == null ? 43 : $propertyCategoryEnum.hashCode());
    Object $propertyTagEnum = getPropertyTagEnum();
    int result19 = (result18 * 59) + ($propertyTagEnum == null ? 43 : $propertyTagEnum.hashCode());
    Object $maskType = getMaskType();
    int result20 = (result19 * 59) + ($maskType == null ? 43 : $maskType.hashCode());
    Object $constraints = getConstraints();
    int result21 = (result20 * 59) + ($constraints == null ? 43 : $constraints.hashCode());
    Object $propertySourceName = getPropertySourceName();
    int result22 =
        (result21 * 59) + ($propertySourceName == null ? 43 : $propertySourceName.hashCode());
    Object $extInfo = getExtInfo();
    int result23 = (result22 * 59) + ($extInfo == null ? 43 : $extInfo.hashCode());
    Object $propType = getPropType();
    int result24 = (result23 * 59) + ($propType == null ? 43 : $propType.hashCode());
    Object $valueTag = getValueTag();
    int result25 = (result24 * 59) + ($valueTag == null ? 43 : $valueTag.hashCode());
    Object $logicRule = getLogicRule();
    int result26 = (result25 * 59) + ($logicRule == null ? 43 : $logicRule.hashCode());
    Object $config = getConfig();
    int result27 = (result26 * 59) + ($config == null ? 43 : $config.hashCode());
    Object $subPropertyList = getSubPropertyList();
    return (result27 * 59) + ($subPropertyList == null ? 43 : $subPropertyList.hashCode());
  }

  public String toString() {
    return "PropertyDTO(id="
        + getId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", description="
        + getDescription()
        + ", rangeId="
        + getRangeId()
        + ", rangeName="
        + getRangeName()
        + ", rangeNameZh="
        + getRangeNameZh()
        + ", rangeEntityId="
        + getRangeEntityId()
        + ", rangeEntityName="
        + getRangeEntityName()
        + ", rangeEntityNameZh="
        + getRangeEntityNameZh()
        + ", propertyToRelationName="
        + getPropertyToRelationName()
        + ", propertyToRelationNameZh="
        + getPropertyToRelationNameZh()
        + ", propertyCategoryEnum="
        + getPropertyCategoryEnum()
        + ", propertyTagEnum="
        + getPropertyTagEnum()
        + ", spreadable="
        + getSpreadable()
        + ", maskType="
        + getMaskType()
        + ", constraints="
        + getConstraints()
        + ", propertySource="
        + getPropertySource()
        + ", propertySourceName="
        + getPropertySourceName()
        + ", aggregation="
        + getAggregation()
        + ", extInfo="
        + getExtInfo()
        + ", propType="
        + getPropType()
        + ", valueTag="
        + getValueTag()
        + ", logicRule="
        + getLogicRule()
        + ", logicProperty="
        + getLogicProperty()
        + ", defaultTime="
        + getDefaultTime()
        + ", config="
        + getConfig()
        + ", subPropertyList="
        + getSubPropertyList()
        + ")";
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }

  public String getDescription() {
    return this.description;
  }

  public Long getRangeId() {
    return this.rangeId;
  }

  public String getRangeName() {
    return this.rangeName;
  }

  public String getRangeNameZh() {
    return this.rangeNameZh;
  }

  public Long getRangeEntityId() {
    return this.rangeEntityId;
  }

  public String getRangeEntityName() {
    return this.rangeEntityName;
  }

  public String getRangeEntityNameZh() {
    return this.rangeEntityNameZh;
  }

  public String getPropertyToRelationName() {
    return this.propertyToRelationName;
  }

  public String getPropertyToRelationNameZh() {
    return this.propertyToRelationNameZh;
  }

  public String getPropertyCategoryEnum() {
    return this.propertyCategoryEnum;
  }

  public String getPropertyTagEnum() {
    return this.propertyTagEnum;
  }

  public Boolean getSpreadable() {
    return this.spreadable;
  }

  public String getMaskType() {
    return this.maskType;
  }

  public List<AttrConsDTO> getConstraints() {
    return this.constraints;
  }

  public Long getPropertySource() {
    return this.propertySource;
  }

  public String getPropertySourceName() {
    return this.propertySourceName;
  }

  public Boolean getAggregation() {
    return this.aggregation;
  }

  public Map<String, Object> getExtInfo() {
    return this.extInfo;
  }

  public String getPropType() {
    return this.propType;
  }

  public String getValueTag() {
    return this.valueTag;
  }

  public LogicRuleDTO getLogicRule() {
    return this.logicRule;
  }

  public Boolean getLogicProperty() {
    return this.logicProperty;
  }

  public Boolean getDefaultTime() {
    return this.defaultTime;
  }

  public String getConfig() {
    return this.config;
  }

  public List<PropertyDTO> getSubPropertyList() {
    return this.subPropertyList;
  }
}
