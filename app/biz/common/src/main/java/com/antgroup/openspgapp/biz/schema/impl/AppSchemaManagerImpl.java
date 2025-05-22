package com.antgroup.openspgapp.biz.schema.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.antgroup.openspg.common.util.exception.SpgException;
import com.antgroup.openspg.common.util.exception.message.SpgMessageEnum;
import com.antgroup.openspg.core.schema.model.alter.SchemaDraft;
import com.antgroup.openspg.core.schema.model.predicate.Relation;
import com.antgroup.openspg.core.schema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.schema.model.type.BaseAdvancedType;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ExtTypeEnum;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.dto.schema.request.SchemaAlterRequest;
import com.antgroup.openspg.server.api.http.client.account.AccountService;
import com.antgroup.openspg.server.biz.common.PermissionManager;
import com.antgroup.openspg.server.biz.common.ProjectManager;
import com.antgroup.openspg.server.biz.schema.OntologyExtManager;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.antgroup.openspg.server.common.model.account.Account;
import com.antgroup.openspg.server.common.model.project.Project;
import com.antgroup.openspg.server.core.schema.service.predicate.model.SimpleProperty;
import com.antgroup.openspg.server.core.schema.service.type.model.OntologyExt;
import com.antgroup.openspgapp.biz.schema.AppSchemaManager;
import com.antgroup.openspgapp.biz.schema.convertor.SchemaConvertor;
import com.antgroup.openspgapp.biz.schema.dto.EntityTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ProjectSchemaDTO;
import com.antgroup.openspgapp.biz.schema.dto.PropertyDTO;
import com.antgroup.openspgapp.biz.schema.dto.RelationTypeDTO;
import com.antgroup.openspgapp.biz.schema.dto.SchemaTreeDTO;
import com.antgroup.openspgapp.biz.schema.model.NodeTypeModel;
import com.antgroup.openspgapp.biz.schema.model.SchemaCompareUtil;
import com.antgroup.openspgapp.biz.schema.model.SchemaModel;
import com.antgroup.openspgapp.biz.schema.model.SchemaModelConvertor;
import com.antgroup.openspgapp.biz.schema.model.SchemaScriptTranslateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/AppSchemaManagerImpl.class */
public class AppSchemaManagerImpl implements AppSchemaManager {
  private static final Logger log = LoggerFactory.getLogger(AppSchemaManagerImpl.class);

  @Autowired private SchemaManager schemaManager;

  @Autowired private OntologyExtManager ontologyExtManager;

  @Autowired private ProjectManager projectManager;

  @Autowired private AccountService accountService;

