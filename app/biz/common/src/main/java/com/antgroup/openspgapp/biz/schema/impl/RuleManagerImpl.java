package com.antgroup.openspgapp.biz.schema.impl;

import com.antgroup.openspg.core.schema.model.semantic.LogicalRule;
import com.antgroup.openspg.core.schema.model.semantic.PredicateSemantic;
import com.antgroup.openspg.core.schema.model.semantic.RuleCode;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.core.schema.service.semantic.LogicalRuleService;
import com.antgroup.openspg.server.core.schema.service.semantic.SemanticService;
import com.antgroup.openspgapp.biz.schema.AppSchemaManager;
import com.antgroup.openspgapp.biz.schema.RuleManager;
import com.antgroup.openspgapp.biz.schema.convertor.RuleConvertor;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.LogicRuleDTO;
import com.antgroup.openspgapp.biz.schema.dto.ObjectType;
import com.antgroup.openspgapp.biz.schema.dto.ProjectSchemaDTO;
import com.antgroup.openspgapp.biz.schema.dto.PropertyDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/RuleManagerImpl.class */
public class RuleManagerImpl implements RuleManager {

  @Autowired private AppSchemaManager appSchemaManager;

  @Autowired private SemanticService semanticService;

  @Autowired private LogicalRuleService logicalRuleService;
  private static final int QUERY_SIZE = 100;

