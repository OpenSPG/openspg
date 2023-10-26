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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ReleaseDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ReleaseDOExample;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ReleaseDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReleaseDOMapper {
    long countByExample(ReleaseDOExample example);

    int deleteByExample(ReleaseDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ReleaseDOWithBLOBs record);

    int insertSelective(ReleaseDOWithBLOBs record);

    List<ReleaseDOWithBLOBs> selectByExampleWithBLOBs(ReleaseDOExample example);

    List<ReleaseDO> selectByExample(ReleaseDOExample example);

    ReleaseDOWithBLOBs selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ReleaseDOWithBLOBs record, @Param("example") ReleaseDOExample example);

    int updateByExampleWithBLOBs(@Param("record") ReleaseDOWithBLOBs record, @Param("example") ReleaseDOExample example);

    int updateByExample(@Param("record") ReleaseDO record, @Param("example") ReleaseDOExample example);

    int updateByPrimaryKeySelective(ReleaseDOWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ReleaseDOWithBLOBs record);

    int updateByPrimaryKey(ReleaseDO record);
}