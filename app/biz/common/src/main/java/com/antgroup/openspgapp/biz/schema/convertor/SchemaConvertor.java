package com.antgroup.openspgapp.biz.schema.convertor;

import com.antgroup.openspg.core.schema.model.SchemaExtInfo;
import com.antgroup.openspg.core.schema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.schema.model.constraint.ConstraintTypeEnum;
import com.antgroup.openspg.core.schema.model.constraint.EnumConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RangeConstraint;
import com.antgroup.openspg.core.schema.model.constraint.RegularConstraint;
import com.antgroup.openspg.core.schema.model.predicate.Property;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.predicate.SubProperty;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeRef;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.infra.dao.repository.schema.enums.ConstraintEnum;
import com.antgroup.openspgapp.biz.schema.dto.AttrConsDTO;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.PropertyDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;

/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/convertor/SchemaConvertor.class */
public class SchemaConvertor {
  public static List<EntityTypeDTO> toEntityTypeDTO(ProjectSchema projectSchema) {
    List<BaseSPGType> spgTypes = projectSchema.getSpgTypes();
    if (CollectionUtils.isEmpty(spgTypes)) {
      return null;
    }
    List<EntityTypeDTO> entityTypeDTOList = Lists.newArrayList();
    for (BaseSPGType spgType : spgTypes) {
      entityTypeDTOList.add(toEntityTypeDTO(spgType));
    }
    return entityTypeDTOList;
  }

  public static EntityTypeDTO toEntityTypeDTO(BaseSPGType spgType) {
    if (null == spgType) {
      return null;
    }
    EntityTypeDTO entityTypeDTO = new EntityTypeDTO();
    entityTypeDTO.setId(spgType.getUniqueId());
    entityTypeDTO.setName(spgType.getName());
    entityTypeDTO.setNameZh(spgType.getBasicInfo().getNameZh());
    entityTypeDTO.setDescription(spgType.getBasicInfo().getDesc());
    entityTypeDTO.setParentId(spgType.getParentUniqueId());
    if (null != spgType.getParentTypeName()) {
      entityTypeDTO.setParentName(spgType.getParentTypeName().getNameEn());
    }
    entityTypeDTO.setBelongToProject(spgType.getProjectId());
    entityTypeDTO.setEntityCategory(spgType.getSpgTypeEnum().name());
    List<Property> properties = spgType.getProperties();
    List<PropertyDTO> attributeTypeDetailList = Lists.newArrayList();
    List<PropertyDTO> inheritAttributeTypeDetailList = Lists.newArrayList();
    if (CollectionUtils.isNotEmpty(properties)) {
      for (Property property : properties) {
        PropertyDTO attributeTypeDTO = toAttributeTypeDTO(property);
        if (property.getInherited().booleanValue()) {
          inheritAttributeTypeDetailList.add(attributeTypeDTO);
        } else {
          attributeTypeDetailList.add(attributeTypeDTO);
        }
      }
    }
    entityTypeDTO.setPropertyList(attributeTypeDetailList);
    entityTypeDTO.setInheritedPropertyList(inheritAttributeTypeDetailList);
    entityTypeDTO.setExtInfo(convertExtInfo(spgType.getExtInfo()));
    return entityTypeDTO;
  }

  private static PropertyDTO toAttributeTypeDTO(Property property) {
    PropertyDTO att = new PropertyDTO();
    att.setId(property.getUniqueId());
    att.setName(property.getName());
    att.setNameZh(property.getBasicInfo().getNameZh());
    att.setDescription(property.getBasicInfo().getDesc());
    att.setMaskType(property.getEncryptTypeEnum().name());
    SPGTypeRef spgTypeRef = property.getObjectTypeRef();
    att.setPropertyCategoryEnum(spgTypeRef.getSpgTypeEnum().name());
    att.setRangeId(spgTypeRef.getUniqueId());
    att.setRangeName(spgTypeRef.getName());
    att.setRangeNameZh(spgTypeRef.getBasicInfo().getNameZh());
    att.setExtInfo(convertExtInfo(property.getExtInfo()));
    att.setLogicRule(RuleConvertor.toRuleDTO(property.getLogicalRule()));
    List<AttrConsDTO> consDTOList = Lists.newArrayList();
    if (null != property.getConstraint()
        && CollectionUtils.isNotEmpty(property.getConstraint().getConstraintItems())) {
      property.getConstraint().getConstraintItems().stream()
          .forEach(
              baseConstraintItem -> {
                consDTOList.add(toAttrConsDTO(baseConstraintItem));
              });
    }
    att.setConstraints(consDTOList);
    List<SubProperty> subPropertyList = property.getAdvancedConfig().getSubProperties();
    if (CollectionUtils.isNotEmpty(subPropertyList)) {
      subPropertyList.forEach(
          subProperty -> {
            att.getSubPropertyList().add(toRelationProperty(subProperty));
          });
    }
    return att;
  }

