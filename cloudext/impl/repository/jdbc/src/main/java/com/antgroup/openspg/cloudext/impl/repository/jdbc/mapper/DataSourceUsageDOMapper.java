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

package com.antgroup.openspg.cloudext.impl.repository.jdbc.mapper;

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.DataSourceUsageDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.DataSourceUsageDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DataSourceUsageDOMapper {
  long countByExample(DataSourceUsageDOExample example);

  int deleteByExample(DataSourceUsageDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(DataSourceUsageDO record);

  int insertSelective(DataSourceUsageDO record);

  List<DataSourceUsageDO> selectByExample(DataSourceUsageDOExample example);

  DataSourceUsageDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") DataSourceUsageDO record,
      @Param("example") DataSourceUsageDOExample example);

  int updateByExample(
      @Param("record") DataSourceUsageDO record,
      @Param("example") DataSourceUsageDOExample example);

  int updateByPrimaryKeySelective(DataSourceUsageDO record);

  int updateByPrimaryKey(DataSourceUsageDO record);
}
