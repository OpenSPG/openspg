package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/EntityTypeDTO.class */
public class EntityTypeDTO extends BaseDTO implements Serializable {
  private static final long serialVersionUID = -5388525486345368694L;
  private Long id;
  private String name;
  private String nameZh;
  private String description;
  private List<PropertyDTO> inheritedPropertyList = Lists.newArrayList();
  private List<PropertyDTO> propertyList = Lists.newArrayList();
  private List<RelationTypeDTO> relations = new ArrayList(2);
  private Long parentId;
  private String parentName;
  private Long belongToProject;
  private String belongToProjectName;
  private Date createTime;
  private Date modifiedTime;
  private String entityCategory;
  private String category;
  private List<AttrConsDTO> constraints;
  private String lockStatus;
  private String refSource;
  private String yuQueUrl;
  private List<String> subGraphs;
  private String examplePk;
  private Boolean wellChosen;
  private Map<String, Object> extInfo;
  private String groupName;
  private List<String> typeOwner;

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

  public void setInheritedPropertyList(final List<PropertyDTO> inheritedPropertyList) {
    this.inheritedPropertyList = inheritedPropertyList;
  }

  public void setPropertyList(final List<PropertyDTO> propertyList) {
    this.propertyList = propertyList;
  }

  public void setRelations(final List<RelationTypeDTO> relations) {
    this.relations = relations;
  }

  public void setParentId(final Long parentId) {
    this.parentId = parentId;
  }

  public void setParentName(final String parentName) {
    this.parentName = parentName;
  }

  public void setBelongToProject(final Long belongToProject) {
    this.belongToProject = belongToProject;
  }

  public void setBelongToProjectName(final String belongToProjectName) {
    this.belongToProjectName = belongToProjectName;
  }

  public void setCreateTime(final Date createTime) {
    this.createTime = createTime;
  }

  public void setModifiedTime(final Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public void setEntityCategory(final String entityCategory) {
    this.entityCategory = entityCategory;
  }

  public void setCategory(final String category) {
    this.category = category;
  }

  public void setConstraints(final List<AttrConsDTO> constraints) {
    this.constraints = constraints;
  }

  public void setLockStatus(final String lockStatus) {
    this.lockStatus = lockStatus;
  }

  public void setRefSource(final String refSource) {
    this.refSource = refSource;
  }

  public void setYuQueUrl(final String yuQueUrl) {
    this.yuQueUrl = yuQueUrl;
  }

  public void setSubGraphs(final List<String> subGraphs) {
    this.subGraphs = subGraphs;
  }

  public void setExamplePk(final String examplePk) {
    this.examplePk = examplePk;
  }

  public void setWellChosen(final Boolean wellChosen) {
    this.wellChosen = wellChosen;
  }

  public void setExtInfo(final Map<String, Object> extInfo) {
    this.extInfo = extInfo;
  }

  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }

