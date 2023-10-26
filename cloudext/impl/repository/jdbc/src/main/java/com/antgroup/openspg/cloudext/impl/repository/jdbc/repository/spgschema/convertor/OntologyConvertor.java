/*
 * Copyright 2023 Ant Group CO., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.convertor;

import com.antgroup.openspg.api.facade.JSON;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyDOWithBLOBs;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config.ConstraintItemConfigDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.config.OntologyEntityName;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.EntityCategoryEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.LayerEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.TrueOrFalseEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.ValidStatusEnum;
import com.antgroup.openspg.common.util.StringUtils;
import com.antgroup.openspg.core.spgschema.model.BasicInfo;
import com.antgroup.openspg.core.spgschema.model.OntologyId;
import com.antgroup.openspg.core.spgschema.model.SchemaConstants;
import com.antgroup.openspg.core.spgschema.model.SchemaExtInfo;
import com.antgroup.openspg.core.spgschema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.spgschema.model.constraint.BaseConstraintItem;
import com.antgroup.openspg.core.spgschema.model.identifier.SPGTypeIdentifier;
import com.antgroup.openspg.core.spgschema.model.type.ConceptLayerConfig;
import com.antgroup.openspg.core.spgschema.model.type.ConceptTaxonomicConfig;
import com.antgroup.openspg.core.spgschema.model.type.MultiVersionConfig;
import com.antgroup.openspg.core.spgschema.model.type.ParentTypeInfo;
import com.antgroup.openspg.core.spgschema.model.type.SPGTypeEnum;
import com.antgroup.openspg.core.spgschema.model.type.VisibleScopeEnum;
import com.antgroup.openspg.core.spgschema.service.type.model.OperatorConfig;
import com.antgroup.openspg.core.spgschema.service.type.model.ProjectOntologyRel;
import com.antgroup.openspg.core.spgschema.service.type.model.SimpleSPGType;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class OntologyConvertor {

    public static OntologyDOWithBLOBs toNewDO(SimpleSPGType advancedType) {
        OntologyDOWithBLOBs ontologyDO = new OntologyDOWithBLOBs();
        ontologyDO.setGmtCreate(new Date());
        ontologyDO.setGmtModified(new Date());
        ontologyDO.setId(advancedType.getAlterId());
        ontologyDO.setOriginalId(advancedType.getUniqueId());
        SPGTypeIdentifier spgTypeIdentifier = advancedType.getSpgTypeIdentifier();
        ontologyDO.setName(new OntologyEntityName(spgTypeIdentifier.getNamespace(),
            spgTypeIdentifier.getNameEn(), 1).getFullName());
        ontologyDO.setUniqueName(spgTypeIdentifier.toString());
        ontologyDO.setNameZh(advancedType.getBasicInfo().getNameZh());
        ontologyDO.setDescription(advancedType.getBasicInfo().getDesc());
        ontologyDO.setEntityCategory(EntityCategoryEnum.getBySchemaType(
            advancedType.getSpgTypeEnum()).name());
        ontologyDO.setLayer(LayerEnum.EXTENSION.name());
        ontologyDO.setStatus(ValidStatusEnum.VALID.getCode());
        ontologyDO.setWithIndex(Boolean.TRUE.equals(advancedType
            .getBooleanExtInfo(SchemaConstants.WITH_COMMON_INDEX))
            ? TrueOrFalseEnum.TRUE.name() : TrueOrFalseEnum.FALSE.name());
        ontologyDO.setScope(advancedType.getVisibleScope().name());
        ontologyDO.setVersion(1);
        ontologyDO.setVersionStatus(AlterStatusEnum.ONLINE.name());
        ontologyDO.setTransformerId(0L);
        ontologyDO.setOperatorConfig(JSON.serialize(advancedType.getOperatorConfigs()));
        ontologyDO.setConfig(ExtConfigConvertor.getExtConfig(advancedType));
        return ontologyDO;
    }

    public static OntologyDOWithBLOBs toUpdateDO(SimpleSPGType advancedType) {
        OntologyDOWithBLOBs ontologyDO = new OntologyDOWithBLOBs();
        ontologyDO.setGmtModified(new Date());
        ontologyDO.setId(advancedType.getAlterId());
        SPGTypeIdentifier spgTypeIdentifier = advancedType.getSpgTypeIdentifier();
        ontologyDO.setName(new OntologyEntityName(spgTypeIdentifier.getNamespace(),
            spgTypeIdentifier.getNameEn(), 1).getFullName());
        ontologyDO.setNameZh(advancedType.getBasicInfo().getNameZh());
        ontologyDO.setDescription(advancedType.getBasicInfo().getDesc());
        ontologyDO.setWithIndex(Boolean.TRUE.equals(advancedType
            .getExtInfo().getBoolean(SchemaConstants.WITH_COMMON_INDEX))
            ? TrueOrFalseEnum.TRUE.name() : TrueOrFalseEnum.FALSE.name());
        ontologyDO.setScope(advancedType.getVisibleScope().name());
        ontologyDO.setVersion(1);
        ontologyDO.setOperatorConfig(JSON.serialize(advancedType.getOperatorConfigs()));
        ontologyDO.setConfig(ExtConfigConvertor.getExtConfig(advancedType));
        return ontologyDO;
    }

    public static List<SimpleSPGType> toSpgType(
        List<OntologyDOWithBLOBs> ontologyDOS,
        List<ProjectOntologyRel> projectOntologyRels,
        List<ParentTypeInfo> parentTypeInfos) {
        Map<Long, OntologyDOWithBLOBs> entityDOMap = ontologyDOS.stream().collect(Collectors
            .toMap(OntologyDO::getOriginalId, Function.identity()));
        Map<Long, ProjectOntologyRel> projectMap = projectOntologyRels.stream().collect(Collectors
            .toMap(ProjectOntologyRel::getResourceId, Function.identity()));
        Map<Long, ParentTypeInfo> parentMap = parentTypeInfos.stream().collect(Collectors
            .toMap(ParentTypeInfo::getUniqueId, Function.identity()));

        List<SimpleSPGType> spgTypes = new ArrayList<>();
        entityDOMap.forEach((k, v) -> {
            if (StringUtils.isBlank(v.getName())) {
                return;
            }
            ProjectOntologyRel projectOntologyRel = projectMap.get(k);
            ParentTypeInfo parentTypeInfo = parentMap.get(k);
            spgTypes.add(toSpgType(v, projectOntologyRel, parentTypeInfo));
        });
        return spgTypes;
    }

    public static SimpleSPGType toSpgType(
        OntologyDOWithBLOBs ontologyDO,
        ProjectOntologyRel projectOntologyRel,
        ParentTypeInfo parentTypeInfo) {
        OntologyEntityName ontologyEntityName = new OntologyEntityName(ontologyDO.getName());
        BasicInfo<SPGTypeIdentifier> basicInfo = new BasicInfo<>(
            SPGTypeIdentifier.parse(ontologyEntityName.getUniqueName()),
            ontologyDO.getNameZh(), ontologyDO.getDescription());
        SPGTypeEnum spgTypeEnum = EntityCategoryEnum.toSpgType(ontologyDO.getEntityCategory());
        VisibleScopeEnum visibleScopeEnum = VisibleScopeEnum.toEnum(ontologyDO.getScope());
        SchemaExtInfo extConfig = JSON.deserialize(ontologyDO.getConfig(), SchemaExtInfo.class);
        List<OperatorConfig> operatorConfigs = JSON.deserialize(
            ontologyDO.getOperatorConfig(),
            new TypeToken<List<OperatorConfig>>() {
            }.getType());
        Long projectId = projectOntologyRel == null ? null : projectOntologyRel.getProjectId();

        switch (spgTypeEnum) {
            case BASIC_TYPE:
                return new SimpleSPGType(projectId,
                    new OntologyId(ontologyDO.getOriginalId(), ontologyDO.getId()),
                    null, extConfig,
                    basicInfo, parentTypeInfo, spgTypeEnum, visibleScopeEnum, operatorConfigs);
            case STANDARD_TYPE: {
                Boolean spreadable = false;
                List<BaseConstraintItem> constraintItems = null;
                if (extConfig != null) {
                    spreadable = extConfig.getBoolean(SchemaConstants.SPREADABLE);
                    Object constraint = extConfig.get(SchemaConstants.STANDARD_CONSTRAINT_KEY);
                    if (constraint instanceof List) {
                        List<ConstraintItemConfigDO> constraintItemConfigDOS = JSON.deserialize(
                            JSON.serialize(constraint), new TypeToken<List<ConstraintItemConfigDO>>() {
                            }.getType());
                        constraintItems = ConstraintItemConfigDOConvertor.toConstraintItem(constraintItemConfigDOS);
                    }
                }

                return new SimpleSPGType(projectId,
                    new OntologyId(ontologyDO.getOriginalId(), ontologyDO.getId()),
                    null, extConfig,
                    basicInfo, parentTypeInfo, spgTypeEnum, visibleScopeEnum, operatorConfigs,
                    null, null, null,
                    spreadable, constraintItems);
            }
            case ENTITY_TYPE:
            case EVENT_TYPE: {
                return new SimpleSPGType(projectId,
                    new OntologyId(ontologyDO.getOriginalId(), ontologyDO.getId()),
                    null, extConfig,
                    basicInfo, parentTypeInfo, spgTypeEnum, visibleScopeEnum, operatorConfigs);
            }
            case CONCEPT_TYPE: {
                ConceptLayerConfig conceptLayerConfig = ExtConfigConvertor.get(extConfig,
                    SchemaConstants.CONCEPT_LAYER_KEY, ConceptLayerConfig.class);
                ConceptTaxonomicConfig conceptTaxonomicConfig = ExtConfigConvertor.get(extConfig,
                    SchemaConstants.CONCEPT_TAXONOMIC_KEY, ConceptTaxonomicConfig.class);
                MultiVersionConfig multiVersionConfig = ExtConfigConvertor.get(extConfig,
                    SchemaConstants.MULTI_VERSION_CONFIG_KEY, MultiVersionConfig.class);

                return new SimpleSPGType(projectId,
                    new OntologyId(ontologyDO.getOriginalId(), ontologyDO.getId()),
                    null, extConfig,
                    basicInfo, parentTypeInfo, spgTypeEnum, visibleScopeEnum, operatorConfigs,
                    conceptLayerConfig, conceptTaxonomicConfig, multiVersionConfig,
                    null, null);
            }
            default:
                throw new IllegalArgumentException("illegal type=" + ontologyDO.getEntityCategory());
        }
    }
}
