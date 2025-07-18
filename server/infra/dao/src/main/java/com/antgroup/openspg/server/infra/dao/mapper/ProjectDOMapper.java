/*
 * Copyright 2023 OpenSPG Authors
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

import com.antgroup.openspg.server.api.facade.dto.common.request.ProjectQueryRequest;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectDO;
import com.antgroup.openspg.server.infra.dao.dataobject.ProjectDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ProjectDOMapper {
  long countByExample(ProjectDOExample example);

  int deleteByExample(ProjectDOExample example);

  int deleteByPrimaryKey(Long id);

  void deleteFromKgOntologyEntity(String namespace);

  void deleteFromKgOntologyEntityPropertyRange(Long id);

  void deleteFromKgProjectEntity(Long id);

  void deleteFromKgOntologyRelease(Long id);

  void deleteFromKgReasonSession(Long id);

  void deleteFromKgReasonTask(Long id);

  void deleteFromKgReasonTutorial(Long id);

  void deleteFromKgBuilderJob(Long id);

  void deleteFromKgSchedulerInstance(Long id);

  void deleteFromKgSchedulerJob(Long id);

  void deleteFromKgSchedulerTask(Long id);

  void deleteFromKgResourcePermission(Long projectId);

  int insert(ProjectDO record);

  int insertSelective(ProjectDO record);

  List<ProjectDO> selectByExample(ProjectDOExample example);

  ProjectDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") ProjectDO record, @Param("example") ProjectDOExample example);

  int updateByExample(
      @Param("record") ProjectDO record, @Param("example") ProjectDOExample example);

  int updateByPrimaryKeySelective(ProjectDO record);

  int updateByPrimaryKey(ProjectDO record);

  List<ProjectDO> selectByCondition(
      @Param("record") ProjectQueryRequest record,
      @Param("orderByGmtCreateDesc") Boolean orderByGmtCreateDesc,
      @Param("start") int start,
      @Param("size") int size);

  long selectCountByCondition(
      @Param("record") ProjectQueryRequest record,
      @Param("orderByGmtCreateDesc") Boolean orderByGmtCreateDesc);
}
