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

import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.TenantDO;
import com.antgroup.openspg.cloudext.impl.repository.jdbc.dataobject.TenantDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TenantDOMapper {
  long countByExample(TenantDOExample example);

  int deleteByExample(TenantDOExample example);

  int deleteByPrimaryKey(Long id);

  int insert(TenantDO record);

  int insertSelective(TenantDO record);

  List<TenantDO> selectByExample(TenantDOExample example);

  TenantDO selectByPrimaryKey(Long id);

  int updateByExampleSelective(
      @Param("record") TenantDO record, @Param("example") TenantDOExample example);

  int updateByExample(@Param("record") TenantDO record, @Param("example") TenantDOExample example);

  int updateByPrimaryKeySelective(TenantDO record);

  int updateByPrimaryKey(TenantDO record);
}
