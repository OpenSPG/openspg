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

import com.antgroup.openspg.server.infra.dao.dataobject.SemanticDO;
import com.antgroup.openspg.server.infra.dao.dataobject.SemanticDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SemanticDOMapper {
  long countByExample(SemanticDOExample example);

  int deleteByExample(SemanticDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(SemanticDO record);

  int insertSelective(SemanticDO record);

  List<SemanticDO> selectByExampleWithBLOBs(SemanticDOExample example);

  List<SemanticDO> selectByExample(SemanticDOExample example);

  SemanticDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") SemanticDO record, @Param("example") SemanticDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") SemanticDO record, @Param("example") SemanticDOExample example);

  int updateByExample(
      @Param("record") SemanticDO record, @Param("example") SemanticDOExample example);

  int updateByPrimaryKeySelective(SemanticDO record);

  int updateByPrimaryKeyWithBLOBs(SemanticDO record);

  int updateByPrimaryKey(SemanticDO record);
}
