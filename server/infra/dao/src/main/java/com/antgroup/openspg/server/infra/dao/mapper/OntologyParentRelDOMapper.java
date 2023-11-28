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

import com.antgroup.openspg.server.infra.dao.dataobject.OntologyParentRelDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyParentRelDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OntologyParentRelDOMapper {
  long countByExample(OntologyParentRelDOExample example);

  int deleteByExample(OntologyParentRelDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(OntologyParentRelDO record);

  int insertSelective(OntologyParentRelDO record);

  List<OntologyParentRelDO> selectByExample(OntologyParentRelDOExample example);

  OntologyParentRelDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") OntologyParentRelDO record,
      @Param("example") OntologyParentRelDOExample example);

  int updateByExample(
      @Param("record") OntologyParentRelDO record,
      @Param("example") OntologyParentRelDOExample example);

  int updateByPrimaryKeySelective(OntologyParentRelDO record);

  int updateByPrimaryKey(OntologyParentRelDO record);
}