  @Autowired private PermissionManager permissionManager;

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public SchemaTreeDTO getProjectEntityType(Long projectId) {
    ProjectSchema projectSchema = this.schemaManager.getProjectSchema(projectId);
    SchemaTreeDTO rootTree = buildRoot();
    if (CollectionUtils.isEmpty(projectSchema.getSpgTypes())) {
      return rootTree;
    }
    Map<Long, SchemaTreeDTO> treeDTOMap = new HashMap<>();
    treeDTOMap.put(rootTree.getEntityTypeDTO().getId(), rootTree);
    Map<Long, BaseSPGType> id2SpgType =
        (Map)
            projectSchema.getSpgTypes().stream()
                .collect(
                    Collectors.toMap(
                        (v0) -> {
                          return v0.getUniqueId();
                        },
                        v -> {
                          return v;
                        }));
    for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
      if (SPGTypeEnum.BASIC_TYPE != spgType.getSpgTypeEnum()
          && SPGTypeEnum.STANDARD_TYPE != spgType.getSpgTypeEnum()) {
        addTreeMap(rootTree, treeDTOMap, id2SpgType, spgType.getInheritPath());
      }
    }
    return rootTree;
  }

  private void addTreeMap(
      SchemaTreeDTO rootTree,
      Map<Long, SchemaTreeDTO> treeDTOMap,
      Map<Long, BaseSPGType> id2SpgType,
      List<Long> inheritPath) {
    Map<Long, Long> child2Parent = Maps.newHashMap();
    for (int i = 1; i < inheritPath.size(); i++) {
      child2Parent.put(inheritPath.get(i), inheritPath.get(i - 1));
    }
    for (Long id : inheritPath) {
      if (!treeDTOMap.containsKey(id)) {
        Long tempParentId = child2Parent.get(id);
        SchemaTreeDTO tree = treeDTOMap.get(tempParentId);
        SchemaTreeDTO currentTree = new SchemaTreeDTO();
        currentTree.setEntityTypeDTO(SchemaConvertor.toEntityTypeDTO(id2SpgType.get(id)));
        if (null == tree) {
          rootTree.getChildren().add(currentTree);
          treeDTOMap.put(tempParentId, rootTree);
        } else {
          tree.getChildren().add(currentTree);
        }
        treeDTOMap.put(id, currentTree);
      }
    }
  }

  private SchemaTreeDTO buildRoot() {
    BaseSPGType spgType = this.schemaManager.getSpgType("Thing");
    SchemaTreeDTO tree = new SchemaTreeDTO();
    tree.setEntityTypeDTO(SchemaConvertor.toEntityTypeDTO(spgType));
    return tree;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public EntityTypeDTO getEntityTypeByName(String entityTypeName) {
    BaseSPGType spgType = this.schemaManager.getSpgType(entityTypeName);
    EntityTypeDTO entityTypeDTO = SchemaConvertor.toEntityTypeDTO(spgType);
    List<Relation> relations = spgType.getRelations();
    if (CollectionUtils.isNotEmpty(relations)) {
      relations.stream()
          .forEach(
              relation -> {
                entityTypeDTO.getRelations().add(SchemaConvertor.toRelationType(relation));
              });
    }
    return entityTypeDTO;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public RelationTypeDTO getRelationTypeBySpo(String sName, String pName, String oName) {
    BaseSPGType spgType = this.schemaManager.getSpgType(sName);
    if (null == spgType) {
      return null;
    }
    List<Relation> relations = spgType.getRelations();
    if (CollectionUtils.isEmpty(relations)) {
      return null;
    }
    for (Relation relation : relations) {
      if (pName.equals(relation.getName()) && oName.equals(relation.getObjectTypeRef().getName())) {
        return SchemaConvertor.toRelationType(relation);
      }
    }
    return null;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public EntityTypeDTO getEntityTypeById(Long id) {
    List<BaseSPGType> baseSPGTypes =
        this.schemaManager.querySPGTypeById(Lists.newArrayList(new Long[] {id}));
    if (CollectionUtils.isEmpty(baseSPGTypes)) {
      return null;
    }
    BaseSPGType spgType = baseSPGTypes.get(0);
    EntityTypeDTO entityTypeDTO = SchemaConvertor.toEntityTypeDTO(spgType);
    List<Relation> relations = spgType.getRelations();
    if (CollectionUtils.isNotEmpty(relations)) {
      relations.stream()
          .forEach(
              relation -> {
                entityTypeDTO.getRelations().add(SchemaConvertor.toRelationType(relation));
              });
    }
    return entityTypeDTO;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public RelationTypeDTO getRelationTypeById(Long id) {
    List<Relation> relations =
        this.schemaManager.queryRelationByUniqueId(Lists.newArrayList(new Long[] {id}));
    if (CollectionUtils.isNotEmpty(relations)) {
      Relation relation = relations.get(0);
      return SchemaConvertor.toRelationType(relation);
    }
    List<SimpleProperty> simpleProperties =
        this.schemaManager.queryPropertyByUniqueId(
            Lists.newArrayList(new Long[] {id}), SPGOntologyEnum.PROPERTY);
    if (CollectionUtils.isNotEmpty(simpleProperties)) {
      SimpleProperty simpleProperty = simpleProperties.get(0);
      Long startId = simpleProperty.getSubjectTypeId().getUniqueId();
      Long endId = simpleProperty.getObjectTypeId().getUniqueId();
      List<BaseSPGType> baseSPGTypes =
          this.schemaManager.querySPGTypeById(Lists.newArrayList(new Long[] {startId, endId}));
      return SchemaConvertor.toRelationType(simpleProperty, baseSPGTypes);
    }
    return null;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public ProjectSchemaDTO getProjectSchemaDetail(Long projectId) {
    ProjectSchemaDTO projectSchemaDTO = new ProjectSchemaDTO();
    ProjectSchema projectSchema = this.schemaManager.getProjectSchema(projectId);
    if (CollectionUtils.isEmpty(projectSchema.getSpgTypes())) {
      return projectSchemaDTO;
    }
    List<EntityTypeDTO> entityTypeDTOS = new ArrayList<>();
    List<RelationTypeDTO> relationTypeDTOS = new ArrayList<>();
    Set<String> existRelations = new HashSet<>();
    Set<Long> standardTypeIds = new HashSet<>();
    Map<Long, BaseSPGType> id2SpgType = new HashMap<>();
    for (BaseSPGType spgType : projectSchema.getSpgTypes()) {
      if (SPGTypeEnum.BASIC_TYPE == spgType.getSpgTypeEnum()
          || SPGTypeEnum.STANDARD_TYPE == spgType.getSpgTypeEnum()) {
        id2SpgType.put(spgType.getUniqueId(), spgType);
      } else {
        entityTypeDTOS.add(SchemaConvertor.toEntityTypeDTO(spgType));
        List<Relation> relations = spgType.getRelations();
        if (!CollectionUtils.isEmpty(relations)) {
          for (Relation relation : relations) {
            if (!existRelations.contains(getSpoStr(relation))) {
              relationTypeDTOS.add(SchemaConvertor.toRelationType(relation));
              if (SPGTypeEnum.STANDARD_TYPE == relation.getObjectTypeRef().getSpgTypeEnum()) {
                standardTypeIds.add(relation.getObjectTypeRef().getUniqueId());
              }
              existRelations.add(getSpoStr(relation));
            }
          }
        }
      }
    }
    for (Long standardTypeId : standardTypeIds) {
      entityTypeDTOS.add(SchemaConvertor.toEntityTypeDTO(id2SpgType.get(standardTypeId)));
    }
    projectSchemaDTO.setEntityTypeDTOList(entityTypeDTOS);
    projectSchemaDTO.setRelationTypeDTOList(relationTypeDTOS);
    return projectSchemaDTO;
  }

  private String getSpoStr(Relation relation) {
    return String.format(
        "%s_%s_%s",
        relation.getSubjectTypeRef().getName(),
        relation.getName(),
        relation.getObjectTypeRef().getName());
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public ProjectSchemaDTO getSimpleProjectSchema(Long projectId) {
    return null;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public Map<String, JSONObject> getDynamicConfig(String type, List<Long> ids) {
    Set<String> resourceIds =
        (Set)
            ids.stream()
                .map(
                    id -> {
                      return String.valueOf(id);
                    })
                .collect(Collectors.toSet());
    List<OntologyExt> extList =
        this.ontologyExtManager.getExtInfoListByIds(
            resourceIds, type, ExtTypeEnum.DYNAMIC_CONFIG_INFO);
    Map<String, JSONObject> result = new HashMap<>();
    if (CollectionUtils.isEmpty(extList)) {
      return result;
    }
    for (OntologyExt ontologyExt : extList) {
      JSONObject config =
          StringUtils.isEmpty(ontologyExt.getConfig())
              ? null
              : JSONObject.parseObject(ontologyExt.getConfig());
      if (null != config && null != config.getJSONObject("online")) {
        result.put(ontologyExt.getResourceId(), config.getJSONObject("online"));
      }
    }
    return result;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public Map<String, String> getSchemaPropertyNameMap(Long projectId) {
    Map<String, String> map = new HashMap<>();
    ProjectSchemaDTO schemaDetail = getProjectSchemaDetail(projectId);
    if (CollectionUtils.isNotEmpty(schemaDetail.getEntityTypeDTOList())) {
      schemaDetail.getEntityTypeDTOList().stream()
          .forEach(
              it -> {
                map.put(it.getName(), it.getNameZh());
                addPropertyNameMap(map, it.getName(), it.getInheritedPropertyList());
                addPropertyNameMap(map, it.getName(), it.getPropertyList());
              });
    }
    if (CollectionUtils.isNotEmpty(schemaDetail.getRelationTypeDTOList())) {
      schemaDetail
          .getRelationTypeDTOList()
          .forEach(
              it2 -> {
                String spo =
                    String.format(
                        "%s_%s_%s",
                        it2.getStartEntity().getName(),
                        it2.getName(),
                        it2.getEndEntity().getName());
                map.put(spo, it2.getNameZh());
                addPropertyNameMap(map, spo, it2.getPropertyList());
              });
    }
    return map;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public String getSchemaScript(Long projectId) {
    Project project = this.projectManager.queryById(projectId);
    ProjectSchema projectSchema = this.schemaManager.getProjectSchema(projectId);
    List<NodeTypeModel> nodeTypeModelList = new ArrayList<>();
    for (BaseSPGType baseAdvancedType : projectSchema.getSpgTypes()) {
      if (SPGTypeEnum.ENTITY_TYPE.equals(baseAdvancedType.getSpgTypeEnum())
          || SPGTypeEnum.EVENT_TYPE.equals(baseAdvancedType.getSpgTypeEnum())
          || SPGTypeEnum.CONCEPT_TYPE.equals(baseAdvancedType.getSpgTypeEnum())) {
        log.info("get originalSPG:[{}]", JSON.toJSONString(baseAdvancedType));
        NodeTypeModel entityModel =
            SchemaModelConvertor.convert2NodeTypeModel(project.getNamespace(), baseAdvancedType);
        nodeTypeModelList.add(entityModel);
      }
    }
    log.info("get nodeTypeModelList:[{}]", JSON.toJSONString(nodeTypeModelList));
    SchemaModel schemaModel = new SchemaModel();
    schemaModel.setNamespace(project.getNamespace());
    schemaModel.setNodeTypeModels(nodeTypeModelList);
    return SchemaScriptTranslateUtil.translateSchema(schemaModel);
  }

  @Override // com.antgroup.openspgapp.biz.schema.AppSchemaManager
  public Map<String, Object> saveSchema(String schema) {
    SchemaModel schemaModel = SchemaScriptTranslateUtil.translateScript(schema);
    String namespace = schemaModel.getNamespace();
    log.info("schemaModel:[{}]", JSON.toJSONString(schemaModel));
    SchemaAlterRequest request = new SchemaAlterRequest();
    Project project = this.projectManager.queryByNamespace(namespace);
    if (project == null) {
      throw new SpgException(
          SpgMessageEnum.PROJECT_NAMESPACE_NOT_EXIST.getCode(),
          SpgMessageEnum.PROJECT_NAMESPACE_NOT_EXIST.getMessage());
    }
    Account loginUser = this.accountService.getLoginUser();
    boolean isSuper = this.permissionManager.isSuper(loginUser.getWorkNo());
    boolean isProjectRole =
        this.permissionManager.isProjectRole(loginUser.getWorkNo(), project.getId());
    if (!isSuper && !isProjectRole) {
      throw new SpgException(SpgMessageEnum.PROJECT_MEMBER_NOT_EXIST);
    }
    request.setProjectId(project.getId());
    ProjectSchema projectSchema = this.schemaManager.getProjectSchema(project.getId());
    List<NodeTypeModel> oldNodeTypeModels = Lists.newArrayList();
    for (BaseSPGType baseAdvancedType : projectSchema.getSpgTypes()) {
      if (SPGTypeEnum.ENTITY_TYPE.equals(baseAdvancedType.getSpgTypeEnum())
          || SPGTypeEnum.EVENT_TYPE.equals(baseAdvancedType.getSpgTypeEnum())
          || SPGTypeEnum.CONCEPT_TYPE.equals(baseAdvancedType.getSpgTypeEnum())) {
        NodeTypeModel entityModel =
            SchemaModelConvertor.convert2NodeTypeModel(namespace, baseAdvancedType);
        oldNodeTypeModels.add(entityModel);
      }
    }
    log.info("oldTypes:[{}]", JSON.toJSONString(oldNodeTypeModels));
    SchemaCompareUtil schemaCompareUtil = new SchemaCompareUtil();
    SchemaCompareUtil.SchemaChangeDTO changeDTO =
        schemaCompareUtil.compare(namespace, oldNodeTypeModels, schemaModel.getNodeTypeModels());
    log.info(
        "changeDTO-ADD({})-DEL({})-UPDATE({})",
        new Object[] {
          Integer.valueOf(changeDTO.getAddTypes().size()),
          Integer.valueOf(changeDTO.getDeleteTypes().size()),
          Integer.valueOf(changeDTO.getUpdateTypes().size())
        });
    SchemaDraft schemaDraft = new SchemaDraft();
    List<BaseAdvancedType> newSchema = Lists.newArrayList();
    newSchema.addAll(changeDTO.getAddTypes());
    newSchema.addAll(changeDTO.getDeleteTypes());
    newSchema.addAll(changeDTO.getUpdateTypes());
    schemaDraft.setAlterSpgTypes(newSchema);
    log.info("schemaDraft:[{}]", JSON.toJSONString(schemaDraft));
    request.setSchemaDraft(schemaDraft);
    this.schemaManager.alterSchema(request);
    Map<String, Object> result = Maps.newHashMap();
    result.put("ADD", toResultMap(changeDTO.getAddTypes()));
    result.put("UPDATE", toResultMap(changeDTO.getUpdateTypes()));
    result.put("DELETE", toResultMap(changeDTO.getDeleteTypes()));
    return result;
  }

  private List<Map<String, Object>> toResultMap(List<BaseAdvancedType> baseAdvancedTypes) {
    List<Map<String, Object>> result = Lists.newArrayList();
    if (CollectionUtils.isEmpty(baseAdvancedTypes)) {
      return result;
    }
    for (BaseAdvancedType baseAdvancedType : baseAdvancedTypes) {
      Map<String, Object> map = Maps.newHashMap();
      map.put("name", baseAdvancedType.getName());
      map.put("id", baseAdvancedType.getUniqueId());
      result.add(map);
    }
    return result;
  }

  private void addPropertyNameMap(
      Map<String, String> map, String name, List<PropertyDTO> propertyList) {
    if (CollectionUtils.isEmpty(propertyList)) {
      return;
    }
    for (PropertyDTO propertyDTO : propertyList) {
      map.put(String.format("%s.%s", name, propertyDTO.getName()), propertyDTO.getNameZh());
    }
  }
}
