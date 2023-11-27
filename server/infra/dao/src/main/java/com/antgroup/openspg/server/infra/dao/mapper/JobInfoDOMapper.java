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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInfoDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.JobInfoDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface JobInfoDOMapper {
  long countByExample(JobInfoDOExample example);

  int deleteByExample(JobInfoDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(JobInfoDO record);

  int insertSelective(JobInfoDO record);

  List<JobInfoDO> selectByExample(JobInfoDOExample example);

  JobInfoDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") JobInfoDO record, @Param("example") JobInfoDOExample example);

  int updateByExample(
      @Param("record") JobInfoDO record, @Param("example") JobInfoDOExample example);

  int updateByPrimaryKeySelective(JobInfoDO record);

  int updateByPrimaryKey(JobInfoDO record);
}
