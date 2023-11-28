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

import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDO;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.SPGJobInstDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SPGJobInstDOMapper {
  long countByExample(SPGJobInstDOExample example);

  int deleteByExample(SPGJobInstDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(SPGJobInstDOWithBLOBs record);

  int insertSelective(SPGJobInstDOWithBLOBs record);

  List<SPGJobInstDOWithBLOBs> selectByExampleWithBLOBs(SPGJobInstDOExample example);

  List<SPGJobInstDO> selectByExample(SPGJobInstDOExample example);

  SPGJobInstDOWithBLOBs selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") SPGJobInstDOWithBLOBs record, @Param("example") SPGJobInstDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") SPGJobInstDOWithBLOBs record, @Param("example") SPGJobInstDOExample example);

  int updateByExample(
      @Param("record") SPGJobInstDO record, @Param("example") SPGJobInstDOExample example);

  int updateByPrimaryKeySelective(SPGJobInstDOWithBLOBs record);

  int updateByPrimaryKeyWithBLOBs(SPGJobInstDOWithBLOBs record);

  int updateByPrimaryKey(SPGJobInstDO record);
}