  private static AttrConsDTO toAttrConsDTO(BaseConstraintItem constraint) {
    AttrConsDTO attrConsDTO = new AttrConsDTO();
    attrConsDTO.setName(constraint.getConstraintTypeEnum().name());
    attrConsDTO.setId(constraint.getConstraintTypeEnum().name());
    ConstraintTypeEnum constraintTypeEnum = constraint.getConstraintTypeEnum();
    switch (AnonymousClass1
        .$SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
        constraintTypeEnum.ordinal()]) {
      case 1:
        attrConsDTO.setNameZh(ConstraintEnum.ENUM.getNameZh());
        EnumConstraint enumConstraint = (EnumConstraint) constraint;
        attrConsDTO.setValue(enumConstraint.getEnumValues());
        break;
      case 2:
        attrConsDTO.setNameZh(ConstraintEnum.RANGE.getNameZh());
        RangeConstraint rangeConstraint = (RangeConstraint) constraint;
        attrConsDTO.setValue(rangeConstraint);
        break;
      case 3:
        attrConsDTO.setNameZh(ConstraintEnum.UNIQUE.getNameZh());
        break;
      case 4:
        attrConsDTO.setNameZh(ConstraintEnum.REGULAR.getNameZh());
        RegularConstraint regularConstraint = (RegularConstraint) constraint;
        attrConsDTO.setValue(regularConstraint.getRegularPattern());
        break;
      case 5:
        attrConsDTO.setNameZh(ConstraintEnum.REQUIRE.getNameZh());
        break;
      case 6:
        attrConsDTO.setNameZh(ConstraintEnum.MULTIVALUE.getNameZh());
        break;
    }
    return attrConsDTO;
  }

  /* renamed from: com.antgroup.openspgapp.biz.schema.convertor.SchemaConvertor$1, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/convertor/SchemaConvertor$1.class */
  static /* synthetic */ class AnonymousClass1 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum =
            new int[ConstraintTypeEnum.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.ENUM.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.RANGE.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.UNIQUE.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.REGULAR.ordinal()] =
            4;
      } catch (NoSuchFieldError e4) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.NOT_NULL.ordinal()] =
            5;
      } catch (NoSuchFieldError e5) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$constraint$ConstraintTypeEnum[
                ConstraintTypeEnum.MULTI_VALUE.ordinal()] =
            6;
      } catch (NoSuchFieldError e6) {
      }
    }
  }

  private static Map<String, Object> convertExtInfo(SchemaExtInfo extInfo) {
    if (null == extInfo) {
      return null;
    }
    HashMap hashMap = new HashMap();
    for (Map.Entry<String, Object> map : extInfo.entrySet()) {
      hashMap.put(map.getKey(), map.getValue());
    }
    return hashMap;
  }

  public static RelationTypeDTO toRelationType(Relation relation) {
    RelationTypeDTO relationTypeDTO = new RelationTypeDTO();
    relationTypeDTO.setId(relation.getUniqueId());
    relationTypeDTO.setName(relation.getName());
    relationTypeDTO.setNameZh(relation.getBasicInfo().getNameZh());
    relationTypeDTO.setOriginBelongToProject(relation.getProjectId());
    SchemaExtInfo extInfo = relation.getExtInfo();
    relationTypeDTO.setRelationCategory(null == extInfo ? null : extInfo.getString("valueType"));
    SPGTypeRef s = relation.getSubjectTypeRef();
    EntityTypeDTO startEntity = new EntityTypeDTO();
    startEntity.setId(s.getUniqueId());
    startEntity.setName(s.getName());
    startEntity.setNameZh(s.getBasicInfo().getNameZh());
    relationTypeDTO.setStartEntity(startEntity);
    SPGTypeRef o = relation.getObjectTypeRef();
    EntityTypeDTO endEntity = new EntityTypeDTO();
    endEntity.setId(o.getUniqueId());
    endEntity.setName(o.getName());
    endEntity.setNameZh(o.getBasicInfo().getNameZh());
    relationTypeDTO.setEndEntity(endEntity);
    relationTypeDTO.setLogicRuleDTO(RuleConvertor.toRuleDTO(relation.getLogicalRule()));
    relationTypeDTO.setExtInfo(convertExtInfo(relation.getExtInfo()));
    List<SubProperty> propertyList = relation.getSubProperties();
    if (CollectionUtils.isNotEmpty(propertyList)) {
      propertyList.stream()
          .forEach(
              subProperty -> {
                relationTypeDTO.getPropertyList().add(toRelationProperty(subProperty));
              });
    }
    return relationTypeDTO;
  }

  private static PropertyDTO toRelationProperty(SubProperty subProperty) {
    PropertyDTO propertyDTO = new PropertyDTO();
    propertyDTO.setName(subProperty.getName());
    propertyDTO.setNameZh(subProperty.getBasicInfo().getNameZh());
    SPGTypeRef spgTypeRef = subProperty.getObjectTypeRef();
    propertyDTO.setPropertyCategoryEnum(spgTypeRef.getSpgTypeEnum().name());
    propertyDTO.setRangeId(spgTypeRef.getUniqueId());
    propertyDTO.setRangeName(spgTypeRef.getName());
    propertyDTO.setRangeNameZh(spgTypeRef.getBasicInfo().getNameZh());
    List<AttrConsDTO> consDTOList = Lists.newArrayList();
    if (null != subProperty.getConstraint()
        && CollectionUtils.isNotEmpty(subProperty.getConstraint().getConstraintItems())) {
      subProperty.getConstraint().getConstraintItems().stream()
          .forEach(
              baseConstraintItem -> {
                consDTOList.add(toAttrConsDTO(baseConstraintItem));
              });
    }
    propertyDTO.setConstraints(consDTOList);
    propertyDTO.setExtInfo(convertExtInfo(spgTypeRef.getExtInfo()));
    return propertyDTO;
  }

  public static RelationTypeDTO toRelationType(
      SimpleProperty simpleProperty, List<BaseSPGType> baseSPGTypes) {
    if (null == simpleProperty) {
      return null;
    }
    RelationTypeDTO relationTypeDTO = new RelationTypeDTO();
    relationTypeDTO.setId(simpleProperty.getUniqueId());
    relationTypeDTO.setName(simpleProperty.getBasicInfo().getName().getName());
    relationTypeDTO.setNameZh(simpleProperty.getBasicInfo().getNameZh());
    relationTypeDTO.setDescription(simpleProperty.getBasicInfo().getDesc());
    relationTypeDTO.setOriginBelongToProject(simpleProperty.getProjectId());
    if (CollectionUtils.isEmpty(baseSPGTypes)) {
      return relationTypeDTO;
    }
    Map<Long, BaseSPGType> typeMap =
        (Map)
            baseSPGTypes.stream()
                .collect(
                    Collectors.toMap(
                        baseSPGType -> {
                          return baseSPGType.getUniqueId();
                        },
                        baseSPGType2 -> {
                          return baseSPGType2;
                        }));
    BaseSPGType startType = typeMap.get(simpleProperty.getSubjectTypeId().getUniqueId());
    if (startType != null) {
      EntityTypeDTO startEntity = new EntityTypeDTO();
      startEntity.setId(startType.getUniqueId());
      startEntity.setName(startType.getName());
      startEntity.setNameZh(startType.getBasicInfo().getNameZh());
      relationTypeDTO.setStartEntity(startEntity);
    }
    BaseSPGType endType = typeMap.get(simpleProperty.getSubjectTypeId().getUniqueId());
    if (endType != null) {
      EntityTypeDTO endEntity = new EntityTypeDTO();
      endEntity.setId(endType.getUniqueId());
      endEntity.setName(endType.getName());
      endEntity.setNameZh(endType.getBasicInfo().getNameZh());
      relationTypeDTO.setEndEntity(endEntity);
    }
    return relationTypeDTO;
  }
}
