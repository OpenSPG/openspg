package com.antgroup.openspgapp.biz.schema.impl;

import com.antgroup.openspg.core.schema.model.identifier.ConceptIdentifier;
import com.antgroup.openspg.core.schema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.schema.model.semantic.DynamicTaxonomySemantic;
import com.antgroup.openspg.core.schema.model.type.BaseSPGType;
import com.antgroup.openspg.core.schema.model.type.ProjectSchema;
import com.antgroup.openspg.core.schema.model.type.SPGTypeEnum;
import com.antgroup.openspg.server.api.facade.dto.schema.request.ConceptLevelInstanceRequest;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptInstanceResponse;
import com.antgroup.openspg.server.api.facade.dto.schema.response.ConceptLevelInstanceResponse;
import com.antgroup.openspg.server.biz.schema.ConceptInstanceManager;
import com.antgroup.openspg.server.biz.schema.SchemaManager;
import com.antgroup.openspg.server.core.schema.service.concept.ConceptSemanticService;
import com.antgroup.openspgapp.biz.schema.AkgConceptManager;
import com.antgroup.openspgapp.biz.schema.convertor.ConceptConvertor;
import com.antgroup.openspgapp.biz.schema.dto.ConceptDTO;
import com.antgroup.openspgapp.biz.schema.dto.ConceptNodeDTO;
import com.antgroup.openspgapp.biz.schema.dto.ConceptTreeDTO;
import com.baidu.brpc.utils.CollectionUtils;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
/* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/AkgConceptManagerImpl.class */
public class AkgConceptManagerImpl implements AkgConceptManager {
  private static final Logger log = LoggerFactory.getLogger(AkgConceptManagerImpl.class);
  ExecutorService pool = Executors.newFixedThreadPool(20);

  @Autowired private ConceptInstanceManager conceptInstanceManager;

  @Autowired private SchemaManager schemaManager;

  @Autowired private ConceptSemanticService conceptService;

