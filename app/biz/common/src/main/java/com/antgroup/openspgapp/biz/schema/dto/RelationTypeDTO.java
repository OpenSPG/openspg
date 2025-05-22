package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/RelationTypeDTO.class */
public class RelationTypeDTO extends BaseDTO implements Serializable {
  private Long id;
  private String name;
  private String nameZh;
  private String description;
  private Long originBelongToProject;
  private EntityTypeDTO startEntity;
  private EntityTypeDTO endEntity;
  private String relationDirectionEnumCode;
  private EntityTypeDTO startEntityTypeDetail;
  private EntityTypeDTO endEntityTypeDetail;
  private Long relationSource;
  private RelationTypeDTO onlineRelationTypeDetail;
  private String relationCategory;
  private String config;
  private LogicRuleDTO logicRuleDTO;
  private List<PropertyDTO> propertyList = Lists.newArrayList();
  private Map<String, Object> extInfo = new HashMap();

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

  public void setOriginBelongToProject(final Long originBelongToProject) {
    this.originBelongToProject = originBelongToProject;
  }

  public void setStartEntity(final EntityTypeDTO startEntity) {
    this.startEntity = startEntity;
  }

  public void setEndEntity(final EntityTypeDTO endEntity) {
    this.endEntity = endEntity;
  }

  public void setRelationDirectionEnumCode(final String relationDirectionEnumCode) {
    this.relationDirectionEnumCode = relationDirectionEnumCode;
  }

  public void setStartEntityTypeDetail(final EntityTypeDTO startEntityTypeDetail) {
    this.startEntityTypeDetail = startEntityTypeDetail;
  }

  public void setEndEntityTypeDetail(final EntityTypeDTO endEntityTypeDetail) {
    this.endEntityTypeDetail = endEntityTypeDetail;
  }

  public void setPropertyList(final List<PropertyDTO> propertyList) {
    this.propertyList = propertyList;
  }

  public void setRelationSource(final Long relationSource) {
    this.relationSource = relationSource;
  }

  public void setOnlineRelationTypeDetail(final RelationTypeDTO onlineRelationTypeDetail) {
    this.onlineRelationTypeDetail = onlineRelationTypeDetail;
  }

  public void setRelationCategory(final String relationCategory) {
    this.relationCategory = relationCategory;
  }

  public void setConfig(final String config) {
    this.config = config;
  }

  public void setExtInfo(final Map<String, Object> extInfo) {
    this.extInfo = extInfo;
  }

