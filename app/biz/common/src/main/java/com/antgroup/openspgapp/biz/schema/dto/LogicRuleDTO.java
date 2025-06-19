package com.antgroup.openspgapp.biz.schema.dto;

import com.antgroup.openspg.server.common.model.base.BaseDTO;
import java.io.Serializable;
import java.util.Date;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/dto/LogicRuleDTO.class */
public class LogicRuleDTO extends BaseDTO implements Serializable {
  private EntityTypeDTO startEntity;
  private ObjectType objectType;
  private String name;
  private String nameZh;
  private String resourceId;
  private String semanticType;
  private String ruleType;
  private String ruleId;
  private Integer version;
  private Boolean isMaster;
  private String expression;
  private String status;
  private String userNo;
  private String scope;
  private Date modifiedDate;

  public void setStartEntity(final EntityTypeDTO startEntity) {
    this.startEntity = startEntity;
  }

  public void setObjectType(final ObjectType objectType) {
    this.objectType = objectType;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public void setNameZh(final String nameZh) {
    this.nameZh = nameZh;
  }

  public void setResourceId(final String resourceId) {
    this.resourceId = resourceId;
  }

  public void setSemanticType(final String semanticType) {
    this.semanticType = semanticType;
  }

  public void setRuleType(final String ruleType) {
    this.ruleType = ruleType;
  }

  public void setRuleId(final String ruleId) {
    this.ruleId = ruleId;
  }

  public void setVersion(final Integer version) {
    this.version = version;
  }

  public void setIsMaster(final Boolean isMaster) {
    this.isMaster = isMaster;
  }

  public void setExpression(final String expression) {
    this.expression = expression;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public void setUserNo(final String userNo) {
    this.userNo = userNo;
  }

  public void setScope(final String scope) {
    this.scope = scope;
  }

  public void setModifiedDate(final Date modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof LogicRuleDTO)) {
      return false;
    }
    LogicRuleDTO other = (LogicRuleDTO) o;
    if (!other.canEqual(this)) {
      return false;
    }
    Object this$version = getVersion();
    Object other$version = other.getVersion();
    if (this$version == null) {
      if (other$version != null) {
        return false;
      }
    } else if (!this$version.equals(other$version)) {
      return false;
    }
    Object this$isMaster = getIsMaster();
    Object other$isMaster = other.getIsMaster();
    if (this$isMaster == null) {
      if (other$isMaster != null) {
        return false;
      }
    } else if (!this$isMaster.equals(other$isMaster)) {
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
    Object this$objectType = getObjectType();
    Object other$objectType = other.getObjectType();
    if (this$objectType == null) {
      if (other$objectType != null) {
        return false;
      }
    } else if (!this$objectType.equals(other$objectType)) {
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
    Object this$resourceId = getResourceId();
    Object other$resourceId = other.getResourceId();
    if (this$resourceId == null) {
      if (other$resourceId != null) {
        return false;
      }
    } else if (!this$resourceId.equals(other$resourceId)) {
      return false;
    }
    Object this$semanticType = getSemanticType();
    Object other$semanticType = other.getSemanticType();
    if (this$semanticType == null) {
      if (other$semanticType != null) {
        return false;
      }
    } else if (!this$semanticType.equals(other$semanticType)) {
      return false;
    }
    Object this$ruleType = getRuleType();
    Object other$ruleType = other.getRuleType();
    if (this$ruleType == null) {
      if (other$ruleType != null) {
        return false;
      }
    } else if (!this$ruleType.equals(other$ruleType)) {
      return false;
    }
    Object this$ruleId = getRuleId();
    Object other$ruleId = other.getRuleId();
    if (this$ruleId == null) {
      if (other$ruleId != null) {
        return false;
      }
    } else if (!this$ruleId.equals(other$ruleId)) {
      return false;
    }
    Object this$expression = getExpression();
    Object other$expression = other.getExpression();
    if (this$expression == null) {
      if (other$expression != null) {
        return false;
      }
    } else if (!this$expression.equals(other$expression)) {
      return false;
    }
    Object this$status = getStatus();
    Object other$status = other.getStatus();
    if (this$status == null) {
      if (other$status != null) {
        return false;
      }
    } else if (!this$status.equals(other$status)) {
      return false;
    }
    Object this$userNo = getUserNo();
    Object other$userNo = other.getUserNo();
    if (this$userNo == null) {
      if (other$userNo != null) {
        return false;
      }
    } else if (!this$userNo.equals(other$userNo)) {
      return false;
    }
    Object this$scope = getScope();
    Object other$scope = other.getScope();
    if (this$scope == null) {
      if (other$scope != null) {
        return false;
      }
    } else if (!this$scope.equals(other$scope)) {
      return false;
    }
    Object this$modifiedDate = getModifiedDate();
    Object other$modifiedDate = other.getModifiedDate();
    return this$modifiedDate == null
        ? other$modifiedDate == null
        : this$modifiedDate.equals(other$modifiedDate);
  }

  protected boolean canEqual(final Object other) {
    return other instanceof LogicRuleDTO;
  }

  public int hashCode() {
    Object $version = getVersion();
    int result = (1 * 59) + ($version == null ? 43 : $version.hashCode());
    Object $isMaster = getIsMaster();
    int result2 = (result * 59) + ($isMaster == null ? 43 : $isMaster.hashCode());
    Object $startEntity = getStartEntity();
    int result3 = (result2 * 59) + ($startEntity == null ? 43 : $startEntity.hashCode());
    Object $objectType = getObjectType();
    int result4 = (result3 * 59) + ($objectType == null ? 43 : $objectType.hashCode());
    Object $name = getName();
    int result5 = (result4 * 59) + ($name == null ? 43 : $name.hashCode());
    Object $nameZh = getNameZh();
    int result6 = (result5 * 59) + ($nameZh == null ? 43 : $nameZh.hashCode());
    Object $resourceId = getResourceId();
    int result7 = (result6 * 59) + ($resourceId == null ? 43 : $resourceId.hashCode());
    Object $semanticType = getSemanticType();
    int result8 = (result7 * 59) + ($semanticType == null ? 43 : $semanticType.hashCode());
    Object $ruleType = getRuleType();
    int result9 = (result8 * 59) + ($ruleType == null ? 43 : $ruleType.hashCode());
    Object $ruleId = getRuleId();
    int result10 = (result9 * 59) + ($ruleId == null ? 43 : $ruleId.hashCode());
    Object $expression = getExpression();
    int result11 = (result10 * 59) + ($expression == null ? 43 : $expression.hashCode());
    Object $status = getStatus();
    int result12 = (result11 * 59) + ($status == null ? 43 : $status.hashCode());
    Object $userNo = getUserNo();
    int result13 = (result12 * 59) + ($userNo == null ? 43 : $userNo.hashCode());
    Object $scope = getScope();
    int result14 = (result13 * 59) + ($scope == null ? 43 : $scope.hashCode());
    Object $modifiedDate = getModifiedDate();
    return (result14 * 59) + ($modifiedDate == null ? 43 : $modifiedDate.hashCode());
  }

  public String toString() {
    return "LogicRuleDTO(startEntity="
        + getStartEntity()
        + ", objectType="
        + getObjectType()
        + ", name="
        + getName()
        + ", nameZh="
        + getNameZh()
        + ", resourceId="
        + getResourceId()
        + ", semanticType="
        + getSemanticType()
        + ", ruleType="
        + getRuleType()
        + ", ruleId="
        + getRuleId()
        + ", version="
        + getVersion()
        + ", isMaster="
        + getIsMaster()
        + ", expression="
        + getExpression()
        + ", status="
        + getStatus()
        + ", userNo="
        + getUserNo()
        + ", scope="
        + getScope()
        + ", modifiedDate="
        + getModifiedDate()
        + ")";
  }

  public EntityTypeDTO getStartEntity() {
    return this.startEntity;
  }

  public ObjectType getObjectType() {
    return this.objectType;
  }

  public String getName() {
    return this.name;
  }

  public String getNameZh() {
    return this.nameZh;
  }

  public String getResourceId() {
    return this.resourceId;
  }

  public String getSemanticType() {
    return this.semanticType;
  }

  public String getRuleType() {
    return this.ruleType;
  }

  public String getRuleId() {
    return this.ruleId;
  }

  public Integer getVersion() {
    return this.version;
  }

  public Boolean getIsMaster() {
    return this.isMaster;
  }

  public String getExpression() {
    return this.expression;
  }

  public String getStatus() {
    return this.status;
  }

  public String getUserNo() {
    return this.userNo;
  }

  public String getScope() {
    return this.scope;
  }

  public Date getModifiedDate() {
    return this.modifiedDate;
  }
}
