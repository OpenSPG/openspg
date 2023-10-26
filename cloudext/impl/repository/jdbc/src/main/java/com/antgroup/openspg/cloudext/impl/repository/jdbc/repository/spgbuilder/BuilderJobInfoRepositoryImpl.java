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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgbuilder;

import com.antgroup.openspg.api.facade.dto.builder.request.BuilderJobInfoQuery;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDOExample;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper.SPGJobInfoDOMapper;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.repository.spgbuilder.convertor.BuilderJobInfoConvertor;
import com.antgroup.openspg.common.util.CollectionsUtils;
import com.antgroup.openspg.core.spgbuilder.model.service.BuilderJobInfo;
import com.antgroup.openspg.core.spgbuilder.service.repo.BuilderJobInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class BuilderJobInfoRepositoryImpl implements BuilderJobInfoRepository {

    @Autowired
    private SPGJobInfoDOMapper spgJobInfoDOMapper;

    @Override
    public Long save(BuilderJobInfo jobInfo) {
        SPGJobInfoDO jobInfoDO = BuilderJobInfoConvertor.toDO(jobInfo);
        spgJobInfoDOMapper.insert(jobInfoDO);
        return jobInfoDO.getId();
    }

    @Override
    public int updateExternalJobId(Long builderJobInfoId, String externalJobInfoId) {
        SPGJobInfoDOExample example = new SPGJobInfoDOExample();
        example.createCriteria()
            .andIdEqualTo(builderJobInfoId);

        SPGJobInfoDO jobInfoDO = new SPGJobInfoDO();
        jobInfoDO.setExternalJobInfoId(externalJobInfoId);
        return spgJobInfoDOMapper.updateByExampleSelective(jobInfoDO, example);
    }

    @Override
    public List<BuilderJobInfo> query(BuilderJobInfoQuery query) {
        SPGJobInfoDOExample example = new SPGJobInfoDOExample();
        SPGJobInfoDOExample.Criteria criteria = example.createCriteria();
        if (query.getBuildingJobInfoId() != null) {
            criteria.andIdEqualTo(query.getBuildingJobInfoId());
        }
        if (query.getExternalJobInfoId() != null) {
            criteria.andExternalJobInfoIdEqualTo(query.getExternalJobInfoId());
        }
        example.setOrderByClause("id desc");
        List<SPGJobInfoDO> spgJobInfoDOS = spgJobInfoDOMapper.selectByExampleWithBLOBs(example);
        return CollectionsUtils.listMap(spgJobInfoDOS, BuilderJobInfoConvertor::toModel);
    }
}