  public void setLogicRuleDTO(final LogicRuleDTO logicRuleDTO) {
    this.logicRuleDTO = logicRuleDTO;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof RelationTypeDTO)) {
      return false;
    }
    RelationTypeDTO other = (RelationTypeDTO) o;
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
    Object this$originBelongToProject = getOriginBelongToProject();
    Object other$originBelongToProject = other.getOriginBelongToProject();
    if (this$originBelongToProject == null) {
      if (other$originBelongToProject != null) {
        return false;
      }
    } else if (!this$originBelongToProject.equals(other$originBelongToProject)) {
      return false;
    }
    Object this$relationSource = getRelationSource();
    Object other$relationSource = other.getRelationSource();
    if (this$relationSource == null) {
      if (other$relationSource != null) {
        return false;
      }
    } else if (!this$relationSource.equals(other$relationSource)) {
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
    Object this$startEntity = getStartEntity();
    Object other$startEntity = other.getStartEntity();
    if (this$startEntity == null) {
      if (other$startEntity != null) {
        return false;
      }
    } else if (!this$startEntity.equals(other$startEntity)) {
      return false;
    }
    Object this$endEntity = getEndEntity();
    Object other$endEntity = other.getEndEntity();
    if (this$endEntity == null) {
      if (other$endEntity != null) {
        return false;
      }
    } else if (!this$endEntity.equals(other$endEntity)) {
      return false;
    }
    Object this$relationDirectionEnumCode = getRelationDirectionEnumCode();
    Object other$relationDirectionEnumCode = other.getRelationDirectionEnumCode();
    if (this$relationDirectionEnumCode == null) {
      if (other$relationDirectionEnumCode != null) {
        return false;
      }
    } else if (!this$relationDirectionEnumCode.equals(other$relationDirectionEnumCode)) {
      return false;
    }
    Object this$startEntityTypeDetail = getStartEntityTypeDetail();
    Object other$startEntityTypeDetail = other.getStartEntityTypeDetail();
    if (this$startEntityTypeDetail == null) {
      if (other$startEntityTypeDetail != null) {
        return false;
      }
    } else if (!this$startEntityTypeDetail.equals(other$startEntityTypeDetail)) {
      return false;
    }
    Object this$endEntityTypeDetail = getEndEntityTypeDetail();
    Object other$endEntityTypeDetail = other.getEndEntityTypeDetail();
    if (this$endEntityTypeDetail == null) {
      if (other$endEntityTypeDetail != null) {
        return false;
      }
    } else if (!this$endEntityTypeDetail.equals(other$endEntityTypeDetail)) {
      return false;
    }
    Object this$propertyList = getPropertyList();
    Object other$propertyList = other.getPropertyList();
    if (this$propertyList == null) {
      if (other$propertyList != null) {
        return false;
      }
    } else if (!this$propertyList.equals(other$propertyList)) {
      return false;
    }
    Object this$onlineRelationTypeDetail = getOnlineRelationTypeDetail();
    Object other$onlineRelationTypeDetail = other.getOnlineRelationTypeDetail();
    if (this$onlineRelationTypeDetail == null) {
      if (other$onlineRelationTypeDetail != null) {
        return false;
      }
    } else if (!this$onlineRelationTypeDetail.equals(other$onlineRelationTypeDetail)) {
      return false;
    }
    Object this$relationCategory = getRelationCategory();
    Object other$relationCategory = other.getRelationCategory();
    if (this$relationCategory == null) {
      if (other$relationCategory != null) {
        return false;
      }
    } else if (!this$relationCategory.equals(other$relationCategory)) {
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
    Object this$extInfo = getExtInfo();
    Object other$extInfo = other.getExtInfo();
    if (this$extInfo == null) {
      if (other$extInfo != null) {
        return false;
      }
    } else if (!this$extInfo.equals(other$extInfo)) {
      return false;
    }
    Object this$logicRuleDTO = getLogicRuleDTO();
    Object other$logicRuleDTO = other.getLogicRuleDTO();
    return this$logicRuleDTO == null
        ? other$logicRuleDTO == null
        : this$logicRuleDTO.equals(other$logicRuleDTO);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof RelationTypeDTO;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $originBelongToProject = getOriginBelongToProject();
    int result2 =
        (result * 59) + ($originBelongToProject == null ? 43 : $originBelongToProject.hashCode());
    Object $relationSource = getRelationSource();
    int result3 = (result2 * 59) + ($relationSource == null ? 43 : $relationSource.hashCode());
    Object $name = getName();
    int result4 = (result3 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result5 = (result4 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $description = getDescription();
    int result6 = (result5 * 59) + ($description == null ? 43 : $description.hashCode());
    Object $startEntity = getStartEntity();
    int result7 = (result6 * 59) + ($startEntity == null ? 43 : $startEntity.hashCode());
    Object $endEntity = getEndEntity();
    int result8 = (result7 * 59) + ($endEntity == null ? 43 : $endEntity.hashCode());
    Object $relationDirectionEnumCode = getRelationDirectionEnumCode();
    int result9 =
        (result8 * 59)
            + ($relationDirectionEnumCode == null ? 43 : $relationDirectionEnumCode.hashCode());
    Object $startEntityTypeDetail = getStartEntityTypeDetail();
    int result10 =
        (result9 * 59) + ($startEntityTypeDetail == null ? 43 : $startEntityTypeDetail.hashCode());
    Object $endEntityTypeDetail = getEndEntityTypeDetail();
    int result11 =
        (result10 * 59) + ($endEntityTypeDetail == null ? 43 : $endEntityTypeDetail.hashCode());
    Object $propertyList = getPropertyList();
    int result12 = (result11 * 59) + ($propertyList == null ? 43 : $propertyList.hashCode());
    Object $onlineRelationTypeDetail = getOnlineRelationTypeDetail();
    int result13 =
        (result12 * 59)
            + ($onlineRelationTypeDetail == null ? 43 : $onlineRelationTypeDetail.hashCode());
    Object $relationCategory = getRelationCategory();
    int result14 =
        (result13 * 59) + ($relationCategory == null ? 43 : $relationCategory.hashCode());
    Object $config = getConfig();
    int result15 = (result14 * 59) + ($config == null ? 43 : $config.hashCode());
    Object $extInfo = getExtInfo();
    int result16 = (result15 * 59) + ($extInfo == null ? 43 : $extInfo.hashCode());
    Object $logicRuleDTO = getLogicRuleDTO();
    return (result16 * 59) + ($logicRuleDTO == null ? 43 : $logicRuleDTO.hashCode());
  }

  public String toString() {
    return "RelationTypeDTO(id="
        + getId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", description="
        + getDescription()
        + ", originBelongToProject="
        + getOriginBelongToProject()
        + ", startEntity="
        + getStartEntity()
        + ", endEntity="
        + getEndEntity()
        + ", relationDirectionEnumCode="
        + getRelationDirectionEnumCode()
        + ", startEntityTypeDetail="
        + getStartEntityTypeDetail()
        + ", endEntityTypeDetail="
        + getEndEntityTypeDetail()
        + ", propertyList="
        + getPropertyList()
        + ", relationSource="
        + getRelationSource()
        + ", onlineRelationTypeDetail="
        + getOnlineRelationTypeDetail()
        + ", relationCategory="
        + getRelationCategory()
        + ", config="
        + getConfig()
        + ", extInfo="
        + getExtInfo()
        + ", logicRuleDTO="
        + getLogicRuleDTO()
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

  public Long getOriginBelongToProject() {
    return this.originBelongToProject;
  }

  public EntityTypeDTO getStartEntity() {
    return this.startEntity;
  }

  public EntityTypeDTO getEndEntity() {
    return this.endEntity;
  }

  public String getRelationDirectionEnumCode() {
    return this.relationDirectionEnumCode;
  }

  public EntityTypeDTO getStartEntityTypeDetail() {
    return this.startEntityTypeDetail;
  }

  public EntityTypeDTO getEndEntityTypeDetail() {
    return this.endEntityTypeDetail;
  }

  public List<PropertyDTO> getPropertyList() {
    return this.propertyList;
  }

  public Long getRelationSource() {
    return this.relationSource;
  }

  public RelationTypeDTO getOnlineRelationTypeDetail() {
    return this.onlineRelationTypeDetail;
  }

  public String getRelationCategory() {
    return this.relationCategory;
  }

  public String getConfig() {
    return this.config;
  }

  public Map<String, Object> getExtInfo() {
    return this.extInfo;
  }

  public LogicRuleDTO getLogicRuleDTO() {
    return this.logicRuleDTO;
  }
}
