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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OperatorOverviewDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.OperatorOverviewDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OperatorOverviewDOMapper {
    long countByExample(OperatorOverviewDOExample example);

    int deleteByExample(OperatorOverviewDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OperatorOverviewDO record);

    int insertSelective(OperatorOverviewDO record);

    List<OperatorOverviewDO> selectByExample(OperatorOverviewDOExample example);

    OperatorOverviewDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OperatorOverviewDO record, @Param("example") OperatorOverviewDOExample example);

    int updateByExample(@Param("record") OperatorOverviewDO record, @Param("example") OperatorOverviewDOExample example);

    int updateByPrimaryKeySelective(OperatorOverviewDO record);

    int updateByPrimaryKey(OperatorOverviewDO record);
}