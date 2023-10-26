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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyPropertyDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OntologyPropertyDOExample;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper.OntologyPropertyDOMapper;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.convertor.SimpleSubPropertyConvertor;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.MapTypeEnum;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgschema.enums.PropertyCategoryEnum;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgschema.model.alter.AlterStatusEnum;
import com.antgroup.openspg.core.spgschema.model.semantic.SPGOntologyEnum;
import com.antgroup.openspg.core.spgschema.service.predicate.model.SimpleSubProperty;
import com.antgroup.openspg.core.spgschema.service.predicate.repository.SubPropertyRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;


@Repository
public class SubPropertyRepositoryImpl implements SubPropertyRepository {

    @Autowired
    private OntologyPropertyDOMapper ontologyPropertyDOMapper;

    @Override
    public int save(SimpleSubProperty simpleSubProperty) {
        OntologyPropertyDO ontologyPropertyDO = SimpleSubPropertyConvertor.toNewDO(simpleSubProperty);
        return ontologyPropertyDOMapper.insert(ontologyPropertyDO);
    }

    @Override
    public int update(SimpleSubProperty simpleProperty) {
        OntologyPropertyDO ontologyPropertyDO = SimpleSubPropertyConvertor.toUpdateDO(simpleProperty);
        return ontologyPropertyDOMapper.updateByPrimaryKeySelective(ontologyPropertyDO);
    }

    @Override
    public int delete(SimpleSubProperty simpleSubProperty) {
        OntologyPropertyDOExample example = new OntologyPropertyDOExample();
        example.createCriteria().andOriginalIdEqualTo(simpleSubProperty.getUniqueId());
        return ontologyPropertyDOMapper.deleteByExample(example);
    }

    @Override
    public SimpleSubProperty queryByUniqueId(Long uniqueId) {
        OntologyPropertyDOExample example = new OntologyPropertyDOExample();
        example.createCriteria()
            .andOriginalIdEqualTo(uniqueId)
            .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
            .andPropertyCategoryEqualTo(PropertyCategoryEnum.BASIC.name());

        List<OntologyPropertyDO> ontologyPropertyDOS = ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);

        if (CollectionUtils.isEmpty(ontologyPropertyDOS)) {
            return null;
        }
        return SimpleSubPropertyConvertor.toModel(ontologyPropertyDOS.get(0));
    }

    @Override
    public List<SimpleSubProperty> queryBySubjectId(List<Long> subjectIds, SPGOntologyEnum ontologyEnum) {
        OntologyPropertyDOExample example = new OntologyPropertyDOExample();
        example.createCriteria()
            .andOriginalDomainIdIn(subjectIds)
            .andVersionStatusEqualTo(AlterStatusEnum.ONLINE.name())
            .andMapTypeEqualTo(SPGOntologyEnum.PROPERTY.equals(ontologyEnum)
                ? MapTypeEnum.PROP.name() : MapTypeEnum.EDGE.name())
            .andPropertyCategoryEqualTo(PropertyCategoryEnum.BASIC.name());

        List<OntologyPropertyDO> ontologyPropertyDOS = ontologyPropertyDOMapper.selectByExampleWithBLOBs(example);

        if (CollectionUtils.isEmpty(ontologyPropertyDOS)) {
            return Collections.emptyList();
        }
        return CollectionsUtils.listMap(ontologyPropertyDOS, SimpleSubPropertyConvertor::toModel);
    }
}
