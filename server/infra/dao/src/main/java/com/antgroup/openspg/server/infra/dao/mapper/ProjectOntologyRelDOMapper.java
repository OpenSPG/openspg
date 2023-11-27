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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ProjectOntologyRelDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.ProjectOntologyRelDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProjectOntologyRelDOMapper {
  long countByExample(ProjectOntologyRelDOExample example);

  int deleteByExample(ProjectOntologyRelDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(ProjectOntologyRelDO record);

  int insertSelective(ProjectOntologyRelDO record);

  List<ProjectOntologyRelDO> selectByExample(ProjectOntologyRelDOExample example);

  ProjectOntologyRelDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ProjectOntologyRelDO record,
      @Param("example") ProjectOntologyRelDOExample example);

  int updateByExample(
      @Param("record") ProjectOntologyRelDO record,
      @Param("example") ProjectOntologyRelDOExample example);

  int updateByPrimaryKeySelective(ProjectOntologyRelDO record);

  int updateByPrimaryKey(ProjectOntologyRelDO record);
}