  @Override // com.antgroup.openspgapp.biz.schema.AkgConceptManager
  public ConceptTreeDTO getConceptTree(Long projectId) {
    ConceptDTO rootNode = rootConcept();
    ConceptTreeDTO rootTree = new ConceptTreeDTO();
    rootTree.setConceptDTO(rootNode);
    ProjectSchema projectSchema = this.schemaManager.getProjectSchema(projectId);
    if (null == projectSchema || CollectionUtils.isEmpty(projectSchema.getSpgTypes())) {
      return rootTree;
    }
    List<BaseSPGType> metaConceptList =
        (List)
            projectSchema.getSpgTypes().stream()
                .filter(
                    item -> {
                      return SPGTypeEnum.CONCEPT_TYPE == item.getSpgTypeEnum();
                    })
                .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(metaConceptList)) {
      return rootTree;
    }
    Map<String, ConceptTreeDTO> treeDTOMap = new HashMap<>();
    buildMetaTree(metaConceptList, treeDTOMap, rootTree);
    List<Future> list = new ArrayList<>();
    metaConceptList.stream()
        .forEach(
            baseSPGType -> {
              Future f =
                  this.pool.submit(new InnerBuildConceptTree(treeDTOMap, baseSPGType.getName()));
              list.add(f);
            });
    for (Future f : list) {
      try {
        if (null == f.get(60L, TimeUnit.SECONDS)) {
          log.warn("getConceptTreeWithSubConcept buildConceptTree timeout");
        }
      } catch (Exception e) {
        log.warn("getConceptTreeWithSubConcept buildConceptTree wait task fail:" + e.getMessage());
      }
    }
    return rootTree;
  }

  /* loaded from: com.antgroup.openspgapp-biz-schema-0.0.1-SNAPSHOT.jar:com/antgroup/openspgapp/biz/schema/impl/AkgConceptManagerImpl$InnerBuildConceptTree.class */
  private class InnerBuildConceptTree implements Callable {
    Map<String, ConceptTreeDTO> treeDTOMap;
    String metaType;

    InnerBuildConceptTree(Map<String, ConceptTreeDTO> treeDTOMap, String metaType) {
      this.treeDTOMap = treeDTOMap;
      this.metaType = metaType;
    }

    @Override // java.util.concurrent.Callable
    public Object call() throws Exception {
      AkgConceptManagerImpl.this.buildConceptTree(this.treeDTOMap, this.metaType);
      return new Object();
    }
  }

  /* JADX INFO: Access modifiers changed from: private */
  public void buildConceptTree(Map<String, ConceptTreeDTO> treeDTOMap, String metaType) {
    ConceptLevelInstanceRequest request = new ConceptLevelInstanceRequest();
    request.setConceptType(metaType);
    ConceptLevelInstanceResponse res =
        this.conceptInstanceManager.queryConceptLevelInstance(request);
    if (null == res || CollectionUtils.isEmpty(res.getChildren())) {
      return;
    }
    List<ConceptInstanceResponse> children = res.getChildren();
    ConceptTreeDTO tree = treeDTOMap.get(getNameKey(metaType, metaType));
    tree.getConceptDTO().setSubTypeCount(Integer.valueOf(children.size()));
    for (ConceptInstanceResponse child : children) {
      ConceptDTO conceptDTO = ConceptConvertor.toConceptDTO(child);
      conceptDTO.setLabel(metaType);
      ConceptTreeDTO conceptTreeDTO = new ConceptTreeDTO();
      conceptTreeDTO.setConceptDTO(conceptDTO);
      tree.getChildren().add(conceptTreeDTO);
    }
  }

  private void buildMetaTree(
      List<BaseSPGType> metaConceptList,
      Map<String, ConceptTreeDTO> treeDTOMap,
      ConceptTreeDTO rootTree) {
    Map<Long, BaseSPGType> id2SpgType =
        (Map)
            metaConceptList.stream()
                .collect(
                    Collectors.toMap(
                        (v0) -> {
                          return v0.getUniqueId();
                        },
                        v -> {
                          return v;
                        }));
    for (BaseSPGType baseSPGType : metaConceptList) {
      ConceptDTO node = null;
      List<Long> inheritPath = baseSPGType.getInheritPath();
      for (Long id : inheritPath) {
        BaseSPGType spgType = id2SpgType.get(id);
        if (null != spgType) {
          String name = spgType.getName();
          if (null != treeDTOMap.get(getNameKey(name, name))) {
            node = treeDTOMap.get(getNameKey(name, name)).getConceptDTO();
          } else {
            String parentName = rootTree.getConceptDTO().getName();
            ConceptTreeDTO tree = rootTree;
            if (null != node) {
              parentName = node.getName();
              tree = treeDTOMap.get(getNameKey(parentName, parentName));
            }
            ConceptDTO n = convertSpgToConceptDTO(baseSPGType, parentName);
            node = n;
            ConceptTreeDTO treeDTO = new ConceptTreeDTO();
            treeDTO.setConceptDTO(node);
            tree.getChildren().add(treeDTO);
            treeDTOMap.put(getNameKey(name, name), treeDTO);
          }
        }
      }
    }
  }

  private ConceptDTO convertSpgToConceptDTO(BaseSPGType baseSPGType, String parentName) {
    ConceptDTO node = new ConceptDTO();
    node.setId(String.valueOf(baseSPGType.getUniqueId()));
    node.setName(baseSPGType.getName());
    node.setNameZh(baseSPGType.getBasicInfo().getNameZh());
    node.setPrimaryKey(baseSPGType.getName());
    node.setMetaConcept(true);
    node.setLabel(baseSPGType.getName());
    node.setConceptParentType(parentName);
    return node;
  }

  private String getNameKey(String primaryKey, String metaType) {
    return primaryKey + metaType;
  }

  private ConceptDTO rootConcept() {
    ConceptDTO conceptDTO = new ConceptDTO();
    conceptDTO.setName("virtualConcept");
    conceptDTO.setNameZh("概念");
    conceptDTO.setConceptParentType("-1");
    conceptDTO.setLabel("-1");
    conceptDTO.setMetaConcept(false);
    return conceptDTO;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AkgConceptManager
  public ConceptNodeDTO getConceptDetail(String primaryKey, String metaType) {
    List<ConceptInstanceResponse> query =
        this.conceptInstanceManager.query(metaType, Sets.newHashSet(new String[] {primaryKey}));
    if (CollectionUtils.isEmpty(query)) {
      return null;
    }
    ConceptInstanceResponse instanceResponse = query.get(0);
    ConceptNodeDTO conceptNodeDTO = ConceptConvertor.toConceptNodeDTO(instanceResponse);
    conceptNodeDTO.setLabel(metaType);
    List<DynamicTaxonomySemantic> dynamicTaxonomySemantics =
        this.conceptService.queryDynamicTaxonomySemantic(
            SPGTypeIdentifier.parse(metaType), new ConceptIdentifier(primaryKey));
    if (CollectionUtils.isEmpty(dynamicTaxonomySemantics)) {
      return conceptNodeDTO;
    }
    DynamicTaxonomySemantic dynamicTaxonomySemantic = dynamicTaxonomySemantics.get(0);
    if (null != dynamicTaxonomySemantic.getLogicalRule()) {
      conceptNodeDTO
          .getProperties()
          .put("dslRule", dynamicTaxonomySemantic.getLogicalRule().getContent());
    }
    return conceptNodeDTO;
  }

  @Override // com.antgroup.openspgapp.biz.schema.AkgConceptManager
  public ConceptTreeDTO expandConcept(String primaryKey, String metaType) {
    ConceptTreeDTO conceptTreeDTO = new ConceptTreeDTO();
    ConceptLevelInstanceRequest request = new ConceptLevelInstanceRequest();
    request.setConceptType(metaType);
    request.setRootConceptInstance(primaryKey);
    ConceptLevelInstanceResponse response =
        this.conceptInstanceManager.queryConceptLevelInstance(request);
    if (null == response || CollectionUtils.isEmpty(response.getChildren())) {
      return conceptTreeDTO;
    }
    ConceptDTO conceptDTO = new ConceptDTO();
    conceptDTO.setPrimaryKey(primaryKey);
    conceptDTO.setMetaConcept(false);
    conceptDTO.setLabel(metaType);
    List<ConceptTreeDTO> children = new ArrayList<>();
    for (ConceptInstanceResponse child : response.getChildren()) {
      ConceptDTO subConceptDTO = ConceptConvertor.toConceptDTO(child);
      subConceptDTO.setLabel(metaType);
      ConceptTreeDTO treeChild = new ConceptTreeDTO();
      treeChild.setConceptDTO(subConceptDTO);
      children.add(treeChild);
    }
    conceptDTO.setSubTypeCount(Integer.valueOf(children.size()));
    conceptTreeDTO.setConceptDTO(conceptDTO);
    conceptTreeDTO.setChildren(children);
    return conceptTreeDTO;
  }
}