  public void setTypeOwner(final List<String> typeOwner) {
    this.typeOwner = typeOwner;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof EntityTypeDTO)) {
      return false;
    }
    EntityTypeDTO other = (EntityTypeDTO) o;
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
    Object this$parentId = getParentId();
    Object other$parentId = other.getParentId();
    if (this$parentId == null) {
      if (other$parentId != null) {
        return false;
      }
    } else if (!this$parentId.equals(other$parentId)) {
      return false;
    }
    Object this$belongToProject = getBelongToProject();
    Object other$belongToProject = other.getBelongToProject();
    if (this$belongToProject == null) {
      if (other$belongToProject != null) {
        return false;
      }
    } else if (!this$belongToProject.equals(other$belongToProject)) {
      return false;
    }
    Object this$wellChosen = getWellChosen();
    Object other$wellChosen = other.getWellChosen();
    if (this$wellChosen == null) {
      if (other$wellChosen != null) {
        return false;
      }
    } else if (!this$wellChosen.equals(other$wellChosen)) {
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
    Object this$inheritedPropertyList = getInheritedPropertyList();
    Object other$inheritedPropertyList = other.getInheritedPropertyList();
    if (this$inheritedPropertyList == null) {
      if (other$inheritedPropertyList != null) {
        return false;
      }
    } else if (!this$inheritedPropertyList.equals(other$inheritedPropertyList)) {
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
    Object this$relations = getRelations();
    Object other$relations = other.getRelations();
    if (this$relations == null) {
      if (other$relations != null) {
        return false;
      }
    } else if (!this$relations.equals(other$relations)) {
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
    Object this$belongToProjectName = getBelongToProjectName();
    Object other$belongToProjectName = other.getBelongToProjectName();
    if (this$belongToProjectName == null) {
      if (other$belongToProjectName != null) {
        return false;
      }
    } else if (!this$belongToProjectName.equals(other$belongToProjectName)) {
      return false;
    }
    Object this$createTime = getCreateTime();
    Object other$createTime = other.getCreateTime();
    if (this$createTime == null) {
      if (other$createTime != null) {
        return false;
      }
    } else if (!this$createTime.equals(other$createTime)) {
      return false;
    }
    Object this$modifiedTime = getModifiedTime();
    Object other$modifiedTime = other.getModifiedTime();
    if (this$modifiedTime == null) {
      if (other$modifiedTime != null) {
        return false;
      }
    } else if (!this$modifiedTime.equals(other$modifiedTime)) {
      return false;
    }
    Object this$entityCategory = getEntityCategory();
    Object other$entityCategory = other.getEntityCategory();
    if (this$entityCategory == null) {
      if (other$entityCategory != null) {
        return false;
      }
    } else if (!this$entityCategory.equals(other$entityCategory)) {
      return false;
    }
    Object this$category = getCategory();
    Object other$category = other.getCategory();
    if (this$category == null) {
      if (other$category != null) {
        return false;
      }
    } else if (!this$category.equals(other$category)) {
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
    Object this$lockStatus = getLockStatus();
    Object other$lockStatus = other.getLockStatus();
    if (this$lockStatus == null) {
      if (other$lockStatus != null) {
        return false;
      }
    } else if (!this$lockStatus.equals(other$lockStatus)) {
      return false;
    }
    Object this$refSource = getRefSource();
    Object other$refSource = other.getRefSource();
    if (this$refSource == null) {
      if (other$refSource != null) {
        return false;
      }
    } else if (!this$refSource.equals(other$refSource)) {
      return false;
    }
    Object this$yuQueUrl = getYuQueUrl();
    Object other$yuQueUrl = other.getYuQueUrl();
    if (this$yuQueUrl == null) {
      if (other$yuQueUrl != null) {
        return false;
      }
    } else if (!this$yuQueUrl.equals(other$yuQueUrl)) {
      return false;
    }
    Object this$subGraphs = getSubGraphs();
    Object other$subGraphs = other.getSubGraphs();
    if (this$subGraphs == null) {
      if (other$subGraphs != null) {
        return false;
      }
    } else if (!this$subGraphs.equals(other$subGraphs)) {
      return false;
    }
    Object this$examplePk = getExamplePk();
    Object other$examplePk = other.getExamplePk();
    if (this$examplePk == null) {
      if (other$examplePk != null) {
        return false;
      }
    } else if (!this$examplePk.equals(other$examplePk)) {
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
    Object this$groupName = getGroupName();
    Object other$groupName = other.getGroupName();
    if (this$groupName == null) {
      if (other$groupName != null) {
        return false;
      }
    } else if (!this$groupName.equals(other$groupName)) {
      return false;
    }
    Object this$typeOwner = getTypeOwner();
    Object other$typeOwner = other.getTypeOwner();
    return this$typeOwner == null
        ? other$typeOwner == null
        : this$typeOwner.equals(other$typeOwner);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof EntityTypeDTO;
  }

  public int hashCode() {
    Object $id = getId();
    int result = (1 * 59) + ($id == null ? 43 : $id.hashCode());
    Object $parentId = getParentId();
    int result2 = (result * 59) + ($parentId == null ? 43 : $parentId.hashCode());
    Object $belongToProject = getBelongToProject();
    int result3 = (result2 * 59) + ($belongToProject == null ? 43 : $belongToProject.hashCode());
    Object $wellChosen = getWellChosen();
    int result4 = (result3 * 59) + ($wellChosen == null ? 43 : $wellChosen.hashCode());
    Object $name = getName();
    int result5 = (result4 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result6 = (result5 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $description = getDescription();
    int result7 = (result6 * 59) + ($description == null ? 43 : $description.hashCode());
    Object $inheritedPropertyList = getInheritedPropertyList();
    int result8 =
        (result7 * 59) + ($inheritedPropertyList == null ? 43 : $inheritedPropertyList.hashCode());
    Object $propertyList = getPropertyList();
    int result9 = (result8 * 59) + ($propertyList == null ? 43 : $propertyList.hashCode());
    Object $relations = getRelations();
    int result10 = (result9 * 59) + ($relations == null ? 43 : $relations.hashCode());
    Object $parentName = getParentName();
    int result11 = (result10 * 59) + ($parentName == null ? 43 : $parentName.hashCode());
    Object $belongToProjectName = getBelongToProjectName();
    int result12 =
        (result11 * 59) + ($belongToProjectName == null ? 43 : $belongToProjectName.hashCode());
    Object $createTime = getCreateTime();
    int result13 = (result12 * 59) + ($createTime == null ? 43 : $createTime.hashCode());
    Object $modifiedTime = getModifiedTime();
    int result14 = (result13 * 59) + ($modifiedTime == null ? 43 : $modifiedTime.hashCode());
    Object $entityCategory = getEntityCategory();
    int result15 = (result14 * 59) + ($entityCategory == null ? 43 : $entityCategory.hashCode());
    Object $category = getCategory();
    int result16 = (result15 * 59) + ($category == null ? 43 : $category.hashCode());
    Object $constraints = getConstraints();
    int result17 = (result16 * 59) + ($constraints == null ? 43 : $constraints.hashCode());
    Object $lockStatus = getLockStatus();
    int result18 = (result17 * 59) + ($lockStatus == null ? 43 : $lockStatus.hashCode());
    Object $refSource = getRefSource();
    int result19 = (result18 * 59) + ($refSource == null ? 43 : $refSource.hashCode());
    Object $yuQueUrl = getYuQueUrl();
    int result20 = (result19 * 59) + ($yuQueUrl == null ? 43 : $yuQueUrl.hashCode());
    Object $subGraphs = getSubGraphs();
    int result21 = (result20 * 59) + ($subGraphs == null ? 43 : $subGraphs.hashCode());
    Object $examplePk = getExamplePk();
    int result22 = (result21 * 59) + ($examplePk == null ? 43 : $examplePk.hashCode());
    Object $extInfo = getExtInfo();
    int result23 = (result22 * 59) + ($extInfo == null ? 43 : $extInfo.hashCode());
    Object $groupName = getGroupName();
    int result24 = (result23 * 59) + ($groupName == null ? 43 : $groupName.hashCode());
    Object $typeOwner = getTypeOwner();
    return (result24 * 59) + ($typeOwner == null ? 43 : $typeOwner.hashCode());
  }

  public String toString() {
    return "EntityTypeDTO(id="
        + getId()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", description="
        + getDescription()
        + ", inheritedPropertyList="
        + getInheritedPropertyList()
        + ", propertyList="
        + getPropertyList()
        + ", relations="
        + getRelations()
        + ", parentId="
        + getParentId()
        + ", parentName="
        + getParentName()
        + ", belongToProject="
        + getBelongToProject()
        + ", belongToProjectName="
        + getBelongToProjectName()
        + ", createTime="
        + getCreateTime()
        + ", modifiedTime="
        + getModifiedTime()
        + ", entityCategory="
        + getEntityCategory()
        + ", category="
        + getCategory()
        + ", constraints="
        + getConstraints()
        + ", lockStatus="
        + getLockStatus()
        + ", refSource="
        + getRefSource()
        + ", yuQueUrl="
        + getYuQueUrl()
        + ", subGraphs="
        + getSubGraphs()
        + ", examplePk="
        + getExamplePk()
        + ", wellChosen="
        + getWellChosen()
        + ", extInfo="
        + getExtInfo()
        + ", groupName="
        + getGroupName()
        + ", typeOwner="
        + getTypeOwner()
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

  public List<PropertyDTO> getInheritedPropertyList() {
    return this.inheritedPropertyList;
  }

  public List<PropertyDTO> getPropertyList() {
    return this.propertyList;
  }

  public List<RelationTypeDTO> getRelations() {
    return this.relations;
  }

  public Long getParentId() {
    return this.parentId;
  }

  public String getParentName() {
    return this.parentName;
  }

  public Long getBelongToProject() {
    return this.belongToProject;
  }

  public String getBelongToProjectName() {
    return this.belongToProjectName;
  }

  public Date getCreateTime() {
    return this.createTime;
  }

  public Date getModifiedTime() {
    return this.modifiedTime;
  }

  public String getEntityCategory() {
    return this.entityCategory;
  }

  public String getCategory() {
    return this.category;
  }

  public List<AttrConsDTO> getConstraints() {
    return this.constraints;
  }

  public String getLockStatus() {
    return this.lockStatus;
  }

  public String getRefSource() {
    return this.refSource;
  }

  public String getYuQueUrl() {
    return this.yuQueUrl;
  }

  public List<String> getSubGraphs() {
    return this.subGraphs;
  }

  public String getExamplePk() {
    return this.examplePk;
  }

  public Boolean getWellChosen() {
    return this.wellChosen;
  }

  public Map<String, Object> getExtInfo() {
    return this.extInfo;
  }

  public String getGroupName() {
    return this.groupName;
  }

  public List<String> getTypeOwner() {
    return this.typeOwner;
  }

  public EntityTypeDTO() {}

  public EntityTypeDTO(String name, String nameZh, Long id) {
    this.name = name;
    this.nameZh = nameZh;
    this.id = id;
  }
}
