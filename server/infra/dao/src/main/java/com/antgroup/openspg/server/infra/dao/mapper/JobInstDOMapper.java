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

package com.antgroup.openspg.server.infra.dao.mapper;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInstDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInstDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobInstDOMapper {
  long countByExample(JobInstDOExample example);

  int deleteByExample(JobInstDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(JobInstDO record);

  int insertSelective(JobInstDO record);

  List<JobInstDO> selectByExampleWithBLOBs(JobInstDOExample example);

  List<JobInstDO> selectByExample(JobInstDOExample example);

  JobInstDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") JobInstDO record, @Param("example") JobInstDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") JobInstDO record, @Param("example") JobInstDOExample example);

  int updateByExample(
      @Param("record") JobInstDO record, @Param("example") JobInstDOExample example);

  int updateByPrimaryKeySelective(JobInstDO record);

  int updateByPrimaryKeyWithBLOBs(JobInstDO record);

  int updateByPrimaryKey(JobInstDO record);
}
