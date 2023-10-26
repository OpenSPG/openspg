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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.SPGJobInfoDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SPGJobInfoDOMapper {
    long countByExample(SPGJobInfoDOExample example);

    int deleteByExample(SPGJobInfoDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SPGJobInfoDO record);

    int insertSelective(SPGJobInfoDO record);

    List<SPGJobInfoDO> selectByExampleWithBLOBs(SPGJobInfoDOExample example);

    List<SPGJobInfoDO> selectByExample(SPGJobInfoDOExample example);

    SPGJobInfoDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SPGJobInfoDO record, @Param("example") SPGJobInfoDOExample example);

    int updateByExampleWithBLOBs(@Param("record") SPGJobInfoDO record, @Param("example") SPGJobInfoDOExample example);

    int updateByExample(@Param("record") SPGJobInfoDO record, @Param("example") SPGJobInfoDOExample example);

    int updateByPrimaryKeySelective(SPGJobInfoDO record);

    int updateByPrimaryKeyWithBLOBs(SPGJobInfoDO record);

    int updateByPrimaryKey(SPGJobInfoDO record);
}