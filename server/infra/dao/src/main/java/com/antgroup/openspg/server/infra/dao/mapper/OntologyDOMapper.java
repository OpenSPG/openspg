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

import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDO;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.OntologyDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OntologyDOMapper {
  long countByExample(OntologyDOExample example);

  int deleteByExample(OntologyDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(OntologyDOWithBLOBs record);

  int insertSelective(OntologyDOWithBLOBs record);

  List<OntologyDOWithBLOBs> selectByExampleWithBLOBs(OntologyDOExample example);

  List<OntologyDO> selectByExample(OntologyDOExample example);

  OntologyDOWithBLOBs selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") OntologyDOWithBLOBs record, @Param("example") OntologyDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") OntologyDOWithBLOBs record, @Param("example") OntologyDOExample example);

  int updateByExample(
      @Param("record") OntologyDO record, @Param("example") OntologyDOExample example);

  int updateByPrimaryKeySelective(OntologyDOWithBLOBs record);

  int updateByPrimaryKeyWithBLOBs(OntologyDOWithBLOBs record);

  int updateByPrimaryKey(OntologyDO record);
}