  @Override // com.antgroup.openspgapp.biz.schema.RuleManager
  public List<LogicRuleDTO> getProjectRule(Long projectId) {
    ProjectSchemaDTO projectSchemaDTO = this.appSchemaManager.getProjectSchemaDetail(projectId);
    if (CollectionUtils.isEmpty(projectSchemaDTO.getEntityTypeDTOList())
        && CollectionUtils.isEmpty(projectSchemaDTO.getRelationTypeDTOList())) {
      return Lists.newArrayList();
    }
    Set<Long> propertyIds = new HashSet<>();
    Set<Long> relationIds = new HashSet<>();
    Set<Long> conceptIds = new HashSet<>();
    Map<Long, RelationTypeDTO> id2RelationType = new HashMap<>();
    Map<Long, EntityTypeDTO> propertyId2Entity = new HashMap<>();
    Map<Long, PropertyDTO> propertyId2Property = new HashMap<>();
    Map<String, Long> ruleId2ResourceId = new HashMap<>();
    List<LogicRuleDTO> logicRuleDTOList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(projectSchemaDTO.getEntityTypeDTOList())) {
      projectSchemaDTO
          .getEntityTypeDTOList()
          .forEach(
              entity -> {
                if (CollectionUtils.isEmpty(entity.getPropertyList())) {
                  return;
                }
                entity
                    .getPropertyList()
                    .forEach(
                        property -> {
                          propertyId2Entity.put(property.getId(), entity);
                          propertyId2Property.put(property.getId(), property);
                          if (null != property.getLogicRule()) {
                            logicRuleDTOList.add(property.getLogicRule());
                            ruleId2ResourceId.put(
                                property.getLogicRule().getRuleId(), property.getId());
                            return;
                          }
                          Object valueType = property.getExtInfo().get("valueType");
                          if (null != valueType
                              && SPGTypeEnum.CONCEPT_TYPE
                                  .name()
                                  .equalsIgnoreCase(valueType.toString())) {
                            conceptIds.add(property.getId());
                          } else {
                            propertyIds.add(property.getId());
                          }
                        });
              });
    }
    if (CollectionUtils.isNotEmpty(projectSchemaDTO.getRelationTypeDTOList())) {
      projectSchemaDTO
          .getRelationTypeDTOList()
          .forEach(
              relation -> {
                if (propertyId2Property.containsKey(relation.getId())) {
                  return;
                }
                id2RelationType.put(relation.getId(), relation);
                if (null != relation.getLogicRuleDTO()) {
                  logicRuleDTOList.add(relation.getLogicRuleDTO());
                  ruleId2ResourceId.put(relation.getLogicRuleDTO().getRuleId(), relation.getId());
                } else {
                  relationIds.add(relation.getId());
                }
              });
    }
    List<RuleCode> ruleCodeList = new ArrayList<>();
    Map<String, PredicateSemantic> ruleId2PredicateSemantic = new HashMap<>();
    getRuleId(
        propertyIds,
        ruleCodeList,
        ruleId2PredicateSemantic,
        ruleId2ResourceId,
        SPGOntologyEnum.PROPERTY);
    getRuleId(
        relationIds,
        ruleCodeList,
        ruleId2PredicateSemantic,
        ruleId2ResourceId,
        SPGOntologyEnum.RELATION);
    getRuleId(
        conceptIds,
        ruleCodeList,
        ruleId2PredicateSemantic,
        ruleId2ResourceId,
        SPGOntologyEnum.CONCEPT);
    if (CollectionUtils.isNotEmpty(ruleCodeList)) {
      List<LogicalRule> logicalRuleList = this.logicalRuleService.queryByRuleCode(ruleCodeList);
      logicalRuleList.forEach(
          rule -> {
            LogicRuleDTO ruleDTO = RuleConvertor.toRuleDTO(rule);
            if (null != ruleDTO) {
              logicRuleDTOList.add(ruleDTO);
            }
          });
    }
    logicRuleDTOList.forEach(
        ruleDTO -> {
          completeRule(
              ruleDTO,
              ruleId2PredicateSemantic,
              ruleId2ResourceId,
              id2RelationType,
              propertyId2Property,
              propertyId2Entity);
        });
    return logicRuleDTOList;
  }

  private void completeRule(
      LogicRuleDTO ruleDTO,
      Map<String, PredicateSemantic> ruleId2PredicateSemantic,
      Map<String, Long> ruleId2ResourceId,
      Map<Long, RelationTypeDTO> id2RelationType,
      Map<Long, PropertyDTO> propertyId2Property,
      Map<Long, EntityTypeDTO> propertyId2Entity) {
    String ruleId = ruleDTO.getRuleId();
    PredicateSemantic predicateSemantic = ruleId2PredicateSemantic.get(ruleId);
    if (null != predicateSemantic) {
      ruleDTO.setResourceId(String.valueOf(predicateSemantic.getSubjectUniqueId()));
      ruleDTO.setSemanticType(predicateSemantic.getPredicateIdentifier().getName());
      ruleDTO.setRuleType(predicateSemantic.getOntologyType().name());
    } else {
      Long rid = ruleId2ResourceId.get(ruleId);
      ruleDTO.setResourceId(String.valueOf(rid));
      if (null != propertyId2Property.get(rid)) {
        ruleDTO.setRuleType(SPGOntologyEnum.PROPERTY.name());
      }
      RelationTypeDTO relationTypeDTO = id2RelationType.get(rid);
      if (null != relationTypeDTO) {
        Object valueType = relationTypeDTO.getExtInfo().get("valueType");
        if (null != valueType
            && SPGTypeEnum.CONCEPT_TYPE.name().equalsIgnoreCase(valueType.toString())) {
          ruleDTO.setRuleType(SPGOntologyEnum.CONCEPT.name());
        } else {
          ruleDTO.setRuleType(SPGOntologyEnum.RELATION.name());
        }
      }
    }
    SPGOntologyEnum spgOntologyEnum = SPGOntologyEnum.toEnum(ruleDTO.getRuleType());
    getPName(
        ruleId, ruleId2ResourceId, id2RelationType, propertyId2Property, ruleDTO, spgOntologyEnum);
    EntityTypeDTO startEntity =
        getStartEntity(
            ruleId, ruleId2ResourceId, id2RelationType, propertyId2Entity, spgOntologyEnum);
    ruleDTO.setStartEntity(startEntity);
    ObjectType objectType =
        getEndObject(
            ruleId, ruleId2ResourceId, id2RelationType, ruleId2PredicateSemantic, spgOntologyEnum);
    ruleDTO.setObjectType(objectType);
  }

  private EntityTypeDTO getStartEntity(
      String ruleId,
      Map<String, Long> ruleId2ResourceId,
      Map<Long, RelationTypeDTO> id2RelationType,
      Map<Long, EntityTypeDTO> propertyId2Entity,
      SPGOntologyEnum spgOntologyEnum) {
    Long resourceId = ruleId2ResourceId.get(ruleId);
    EntityTypeDTO startEntity = null;
    switch (AnonymousClass1
        .$SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
        spgOntologyEnum.ordinal()]) {
      case 1:
        RelationTypeDTO relationTypeDTO = id2RelationType.get(resourceId);
        if (null != relationTypeDTO) {
          startEntity =
              new EntityTypeDTO(
                  relationTypeDTO.getStartEntity().getName(),
                  relationTypeDTO.getStartEntity().getNameZh(),
                  relationTypeDTO.getStartEntity().getId());
          break;
        }
        break;
      case 2:
        RelationTypeDTO relationType = id2RelationType.get(resourceId);
        if (null != relationType) {
          startEntity =
              new EntityTypeDTO(
                  relationType.getStartEntity().getName(),
                  relationType.getStartEntity().getNameZh(),
                  relationType.getStartEntity().getId());
          break;
        } else {
          EntityTypeDTO entity = propertyId2Entity.get(resourceId);
          if (null != entity) {
            startEntity = new EntityTypeDTO(entity.getName(), entity.getNameZh(), entity.getId());
            break;
          }
        }
        break;
      case 3:
        EntityTypeDTO entity2 = propertyId2Entity.get(resourceId);
        if (null != entity2) {
          startEntity = new EntityTypeDTO(entity2.getName(), entity2.getNameZh(), entity2.getId());
          break;
        }
        break;
    }
    return startEntity;
  }

  /* renamed from: com.antgroup.openspgapp.biz.schema.impl.RuleManagerImpl$1, reason: invalid class name */
  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/RuleManagerImpl$1.class */
  static /* synthetic */ class AnonymousClass1 {
    static final /* synthetic */ int[]
        $SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum =
            new int[SPGOntologyEnum.values().length];

    static {
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
                SPGOntologyEnum.RELATION.ordinal()] =
            1;
      } catch (NoSuchFieldError e) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
                SPGOntologyEnum.CONCEPT.ordinal()] =
            2;
      } catch (NoSuchFieldError e2) {
      }
      try {
        $SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
                SPGOntologyEnum.PROPERTY.ordinal()] =
            3;
      } catch (NoSuchFieldError e3) {
      }
    }
  }

  private void getRuleId(
      Set<Long> resourceIds,
      List<RuleCode> ruleCodeList,
      Map<String, PredicateSemantic> ruleId2PredicateSemantic,
      Map<String, Long> ruleId2ResourceId,
      SPGOntologyEnum spgOntologyEnum) {
    if (CollectionUtils.isEmpty(resourceIds)) {
      return;
    }
    List<List<Long>> batchList = Lists.partition(new ArrayList(resourceIds), QUERY_SIZE);
    for (List<Long> list : batchList) {
      List<PredicateSemantic> semanticList =
          this.semanticService.queryBySubjectIds(list, spgOntologyEnum);
      semanticList.forEach(
          predicateSemantic -> {
            if (null != predicateSemantic.getRuleCode()) {
              ruleCodeList.add(predicateSemantic.getRuleCode());
              ruleId2PredicateSemantic.put(
                  predicateSemantic.getRuleCode().getCode(), predicateSemantic);
              ruleId2ResourceId.put(
                  predicateSemantic.getRuleCode().getCode(),
                  predicateSemantic.getSubjectTypeRef().getUniqueId());
            }
          });
    }
  }

  private void getPName(
      String ruleId,
      Map<String, Long> ruleId2ResourceId,
      Map<Long, RelationTypeDTO> id2RelationType,
      Map<Long, PropertyDTO> propertyId2Property,
      LogicRuleDTO ruleDTO,
      SPGOntologyEnum spgOntologyEnum) {
    Long resourceId = ruleId2ResourceId.get(ruleId);
    switch (AnonymousClass1
        .$SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
        spgOntologyEnum.ordinal()]) {
      case 1:
        RelationTypeDTO relationTypeDTO = id2RelationType.get(resourceId);
        if (null != relationTypeDTO) {
          ruleDTO.setName(relationTypeDTO.getName());
          ruleDTO.setNameZh(relationTypeDTO.getNameZh());
          break;
        }
        break;
      case 2:
        RelationTypeDTO relationType = id2RelationType.get(resourceId);
        if (null != relationType) {
          ruleDTO.setName(relationType.getName());
          ruleDTO.setNameZh(relationType.getNameZh());
          break;
        } else {
          PropertyDTO propertyDTO = propertyId2Property.get(resourceId);
          if (null != propertyDTO) {
            ruleDTO.setName(propertyDTO.getName());
            ruleDTO.setNameZh(propertyDTO.getNameZh());
            break;
          }
        }
        break;
      case 3:
        PropertyDTO propertyDTO2 = propertyId2Property.get(resourceId);
        if (null != propertyDTO2) {
          ruleDTO.setName(propertyDTO2.getName());
          ruleDTO.setNameZh(propertyDTO2.getNameZh());
          break;
        }
        break;
    }
  }

  private ObjectType getEndObject(
      String ruleId,
      Map<String, Long> ruleId2ResourceId,
      Map<Long, RelationTypeDTO> id2RelationType,
      Map<String, PredicateSemantic> ruleId2PredicateSemantic,
      SPGOntologyEnum spgOntologyEnum) {
    ObjectType objectType = new ObjectType();
    Long resourceId = ruleId2ResourceId.get(ruleId);
    switch (AnonymousClass1
        .$SwitchMap$com$antgroup$openspg$core$schema$model$semantic$SPGOntologyEnum[
        spgOntologyEnum.ordinal()]) {
      case 1:
        RelationTypeDTO relationTypeDTO = id2RelationType.get(resourceId);
        if (null != relationTypeDTO) {
          objectType.setId(relationTypeDTO.getEndEntity().getId());
          objectType.setName(relationTypeDTO.getEndEntity().getName());
          objectType.setNameZh(relationTypeDTO.getEndEntity().getNameZh());
          break;
        }
        break;
      case 2:
        PredicateSemantic predicateSemantic = ruleId2PredicateSemantic.get(ruleId);
        if (null != predicateSemantic) {
          objectType.setName(predicateSemantic.getObjectTypeRef().getName());
          break;
        } else {
          RelationTypeDTO relationType = id2RelationType.get(resourceId);
          if (null != relationType) {
            objectType.setId(relationType.getEndEntity().getId());
            objectType.setName(relationType.getEndEntity().getName());
            objectType.setNameZh(relationType.getEndEntity().getNameZh());
            break;
          }
        }
        break;
    }
    return objectType;
  }
}
