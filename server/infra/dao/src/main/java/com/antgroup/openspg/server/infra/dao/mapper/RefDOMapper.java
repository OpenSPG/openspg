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

import com.antgroup.openspg.server.infra.dao.dataobject.RefDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RefDOMapper {
  int deleteByPrimaryKey(Long id);

  Long insert(RefDO record);

  int insertSelective(RefDO record);

  RefDO selectByPrimaryKey(Long id);

  RefDO selectByUniqueKey(RefDO record);

  List<RefDO> query(RefDO record);

  int updateByPrimaryKeySelective(RefDO record);

  int updateByPrimaryKeyWithBLOBs(RefDO record);

  int updateByPrimaryKey(RefDO record);

  List<RefDO> getRefInfoByRightMatchRefedId(String refedId);

  int updateByUniqueKey(RefDO refDO);

  int deleteByIds(@Param("ids") List<Long> ids);

  int deleteByUniqueKey(RefDO refDO);

  int checkUniqueToken(@Param("alias") String alias, @Param("token") String token);
}
