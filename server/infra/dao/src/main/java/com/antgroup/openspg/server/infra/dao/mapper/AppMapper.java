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

import com.antgroup.openspg.server.api.facade.dto.common.request.AppRequest;
import com.antgroup.openspg.server.infra.dao.dataobject.AppDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AppMapper {

  int insert(AppDO record);

  int deleteById(Long id);

  int update(AppDO record);

  AppDO getById(Long id);

  List<AppDO> selectByCondition(
      @Param("request") AppRequest request, @Param("start") int start, @Param("size") int size);

  Long selectCountByCondition(@Param("request") AppRequest request);

  AppDO getByName(String name);

  List<AppDO> queryByCondition(AppRequest request);
}
