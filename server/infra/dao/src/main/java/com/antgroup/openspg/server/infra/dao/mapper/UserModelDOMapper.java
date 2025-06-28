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

import com.antgroup.openspg.server.common.model.usermodel.UserModelQuery;
import com.antgroup.openspg.server.infra.dao.dataobject.UserModelDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserModelDOMapper {

  Long insert(UserModelDO record);

  int deleteById(Long id);

  int deleteByIds(@Param("ids") List<Long> ids);

  Long update(UserModelDO record);

  UserModelDO getById(Long id);

  UserModelDO getByInstanceId(String instanceId);

  List<UserModelDO> query(UserModelQuery record);

  List<UserModelDO> getByProviderAndName(
      @Param("provider") String provider, @Param("name") String name);

  List<UserModelDO> selectUserPrivateOrPublic(String userNo);

  Long updateBaseInfoByIds(
      @Param("ids") List<Long> ids,
      @Param("name") String name,
      @Param("visibility") String visibility,
      @Param("userNo") String userNo,
      @Param("config") String config);
}
