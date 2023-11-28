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

import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDO;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDOExample;
import com.antgroup.openspg.server.infra.dao.dataobject.DataSourceDOWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSourceDOMapper {
  long countByExample(DataSourceDOExample example);

  int deleteByExample(DataSourceDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(DataSourceDOWithBLOBs record);

  int insertSelective(DataSourceDOWithBLOBs record);

  List<DataSourceDOWithBLOBs> selectByExampleWithBLOBs(DataSourceDOExample example);

  List<DataSourceDO> selectByExample(DataSourceDOExample example);

  DataSourceDOWithBLOBs selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") DataSourceDOWithBLOBs record, @Param("example") DataSourceDOExample example);

  int updateByExampleWithBLOBs(
      @Param("record") DataSourceDOWithBLOBs record, @Param("example") DataSourceDOExample example);

  int updateByExample(
      @Param("record") DataSourceDO record, @Param("example") DataSourceDOExample example);

  int updateByPrimaryKeySelective(DataSourceDOWithBLOBs record);

  int updateByPrimaryKeyWithBLOBs(DataSourceDOWithBLOBs record);

  int updateByPrimaryKey(DataSourceDO record);
}
