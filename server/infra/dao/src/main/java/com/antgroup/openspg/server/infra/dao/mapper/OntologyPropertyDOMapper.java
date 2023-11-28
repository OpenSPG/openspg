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

import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyPropertyDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OntologyPropertyDOMapper {
  long countByExample(OntologyPropertyDOExample example);

  int deleteByExample(OntologyPropertyDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(OntologyPropertyDO record);

  int insertSelective(OntologyPropertyDO record);

  List<OntologyPropertyDO> selectByExampleWithBLOBs(OntologyPropertyDOExample example);

  List<OntologyPropertyDO> selectByExample(OntologyPropertyDOExample example);

  OntologyPropertyDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") OntologyPropertyDO record,
      @Param("example") OntologyPropertyDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") OntologyPropertyDO record,
      @Param("example") OntologyPropertyDOExample example);

  int updateByExample(
      @Param("record") OntologyPropertyDO record,
      @Param("example") OntologyPropertyDOExample example);

  int updateByPrimaryKeySelective(OntologyPropertyDO record);

  int updateByPrimaryKeyWithBLOBs(OntologyPropertyDO record);

  int updateByPrimaryKey(OntologyPropertyDO record);
}
